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

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.SessionCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * DatabaseSessionNode defines a SC Node that is wrapping a DatabaseSessionAdapter.
 */
public class DatabaseSessionNode extends SessionNode {
	
	// ********** constructors/initialization **********
	public DatabaseSessionNode( DatabaseSessionAdapter session, TreeNodeValueModel parent, SCPlugin plugin, ApplicationContext context) {

		super( session, parent, plugin, context);
	}

	// **************** factory methods ****************************************

	protected AbstractPropertiesPage buildPropertiesPage(WorkbenchContext context) {

		AbstractPropertiesPage propertiesPage = null;
	
		if( this.session().platformIsXml()) {
			propertiesPage = new XmlSessionTabbedPropertiesPage(context);
		}
		else if( this.session().platformIsRdbms()) {
			propertiesPage = new RdbmsSessionTabbedPropertiesPage(context);
		}
		else if( this.session().platformIsEis()) {
			propertiesPage = new EisSessionTabbedPropertiesPage(context);
		}
		return propertiesPage;
	}

	protected Object propertiesPageKey() {

		if( this.session().platformIsXml()) {
			return XmlSessionTabbedPropertiesPage.class;
		}
		else if( this.session().platformIsRdbms()) {
			return RdbmsSessionTabbedPropertiesPage.class;
		}
		else if( this.session().platformIsEis()) {
			return EisSessionTabbedPropertiesPage.class;
		}
		throw new IllegalArgumentException("The key of the properties page is unknown");
	}

	public String helpTopicID() {
		return "navigator.session.database";
	}

	protected String buildIconKey() {
		return SessionCellRendererAdapter.iconKey(session());
	}

}
