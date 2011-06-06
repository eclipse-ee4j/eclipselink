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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWSequencingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ColumnCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ExtendedListValueModelWrapper;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


final class RelationalProjectSequencingPropertiesPage extends ScrollablePropertiesPage {

	private PropertyValueModel sequencingPolicyHolder;
	private PseudoPreallocationSizeModel pseudoModel;
	private PropertyValueModel sequenceTableHolder;


	RelationalProjectSequencingPropertiesPage(PropertyValueModel projectNodeHolder, WorkbenchContextHolder contextHolder) {
		super(projectNodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.sequencingPolicyHolder = buildSequencingPolicyHolder();
		this.sequenceTableHolder = this.buildSequenceTableHolder(this.sequencingPolicyHolder);
		getSelectionHolder().addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectionHolderListener());

		pseudoModel = new PseudoPreallocationSizeModel();
		pseudoModel.setParentNode((AbstractNodeModel) selection());
	}
	
	private PropertyChangeListener buildSelectionHolderListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				pseudoModel.setParentNode((AbstractNodeModel) e.getNewValue());
			}
		};
	}

	private PropertyValueModel buildSequencingPolicyHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWRelationalProject) subject).getSequencingPolicy();
			}
		};
	}
	
	private PropertyValueModel buildSequenceTableHolder(ValueModel sequencingPolicyHolder) {
		return new PropertyAspectAdapter(sequencingPolicyHolder, MWSequencingPolicy.SEQUENCING_TABLE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWSequencingPolicy) subject).getTable();
			}
			protected void setValueOnSubject(Object value) {
				((MWSequencingPolicy) subject).setTable((MWTable) value);
			}
		};
	}

	private String helpTopicId() {
		return "project.sequencing";
	}

	private ComponentEnabler buildNativeSequencingEnabler(JComponent component) {
		return new ComponentEnabler(buildNativeSequencingEnableStateHolder(), Collections.singleton(component));
	}

	private PropertyValueModel buildNativeSequencingEnableStateHolder() {
		PropertyAspectAdapter databaseHolder = new PropertyAspectAdapter(getSelectionHolder(), "") {
			protected Object getValueFromSubject() {
				MWRelationalProject project = (MWRelationalProject) subject;
				return project.getDatabase();
			}
		};

		PropertyAspectAdapter platformHolder = new PropertyAspectAdapter(databaseHolder, MWDatabase.DATABASE_PLATFORM_PROPERTY) {
			protected Object getValueFromSubject() {
				MWDatabase database = (MWDatabase) subject;
				return database.getDatabasePlatform();
			}
		};

		return new TransformationPropertyValueModel(platformHolder) {
			protected Object transform(Object value) {
				if (value == null)
					return null;

				try {
					DatabasePlatform platform = (DatabasePlatform) value;
					return Boolean.valueOf(platform.supportsNativeSequencing());
				}
				catch (IllegalArgumentException e) {
					return Boolean.FALSE;
				}
			}
		};
	}

	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the label
		JComponent preallocationSizeWidgets = buildLabeledSpinnerNumber
		(
			"SEQUENCING_PREALLOCATION_SIZE_SPINNER_LABEL",
			buildSequencingPrealocationSizeSpinnerModel()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 0);

		panel.add(preallocationSizeWidgets, constraints);
		addHelpTopicId(preallocationSizeWidgets, helpTopicId() + ".preallocation");
		buildPreallocationSizeEnabler(preallocationSizeWidgets);

		PropertyValueModel sequencingTypeHolder = buildSequencingTypeHolder(this.sequencingPolicyHolder);

		// Default Sequencing option
		JRadioButton useDefaultSequencingRadioButton = buildDefaultSequencingRadioButton(sequencingTypeHolder);
		addHelpTopicId(useDefaultSequencingRadioButton, helpTopicId() + ".useDefault");

		// Native Sequencing option
		JRadioButton useNativeSequencingRadioButton = buildNativeSequencingRadioButton(sequencingTypeHolder);
		addHelpTopicId(useNativeSequencingRadioButton, helpTopicId() + ".native");
		buildNativeSequencingEnabler(useNativeSequencingRadioButton);

		// sequence table choice
		JRadioButton useSequenceTableRadioButton = buildSequenceTableRadioButton(sequencingTypeHolder);
		addHelpTopicId(useSequenceTableRadioButton, "session.login.database.sequencing.table");

		// Sequence table panel
		JComponent useSequenceTablePanel = buildSequenceTablePanel(sequencingTypeHolder);

		GroupBox useSequenceTableBox = new GroupBox
		(
			useDefaultSequencingRadioButton,
			useNativeSequencingRadioButton,
			useSequenceTableRadioButton,
			useSequenceTablePanel
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(10, 0, 0, 0);

		panel.add(useSequenceTableBox, constraints);
		addHelpTopicId(useSequenceTableBox, "session.login.database.sequencing.table");
		
		addHelpTopicId(panel, helpTopicId());
		return panel;
	}

	protected JComponent buildSequenceTablePanel(final PropertyValueModel sequencingTypeHolder) {
		JPanel sequenceTablePanel = new JPanel(new GridBagLayout());
		GridBagConstraints contraints = new GridBagConstraints();

		Collection components = new ArrayList();
		
		// Create the label
		final JLabel nameLabel = SwingComponentFactory.buildLabel("SEQUENCING_TABLE_NAME_LIST_CHOOSER_LABEL", resourceRepository());
		components.add(nameLabel);
		
		contraints.gridx = 0;
		contraints.gridy = 0;
		contraints.gridwidth = 2;
		contraints.gridheight = 1;
		contraints.weightx = 0;
		contraints.weighty = 0;
		contraints.fill = GridBagConstraints.NONE;
		contraints.anchor = GridBagConstraints.LINE_START;
		contraints.insets = new Insets(0, 0, 0, 0);

		sequenceTablePanel.add(nameLabel, contraints);

		// Create the combo box
		final ListChooser sequenceTableNameChooser = buildSequenceTableChooser();
		components.add(sequenceTableNameChooser);

		contraints.gridx = 1;
		contraints.gridy = 0;
		contraints.gridwidth = 1;
		contraints.gridheight = 1;
		contraints.weightx = 1;
		contraints.weighty = 0;
		contraints.fill = GridBagConstraints.HORIZONTAL;
		contraints.anchor = GridBagConstraints.CENTER;
		contraints.insets = new Insets(0, 5, 0, 0);

		sequenceTablePanel.add(sequenceTableNameChooser, contraints);
		nameLabel.setLabelFor(sequenceTableNameChooser);

		//
		// Action:	Create the Name Field combo box
		//
		// Create the label
		final JLabel nameFieldLabel = SwingComponentFactory.buildLabel("SEQUENCING_NAME_FIELD_COMBO_BOX_LABEL", resourceRepository());
		components.add(nameFieldLabel);

		contraints.gridx = 0;
		contraints.gridy = 1;
		contraints.gridwidth = 1;
		contraints.gridheight = 1;
		contraints.weightx = 0;
		contraints.weighty = 0;
		contraints.fill = GridBagConstraints.NONE;
		contraints.anchor = GridBagConstraints.LINE_START;
		contraints.insets = new Insets(5, 0, 0, 0);

		sequenceTablePanel.add(nameFieldLabel, contraints);

		// Create the combo box
		final ListChooser sequenceTableNameFieldChooser = buildSequenceNameColumnChooser();
		components.add(sequenceTableNameFieldChooser);
	
		contraints.gridx = 1;
		contraints.gridy = 1;
		contraints.gridwidth = 1;
		contraints.gridheight = 1;
		contraints.weightx = 1;
		contraints.weighty = 0;
		contraints.fill = GridBagConstraints.HORIZONTAL;
		contraints.anchor = GridBagConstraints.CENTER;
		contraints.insets = new Insets(5, 5, 0, 0);

		sequenceTablePanel.add(sequenceTableNameFieldChooser, contraints);
		nameFieldLabel.setLabelFor(sequenceTableNameFieldChooser);

		//
		// Action:	Create the Counter Field combo box
		//
		// Create the label
		final JLabel counterFieldLabel = SwingComponentFactory.buildLabel("SEQUENCING_COUNTER_FIELD_COMBO_BOX_LABEL", resourceRepository());
		components.add(counterFieldLabel);

		contraints.gridx = 0;
		contraints.gridy = 2;
		contraints.gridwidth = 1;
		contraints.gridheight = 1;
		contraints.weightx = 0;
		contraints.weighty = 0;
		contraints.fill = GridBagConstraints.NONE;
		contraints.anchor = GridBagConstraints.LINE_START;
		contraints.insets = new Insets(5, 0, 0, 0);

		sequenceTablePanel.add(counterFieldLabel, contraints);

		// Create the combo box
		final ListChooser sequenceCounterFieldChooser = buildSequenceCounterColumnChooser();
		components.add(sequenceCounterFieldChooser);

		contraints.gridx = 1;
		contraints.gridy = 2;
		contraints.gridwidth = 1;
		contraints.gridheight = 1;
		contraints.weightx = 1;
		contraints.weighty = 0;
		contraints.fill = GridBagConstraints.HORIZONTAL;
		contraints.anchor = GridBagConstraints.CENTER;
		contraints.insets = new Insets(5, 5, 0, 0);

		sequenceTablePanel.add(sequenceCounterFieldChooser, contraints);
		counterFieldLabel.setLabelFor(sequenceCounterFieldChooser);

		new ComponentEnabler(buildSequenceTypeBooleanHolder(sequencingTypeHolder), components);
		
		return sequenceTablePanel;
	}

	private ValueModel buildSequenceTypeBooleanHolder(PropertyValueModel sequencingTypeHolder) {
		return new TransformationPropertyValueModel(sequencingTypeHolder) {
			protected Object transform(Object value) {
				return value == MWSequencingPolicy.SEQUENCE_TABLE ? Boolean.TRUE : Boolean.FALSE;
			}
		};
	}
	
	//******** Preallocation Size ********
				
	private PropertyValueModel buildPreallocationSizeEnableStateHolder() {
		return new PropertyAspectAdapter("enabled", pseudoModel) 	{
			protected Object getValueFromSubject() {
				PseudoPreallocationSizeModel model = (PseudoPreallocationSizeModel) subject;
				return Boolean.valueOf(model.isEnabled());
			}
		};
	}

	private ComponentEnabler buildPreallocationSizeEnabler(JComponent widgets) {
		return new ComponentEnabler(buildPreallocationSizeEnableStateHolder(),
											 Collections.singleton(widgets));
	}

	private SpinnerNumberModel buildSequencingPrealocationSizeSpinnerModel() {
		return new NumberSpinnerModelAdapter(buildSequencingPrealocationSizeHolder(), new Integer(0), null, new Integer(1), new Integer(0));
	}

	private PropertyValueModel buildSequencingPrealocationSizeHolder() {
		return new PropertyAspectAdapter(this.sequencingPolicyHolder, MWSequencingPolicy.PREALLOCATION_SIZE_PROPERTY) {
			protected Object getValueFromSubject() {
				return new Integer(((MWSequencingPolicy) subject).getPreallocationSize());
			}
			protected void setValueOnSubject(Object value) {
				((MWSequencingPolicy) subject).setPreallocationSize(((Number) value).intValue());
			}
		};
	}


	//******** Sequencing Type  ********

	private PropertyValueModel buildSequencingTypeHolder(ValueModel sequencingPolicyHolder) {
		return new PropertyAspectAdapter(sequencingPolicyHolder, MWSequencingPolicy.SEQUENCING_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWSequencingPolicy) subject).getSequencingType();
			}
			protected void setValueOnSubject(Object value) {
				((MWSequencingPolicy) subject).setSequencingType((String) value);
			}
		};
	}

	private JRadioButton buildDefaultSequencingRadioButton(PropertyValueModel sequencingTypeHolder) {
		JRadioButton radioButton = 
			SwingComponentFactory.buildRadioButton(
									"DEFAULT_SEQUENCING_RADIO_BUTTON_TEXT", 
									buildRadioButtonModelAdapter(sequencingTypeHolder, MWSequencingPolicy.DEFAULT_SEQUENCING, true),
									resourceRepository()
			); 
		return radioButton;
	}

	private JRadioButton buildNativeSequencingRadioButton(PropertyValueModel sequencingTypeHolder) {
		JRadioButton radioButton = 
			SwingComponentFactory.buildRadioButton(
									"NATIVE_SEQUENCING_RADIO_BUTTON_TEXT", 
									buildRadioButtonModelAdapter(sequencingTypeHolder, MWSequencingPolicy.NATIVE_SEQUENCING, false),
									resourceRepository()
			); 
		return radioButton;
	}

	private JRadioButton buildSequenceTableRadioButton(PropertyValueModel sequencingTypeHolder) {
		JRadioButton radioButton = 
			SwingComponentFactory.buildRadioButton(
									"CUSTOM_SEQUENCE_TABLE_RADIO_BUTTON_TEXT", 
									buildRadioButtonModelAdapter(sequencingTypeHolder, MWSequencingPolicy.SEQUENCE_TABLE, false),
									resourceRepository()
			); 
		return radioButton;
	}

	private ButtonModel buildRadioButtonModelAdapter(PropertyValueModel sequencingTypeHolder, String sequencingType, boolean defaultValue) {
		return new RadioButtonModelAdapter(sequencingTypeHolder, sequencingType, defaultValue);
	}


	//******** Sequence Table ********
	
	private ListChooser buildSequenceTableChooser() {
		return RelationalProjectComponentFactory.
					buildTableChooser(
						getSelectionHolder(), 
						this.sequenceTableHolder, 
						buildSequenceTableChooserDialogBuilder(), 
						getWorkbenchContextHolder()
					);
	}
	
	
	private DefaultListChooserDialog.Builder buildSequenceTableChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("SEQUENCE_TABLE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("SEQUENCE_TABLE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildTableStringConverter());
		return builder;
	}
	
	private StringConverter buildTableStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWTable) o).getName();
			}
		};
	}
	
	
	//******** Sequence Name Column ********

	private ListChooser buildSequenceNameColumnChooser() {
		PropertyValueModel sequenceNameFieldHolder = this.buildSequenceNameColumnAdapter(this.sequencingPolicyHolder);

		ListChooser listChooser = 
			new DefaultListChooser(
				buildComboBoxModelAdapter(this.buildExtendedColumnsAdapter(sequenceTableHolder), sequenceNameFieldHolder), 
				getWorkbenchContextHolder(),
				buildSequenceNameColumnChooserDialogBuilder()
			);
		listChooser.setRenderer(buildColumnListCellRenderer());

		return listChooser;
	}
	
	private DefaultListChooserDialog.Builder buildSequenceNameColumnChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("SEQUENCE_NAME_FIELD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("SEQUENCE_NAME_FIELD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildColumnStringConverter());
		return builder;
	}
	
	private PropertyValueModel buildSequenceNameColumnAdapter(ValueModel sequencingPolicyHolder) {
		return new PropertyAspectAdapter(sequencingPolicyHolder, MWSequencingPolicy.NAME_COLUMN_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWSequencingPolicy) subject).getNameColumn();
			}
			protected void setValueOnSubject(Object value) {
				((MWSequencingPolicy) subject).setNameColumn((MWColumn) value);
			}
		};
	}
	
	private static ComboBoxModel buildComboBoxModelAdapter(ListValueModel listHolder, PropertyValueModel selectionHolder) {
		return new ComboBoxModelAdapter(listHolder, selectionHolder);
	}

	private ListCellRenderer buildColumnListCellRenderer() {
		return new AdaptableListCellRenderer(new ColumnCellRendererAdapter(this.resourceRepository()));
	}
	
	private StringConverter buildColumnStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWColumn) o).getName();
			}
		};
	}
	
	//******** Sequence Counter Column ********
	
	private ListChooser buildSequenceCounterColumnChooser() {
		PropertyValueModel sequenceNameFieldHolder = this.buildSequenceCounterColumnAdapter(this.sequencingPolicyHolder);

		ListChooser listChooser = 
			new DefaultListChooser(
				buildComboBoxModelAdapter(this.buildExtendedColumnsAdapter(sequenceTableHolder), sequenceNameFieldHolder), 
				getWorkbenchContextHolder(),
				buildSequenceCounterColumnChooserDialogBuilder()
			);
		listChooser.setRenderer(buildColumnListCellRenderer());

		return listChooser;
	}
	
	private DefaultListChooserDialog.Builder buildSequenceCounterColumnChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("SEQUENCE_COUNTER_FIELD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("SEQUENCE_COUNTER_FIELD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildColumnStringConverter());
		return builder;
	}
	
	private PropertyValueModel buildSequenceCounterColumnAdapter(ValueModel sequencingPolicyHolder) {
		return new PropertyAspectAdapter(sequencingPolicyHolder, MWSequencingPolicy.COUNTER_COLUMN_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWSequencingPolicy) subject).getCounterColumn();
			}
			protected void setValueOnSubject(Object value) {
				((MWSequencingPolicy) subject).setCounterColumn((MWColumn) value);
			}
		};
	}
	
	private ListValueModel buildExtendedColumnsAdapter(PropertyValueModel sequencingTableHolder) {
		return new ExtendedListValueModelWrapper(new CollectionListValueModelAdapter(buildColumnsAdapter(sequencingTableHolder)));
	}
	
	private CollectionValueModel buildColumnsAdapter(PropertyValueModel sequencingTableHolder) {
		return new CollectionAspectAdapter(sequencingTableHolder, MWTable.COLUMNS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWTable) subject).columns();
			}
			protected int sizeFromSubject() {
				return ((MWTable) subject).columnsSize();
			}
		};
	}

	private class PseudoPreallocationSizeModel extends AbstractNodeModel {
		private boolean enabled;
		private PropertyChangeListener listener;

		private PseudoPreallocationSizeModel() {
			super();
		}

		private PropertyChangeListener buildPropertyChangeListener() {
			return new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					updateEnableState();
				}
			};
		}

		protected void checkParent(Node parent) {
		}

		private void disengageListeners() {
			MWRelationalProject project = (MWRelationalProject) getParent();

			if (project != null) {
				project.getSequencingPolicy().removePropertyChangeListener(MWSequencingPolicy.SEQUENCING_TYPE_PROPERTY, listener);
				project.removePropertyChangeListener(MWDatabase.DATABASE_PLATFORM_PROPERTY, listener);
			}
		}

		public String displayString() {
			return null;
		}

		private void engageListeners() {
			MWRelationalProject project = (MWRelationalProject) getParent();

			if (project != null) {
				project.getSequencingPolicy().addPropertyChangeListener(MWSequencingPolicy.SEQUENCING_TYPE_PROPERTY, listener);
				project.addPropertyChangeListener(MWDatabase.DATABASE_PLATFORM_PROPERTY, listener);
			}
		}

		protected void initialize() {
			super.initialize();
			listener = buildPropertyChangeListener();
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			boolean oldEnabled = isEnabled();
			this.enabled = enabled;
			firePropertyChanged("enabled", oldEnabled, enabled);
		}

		public void setParentNode(Node parentNode) {
			disengageListeners();
			super.setParent(parentNode);
			engageListeners();

			if (parentNode != null)
				updateEnableState();
		}

		private void updateEnableState() {
			MWRelationalProject project = (MWRelationalProject) getParent();
			DatabasePlatform platform = project.getDatabase().getDatabasePlatform();

			// Verify if the Database Platform is Oracle
			boolean oraclePlatform = platform.getName().toLowerCase().indexOf("oracle") != -1;

			// Preallocation Size is enabled only if the platform is Oracle when
			// Native Sequencing is selected
			if (project.getSequencingPolicy().getSequencingType() == MWSequencingPolicy.NATIVE_SEQUENCING) {
				setEnabled(oraclePlatform);
			}
			// Always enabled for any other case
			else {
				setEnabled(true);
			}
		}
	}
}
