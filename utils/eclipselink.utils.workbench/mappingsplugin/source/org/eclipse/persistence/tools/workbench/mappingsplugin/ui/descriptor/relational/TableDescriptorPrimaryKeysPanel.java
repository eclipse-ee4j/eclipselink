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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
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
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalPrimaryKeyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalMappingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;


final class TableDescriptorPrimaryKeysPanel 
	extends AbstractPanel 
{
	// **************** Instance variables ************************************
	
	private ValueModel relationalDescriptorHolder;
	
	
	// **************** Constructors ******************************************
	
	TableDescriptorPrimaryKeysPanel(ValueModel relationalDescriptorHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.relationalDescriptorHolder = relationalDescriptorHolder;
		this.initializeLayout();
	}
	
	
	// **************** Initialization ****************************************
	
	private void initializeLayout() {
		this.setBorder(BorderFactory.createCompoundBorder(
			buildTitledBorder("PRIMARY_KEYS_PANEL.TITLE"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));

		GridBagConstraints constraints = new GridBagConstraints();

		// primary keys - add/remove list panel
		AddRemoveListPanel pane = this.buildPrimaryKeysList();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
		this.add(pane, constraints);
		this.addPaneForAlignment(pane);
		
		addHelpTopicId(this, this.helpTopicId());
	}
	
	private AddRemoveListPanel buildPrimaryKeysList() {
		AddRemoveListPanel listPanel = new AddRemoveListPanel(
			getApplicationContext(),
			this.buildPrimaryKeysAddRemoveAdapter(), 
			this.buildSortedPrimaryKeysHolder(),
			AddRemovePanel.RIGHT,
			"PRIMARY_KEYS_PANEL.TITLE",
            RelationalMappingComponentFactory.buildColumnNodeSelector(getWorkbenchContextHolder())
		) {
            protected String addButtonKey() {
                return "PRIMARY_KEYS_PANEL_ADD_BUTTON";
            }
            
            protected String removeButtonKey() {
                return "PRIMARY_KEYS_PANEL_REMOVE_BUTTON";
            }
        };

		listPanel.setBorder(SwingComponentFactory.buildStandardEmptyBorder());
		listPanel.setCellRenderer(buildPrimaryKeysListCellRenderer());
		return listPanel;
	}
	
	private AddRemoveListPanel.Adapter buildPrimaryKeysAddRemoveAdapter() {
		return new AddRemoveListPanel.Adapter() {	
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				listSelectionModel.setSelectedValues(TableDescriptorPrimaryKeysPanel.this.addPrimaryKeys());
			}
			
			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				TableDescriptorPrimaryKeysPanel.this.removePrimaryKeys(listSelectionModel.getSelectedValues());
			}
		};
	}
	
	private Object[] addPrimaryKeys() {
		Object[] primaryKeys = new PrimaryKeyDialog(getWorkbenchContext()).promptForPrimaryKeys();
		
		for (int i = 0; i < primaryKeys.length; i ++) {
			this.relationalDescriptor().primaryKeyPolicy().addPrimaryKey((MWColumn) primaryKeys[i]);
		}
		
		return primaryKeys;
	}
	
	private void removePrimaryKeys(Object[] primaryKeys) {
		for (int i = 0; i < primaryKeys.length; i ++) {
			this.relationalDescriptor().primaryKeyPolicy().removePrimaryKey((MWColumn) primaryKeys[i]);
		}
	}
	
	private ListValueModel buildSortedPrimaryKeysHolder() {	
		return new SortedListValueModelAdapter(this.buildNamedPrimaryKeysHolder());
	}
	
	private ListValueModel buildNamedPrimaryKeysHolder() {
		return new ItemPropertyListValueModelAdapter(this.buildPrimaryKeysHolder(), MWColumn.QUALIFIED_NAME_PROPERTY);
	}
	
	private CollectionValueModel buildPrimaryKeysHolder() {
		return new CollectionAspectAdapter(this.buildPrimaryKeyPolicyHolder(), MWRelationalPrimaryKeyPolicy.PRIMARY_KEYS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWRelationalPrimaryKeyPolicy) this.subject).primaryKeys();
			}
			protected int sizeFromSubject() {
				return ((MWRelationalPrimaryKeyPolicy) this.subject).primaryKeysSize();
			}
		};
	}
	
	private ValueModel buildPrimaryKeyPolicyHolder() {
		return new PropertyAspectAdapter(this.relationalDescriptorHolder) {
			protected Object getValueFromSubject() {
				return ((MWTableDescriptor) this.subject).primaryKeyPolicy();
			}
		};
	}
	
	private ListCellRenderer buildPrimaryKeysListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return ((MWColumn) value).displayString();
			}
		};
	}
	
	
	// **************** Internal **********************************************
	
	private MWTableDescriptor relationalDescriptor() {
		return (MWTableDescriptor) this.relationalDescriptorHolder.getValue();
	}
	
	
	// **************** Public ************************************************
	
	public String helpTopicId() {
		return "descriptor.descriptorInfo.primaryKeys";
	}
	
	
	// **************** Member classes ****************************************
	
	private class PrimaryKeyDialog
		extends AbstractDialog
	{	
		private ListModel primaryKeysModel;
		
		private ObjectListSelectionModel primaryKeysSelectionModel;
		
		
		private PrimaryKeyDialog(WorkbenchContext context) {
			super(context);
		}
		
		protected void initialize() {
			super.initialize();
			this.primaryKeysModel = this.buildPrimaryKeyListAdapter();
			this.primaryKeysSelectionModel = this.buildPrimaryKeysSelectionModel();
			
			this.setTitle(TableDescriptorPrimaryKeysPanel.this.resourceRepository().getString("PRIMARY_KEYS_DIALOG.TITLE"));
			this.getOKAction().setEnabled(false);
		}
		
		private ListModel buildPrimaryKeyListAdapter() {
			return new ListModelAdapter(this.buildSortedPrimaryKeysHolder());
		}
		
		private ListValueModel buildSortedPrimaryKeysHolder() {
			return new SortedListValueModelAdapter(this.buildPrimaryKeysHolder());
		}
		
		private CollectionValueModel buildPrimaryKeysHolder() {
			return new CollectionAspectAdapter(TableDescriptorPrimaryKeysPanel.this.relationalDescriptorHolder) {
				protected Iterator getValueFromSubject() {
					return ((MWTableDescriptor) this.subject).primaryKeyChoices();
				}
			};
		}
		
		private ObjectListSelectionModel buildPrimaryKeysSelectionModel() {
			ObjectListSelectionModel selectionModel = new ObjectListSelectionModel(this.primaryKeysModel);
			selectionModel.addListSelectionListener(this.buildSelectionListener());
			return selectionModel;
		}
		
		private ListSelectionListener buildSelectionListener() {
			return new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting()) {
						PrimaryKeyDialog.this.selectionChanged();
					}
				}
			};
		}
		
		private void selectionChanged() {
			this.getOKAction().setEnabled(! this.primaryKeysSelectionModel.isSelectionEmpty());
		}
		
		protected Component buildMainPanel() {
			JList list = SwingComponentFactory.buildList(this.primaryKeysModel);
			list.setSelectionModel(this.primaryKeysSelectionModel);
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			list.setCellRenderer(this.buildListCellRenderer());
			list.addMouseListener(buildListMouseListener());
			return new JScrollPane(list);
		}
		
		private ListCellRenderer buildListCellRenderer() {
			return new SimpleListCellRenderer() {
				protected String buildText(Object value) {
					return ((MWColumn) value).getName();
				}
			};
		}
		
		/**
		 * Double-clicking on a selection in the list will automatically
		 * make the selection.
		 */
		protected MouseListener buildListMouseListener() {
			return new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						PrimaryKeyDialog.this.clickOK();
					}
				}
			};
		}

		protected String helpTopicId() {
			return TableDescriptorPrimaryKeysPanel.this.helpTopicId();
		}
		
		private Object[] promptForPrimaryKeys() {
			this.show();
			
			if (this.wasConfirmed()) {
				return this.primaryKeysSelectionModel.getSelectedValues();
			}
			else {
				return new Object[0];
			}
		}
	}
}
