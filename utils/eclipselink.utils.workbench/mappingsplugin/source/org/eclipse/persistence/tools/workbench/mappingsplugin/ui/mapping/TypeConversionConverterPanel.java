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

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Iterator;
import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.TypeDeclarationCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


final class TypeConversionConverterPanel extends AbstractSubjectPanel {
	
	TypeConversionConverterPanel(PropertyValueModel typeConversionConverterHolder, WorkbenchContextHolder context) {
		super(typeConversionConverterHolder, context);
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
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);
		this.add(attributeTypeWidget, constraints);

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
		chooser.setRenderer(buildTypeDeclarationListCellRenderer());
		return chooser;
	}

	private DefaultListChooserDialog.Builder buildDataTypeChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("DATA_TYPE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("DATA_TYPE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildTypeDeclarationStringConverter());
		return builder;
	}


	private ComboBoxModel buildDataTypeComboBoxModel() {
		return new ComboBoxModelAdapter(buildTypesCollectionModel(), buildDataTypeHolder()); 
	}
	
	private PropertyValueModel buildDataTypeHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWTypeConversionConverter.DATA_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTypeConversionConverter) subject).getDataType();
			}
			protected void setValueOnSubject(Object value) {
				((MWTypeConversionConverter) subject).setDataType((MWTypeDeclaration) value);
			}
		};
	}

	private CollectionValueModel buildTypesCollectionModel() {
		return new CollectionAspectAdapter(getSubjectHolder()) {
			protected Iterator getValueFromSubject() {
				return ((MWTypeConversionConverter) subject).getBasicTypes().iterator();
			}
		};
	}
	
	private StringConverter buildTypeDeclarationStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWTypeDeclaration) o).displayStringWithPackage();
			}
		};
	}
	
	private ListCellRenderer buildTypeDeclarationListCellRenderer() {
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
		chooser.setRenderer(buildTypeDeclarationListCellRenderer());
		return chooser;
	}

	private DefaultListChooserDialog.Builder buildAttributeTypeChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("ATTRIBUTE_TYPE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("ATTRIBUTE_TYPE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildTypeDeclarationStringConverter());
		return builder;
	}


	private ComboBoxModel buildAttributeTypeComboBoxModel() {
		return new ComboBoxModelAdapter(buildTypesCollectionModel(), buildAttributeTypeHolder()); 
	}
	
	private PropertyValueModel buildAttributeTypeHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWTypeConversionConverter.ATTRIBUTE_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTypeConversionConverter) subject).getAttributeType();
			}
			protected void setValueOnSubject(Object value) {
				((MWTypeConversionConverter) subject).setAttributeType((MWTypeDeclaration) value);
			}

		};
	}
	
	protected String helpTopicId() {
		return "mapping.converter.typeConverter";
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		for (int i = this.getComponentCount() - 1; i >= 0; i -- ) {
			this.getComponent(i).setEnabled(enabled);
		}
	}
}
