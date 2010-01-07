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
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCustomReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCustomReadObjectQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWDeleteQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWInsertQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWUpdateQuery;
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

		//	Create the tabbed pane
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

		// Create the Insert Query tab
		JComponent insertSQLPane = buildInsertQueryPanel();

		insertSQLPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), insertSQLPane.getBorder()));
		addHelpTopicId(insertSQLPane, helpTopicId() + ".insert");
		sqlStringTabbedPane.addTab(resourceRepository().getString("CUSTOM_QUERY_INSERT_TAB"), insertSQLPane);

		// Create the Update Query tab
		JComponent updateSQLPane = buildUpdateQueryPanel();

		updateSQLPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), updateSQLPane.getBorder()));
		addHelpTopicId(updateSQLPane, helpTopicId() + ".update");
		sqlStringTabbedPane.addTab(resourceRepository().getString("CUSTOM_QUERY_UPDATE_TAB"), updateSQLPane);

		// Create the Delete Query tab
		JComponent deleteSQLPane = buildDeleteQueryPanel();

		deleteSQLPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), deleteSQLPane.getBorder()));
		addHelpTopicId(deleteSQLPane, helpTopicId() + ".delete");
		sqlStringTabbedPane.addTab(resourceRepository().getString("CUSTOM_QUERY_DELETE_TAB"), deleteSQLPane);

		// Create the Read Object Query tab
		JComponent readObjectSQLPane = buildReadObjectQueryPanel();

		readObjectSQLPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), readObjectSQLPane.getBorder()));
		addHelpTopicId(readObjectSQLPane, helpTopicId() + ".readObject");
		sqlStringTabbedPane.addTab(resourceRepository().getString("CUSTOM_QUERY_READ_OBJECT_TAB"), readObjectSQLPane);

		// Create the Read All Query tab
		JComponent readAllSQLPane = buildReadAllQueryPanel();

		readAllSQLPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), readAllSQLPane.getBorder()));
		addHelpTopicId(readAllSQLPane, helpTopicId() + ".readAll");
		sqlStringTabbedPane.addTab(resourceRepository().getString("CUSTOM_QUERY_READ_ALL_TAB"), readAllSQLPane);
		
		addHelpTopicId(panel, helpTopicId());

		return panel;
	}
	
	// *********** insert query ***********

	private JComponent buildInsertQueryPanel()
	{
		return new CustomQuerySelectionCriteriaPanel(buildInsertQueryHolder(), getWorkbenchContextHolder());
	}

	private PropertyValueModel buildInsertQueryHolder() {
		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.INSERT_QUERY_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWRelationalQueryManager)subject).getInsertQuery();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWRelationalQueryManager)subject).setInsertQuery((MWInsertQuery)value);
			}
		};
	}

	// *********** update query ***********

	private JComponent buildUpdateQueryPanel() {
		return new CustomQuerySelectionCriteriaPanel(buildUpdateQueryHolder(), getWorkbenchContextHolder());
	}

	private PropertyValueModel buildUpdateQueryHolder() {
		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.UPDATE_QUERY_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWRelationalQueryManager)subject).getUpdateQuery();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWRelationalQueryManager)subject).setUpdateQuery((MWUpdateQuery)value);
			}
		};
	}


	// *********** delete query ***********

	private JComponent buildDeleteQueryPanel() {
		return new CustomQuerySelectionCriteriaPanel(buildDeleteQueryHolder(), getWorkbenchContextHolder());
	}

	private PropertyValueModel buildDeleteQueryHolder() {
		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.DELETE_QUERY_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWRelationalQueryManager)subject).getDeleteQuery();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWRelationalQueryManager)subject).setDeleteQuery((MWDeleteQuery)value);
			}
		};
	}


	// *********** read object query ***********

	private JComponent buildReadObjectQueryPanel() {
		return new CustomQuerySelectionCriteriaPanel(buildReadObjectQueryHolder(), getWorkbenchContextHolder());
	}

	private PropertyValueModel buildReadObjectQueryHolder() {
		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.READ_OBJECT_QUERY_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWRelationalQueryManager)subject).getReadObjectQuery();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWRelationalQueryManager)subject).setReadObjectQuery((MWCustomReadObjectQuery)value);
			}
		};
	}


	// *********** read all query ***********

	private JComponent buildReadAllQueryPanel() {
		return new CustomQuerySelectionCriteriaPanel(buildReadAllQueryHolder(), getWorkbenchContextHolder());
	}

	private PropertyValueModel buildReadAllQueryHolder() {
		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.READ_ALL_QUERY_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((MWRelationalQueryManager)subject).getReadAllQuery();
			}

			@Override
			protected void setValueOnSubject(Object value) {
				((MWRelationalQueryManager)subject).setReadAllQuery((MWCustomReadAllQuery)value);
			}
		};
	}
	
	private JTextArea buildTextArea(PropertyValueModel stringHolder) {
		JTextArea textArea = new JTextArea(new DocumentAdapter(stringHolder));
		textArea.setFont(new Font("dialog", Font.PLAIN, 12));	
		return textArea;
	}
	
		
	// *********** insert sql ***********
	
