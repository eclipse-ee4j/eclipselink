/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToggleMenuItemDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToggleToolBarButtonDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;


public final class OXMappingSelectionActionsPolicy 
	extends XmlMappingSelectionActionsPolicy
{
	// **************** Constructors ******************************************
	
	public OXMappingSelectionActionsPolicy(MappingsPlugin plugin) {
		super(plugin);
	}
	
	
	// **************** Mapping toolbar ***************************************
	
	protected ToolBarButtonGroupDescription buildMapAsToolBarGroup(WorkbenchContext context) {
		ToolBarButtonGroupDescription toolBarGroup = new ToolBarButtonGroupDescription();
		this.addDirectMappingButtons(toolBarGroup, context);
		this.addCompositeMappingButtons(toolBarGroup, context);
		this.addAnyMappingButtons(toolBarGroup, context);
		this.addJaxbMappingButtons(toolBarGroup, context);
		this.addTransformationMappingButton(toolBarGroup, context);
		this.addUnmapButton(toolBarGroup, context);
		return toolBarGroup;
	}
	
	private void addAnyMappingButtons(ToolBarButtonGroupDescription toolBarGroup, WorkbenchContext context) {
		toolBarGroup.add(this.buildAnyObjectMappingToolBarButton(context));
		toolBarGroup.add(this.buildAnyCollectionMappingToolBarButton(context));
	}
	
	private void addJaxbMappingButtons(ToolBarButtonGroupDescription toolBarGroup, WorkbenchContext context) {
		toolBarGroup.add(this.buildAnyAttributeMappingToolBarButton(context));
		toolBarGroup.add(this.buildXmlObjectReferenceMappingToolBarButton(context));
		toolBarGroup.add(this.buildXmlCollectionReferenceMappingToolBarButton(context));
		toolBarGroup.add(this.buildXmlFragmentMappingToolBarButton(context));
		toolBarGroup.add(this.buildXmlFragmentCollectionMappingToolBarButton(context));
	}
	
	private final ToggleToolBarButtonDescription buildAnyObjectMappingToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(this.getMapAsAnyObjectAction(context));
	}
	
	private final ToggleToolBarButtonDescription buildAnyCollectionMappingToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(this.getMapAsAnyCollectionAction(context));
	}
	
	private final ToggleToolBarButtonDescription buildAnyAttributeMappingToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(this.getMapAsAnyAttributeAction(context));
	}
	
	private final ToggleToolBarButtonDescription buildXmlObjectReferenceMappingToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(this.getMapAsXmlObjectReferenceAction(context));
	}
	
	private final ToggleToolBarButtonDescription buildXmlCollectionReferenceMappingToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(this.getMapAsXmlCollectionReferenceAction(context));
	}

	private final ToggleToolBarButtonDescription buildXmlFragmentMappingToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(this.getMapAsXmlFragmentAction(context));
	}

	private final ToggleToolBarButtonDescription buildXmlFragmentCollectionMappingToolBarButton(WorkbenchContext context) {
		return new ToggleToolBarButtonDescription(this.getMapAsXmlFragmentCollectionAction(context));
	}
	
	// **************** Mapping menu ******************************************
	
	protected MenuGroupDescription buildMapAsMenuGroup(WorkbenchContext context) {
		MenuGroupDescription menuGroup = new MenuGroupDescription();
		this.addDirectMappingMenuItems(menuGroup, context);
		this.addCompositeMappingMenuItems(menuGroup, context);
		this.addAnyMappingMenuItems(menuGroup, context);
		this.addJaxbMappingMenuItems(menuGroup, context);
		this.addTransformationMappingMenuItem(menuGroup, context);
		this.addUnmapMenuItem(menuGroup, context);
		return menuGroup;
	}
	
	private void addAnyMappingMenuItems(MenuGroupDescription menuGroup, WorkbenchContext context) {
		menuGroup.add(this.buildAnyObjectMappingMenuItem(context));
		menuGroup.add(this.buildAnyCollectionMappingMenuItem(context));
	}
	
	private final ToggleMenuItemDescription buildAnyObjectMappingMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(this.getMapAsAnyObjectAction(context));
	}
	
	private final ToggleMenuItemDescription buildAnyCollectionMappingMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(this.getMapAsAnyCollectionAction(context));
	}
	
	private final ToggleMenuItemDescription buildAnyAttributeMappingMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(this.getMapAsAnyAttributeAction(context));
	}
	
	private void addJaxbMappingMenuItems(MenuGroupDescription menuGroup, WorkbenchContext context) {
		menuGroup.add(this.buildAnyAttributeMappingMenuItem(context));
		menuGroup.add(this.buildXmlObjectReferenceMappingMenuItem(context));
		menuGroup.add(this.buildXmlCollectionReferenceMappingMenuItem(context));
		menuGroup.add(this.buildXmlFragmentMappingMenuItem(context));
		menuGroup.add(this.buildXmlFragmentCollectionMappingMenuItem(context));
	}

	private final ToggleMenuItemDescription buildXmlObjectReferenceMappingMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(this.getMapAsXmlObjectReferenceAction(context));
	}
	
	private final ToggleMenuItemDescription buildXmlCollectionReferenceMappingMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(this.getMapAsXmlCollectionReferenceAction(context));
	}
	
	private final ToggleMenuItemDescription buildXmlFragmentMappingMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(this.getMapAsXmlFragmentAction(context));
	}
	
	private final ToggleMenuItemDescription buildXmlFragmentCollectionMappingMenuItem(WorkbenchContext context) {
		return new ToggleMenuItemDescription(this.getMapAsXmlFragmentCollectionAction(context));
	}
	
	
	// **************** Mapping actions ***************************************
	
	private ToggleFrameworkAction getMapAsAnyObjectAction(WorkbenchContext context) {
		return new MapAsAnyObjectAction(context);
	}	
	
	private ToggleFrameworkAction getMapAsAnyCollectionAction(WorkbenchContext context) {
		return new MapAsAnyCollectionAction(context);
	}	
	
	private ToggleFrameworkAction getMapAsAnyAttributeAction(WorkbenchContext context) {
		return new MapAsAnyAttributeAction(context);
	}	
	
	private ToggleFrameworkAction getMapAsXmlObjectReferenceAction(WorkbenchContext context) {
		return new MapAsXmlObjectReferenceAction(context);
	}	
	
	private ToggleFrameworkAction getMapAsXmlCollectionReferenceAction(WorkbenchContext context) {
		return new MapAsXmlCollectionReferenceAction(context);
	}	
	
	private ToggleFrameworkAction getMapAsXmlFragmentAction(WorkbenchContext context) {
		return new MapAsXmlFragmentAction(context);
	}	

	private ToggleFrameworkAction getMapAsXmlFragmentCollectionAction(WorkbenchContext context) {
		return new MapAsXmlFragmentCollectionAction(context);
	}	

	@Override
	protected ToggleFrameworkAction getMapAsXmlDirectCollectionAction(WorkbenchContext context) {
		return new MapAsOxDirectCollectionAction(context);
	}
	
	@Override
	protected ToggleFrameworkAction getMapAsXmlDirectAction(WorkbenchContext context) {
		return new MapAsOxDirectAction(context);
	}
}
