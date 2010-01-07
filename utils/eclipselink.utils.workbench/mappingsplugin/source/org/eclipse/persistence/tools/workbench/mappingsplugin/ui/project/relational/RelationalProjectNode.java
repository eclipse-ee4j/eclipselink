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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.DatabaseNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.TableNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.UiDbBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode.DescriptorNodeBuilder;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.AggregateDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.InterfaceDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.RelationalClassDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.RelationalDescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.TableDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.UiDescriptorRelationalBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.UiMappingRelationalBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;


public final class RelationalProjectNode 
	extends ProjectNode 
{
	// ************ constructors **********

	public RelationalProjectNode(MWRelationalProject project, ApplicationContext context, MappingsPlugin plugin) {
		super(project, plugin, context);
	}
    
    protected ApplicationContext expandContext(ApplicationContext context) {
        return super.expandContext(context).
            buildExpandedResourceRepositoryContext(UiDescriptorRelationalBundle.class).
            buildExpandedResourceRepositoryContext(UiMappingRelationalBundle.class).
            buildExpandedResourceRepositoryContext(UiDbBundle.class);
    }
    
	// ********** ProjectNode implementation **********

	public String getCannotAutomapDescriptorsStringKey() {
		return "RELATIONAL_PROJECT_UNAUTOMAPPABLE";
	}

	// ************ ProjectNode implementation **********

	protected Child buildMetaDataRepositoryNode() {
		return new DatabaseNode(this.getProject().getDatabase(), this, this.getMappingsPlugin(), this.getApplicationContext());
	}
	
	
	// ********** AbstractApplicationNode overrides **********
	
	protected String accessibleNameKey() {
		return "ACCESSIBLE_RELATIONAL_PROJECT_NODE";
	}

	protected void addToMenuDescription(GroupContainerDescription menuDescription, WorkbenchContext context) {
		menuDescription.add(this.buildClassActionGroup(context));
		menuDescription.add(this.buildCloseDeleteActionGroup(context));
		menuDescription.add(this.buildSaveActionGroup(context));
		menuDescription.add(this.buildExportActionGroup(context));
		menuDescription.add(this.buildAutomapActionGroup(context));
		menuDescription.add(this.buildOracleHelpMenuGroup(context));
	}
	
	private MenuGroupDescription buildAutomapActionGroup(WorkbenchContext context) {
		MenuGroupDescription autoMapGroup = new MenuGroupDescription();
		autoMapGroup.add(this.getMappingsPlugin().getAutomapAction(context));
		return autoMapGroup;
	}
	
	protected String buildIconKey() {
		return "project.relational";
	}
	
	
	// ********** MWApplicationNode overrides **********
	
	protected Class propertiesPageClass() {
		return RelationalProjectTabbedPropertiesPage.class;
	}
	
	
	// *********** ProjectNode implementation *********
	
	protected DescriptorPackageNode buildDescriptorPackageNodeFor(MWDescriptor descriptor) {
		return new RelationalDescriptorPackageNode(descriptor.packageName(), this, this.getDescriptorNodeBuilder());
	}

	protected DescriptorNodeBuilder buildDescriptorNodeBuilder() {
		return new DescriptorPackageNode.DescriptorNodeBuilder() {
			public DescriptorNode buildDescriptorNode(MWDescriptor descriptor, DescriptorPackageNode descriptorPackageNode) {
				if (descriptor instanceof MWTableDescriptor) {
					return new TableDescriptorNode((MWTableDescriptor) descriptor, descriptorPackageNode);
				} 
				else if (descriptor instanceof MWInterfaceDescriptor) {
					return new InterfaceDescriptorNode((MWInterfaceDescriptor) descriptor, descriptorPackageNode);
				} 
				else if (descriptor instanceof MWAggregateDescriptor) {
					return new AggregateDescriptorNode((MWAggregateDescriptor) descriptor, descriptorPackageNode);
				} 
				else {
					throw new IllegalArgumentException(descriptor.toString());
				}
			}
		};
	}
	
	protected GroupContainerDescription buildExportMenuDescription(WorkbenchContext context) {
		GroupContainerDescription exportMenu =
			new MenuDescription(
					this.resourceRepository().getString("EXPORT_MENU"),
					this.resourceRepository().getString("EXPORT_MENU"), 
					this.resourceRepository().getMnemonic("EXPORT_MENU"),
					this.resourceRepository().getIcon("file.export")
			);
		MenuGroupDescription groupDesc = new MenuGroupDescription();
        groupDesc.add(this.getExportDeploymentXmlAction(context));
        if (getMappingsPlugin().isDevelopmentModeIn(context)) {
            groupDesc.add(this.getExportDeploymentXmlAndInitializeRuntimeDescriptorsAction(context));
        }
		groupDesc.add(this.getExportProjectJavaSourceAction(context));
		groupDesc.add(this.getModelJavaSourceAction(context));
		groupDesc.add(this.getExportTableCreatorJavaSourceAction(context));
		exportMenu.add(groupDesc);
		
		return exportMenu;
	}
	
	public boolean supportsExportProjectJavaSource() {
		return true;
	}
	
	public boolean supportsExportTableCreatorJavaSource() {
		return true;
	}
	
	protected FrameworkAction getExportProjectJavaSourceAction(WorkbenchContext context) {
		return this.getMappingsPlugin().getExportProjectJavaSourceAction(context);
	}
	
	protected FrameworkAction getExportTableCreatorJavaSourceAction(WorkbenchContext context) {
		return this.getMappingsPlugin().getExportTableCreatorJavaSourceAction(context);
	}
    
    public void selectTableNodeFor(MWTable table, NavigatorSelectionModel nsm) {
        selectTableNode(tableNodeFor(table), nsm);
    }
    
    public void selectTableNode(TableNode tableNode, NavigatorSelectionModel nsm) {
        nsm.setSelectedNode(tableNode);
    }
    public TableNode tableNodeFor(MWTable table) {
        DatabaseNode databaseNode = (DatabaseNode) descendantNodeForValue(table.getDatabase());
        return databaseNode.tableNodeFor(table); 
    }
    
    public void selectColumn(MWColumn column, WorkbenchContext context) {
        TableNode tableNode = tableNodeFor(column.getTable());       
        selectTableNode(tableNode, context.getNavigatorSelectionModel());
        tableNode.selectColumn(column, context);            
    }
    
    public void selectReference(MWReference reference, WorkbenchContext context) {
        TableNode tableNode = tableNodeFor(reference.getSourceTable());
        selectTableNode(tableNode, context.getNavigatorSelectionModel());
        tableNode.selectReference(reference, context);
    }
    
    public void selectQueryKey(MWQueryKey queryKey, WorkbenchContext context) {
        RelationalClassDescriptorNode descriptorNode = (RelationalClassDescriptorNode) descriptorNodeFor(queryKey.getDescriptor());
        selectDescriptorNode(descriptorNode, context.getNavigatorSelectionModel());
        descriptorNode.selectQueryKey(queryKey, context);
    }
}