//	private JTextArea buildInsertSqlTextArea() {
//		return buildTextArea(buildInsertSqlHolder());
//	}
//	
//	private PropertyValueModel buildInsertSqlHolder() {
//		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.INSERT_SQL_STRING_PROPERTY) {
//			protected Object getValueFromSubject() {
//				return ((MWRelationalQueryManager) subject).getInsertSQLString();
//			}
//
//			protected void setValueOnSubject(Object value) {
//				((MWRelationalQueryManager) subject).setInsertSQLString((String) value);
//			}
//		};	
//	}
//	
//	// *********** update sql ***********
//	
//	private JTextArea buildUpdateSqlTextArea() {
//		return buildTextArea(buildUpdateSqlHolder());
//	}
//	
//	private PropertyValueModel buildUpdateSqlHolder() {
//		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.UPDATE_SQL_STRING_PROPERTY) {
//			protected Object getValueFromSubject() {
//				return ((MWRelationalQueryManager) subject).getUpdateSQLString();
//			}
//
//			protected void setValueOnSubject(Object value) {
//				((MWRelationalQueryManager) subject).setUpdateSQLString((String) value);
//			}
//		};	
//	}
//	
//	
//	// *********** delete sql ***********
//	
//	private JTextArea buildDeleteSqlTextArea() {
//		return buildTextArea(buildDeleteSqlHolder());
//	}
//	
//	private PropertyValueModel buildDeleteSqlHolder() {
//		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.DELETE_SQL_STRING_PROPERTY) {
//			protected Object getValueFromSubject() {
//				return ((MWRelationalQueryManager) subject).getDeleteSQLString();
//			}
//
//			protected void setValueOnSubject(Object value) {
//				((MWRelationalQueryManager) subject).setDeleteSQLString((String) value);
//			}
//		};	
//	}
//	
//	
//	// *********** read object sql ***********
//	
//	private JTextArea buildReadObjectSqlTextArea() {
//		return buildTextArea(buildReadObjectSqlHolder());
//	}
//	
//	private PropertyValueModel buildReadObjectSqlHolder() {
//		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.READ_OBJECT_SQL_STRING_PROPERTY) {
//			protected Object getValueFromSubject() {
//				return ((MWRelationalQueryManager) subject).getReadObjectSQLString();
//			}
//
//			protected void setValueOnSubject(Object value) {
//				((MWRelationalQueryManager) subject).setReadObjectSQLString((String) value);
//			}
//		};	
//	}
//	
//	
//	// *********** read all sql ***********
//	
//	private JTextArea buildReadAllSqlTextArea() {
//		return buildTextArea(buildReadAllSqlHolder());
//	}
//	
//	private PropertyValueModel buildReadAllSqlHolder() {
//		return new PropertyAspectAdapter(this.queryManagerHolder, MWRelationalQueryManager.READ_ALL_SQL_STRING_PROPERTY) {
//			protected Object getValueFromSubject() {
//				return ((MWRelationalQueryManager) subject).getReadAllSQLString();
//			}
//
//			protected void setValueOnSubject(Object value) {
//				((MWRelationalQueryManager) subject).setReadAllSQLString((String) value);
//			}
//		};	
//	}
}
