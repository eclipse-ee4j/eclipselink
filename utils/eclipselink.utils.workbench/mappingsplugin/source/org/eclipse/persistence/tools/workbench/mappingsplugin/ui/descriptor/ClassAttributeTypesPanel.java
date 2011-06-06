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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;



final class ClassAttributeTypesPanel 
	extends AbstractPanel
{
	// **************** Variables *********************************************
	
	private ValueModel attributeHolder;
	private PropertyValueModel attributeTypeHolder;
	private PropertyValueModel attributeTypeDimensionalityHolder;		
	private PropertyValueModel attributeValueTypeHolder;
	private PropertyValueModel attributeKetTypeHolder;
	private PropertyValueModel attributeItemTypeHolder;
	
	private ClassRepositoryHolder classRepositoryHolder;
	
	private JLabel valueTypeLabel;
	private ClassChooserPanel valueTypeClassChooserPanel;
	
	private JLabel keyTypeLabel;
	private ClassChooserPanel keyTypeClassChooserPanel;
	
	private JLabel itemTypeLabel;
	private ClassChooserPanel itemTypeClassChooserPanel;
	
	
	
	// **************** Constructors ******************************************
	
	ClassAttributeTypesPanel(ValueModel attributeHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.attributeHolder = attributeHolder;
		this.attributeTypeHolder = this.buildAttributeTypeAdapter();
		this.attributeTypeDimensionalityHolder = this.buildAttributeTypeDimensionalityAdapter();
		this.attributeValueTypeHolder = this.buildAttributeValueTypeHolder();
		this.attributeKetTypeHolder = this.buildAttributeKeyTypeHolder();
		this.attributeItemTypeHolder = this.buildAttributeItemTypeHolder();
		this.classRepositoryHolder = this.buildClassRepositoryHolder();
		initializeLayout();
	}
	
	// **************** Initialization ****************************************
	
	private PropertyValueModel buildAttributeTypeAdapter() {
		PropertyValueModel adapter = 
			new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
					return ((MWClassAttribute) this.subject).getType();
			}
			protected void setValueOnSubject(Object value) {
					((MWClassAttribute) this.subject).setType((MWClass) value);
			}
		};
		
		adapter.addPropertyChangeListener(ValueModel.VALUE, this.buildAttributeListener());
		return new ValuePropertyPropertyValueModelAdapter(adapter, MWClass.NAME_PROPERTY);
	}

	private PropertyValueModel buildAttributeTypeDimensionalityAdapter() {
		PropertyValueModel adapter = 
			new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.DIMENSIONALITY_PROPERTY) {
				protected Object getValueFromSubject() {
					return new Integer(((MWClassAttribute) this.subject).getDimensionality());
				}
				
				protected void setValueOnSubject(Object value) {
					((MWClassAttribute) this.subject).setDimensionality(((Integer) value).intValue());
				}
			};
		
		adapter.addPropertyChangeListener(ValueModel.VALUE, this.buildAttributeListener());
		return adapter;
	}
	
	private PropertyChangeListener buildAttributeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				ClassAttributeTypesPanel.this.updateAllTypeComponents();
			}
		};
	}
	
	private PropertyValueModel buildAttributeValueTypeHolder() {
		PropertyValueModel attributeTypeValueHolder =
			new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.VALUE_TYPE_PROPERTY) {
				protected Object getValueFromSubject() {
					return ((MWClassAttribute) this.subject).getValueType();
				}
				
				protected void setValueOnSubject(Object value) {
					((MWClassAttribute) this.subject).setValueType((MWClass) value);
				}
			};
		attributeTypeValueHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildAttributeValueTypeListener());
		return attributeTypeValueHolder;
	}
	
	private PropertyChangeListener buildAttributeValueTypeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				ClassAttributeTypesPanel.this.updateAllTypeComponents();
			}
		};
	}
	
	private PropertyValueModel buildAttributeKeyTypeHolder() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.KEY_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassAttribute) this.subject).getKeyType();
			}
			protected void setValueOnSubject(Object value) {
				((MWClassAttribute) this.subject).setKeyType((MWClass) value);
			}
		};
	}
	
	private PropertyValueModel buildAttributeItemTypeHolder() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.ITEM_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassAttribute) this.subject).getItemType();
			}
	
			protected void setValueOnSubject(Object value) {
					((MWClassAttribute) this.subject).setItemType((MWClass) value);
			}
		};
	}
	
	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return ClassAttributeTypesPanel.this.getAttribute().getRepository();
	}
		};
	}
	
	private void initializeLayout() {				
		//
		//	Action:	Create the panel
		//
		this.setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("TYPE_PANEL_TITLE")));
				
		GridBagConstraints constraints = new GridBagConstraints();
		
		Collection components = new ArrayList();
		//
		//	Action:	Create the type label
		//
		JLabel typeLabel = SwingComponentFactory.buildLabel("TYPE_LABEL", resourceRepository());
		components.add(typeLabel);
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 5, 0, 5);
		this.add(typeLabel, constraints);
		
		//
		//	Action:	Create the type chooser
		//
		ClassChooserPanel typeChooserPanel = 
			ClassChooserTools.buildPanel(
				this.attributeTypeHolder,
						this.classRepositoryHolder,
						ClassChooserTools.buildDeclarableNonVoidFilter(),
						typeLabel,
						this.getWorkbenchContextHolder()
		);
		components.add(typeChooserPanel);
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 5);
		this.add(typeChooserPanel, constraints);
		
		
		//
		//	Action:	Create the dimensionality label
		//
		JLabel dimensionalityLabel = 
			SwingComponentFactory.buildLabel("DIMENSIONALITY_LABEL", this.resourceRepository());
		components.add(dimensionalityLabel);
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 5, 5, 0);
		this.add(dimensionalityLabel, constraints);
		
		//
		//	Action:	Create the dimensionality spin button
		//
		JSpinner dimensionalitySpinButton = this.buildDimensionalitySpinner();
		components.add(dimensionalitySpinButton);
		dimensionalityLabel.setLabelFor(dimensionalitySpinButton);
		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
		this.add(dimensionalitySpinButton, constraints);
		
		//
		//	Action: Create the value type label
		//
		this.valueTypeLabel = SwingComponentFactory.buildLabel("VALUE_TYPE_LABEL", this.resourceRepository());
		this.valueTypeLabel.setVisible(false);
		components.add(this.valueTypeLabel);
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 5, 5);
		this.add(this.valueTypeLabel, constraints);
		
		//
		//	Action: Create the value type chooser
		//
		this.valueTypeClassChooserPanel = 
			ClassChooserTools.buildPanel(
				this.attributeValueTypeHolder,
						this.classRepositoryHolder,
						ClassChooserTools.buildDeclarableReferenceFilter(),
						this.valueTypeLabel,
						this.getWorkbenchContextHolder()
		);
		this.valueTypeClassChooserPanel.setVisible(false);
		components.add(this.valueTypeClassChooserPanel);
		constraints.gridx		= 1;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 5);
		this.add(this.valueTypeClassChooserPanel, constraints);
				
		//
		//	Action: Create the key type label
		//
		this.keyTypeLabel = SwingComponentFactory.buildLabel("MAP_KEY_TYPE_LABEL", this.resourceRepository());
		this.keyTypeLabel.setVisible(false);
		components.add(this.keyTypeLabel);
		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 5, 5);
		this.add(this.keyTypeLabel, constraints);
		
		//
		//	Action: Create the key type chooser
		//
		this.keyTypeClassChooserPanel = 
			ClassChooserTools.buildPanel(
				this.attributeKetTypeHolder,
						this.classRepositoryHolder,
						ClassChooserTools.buildDeclarableReferenceFilter(),
						this.keyTypeLabel,
						this.getWorkbenchContextHolder()
		);
		this.keyTypeClassChooserPanel.setVisible(false);
		components.add(this.keyTypeClassChooserPanel);
		constraints.gridx		= 1;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 5);
		this.add(this.keyTypeClassChooserPanel, constraints);
				
		//
		//	Action: Create the item type label
		//
		this.itemTypeLabel = SwingComponentFactory.buildLabel("ELEMENT_TYPE_LABEL", resourceRepository());
		this.itemTypeLabel.setVisible(false);
		components.add(this.itemTypeLabel);
		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 5, 5);
		this.add(this.itemTypeLabel, constraints);
		
		//
		//	Action: Create the item type chooser
		//
		this.itemTypeClassChooserPanel = 
			ClassChooserTools.buildPanel(
				this.attributeItemTypeHolder,
						this.classRepositoryHolder,
						ClassChooserTools.buildDeclarableReferenceFilter(),
						this.itemTypeLabel,
						this.getWorkbenchContextHolder()
		);
		this.itemTypeClassChooserPanel.setVisible(false);
		components.add(this.itemTypeClassChooserPanel);
		constraints.gridx		= 1;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 5);
		this.add(this.itemTypeClassChooserPanel, constraints);
					
		new ComponentEnabler(this.buildAttributeTypeBooleanHolder(), components);
		this.addHelpTopicId(this, this.helpTopicId());
	}
	
	private ValueModel buildAttributeTypeBooleanHolder() {
		return new TransformationPropertyValueModel(this.attributeTypeHolder) {
			protected Object transform(Object value) {
				return value == null ? Boolean.FALSE : Boolean.TRUE;
			}
		};
	}
	
	private String helpTopicId() {
		return "descriptor.classInfo.attributes.general.typeInfo";
			}
	
	
	//***************** Dimensionality ****************************************

	private JSpinner buildDimensionalitySpinner() {
		JSpinner spinner = new JSpinner(this.buildDimensionalitySpinnerModel());
		spinner.setPreferredSize(new Dimension(50, spinner.getPreferredSize().height));
		
		return spinner;
	}
	
	private SpinnerModel buildDimensionalitySpinnerModel() {
		return new NumberSpinnerModelAdapter(this.attributeTypeDimensionalityHolder, new Integer(0), null, new Integer(1), new Integer(0));
	}
	
	
	// **************** Behavior **********************************************
	
	void updateAllTypeComponents() {
		this.updateValueTypeComponents();
		this.updateItemTypeComponents();
		this.updateKeyTypeComponents();
			}			
		
	private void updateValueTypeComponents() {
		boolean canChooseValueType = this.getAttribute() != null && this.getAttribute().canHaveValueType();
		this.valueTypeLabel.setVisible(canChooseValueType);
		this.valueTypeClassChooserPanel.setVisible(canChooseValueType);
	}
		
	private void updateItemTypeComponents() {
		MWClassAttribute attribute = this.getAttribute();
		
		boolean canChooseItemType = attribute != null && attribute.canHaveItemType();
		this.itemTypeLabel.setVisible(canChooseItemType);
		this.itemTypeClassChooserPanel.setVisible(canChooseItemType);
	
		if (attribute != null && attribute.canHaveMapKeyAndValueTypes()) {
				this.itemTypeLabel.setText(resourceRepository().getString("MAP_VALUE_TYPE_LABEL"));
				this.itemTypeLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("MAP_VALUE_TYPE_LABEL"));
			}
		else {
			this.itemTypeLabel.setText(resourceRepository().getString("ELEMENT_TYPE_LABEL"));
			this.itemTypeLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("ELEMENT_TYPE_LABEL"));
		}
	}

	private void updateKeyTypeComponents() {
		boolean canChooseKeyType = this.getAttribute() != null && this.getAttribute().canHaveKeyType();
		this.keyTypeLabel.setVisible(canChooseKeyType);
		this.keyTypeClassChooserPanel.setVisible(canChooseKeyType);
	}
	
	
	// **************** Convenience *******************************************
	
	MWClassAttribute getAttribute() {
		return (MWClassAttribute) this.attributeHolder.getValue();
	}
}
