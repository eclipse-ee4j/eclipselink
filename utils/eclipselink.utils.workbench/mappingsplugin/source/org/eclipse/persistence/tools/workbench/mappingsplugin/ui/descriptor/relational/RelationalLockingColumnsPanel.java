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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ColumnCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalMappingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CompositeCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListCollectionValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;


public class RelationalLockingColumnsPanel extends AbstractSubjectPanel
{
	// **************** Constructors ******************************************
	
	RelationalLockingColumnsPanel(PropertyValueModel lockingPolicyHolder, WorkbenchContextHolder contextHolder) 
	{
		super(lockingPolicyHolder, contextHolder);
		this.initializeLayout();
	}
	
	
	// **************** Initialization ****************************************
		
	protected void initializeLayout() 
	{
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 1, 1, 1);

		this.add(this.buildColumnLockingColumnsList(), constraints);

		addHelpTopicId(this, this.helpTopicId());

	}
	
	private AddRemoveListPanel buildColumnLockingColumnsList() {
		AddRemoveListPanel listPanel = 
            new AddRemoveListPanel(
                    getApplicationContext(), 
                    this.buildPrimaryKeysAddRemoveAdapter(), 
									 						  
                    this.buildSortedColumnLockingColumnsHolder(), 
                    AddRemovePanel.RIGHT,
                    resourceRepository().getString("LOCKING_POLICY_SELECTED_FIELDS_LOCKING"),
                    RelationalMappingComponentFactory.buildColumnNodeSelector(getWorkbenchContextHolder()));
		listPanel.setBorder(buildStandardEmptyBorder());
		listPanel.setCellRenderer(buildColumnLockingColumnsListCellRenderer());
		return listPanel;
	}
	
	private AddRemoveListPanel.Adapter buildPrimaryKeysAddRemoveAdapter() {
		return new AddRemoveListPanel.Adapter() {	
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				listSelectionModel.setSelectedValues(addColumnLockingColumns());
			}
			
			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				removeColumnLockingColumns(listSelectionModel.getSelectedValues());
			}
		};
	}
	
	/**
	 * Overide since this panel should be treated as a single Component.
	 */
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		Component[] children = getComponents();
		for (int i = 0; i < children.length; i++)
		{
			children[i].setEnabled(enabled);
		}
	}
	
	Object[] addColumnLockingColumns()
	{
		Object[] columnLockingColumns = new LockingColumnDialog(getWorkbenchContext()).promptForPrimaryKeys();
		
		for (int i = 0; i < columnLockingColumns.length; i ++) {
			((MWTableDescriptorLockingPolicy)relationalDescriptor().getLockingPolicy()).addColumnLockColumn((MWColumn) columnLockingColumns[i]);
		}
		
		return columnLockingColumns;
	}
	
	void removeColumnLockingColumns(Object[] lockingColumns) 
	{
		for (int i = 0; i < lockingColumns.length; i ++) {
			((MWTableDescriptorLockingPolicy)relationalDescriptor().getLockingPolicy()).removeColumnLockColumn((MWColumn) lockingColumns[i]);
		}
	}
	
		
	private ListValueModel buildSortedColumnLockingColumnsHolder() 
	{	
		return new SortedListValueModelAdapter(buildNamedColumnLockingColumnsHolder());
	}
	
	private ListValueModel buildNamedColumnLockingColumnsHolder() 
	{
		return new ItemPropertyListValueModelAdapter(buildColumnLockingColumnsHolder(), MWColumn.QUALIFIED_NAME_PROPERTY, MWColumn.DATABASE_TYPE_PROPERTY);
	}
	
	private CollectionValueModel buildColumnLockingColumnsHolder() 
	{
		return new CollectionAspectAdapter(getSubjectHolder(), MWTableDescriptorLockingPolicy.COLUMN_LOCK_COLUMNS_COLLECTION) 
		{
			protected Iterator getValueFromSubject() 
			{
				return ((MWTableDescriptorLockingPolicy) this.subject).columnLockColumns();
			}
		};
	}
	
	private ListCellRenderer buildColumnLockingColumnsListCellRenderer() 
	{
		return new AdaptableListCellRenderer(new ColumnCellRendererAdapter(resourceRepository()));
	}
	
	
	// **************** Internal **********************************************
	
	private MWTableDescriptor relationalDescriptor() 
	{
		return ((MWTableDescriptorLockingPolicy) this.getSubjectHolder().getValue()).getOwningTableDescriptor();
	}

	/**
	 * broaden access a bit
	 */
	protected ValueModel getSubjectHolder() {
		return super.getSubjectHolder();
	}
	
	// **************** Public ************************************************
	
	public String helpTopicId() 
	{
		return "descriptor.locking.selectedfields";
	}
	
	
	// **************** Member classes ****************************************
	
	private class LockingColumnDialog
		extends AbstractDialog
	{	
		private ListModel lockingColumnsModel;
		
		private ObjectListSelectionModel lockingColumnsSelectionModel;
		
		
		LockingColumnDialog(WorkbenchContext context) 
		{
			super(context);
		}
		
		protected void initialize() 
		{
			super.initialize();
			this.lockingColumnsModel = this.buildPrimaryKeyListAdapter();
			this.lockingColumnsSelectionModel = this.buildLockingColumnsSelectionModel();
			
			this.setTitle(resourceRepository().getString("LOCKING_ADD_REMOVE_DIALOG_TITLE"));
			this.getOKAction().setEnabled(false);
		}
		
		private ListModel buildPrimaryKeyListAdapter() 
		{
			return new ListModelAdapter(this.buildSortedColumnsHolder());
		}
		
		private ListValueModel buildSortedColumnsHolder() {
			return new SortedListValueModelAdapter(buildAllColumnsCollectionHolder());
		}
		
		private CollectionValueModel buildAllColumnsCollectionHolder() {
			return new CompositeCollectionValueModel(buildSortedTablesHolder()) {
				protected CollectionValueModel transform(Object value) {
					return new ListCollectionValueModelAdapter(buildSortedColumnsHolder((MWTable) value));
				}
			};
		}
		
		private ListValueModel buildSortedTablesHolder() {
			return new SortedListValueModelAdapter(buildTableNameAdapter());
		}
		
		private ListValueModel buildTableNameAdapter() {
			return new ItemPropertyListValueModelAdapter(buildTablesHolder(), MWTable.QUALIFIED_NAME_PROPERTY);
		}

		private CollectionValueModel buildTablesHolder() {
			return new CollectionAspectAdapter(getSubjectHolder()) {
				protected Iterator getValueFromSubject() {
					return ((MWTableDescriptorLockingPolicy) this.subject).getOwningTableDescriptor().associatedTables();
				}
			};
		}

		ListValueModel buildSortedColumnsHolder(MWTable table) {
			return new SortedListValueModelAdapter(buildColumnNameAdapter(table));
		}
		private ListValueModel buildColumnNameAdapter(MWTable table) {
			return new ItemPropertyListValueModelAdapter(buildColumnsHolder(table), MWColumn.NAME_PROPERTY);
		}
		

		private CollectionAspectAdapter buildColumnsHolder(MWTable table) {
			return new CollectionAspectAdapter(MWTable.COLUMNS_COLLECTION, table) {
				protected Iterator getValueFromSubject() {
					return ((MWTable) this.subject).columns();
				}
				protected int sizeFromSubject() {
					return ((MWTable) this.subject).columnsSize();
				}
			};	
		}
		
		private ObjectListSelectionModel buildLockingColumnsSelectionModel() {
			ObjectListSelectionModel selectionModel = new ObjectListSelectionModel(this.lockingColumnsModel);
			selectionModel.addListSelectionListener(this.buildSelectionListener());
			return selectionModel;
		}
		
		private ListSelectionListener buildSelectionListener() {
			return new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting()) {
						LockingColumnDialog.this.selectionChanged();
					}
				}
			};
		}
		
		void selectionChanged() {
			this.getOKAction().setEnabled(! this.lockingColumnsSelectionModel.isSelectionEmpty());
		}
		
		protected Component buildMainPanel() {
			JList list = SwingComponentFactory.buildList(this.lockingColumnsModel);
			list.setSelectionModel(this.lockingColumnsSelectionModel);
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			list.setCellRenderer(this.buildListCellRenderer());
			list.addMouseListener(buildListMouseListener());
			return new JScrollPane(list);
		}
		
		private ListCellRenderer buildListCellRenderer() {
			return new AdaptableListCellRenderer(new ColumnCellRendererAdapter(this.resourceRepository()));			
		}
		
		/**
		 * Double-clicking on a selection in the list will automatically
		 * make the selection.
		 */
		protected MouseListener buildListMouseListener() {
			return new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						LockingColumnDialog.this.clickOK();
					}
				}
			};
		}

		/**
		 * broaden access a bit
		 */
		protected void clickOK() {
			super.clickOK();
		}

		protected String helpTopicId() {
			return RelationalLockingColumnsPanel.this.helpTopicId();
		}
		
		Object[] promptForPrimaryKeys() {
			this.show();
			
			if (this.wasConfirmed()) {
				return this.lockingColumnsSelectionModel.getSelectedValues();
			}
			return new Object[0];
		}
	}
}
