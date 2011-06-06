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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;



public final class ConverterPanel 
	extends AbstractPanel 
{
	// **************** Variables *********************************************
	
	/** Holds the current converter */
	private PropertyValueModel converterHolder;
	private PropertyValueModel converterTypeHolder;
	
	
	public ConverterPanel(PropertyValueModel converterHolder, ConverterSetter converterSetter, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.initialize(converterHolder, converterSetter);
		this.initializeLayout();
	}
	
	private void initialize(PropertyValueModel converterHolder, ConverterSetter converterSetter) {
		this.converterHolder = converterHolder;
		this.converterTypeHolder = buildConverterTypeHolder(converterHolder, converterSetter);
	}
	
	private PropertyValueModel buildConverterTypeHolder(PropertyValueModel converterHolder, final ConverterSetter converterSetter) {
		return new PropertyAspectAdapter(converterHolder, converterSetter.converterTypePropertyString()) {
			protected Object getValueFromSubject() {
				return converterSetter.getType();
			}
			
			protected void setValueOnSubject(Object value) {
				String converterType = (String) value;
				if (converterType.equals(MWConverter.NO_CONVERTER)) {
					if (!converterSetter.getType().equals(MWConverter.NO_CONVERTER)){
						converterSetter.setNullConverter();
					}
				}
				else if (converterType.equals(MWConverter.OBJECT_TYPE_CONVERTER)) {
					if (!converterSetter.getType().equals(MWConverter.OBJECT_TYPE_CONVERTER)){
						converterSetter.setObjectTypeConverter();
					}
				}
				else if (converterType.equals(MWConverter.SERIALIZED_OBJECT_CONVERTER)) {
					if (!converterSetter.getType().equals(MWConverter.SERIALIZED_OBJECT_CONVERTER)) {
						converterSetter.setSerializedObjectConverter();
					}
				}
				else if (converterType.equals(MWConverter.TYPE_CONVERSION_CONVERTER)) {
					if (!converterSetter.getType().equals(MWConverter.TYPE_CONVERSION_CONVERTER)) {
						converterSetter.setTypeConversionConverter();
					}
				}
			}
		};
	}
	
	private void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		JRadioButton noConverterRadioButton = this.buildNoConverterRadioButton();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 10, 0, 5);
		this.add(noConverterRadioButton, constraints);
		
		JRadioButton serializedObjectConverterRadioButton = 
			this.buildSerializedObjectConverterRadioButton();
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(3, 10, 0, 5);
		this.add(serializedObjectConverterRadioButton, constraints);
		
		JRadioButton typeConversionConverterRadioButton = 
			this.buildTypeConversionConverterRadioButton();
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(3, 10, 0, 5);
		this.add(typeConversionConverterRadioButton, constraints);
		
		AbstractPanel typeConversionConverterPanel = 
			this.buildTypeConversionConverterPanel();
		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(3, 36, 0, 5);
		this.add(typeConversionConverterPanel, constraints);
		this.addPaneForAlignment(typeConversionConverterPanel);
		
		JRadioButton objectTypeConverterRadioButton = 
			this.buildObjectTypeConverterRadioButton();
		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(8, 10, 0, 5);
		this.add(objectTypeConverterRadioButton, constraints);
		
		AbstractPanel objectTypeConverterPanel = 
			this.buildObjectTypeConverterPanel();
		constraints.gridx		= 0;
		constraints.gridy		= 5;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(3, 36, 5, 5);
		this.add(objectTypeConverterPanel, constraints);
		this.addPaneForAlignment(objectTypeConverterPanel);
		
		this.addHelpTopicId(this, this.helpTopicId());
	}
	
	private JRadioButton buildNoConverterRadioButton() {
		return 
			this.buildRadioButton(
				"NO_CONVERTER_RADIO_BUTTON", 
				this.buildConverterRadioButtonModelAdapter(MWConverter.NO_CONVERTER, true)
			);
	}
	
	private JRadioButton buildSerializedObjectConverterRadioButton() {
		JRadioButton button = 
			this.buildRadioButton(
				"SERIALIZED_OBJECT_CONVERTER_RADIO_BUTTON", 
				this.buildConverterRadioButtonModelAdapter(MWConverter.SERIALIZED_OBJECT_CONVERTER, false)
			);
		this.addHelpTopicId(button, this.helpTopicId() + ".serialized");
		return button;
	}
	
	private JRadioButton buildTypeConversionConverterRadioButton() {
		JRadioButton button = 
			this.buildRadioButton(
				"TYPE_CONVERSION_CONVERTER_RADIO_BUTTON", 
				this.buildConverterRadioButtonModelAdapter(MWConverter.TYPE_CONVERSION_CONVERTER, false)
			);
		this.addHelpTopicId(button, this.helpTopicId() + ".typeConverter");
		return button;
	}
	
	private AbstractPanel buildTypeConversionConverterPanel() {
		AbstractPanel panel = 
			new TypeConversionConverterPanel(
				this.buildTypeConversionConverterHolder(), 
				this.getWorkbenchContextHolder()
			);
		this.buildTypeConversionConverterPanelEnabler(panel);
		return panel;
	}
	
	private PropertyValueModel buildTypeConversionConverterHolder() {
		return new FilteringPropertyValueModel(this.converterHolder) {
			protected boolean accept(Object value) {
				return (value instanceof MWTypeConversionConverter);
			}
		};
	}
	
	private ComponentEnabler buildTypeConversionConverterPanelEnabler(final AbstractPanel panel) {
		return new ComponentEnabler(this.buildIsTypeConversionConverterHolder(), panel);
	}
	
	private ValueModel buildIsTypeConversionConverterHolder() {
		return new PropertyAspectAdapter(this.converterHolder) {
			protected Object getValueFromSubject() {
				return new Boolean(this.subject instanceof MWTypeConversionConverter);
			}
		};
	}
	
	private JRadioButton buildObjectTypeConverterRadioButton() {
		JRadioButton button =
			this.buildRadioButton(
				"OBJECT_TYPE_CONVERTER_RADIO_BUTTON", 
				buildConverterRadioButtonModelAdapter(MWConverter.OBJECT_TYPE_CONVERTER, false)
			);
		this.addHelpTopicId(button, this.helpTopicId() + ".objectType");
		return button;
	}
	
	private AbstractPanel buildObjectTypeConverterPanel() {
		AbstractPanel panel =
			new ObjectTypeConverterPanel(
				this.buildObjectTypeConverterHolder(), 
				this.getWorkbenchContextHolder()
			);
		this.buildObjectTypeConverterPanelEnabler(panel);
		return panel;
	}

	private PropertyValueModel buildObjectTypeConverterHolder() {
		return new FilteringPropertyValueModel(this.converterHolder) {
			protected boolean accept(Object value) {
				return (value instanceof MWObjectTypeConverter);
			}
		};
	}
	
	private ComponentEnabler buildObjectTypeConverterPanelEnabler(final AbstractPanel panel) {
		return new ComponentEnabler(this.buildIsObjectTypeConverterHolder(), panel);
	}
	
	private ValueModel buildIsObjectTypeConverterHolder() {
		return new PropertyAspectAdapter(this.converterHolder) {
			protected Object getValueFromSubject() {
				return new Boolean(this.subject instanceof MWObjectTypeConverter);
			}
		};
	}
	
	private RadioButtonModelAdapter buildConverterRadioButtonModelAdapter(String propertyName, boolean defaultValue) {
		return new RadioButtonModelAdapter(this.converterTypeHolder, propertyName, defaultValue);
	}
	
	protected String helpTopicId() {
		return "mapping.converter";
	}
	
	
	public interface ConverterSetter 
	{	
		String getType();
		
		void setNullConverter();
		void setObjectTypeConverter();
		void setSerializedObjectConverter();
		void setTypeConversionConverter();
		
		String converterTypePropertyString();
	}
}
