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
package org.carewebframework.ui.zk;

import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

/**
 * Base row renderer.
 * 
 * @param <T> Class of rendered object.
 */
public abstract class AbstractComboitemRenderer<T> extends AbstractRenderer implements ComboitemRenderer<T> {
    
    /**
     * No args Constructor
     */
    public AbstractComboitemRenderer() {
        super();
    }
    
    @Override
    public final void render(Comboitem item, T object, int index) throws Exception {
        item.setValue(object);
        renderItem(item, object);
    }
    
    protected abstract void renderItem(Comboitem item, T object);
    
}
