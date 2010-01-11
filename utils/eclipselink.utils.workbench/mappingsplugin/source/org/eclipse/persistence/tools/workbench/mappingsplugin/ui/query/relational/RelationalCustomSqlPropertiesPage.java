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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQueryManager;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


public final class RelationalCustomSqlPropertiesPage 
	extends ScrollablePropertiesPage  
{
	private PropertyValueModel queryManagerHolder;

	public RelationalCustomSqlPropertiesPage(PropertyValueModel relationalDescriptorNodeHolder, WorkbenchContextHolder contextHolder) {
		super(relationalDescriptorNodeHolder, contextHolder);
	}
	
	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.queryManagerHolder = buildQueryManagerHolder();
	}

	private PropertyValueModel buildQueryManagerHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWTableDescriptor) subject).getQueryManager();
			}
		};
	}

	protected String helpTopicId() {
		return "descriptor.queryManager.customSQL";
	}
	
	protected Component buildPage() {
		setName("Queries");
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		//
		// Action:	Create the tabbed pane
		//
		JTabbedPane sqlStringTabbedPane = new JTabbedPane();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(sqlStringTabbedPane, constraints);
		//
		// Action:	Create the Insert SQL string tab
		//
		JTextArea insertSQLStringTextArea = buildInsertSqlTextArea();
		JScrollPane insertSQLStringScrollPane = new JScrollPane(insertSQLStringTextArea);
		insertSQLStringScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), insertSQLStringScrollPane.getBorder()));
		addHelpTopicId(insertSQLStringScrollPane, helpTopicId() + ".insert");
		sqlStringTabbedPane.addTab(resourceRepository().getString("CUSTOM_SQL_INSERT_TAB"), insertSQLStringScrollPane);
		//
		// Action:	Create the Update SQL string tab
		//
		JTextArea updateSQLStringTextArea = buildUpdateSqlTextArea();
		JScrollPane updateSQLStingScrollPane = new JScrollPane(updateSQLStringTextArea);
		updateSQLStingScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), updateSQLStingScrollPane.getBorder()));
		addHelpTopicId(updateSQLStingScrollPane, helpTopicId() + ".update");
		sqlStringTabbedPane.addTab(resourceRepository().getString("CUSTOM_SQL_UPDATE_TAB"), updateSQLStingScrollPane);
		//
		// Action:	Create the Delete SQL string tab
		//
		JTextArea deleteSQLStringTextArea = buildDeleteSqlTextArea();
		JScrollPane deleteSQLStingScrollPane = new JScrollPane(deleteSQLStringTextArea);
		deleteSQLStingScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), deleteSQLStingScrollPane.getBorder()));
		addHelpTopicId(deleteSQLStingScrollPane, helpTopicId() + ".delete");
		sqlStringTabbedPane.addTab(resourceRepository().getString("CUSTOM_SQL_DELETE_TAB"), deleteSQLStingScrollPane);
		//
		// Action:	Create the Read object SQL string tab
		//
		JTextArea readObjectSQLStringTextArea = buildReadObjectSqlTextArea();
		JScrollPane readObjectSQLStingScrollPane = new JScrollPane(readObjectSQLStringTextArea);
		readObjectSQLStingScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), readObjectSQLStingScrollPane.getBorder()));
		addHelpTopicId(readObjectSQLStingScrollPane, helpTopicId() + ".readObject");
		sqlStringTabbedPane.addTab(
			resourceRepository().getString("CUSTOM_SQL_READ_OBJECT_TAB"),
			readObjectSQLStingScrollPane);
		//
		// Action:	Create the Read all SQL string tab
		//
		JTextArea readAllSQLStringTextArea = buildReadAllSqlTextArea();
		JScrollPane readAllSQLStingScrollPane = new JScrollPane(readAllSQLStringTextArea);
		readAllSQLStingScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), readAllSQLStingScrollPane.getBorder()));
		addHelpTopicId(readAllSQLStingScrollPane, helpTopicId() + ".readAll");
		sqlStringTabbedPane.addTab(resourceRepository().getString("CUSTOM_SQL_READ_ALL_TAB"), readAllSQLStingScrollPane);
		
		addHelpTopicId(panel, helpTopicId());

		return panel;
	}
	
	
	private JTextArea buildTextArea(PropertyValueModel stringHolder) {
		JTextArea textArea = new JTextArea(new DocumentAdapter(stringHolder));
		textArea.setFont(new Font("dialog", Font.PLAIN, 12));	
		return textArea;
	}
	
		
	// *********** insert sql ***********
	
	private JTextArea buildInsertSqlTextArea() {
		return buildTextArea(buildInsertSqlHolder());
	}
	
	private PropertyValueModel buildInsertSqlHolder() {
		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.INSERT_SQL_STRING_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalQueryManager) subject).getInsertSQLString();
			}

			protected void setValueOnSubject(Object value) {
				((MWRelationalQueryManager) subject).setInsertSQLString((String) value);
			}
		};	
	}
	
	// *********** update sql ***********
	
	private JTextArea buildUpdateSqlTextArea() {
		return buildTextArea(buildUpdateSqlHolder());
	}
	
	private PropertyValueModel buildUpdateSqlHolder() {
		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.UPDATE_SQL_STRING_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalQueryManager) subject).getUpdateSQLString();
			}

			protected void setValueOnSubject(Object value) {
				((MWRelationalQueryManager) subject).setUpdateSQLString((String) value);
			}
		};	
	}
	
	
	// *********** delete sql ***********
	
	private JTextArea buildDeleteSqlTextArea() {
		return buildTextArea(buildDeleteSqlHolder());
	}
	
	private PropertyValueModel buildDeleteSqlHolder() {
		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.DELETE_SQL_STRING_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalQueryManager) subject).getDeleteSQLString();
			}

			protected void setValueOnSubject(Object value) {
				((MWRelationalQueryManager) subject).setDeleteSQLString((String) value);
			}
		};	
	}
	
	
	// *********** read object sql ***********
	
	private JTextArea buildReadObjectSqlTextArea() {
		return buildTextArea(buildReadObjectSqlHolder());
	}
	
	private PropertyValueModel buildReadObjectSqlHolder() {
		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.READ_OBJECT_SQL_STRING_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalQueryManager) subject).getReadObjectSQLString();
			}

			protected void setValueOnSubject(Object value) {
				((MWRelationalQueryManager) subject).setReadObjectSQLString((String) value);
			}
		};	
	}
	
	
	// *********** read all sql ***********
	
	private JTextArea buildReadAllSqlTextArea() {
		return buildTextArea(buildReadAllSqlHolder());
	}
	
	private PropertyValueModel buildReadAllSqlHolder() {
		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.READ_ALL_SQL_STRING_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalQueryManager) subject).getReadAllSQLString();
			}

			protected void setValueOnSubject(Object value) {
				((MWRelationalQueryManager) subject).setReadAllSQLString((String) value);
			}
		};	
	}
}
