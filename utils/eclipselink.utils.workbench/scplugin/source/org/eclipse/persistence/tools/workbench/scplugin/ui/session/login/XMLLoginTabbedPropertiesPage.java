/*
 * @(#)SessionLoginPropertiesPage.java
 *
 * Copyright 2004 by Oracle Corporation,
 * 500 Oracle Parkway, Redwood Shores, California, 94065, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Oracle Corporation.
 */
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

public class XMLLoginTabbedPropertiesPage extends TabbedPropertiesPage {

	public XMLLoginTabbedPropertiesPage( PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super( nodeHolder, contextHolder);
	}

	protected Component buildOptionsPropertiesPage() {
		return new XMLOptionsPropertiesPage( getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildOptionsPropertiesPageTitle() {
		return "LOGIN_OPTIONS_TAB_TITLE";
	}

	protected Component buildSequencingPropertiesPage() {
		return new SequencingPropertiesPage( getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildSequencingPropertiesPageTitle() {
		return "LOGIN_SEQUENCING_TAB_TITLE";
	}

	protected Component buildTitlePanel() {
		return new JComponent() { };
	}

	protected JTabbedPane buildTabbedPane()
	{
		JTabbedPane tabbedPane = super.buildTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return tabbedPane;
	}

	protected void initializeTabs() {
		addTab( buildOptionsPropertiesPage(), buildOptionsPropertiesPageTitle());
	}
}