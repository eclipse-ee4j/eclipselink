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
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.SCApplicationNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.DatabaseSessionNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.ServerSessionNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.SessionNode;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * Abstract class for SC Nodes that have Session Nodes as children node.
 */
public abstract class SessionsNode extends SCApplicationNode {

	private ListValueModel childrenModel;
		
	// ********** constructors/initialization **********
	
	public SessionsNode( SCAdapter scAdapter, TreeNodeValueModel parent, SCPlugin plugin, ApplicationContext context) {
	
		super( scAdapter, parent, plugin, context);
	}

	// **************** Initialization ****************************************

	protected void initialize() {
		super.initialize();
		this.childrenModel = this.buildChildrenModel();
	}
	
	// **************** factory methods ****************************************

	protected ListValueModel buildChildrenModel() {
		return new SortedListValueModelAdapter( this.buildDisplayStringAdapter());
	}

	protected ListValueModel buildDisplayStringAdapter() {
		return new ItemPropertyListValueModelAdapter( this.buildChildrenNodeWrapper(), DISPLAY_STRING_PROPERTY);
	}

	protected ListValueModel buildChildrenNodeWrapper() {
		return new TransformationListValueModelAdapter( this.buildSessionsAspectAdapter()) {
			protected Object transformItem( Object item) {
				return SessionsNode.this.buildChildNode(( SessionAdapter) item);
			}
		};
	}
	
	protected abstract CollectionValueModel buildSessionsAspectAdapter();

	protected AbstractApplicationNode buildChildNode(SessionAdapter session) {
		SessionNode node = null;
		
		if( session instanceof ServerSessionAdapter)
			node = new ServerSessionNode(( ServerSessionAdapter)session, this, ( SCPlugin)this.getPlugin(), this.getApplicationContext());
		else if( session instanceof DatabaseSessionAdapter)
			node = new DatabaseSessionNode(( DatabaseSessionAdapter)session, this, ( SCPlugin)this.getPlugin(), this.getApplicationContext());
		else
			throw new IllegalArgumentException( "Invalid Session");
		return node;
	}


	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}

}
