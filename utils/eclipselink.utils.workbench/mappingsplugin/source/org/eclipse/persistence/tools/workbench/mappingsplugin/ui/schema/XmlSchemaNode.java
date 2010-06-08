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

import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsplugin.RemovableNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


public final class XmlSchemaNode 
	extends MappingsApplicationNode
	implements RemovableNode
{
	// **************** Variables *********************************************
	
	/** 
	 * Key -> window, value -> page
	 * 
	 * These are stored like this because each node/window combination needs its
	 * own page in order to preserve the tree expansion state
	 */
	Map propertiesPages;

	/**
	 * this listens for the window to close, which means we can
	 * throw away the properties page associated with the window
	 */
	private WindowListener windowListener;

	protected static final String[] SCHEMA_DISPLAY_STRING_PROPERTY_NAMES = {MWXmlSchema.NAME_PROPERTY};


	// **************** Constructors ******************************************
	
	public XmlSchemaNode(MWXmlSchema schema, XmlSchemaRepositoryNode schemaRepositoryNode) {
		this(schema, schemaRepositoryNode.getApplicationContext(), schemaRepositoryNode.getPlugin(), schemaRepositoryNode);
	}
	
	private XmlSchemaNode(MWXmlSchema schema, ApplicationContext context, Plugin plugin, XmlSchemaRepositoryNode schemaRepositoryNode) {
		super(schema, schemaRepositoryNode, plugin, context);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		this.propertiesPages = new HashMap();
		this.windowListener = this.buildWindowListener();
	}
	
	/**
	 * Remove the page from the cache if the window closes
	 */
	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				XmlSchemaNode.this.disposePropertiesPage(e.getWindow());
			}
		};
	}
	
	
	// **************** Actions ***********************************************
	
	FrameworkAction getReimportSchemaAction(WorkbenchContext context) {
		return new ReimportSchemaAction(context);
	}
	
	FrameworkAction getSchemaPropertiesAction(WorkbenchContext context) {
		return new SchemaPropertiesAction(context);
	}
	
	
	// **************** Properties pages **************************************
	
	public Component propertiesPage(WorkbenchContext context) {
		Window window = context.getCurrentWindow();
		Component propertiesPage = (Component) this.propertiesPages.get(window);
		if (propertiesPage == null) {
			propertiesPage = this.buildPropertiesPage(this.buildLocalWorkbenchContext(context));
			this.propertiesPages.put(window, propertiesPage);
			window.addWindowListener(this.windowListener);
		}
		return propertiesPage;
	}
	
	protected AbstractPropertiesPage buildPropertiesPage(WorkbenchContext context) {
		return new XmlSchemaPanel(this, context);
	}
	
	/** 
	 * This is a no-op, because properties pages for this node are stored locally,
	 * and so they do not need to be released.
	 */
	protected void releasePropertiesPage(AbstractPropertiesPage propertiesPage) {
		// do nothing
	}
	
	void disposePropertiesPages() {
		for (Iterator stream = this.propertiesPages.keySet().iterator(); stream.hasNext(); ) {
			this.disposePropertiesPage((Window) stream.next());
		}
		this.propertiesPages.clear();
	}
	
	/**
	 * Properties pages are disposed differently here than for the general node,
	 * so they need a separately called method.
	 */
	void disposePropertiesPage(Window keyWindow) {
		XmlSchemaPanel propertiesPage = (XmlSchemaPanel) this.propertiesPages.remove(keyWindow);
		propertiesPage.setNode(null, this.buildShellWorkbenchContext());
		keyWindow.removeWindowListener(this.windowListener);
	}
	
	
	// **************** AbstractApplicationNode contract **********************
	
	protected String buildDisplayString() {
		return this.getSchema().getName();
	}
	
	protected String buildIconKey() {
		return "file.xml";
	}
	
	protected String[] displayStringPropertyNames() {
		return SCHEMA_DISPLAY_STRING_PROPERTY_NAMES;
	}
	
	public String helpTopicID() {
		return "xmlSchema";
	}
	
	
	// **************** Convenience *******************************************
	
	MWXmlSchema getSchema() {
		return (MWXmlSchema) this.getValue();
	}
	

	// ********** MWApplicationNode overrides **********

	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext) {
		WorkbenchContext localContext = buildLocalWorkbenchContext(workbenchContext);
		
		RootMenuDescription menuDesc = new RootMenuDescription();
		MenuGroupDescription groupDesc = new MenuGroupDescription();
		groupDesc.add(getReimportSchemaAction(localContext));
		groupDesc.add(getSchemaPropertiesAction(localContext));
		groupDesc.add(getMappingsPlugin().getRemoveAction(localContext));
		menuDesc.add(groupDesc);
		
		menuDesc.add(buildOracleHelpMenuGroup(localContext));
		
		return menuDesc;
	}
	
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext) {
		GroupContainerDescription desc = new ToolBarDescription();
		ToolBarButtonGroupDescription buttonGroup = new ToolBarButtonGroupDescription();
		buttonGroup.add(getReimportSchemaAction(buildLocalWorkbenchContext(workbenchContext)));
		desc.add(buttonGroup);
		
		return desc;
	}
	
	
	// **************** Removable implementation **********************

	public String getName() {
		return getSchema().getName();
	}
	
	public void remove() {
		this.getSchema().schemaRepository().removeSchema(this.getSchema());
	}
	
	
	// **************** Member classes ****************************************
	
	private final class ReimportSchemaAction
		extends AbstractFrameworkAction
	{
		private ReimportSchemaAction(WorkbenchContext context) {
			super(context);
		}
			
		protected void initialize() {
			this.initializeTextAndMnemonic("REIMPORT_SCHEMA_ACTION");
			//this.setAccelerator(getResourceRepository().getAccelerator("???"));
			this.initializeToolTipText("REIMPORT_SCHEMA_ACTION.TOOL_TIP");
			this.initializeIcon("file.xml.refresh");
			this.setEnabled(true);
		}
		
		protected void execute() {
			Iterator nodesIterator = CollectionTools.iterator(this.selectedNodes());
			Iterator schemasIterator = new TransformationIterator(nodesIterator) {
				protected Object transform(Object next) {
					return ((XmlSchemaNode) next).getSchema();
				}
			};
			SchemaDialogUtilities.reloadSchemas(getWorkbenchContext(), schemasIterator);
		}
	}
	
	
	private final class SchemaPropertiesAction
		extends AbstractFrameworkAction
	{
		private SchemaPropertiesAction(WorkbenchContext context) {
			super(context);
		}
		
		protected void initialize() {
			this.setIcon(EMPTY_ICON);
			this.initializeTextAndMnemonic("SCHEMA_PROPERTIES_ACTION");
			//this.setAccelerator(getResourceRepository().getAccelerator("???"));
			this.initializeToolTipText("SCHEMA_PROPERTIES_ACTION.TOOL_TIP");
			this.setEnabled(true);
		}
		
		protected void execute(ApplicationNode selectedNode) {
			new EditSchemaDialog(this.getWorkbenchContext(), ((XmlSchemaNode) selectedNode).getSchema()).promptToEditSchema();
			this.navigatorSelectionModel().setSelectedNode(selectedNode);
		}
	}
}
