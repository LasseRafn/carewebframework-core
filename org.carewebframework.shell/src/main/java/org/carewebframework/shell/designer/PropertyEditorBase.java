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
package org.carewebframework.shell.designer;

import org.carewebframework.shell.layout.UIElementBase;
import org.carewebframework.shell.property.PropertyInfo;
import org.carewebframework.ui.zk.ZKUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.impl.XulElement;

/**
 * All property editors must descend from this abstract class.
 * 
 * @param <T> The type of input component.
 */
public abstract class PropertyEditorBase<T extends XulElement> {
    
    protected final T editor;
    
    private Object value;
    
    private UIElementBase target;
    
    private PropertyInfo propInfo;
    
    /**
     * Create property editor using the specified template.
     * 
     * @param template The template to create the editing component.
     */
    @SuppressWarnings("unchecked")
    protected PropertyEditorBase(String template) {
        this((T) ZKUtil.loadZulPage(template, null));
    }
    
    /**
     * Create property editor using the specified component for editing.
     * 
     * @param editor The component used to edit the property.
     */
    protected PropertyEditorBase(T editor) {
        this.editor = editor;
        editor.setHeight("80%");
        editor.setWidth("95%");
        ZKUtil.wireController(editor, this);
    }
    
    /**
     * Returns the component used to edit the property.
     * 
     * @return The editor component.
     */
    public XulElement getComponent() {
        return editor;
    }
    
    /**
     * Logic to return the value from the editor component.
     * 
     * @return Value from the editor component.
     */
    protected abstract Object getValue();
    
    /**
     * Logic to set the value in the editor component.
     * 
     * @param value Value for the editor component.
     */
    protected abstract void setValue(Object value);
    
    /**
     * Returns true if the property value has been changed since the last commit.
     * 
     * @return True if pending changes exist.
     */
    public boolean hasChanged() {
        Object currentValue = getValue();
        return value == null || currentValue == null ? value != currentValue : !value.equals(currentValue);
    }
    
    /**
     * Returns the PropertyInfo object associated with this editor.
     * 
     * @return PropInfo object.
     */
    public PropertyInfo getPropInfo() {
        return propInfo;
    }
    
    /**
     * Returns the target UI element associated with this editor.
     * 
     * @return Target UI element.
     */
    public UIElementBase getTarget() {
        return target;
    }
    
    /**
     * Updates the last committed value.
     */
    public void updateValue() {
        value = getValue();
    }
    
    /**
     * Sets focus to the editor component.
     */
    public void setFocus() {
        editor.setFocus(true);
    }
    
    /**
     * Initializes the property editor.
     * 
     * @param target The target UI element.
     * @param propInfo The PropertyInfo instance reflecting the property being edited on the target.
     * @param propGrid The property grid owning this property editor.
     */
    protected void init(UIElementBase target, PropertyInfo propInfo, PropertyGrid propGrid) {
        this.target = target;
        this.propInfo = propInfo;
        editor.addForward(Events.ON_CHANGE, propGrid, Events.ON_CHANGE);
        editor.addForward(Events.ON_FOCUS, propGrid, Events.ON_SELECT);
    }
    
    /**
     * Commit the changed value.
     * 
     * @return True if the operation was successful.
     */
    public boolean commit() {
        try {
            setWrongValueMessage(null);
            propInfo.setPropertyValue(target, getValue());
            updateValue();
            return true;
        } catch (Exception e) {
            setWrongValueException(e);
            return false;
        }
    }
    
    /**
     * Revert changes to the property value.
     * 
     * @return True if the operation was successful.
     */
    public boolean revert() {
        try {
            setWrongValueMessage(null);
            setValue(propInfo.getPropertyValue(target));
            updateValue();
            return true;
        } catch (Exception e) {
            setWrongValueException(e);
            return false;
        }
    }
    
    /**
     * Updates the wrong value message at the client.
     * 
     * @param exc The exception to display.
     */
    public void setWrongValueException(Throwable exc) {
        setWrongValueMessage(ZKUtil.formatExceptionForDisplay(exc));
    }
    
    /**
     * Updates the wrong value message at the client.
     * 
     * @param message If null, any existing message is removed. Otherwise, the specified method is
     *            displayed next to the editor component.
     */
    public void setWrongValueMessage(String message) {
        if (message == null) {
            Clients.clearWrongValue(editor);
        } else {
            Clients.wrongValue(editor, message);
        }
    }
}
