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

import java.util.Iterator;

import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

/**
 * Iterates over items in a tree in a depth first search. Is not susceptible to concurrent
 * modification errors if tree composition changes during iteration and can be started at any
 * arbitrary position within the tree.
 */
public class TreeIterator implements Iterator<Treeitem> {
    
    private Treeitem last;
    
    private Treeitem next;
    
    /**
     * Starts iterator at top of tree.
     * 
     * @param tree The tree.
     */
    public TreeIterator(Tree tree) {
        this(tree.getTreechildren());
    }
    
    /**
     * Starts iterator at tree children node.
     * 
     * @param treeChildren The tree children node.
     */
    public TreeIterator(Treechildren treeChildren) {
        this.next = treeChildren == null ? null : (Treeitem) treeChildren.getFirstChild();
    }
    
    /**
     * Starts iterator after tree item.
     * 
     * @param last The last tree item.
     */
    public TreeIterator(Treeitem last) {
        this.last = last;
    }
    
    /**
     * Returns next tree item following specified item.
     * 
     * @param item The reference tree item.
     * @return Next tree item or null if no more.
     */
    private Treeitem nextItem(Treeitem item) {
        if (item == null) {
            return null;
        }
        
        Treeitem next;
        
        if (!item.isLoaded()) {
            item.getTree().renderItem(item);
        }
        
        Treechildren tc = item.getTreechildren();
        
        if (tc != null) {
            next = (Treeitem) tc.getFirstChild();
            
            if (next != null) {
                return next;
            }
        }
        
        next = (Treeitem) item.getNextSibling();
        
        while (next == null && (item = item.getParentItem()) != null) {
            next = (Treeitem) item.getNextSibling();
        }
        
        return next;
    }
    
    /**
     * Returns next tree item.
     * 
     * @return The next tree item.
     */
    private Treeitem nextItem() {
        if (next == null) {
            next = nextItem(last);
        }
        
        return next;
    }
    
    /**
     * Returns true if iterator not at end.
     */
    @Override
    public boolean hasNext() {
        return nextItem() != null;
    }
    
    /**
     * Returns next tree item, advancing internal state to next item.
     */
    @Override
    public Treeitem next() {
        last = nextItem();
        next = null;
        return last;
    }
    
    /**
     * Not supported.
     */
    @Override
    public void remove() {
    }
    
};
