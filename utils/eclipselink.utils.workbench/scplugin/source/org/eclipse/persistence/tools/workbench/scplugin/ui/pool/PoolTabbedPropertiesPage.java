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
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic.LoginPropertiesPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic.PoolGeneralPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic.RdbmsPoolLoginPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ConnectionPoolDisplayableTranslatorAdapter;
import org.eclipse.persistence.tools.workbench.uitools.DisplayableAdapter;


public class PoolTabbedPropertiesPage extends TabbedPropertiesPage {

	public PoolTabbedPropertiesPage(WorkbenchContext context) {

		super(context);
	}

	protected String buildConnectionPropertiesPageTitle() {

		return "LOGIN_CONNECTION_TAB_TITLE";
	}

	protected DisplayableAdapter buildDisplayableAdapter()
	{
		return new ConnectionPoolDisplayableTranslatorAdapter(resourceRepository());
	}

	protected Component buildGeneralPropertiesPage() {

		return new PoolGeneralPropertiesPage( this.getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildGeneralPropertiesPageTitle() {

		return "POOL_GENERAL_TAB_TITLE";
	}

	protected Component buildLoginPropertiesPage() {
		
		return new RdbmsPoolLoginPropertiesPage( this.getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildLoginPropertiesPageTitle() {

		return "POOL_LOGIN_TAB_TITLE";
	}

	protected AbstractPropertiesPage buildLoginTabbedPropertiesPage() {

		return new AbstractPropertiesPage( getNodeHolder(), getWorkbenchContextHolder()) {

			protected void initializeLayout() {

				JTabbedPane tabbedPane = new JTabbedPane();
				tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

				tabbedPane.addTab( resourceRepository().getString(buildConnectionPropertiesPageTitle()), buildLoginPropertiesPage());
				tabbedPane.addTab( resourceRepository().getString(buildPropertiesPropertiesPageTitle()), buildPropertiesPropertiesPage());

				add(tabbedPane, BorderLayout.CENTER);
			}
		};
	}

	protected Component buildPropertiesPropertiesPage() {
		
		return new LoginPropertiesPropertiesPage( this.getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildPropertiesPropertiesPageTitle() {

		return "LOGIN_PROPERTIES_TAB_TITLE";
	}
	
	protected void initializeTabs() {

		this.addTab( this.buildGeneralPropertiesPage(),     this.buildGeneralPropertiesPageTitle());
		this.addTab( this.buildLoginTabbedPropertiesPage(), this.buildLoginPropertiesPageTitle());
	}
}
