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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ColumnCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.TableCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalMappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


final class TableDescriptorSequencingPanel 
	extends AbstractPanel 
{
	private PropertyValueModel relationalDescriptorHolder;
	
	private PropertyValueModel useSequencingHolder;
	
	private PropertyValueModel sequenceTableHolder;
	
	
	TableDescriptorSequencingPanel(PropertyValueModel relationalDescriptorHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.relationalDescriptorHolder = relationalDescriptorHolder;
		this.useSequencingHolder = this.buildUseSequencingHolder();
		this.sequenceTableHolder = this.buildSequenceTableHolder();
		initializeLayout();
	}
	
	private PropertyValueModel buildUseSequencingHolder() {
		return new PropertyAspectAdapter(this.relationalDescriptorHolder, MWTableDescriptor.USES_SEQUENCING_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWTableDescriptor) this.subject).usesSequencing());
			}

			protected void setValueOnSubject(Object value) {
				((MWTableDescriptor) this.subject).setUsesSequencing(((Boolean) value).booleanValue());
			}
		};
	}
	
	private PropertyValueModel buildSequenceTableHolder() {
		PropertyValueModel propertyValueModel = new PropertyAspectAdapter(this.relationalDescriptorHolder, MWTableDescriptor.SEQUENCE_NUMBER_TABLE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTableDescriptor) this.subject).getSequenceNumberTable();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWTableDescriptor) this.subject).setSequenceNumberTable((MWTable) value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWTable.QUALIFIED_NAME_PROPERTY);
	}
	
	private void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		GroupBox groupBox = new GroupBox(buildUseSequencingCheckBox(), buildSequencingPanel());

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
	
		add(groupBox, constraints);
	}
	
	protected JPanel buildSequencingPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		Vector components = new Vector();

		Pane panel = new Pane(new GridBagLayout());

		JComponent sequenceNameWidgets = buildLabeledTextField(
			"name",
			this.buildSequencingNameTextFieldDocument(buildSequencingNameHolder())
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
	
		panel.add(sequenceNameWidgets, constraints);
		components.add(sequenceNameWidgets);

		// Sequence Table widgets
		JComponent sequenceTableWidgets = buildLabeledComponent(
			"table",
			this.buildSequenceTableListChooser()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);
	
		panel.add(sequenceTableWidgets, constraints);
		components.add(sequenceTableWidgets);

		// Sequence Field widgets
		JComponent sequenceFieldWidgets = buildLabeledComponent(
			"field*",
			this.buildSequenceColumnListChooser()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);
	
		panel.add(sequenceFieldWidgets, constraints);
		components.add(sequenceFieldWidgets);

		new ComponentEnabler(this.useSequencingHolder, components);
		return panel;
	}

	// *********** use sequencing ***********
	
	private JCheckBox buildUseSequencingCheckBox() {
		return buildCheckBox("useSequencing", new CheckBoxModelAdapter(this.useSequencingHolder));
	}
	

	// *********** sequencing name ***********
	
	private PropertyValueModel buildSequencingNameHolder() {
		return new PropertyAspectAdapter(this.relationalDescriptorHolder, MWTableDescriptor.SEQUENCE_NUMBER_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTableDescriptor) this.subject).getSequenceNumberName();
			}

			protected void setValueOnSubject(Object value) {
				((MWTableDescriptor) this.subject).setSequenceNumberName((String) value);
			}
		};
	}

	private Document buildSequencingNameTextFieldDocument(PropertyValueModel sequencingNameHolder) {
		return new DocumentAdapter(sequencingNameHolder);
	}
	
	
	// **************** sequence table ****************************************
	
	private ListChooser buildSequenceTableListChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildTableComboBoxModel(),
				this.getWorkbenchContextHolder(),
                RelationalProjectComponentFactory.buildTableNodeSelector(getWorkbenchContextHolder()),
				this.buildSequenceTableChooserDialogBuilder()
			);
		listChooser.setRenderer(this.buildTableListRenderer());
		
		return listChooser;
	}
	
		
	private CachingComboBoxModel buildTableComboBoxModel() {
		return new ExtendedComboBoxModel( 
				new IndirectComboBoxModel(this.sequenceTableHolder, this.relationalDescriptorHolder) {
					protected ListIterator listValueFromSubject(Object subject) {
						return orderedTableChoices((MWRelationalDescriptor) subject);
					}
				}
		);
	}
	
	ListIterator orderedTableChoices(MWRelationalDescriptor descriptor) {
		return CollectionTools.sort(descriptor.associatedTables(), buildTableComparator()).listIterator();
	}

	
	private Comparator buildTableComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((MWTable) o1).getName().compareTo(((MWTable) o2).getName());
			}
		};
	}
	
	private DefaultListChooserDialog.Builder buildSequenceTableChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("SEQUENCE_TABLE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("SEQUENCE_TABLE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(this.buildTableStringConverter());
		return builder;
	}
	
	private StringConverter buildTableStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWTable) o).getName();
			}
		};
	}
	
	private ListCellRenderer buildTableListRenderer() {
		return new AdaptableListCellRenderer(new TableCellRendererAdapter(this.resourceRepository()));
	}
	
	
	// **************** sequence column ****************************************
	
	private ListChooser buildSequenceColumnListChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildExtendedSequenceColumnComboBoxModel(),
				this.getWorkbenchContextHolder(),
                RelationalMappingComponentFactory.buildColumnNodeSelector(getWorkbenchContextHolder()),
				this.buildSequenceColumnChooserDialogBuilder()
			);
		listChooser.setRenderer(buildColumnListRenderer());	
		
		return listChooser;
	}
	
	private ComboBoxModel buildExtendedSequenceColumnComboBoxModel() {
		return new ExtendedComboBoxModel(this.buildSequenceColumnComboBoxModel());
	}
	
	private ComboBoxModel buildSequenceColumnComboBoxModel() {
		return new ComboBoxModelAdapter(this.buildSortedColumnsListHolder(), this.buildSequenceColumnAdapter());
	}
	
	private ListValueModel buildSortedColumnsListHolder() {
		return new SortedListValueModelAdapter(this.buildUpdatingColumnsListHolder(), this.buildColumnComparator());
	}
	
	private ListValueModel buildUpdatingColumnsListHolder() {
		return new ItemPropertyListValueModelAdapter(this.buildColumnsAdapter(), MWColumn.NAME_PROPERTY);
	}
	
	private CollectionValueModel buildColumnsAdapter() {
		return new CollectionAspectAdapter(this.sequenceTableHolder, MWTable.COLUMNS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWTable) this.subject).columns();
			}
			
			protected int sizeFromSubject() {
				return ((MWTable) this.subject).columnsSize();
			}
		};
	}
	
	private Comparator buildColumnComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((MWColumn) o1).getName().compareTo(((MWColumn) o2).getName());
			}
		};
	}
	
	private PropertyValueModel buildSequenceColumnAdapter() {
		PropertyValueModel propertyValueModel = new PropertyAspectAdapter(this.relationalDescriptorHolder, MWTableDescriptor.SEQUENCE_NUMBER_COLUMN_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTableDescriptor) this.subject).getSequenceNumberColumn();
			}

			protected void setValueOnSubject(Object value) {
				((MWTableDescriptor) this.subject).setSequenceNumberColumn((MWColumn) value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWColumn.QUALIFIED_NAME_PROPERTY, MWColumn.DATABASE_TYPE_PROPERTY);
	}
	
	private DefaultListChooserDialog.Builder buildSequenceColumnChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("SEQUENCE_FIELD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("SEQUENCE_FIELD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(this.buildColumnStringConverter());
		return builder;
	}


	private StringConverter buildColumnStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWColumn) o).getName();
			}
		};
	}
	
	private ListCellRenderer buildColumnListRenderer() {
		return new AdaptableListCellRenderer(new ColumnCellRendererAdapter(this.resourceRepository(), false));
	}
}
