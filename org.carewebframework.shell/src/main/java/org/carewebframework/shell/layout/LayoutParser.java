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
package org.carewebframework.shell.layout;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.carewebframework.common.MiscUtil;
import org.carewebframework.common.Version;
import org.carewebframework.common.XMLUtil;
import org.carewebframework.shell.ancillary.UIException;
import org.carewebframework.shell.elements.ElementBase;
import org.carewebframework.shell.elements.ElementDesktop;
import org.carewebframework.shell.layout.LayoutElement.LayoutRoot;
import org.carewebframework.shell.plugins.PluginDefinition;
import org.carewebframework.shell.plugins.PluginRegistry;
import org.carewebframework.shell.property.PropertyInfo;
import org.carewebframework.web.client.ExecutionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Parses a layout from a number of sources.
 */
public class LayoutParser {

    private static final LayoutParser instance = new LayoutParser();

    private enum Tag {
        LAYOUT(false), ELEMENT(true), TRIGGER(true);
        
        private final boolean allowMultiple;
        
        Tag(boolean allowMultiple) {
            this.allowMultiple = allowMultiple;
        }
        
        boolean allowMultiple() {
            return allowMultiple;
        }
    }
    
    private final Version newVersion = new Version("4.0");

    /**
     * Loads a layout from the specified resource, using a registered layout loader or, failing
     * that, using the default loadFromUrl method.
     *
     * @param resource The resource to be loaded.
     * @return The loaded layout.
     */
    public static Layout parseResource(String resource) {
        int i = resource.indexOf(":");
        
        if (i > 0) {
            String loaderId = resource.substring(0, i);
            ILayoutLoader layoutLoader = LayoutLoaderRegistry.getInstance().get(loaderId);
            
            if (layoutLoader != null) {
                String name = resource.substring(i + 1);
                return layoutLoader.loadLayout(name);
            }
        }
        
        InputStream strm = ExecutionContext.getSession().getServletContext().getResourceAsStream(resource);

        if (strm == null) {
            throw new UIException("Unable to locate layout resource: " + resource);
        }

        return parseStream(strm);
    }
    
