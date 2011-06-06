/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWDescriptorMultiTableInfoPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWSecondaryTableHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ReferenceCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.ColumnPairsPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.LabelPanel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


/**
 * @version 10.1.3
 */
public class MultiTableInfoPropertiesPage extends ScrollablePropertiesPage {
	
    public static final int EDITOR_WEIGHT = 10;

    private PropertyValueModel selectedSecondaryTableHolder;
	private PropertyValueModel primaryKeysHaveSameNameValueHolder;
	private ListChooser referenceListChooser; 
	private PropertyValueModel multiTableInfoPolicyHolder;
	private PropertyValueModel primaryTableHolder;
	private PropertyValueModel selectedTableReferenceSelectionHolder;
	 
	
	public MultiTableInfoPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}
	
	protected void initialize(PropertyValueModel selectionNodeHolder) {
		super.initialize(selectionNodeHolder);
		this.multiTableInfoPolicyHolder = buildMultiTableInfoPolicyHolder();
		this.primaryTableHolder = buildPrimaryTableHolder();
		this.selectedSecondaryTableHolder = new SimplePropertyValueModel();
		this.primaryKeysHaveSameNameValueHolder =  buildPrimaryKeysHaveSameNameValueHolder();
		this.selectedTableReferenceSelectionHolder = buildSelectedTableReferenceHolder();
	}

	protected String getHelpTopicId() {
		return "descriptor.multitable";
	}
	
	protected Component buildPage() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints constraints = new GridBagConstraints();
				
		JPanel primaryTablePanel = buildPrimaryTablePanel();
		
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 10, 0);
		mainPanel.add(primaryTablePanel, constraints);
		
		JComponent multiTableSelectionPanel = buildMultiTableSelectionPanel();
		
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);
		mainPanel.add(multiTableSelectionPanel, constraints);
		
		JPanel tableAssociationsPanel = buildTableAssociationPanel();
		
		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 0);
		mainPanel.add(tableAssociationsPanel, constraints);

		addHelpTopicId(mainPanel, getHelpTopicId());
		
		return mainPanel;
	}
	
	private JPanel buildPrimaryTablePanel() {
		JPanel primaryTablePanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the label
		JLabel tableLabel = buildLabel("MULTI_TABLE_INFO_POLICY_PRIMARY_TABLE");

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 0;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		primaryTablePanel.add(tableLabel, constraints);

		// add the associated table label
		LabelPanel primaryTableLabelPanel = new LabelPanel(this.buildLabelPanelTextAdapter());

		Insets insets = UIManager.getInsets("TextField.margin");
		if (insets == null) {
			insets = UIManager.getInsets("TextField.contentMargins");
		}
		primaryTableLabelPanel.setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TextField.border"), BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right)));

		Dimension size = tableLabel.getPreferredSize();
		insets = primaryTableLabelPanel.getBorder().getBorderInsets(primaryTableLabelPanel);
		primaryTableLabelPanel.setPreferredSize(new Dimension(1, size.height + insets.top + insets.bottom));

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 0);

		primaryTablePanel.add(primaryTableLabelPanel, constraints);

		return primaryTablePanel;
	}
	
	protected PropertyValueModel buildLabelPanelTextAdapter() {
		return new PropertyAspectAdapter(this.getPrimaryTableHolder(), MWTable.QUALIFIED_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTable) this.subject).getName();
			}
		};
	}

	private JComponent buildMultiTableSelectionPanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel multiTableSelectionPanel = new JPanel(new GridBagLayout());

		// Label
		JLabel label = buildLabel("MULTI_TABLE_INFO_POLICY_ADDITIONAL_TABLES");

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		
		multiTableSelectionPanel.add(label, constraints);

		// Add Remove list
		AddRemoveListPanel tableAddRemoveListPanel = new AddRemoveListPanel(
			getApplicationContext(), 
			new TableSelectionAdapter(),
			buildSortedSecondaryTablesAdapter(),
            AddRemovePanel.BOTTOM,
            resourceRepository().getString("MULTI_TABLE_INFO_POLICY_ADDITIONAL_TABLES"),
            buildTableNodeSelector()
		);
		tableAddRemoveListPanel.setCellRenderer(new TableCellRenderer());
		tableAddRemoveListPanel.getList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableAddRemoveListPanel.addListSelectionListener(new MultiTableListSelectionHandler(
														tableAddRemoveListPanel, this.selectedSecondaryTableHolder));

		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(1, 0, 0, 0);
		
		multiTableSelectionPanel.add(tableAddRemoveListPanel, constraints);
		label.setLabelFor(tableAddRemoveListPanel);
		
		// Need enable the table selection panel based on the existence of the 
		// primary table
		buildMultiTableSelectionPanelEnabler(new Component[] { tableAddRemoveListPanel });
		
		return multiTableSelectionPanel;
	}
	
	private JPanel buildTableAssociationPanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel tableAssociationPanel = new JPanel(new GridBagLayout());
		tableAssociationPanel.setBorder(BorderFactory.createCompoundBorder(buildTitledBorder("MULTI_TABLE_INFO_POLICY_ASSOCIATION_TO_PRIMARY"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));

		// Primary Keys Have the Same Name
		JRadioButton samePKButton = buildPrimaryKeysHaveSameNameButton();

		// Reference widgets
		JPanel referenceChooserPanel = buildReferenceSelectionPanel();
		GroupBox.fillVertical(referenceChooserPanel);

		JRadioButton useReferenceButton = buildUseReferenceButton();

		// Add the widgets
		GroupBox groupBox = new GroupBox(samePKButton, useReferenceButton, referenceChooserPanel);
		
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);
		
		tableAssociationPanel.add(groupBox, constraints);		

		// Set the ComponentEnabler
		new ComponentEnabler(buildReferencePanelEnabler(), referenceChooserPanel.getComponents());
		new ComponentEnabler(buildPrimaryKeyButtonEnabler(), new Component[] { samePKButton, useReferenceButton });

		return tableAssociationPanel;
	}
	
	private JPanel buildReferenceSelectionPanel() {
		JPanel referenceSelectionPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		JLabel referenceChooserLabel = buildLabel("MULTI_TABLE_INFO_POLICY_TABLE_REFERENCE");
		
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		
		referenceSelectionPanel.add(referenceChooserLabel, constraints);
		
		this.referenceListChooser = buildSelectedTableReferenceListChooser(); 

		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(1, 0, 0, 0);
		
		referenceSelectionPanel.add(this.referenceListChooser, constraints);
		referenceChooserLabel.setLabelFor(this.referenceListChooser);
		
		Spacer spacer = new Spacer(); 

		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(1, 5, 0, 0);
		
		referenceSelectionPanel.add(spacer, constraints);
		addAlignRight(spacer);

		ColumnPairsPanel associatonsPanel = new ColumnPairsPanel(getWorkbenchContextHolder(), this.selectedTableReferenceSelectionHolder); 

		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);
	
		referenceSelectionPanel.add(associatonsPanel, constraints);
		addPaneForAlignment(associatonsPanel);
		
		return referenceSelectionPanel;
	}

	PropertyValueModel getMultiTableInfoPolicyHolder() {
		return this.multiTableInfoPolicyHolder;
	}
	
	private PropertyValueModel buildMultiTableInfoPolicyHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(),
				MWTableDescriptor.MULTI_TABLE_INFO_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				MWDescriptorPolicy policy = ((MWTableDescriptor) this.subject).getMultiTableInfoPolicy();
				return policy.isActive() ? policy : null;
			}
		};
	}
	
	private PropertyValueModel buildPrimaryTableHolder()  {
		return 
			new PropertyAspectAdapter(getSelectionHolder(), MWTableDescriptor.PRIMARY_TABLE_PROPERTY) {
				protected Object getValueFromSubject() {
					return ((MWTableDescriptor) this.subject).getPrimaryTable();
				}
			};
	}
	
	private PropertyValueModel getPrimaryTableHolder() {
		return this.primaryTableHolder;
	}
	
	private PropertyValueModel buildPrimaryKeysHaveSameNameValueHolder() {
		return new PropertyAspectAdapter(getSelectedSecondaryTableAssocationHolder(), MWSecondaryTableHolder.PRIMARY_KEYS_HAVE_SAME_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWSecondaryTableHolder) this.subject).primaryKeysHaveSameName());
			}
			
			protected void setValueOnSubject(Object value) {
				((MWSecondaryTableHolder) this.subject).setPrimaryKeysHaveSameName(((Boolean) value).booleanValue());
			}
		};
	}	
	
	private PropertyValueModel getPrimaryKeysHaveSameNameValueHolder() {
		return this.primaryKeysHaveSameNameValueHolder;
	}
	
	private PropertyValueModel buildSelectedTableReferenceHolder() {
		return new PropertyAspectAdapter(this.selectedSecondaryTableHolder, MWSecondaryTableHolder.REFERENCE_PROPERTY) {
			protected Object getValueFromSubject() {
				if (((MWSecondaryTableHolder) this.subject).primaryKeysHaveSameName()) {
					return null;
				}
				return ((MWSecondaryTableHolder) this.subject).getReference();
			}
			protected void setValueOnSubject(Object value) {
				((MWSecondaryTableHolder) this.subject).setReference((MWReference) value);
			}
		};
	}
	
	private PropertyValueModel getSelectedSecondaryTableAssocationHolder(){
		return this.selectedSecondaryTableHolder;
	}

	private ListValueModel buildSortedSecondaryTablesAdapter() {
		return new SortedListValueModelAdapter(this.buildSecondaryTableNamesAdapter());
	}
	
	private ListValueModel buildSecondaryTableNamesAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildSecondaryTablesAdapter(), MWTable.QUALIFIED_NAME_PROPERTY);
	}

	private ListValueModel buildSecondaryTablesAdapter() {
		return new TransformationListValueModelAdapter(this.buildSecondaryTableHoldersAdapter()) {
			protected Object transformItem(Object item) {
				return ((MWSecondaryTableHolder) item).getTable();
			}
		};
	}
	
	private CollectionValueModel buildSecondaryTableHoldersAdapter() {
		return new CollectionAspectAdapter(this.getMultiTableInfoPolicyHolder(), MWDescriptorMultiTableInfoPolicy.SECONDARY_TABLE_HOLDERS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWDescriptorMultiTableInfoPolicy) this.subject).secondaryTableHolders();
			}
		};			
	}

    private NodeSelector buildTableNodeSelector() {
        return new NodeSelector() {       
            public void selectNodeFor(Object item) {
                RelationalProjectNode projectNode = (RelationalProjectNode) navigatorSelectionModel().getSelectedProjectNodes()[0];
                projectNode.selectTableNodeFor((MWTable) item, navigatorSelectionModel());
            }
        };
    }

	MWTableDescriptor getDescriptor() {
		return (MWTableDescriptor)getSelectionHolder().getValue();
	}
		
	void removeAssociatedTables(Collection associatedTables) {
		for (Iterator iter = associatedTables.iterator(); iter.hasNext();) {
			getDescriptor().removeAssociatedTable((MWTable) iter.next());
		}
	}

	private ListChooser buildSelectedTableReferenceListChooser() {
		ListChooser listChooser = new ListChooser(buildSelectedTableReferenceComboboxModel());
		listChooser.setRenderer(buildReferenceCellRenderer());
		return listChooser;
	}
	
	private CachingComboBoxModel buildSelectedTableReferenceComboboxModel() {
		return new IndirectComboBoxModel(this.selectedTableReferenceSelectionHolder, this.selectedSecondaryTableHolder) {
			protected ListIterator listValueFromSubject(Object subject) {
				return MultiTableInfoPropertiesPage.this.orderedReferenceChoices(((MWSecondaryTableHolder) subject).getTable());
			}
		};
	}

	ListIterator orderedReferenceChoices(MWTable mwTable) {
		return CollectionTools.sort(mwTable.referencesBetween((MWTable) this.primaryTableHolder.getValue())).listIterator();
	}
	
	private ValueModel buildPrimaryKeyButtonEnabler() {
		return new TransformationPropertyValueModel(this.selectedSecondaryTableHolder) {
			protected Object transform(Object value) {
				return Boolean.valueOf(value != null);
			}
		};
	}
	private ValueModel buildReferencePanelEnabler() {
		return new TransformationPropertyValueModel(this.primaryKeysHaveSameNameValueHolder) {
			protected Object transform(Object value) {
				if (value == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(!((Boolean) value).booleanValue());
			}
		};
	}	
	
	private ComponentEnabler buildMultiTableSelectionPanelEnabler(Component[] components) {
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(getPrimaryTableHolder()) {
			protected Object transform(Object value) {	
				return Boolean.valueOf(value != null);
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}
	
	private JRadioButton buildPrimaryKeysHaveSameNameButton()
	{
		JRadioButton radioButton = 
			buildRadioButton(
					"MULTI_TABLE_INFO_POLICY_PRIMARY_KEYS_HAVE",
					new RadioButtonModelAdapter(
							getPrimaryKeysHaveSameNameValueHolder(), 
							Boolean.TRUE)
			);
		return radioButton;
	}
	
	private JRadioButton buildUseReferenceButton()
	{
		JRadioButton radioButton = 
			buildRadioButton(
					"MULTI_TABLE_INFO_POLICY_REFERENCE",
					new RadioButtonModelAdapter(
							getPrimaryKeysHaveSameNameValueHolder(), 
							Boolean.FALSE)
			);
		return radioButton;
	}

	//	 -----------------------------------------
	

	/**
	 * Handles the remove and add actions for the mutli-table list.
	 */
	private class TableSelectionAdapter implements AddRemoveListPanel.Adapter
	{
		public void addNewItem(ObjectListSelectionModel listSelectionModel)
		{

			DefaultListChooserDialog dialog = buildAddTableDialog();
			dialog.show();
			if (dialog.wasConfirmed()) {
				MWTable selection = (MWTable) dialog.selection();
				if (selection != null) {
					getDescriptor().addAssociatedTable(selection);
					listSelectionModel.setSelectedValue(((MWDescriptorMultiTableInfoPolicy) getDescriptor().getMultiTableInfoPolicy()).secondaryTableHolderFor(selection));
				}		
			}
		}

		public void removeSelectedItems(ObjectListSelectionModel listSelectionModel)
		{
			removeAssociatedTables(CollectionTools.collection(listSelectionModel.getSelectedValues()));
		}
	}
	
	/**
	 * List Label for a MWTable.
	 */
	private class TableCellRenderer extends SimpleListCellRenderer
	{
		protected String buildText(Object value)
		{
			return ((MWTable)value).getName();
		}
	
	}

	
	private ListCellRenderer buildReferenceCellRenderer() {
		return new AdaptableListCellRenderer(new ReferenceCellRendererAdapter(resourceRepository()));
	}
	
	/**
	 * Handles updating the selected values from the AddRemoveListPanel to the underlying ValueModel.
	 */
	private class MultiTableListSelectionHandler implements ListSelectionListener
	{
		private AddRemoveListPanel listPanel;
		private PropertyValueModel selectionModel;
		
		private MultiTableListSelectionHandler(AddRemoveListPanel listPanel, PropertyValueModel selectionModel)
		{
			this.selectionModel = selectionModel;
			this.listPanel = listPanel;
		}
		
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting())
			{
				return;
			}

			if (this.listPanel.getSelectedValues().length > 1)
			{
				this.selectionModel.setValue(null);
			}
			else
			{
				MWTable selectedTable = (MWTable) this.listPanel.getSelectedValue();
				MWDescriptorMultiTableInfoPolicy multiTablePolicy = ((MWDescriptorMultiTableInfoPolicy) getMultiTableInfoPolicyHolder().getValue());
				if (multiTablePolicy != null) {
					this.selectionModel.setValue(multiTablePolicy.secondaryTableHolderFor(selectedTable));
				}
				else {
					this.selectionModel.setValue(null);				
				}
			}
		}
	}
	
	
	// ************* version locking field ***********


	DefaultListChooserDialog buildAddTableDialog() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("MULTI_TABLE_INFO_POLICY_ADD_TABLE_DIALOG.title");
		builder.setListBoxLabelKey("MULTI_TABLE_INFO_POLICY_ADD_TABLE_DIALOG.listLabel");
		builder.setHelpTopicId("dialog.selectTable");
		builder.setStringConverter(buildTableStringConverter());
		builder.setCompleteList(CollectionTools.array(buildTables(), new MWTable[0]));
		return new DefaultListChooserDialog(getWorkbenchContext(), builder);
	}	
	
	private StringConverter buildTableStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return ((MWTable) o).getName();
			}
		};
	}
	
	private Iterator buildTables() {		
		// need to remove all existing associated tables from the list
		List allTables = CollectionTools.list(((MWTableDescriptor)selection()).getDatabase().tables());
		if (getDescriptor() != null)
		{
			allTables.removeAll(CollectionTools.list(getDescriptor().associatedTables()));
		}
		return CollectionTools.sort(allTables).listIterator();
	}

}
