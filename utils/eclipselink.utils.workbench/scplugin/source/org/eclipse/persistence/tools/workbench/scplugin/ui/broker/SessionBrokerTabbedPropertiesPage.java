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
package org.eclipse.persistence.tools.workbench.scplugin.ui.broker;

// JDK
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionLoggingPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionOptionsPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionServerPlatformPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering.SessionClusteringPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.SessionDisplayableTranslatorAdapter;
import org.eclipse.persistence.tools.workbench.uitools.DisplayableAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * @version 10.1.3
 * @author Pascal Filion
 */
final class SessionBrokerTabbedPropertiesPage extends TabbedPropertiesPage
{

	SessionBrokerTabbedPropertiesPage(WorkbenchContext context)
	{
		super(context);
	}

	protected Component buildClusteringPropertiesPage()
	{
		return new SessionClusteringPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildClusteringPropertiesPageTitle()
	{
		return "SESSION_CLUSTERING_TAB_TITLE";
	}

	protected DisplayableAdapter buildDisplayableAdapter()
	{
		return new SessionDisplayableTranslatorAdapter(resourceRepository());
	}

	protected AbstractPropertiesPage buildGeneralPropertiesPage()
	{
		return new GeneralTabbedPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildGeneralPropertiesPageTitle()
	{
		return "SESSION_GENERAL_TAB_TITLE";
	}

	protected AbstractPropertiesPage buildLoggingPropertiesPage()
	{
		return new SessionLoggingPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildLoggingPropertiesPageTitle()
	{
		return "SESSION_LOGGING_TAB_TITLE";
	}

	protected AbstractPropertiesPage buildOptionsPropertiesPage()
	{
		return new SessionOptionsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildOptionsPropertiesPageTitle()
	{
		return "SESSION_OPTIONS_TAB_TITLE";
	}

	protected void initializeTabs()
	{
		addTab(buildGeneralPropertiesPage(),    buildGeneralPropertiesPageTitle());
		addTab(buildOptionsPropertiesPage(),    buildOptionsPropertiesPageTitle());
		addTab(buildLoggingPropertiesPage(),    buildLoggingPropertiesPageTitle());
		addTab(buildClusteringPropertiesPage(), buildClusteringPropertiesPageTitle());
	}

	private class GeneralTabbedPropertiesPage extends TabbedPropertiesPage
	{
		private GeneralTabbedPropertiesPage(PropertyValueModel nodeHolder,
														WorkbenchContextHolder contextHolder)
		{
			super(nodeHolder, contextHolder);
		}

		private AbstractPropertiesPage buildServerPlatformPropertiesPage()
		{
			return new SessionServerPlatformPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
		}

		private String buildServerPlatformPropertiesPageTitle()
		{
			return "SESSION_SERVER_PLATFORM_TAB_TITLE";
		}

		private AbstractPropertiesPage buildSessionsPropertiesPage()
		{
			return new SessionBrokerSessionsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
		}

		private String buildSessionsPropertiesPageTitle()
		{
			return "SESSION_SESSIONS_TAB_TITLE";
		}

		protected JTabbedPane buildTabbedPane()
		{
			JTabbedPane tabbedPane = super.buildTabbedPane();
			tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return tabbedPane;
		}

		protected Component buildTitlePanel()
		{
			return new JComponent() {};
		}

		protected void initializeTabs()
		{
			addTab(buildSessionsPropertiesPage(),       0, buildSessionsPropertiesPageTitle());
			addTab(buildServerPlatformPropertiesPage(), 1, buildServerPlatformPropertiesPageTitle());
		}
	}
}
