/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToXmlTypeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UnmappedMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.AggregateMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.DirectToFieldMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.DirectToXmlTypeMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.ManyToManyMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.OneToManyMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.OneToOneMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalDirectCollectionMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalDirectMapMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalMappingSelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalTransformationMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.VariableOneToOneMappingNode;


public abstract class RelationalClassDescriptorNode extends MappingDescriptorNode {
	
	
	// ********** constructors/initialization **********
	
	RelationalClassDescriptorNode(MWMappingDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode);
	}
	
	

	// ********** ApplicationNode implementation **********

	public GroupContainerDescription buildMenuDescription(WorkbenchContext context) {
		GroupContainerDescription desc = super.buildMenuDescription(context);
		context = buildLocalWorkbenchContext(context);
		
		MenuGroupDescription classGroup = new MenuGroupDescription();
		classGroup.add(getMappingsPlugin().getRefreshClassesAction(context));
		classGroup.add(getMappingsPlugin().getAddOrRefreshClassesAction(context));
		classGroup.add(getMappingsPlugin().getCreateNewClassAction(context));
		desc.add(classGroup);
				
		MenuGroupDescription removeRenameGroup = new MenuGroupDescription();
		removeRenameGroup.add(getRemoveDescriptorAction(context));
		removeRenameGroup.add(getRenameDescriptorAction(context));
		removeRenameGroup.add(getMoveDescriptorAction(context));
		desc.add(removeRenameGroup);
		
		MenuGroupDescription inheritedMappingsGroup = new MenuGroupDescription();
		inheritedMappingsGroup.add(buildMapInheritedAttributesMenuDescription(context));
		inheritedMappingsGroup.add(buildUnmapMenuDescription(context));
		desc.add(inheritedMappingsGroup);
		
		MenuGroupDescription mappingsGroup = new MenuGroupDescription();
		mappingsGroup.add(buildGenerateTablesFromDescriptorsMenuDescription(context));
		mappingsGroup.add(getAutomapAction(context));
		desc.add(mappingsGroup);
		
		MenuGroupDescription exportJavaGroup = new MenuGroupDescription();
		exportJavaGroup.add(getMappingsPlugin().getExportSpecificDescriptorModelJavaSourceAction(context));
		desc.add(exportJavaGroup);
		
		desc.add(buildOracleHelpMenuGroup(context));
		
		return desc;
	}
    
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext)
	{
		GroupContainerDescription desc =  super.buildToolBarDescription(workbenchContext);

		WorkbenchContext wrappedContext = buildLocalWorkbenchContext(workbenchContext);
		
		ToolBarButtonGroupDescription buttonGroup = new ToolBarButtonGroupDescription();
		buttonGroup.add(getMorphToAggregateDescriptorAction(wrappedContext));
		buttonGroup.add(getMorphToTableDescriptorAction(wrappedContext));
		desc.add(buttonGroup);
		
		return desc;
	}
	
	protected boolean supportsDescriptorMorphing() {
		return true;
	}
	
	protected MenuGroupDescription buildDescriptorTypeMenuGroupDescription(WorkbenchContext workbenchContext) {
		MenuGroupDescription typeDesc = new MenuGroupDescription();
		typeDesc.add(this.getMorphToAggregateDescriptorAction(workbenchContext));
		typeDesc.add(this.getMorphToTableDescriptorAction(workbenchContext));
		return typeDesc;
	}

	private ToggleFrameworkAction getMorphToAggregateDescriptorAction(WorkbenchContext workbenchContext)
	{
		return new AggregateDescriptorAction(workbenchContext);
	}
		
	private ToggleFrameworkAction getMorphToTableDescriptorAction(WorkbenchContext workbenchContext)
	{
		return new TableDescriptorAction(workbenchContext);
	}

	protected MenuDescription buildGenerateTablesFromDescriptorsMenuDescription(WorkbenchContext workbenchContext) 
	{
		MenuDescription menuDesc = new MenuDescription(resourceRepository().getString("GENERATE_TABLES_FROM_DESCRIPTORS_MENU_ITEM"),
																resourceRepository().getString("GENERATE_TABLES_FROM_DESCRIPTORS_MENU_ITEM"),
																resourceRepository().getMnemonic("GENERATE_TABLES_FROM_DESCRIPTORS_MENU_ITEM"),
																EMPTY_ICON
												);

		MenuGroupDescription groupDesc = new MenuGroupDescription();
		groupDesc.add(getGenerateTablesFromAllDescriptorsAction(workbenchContext));
		groupDesc.add(getGenerateTablesFromSelectedDescriptorsAction(workbenchContext));
		menuDesc.add(groupDesc);
		
		return menuDesc;
	}

	
	private FrameworkAction getGenerateTablesFromAllDescriptorsAction(WorkbenchContext workbenchContext) {
		return new GenerateTablesFromAllDescriptorsAction(workbenchContext);
	}
	
	private FrameworkAction getGenerateTablesFromSelectedDescriptorsAction(WorkbenchContext workbenchContext) {
		return new GenerateTablesFromSelectedDescriptorsAction(workbenchContext);
	}
	
	// ************** MappingDescriptorNode implementation ************
	
	protected MappingNode buildMappingNode(MWMapping mapping) {
		if (mapping instanceof MWDirectToFieldMapping) {
			return new DirectToFieldMappingNode((MWDirectToFieldMapping) mapping, new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
		}
		else if (mapping instanceof MWDirectToXmlTypeMapping) {
			return new DirectToXmlTypeMappingNode((MWDirectToXmlTypeMapping) mapping, new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
		}
		else if (mapping instanceof MWRelationalDirectCollectionMapping) {
			return new RelationalDirectCollectionMappingNode((MWRelationalDirectCollectionMapping) mapping, new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
		}
		else if (mapping instanceof MWRelationalDirectMapMapping) {
			return new RelationalDirectMapMappingNode((MWRelationalDirectMapMapping) mapping, new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
		}
		else if (mapping instanceof MWAggregateMapping) {
			return new AggregateMappingNode((MWAggregateMapping) mapping, new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
		}
		else if (mapping instanceof MWOneToOneMapping) {
			return new OneToOneMappingNode((MWOneToOneMapping) mapping, new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
		}
		else if (mapping instanceof MWVariableOneToOneMapping) {
			return new VariableOneToOneMappingNode((MWVariableOneToOneMapping) mapping, new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
		}
		else if (mapping instanceof MWOneToManyMapping) {
			return new OneToManyMappingNode((MWOneToManyMapping) mapping, new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
		}
		else if (mapping instanceof MWManyToManyMapping) {
			return new ManyToManyMappingNode((MWManyToManyMapping) mapping, new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
		}
		else if (mapping instanceof MWRelationalTransformationMapping) {
			return new RelationalTransformationMappingNode((MWRelationalTransformationMapping) mapping, new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
		}
		
		throw new IllegalArgumentException(mapping.toString());
	}

	protected MappingNode buildUnmappedMappingNode(MWClassAttribute attribute) {
		return new UnmappedMappingNode(attribute, getApplicationContext(), new RelationalMappingSelectionActionsPolicy(getMappingsPlugin()), this);
	}

	public String mappingHelpTopicPrefix() {
//		return "mapping.relational";
		return "mapping"; // For 10.1.3
	}
    
    public abstract void selectQueryKey(MWQueryKey queryKey, WorkbenchContext context);
}
