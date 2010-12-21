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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWNullValuePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.TypeDeclarationCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public final class NullValuePolicyPanel 
	extends AbstractSubjectPanel 
{	
	// **************** Variables *********************************************
	
	private PropertyValueModel nullValuePolicyHolder;
	
	
	// **************** Constructors ******************************************
	
	public NullValuePolicyPanel(PropertyValueModel mappingHolder, WorkbenchContextHolder contextHolder) {
		super(mappingHolder, contextHolder);
	}
	
	protected void initialize(ValueModel subjectHolder) {
		super.initialize(subjectHolder);
		this.nullValuePolicyHolder = this.buildNullValuePolicyHolder();
	}
	
	protected PropertyValueModel buildNullValuePolicyHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWDirectMapping.USES_NULL_VALUE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDirectMapping) subject).getNullValuePolicy();
			}
		};
	}
	
	private PropertyChangeListener buildUsesNullValueListener(final Component component) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(usesNullValue());
			}
		};
	}
	
	private boolean usesNullValue() {
		return subject() == null ? false : ((MWDirectMapping) subject()).usesNullValue();
	}

	protected void initializeLayout() {
		
		GridBagConstraints constraints = new GridBagConstraints();

		//check box
		JCheckBox useNullValueCheckBox = buildCheckBox("DEFAULT_NULL_VALUE_CHECK_BOX", buildUseNullValueCheckBoxModelAdapter());

		// Create the Null Value panel
		Pane nullValuePanel = new Pane(new GridBagLayout());
		this.nullValuePolicyHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildUsesNullValueListener(nullValuePanel));

		// Create the group box
		GroupBox groupBox = new GroupBox(useNullValueCheckBox, nullValuePanel);
		
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
		add(groupBox, constraints);

		// Initialize type label
		JComponent nullValueTypeWidgets = buildLabeledComponent(
			"DEFAULT_NULL_VALUE_TYPE_LABEL",
			buildNullValueTypeListChooser()
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
		nullValuePanel.add(nullValueTypeWidgets, constraints);

		// Initialize value label
		JComponent nullValueValueWidgets = buildLabeledComponent(
			"DEFAULT_NULL_VALUE_VALUE_LABEL",
			buildNullValueTextField()
		);
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);
		nullValuePanel.add(nullValueValueWidgets, constraints);

		addHelpTopicId(this, "mapping.direct.nullValue");
	}
	
	
	// *************** use null value ************
	
	private CheckBoxModelAdapter buildUseNullValueCheckBoxModelAdapter() {
		return new CheckBoxModelAdapter(buildUseNullValueHolder());
	}
	
	private PropertyValueModel buildUseNullValueHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWDirectMapping.USES_NULL_VALUE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWDirectMapping) subject).usesNullValue());
			}
			protected void setValueOnSubject(Object value) {
				((MWDirectMapping) subject).setUseNullValue(((Boolean) value).booleanValue());
			}
		};
	}
	
	// ************ type ************
	
	private ListChooser buildNullValueTypeListChooser() {
		ListChooser chooser = 
			new DefaultListChooser(
				this.buildNullValueComboBoxModelAdapter(), 
				this.getWorkbenchContextHolder(),
				this.buildNullValueTypeChooserDialogBuilder()
			);
		chooser.setRenderer(buildMWClassListCellRenderer());
		return chooser;
	}
	
	private DefaultListChooserDialog.Builder buildNullValueTypeChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("NULL_VALUE_TYPE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("NULL_VALUE_TYPE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildNullValueTypeStringConverter());
		return builder;
	}
	

	private ComboBoxModelAdapter buildNullValueComboBoxModelAdapter() {
		return new ComboBoxModelAdapter(buildNullValueTypeCollectionHolder(), buildNullValueTypeHolder());
	}
	
	private CollectionValueModel buildNullValueTypeCollectionHolder() {
		return new CollectionAspectAdapter(getSubjectHolder()) {
			protected Iterator getValueFromSubject() {
				return ((MWDirectMapping) subject).buildBasicTypes().iterator();
			}
		};
	}
	
	private PropertyValueModel buildNullValueTypeHolder() {
		return new PropertyAspectAdapter(this.nullValuePolicyHolder, MWNullValuePolicy.NULL_VALUE_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWNullValuePolicy) subject).getNullValueType();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWNullValuePolicy) subject).setNullValueType((MWTypeDeclaration) value);
			}
		};
	}
	
	private StringConverter buildNullValueTypeStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return ((MWTypeDeclaration) o).typeName();
			}
		};		
	}
	
	private ListCellRenderer buildMWClassListCellRenderer() {
		return new AdaptableListCellRenderer(new TypeDeclarationCellRendererAdapter(resourceRepository()));
	}
	
	
	// ************ value ************
	
	private JTextField buildNullValueTextField() {
		JTextField textField = new JTextField();
		//TODO use a Document for the DocumentAdapter?
		textField.setDocument(new DocumentAdapter(buildNullValueHolder()));
		
		return textField;
	}
	
	private PropertyValueModel buildNullValueHolder() {
		return new PropertyAspectAdapter(this.nullValuePolicyHolder, MWNullValuePolicy.NULL_VALUE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWNullValuePolicy) subject).getNullValue();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWNullValuePolicy) subject).setNullValue((String) value);
			}
		};
	}
}
