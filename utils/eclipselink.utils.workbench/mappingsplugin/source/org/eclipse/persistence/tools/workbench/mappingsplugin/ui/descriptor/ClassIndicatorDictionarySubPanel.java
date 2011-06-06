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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAbstractClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.DescriptorCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.TypeDeclarationCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.CheckBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;

import org.eclipse.persistence.exceptions.ConversionException;

public abstract class ClassIndicatorDictionarySubPanel extends AbstractPanel implements RootListener, IndicatorFieldListener, IndicatorDictionaryListener {
	
	private JButton editButton;	

	private ObjectListSelectionModel rowSelectionModel;
	
	private TableModel classIndicatorTableModel;
	
	private ValueModel classIndicatorFieldPolicyHolder;
	
	protected ListChooser indicatorTypeListChooser;
	
	protected JLabel typeLabel;
	
	protected JScrollPane tableScrollPane;
	
	private boolean isRoot;
	
	private boolean isIndicatorField;
	
	private boolean isIndicatorDictionary;

	protected ClassIndicatorDictionarySubPanel(PropertyValueModel classIndicatorPolicyHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		initialize(classIndicatorPolicyHolder);
	}

	protected void initialize(PropertyValueModel classIndicatorPolicyHolder) {
		this.classIndicatorFieldPolicyHolder = buildClassIndicatorFieldPolicyHolder(classIndicatorPolicyHolder);
		this.rowSelectionModel = buildRowSelectionModel();
		this.editButton = buildEditButton();
	}

    protected PropertyValueModel buildClassIndicatorFieldPolicyHolder(PropertyValueModel classIndicatorPolicyHolder) {
        return new FilteringPropertyValueModel(classIndicatorPolicyHolder) {
            protected boolean accept(Object value) {
                return  value instanceof MWClassIndicatorFieldPolicy;
            }
        };
    }
	
