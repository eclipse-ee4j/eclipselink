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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.TypeDeclarationCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;

import org.eclipse.persistence.exceptions.ConversionException;

final class ObjectTypeConverterPanel
	extends AbstractSubjectPanel 
{
	ObjectTypeConverterPanel(PropertyValueModel objectTypeConverterHolder, WorkbenchContextHolder contextHolder) {
		super(objectTypeConverterHolder, contextHolder);
	}
	
	MWObjectTypeConverter getObjectTypeConverter() {
		return (MWObjectTypeConverter) subject();
	}
	
	protected void initializeLayout() {		
		GridBagConstraints constraints = new GridBagConstraints();

		// Data Type widgets
		JComponent dataTypeWidget = buildLabeledComponent(
			"DATA_TYPE_LABEL",
			this.buildDataTypeChooser()
		);
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		this.add(dataTypeWidget, constraints);

		// Attribute Type widgets
		JComponent attributeTypeWidget = buildLabeledComponent(
			"ATTRIBUTE_TYPE_LABEL",
			this.buildAttributeTypeChooser()
		);
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);
		this.add(attributeTypeWidget, constraints);
		
		// Conversion values label
		JLabel conversionValuesLabel = 
			this.buildConversionValuesLabel();
		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(10, 0, 0, 0);
		this.add(conversionValuesLabel, constraints);
		this.addAlignLeft(conversionValuesLabel);
		
		// value pairs
		AbstractPanel valuePairsPanel = this.buildConversionValuesPanel();
		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(2, 0, 0, 0);
		this.add(valuePairsPanel, constraints);
		this.addPaneForAlignment(valuePairsPanel);
		
		addHelpTopicId(this, helpTopicId());	
	}
	
	
	// ************* data type ***************
	
	private ListChooser buildDataTypeChooser() {
		ListChooser chooser = 
			new DefaultListChooser(
				this.buildDataTypeComboBoxModel(), 
				this.getWorkbenchContextHolder(),
				this.buildDataTypeChooserDialogBuilder()
			);
		chooser.setRenderer(buildMWClassListCellRenderer());
		return chooser;
	}

	private DefaultListChooserDialog.Builder buildDataTypeChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("DATA_TYPE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("DATA_TYPE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildMWClassStringConverter());
		return builder;
	}

	private ComboBoxModel buildDataTypeComboBoxModel() {
		return new ComboBoxModelAdapter(buildTypesCollectionModel(), buildDataTypeHolder()); 
	}
	
	private PropertyValueModel buildDataTypeHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWTypeConverter.DATA_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWObjectTypeConverter) this.subject).getDataType();
			}
			protected void setValueOnSubject(Object value) {
				((MWObjectTypeConverter) this.subject).setDataType((MWTypeDeclaration) value);
			}
		};
	}

	private CollectionValueModel buildTypesCollectionModel() {
		return new CollectionAspectAdapter(getSubjectHolder()) {
			protected Iterator getValueFromSubject() {
				return ((MWConverter) this.subject).buildBasicTypes().iterator();
			}
		};
	}
	
	private StringConverter buildMWClassStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWTypeDeclaration) o).displayStringWithPackage();
			}
		};
	}
	
	private ListCellRenderer buildMWClassListCellRenderer() {
		return new AdaptableListCellRenderer(new TypeDeclarationCellRendererAdapter(resourceRepository()));
	}

	
	// ************* attribute type ***************

	private ListChooser buildAttributeTypeChooser() {
		ListChooser chooser = 
			new DefaultListChooser(
				this.buildAttributeTypeComboBoxModel(), 
				this.getWorkbenchContextHolder(),
				this.buildAttributeTypeChooserDialogBuilder()
			);
		chooser.setRenderer(buildMWClassListCellRenderer());
		return chooser;
	}

	private DefaultListChooserDialog.Builder buildAttributeTypeChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("ATTRIBUTE_TYPE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("ATTRIBUTE_TYPE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildMWClassStringConverter());
		return builder;
	}

	private ComboBoxModel buildAttributeTypeComboBoxModel() {
		return new ComboBoxModelAdapter(buildTypesCollectionModel(), buildAttributeTypeHolder()); 
	}
	
	private PropertyValueModel buildAttributeTypeHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWTypeConverter.ATTRIBUTE_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWObjectTypeConverter) this.subject).getAttributeType();
			}
			protected void setValueOnSubject(Object value) {
				((MWObjectTypeConverter) this.subject).setAttributeType((MWTypeDeclaration) value);
			}

		};
	}
	
	private JLabel buildConversionValuesLabel() {
		JLabel label = this.buildLabel("CONVERSION_VALUES_TABLE_LABEL");
		return label;
	}
	
	private AbstractPanel buildConversionValuesPanel() {
		return new ConversionValuesPanel(this.getSubjectHolder(), this.getWorkbenchContextHolder());
	}
		
	
	
	// **************** value pairs ****************
	
	protected String helpTopicId() {
		return "mapping.converter.objectType";
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		for (int i = this.getComponentCount() - 1; i >= 0; i -- ) {
			this.getComponent(i).setEnabled(enabled);
		}
	}
	
	
	private class ConversionValuesPanel
		extends AbstractSubjectPanel
	{
		ObjectListSelectionModel valuePairsTableSelectionModel;
		
		private Action removeAction;
		private Action editAction;
		
		private ConversionValuesPanel(ValueModel objectTypeConverterHolder, WorkbenchContextHolder contextHolder) {
			super(objectTypeConverterHolder, contextHolder);
		}
		
		protected void initializeLayout() {
			this.setLayout(new GridBagLayout());
			
			GridBagConstraints constraints = new GridBagConstraints();
			
			// add button
			JButton addButton = this.buildAddConversionValueButton();
			constraints.gridx		= 1;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(0, 0, 0, 0);
			this.add(addButton, constraints);
			this.addAlignRight(addButton);
			
			// edit button
			JButton editButton = this.buildEditConversionValueButton();
			constraints.gridx		= 1;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(5, 0, 0, 0);
			this.add(editButton, constraints);
			this.addAlignRight(editButton);
			
			// remove button
			JButton removeButton = this.buildRemoveConversionValueButton();
			constraints.gridx		= 1;
			constraints.gridy		= 2;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.PAGE_START;
			constraints.insets		= new Insets(5, 0, 0, 0);
			this.add(removeButton, constraints);
			this.addAlignRight(removeButton);
			
			// value pairs
			JTable valuePairsTable = this.buildValuePairsTable();
			JScrollPane scrollPane = new JScrollPane(valuePairsTable);
			scrollPane.setPreferredSize(new Dimension(10, 10));
			scrollPane.getViewport().setBackground(valuePairsTable.getBackground());
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 3;
			constraints.weightx		= 1;
			constraints.weighty		= 1;
			constraints.fill		= GridBagConstraints.BOTH;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(0, 0, 0, 5);
			this.add(scrollPane, constraints);
		}
		
		private JButton buildAddConversionValueButton() {
			Action addAction = this.buildAddConversionValueAction();
			this.addPropertyChangeListener("enabled" /* stupid hard-coded property name */, this.buildAddButtonEnabler(addAction));
			return new JButton(addAction);
		}
		
		private Action buildAddConversionValueAction() {
			return new AbstractAction(this.resourceRepository().getString("ADD_VALUE_PAIRS_BUTTON")) {
				public void actionPerformed(ActionEvent e) {
					ConversionValueDialog.promptToAddConversionValuePair(
						ObjectTypeConverterPanel.this.getObjectTypeConverter(), 
						ObjectTypeConverterPanel.this.getWorkbenchContext()
					);
				}
			};
		}
		
		/** Sets the addButton enabled/disabled based on this panels enabled/disabled state */
		private PropertyChangeListener buildAddButtonEnabler(final Action action) {
			return new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					action.setEnabled(((Boolean) evt.getNewValue()).booleanValue());
				}
			};
		}
		
		private JButton buildEditConversionValueButton() {
			this.editAction = this.buildEditConversionValueAction();
			this.editAction.setEnabled(false);
			return new JButton(this.editAction);
		}
			
		private Action buildEditConversionValueAction() {
			return new AbstractAction(this.resourceRepository().getString("EDIT_VALUE_PAIRS_BUTTON")) {
				public void actionPerformed(ActionEvent e) {
					MWObjectTypeConverter.ValuePair selectedValuePair = 
						(MWObjectTypeConverter.ValuePair) ConversionValuesPanel.this.valuePairsTableSelectionModel.getMinSelectedValue();
					ConversionValueDialog.promptToEditConversionValuePair(
						selectedValuePair, 
						ObjectTypeConverterPanel.this.getWorkbenchContext()
					);
				}
			};
		}
		
		private JButton buildRemoveConversionValueButton() {
			this.removeAction = this.buildRemoveConversionValueAction();
			this.removeAction.setEnabled(false);
			return new JButton(this.removeAction);
		}
		
		private Action buildRemoveConversionValueAction() {
			return new AbstractAction(this.resourceRepository().getString("REMOVE_VALUE_PAIRS_BUTTON")) {
				public void actionPerformed(ActionEvent e) {
					for (
						Iterator stream = CollectionTools.iterator(ConversionValuesPanel.this.valuePairsTableSelectionModel.getSelectedValues()); 
						stream.hasNext(); 
					) {
						MWObjectTypeConverter.ValuePair selectedValuePair = (MWObjectTypeConverter.ValuePair) stream.next();
						ObjectTypeConverterPanel.this.getObjectTypeConverter().removeValuePair(selectedValuePair);
					}
				}
			};
		}
		
		private JTable buildValuePairsTable() {
			CollectionValueModel valuePairsHolder = buildValuePairsHolder();
			JTable table = SwingComponentFactory.buildTable(
				this.buildValuePairsTableModel(valuePairsHolder), 
				this.buildValuePairsSelectionModel(valuePairsHolder)
			);
			
			this.addPropertyChangeListener("enabled" /* stupid hard-coded property name */, this.buildTableEnabler(table));
			
			return table;
		}
		
		private TableModel buildValuePairsTableModel(CollectionValueModel valuePairsHolder) {
			return new TableModelAdapter(valuePairsHolder, this.buildValuePairsColumnModelAdapter());
		}
		
		private CollectionValueModel buildValuePairsHolder() {
			return new CollectionAspectAdapter(getSubjectHolder(), MWObjectTypeConverter.VALUE_PAIRS_COLLECTION) {
				protected Iterator getValueFromSubject() {
					return ((MWObjectTypeConverter) this.subject).valuePairs();
				}		
				protected int sizeFromSubject() {
					return ((MWObjectTypeConverter) this.subject).valuePairsSize();
				}
			};
		}
		
		private ColumnAdapter buildValuePairsColumnModelAdapter() {
			return new ValuePairsColumnAdapter(resourceRepository());
		}
		
	
		private ListSelectionModel buildValuePairsSelectionModel(CollectionValueModel valuePairsHolder) {
			this.valuePairsTableSelectionModel = new ObjectListSelectionModel(new ListModelAdapter(valuePairsHolder));
			this.valuePairsTableSelectionModel.addListSelectionListener(this.buildValuePairsListSelectionListener(this.valuePairsTableSelectionModel));
			return this.valuePairsTableSelectionModel;
		}
	
		private ListSelectionListener buildValuePairsListSelectionListener(final ListSelectionModel selectionModel) {
			return new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting()) {
						updateValuePairsActions(selectionModel);
					}
				}
			};
		}
		
		void updateValuePairsActions(ListSelectionModel selectionModel) {
			this.removeAction.setEnabled( ! selectionModel.isSelectionEmpty());
			this.editAction.setEnabled( ! selectionModel.isSelectionEmpty()
										 && selectionModel.getMinSelectionIndex() == selectionModel.getMaxSelectionIndex());
		}
		
		/** Sets the table enabled/disabled based on this panels enabled/disabled state */
		private PropertyChangeListener buildTableEnabler(final JTable table) {
			return new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					table.setEnabled(((Boolean) evt.getNewValue()).booleanValue());
				}
			};
		}
	}
	
	
	private static class ConversionValueDialog 
		extends AbstractDialog
	{
		private MWObjectTypeConverter converter;
		
		private MWObjectTypeConverter.ValuePair valuePair;
		
		private int mode;
			private final static int ADD_MODE 	= 0;
			private final static int EDIT_MODE 	= 1;
		
		private RegexpDocument dataValueDocument;
		
		private RegexpDocument attributeValueDocument;
		
		private Component dataValueTextField;
		
		
		// **************** Member Classes ************************************
		
		private class ConversionValueDocumentHandler 
			implements DocumentListener
		{
			public void changedUpdate(DocumentEvent de) {
				updateOKButton();
			}
			public void insertUpdate(DocumentEvent de) {
				updateOKButton();
			}
			public void removeUpdate(DocumentEvent de) {
				updateOKButton();
			}
		}
		
		
		// **************** Static Creators ***********************************
		
		static void promptToAddConversionValuePair(MWObjectTypeConverter converter, WorkbenchContext context) {
			new ConversionValueDialog(converter, null, ADD_MODE, context).show();
		}
		
		static void promptToEditConversionValuePair(MWObjectTypeConverter.ValuePair valuePair, WorkbenchContext context) {
			new ConversionValueDialog(valuePair.getObjectTypeConverter(), valuePair, EDIT_MODE, context).show();
		}
		
		
		// **************** Constructors **************************************
		
		private ConversionValueDialog(MWObjectTypeConverter objectTypeConverter, 
									  MWObjectTypeConverter.ValuePair valuePair,
									  int mode, WorkbenchContext context) {
			super(context);
			this.initialize(objectTypeConverter, valuePair, mode);
		}
		
		
		// **************** Initialization ************************************
		
		private void initialize(MWObjectTypeConverter objectTypeConverter, 
								MWObjectTypeConverter.ValuePair vp,
								int m) {
			this.converter = objectTypeConverter;
			this.valuePair = vp;
			this.mode = m;
			this.dataValueDocument = this.buildDataValueDocument();
			this.attributeValueDocument = this.buildAttributeValueDocument();
		}
		
		protected void initialize() {
			super.initialize();
			getOKAction().setEnabled(false);
		}

		private String addOrEditString() {
			if (this.mode == ConversionValueDialog.ADD_MODE) {
				return "ADD";
			}
			return "EDIT";
		}
		
		protected Component buildMainPanel() {
			this.setTitle(resourceRepository().getString("CONVERSION_VALUE_DIALOG_" + addOrEditString() +".title"));
			
			GridBagConstraints constraints = new GridBagConstraints();
			Insets margin = UIManager.getInsets("TextField.margin");

			if (margin == null)
				margin = new Insets(1, 2, 1, 2);

			JPanel messagePanel = new JPanel(new GridBagLayout());
			
			// Data Type Label
			JLabel dataTypeLabel = new JLabel(resourceRepository().getString("CONVERSION_VALUE_DIALOG.DATA_TYPE_LABEL"));
			constraints.gridx 		= 0;
			constraints.gridy 		= 0;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 0;
			constraints.weighty		= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			messagePanel.add(dataTypeLabel, constraints);

			// Data Type Display Label
			//TODO an icon for this label? had one in 9.0.4
			JTextField dataTypeField = new JTextField(this.converter.getDataType().displayStringWithPackage(), 20);
			dataTypeField.setEditable(false);
			constraints.gridx 		= 1;
			constraints.gridy 		= 0;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 1;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			messagePanel.add(dataTypeField, constraints);

			// Data Value Label 
			JLabel dataValueLabel = new JLabel(resourceRepository().getString("CONVERSION_VALUE_DIALOG.DATA_VALUE_LABEL"));
			constraints.gridx 		= 0;
			constraints.gridy 		= 1;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 0;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			messagePanel.add(dataValueLabel, constraints);
			
			// Data Value Text Field
			this.dataValueTextField = new JTextField(this.dataValueDocument, this.valuePairDataValueString(), 20);
			dataValueLabel.setLabelFor(this.dataValueTextField);
			constraints.gridx 		= 1;
			constraints.gridy 		= 1;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 1;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			messagePanel.add(this.dataValueTextField, constraints);
			
			// Attribute Type Label
			JLabel attributeTypeLabel = new JLabel(resourceRepository().getString("CONVERSION_VALUE_DIALOG.ATTRIBUTE_TYPE_LABEL"));
			constraints.gridx 		= 0;
			constraints.gridy 		= 2;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 0;
			constraints.weighty		= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(15, 5, 5, 5);
			messagePanel.add(attributeTypeLabel, constraints);
			
			// Attribute Type Display Label
			//TODO an icon for this label? had one in 9.0.4
			JTextField objectTypeField = new JTextField(this.converter.getAttributeType().displayStringWithPackage(), 20);
			objectTypeField.setEditable(false);
			constraints.gridx 		= 1;
			constraints.gridy 		= 2;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 1;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(15, 5, 5, 5);
			messagePanel.add(objectTypeField, constraints);
			
			// Attribute Value Label
			JLabel attributeValueLabel = new JLabel(resourceRepository().getString("CONVERSION_VALUE_DIALOG.ATTRIBUTE_VALUE_LABEL"));
			constraints.gridx 		= 0;
			constraints.gridy 		= 3;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 0;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			messagePanel.add(attributeValueLabel, constraints);
			
			// Attribute Value Text Field
			JTextField attributeValueTextField = new JTextField(this.attributeValueDocument, this.valuePairAttributeValueString(), 20);
			attributeValueLabel.setLabelFor(attributeValueTextField);
			constraints.gridx 		= 1;
			constraints.gridy 		= 3;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 1;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.PAGE_START;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			messagePanel.add(attributeValueTextField, constraints);

			// Push everything up
			constraints.gridx 		= 0;
			constraints.gridy 		= 4;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 0;
			constraints.weighty 	= 1;
			constraints.fill 		= GridBagConstraints.NONE;
			constraints.anchor 		= GridBagConstraints.CENTER;
			constraints.insets 		= new Insets(0, 0, 0, 0);
			messagePanel.add(new Spacer(), constraints);

			return messagePanel;
		}
		
		protected Component initialFocusComponent() {
			return this.dataValueTextField;
		}
		
		protected String helpTopicId() {
			return "mapping.converter.objectType.conversionValueDialog";
		}

		
		private RegexpDocument buildDataValueDocument() {
			this.dataValueDocument = new RegexpDocument(RegexpDocument.RE_OTHER);
			this.dataValueDocument.addDocumentListener(new ConversionValueDocumentHandler());
			return this.dataValueDocument;
		}
		
		private String valuePairDataValueString() {
			return (this.valuePair == null) ? "" : this.valuePair.getDataValueAsString();
		}
		
		private RegexpDocument buildAttributeValueDocument() {
			this.attributeValueDocument = new RegexpDocument(RegexpDocument.RE_OTHER);
			this.attributeValueDocument.addDocumentListener(new ConversionValueDocumentHandler());
			return this.attributeValueDocument;
		}
		
		private String valuePairAttributeValueString() {
			return (this.valuePair == null) ? "" : this.valuePair.getAttributeValueAsString();
		}
		
		protected boolean preConfirm() {
			return ConversionValueDialog.this.wasAbleToCompleteValuePair();
		}
				
		
		
		// **************** Behavior ******************************************
		
		
		void updateOKButton() {
			boolean enableOKButton = ! this.dataValueDocumentText().equals("") && ! this.attributeValueDocumentText().equals("");
			this.getOKAction().setEnabled(enableOKButton);
		}
		
		private String dataValueDocumentText() {
			try {
				return this.dataValueDocument.getText(0, this.dataValueDocument.getLength());
			}
			catch (BadLocationException ble) {
				return "";
			}
		}
		
		private String attributeValueDocumentText() {
			try {
				return this.attributeValueDocument.getText(0, this.attributeValueDocument.getLength());
			}
			catch (BadLocationException ble) {
				return "";
			}
		}
		
		public boolean wasAbleToCompleteValuePair() {		
			String dataValueText = this.dataValueDocumentText();
			String attributeValueText = this.attributeValueDocumentText();
			
			try {
				if (this.mode == ADD_MODE) {
					this.converter.addValuePair(dataValueText, attributeValueText);
				}
				else {
					this.converter.editValuePair(this.valuePair, dataValueText, attributeValueText);
				}
			}
			catch (ConversionException ce) {
				showInvalidConversionDialog(ce);
				return false;
			}
			catch (MWObjectTypeConverter.ConversionValueException cve) {
				if (cve.isRepeatedDataValue()) {
					showRepeatedDataValueDialog(dataValueText);
					return false;
				}
				else if (cve.isRepeatedAttributeValue()) {
					showRepeatedAttributeValueDialog(attributeValueText);
					return false;
				}
			}
			
			return true;
		}
		
		private void showInvalidConversionDialog(ConversionException ce) {
			JOptionPane.showMessageDialog(currentWindow(), 
										  this.getInvalidInputMessage(ce.getClassToConvertTo()),
										  resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_FORMAT_DIALOG.title"),
										  JOptionPane.WARNING_MESSAGE);
		}
		
		protected String getInvalidInputMessage(Class javaClass) {
			String javaClassName = javaClass.getName();
			
			if (javaClass == Boolean.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_BOOLEAN_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == Character.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_CHARACTER_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == Short.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_NUMBER_FORMAT", 
													 new Object[] {javaClassName, 
																   new Short(Short.MIN_VALUE), 
																   new Short(Short.MAX_VALUE)});
			}
			else if (javaClass == Byte.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_NUMBER_FORMAT", 
													 new Object[] {javaClassName, 
																   new Byte(Byte.MIN_VALUE), 
																   new Byte(Byte.MAX_VALUE)});
			}
			else if (javaClass == Float.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_FLOAT_NUMBER_FORMAT", 
													 new Object[] {javaClassName, 
																   new Float(Float.MIN_VALUE), 
																   new Float(Float.MAX_VALUE)});
			}
			else if (javaClass == Double.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_FLOAT_NUMBER_FORMAT", 
													 new Object[] {javaClassName, 
																   new Double(Double.MIN_VALUE), 
																   new Double(Double.MAX_VALUE)});
			}
			else if (javaClass == Integer.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_NUMBER_FORMAT", 
													 new Object[] {javaClassName, 
																   new Integer(Integer.MIN_VALUE), 
																   new Integer(Integer.MAX_VALUE)});
			}
			else if (javaClass == Long.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_NUMBER_FORMAT", 
													 new Object[] {javaClassName, 
																   new Long(Long.MIN_VALUE), 
																   new Long(Long.MAX_VALUE)});
			}
			else if (javaClass == String.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_STRING_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == Number.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_NUMBER_CLASS_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == java.math.BigDecimal.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_BIGDECIMAL_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == java.math.BigInteger.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_BIGINTEGER_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == java.sql.Date.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_SQLDATE_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == java.sql.Time.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_SQLTIME_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == java.sql.Timestamp.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_SQLTIMESTAMP_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == java.util.Date.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_UTILDATE_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == java.util.Calendar.class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_UTILCALENDAR_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == byte[].class || javaClass == Byte[].class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_BYTEARRAY_FORMAT", new Object[] {javaClassName});
			}
			else if (javaClass == char[].class || javaClass == Character[].class) {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_CHARARRAY_FORMAT", new Object[] {javaClassName});
			}
			else {
				return resourceRepository().getString("CONVERSION_VALUE_DIALOG.ILLEGAL_GENERIC_FORMAT");
			}
		}
		
		private void showRepeatedDataValueDialog(String valueString) {
			JOptionPane.showMessageDialog(currentWindow(), 
										  resourceRepository().getString("CONVERSION_VALUE_DIALOG.REPEATED_DATA_VALUE_DIALOG.message", valueString),
										  resourceRepository().getString("CONVERSION_VALUE_DIALOG.REPEATED_DATA_VALUE_DIALOG.title"),
										  JOptionPane.WARNING_MESSAGE);

		}
		
		private void showRepeatedAttributeValueDialog(String valueString) {
			JOptionPane.showMessageDialog(currentWindow(), 
											  resourceRepository().getString("CONVERSION_VALUE_DIALOG.REPEATED_OBJECT_VALUE_DIALOG.message", valueString),
											  resourceRepository().getString("CONVERSION_VALUE_DIALOG.REPEATED_OBJECT_VALUE_DIALOG.title"),
											  JOptionPane.WARNING_MESSAGE);

		}
		
	}


	// ********** classes **********

	private static class ValuePairsColumnAdapter implements ColumnAdapter {
		
		private ResourceRepository resourceRepository;
		
		public static final int COLUMN_COUNT = 3;

		public static final int DATA_VALUE_COLUMN = 0;
		public static final int ATTRIBUTE_VALUE_COLUMN = 1;
		public static final int DEFAULT_ATTRIBUTE_VALUE_COLUMN = 2;

		private static final String[] COLUMN_NAME_KEYS = new String[] {
			"DATA_VALUE_HEADER",
			"ATTRIBUTE_VALUE_HEADER",
			"DEFAULT_ATTRIBUTE_VALUE_HEADER",
		};

		protected ValuePairsColumnAdapter(ResourceRepository repository) {
			super();
			this.resourceRepository = repository;
		}
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		public String getColumnName(int index) {
			return this.resourceRepository.getString(COLUMN_NAME_KEYS[index]);
		}

		public Class getColumnClass(int index) {
			switch (index) {
				case DATA_VALUE_COLUMN:					return Object.class;
				case ATTRIBUTE_VALUE_COLUMN:			return Object.class;
				case DEFAULT_ATTRIBUTE_VALUE_COLUMN:	return Boolean.class;
				default: 								return Object.class;
			}
		}

		public boolean isColumnEditable(int index) {
			return index == DEFAULT_ATTRIBUTE_VALUE_COLUMN;
		}

		public PropertyValueModel[] cellModels(Object subject) {
			MWObjectTypeConverter.ValuePair valuePair = (MWObjectTypeConverter.ValuePair) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[DATA_VALUE_COLUMN]				= this.buildDataValueAdapter(valuePair);
			result[ATTRIBUTE_VALUE_COLUMN]			= this.buildAttributeValueAdapter(valuePair);
			result[DEFAULT_ATTRIBUTE_VALUE_COLUMN]	= this.buildDefaultAttributeValueAdapter(valuePair);

			return result;
		}

		private PropertyValueModel buildDataValueAdapter(MWObjectTypeConverter.ValuePair valuePair) {
			return new PropertyAspectAdapter(MWObjectTypeConverter.ValuePair.DATA_VALUE_PROPERTY, valuePair) {
				protected Object getValueFromSubject() {
					return ((MWObjectTypeConverter.ValuePair) this.subject).getDataValueAsString();
				}
			};
		}
		
		private PropertyValueModel buildAttributeValueAdapter(MWObjectTypeConverter.ValuePair valuePair) {
			return new PropertyAspectAdapter(MWObjectTypeConverter.ValuePair.ATTRIBUTE_VALUE_PROPERTY, valuePair) {
				protected Object getValueFromSubject() {
					return ((MWObjectTypeConverter.ValuePair) this.subject).getAttributeValueAsString();
				}
			};
		}

		private PropertyValueModel buildDefaultAttributeValueAdapter(MWObjectTypeConverter.ValuePair valuePair) {
			return new PropertyAspectAdapter(MWObjectTypeConverter.ValuePair.DEFAULT_ATTRIBUTE_VALUE_PROPERTY, valuePair) {
				protected Object getValueFromSubject() {
					return Boolean.valueOf(((MWObjectTypeConverter.ValuePair) this.subject).isDefaultAttributeValue());
				}
				protected void setValueOnSubject(Object value) {
					((MWObjectTypeConverter.ValuePair) this.subject).setDefaultAttributeValue(((Boolean) value).booleanValue());
				}
			};
		}
	}
}
