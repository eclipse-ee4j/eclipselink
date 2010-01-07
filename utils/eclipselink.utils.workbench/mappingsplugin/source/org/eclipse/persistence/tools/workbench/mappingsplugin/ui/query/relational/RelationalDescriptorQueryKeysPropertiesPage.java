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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListSplitPane;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWUserDefinedQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.QueryKeyCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalMappingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


public final class RelationalDescriptorQueryKeysPropertiesPage
	extends ScrollablePropertiesPage {
	private PropertyValueModel selectedQueryKeyHolder;
	private ListChooser fieldChooser;
	
    private AddRemoveListPanel queryKeyListPanel;
    
	public RelationalDescriptorQueryKeysPropertiesPage(PropertyValueModel node, WorkbenchContextHolder contextHolder) {
		super(node, contextHolder);
	}

	protected void initialize(PropertyValueModel descriptorNodeHolder) {
		super.initialize(descriptorNodeHolder);
	}

	protected String helpTopicId() {
		return "descriptor.queryKeys";	
	}
	
	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.selectedQueryKeyHolder = new SimplePropertyValueModel();
		
		// Create the list
		this.queryKeyListPanel = 
			new AddRemoveListPanel(
					getApplicationContext(), 
					buildAddRemoveListPanelAdapter(), 
					buildSortedQueryKeysListValueModel(), 
					resourceRepository().getString("QUERYKEYS"));
		
        this.queryKeyListPanel.setCellRenderer(buildAbstractQueryListCellRenderer());
        this.queryKeyListPanel.addListSelectionListener(buildListSelectionListener(this.queryKeyListPanel));

		AddRemoveListSplitPane addRemoveList = new AddRemoveListSplitPane(this.queryKeyListPanel);

		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 5, 5);

		panel.add(addRemoveList, constraints);

		// Add the Query Key panel
		JPanel selectedQueryKeyPanel = new JPanel(new GridBagLayout());
		addRemoveList.setRightComponent(selectedQueryKeyPanel);

		JLabel fieldLabel = buildLabel("FIELD");
		
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 10, 0, 0);

		selectedQueryKeyPanel.add(fieldLabel, constraints);
		
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 0);

		this.fieldChooser = buildDatabaseFieldChooser();
		fieldLabel.setLabelFor(this.fieldChooser);
		this.fieldChooser.setEnabled(false);
		new ComponentEnabler(buildFieldChooserEnablerModel(queryKeyListPanel), Collections.singleton(this.fieldChooser));
		
		selectedQueryKeyPanel.add(this.fieldChooser, constraints);
		addHelpTopicId(selectedQueryKeyPanel, helpTopicId() + ".field");

		Spacer spacer = new Spacer();

		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		selectedQueryKeyPanel.add(spacer, constraints);

		addHelpTopicId(panel, helpTopicId());
				
		return panel;
		
	}
	
	private AddRemoveListPanel.OptionAdapter buildAddRemoveListPanelAdapter() {
		return new AddRemoveListPanel.OptionAdapter() {
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addNewQueryKey(listSelectionModel);
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				removeSelectedQueryKeys(CollectionTools.iterator(listSelectionModel.getSelectedValues()));
			}
			
			public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
				renameSelectedQueryKey(listSelectionModel);
			}
			
			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
				return listSelectionModel.getSelectedValuesSize() == 1 
					&& !((MWQueryKey)listSelectionModel.getSelectedValue()).isAutoGenerated();
            }

			public String optionalButtonKey() {
				return "RENAME_BUTTON";
			}
		};
	}

	private MWRelationalClassDescriptor descriptor() {
		return (MWRelationalClassDescriptor) getSelectionHolder().getValue();
	}
		
	private Iterator queryKeyNames() {
		return new TransformationIterator(descriptor().allQueryKeys()) {
			protected Object transform(Object next) {
				return ((MWQueryKey)next).getName();
			}
		};
	}
	
	private ListValueModel buildSortedQueryKeysListValueModel() {
		return new SortedListValueModelAdapter(buildItemListValueModelAdapter());
	}
	
	private ListValueModel buildItemListValueModelAdapter() {
		return new ItemPropertyListValueModelAdapter(buildQueryKeysHolder(), MWUserDefinedQueryKey.NAME_PROPERTY);
	}
	
	private void addNewQueryKey(ObjectListSelectionModel listSelectionModel) {
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setTitle(resourceRepository().getString("ADD_NEW_QUERYKEY"));
		builder.setTextFieldDescription(resourceRepository().getString("ENTER_NEW_QUERYKEY_NAME"));
		builder.setExistingNames(queryKeyNames());	
		builder.setHelpTopicId("descriptor.queryKeys");
		
		NewNameDialog dialog = builder.buildDialog(getWorkbenchContext());
		dialog.show();

		if (dialog.wasCanceled())
			return;

		MWUserDefinedQueryKey newQueryKey = descriptor().addQueryKey(dialog.getNewName(), null);
		
		listSelectionModel.setSelectedValue(newQueryKey);
	}
	
	private void renameSelectedQueryKey(ObjectListSelectionModel listSelectionModel) {
        MWUserDefinedQueryKey queryKey = (MWUserDefinedQueryKey)listSelectionModel.getSelectedValue();
		
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setExistingNames(queryKeyNames());
		builder.setOriginalName(queryKey.getName());
		builder.setTextFieldDescription(resourceRepository().getString("RENAME_QUERYKEY_MESSAGE"));
		builder.setTitle(resourceRepository().getString("RENAME_QUERYKEY_TITLE", queryKey.getName()));
		builder.setHelpTopicId("dialog.queryKeyRename");
		
		NewNameDialog dialog = builder.buildDialog(getWorkbenchContext());
		dialog.show();

		if(dialog.wasCanceled())
			return;

		String newQueryKeyName = dialog.getNewName();
		queryKey.setName(newQueryKeyName);
		
		listSelectionModel.setSelectedValue(queryKey);
	}

	private void removeSelectedQueryKeys(Iterator queryKeys) {
		while (queryKeys.hasNext()) {
			this.removeSelectedQueryKey((MWQueryKey) queryKeys.next());
		}
	}
	
	private void removeSelectedQueryKey(MWQueryKey queryKey) {
		if (queryKey.isAutoGenerated()) {
			JOptionPane.showMessageDialog(currentWindow(),
					resourceRepository().getString("CANNOT_REMOVE_AUTOGENERATED"),
					resourceRepository().getString("QUERYKEY_REMOVE"),
					JOptionPane.WARNING_MESSAGE);

		}
		else {
			queryKey.getDescriptor().removeQueryKey((MWUserDefinedQueryKey)queryKey);
		}		
	}
	


	private ListChooser buildDatabaseFieldChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
					RelationalMappingComponentFactory.buildExtendedColumnComboBoxModel(buildFieldHolder(), getSelectionHolder()),
					this.getWorkbenchContextHolder(),
                    RelationalMappingComponentFactory.buildColumnNodeSelector(getWorkbenchContextHolder()),
					RelationalMappingComponentFactory.buildColumnChooserDialogBuilder()
			);
		listChooser.setRenderer(RelationalMappingComponentFactory.buildColumnListRenderer(getSelectionHolder(), resourceRepository()));
		return listChooser;
	}

	private PropertyValueModel buildFieldHolder() {
		return new PropertyAspectAdapter(this.selectedQueryKeyHolder, MWUserDefinedQueryKey.COLUMN_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWQueryKey) this.subject).getColumn();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWUserDefinedQueryKey) this.subject).setColumn((MWColumn)value);
			}
		};
	}
	
	private CollectionValueModel buildQueryKeysHolder() {
		return new CollectionAspectAdapter(getSelectionHolder(), MWRelationalClassDescriptor.QUERY_KEYS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWRelationalClassDescriptor) this.subject).allQueryKeys();
			}
		};
	}
	
	private ListCellRenderer buildAbstractQueryListCellRenderer(){
		return new AdaptableListCellRenderer(new QueryKeyCellRendererAdapter(resourceRepository()));
	}
	
	private ListSelectionListener buildListSelectionListener(final AddRemoveListPanel queryKeyListPanel) {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				
				MWQueryKey qKey = (MWQueryKey)queryKeyListPanel.getSelectedValue();
				selectedQueryKeyHolder.setValue(qKey);
				MWRelationalClassDescriptor descriptor = ((MWRelationalClassDescriptor) getSelectionHolder().getValue());							
				
				if (qKey == null || descriptor == null) {
					fieldChooser.setEnabled(false);
				}
				else {
					fieldChooser.setEnabled(! descriptor.isAggregateDescriptor());
					fieldChooser.setChoosable(! qKey.isAutoGenerated());
				}
			}
		};
	}
	
	private ValueModel buildFieldChooserEnablerModel(final AddRemoveListPanel queryKeyListPanel) {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				if ((MWQueryKey) queryKeyListPanel.getSelectedValue() == null) {
					return Boolean.FALSE;
}				return Boolean.valueOf(!((MWRelationalClassDescriptor) this.subject).isAggregateDescriptor());
			}
		};
	}
    
   public void selectQueryKey(MWQueryKey queryKey) {
       this.queryKeyListPanel.setSelectedValue(queryKey, true);
}
}
