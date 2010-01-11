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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ListIterator;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.*;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;


final class SchemaComponentDetailsPanel 
	extends AbstractPanel 
{
	
	private PropertyValueModel schemaComponentNodeHolder;
	
	
	SchemaComponentDetailsPanel(ApplicationContext context, PropertyValueModel schemaComponentNodeHolder) {
		super(new BorderLayout(), context);
		this.initialize(schemaComponentNodeHolder);
		this.initializeLayout();
	}
	
	private void initialize(PropertyValueModel schemaComponentNodeHolder) {
		this.schemaComponentNodeHolder = schemaComponentNodeHolder;
	}
	
	private void initializeLayout() {
		
		JLabel detailsLabel = this.buildDetailsLabel();
		JTable detailsTable = this.buildDetailsTable();
		detailsLabel.setLabelFor(detailsTable);
		
		this.add(detailsLabel, BorderLayout.BEFORE_FIRST_LINE);
		this.add(detailsTable, BorderLayout.CENTER);
	}
	
	private JLabel buildDetailsLabel() {
		JLabel label = new JLabel(resourceRepository().getString("DETAILS_TABLE_LABEL"));
		label.setDisplayedMnemonic(resourceRepository().getMnemonic("DETAILS_TABLE_LABEL"));
		Color background = label.getBackground() != null ? label.getBackground() : UIManager.getColor("Panel.background");
		label.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, background.darker()), 
			BorderFactory.createEmptyBorder(2, 4, 2, 2)));

		JTableHeader header = new JTableHeader();
		label.setBackground(header.getBackground());
		label.setForeground(header.getForeground());
		label.setFont(header.getFont());
		return label;
	}
	
	private JTable buildDetailsTable() {
		JTable table = SwingComponentFactory.buildTable(this.buildDetailsTableModel());
		table.setBorder(BorderFactory.createEmptyBorder());
		table.setPreferredSize(new Dimension(200, 300));
		return table;
	}
	
	private TableModelAdapter buildDetailsTableModel() {
		return new TableModelAdapter(this.buildDetailsAdapter(), this.buildDetailsTableColumnModelAdapter());
	}
	
	private ListValueModel buildDetailsAdapter() {
		return new ListAspectAdapter(this.schemaComponentNodeHolder) {
			protected ListIterator getValueFromSubject() {
				return ((SchemaComponentNode) this.subject).details();
			}
		};
	}
	
	private ColumnAdapter buildDetailsTableColumnModelAdapter() {
		return new DetailsColumnAdapter();
	}
	
	
	// **************** Member classes ****************************************
	
	private static class DetailsColumnAdapter
		implements ColumnAdapter
	{
		private static final int COLUMN_COUNT 			= 2;
		
		private static final int DETAIL_NAME_COLUMN 	= 0;
		private static final int DETAIL_VALUE_COLUMN 	= 1;
		
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}
		
		public String getColumnName(int index) {
			return "";
		}
		
		public Class getColumnClass(int index) {
			return String.class;
		}
		
		public boolean isColumnEditable(int index) {
			return false;
		}
		
		public PropertyValueModel[] cellModels(Object subject) {
			SchemaComponentDetail detail = (SchemaComponentDetail) subject;
			PropertyValueModel[] cellModels = new PropertyValueModel[COLUMN_COUNT];
			
			cellModels[DETAIL_NAME_COLUMN] = this.buildDetailNameAdapter(detail);
			cellModels[DETAIL_VALUE_COLUMN] = this.buildDetailValueAdapter(detail);
			
			return cellModels;
		}
		
		private PropertyValueModel buildDetailNameAdapter(final SchemaComponentDetail detail) {
			//TODO not positive, but don't think we should be using a read only propertyValueModel here.  
			return new AbstractReadOnlyPropertyValueModel() {
				public Object getValue() {
					return detail.getName();
				}
			};
		}
		
		private PropertyValueModel buildDetailValueAdapter(SchemaComponentDetail detail) {
			return new PropertyAspectAdapter(SchemaComponentDetail.VALUE_PROPERTY, detail) {
				protected Object getValueFromSubject() {
					return ((SchemaComponentDetail) this.subject).getValue();
				}
			};
		}
	}
}
