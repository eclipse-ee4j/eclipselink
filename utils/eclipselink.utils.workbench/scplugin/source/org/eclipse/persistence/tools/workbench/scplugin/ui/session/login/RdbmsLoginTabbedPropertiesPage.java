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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ComponentBuilder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


public class RdbmsLoginTabbedPropertiesPage extends TabbedPropertiesPage {

	public RdbmsLoginTabbedPropertiesPage( PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super( nodeHolder, contextHolder);
	}

	protected Component buildConnectionPropertiesPage() {
		return new RdbmsConnectionPropertiesPage( getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildConnectionPropertiesPageTitle() {
		return "LOGIN_CONNECTION_TAB_TITLE";
	}

	protected Component buildOptionsPropertiesPage() {
		return new RdbmsOptionsPropertiesPage( getNodeHolder(), getWorkbenchContextHolder());
	}

	protected String buildOptionsPropertiesPageTitle() {
		return "LOGIN_OPTIONS_TAB_TITLE";
	}

	protected PropertyValueModel buildPropertiesHolder() {

		PropertyAspectAdapter loginHolder = new PropertyAspectAdapter( getSelectionHolder(), DatabaseSessionAdapter.LOGIN_CONFIG_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((DatabaseSessionAdapter) subject).getLogin();
			}
		};

		return new PropertyAspectAdapter( loginHolder, DatabaseLoginAdapter.USE_PROPERTIES_PROPERTY) {
			protected Object buildValue() {
				if (subject == null) {
					return Boolean.FALSE;
				} else {
					return getValueFromSubject();
				}
			}
			public Object getValueFromSubject() {
				DatabaseLoginAdapter session = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf( session.usesProperties());
			}
		};
	}

	protected ComponentBuilder buildPropertiesPageBuilder() {
		return new ComponentBuilder() {
			private RdbmsPropertiesPropertiesPage page;
			
			public Component buildComponent( PropertyValueModel nodeHolder) {
				if (page == null)
					page = new RdbmsPropertiesPropertiesPage( nodeHolder, getWorkbenchContextHolder());

				return page;
			}
		};
	}

	protected String buildPropertiesPropertiesPageTitle() {
		return "LOGIN_PROPERTIES_TAB_TITLE";
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
		addTab( buildConnectionPropertiesPage(), buildConnectionPropertiesPageTitle());
		addTab( buildSequencingPropertiesPage(), buildSequencingPropertiesPageTitle());
		addTab( buildOptionsPropertiesPage(), buildOptionsPropertiesPageTitle());
		addTab( buildPropertiesHolder(), 3, buildPropertiesPageBuilder(), buildPropertiesPropertiesPageTitle());
	}
}
