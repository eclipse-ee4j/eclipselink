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
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool;

import java.util.List;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.ui.view.EmptyPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.SCApplicationNode;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * PoolNode defines a SC Node that is wrapping a ConnectionPoolAdapter.
 */
public class PoolNode extends SCApplicationNode {
	
	/**
	 * Constructor for SessionNode
	 */
	public PoolNode( ConnectionPoolAdapter pool, TreeNodeValueModel parent, SCPlugin plugin, ApplicationContext context) {
		super( pool, parent, plugin, context);
	}

	protected List buildDisplayStringPropertyNamesList() {
		
		List displayStrings = super.buildDisplayStringPropertyNamesList();
		displayStrings.add( ConnectionPoolAdapter.NAME_PROPERTY);
		return displayStrings;
	}

	protected FrameworkAction buildDeleteNodeAction(WorkbenchContext workbenchContext) {
		
		return new DeletePoolAction(workbenchContext);
	}

	ConnectionPoolAdapter pool() {
		
		return ( ConnectionPoolAdapter)this.getValue();
	}
	
	protected AbstractPropertiesPage buildPropertiesPage(WorkbenchContext context) {

		AbstractPropertiesPage propertiesPage = null;

		if( this.pool().platformIsXml()) {
			
			propertiesPage = new EmptyPropertiesPage(context);
		}
		else if( this.pool().platformIsRdbms()) {
			
			propertiesPage = this.buildRdbmsPropertiesPage(context);
		}
		else if( this.pool().platformIsEis()) {
			
			propertiesPage = this.buildEisPropertiesPage(context);
		}
		return propertiesPage;
	}

	private AbstractPropertiesPage buildRdbmsPropertiesPage(WorkbenchContext context) {
			
		if( this.pool().isWriteConnectionPool()) {
			return new WritePoolTabbedPropertiesPage(context);
		}
		else if( this.pool().isReadConnectionPool()) {
			return new RdbmsReadPoolTabbedPropertiesPage(context);
		}
		return new PoolTabbedPropertiesPage(context);
	}
		
	private AbstractPropertiesPage buildEisPropertiesPage(WorkbenchContext context) {
			
		if( this.pool().isWriteConnectionPool()) {
			return new WritePoolTabbedPropertiesPage(context);
		}
		else if( this.pool().isReadConnectionPool()) {
			return new EisReadPoolTabbedPropertiesPage(context);
		}
		return new EisPoolTabbedPropertiesPage(context);
	}
		
	private Object rdbmsPropertiesPageKey() {
		
		if( this.pool().isWriteConnectionPool()) {
			return WritePoolTabbedPropertiesPage.class;
		}
		else if( this.pool().isReadConnectionPool()) {
			return RdbmsReadPoolTabbedPropertiesPage.class;
		}
		return PoolTabbedPropertiesPage.class;
	}

	private Object eisPropertiesPageKey() {
		
		if( this.pool().isWriteConnectionPool()) {
			return WritePoolTabbedPropertiesPage.class;
		}
		else if( this.pool().isReadConnectionPool()) {
			return EisReadPoolTabbedPropertiesPage.class;
		}
		return EisPoolTabbedPropertiesPage.class;
	}

	protected Object propertiesPageKey() {

		if( this.pool().platformIsRdbms()) {
			
			return rdbmsPropertiesPageKey();
		}
		else if( this.pool().platformIsEis()) {
			
			return eisPropertiesPageKey();
		}

		return EmptyPropertiesPage.class;
	}

	public String helpTopicID() {
		return "navigator.connectionPool";
	}

	protected String buildIconKey() {
		if( pool().isReadConnectionPool())
			return "CONNECTION_POOL_READ";

		if( pool().isSequenceConnectionPool())
			return "CONNECTION_POOL_SEQUENCE";

		if( pool().isWriteConnectionPool())
			return "CONNECTION_POOL_WRITE";

		return "CONNECTION_POOL";
	}

	protected FrameworkAction buildRenameNodeAction(WorkbenchContext workbenchContext) {
		
		return new RenamePoolAction( workbenchContext);
	}
}
