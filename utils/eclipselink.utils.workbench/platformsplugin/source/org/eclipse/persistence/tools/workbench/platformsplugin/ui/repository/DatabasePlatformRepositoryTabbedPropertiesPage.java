/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;


/**
 * just a couple of tabs:
 * 	- general properties
 * 	- JDBC mappings
 */
final class DatabasePlatformRepositoryTabbedPropertiesPage extends TabbedPropertiesPage {

	public DatabasePlatformRepositoryTabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initializeTabs() {
		this.addTab(this.buildGeneralPropertiesPage(), this.buildGeneralPropertiesPageTitleKey());
		this.addTab(this.buildJDBCMappingsPropertiesPage(), this.buildJDBCMappingsPropertiesPageTitleKey());
		this.addTab(this.buildJavaMappingsPropertiesPage(), this.buildJavaMappingsPropertiesPageTitleKey());
	}

	private Component buildGeneralPropertiesPage() {
		return new DatabasePlatformRepositoryGeneralPropertiesPage(this.getNodeHolder(), this.getWorkbenchContextHolder());
	}

	private String buildGeneralPropertiesPageTitleKey() {
		return "DATABASE_PLATFORM_REPOSITORY_GENERAL_TAB_TITLE";
	}

	private Component buildJDBCMappingsPropertiesPage() {
		return new DatabasePlatformRepositoryJDBCMappingsPropertiesPage(this.getNodeHolder(), this.getWorkbenchContextHolder());
	}

	private String buildJDBCMappingsPropertiesPageTitleKey() {
		return "DATABASE_PLATFORM_REPOSITORY_JDBC_MAPPINGS_TAB_TITLE";
	}

	private Component buildJavaMappingsPropertiesPage() {
		return new DatabasePlatformRepositoryJavaMappingsPropertiesPage(this.getNodeHolder(), this.getWorkbenchContextHolder());
	}

	private String buildJavaMappingsPropertiesPageTitleKey() {
		return "DATABASE_PLATFORM_REPOSITORY_JAVA_MAPPINGS_TAB_TITLE";
	}

}
