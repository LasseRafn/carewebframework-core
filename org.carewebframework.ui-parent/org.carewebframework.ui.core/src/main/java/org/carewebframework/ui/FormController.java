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
package org.carewebframework.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.zk.PopupDialog;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;

/**
 * Base controller for creating/editing a domain object.
 * <p>
 * Some basic requirements:
 * <ul>
 * <li>Button with an id of <b>btnOK</b> for committing changes.</li>
 * <li>Button with an id of <b>btnCancel</b> for canceling changes.</li>
 * <li>Implement <b>populateControls</b> to populate input elements from domain object.</li>
 * <li>Implement <b>populateDomainObject</b> to populate the domain object from input elements.</li>
 * <li>Implement <b>commit</b> to commit changes to domain object.</li>
 * <li>Optionally implement <b>hasRequired</b> to signal if all required inputs have been provided.
 * </li>
 * <li>Optionally implement <b>initControls</b> to provide custom initialization of input elements.
 * </li>
 * </ul>
 * 
 * @param <T> Class of the domain object.
 */
public abstract class FormController<T> extends FrameworkController {
    
    private static final long serialVersionUID = 1L;
    
    private T domainObject;
    
    private Component wrongValueTarget;
    
    private final Set<Component> changeSet = new HashSet<>();
    
    private String label_cancel_title = "@cwf.formcontroller.cancel.title";
    
    private String label_cancel_message = "@cwf.formcontroller.cancel.message";
    
    private String label_required_message = "@cwf.formcontroller.required.message";
    
    // Start of auto-wired members
    
    private Button btnOK;
    
    // End of auto-wired members.
    
    /**
     * Creates and displays the form.
     * 
     * @param form Url of the form.
     * @param domainObject The domain object to be modified.
     * @return True if changes were committed. False if canceled.
     */
    protected static boolean execute(String form, Object domainObject) {
        Map<Object, Object> args = new HashMap<>();
        args.put("domainObject", domainObject);
        Component dlg = PopupDialog.popup(form, args, true, false, true);
        return !ZKUtil.getAttributeBoolean(dlg, "cancelled");
    }
    
    /**
     * Populates the goal types and initializes the input elements with values from the step or
     * goal. Also, wires change events for all input elements.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        domainObject = (T) arg.get("domainObject");
        btnOK.setDisabled(true);
        initControls();
        Events.postEvent("onDeferredInit", root, null);
    }
    
    /**
     * Deferred initialization (allows any model-based controls to fully render).
     */
    public void onDeferredInit() {
        populateControls(domainObject);
        ZKUtil.focusFirst(root, true);
        ZKUtil.wireChangeEvents(root, root, Events.ON_CHANGE);
    }
    
    /**
     * Handler for change events from input elements.
     * 
     * @param event Change event.
     */
    public void onChange(Event event) {
        changed(ZKUtil.getEventOrigin(event).getTarget());
    }
    
    /**
     * Captures form closure from close icon.
     */
    public void onClose() {
        close(true);
    }
    
    /**
     * Registers a component as changed.
     * 
     * @param target The component whose input state changed.
     */
    protected void changed(Component target) {
        changeSet.add(target);
        wrongValue(null, null);
        btnOK.setDisabled(false);
    }
    
    /**
     * Displays the validation error for a required element.
     * 
     * @param target The target input element.
     * @return Always false.
     */
    protected boolean isMissing(Component target) {
        wrongValue(target, label_required_message);
        return false;
    }
    
    /**
     * Clears any current validation error and displays a new validation error for the specified
     * input element.
     * 
     * @param target The target input element.
     * @param message The validation error message.
     */
    protected void wrongValue(Component target, String message) {
        if (wrongValueTarget != null) {
            Clients.clearWrongValue(wrongValueTarget);
        }
        
        wrongValueTarget = target;
        
        if (target != null && message != null) {
            Clients.wrongValue(target, StrUtil.formatMessage(message));
        }
    }
    
    /**
     * Returns the text or "@"-prefixed label reference for the title of the cancel warning dialog.
     * 
     * @return Text or label reference.
     */
    public String getCancelTitleLabel() {
        return label_cancel_title;
    }
    
    /**
     * Sets the text or label reference for the title of the cancel warning dialog.
     * 
     * @param value Text or "@"-prefixed label reference.
     */
    public void setCancelTitleLabel(String value) {
        label_cancel_title = value;
    }
    
    /**
     * Returns the text or "@"-prefixed label reference for the message of the cancel warning
     * dialog.
     * 
     * @return Text or label reference.
     */
    public String getCancelMessageLabel() {
        return label_cancel_message;
    }
    
    /**
     * Sets the text or label reference for the message of the cancel warning dialog.
     * 
     * @param value Text or "@"-prefixed label reference.
     */
    public void setCancelMessageLabel(String value) {
        label_cancel_message = value;
    }
    
    /**
     * Returns the text or "@"-prefixed label reference of the required input message.
     * 
     * @return Text or label reference.
     */
    public String getRequiredMessageLabel() {
        return label_required_message;
    }
    
    /**
     * Sets the text or label reference for the title of the required input message.
     * 
     * @param value Text or "@"-prefixed label reference.
     */
    public void setRequiredMessageLabel(String value) {
        label_required_message = value;
    }
    
    /**
     * Commit changes and close the form when OK button is clicked.
     */
    public void onClick$btnOK() {
        close(false);
    }
    
    /**
     * Close the form when Cancel button is clicked, ignoring any changes.
     */
    public void onClick$btnCancel() {
        close(true);
    }
    
    /**
     * Commits all changes.
     * 
     * @return True if the operation was successful.
     */
    private boolean doCommit() {
        if (!hasRequired()) {
            return false;
        }
        
        populateDomainObject(domainObject);
        
        try {
            commit(domainObject);
            changeSet.clear();
            return true;
        } catch (Exception e) {
            PromptDialog.showError(e);
            return false;
        }
        
    }
    
    private void close(boolean cancel) {
        if (cancel && !changeSet.isEmpty() && !PromptDialog.confirm(label_cancel_message, label_cancel_title)) {
            return;
        }
        
        if (!cancel && !doCommit()) {
            return;
        }
        
        root.setAttribute("cancelled", cancel);
        root.detach();
    }
    
    /**
     * Perform any necessary post-composition initialization of controls here.
     */
    protected void initControls() {
    }
    
    /**
     * Returns true if all required inputs are present.
     * 
     * @return True if all required inputs are present.
     */
    protected boolean hasRequired() {
        return true;
    }
    
    /**
     * Populates input elements from the domain object.
     * 
     * @param domainObject The domain object.
     */
    protected abstract void populateControls(T domainObject);
    
    /**
     * Populates the domain object from the input elements.
     * 
     * @param domainObject The domain object.
     */
    protected abstract void populateDomainObject(T domainObject);
    
    /**
     * Commits changes to the the domain object.
     * 
     * @param domainObject The domain object.
     */
    protected abstract void commit(T domainObject);
    
}
