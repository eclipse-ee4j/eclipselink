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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.type;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerModel;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCType;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;


/**
 * nothing special...
 */
final class DatabaseTypePropertiesPage
	extends TitledPropertiesPage
{
	static final Object DEFAULT_INITIAL_SIZE_VALUE = new Integer(0);


	public DatabaseTypePropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// JDBC type
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(this.buildJDBCTypeComboBoxPanel(), constraints);

		// allows size
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(this.buildCheckBox("DATABASE_TYPE_ALLOWS_SIZE_CHECK_BOX", this.buildAllowsSizeAdapter()), constraints);

		// requires size
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(this.buildCheckBox("DATABASE_TYPE_REQUIRES_SIZE_CHECK_BOX", this.buildRequiresSizeAdapter()), constraints);

		// initial size
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(this.buildInitialSizeSpinnerPanel(), constraints);

		// allows sub-size
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(this.buildCheckBox("DATABASE_TYPE_ALLOWS_SUB_SIZE_CHECK_BOX", this.buildAllowsSubSizeAdapter()), constraints);

		// allows null
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(this.buildCheckBox("DATABASE_TYPE_ALLOWS_NULL_CHECK_BOX", this.buildAllowsNullAdapter()), constraints);

		// comment
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(this.buildCommentPanel(), constraints);

		// bottom filler
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(new JPanel(), constraints);

		return panel;
	}


	// ********** JDBC type **********

	private Component buildJDBCTypeComboBoxPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		JLabel label = new JLabel(this.resourceRepository().getString("DATABASE_TYPE_JDBC_TYPE_COMBO_BOX"));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic("DATABASE_TYPE_JDBC_TYPE_COMBO_BOX"));
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 0, 0, 5);
		panel.add(label, constraints);

		JComboBox comboBox = this.buildJDBCTypeComboBox();
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		panel.add(comboBox, constraints);

		label.setLabelFor(comboBox);

		return panel;
	}

	private JComboBox buildJDBCTypeComboBox() {
		JComboBox comboBox = new JComboBox(new ComboBoxModelAdapter(this.buildAllJDBCTypesAdapter(), this.buildJDBCTypeAdapter()));
		comboBox.setRenderer(this.buildJDBCTypeRenderer());
		return comboBox;
	}

	private ListCellRenderer buildJDBCTypeRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				// need null check for combo-box
				return (value == null) ? "" : ((JDBCType) value).getName();
			}
		};
	}

	private CollectionValueModel buildAllJDBCTypesAdapter() {
		return new CollectionAspectAdapter(this.getSelectionHolder()) {
			protected Iterator getValueFromSubject() {
				return CollectionTools.sortedSet(((DatabaseType) this.subject).jdbcTypes()).iterator();
			}
			protected int sizeFromSubject() {
				return ((DatabaseType) this.subject).jdbcTypesSize();
			}
		};
	}

	private PropertyValueModel buildJDBCTypeAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabaseType.JDBC_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((DatabaseType) this.subject).getJDBCType();
			}	
			protected void setValueOnSubject(Object value) {
				((DatabaseType) this.subject).setJDBCType((JDBCType) value);
			}
		};
	}


	// ********** check boxes **********

	private JCheckBox buildCheckBox(String key, PropertyValueModel booleanHolder) {
		JCheckBox checkBox = new JCheckBox();
		// set the model first, otherwise it zaps the mnemonic...
		checkBox.setModel(new CheckBoxModelAdapter(booleanHolder));
		checkBox.setText(this.resourceRepository().getString(key));
		checkBox.setMnemonic(this.resourceRepository().getMnemonic(key));
		return checkBox;
	}

	private PropertyValueModel buildAllowsSizeAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabaseType.ALLOWS_SIZE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((DatabaseType) this.subject).allowsSize());
			}	
			protected void setValueOnSubject(Object value) {
				((DatabaseType) this.subject).setAllowsSize(((Boolean) value).booleanValue());
			}
		};
	}

	private PropertyValueModel buildRequiresSizeAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabaseType.REQUIRES_SIZE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((DatabaseType) this.subject).requiresSize());
			}	
			protected void setValueOnSubject(Object value) {
				((DatabaseType) this.subject).setRequiresSize(((Boolean) value).booleanValue());
			}
		};
	}

	private PropertyAspectAdapter buildAllowsSubSizeAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabaseType.ALLOWS_SUB_SIZE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((DatabaseType) this.subject).allowsSubSize());
			}
			protected void setValueOnSubject(Object value) {
				((DatabaseType) this.subject).setAllowsSubSize(((Boolean) value).booleanValue());
			}
		};
	}

	private PropertyAspectAdapter buildAllowsNullAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabaseType.ALLOWS_NULL_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((DatabaseType) this.subject).allowsNull());
			}
			protected void setValueOnSubject(Object value) {
				((DatabaseType) this.subject).setAllowsNull(((Boolean) value).booleanValue());
			}
		};
	}


	// ********** initial size **********

	private Component buildInitialSizeSpinnerPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		JLabel label = new JLabel(this.resourceRepository().getString("DATABASE_TYPE_INITIAL_SIZE_SPINNER"));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic("DATABASE_TYPE_INITIAL_SIZE_SPINNER"));
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 0, 0, 5);
		panel.add(label, constraints);

		JSpinner spinner = this.buildInitialSizeSpinner();
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		panel.add(spinner, constraints);

		label.setLabelFor(spinner);

		return panel;
	}

	private JSpinner buildInitialSizeSpinner() {
		return new JSpinner(this.buildInitialSizeSpinnerModel());
	}

	private SpinnerModel buildInitialSizeSpinnerModel() {
		PropertyValueModel model = this.buildInitialSizeAdapter();
		return new NumberSpinnerModelAdapter(model, new Integer(0), null, new Integer(1), new Integer(0));
	}

	private PropertyValueModel buildInitialSizeAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabaseType.INITIAL_SIZE_PROPERTY) {
			protected Object getValueFromSubject() {
				return new Integer(((DatabaseType) this.subject).getInitialSize());
			}
			protected void setValueOnSubject(Object value) {
				((DatabaseType) this.subject).setInitialSize(((Number) value).intValue());
			}
		};
	}


	// ********** comment **********

	private Component buildCommentPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		JLabel label = new JLabel(this.resourceRepository().getString("DATABASE_TYPE_COMMENT_TEXT_FIELD"));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic("DATABASE_TYPE_COMMENT_TEXT_FIELD"));
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 0, 0, 5);
		panel.add(label, constraints);

		JTextField commentTextField = this.buildCommentTextField();
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		panel.add(commentTextField, constraints);

		label.setLabelFor(commentTextField);

		return panel;
	}

	private JTextField buildCommentTextField() {
		return new JTextField(this.buildCommentDocument(), null, 0);
	}

	private Document buildCommentDocument() {
		return new DocumentAdapter(this.buildCommentAdapter());
	}

	private PropertyAspectAdapter buildCommentAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), AbstractNodeModel.COMMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((DatabaseType) this.subject).getComment();
			}
			protected void setValueOnSubject(Object value) {
				((DatabaseType) this.subject).setComment((String) value);
			}
		};
	}

}
