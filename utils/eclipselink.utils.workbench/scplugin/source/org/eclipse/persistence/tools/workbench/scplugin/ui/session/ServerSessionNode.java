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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ReadConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.WriteConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.AddNewNamedPoolAction;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.AddReadPoolAction;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.AddSequencePoolAction;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.AddWritePoolAction;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.PoolNode;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;


/**
 * ServerSessionNode defines a SC Node that is wrapping a ServerSessionAdapter.
 */
public class ServerSessionNode extends DatabaseSessionNode {

	private ListValueModel childrenModel;

	// ********** constructors/initialization **********
	public ServerSessionNode( ServerSessionAdapter session, TreeNodeValueModel parent, SCPlugin plugin, ApplicationContext context) {

		super( session, parent, plugin, context);
	}

	// **************** Initialization ****************************************

	protected void initialize() {
		super.initialize();
		this.childrenModel = this.buildChildrenModel();
	}
	
	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext)
	{
		WorkbenchContext wrappedContext = buildLocalWorkbenchContext(workbenchContext);

		GroupContainerDescription desc = new RootMenuDescription();
		if (!serverSession().platformIsXml())
		{
			MenuDescription newMenuDesc = new MenuDescription(resourceRepository().getString("NEW_MENU"),
									resourceRepository().getString("NEW_MENU"), 
									resourceRepository().getMnemonic("NEW_MENU"),
									EMPTY_ICON);
			
			MenuGroupDescription poolGroup = new MenuGroupDescription();
			poolGroup.add(getAddNamedPoolAction(wrappedContext));
			poolGroup.add(getAddSequencePoolAction(wrappedContext));
			poolGroup.add(getAddWritePoolAction(wrappedContext));
			poolGroup.add(getAddReadPoolAction(wrappedContext));
			
			newMenuDesc.add(poolGroup);
			MenuGroupDescription poolMainMenuGroup = new MenuGroupDescription();
			poolMainMenuGroup.add(newMenuDesc);
			desc.add(poolMainMenuGroup);
		}
		MenuGroupDescription deleteRenameGroup = new MenuGroupDescription();
		deleteRenameGroup.add(getRenameNodeAction(wrappedContext));
		deleteRenameGroup.add(getDeleteNodeAction(wrappedContext));
		desc.add(deleteRenameGroup);
		
		desc.add(buildHelpMenuGroup(wrappedContext));
		
		return desc;
	}
	
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext)
	{
		WorkbenchContext wrappedContext = buildLocalWorkbenchContext(workbenchContext);

		ToolBarDescription desc = new ToolBarDescription();
		
		if (!serverSession().platformIsXml())
		{
			ToolBarButtonGroupDescription poolGroup = new ToolBarButtonGroupDescription();
			poolGroup.add(getAddNamedPoolAction(wrappedContext));
			poolGroup.add(getAddSequencePoolAction(wrappedContext));
			poolGroup.add(getAddWritePoolAction(wrappedContext));
			poolGroup.add(getAddReadPoolAction(wrappedContext));
			
			desc.add(poolGroup);
		}
		return desc;
	}

	// **************** factory methods ****************************************
	
	// the list should be sorted
	protected ListValueModel buildChildrenModel() {
		return new SortedListValueModelAdapter( this.buildDisplayStringAdapter());
	}

	// the display string (name) of each node can change
	protected ListValueModel buildDisplayStringAdapter() {
		
		return new ItemPropertyListValueModelAdapter( this.buildChildrenNodeWrapper(), DISPLAY_STRING_PROPERTY);
	}
	
	// wrap the config models in nodes
	protected ListValueModel buildChildrenNodeWrapper() {
		
		return new TransformationListValueModelAdapter( this.buildChildrenAspectAdapter()) {
			protected Object transformItem( Object item) {
				return ServerSessionNode.this.buildChildNode(( ConnectionPoolAdapter) item);
			}
		};
	}

	protected PoolNode buildChildNode( ConnectionPoolAdapter pool) {
		
		if( pool instanceof ReadConnectionPoolAdapter) {
			return buildReadConnectionPoolNode();
		}
		else if( pool instanceof WriteConnectionPoolAdapter) {
			return buildWriteConnectionPoolNode();
		}
		return new PoolNode(pool, this, this.getSCPlugin(), this.getApplicationContext());
	}

	// the list of children can change
	protected CollectionValueModel buildChildrenAspectAdapter() {
		
		return new CollectionAspectAdapter( this, ServerSessionAdapter.POOLS_CONFIG_COLLECTION) {
			protected Iterator getValueFromSubject() {
				ServerSessionAdapter session = ( ServerSessionAdapter)subject;
				if (session.platformIsXml()) {
					return NullIterator.instance();
				}
				Collection collection = CollectionTools.collection((( ServerSessionAdapter)subject).pools());
				if (session.hasReadPool()) 
					collection.add( session.getReadConnectionPool());
				if( session.hasWritePool())
					collection.add( session.getWriteConnectionPool());
				if( session.hasSequencePool())
					collection.add( session.getSequenceConnectionPool());
				return collection.iterator();
			}
			protected int sizeFromSubject() {
				int readPool = (((( ServerSessionAdapter)subject).hasReadPool()) ? 1 : 0);
				int writePool = (((( ServerSessionAdapter)subject).hasWritePool()) ? 1 : 0);
				int sequencePool = (((( ServerSessionAdapter)subject).hasSequencePool()) ? 1 : 0);
				return (( ServerSessionAdapter)subject).poolsSize() + readPool + writePool + sequencePool;
			}
		};
	}

	protected PoolNode buildReadConnectionPoolNode() {
		
		return new PoolNode((( ServerSessionAdapter)this.session()).getReadConnectionPool(), this, getSCPlugin(), getApplicationContext());
	}

	protected PoolNode buildWriteConnectionPoolNode() {
		
		return new PoolNode((( ServerSessionAdapter)this.session()).getWriteConnectionPool(), this, getSCPlugin(), getApplicationContext());
	}

	private FrameworkAction getAddNamedPoolAction(WorkbenchContext workbenchContext) {
		return new AddNewNamedPoolAction(workbenchContext);
	}

	private FrameworkAction getAddSequencePoolAction(WorkbenchContext workbenchContext) {
		return new AddSequencePoolAction(workbenchContext);
	}

	private FrameworkAction getAddWritePoolAction(WorkbenchContext workbenchContext) {
		return new AddWritePoolAction(workbenchContext);
	}

	private FrameworkAction getAddReadPoolAction(WorkbenchContext workbenchContext) {
		return new AddReadPoolAction(workbenchContext);
	}

	protected List buildDisplayStringPropertyNamesList() {
		
		List displayStrings = super.buildDisplayStringPropertyNamesList();
		displayStrings.add( ConnectionPoolAdapter.NAME_PROPERTY);
		return displayStrings;
	}
	
	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}
	
	public String helpTopicID() {
		return "navigator.session.server";
	}

	private ServerSessionAdapter serverSession() {
		return (ServerSessionAdapter) session();
	}
}
