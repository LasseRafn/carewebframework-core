/*
 * #%L
 * carewebframework
 * %%
 * Copyright (C) 2008 - 2016 Regenstrief Institute, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.carewebframework.org/licensing/disclaimer.
 *
 * #L%
 */
package org.carewebframework.plugin.messagetesting;

import java.util.Collection;

import org.carewebframework.api.event.EventMessage;
import org.carewebframework.api.event.EventUtil;
import org.carewebframework.api.messaging.ConsumerService;
import org.carewebframework.api.messaging.IMessageConsumer.IMessageCallback;
import org.carewebframework.api.messaging.IMessageProducer;
import org.carewebframework.api.messaging.Message;
import org.carewebframework.api.messaging.ProducerService;
import org.carewebframework.shell.plugins.PluginContainer;
import org.carewebframework.shell.plugins.PluginController;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.ui.zk.ZKUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

/**
 * Controller class for ActiveMQ Tester.
 */
public class MainController extends PluginController {
    
    private static final long serialVersionUID = 1L;
    
    private final IMessageCallback messageCallback = new IMessageCallback() {
        
        private final EventListener<Event> eventListener = new EventListener<Event>() {
            
            @Override
            public void onEvent(Event event) throws Exception {
                received.add((Message) event.getData());
                
                if (!chkScrollLock.isChecked()) {
                    Events.echoEvent("onScrollToBottom", root, null);
                }
            }
            
        };
        
        @Override
        public void onMessage(String channel, Message message) {
            ZKUtil.fireEvent(new Event(channel, root, message), eventListener);
        }
        
    };
    
    private Listbox lboxProviders;
    
    private Listbox lboxSubscriptions;
    
    private Listbox lboxReceived;
    
    private Combobox cboxChannels;
    
    private Textbox tboxMessage;
    
    private Button btnSendMessage;
    
    private Checkbox chkAsEvent;
    
    private Checkbox chkScrollLock;
    
    private final ConsumerService consumerService;
    
    private final ProducerService producerService;
    
    private final ListModelList<String> channels = new ListModelList<>();
    
    private final ListModelList<String> channels2 = new ListModelList<>();
    
    private final ListModelList<Message> received = new ListModelList<>();
    
    public MainController(ConsumerService consumerService, ProducerService producerService) {
        this.consumerService = consumerService;
        this.producerService = producerService;
    }
    
    @Override
    public void onLoad(PluginContainer container) {
        super.onLoad(container);
        lboxProviders.setItemRenderer(new MessageProviderRenderer());
        ListModelList<IMessageProducer> providers = new ListModelList<>(getProviders());
        providers.setMultiple(true);
        lboxProviders.setModel(providers);
        lboxReceived.setItemRenderer(new ReceivedMessageRenderer());
        lboxReceived.setModel(received);
        channels.setMultiple(true);
        lboxSubscriptions.setModel(channels);
        lboxSubscriptions.setItemRenderer(new SubscriptionRenderer());
        cboxChannels.setModel(channels2);
    }
    
    private Collection<IMessageProducer> getProviders() {
        return producerService.getRegisteredProducers();
    }
    
    @Override
    public void onUnload() {
        super.onUnload();
        
        for (String channel : channels) {
            if (channels.isSelected(channel)) {
                subscribe(channel, false);
            }
        }
    }
    
    public void onClick$btnAddSubscription() {
        String channel = PromptDialog.input("Enter the name of the channel to subscribe to:", "Subscribe to Channel");
        
        if (channel != null && !channels.contains(channel)) {
            channels.add(channel);
            channels2.add(channel);
            subscribe(channel, true);
        }
    }
    
    public void onClick$btnRemoveSubscription() {
        Listitem item = lboxSubscriptions.getSelectedItem();
        
        if (item != null) {
            if (item.isSelected()) {
                subscribe(item.getLabel(), false);
            }
            
            lboxSubscriptions.removeChild(item);
        }
    }
    
    public void onClick$btnClearMessage() {
        tboxMessage.setText(null);
    }
    
    public void onClick$btnSendMessage() {
        Comboitem item = cboxChannels.getSelectedItem();
        
        if (item != null) {
            String type = item.getLabel();
            String channel = chkAsEvent.isChecked() ? EventUtil.getChannelName(type) : type;
            Message message = chkAsEvent.isChecked() ? new EventMessage(type, tboxMessage.getText())
                    : new Message(channel, tboxMessage.getText());
            producerService.publish(channel, message);
        }
    }
    
    public void onClick$btnClearReceived() {
        received.clear();
    }
    
    public void onSelect$cboxChannels(SelectEvent<Comboitem, ?> event) {
        btnSendMessage.setDisabled(false);
    }
    
    public void onSelect$lboxSubscriptions(SelectEvent<Listitem, ?> event) {
        Listitem item = event.getReference();
        subscribe(item.getLabel(), item.isSelected());
    }
    
    public void onSelect$lboxProviders(SelectEvent<Listitem, ?> event) {
        Listitem item = event.getReference();
        IMessageProducer producer = (IMessageProducer) item.getValue();
        
        if (item.isSelected()) {
            producerService.registerProducer(producer);
        } else {
            producerService.unregisterProducer(producer);
        }
    }
    
    public void onScrollToBottom() {
        Listitem item = lboxReceived.getItemAtIndex(lboxReceived.getItemCount() - 1);
        Clients.scrollIntoView(item);
    }
    
    private void subscribe(String channel, boolean subscribe) {
        if (subscribe) {
            consumerService.subscribe(channel, messageCallback);
        } else {
            consumerService.unsubscribe(channel, messageCallback);
        }
        
        if (!channel.startsWith("cwf-event-")) {
            subscribe("cwf-event-" + channel, subscribe);
        }
    }
}
