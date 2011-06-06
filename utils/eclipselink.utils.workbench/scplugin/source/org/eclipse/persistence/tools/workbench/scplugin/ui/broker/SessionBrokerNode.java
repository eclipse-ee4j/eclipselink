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
package org.eclipse.persistence.tools.workbench.scplugin.ui.broker;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.DeleteBrokerAction;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.RenameSessionAction;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.SessionsNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.SessionCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListCollectionValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * SessionBrokerNode share the same behavior then a ProjectNode. 
 * It has to display DatabaseSession and ServerSession with their children nodes.
 */
public class SessionBrokerNode extends SessionsNode {
	
	// ********** constructors/initialization **********

	public SessionBrokerNode( SessionBrokerAdapter broker, TreeNodeValueModel parent, SCPlugin plugin, ApplicationContext context) {

		super( broker, parent, plugin, context);
	}

	// **************** factory methods ****************************************


	protected CollectionValueModel buildSessionsAspectAdapter() {
		
		return new ListCollectionValueModelAdapter(
			new ItemPropertyListValueModelAdapter(this.buildChildrenDirtyAdapter(), DIRTY_PROPERTY)
		);
	}

	private ListValueModel buildChildrenDirtyAdapter() {
		return new CollectionListValueModelAdapter(
			new CollectionAspectAdapter( this, SessionBrokerAdapter.SESSIONS_COLLECTION) {
				protected Iterator getValueFromSubject() {
					return (( SessionBrokerAdapter)subject).sessions();
				}
				protected int sizeFromSubject() {
					return (( SessionBrokerAdapter)subject).sessionsSize();
				}
			}
		);
	}

	protected AbstractPropertiesPage buildPropertiesPage(WorkbenchContext context) {

		return new SessionBrokerTabbedPropertiesPage(context);
	}

	protected Object propertiesPageKey() {

		return SessionBrokerTabbedPropertiesPage.class;
	}

	protected List buildDisplayStringPropertyNamesList() {
		
		List displayStrings = super.buildDisplayStringPropertyNamesList();
		displayStrings.add( SessionAdapter.NAME_PROPERTY);
		return displayStrings;
	}
	
	protected String buildIconKey() {
		return SessionCellRendererAdapter.iconKey(broker());
	}

	protected FrameworkAction buildDeleteNodeAction(WorkbenchContext workbenchContext) {

		return new DeleteBrokerAction(workbenchContext);
	}

	protected FrameworkAction buildRenameNodeAction(WorkbenchContext workbenchContext) {

		return new RenameSessionAction(workbenchContext);
	}
	
	SessionBrokerAdapter broker() {
		
		return ( SessionBrokerAdapter)this.getValue();
	}
	
	public String helpTopicID() {
		return "navigator.session.broker";
	}
}
