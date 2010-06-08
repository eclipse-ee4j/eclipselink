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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.XmlProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeAdapter;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


public final class XmlSchemaRepositoryNode 
	extends MappingsApplicationNode
	implements ProjectNode.Child
{	
	
	// **************** Instance variables ************************************
	
	private ListValueModel childrenModel;
	
	
	// **************** Constructors ******************************************
	
	public XmlSchemaRepositoryNode(MWXmlSchemaRepository schemaRepository, XmlProjectNode projectNode) {
		this(schemaRepository, projectNode.getApplicationContext(), projectNode.getPlugin(), projectNode);
	}
	
	private XmlSchemaRepositoryNode(MWXmlSchemaRepository schemaRepository, ApplicationContext context, Plugin plugin, XmlProjectNode projectNode) {
		super(schemaRepository, projectNode, plugin, context);
	}
	
	
	// **************** Initialization ****************************************
	
	/** Overridden from AbstractTreeNode */
	protected /*private-protected*/ ApplicationContext expandContext(ApplicationContext context) {
		return super.expandContext(context).buildExpandedResourceRepositoryContext(UiSchemaResourceBundle.class);
	}
	
	
	// *********** ProjectChildNode implementation *********
		
	public int getProjectNodeChildPriority() {
		return 1;
	}
	

	// **************** Actions ***********************************************
	
	private FrameworkAction getImportSchemaAction(WorkbenchContext context) {
		return new ImportSchemaAction(context);
	}
	
	private FrameworkAction getReimportAllSchemasAction(WorkbenchContext context) {
		return new ReimportAllSchemasAction(context);
	}
	
	
	// **************** TreeNodeValueModel contract ***************************
	
	public ListValueModel getChildrenModel() {
		if (this.childrenModel == null) {
			this.childrenModel = this.buildChildrenModel();
		}
		
		return this.childrenModel;
	}
	
	// the list should be sorted
	protected ListValueModel buildChildrenModel() {
		ListValueModel childrenModel = 
			new SortedListValueModelAdapter(this.buildDisplayStringAdapter());
		childrenModel.addListChangeListener(ValueModel.VALUE, this.buildChildrenModelListener());
		return childrenModel;
	}
	
	private ListChangeListener buildChildrenModelListener() {
		return new ListChangeAdapter() {
			public void itemsRemoved(ListChangeEvent e) {
				for (Iterator stream = e.items(); stream.hasNext(); ) {
					((XmlSchemaNode) stream.next()).disposePropertiesPages();
				}
			}
		};
	}
	
	// the display string (name) of each schema node can change
	protected ListValueModel buildDisplayStringAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildNodeWrapperAdapter(), DISPLAY_STRING_PROPERTY);
	}
	
	// wrap the schemas in nodes
	protected ListValueModel buildNodeWrapperAdapter() {
		return new TransformationListValueModelAdapter(this.buildNodeChildrenAdapter()) {
			protected Object transformItem(Object item) {
				return new XmlSchemaNode((MWXmlSchema) item, XmlSchemaRepositoryNode.this);
			}
		};
	}
	
	// the collection of schemas can change
	protected CollectionValueModel buildNodeChildrenAdapter() {
		return new CollectionAspectAdapter(this, MWXmlSchemaRepository.SCHEMAS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWXmlSchemaRepository) this.subject).schemas();
			}
			protected int sizeFromSubject() {
				return ((MWXmlSchemaRepository) this.subject).schemasSize();
			}
		};
	}
	
	
	// **************** ApplicationNode contract ******************************
	
	public String helpTopicID() {
		return "xmlSchemaRepository";
	}

	// **************** AbstractApplicationNode contract **********************
	
	protected String buildDisplayString() {
		return this.resourceRepository().getString("SCHEMA_REPOSITORY_NODE_DISPLAY_STRING");
	}
	
	protected String buildIconKey() {
		return "file.xml.multi";
	}

	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return XmlSchemaRepositoryPanel.class;
	}

	
	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext)
	{
		WorkbenchContext localContext = buildLocalWorkbenchContext(workbenchContext);

		RootMenuDescription desc = new RootMenuDescription();
		MenuGroupDescription group = new MenuGroupDescription();
		group.add(getImportSchemaAction(localContext));
		group.add(getReimportAllSchemasAction(localContext));
		desc.add(group);
		
		desc.add(buildOracleHelpMenuGroup(localContext));
		
		return desc;
	}
	
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext)
	{
		WorkbenchContext localContext = buildLocalWorkbenchContext(workbenchContext);

		ToolBarDescription desc = new ToolBarDescription();
		ToolBarButtonGroupDescription group = new ToolBarButtonGroupDescription();
		group.add(getImportSchemaAction(localContext));
		group.add(getReimportAllSchemasAction(localContext));
		desc.add(group);
		
		return desc;
	}
	
	// **************** Convenience *******************************************
	
	MWXmlSchemaRepository getSchemaRepository() {
		return (MWXmlSchemaRepository) this.getValue();
	}
	
	
	// **************** Member classes ****************************************
	
	private final class ImportSchemaAction
		extends AbstractFrameworkAction
	{
		private ImportSchemaAction(WorkbenchContext context) {
			super(context);
		}
		
		protected void initialize() {
			this.initializeTextAndMnemonic("IMPORT_SCHEMA_ACTION");
			//this.setAccelerator(getResourceRepository().getAccelerator("???"));
			this.initializeToolTipText("IMPORT_SCHEMA_ACTION.TOOL_TIP");
			this.initializeIcon("file.xml.new");
			this.setEnabled(true);
		}
		
		protected void execute(ApplicationNode selectedNode) {
			MWXmlSchema schema = 
				new ImportSchemaDialog(
					this.getWorkbenchContext(), 
					((XmlSchemaRepositoryNode) selectedNode).getSchemaRepository()
				).promptToImportSchema();
			
			if (schema != null) {
				this.navigatorSelectionModel().setSelectedNode(selectedNode.descendantNodeForValue(schema));
			}
		}
	}
	
	private final class ReimportAllSchemasAction
		extends AbstractFrameworkAction
	{
		private ReimportAllSchemasAction(WorkbenchContext context) {
			super(context);
		}
		
		protected void initialize() {
			this.initializeTextAndMnemonic("REIMPORT_ALL_SCHEMAS_ACTION");
			//this.setAccelerator(getResourceRepository().getAccelerator("???"));
			this.initializeToolTipText("REIMPORT_ALL_SCHEMAS_ACTION");
			this.initializeIcon("file.xml.multi.refresh");
			this.setEnabled(true);
		}
		
		protected void execute(ApplicationNode selectedNode) {
			Iterator schemasIterator = ((XmlSchemaRepositoryNode) selectedNode).getSchemaRepository().schemas();
			if (schemasIterator.hasNext()) {
				SchemaDialogUtilities.reloadSchemas(getWorkbenchContext(), schemasIterator);
			}
		}
	}
}
