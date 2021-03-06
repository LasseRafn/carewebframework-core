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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.security.SecurityUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.shell.CareWebShell;
import org.carewebframework.shell.layout.LayoutIdentifier;
import org.carewebframework.shell.layout.UIElementDesktop;
import org.carewebframework.shell.layout.UILayout;
import org.carewebframework.ui.xml.XMLViewer;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

/**
 * This is the design menu that appears in the desktop's menu bar.
 */
public class DesignMenu extends Menu implements IdSpace {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(DesignMenu.class);
    
    private CareWebShell shell;
    
    private UIElementDesktop owner;
    
    private Menuitem mnuDesignMode;
    
    private Menuitem mnuShowZul;
    
    private Menupopup menupopup;
    
    /**
     * Creates the design menu with the specified desktop as owner.
     * 
     * @param owner Desktop UI element owner.
     * @return Design menu instance.
     */
    public static DesignMenu create(UIElementDesktop owner) {
        DesignMenu designMenu = null;
        
        try {
            designMenu = null;
            PageDefinition def = ZKUtil.loadCachedPageDefinition(DesignConstants.RESOURCE_PREFIX + "DesignMenu.zul");
            designMenu = (DesignMenu) Executions.createComponents(def, null, null);
            designMenu.init(owner);
        } catch (Exception e) {
            log.error("Error creating design menu.", e);
            
            if (designMenu != null) {
                designMenu.detach();
                designMenu = null;
            }
        }
        return designMenu;
    }
    
    /**
     * Initializes the design menu.
     * 
     * @param owner Owner of the menu.
     */
    private void init(UIElementDesktop owner) {
        this.owner = owner;
        shell = owner.getShell();
        ZKUtil.wireController(this);
        
        if (!SecurityUtil.hasDebugRole()) {
            mnuShowZul.detach();
        }
        
        updateMenus(false);
    }
    
    /**
     * Toggles design mode.
     */
    public void onClick$mnuDesignMode() {
        boolean enabled = !mnuDesignMode.isChecked();
        mnuDesignMode.setChecked(enabled);
        owner.setDesignMode(enabled);
        updateMenus(enabled);
        
        if (enabled) {
            Events.echoEvent("onAfterEnabled", this, null);
        } else {
            LayoutDesigner.close();
        }
    }
    
    public void onAfterEnabled() {
        this.open();
    }
    
    /**
     * Clear desktop.
     */
    public void onClick$mnuClearDesktop() {
        if (PromptDialog.confirm(DesignConstants.MSG_DESKTOP_CLEAR, DesignConstants.CAP_DESKTOP_CLEAR)) {
            shell.reset();
        }
    }
    
    /**
     * Brings up property editor for desktop.
     * 
     * @throws Exception Unspecified exception.
     */
    public void onClick$mnuDesktopProperties() throws Exception {
        PropertyGrid.create(owner, null);
    }
    
    /**
     * Brings up layout designer for desktop.
     * 
     * @throws Exception Unspecified exception.
     */
    public void onClick$mnuLayoutDesigner() throws Exception {
        LayoutDesigner.execute(owner);
    }
    
    /**
     * Brings up layout manager.
     * 
     * @throws Exception Unspecified exception.
     */
    public void onClick$mnuLayoutManager() throws Exception {
        LayoutManager.execute(true, shell.getUILayout().getName());
    }
    
    /**
     * Performs logout.
     */
    public void onClick$mnuLogout() {
        shell.logout();
    }
    
    /**
     * Prompts to save a layout.
     * 
     * @throws Exception Unspecified exception.
     */
    public void onClick$mnuSaveLayout() throws Exception {
        LayoutManager.saveLayout(UILayout.serialize(owner), new LayoutIdentifier(shell.getUILayout().getName(),
                LayoutManager.defaultIsShared()), false);
    }
    
    /**
     * Prompts to load layout.
     * 
     * @throws Exception Unspecified exception.
     */
    public void onClick$mnuLoadLayout() throws Exception {
        LayoutIdentifier layoutId = LayoutManager.execute(false, shell.getUILayout().getName());
        
        if (layoutId != null) {
            UILayout newLayout = new UILayout();
            newLayout.loadFromProperty(layoutId);
            shell.buildUI(newLayout);
        }
    }
    
    /**
     * Shows ZK markup for current desktop.
     */
    public void onClick$mnuShowZul() {
        XMLViewer.showZUML(owner.getOuterComponent());
    }
    
    /**
     * Updates the visibility of menu items
     * 
     * @param enabled The enabled status.
     */
    private void updateMenus(boolean enabled) {
        setImage(enabled ? DesignConstants.DESIGN_ICON_ACTIVE : DesignConstants.DESIGN_ICON_INACTIVE);
        ZKUtil.updateStyle(mnuDesignMode, "border-bottom", enabled ? "2px solid lightgray" : null);
        setTooltiptext(StrUtil.formatMessage(enabled ? DesignConstants.DESIGN_HINT_ACTIVE
                : DesignConstants.DESIGN_HINT_INACTIVE));
        Component child = menupopup.getFirstChild();
        
        while (child != null) {
            child.setVisible(enabled || child == mnuDesignMode);
            child = child.getNextSibling();
        }
    }
}