	protected ValueModel buildUseClassIndicatorDictionaryBooleanHolder() {
		return new PropertyAspectAdapter(this.classIndicatorFieldPolicyHolder, MWClassIndicatorFieldPolicy.CLASS_NAME_IS_INDICATOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(!((MWClassIndicatorFieldPolicy) this.subject).classNameIsIndicator());
			}
		};
	}

	private JButton buildEditButton() {
		JButton editButton = buildButton("EDIT");
		editButton.addActionListener(buildEditAction());
		editButton.setEnabled(false);
		
		return editButton;
	}
	
	protected ListChooser buildIndicatorTypeChooser() {
		ListChooser indicatorTypeChooser = 
			new DefaultListChooser(
				new ComboBoxModelAdapter(
						buildIndicatorTypeChooserValueModel(), 
						buildIndicatorTypeChooserPropertyAdapter()), 
				getWorkbenchContextHolder());
		indicatorTypeChooser.setRenderer(buildIndicatorTypeChooserRenderer());
		indicatorTypeChooser.setLongListSize(30);
		
		return indicatorTypeChooser;
	}
	
	private ListCellRenderer buildIndicatorTypeChooserRenderer() {
		return new AdaptableListCellRenderer(new TypeDeclarationCellRendererAdapter(resourceRepository()));
	}

	private TableCellRenderer buildClassColumnRenderer() {
		return new AdaptableTableCellRenderer(new DescriptorCellRendererAdapter(resourceRepository()));
	}
	
	private CollectionValueModel buildIndicatorTypeChooserValueModel() {
		return new CollectionAspectAdapter(this.classIndicatorFieldPolicyHolder) {
			protected Iterator getValueFromSubject() {
				MWAbstractClassIndicatorPolicy policy = (MWAbstractClassIndicatorPolicy) this.subject;
				return policy.buildBasicTypes().iterator();
			}
		};
	}
	
	private PropertyValueModel buildIndicatorTypeChooserPropertyAdapter() {
		return new PropertyAspectAdapter(this.classIndicatorFieldPolicyHolder, MWClassIndicatorFieldPolicy.INDICATOR_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassIndicatorFieldPolicy) this.subject).getIndicatorType();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWClassIndicatorFieldPolicy) this.subject).setIndicatorType((MWTypeDeclaration) value);
			}	
		};	
	}
				
	private static class ClassIndicatorValueColumnAdapter implements ColumnAdapter {
		
		private ResourceRepository resourceRepository;
		
		private ClassIndicatorDictionarySubPanel parentPanel;
				
		public static final int COLUMN_COUNT = 3;

		public static final int INCLUDE_COLUMN = 0;
		public static final int CLASS_COLUMN = 1;
		public static final int VALUE_COLUMN = 2;

		private static final String[] COLUMN_NAME_KEYS = new String[] {
			"INCLUDE_COLUMN_HEADER",
			"CLASS_COLUMN_HEADER",
			"VALUE_COLUMN_HEADER",
		};

		protected ClassIndicatorValueColumnAdapter(ResourceRepository repository, ClassIndicatorDictionarySubPanel parentPanel) {
			super();
			this.parentPanel = parentPanel;
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
				case INCLUDE_COLUMN:			return Boolean.class;
				case CLASS_COLUMN:				return Object.class;
				case VALUE_COLUMN:				return Object.class;

				default: 						return Object.class;
			}
		}

		public boolean isColumnEditable(int index) {
			return index == INCLUDE_COLUMN;
		}

		public PropertyValueModel[] cellModels(Object subject) {
			MWClassIndicatorValue indicatorValue = (MWClassIndicatorValue)subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];
			
			//need to add these listeners here so that we can enable/disable the edit button according to the include checkbox
			PropertyValueModel includeAdapter = this.buildIncludeAdapter(indicatorValue);
			includeAdapter.addPropertyChangeListener(parentPanel.buildIncludePropertyChangeListener());
			result[INCLUDE_COLUMN]	= includeAdapter;
			result[CLASS_COLUMN]	= this.buildClassAdapter(indicatorValue);
			result[VALUE_COLUMN]	= this.buildValueAdapter(indicatorValue);

			return result;
		}

		private PropertyValueModel buildIncludeAdapter(MWClassIndicatorValue indicatorValue) {
			return new PropertyAspectAdapter(MWClassIndicatorValue.INCLUDE_PROPERTY, indicatorValue) {
				protected Object getValueFromSubject() {
					return Boolean.valueOf(((MWClassIndicatorValue) this.subject).isInclude());
				}
				protected void setValueOnSubject(Object value) {
					((MWClassIndicatorValue) this.subject).setInclude(((Boolean)value).booleanValue());
				}
			};
		}

		private PropertyValueModel buildClassAdapter(MWClassIndicatorValue indicatorValue) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWClassIndicatorValue.DESCRIPTOR_PROPERTY, indicatorValue) {
				protected Object getValueFromSubject() {
					return ((MWClassIndicatorValue) this.subject).getDescriptorValue();
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWDescriptor.NAME_PROPERTY);
		}
		
		private PropertyValueModel buildValueAdapter(MWClassIndicatorValue indicatorValue) {
			return new PropertyAspectAdapter(MWClassIndicatorValue.INDICATOR_VALUE_PROPERTY, indicatorValue) {
				protected Object getValueFromSubject() {
					return ((MWClassIndicatorValue) this.subject).getIndicatorValueAsString();
				}
			};
		}

	}
		
	private static class MWClassIndicatorValueDialog extends AbstractDialog {
		
		private Object originalIndicatorValue;
		private JTextField indicatorValueTextField;
		private MWClassIndicatorFieldPolicy policy;
		private MWMappingDescriptor descriptor;
		
		private MWClassIndicatorValueDialog(WorkbenchContext context, ValueModel model, MWMappingDescriptor descriptor, Object indicatorValue) {
			super(context);
            this.originalIndicatorValue = indicatorValue;
			this.descriptor = descriptor;
            this.policy = (MWClassIndicatorFieldPolicy) model.getValue();
		}
		
		private String getStringValue(Object object) {
			if (object == null)
				return "";
			else
				return object.toString();
		}
		
		protected String helpTopicId() {
			return "descriptor.inheritance.indicator.editDialog";
		}
		
		protected Component buildMainPanel() {
			JPanel panel = new JPanel(new GridBagLayout());

			setTitle(resourceRepository().getString("ENTER_INDICATOR_VALUE"));

			GridBagConstraints constraints = new GridBagConstraints();
			
			// Create the Database type label and textfield/ label
			JLabel indicatorTypeLabel = new JLabel(resourceRepository().getString("INDICATOR_TYPE"));
			indicatorTypeLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("INDICATOR_TYPE"));
			
			constraints.gridx 		= 0;
			constraints.gridy 		= 0;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 0;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.NORTHWEST;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			
			panel.add(indicatorTypeLabel, constraints);
			
			JLabel indicatorType = new JLabel(this.policy.getIndicatorType().displayStringWithPackage(), resourceRepository().getIcon("class.public"), SwingConstants.CENTER);
			JPanel indicatorTypePanel = new JPanel(new BorderLayout());
			indicatorTypePanel.setMinimumSize(new Dimension(0, 20));
			indicatorTypePanel.setBorder(BorderFactory.createEtchedBorder());
			indicatorTypePanel.add(indicatorType, BorderLayout.WEST);
			
			constraints.gridx 		= 1;
			constraints.gridy 		= 0;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 1;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.BOTH;
			constraints.anchor 		= GridBagConstraints.NORTHWEST;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			
			panel.add(indicatorTypePanel, constraints);
			
			// Create the indicator value label and textfield
			JLabel indicatorValueLabel = new JLabel(resourceRepository().getString("INDICATOR_VALUE"));
			indicatorValueLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("INDICATOR_VALUE"));
			
			constraints.gridx 		= 0;
			constraints.gridy 		= 1;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 0;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.NORTHWEST;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			
			panel.add(indicatorValueLabel, constraints);
			
            this.indicatorValueTextField = new JTextField(getStringValue(this.originalIndicatorValue));
			helpManager().addTopicID(this.indicatorValueTextField, helpTopicId() + ".indicatorValue");
            this.indicatorValueTextField.setColumns(20);
            this.indicatorValueTextField.getDocument().addDocumentListener(new CVDocumentListener());
			indicatorValueLabel.setLabelFor(this.indicatorValueTextField);
			
			constraints.gridx 		= 1;
			constraints.gridy 		= 1;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 1;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.NORTHWEST;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			
			panel.add(this.indicatorValueTextField, constraints);
			
			updateOKButton();
			
			return panel;
		}
				
		public boolean preConfirm() {
			String indicatorValueText = this.indicatorValueTextField.getText();
			Object indicatorValue = null;
			try {
				indicatorValue = this.policy.buildIndicatorValueFromString(indicatorValueText);
			} catch (ConversionException ce) {
				showInvalidConversionDialog(ce);
				return false;
			}
			if (indicatorValue.equals(this.originalIndicatorValue)) {
				return true;
			} else {
				if (this.policy.isRepeatedIndicatorValue(indicatorValue)) {
					showRepeatedIndicatorValueDialog(indicatorValueText);
					return false;
				} else {
                    this.policy.getClassIndicatorValueForDescriptor(descriptor).setIndicatorValue(indicatorValue);
					return true;
				}
			}
		}
		
		protected String getInvalidInputMessage(Class javaClass) {
			String javaClassName = javaClass.getName();
			if (javaClassName.equals("java.lang.Boolean"))
				return resourceRepository().getString("BE_EITHER_TRUE_OR_FALSE", javaClassName);
			else if (javaClassName.equals("java.lang.Byte"))
				return resourceRepository().getString("BE_BETWEEN_0_AND_127", javaClassName);
			else if (javaClassName.equals("java.lang.Character"))
				return resourceRepository().getString("BE_SINGLE_CHARACTER", javaClassName);
			else if (javaClassName.equals("java.lang.Double"))
				return resourceRepository().getString("BE_BETWEEN_DOUBLE",
					new Object[] { javaClassName, NumberFormat.getInstance().format(Double.MIN_VALUE), NumberFormat.getInstance().format(Double.MAX_VALUE)});
			else if (javaClassName.equals("java.lang.Float"))
				return resourceRepository().getString("BE_BETWEEN_FLOAT",
					new Object[] { javaClassName, NumberFormat.getInstance().format(Float.MIN_VALUE), NumberFormat.getInstance().format(Float.MAX_VALUE)});
			else if (javaClassName.equals("java.lang.Integer"))
				return resourceRepository().getString("BE_BETWEEN_INT",
					new Object[] { javaClassName, NumberFormat.getInstance().format(Integer.MIN_VALUE), NumberFormat.getInstance().format(Integer.MAX_VALUE)});
			else if (javaClassName.equals("java.lang.Long"))
				return resourceRepository().getString("BE_BETWEEN_LONG", new Object[] { javaClassName, NumberFormat.getInstance().format(Long.MIN_VALUE), NumberFormat.getInstance().format(Long.MAX_VALUE)});
			else if (javaClassName.equals("java.lang.Short"))
				return resourceRepository().getString("BE_BETWEEN_SHORT",
					new Object[] { javaClassName, NumberFormat.getInstance().format(Short.MIN_VALUE), NumberFormat.getInstance().format(Short.MAX_VALUE)});
			else if (javaClassName.equals("java.lang.String"))
				return resourceRepository().getString("BE_A_STRING", javaClassName);
			else if (javaClassName.equals("java.math.BigDecimal"))
				return resourceRepository().getString("CONTAINS_ONLY_DIGITS", javaClassName);
			else if (javaClassName.equals("java.math.BigInteger"))
				return resourceRepository().getString("CONTAINS_ONLY_DIGITS_AND", javaClassName);
			else if (javaClassName.equals("java.sql.Date"))
				return resourceRepository().getString("BE_IN_FORMAT1", javaClassName);
			else if (javaClassName.equals("java.sql.Time"))
				return resourceRepository().getString("BE_IN_FORMAT2", javaClassName);
			else if (javaClassName.equals("java.sql.Timestamp"))
				return resourceRepository().getString("BE_IN_FORMAT3", javaClassName);
			else if (javaClassName.equals("java.util.Date"))
				return resourceRepository().getString("BE_IN_FORMAT4", javaClassName);
			else if (javaClassName.equals("java.util.Calendar"))
				return resourceRepository().getString("BE_IN_FORMAT5", javaClassName);
			else if (javaClass == byte[].class || javaClass == Byte[].class) {
				return resourceRepository().getString("BE_IN_FORMAT_BYTE_ARRAY", javaClassName);
			}
			else if (javaClass == char[].class || javaClass == Character[].class) {
				return resourceRepository().getString("BE_A_STRING", javaClassName);
			}
			else
				return resourceRepository().getString("ILLEGAL_FORMAT");
		}
		
		private void showInvalidConversionDialog(ConversionException ce) {
			JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(), 
				getInvalidInputMessage(ce.getClassToConvertTo()),
				resourceRepository().getString("ILLEGAL_FORMAT"),
				JOptionPane.WARNING_MESSAGE);
		}
		
		private void showRepeatedIndicatorValueDialog(String valueString) {
			JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(),
				resourceRepository().getString("INDICATOR_VALUE_ALLREADY_EXISTS", valueString),
				resourceRepository().getString("REPEATED_INDICATOR_VALUE"),
				JOptionPane.WARNING_MESSAGE);
		}
		
		private void updateOKButton() {
			boolean enableOKButton = this.indicatorValueTextField.getText() != null && !this.indicatorValueTextField.getText().equals("");
			getOKAction().setEnabled(enableOKButton);
		}
		
		private class CVDocumentListener implements DocumentListener {
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
	}
			
	protected MWClassIndicatorFieldPolicy getClassIndicatorPolicy() {
		return (MWClassIndicatorFieldPolicy) this.classIndicatorFieldPolicyHolder.getValue();
	}
		
	protected String helpTopicId() {
		return "descriptor.inheritance.classIndicator.classIndicatorDictionary";	
	}
		
	protected JTable buildClassIndicatorValuesTable() 
	{
		// Create the table view
        this.classIndicatorTableModel = buildClassIndicatorValuesTableModel();
		JTable classIndicatorValuesTable = SwingComponentFactory.buildTable(this.classIndicatorTableModel, this.rowSelectionModel);
		classIndicatorValuesTable.setPreferredScrollableViewportSize(new Dimension(400, 75));
			
		// include column
		TableColumn column = classIndicatorValuesTable.getColumnModel().getColumn(ClassIndicatorValueColumnAdapter.INCLUDE_COLUMN);
		column.setPreferredWidth(25);
		CheckBoxTableCellRenderer includeRenderer = new CheckBoxTableCellRenderer();
		column.setCellRenderer(includeRenderer);
		column.setCellEditor(new TableCellEditorAdapter(new CheckBoxTableCellRenderer()));

		// database type column (combo-box)
		column = classIndicatorValuesTable.getColumnModel().getColumn(ClassIndicatorValueColumnAdapter.CLASS_COLUMN);
		column.setPreferredWidth(100);
		TableCellRenderer classRenderer = buildClassColumnRenderer();
		column.setCellRenderer(classRenderer);

		// size column (spinner)
		column = classIndicatorValuesTable.getColumnModel().getColumn(ClassIndicatorValueColumnAdapter.VALUE_COLUMN);
		column.setPreferredWidth(100);
		
		return classIndicatorValuesTable;
	}
	
	private TableModel buildClassIndicatorValuesTableModel() {
		return new TableModelAdapter(buildClassIndicatorValueAdapter(), buildClassIndicatorValueTableColumnAdapter());
	}

	private ColumnAdapter buildClassIndicatorValueTableColumnAdapter() {
		return new ClassIndicatorValueColumnAdapter(resourceRepository(), this);
	}

	private ObjectListSelectionModel buildRowSelectionModel() {
		ObjectListSelectionModel rowSelectionModel = new ObjectListSelectionModel(new ListModelAdapter(buildClassIndicatorValuesHolder()));
		rowSelectionModel.addListSelectionListener(this.buildRowSelectionListener(rowSelectionModel));
		rowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		return rowSelectionModel;
	}
	
	private ListValueModel buildClassIndicatorValueAdapter() {
		return new ItemPropertyListValueModelAdapter(buildClassIndicatorValuesHolder(), MWClassIndicatorValue.INDICATOR_VALUE_PROPERTY);
	}

	private CollectionValueModel buildClassIndicatorValuesHolder() {
		return new CollectionAspectAdapter(this.classIndicatorFieldPolicyHolder, MWClassIndicatorFieldPolicy.CLASS_INDICATOR_VALUES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWClassIndicatorFieldPolicy) this.subject).classIndicatorValues();
			}
		};
	}
	
	private Action buildEditAction() {
		return new AbstractFrameworkAction(getApplicationContext()) {
			public void actionPerformed(ActionEvent e) {
				editIndicator();
			}
		};	
	}
	
	private void editIndicator() {
		Object indicatorValue = null;
		MWMappingDescriptor descriptor = null;
		for (int row = this.classIndicatorTableModel.getRowCount() - 1; row >= 0; row--) {
			if (this.rowSelectionModel.isSelectedIndex(row)) {
				indicatorValue = this.classIndicatorTableModel.getValueAt(row, ClassIndicatorValueColumnAdapter.VALUE_COLUMN);
				descriptor = (MWMappingDescriptor) this.classIndicatorTableModel.getValueAt(row, ClassIndicatorValueColumnAdapter.CLASS_COLUMN);
			}
		}
		MWClassIndicatorValueDialog dlg = new MWClassIndicatorValueDialog(getWorkbenchContext(), this.classIndicatorFieldPolicyHolder, descriptor, indicatorValue);
		dlg.show();
		
//		if(dlg.wasConfirmed())
	}

	private ListSelectionListener buildRowSelectionListener(final ListSelectionModel selectionModel) {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					editButton.getModel().setEnabled( editButtonShouldBeEnabled() && ! (selectionModel.isSelectionEmpty()
						 && selectionModel.getMinSelectionIndex() == selectionModel.getMaxSelectionIndex()));
				}
			}
		};
	}
	
	private PropertyChangeListener buildIncludePropertyChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateEditButton();
			}
		};
	}
	private void updateEditButton() {
		editButton.setEnabled(editButtonShouldBeEnabled());
	}
	
	private boolean editButtonShouldBeEnabled() {
		return getRowSelectionModel().getSelectedValue() != null
		&& ((MWClassIndicatorValue)rowSelectionModel.getSelectedValue()).isInclude();
	}
	
	protected ObjectListSelectionModel getRowSelectionModel() {
		return this.rowSelectionModel;
	}

	protected JButton getEditButton() {
		return this.editButton;
	}
	
	public void updateRootStatus(boolean newValue) {
		this.isRoot = newValue;
		updateEnablementStatus();
	}
	
	public void updateIndicatorFieldStatus(boolean newValue) {
		this.isIndicatorField = newValue;
		updateEnablementStatus();
	}

	public void updateIndicatorDictionaryStatus(boolean newValue) {
		this.isIndicatorDictionary = newValue;
		updateEnablementStatus();		
	}
	
	protected void updateEnablementStatus() {
        this.indicatorTypeListChooser.setEnabled(this.isRoot() && this.isIndicatorField() && this.isIndicatorDictionary());
        this.typeLabel.setEnabled(this.isRoot() && this.isIndicatorField() && this.isIndicatorDictionary());
        this.tableScrollPane.setEnabled(this.isRoot() && this.isIndicatorField() && this.isIndicatorDictionary());
	}

	public boolean isRoot() {
		return this.isRoot;
	}

	public boolean isIndicatorField() {
		return this.isIndicatorField;
	}

	public boolean isIndicatorDictionary() {
		return this.isIndicatorDictionary;
	}

}
