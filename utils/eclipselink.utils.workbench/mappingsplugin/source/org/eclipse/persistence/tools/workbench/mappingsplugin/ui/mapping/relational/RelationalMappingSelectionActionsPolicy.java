/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToggleMenuItemDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToggleToolBarButtonDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MapAsTransformationAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingSelectionActionsPolicy;


public final class RelationalMappingSelectionActionsPolicy 
	extends MappingSelectionActionsPolicy 
{
	// **************** Constructors ******************************************
	
	public RelationalMappingSelectionActionsPolicy(MappingsPlugin plugin) {
		super(plugin);
	}
	
	
	// **************** SelectionActionsPolicy contract ***********************
	
	protected void addToMapAsMenuDescription(MenuDescription menuDescription, WorkbenchContext context) 
	{
		MenuGroupDescription mappingGroup = new MenuGroupDescription();
		
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsRelationalDirectToFieldAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsObjectTypeAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsTypeConversionAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsSerializedObjectAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsDirectToXmlTypeAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsRelationalDirectCollectionAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsDirectMapAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsAggregateAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsOneToOneAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsVariableOneToOneAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsOneToManyAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsManyToManyAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsTransformationAction(context)));
		mappingGroup.add(new ToggleMenuItemDescription(getMapAsUnmappedAction(context)));
		
		menuDescription.add(mappingGroup);
	}

	public GroupContainerDescription buildToolBarDescription(WorkbenchContext context) 
	{
		
		ToolBarDescription mappingDesc = new ToolBarDescription();
		ToolBarButtonGroupDescription mappingGroup = new ToolBarButtonGroupDescription();				

		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsRelationalDirectToFieldAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsObjectTypeAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsTypeConversionAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsSerializedObjectAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsDirectToXmlTypeAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsRelationalDirectCollectionAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsDirectMapAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsAggregateAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsOneToOneAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsVariableOneToOneAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsOneToManyAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsManyToManyAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsTransformationAction(context)));
		mappingGroup.add(new ToggleToolBarButtonDescription(getMapAsUnmappedAction(context)));
		
		mappingDesc.add(mappingGroup);
		
		return mappingDesc;
	}
	
	private ToggleFrameworkAction getMapAsRelationalDirectToFieldAction(WorkbenchContext context) {
		return new MapAsRelationalDirectToFieldAction(context);
	}

	private ToggleFrameworkAction getMapAsObjectTypeAction(WorkbenchContext context) {
		return new MapAsObjectTypeAction(context);
	}


	private ToggleFrameworkAction getMapAsTypeConversionAction(WorkbenchContext context) {
		return new MapAsTypeConversionAction(context);
	}

	private ToggleFrameworkAction getMapAsSerializedObjectAction(WorkbenchContext context) {
		return new MapAsSerializedObjectAction(context);
	}

	private ToggleFrameworkAction getMapAsDirectToXmlTypeAction(WorkbenchContext context) {
		return new MapAsDirectToXmlTypeAction(context);
	}

	private ToggleFrameworkAction getMapAsRelationalDirectCollectionAction(WorkbenchContext context) {
		return new MapAsRelationalDirectCollectionAction(context);
	}

	private ToggleFrameworkAction getMapAsDirectMapAction(WorkbenchContext context) {
		return new MapAsDirectMapAction(context);
	}

	private ToggleFrameworkAction getMapAsAggregateAction(WorkbenchContext context) {
		return new MapAsAggregateAction(context);
	}

	private ToggleFrameworkAction getMapAsOneToOneAction(WorkbenchContext context) {
		return new MapAsOneToOneAction(context);
	}

	private ToggleFrameworkAction getMapAsVariableOneToOneAction(WorkbenchContext context) {
		return new MapAsVariableOneToOneAction(context);
	}

	private ToggleFrameworkAction getMapAsOneToManyAction(WorkbenchContext context) {
		return new MapAsOneToManyAction(context);
	}
	
	private ToggleFrameworkAction getMapAsManyToManyAction(WorkbenchContext context) {
		return new MapAsManyToManyAction(context);
	}	
	
	private ToggleFrameworkAction getMapAsTransformationAction(WorkbenchContext context) {
		return new MapAsTransformationAction(context);
	}
	
}
