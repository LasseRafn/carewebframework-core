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
package org.carewebframework.ui.xml;

import org.carewebframework.common.XMLUtil;
import org.carewebframework.common.XMLUtil.TagFormat;
import org.carewebframework.ui.xml.XMLTreeModel.XMLTreeNode;
import org.carewebframework.ui.zk.TreeUtil.ITreeitemSearch;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Renderer for xml tree.
 */
class XMLViewerRenderer implements TreeitemRenderer<XMLTreeNode> {
    
    /**
     * Value associated with each tree item.
     */
    private class TreeitemValue {
        
        private Treecell cell;
        
        private Label closingTag;
        
        private String text = "";
        
    }
    
    /**
     * Open event listener for tree items in XML viewer.
     */
    private final EventListener<OpenEvent> nodeListener = new EventListener<OpenEvent>() {
        
        @Override
        public void onEvent(OpenEvent event) throws Exception {
            Treeitem item = (Treeitem) event.getTarget();
            boolean open = event.isOpen();
            TreeitemValue itemValue = (TreeitemValue) item.getValue();
            itemValue.closingTag.setVisible(!open);
            Treeitem sib = (Treeitem) item.getNextSibling();
            
            if (sib != null) {
                sib.setVisible(item.isOpen());
            }
        }
        
    };
    
    /**
     * Search logic for tree.
     */
    protected final ITreeitemSearch treeitemSearch = new ITreeitemSearch() {
        
        @Override
        public boolean isMatch(Treeitem item, String text) {
            String label = ((TreeitemValue) item.getValue()).text;
            return label != null && label.contains(text.toLowerCase());
        }
        
    };
    
    @Override
    public void render(Treeitem item, XMLTreeNode data, int index) throws Exception {
        Node node = data.getData();
        item.setLabel(null);
        TreeitemValue itemValue = new TreeitemValue();
        item.setValue(itemValue);
        itemValue.cell = (Treecell) item.getTreerow().getFirstChild();
        
        if (node.getNodeType() == Node.TEXT_NODE) {
            addLabel(itemValue, node.getNodeValue(), XMLConstants.STYLE_CONTENT);
            return;
        }
        
        if (node.getParentNode() == null) { // Closing tag
            addLabel(itemValue, XMLUtil.formatNodeName(node, TagFormat.CLOSING), XMLConstants.STYLE_TAG);
            return;
        }
        
        boolean leaf = !node.hasChildNodes();
        addLabel(itemValue, "<" + node.getNodeName(), XMLConstants.STYLE_TAG);
        
        NamedNodeMap attrs = node.getAttributes();
        
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            addLabel(itemValue, " " + attr.getNodeName(), XMLConstants.STYLE_ATTR_NAME);
            addLabel(itemValue, "='", null);
            addLabel(itemValue, attr.getNodeValue(), XMLConstants.STYLE_ATTR_VALUE);
            addLabel(itemValue, "'", null);
        }
        
        addLabel(itemValue, (leaf ? " />" : ">"), XMLConstants.STYLE_TAG);
        
        if (!leaf) {
            Label label = addLabel(itemValue, XMLUtil.formatNodeName(node, TagFormat.CLOSING), XMLConstants.STYLE_TAG);
            itemValue.closingTag = label;
            label.setVisible(false);
            item.setOpen(true);
            item.addEventListener(Events.ON_OPEN, nodeListener);
        }
    }
    
    private Label addLabel(TreeitemValue itemValue, String text, String sclass) {
        Label label = new Label(text);
        label.setSclass(sclass);
        label.setParent(itemValue.cell);
        itemValue.text += text.toLowerCase();
        return label;
    }
    
}
