/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToggleMenuItemDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToggleToolBarButtonDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;


public final class EisMappingSelectionActionsPolicy
    extends XmlMappingSelectionActionsPolicy
{
    // **************** Constructors ******************************************

    public EisMappingSelectionActionsPolicy(MappingsPlugin plugin) {
        super(plugin);
    }


    // **************** Mapping toolbar ***************************************

    protected ToolBarButtonGroupDescription buildMapAsToolBarGroup(WorkbenchContext context) {
        ToolBarButtonGroupDescription toolBarGroup = new ToolBarButtonGroupDescription();
        this.addDirectMappingButtons(toolBarGroup, context);
        this.addCompositeMappingButtons(toolBarGroup, context);
        this.addEisReferenceMappingButtons(toolBarGroup, context);
        this.addTransformationMappingButton(toolBarGroup, context);
        this.addUnmapButton(toolBarGroup, context);
        return toolBarGroup;
    }

    private void addEisReferenceMappingButtons(ToolBarButtonGroupDescription toolBarGroup, WorkbenchContext context) {
        toolBarGroup.add(this.buildEisExternalOneToOneRootToolBarButton(context));
        toolBarGroup.add(this.buildEisExternalOneToManyRootToolBarButton(context));
    }

    private ToggleToolBarButtonDescription buildEisExternalOneToOneRootToolBarButton(WorkbenchContext context) {
        return new ToggleToolBarButtonDescription(getMapAsEisOneToOneAction(context));
    }

    private ToggleToolBarButtonDescription buildEisExternalOneToManyRootToolBarButton(WorkbenchContext context) {
        return new ToggleToolBarButtonDescription(getMapAsEisOneToManyAction(context));
    }


    // **************** Mapping menu ******************************************

    protected MenuGroupDescription buildMapAsMenuGroup(WorkbenchContext context) {
        MenuGroupDescription menuGroup = new MenuGroupDescription();
        this.addDirectMappingMenuItems(menuGroup, context);
        this.addCompositeMappingMenuItems(menuGroup, context);
        this.addEisReferenceMappingMenuItems(menuGroup, context);
        this.addTransformationMappingMenuItem(menuGroup, context);
        this.addUnmapMenuItem(menuGroup, context);
        return menuGroup;
    }

    private void addEisReferenceMappingMenuItems(MenuGroupDescription menuGroup, WorkbenchContext context) {
        menuGroup.add(this.buildEisExternalOneToOneRootMenuItem(context));
        menuGroup.add(this.buildEisExternalOneToManyRootMenuItem(context));
    }

    private ToggleMenuItemDescription buildEisExternalOneToOneRootMenuItem(WorkbenchContext context) {
        return new ToggleMenuItemDescription(getMapAsEisOneToOneAction(context));
    }

    private ToggleMenuItemDescription buildEisExternalOneToManyRootMenuItem(WorkbenchContext context) {
        return new ToggleMenuItemDescription(getMapAsEisOneToManyAction(context));
    }


    // **************** Mapping actions ***************************************

    private ToggleFrameworkAction getMapAsEisOneToOneAction(WorkbenchContext context) {
        return new MapAsEisOneToOneAction(context);
    }

    private ToggleFrameworkAction getMapAsEisOneToManyAction(WorkbenchContext context) {
        return new MapAsEisOneToManyAction(context);
    }

    @Override
    protected ToggleFrameworkAction getMapAsXmlDirectCollectionAction(WorkbenchContext context) {
        return new MapAsEisDirectCollectionAction(context);
    }

    @Override
    protected ToggleFrameworkAction getMapAsXmlDirectAction(WorkbenchContext context) {
        return new MapAsEisDirectAction(context);
    }
}
