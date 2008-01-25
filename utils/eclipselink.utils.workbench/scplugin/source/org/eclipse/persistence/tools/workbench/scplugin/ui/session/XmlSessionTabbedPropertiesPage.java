/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.session;

// JDK
import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ComponentBuilder;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionServerPlatformPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.XmlSessionMultipleProjectsPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.XmlSessionProjectPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * @version 10.1.3
 * @author Pascal Filion
 */
final class XmlSessionTabbedPropertiesPage extends SessionTabbedPropertiesPage
{

	XmlSessionTabbedPropertiesPage(WorkbenchContext context)
	{
		super(context);
	}

	protected AbstractPropertiesPage buildGeneralPropertiesPage()
	{
		return new XmlSessionProjectPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
	}

	protected ComponentBuilder buildMultipleProjectsPageBuilder()
	{
		return new XmlSessionMultipleProjectsPropertiesPageBuilder();
	}

	protected AbstractPropertiesPage buildServerPlatformPropertiesPage()
	{
		return buildPropertiesPage(SessionServerPlatformPropertiesPage.class, "SESSION_SERVER_PLATFORM_TAB_MESSAGE");
	}

	protected String buildServerPlatformPropertiesPageTitle()
	{
		return "SESSION_SERVER_PLATFORM_TAB_TITLE";
	}

	protected void initializeTabs()
	{

		addTab(buildGeneralPropertiesPage(), 0, buildGeneralPropertiesPageTitle());
		addTab(buildMultipleProjectsHolder(), 1, buildMultipleProjectsPageBuilder(), buildMultipleProjectsPropertiesPageTitle());
		addTab(buildServerPlatformPropertiesPage(), 2, buildServerPlatformPropertiesPageTitle());
	}



	private class XmlSessionMultipleProjectsPropertiesPageBuilder implements ComponentBuilder
	{
		private XmlSessionMultipleProjectsPropertiesPage page;

		public Component buildComponent(PropertyValueModel nodeHolder)
		{
			if (this.page == null)
				this.page = new XmlSessionMultipleProjectsPropertiesPage(nodeHolder, getWorkbenchContextHolder());

			return this.page;
		}
	}
}