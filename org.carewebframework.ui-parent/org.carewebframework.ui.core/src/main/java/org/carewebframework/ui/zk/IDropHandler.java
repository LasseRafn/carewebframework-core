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

import org.zkoss.zk.ui.Component;

/**
 * Interface to be implemented by handlers of drop events.
 */
public interface IDropHandler {
    
    /**
     * Handler returns the id(s) of the drop types it can accept. Separate multiple drop ids with
     * commas.
     * 
     * @return Comma-delimited list of acceptable drop ids.
     */
    String getDropId();
    
    /**
     * Simulates a drop event. The handler will process the item as if it had been dropped by user
     * action.
     * 
     * @param droppedItem The dropped item.
     */
    void drop(Component droppedItem);
}