    /**
     * Parse the layout from XML content.
     *
     * @param xml The XML content to parse.
     * @return The root layout element.
     */
    public static Layout parseText(String xml) {
        try {
            return parseDocument(XMLUtil.parseXMLFromString(xml));
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }
    
    /**
     * Parse layout from an input stream.
     *
     * @param strm The input stream.
     * @return The root layout element.
     */
    public static Layout parseStream(InputStream strm) {
        try {
            return parseDocument(XMLUtil.parseXMLFromStream(strm));
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        } finally {
            IOUtils.closeQuietly(strm);
        }
    }
    
    /**
     * Parse the layout from a stored layout.
     *
     * @param layoutId Layout identifier.
     * @return The root layout element.
     */
    public static Layout parseProperty(LayoutIdentifier layoutId) {
        return parseText(LayoutUtil.getLayoutContent(layoutId));
    }

    /**
     * Parse the layout associated with the specified application id.
     *
     * @param appId An application id.
     * @return The root layout element.
     */
    public static Layout parseAppId(String appId) {
        return parseText(LayoutUtil.getLayoutContentByAppId(appId));
    }

    /**
     * Parse the layout from an XML document.
     *
     * @param document An XML document.
     * @return The root layout element.
     */
    public static Layout parseDocument(Document document) {
        return new Layout(instance.parseChildren(document, null, Tag.LAYOUT));
    }

    /**
     * Parse the layout from the UI.
     *
     * @param root Root UI element.
     * @return The root layout element.
     */
    public static Layout parseElement(ElementBase root) {
        return new Layout(instance.parseUI(root));
    }

    private LayoutParser() {
    }

    private LayoutRoot parseChildren(Node parentNode, LayoutElement parent, Tag... tags) {
        Element node = getFirstChild(parentNode);

        while (node != null) {
            Tag tag = getTag(node, tags);

            switch (tag) {
                case LAYOUT:
                    return parseLayout(node);

                case ELEMENT:
                    parseElement(node, parent);
                    break;

                case TRIGGER:
                    parseTrigger(node, parent);
                    break;
            }

            node = getNextSibling(node);

            if (node != null && !tag.allowMultiple()) {
                ArrayUtils.removeElement(tags, tag);
            }
        }

        return null;
    }

    private boolean isElementNode(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE;
    }

    private Element getFirstChild(Node parent) {
        Node child = parent.getFirstChild();
        return child == null ? null : isElementNode(child) ? (Element) child : getNextSibling(child);
    }

    private Element getNextSibling(Node node) {
        Node sib = node;

        do {
            sib = sib.getNextSibling();
        } while (sib != null && !isElementNode(sib));
        
        return (Element) sib;
    }

    private LayoutRoot parseLayout(Element node) {
        LayoutRoot root = new LayoutRoot();
        copyAttributes(node, root);
        Version version = new Version(getRequiredAttribute(node, "version"));

        if (version.compareTo(newVersion) >= 0) {
            parseChildren(node, root, Tag.ELEMENT);
        } else {
            parseLegacy(node, root);
        }
        
        return root;
    }
    
    private void parseLegacy(Element node, LayoutElement parent) {
        Element child = getFirstChild(node);
        
        while (child != null) {
            LayoutElement ele = newLayoutElement(child, parent, child.getTagName());
            parseLegacy(child, ele);
            child = getNextSibling(child);
        }
        
    }

    private LayoutElement newLayoutElement(Element node, LayoutElement parent, String type) {
        PluginDefinition pluginDefinition = PluginRegistry.getInstance().get(type);
        
        if (pluginDefinition == null) {
            throw new IllegalArgumentException("Unrecognized element type: " + type);
        }
        
        LayoutElement layoutElement = new LayoutElement(pluginDefinition, parent);

        if (node != null) {
            copyAttributes(node, layoutElement);
        }

        return layoutElement;
    }
    
    /**
     * Parse a layout element node.
     *
     * @param node The DOM node.
     * @param parent The parent layout element.
     * @return The newly created layout element.
     */
    private LayoutElement parseElement(Element node, LayoutElement parent) {
        String type = getRequiredAttribute(node, "_type");
        LayoutElement layoutElement = newLayoutElement(node, parent, type);
        parseChildren(node, layoutElement, Tag.ELEMENT, Tag.TRIGGER);
        return layoutElement;
    }
    
    /**
     * Returns the value of the named attribute from a DOM node, throwing an exception if not found.
     *
     * @param node The DOM node.
     * @param name The attribute name.
     * @return The attribute value.
     */
    private String getRequiredAttribute(Element node, String name) {
        String value = node.getAttribute(name);

        if (value == null) {
            throw new IllegalArgumentException("Missing " + name + " attribute on node: " + node.getTagName());
        }

        return value;
    }

    /**
     * Parse a trigger node.
     *
     * @param node The DOM node.
     * @param parent The parent layout element.
     */
    private void parseTrigger(Element node, LayoutElement parent) {
        String condition = getRequiredAttribute(node, "condition");
        String action = getRequiredAttribute(node, "action");
        LayoutTrigger trigger = new LayoutTrigger(condition, action);
        parent.getTriggers().add(trigger);
    }

    /**
     * Copy attributes from DOM node to layout element.
     *
     * @param from DOM node.
     * @param to Layout element.
     */
    private void copyAttributes(Element from, LayoutElement to) {
        NamedNodeMap attributes = from.getAttributes();
        
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                to.getAttributes().put(attribute.getNodeName(), attribute.getNodeValue());
            }
        }
    }
    
    /**
     * Return and validate the tag type, throwing an exception if the tag is unknown or among the
     * allowable types.
     *
     * @param node The DOM node.
     * @param tags The allowable tag types.
     * @return The tag type.
     */
    private Tag getTag(Element node, Tag... tags) {
        String name = node.getTagName();
        String error = null;

        try {
            Tag tag = Tag.valueOf(name.toUpperCase());

            if (!ArrayUtils.contains(tags, tag)) {
                error = "Tag not valid at this location: " + name;
            } else {
                return tag;
            }
        } catch (IllegalArgumentException e) {
            error = "Unrecognized tag in layout: " + name;
        }
        
        throw new IllegalArgumentException(error);
    }

    /**
     * Parse the layout from the UI element tree.
     *
     * @param root The root of the UI element tree.
     * @return The root layout element.
     */
    private LayoutRoot parseUI(ElementBase root) {
        LayoutRoot ele = new LayoutRoot();
        
        if (root instanceof ElementDesktop) {
            copyAttributes(root, ele);
        }
        
        parseChildren(root, ele);
        return ele;
    }
    
    private void parseChildren(ElementBase parentNode, LayoutElement parent) {
        for (ElementBase child : parentNode.getSerializableChildren()) {
            LayoutElement ele = new LayoutElement(child.getDefinition(), parent);
            copyAttributes(child, ele);
            parseChildren(child, ele);
        }
    }
    
    private void copyAttributes(ElementBase source, LayoutElement element) {
        for (PropertyInfo propInfo : source.getDefinition().getProperties()) {
            Object value = propInfo.isSerializable() ? propInfo.getPropertyValue(source) : null;
            String val = value == null ? null : propInfo.getPropertyType().getSerializer().serialize(value);

            if (!ObjectUtils.equals(value, propInfo.getDefault())) {
                element.getAttributes().put(propInfo.getId(), val);
            }
        }
    }
    
}
