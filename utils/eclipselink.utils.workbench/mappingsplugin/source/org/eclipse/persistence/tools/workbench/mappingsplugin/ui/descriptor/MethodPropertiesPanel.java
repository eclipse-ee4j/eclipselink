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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;



final class MethodPropertiesPanel extends AbstractPanel {
	
	private PropertyValueModel methodHolder;
	private PropertyValueModel returnTypeHolder;
	
	MethodPropertiesPanel(PropertyValueModel methodHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.methodHolder = methodHolder;
		this.returnTypeHolder = buildReturnTypeHolder();
		initializeLayout();
	}


	private PropertyValueModel buildReturnTypeHolder() {
		PropertyValueModel adapter = new PropertyAspectAdapter(this.methodHolder, MWMethod.RETURN_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				if (((MWMethod) subject).isConstructor()) {
					return null;
				}
				return ((MWMethod) subject).getReturnType();
			}
			protected void setValueOnSubject(Object value) {
				((MWMethod) subject).setReturnType((MWClass) value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(adapter, MWClass.NAME_PROPERTY);
	}
	
	protected String helpTopicId() {
		return "descriptor.classInfo.methods.properties";	
	}
	
	private void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		// modifiers panel
		MethodModifiersPanel modifiersPanel = new MethodModifiersPanel(this.methodHolder, getApplicationContext());
		addHelpTopicId(modifiersPanel, helpTopicId() + ".modifiers");
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);
		this.add(modifiersPanel, constraints);
		
		
		// return type panel
		JPanel typePanel = new JPanel(new GridBagLayout());
		typePanel.setBorder(buildTitledBorder("RETURN_TYPE_PANEL_TITLE"));
		constraints.gridx 		= 0;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		add(typePanel, constraints);
			
			// return type label
			JLabel returnTypeLabel = buildLabel("RETURN_TYPE_LABEL");
			returnTypeLabel.setEnabled(false);
			this.methodHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildMethodListener(returnTypeLabel));
			constraints.gridx 		= 0;
			constraints.gridy 		= 0;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 0;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.NONE;
			constraints.anchor 		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(0, 5, 0, 0);
			typePanel.add(returnTypeLabel, constraints);
			
			// return type text field
			ClassChooserPanel returnTypeClassChooserPanel = ClassChooserTools.buildPanel(
							this.returnTypeHolder,
							this.buildClassRepositoryHolder(),
							ClassChooserTools.buildDeclarableFilter(),
							returnTypeLabel,
							this.getWorkbenchContextHolder()
			);
			this.methodHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildMethodListener(returnTypeClassChooserPanel));
			returnTypeClassChooserPanel.setEnabled(false);
			constraints.gridx 		= 1;
			constraints.gridy 		= 0;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 1;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.HORIZONTAL;
			constraints.anchor 		= GridBagConstraints.CENTER;
			constraints.insets 		= new Insets(0, 5, 0, 5);
			typePanel.add(returnTypeClassChooserPanel, constraints);

			// dimensionality label
			JLabel dimensionalityLabel = buildLabel("DIMENSIONALITY_LABEL");
			dimensionalityLabel.setEnabled(false);
			this.methodHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildMethodListener(dimensionalityLabel));
			this.returnTypeHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildReturnTypeListener(dimensionalityLabel));
			constraints.gridx 		= 0;
			constraints.gridy 		= 1;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 0;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.NONE;
			constraints.anchor 		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(5, 5, 5, 0);
			typePanel.add(dimensionalityLabel, constraints);
			
			//	dimensionality spin button
			JSpinner arraySizeSpinButton = buildDimensionalitySpinner();
			dimensionalityLabel.setLabelFor(arraySizeSpinButton);
			constraints.gridx 		= 1;
			constraints.gridy 		= 1;
			constraints.gridwidth 	= 1;
			constraints.gridheight 	= 1;
			constraints.weightx 	= 0;
			constraints.weighty 	= 0;
			constraints.fill 		= GridBagConstraints.NONE;
			constraints.anchor 		= GridBagConstraints.LINE_START;
			constraints.insets 		= new Insets(5, 5, 5, 5);
			typePanel.add(arraySizeSpinButton, constraints);

		addHelpTopicId(typePanel, helpTopicId() + ".typeInfo");

		// Create the panel
		JPanel parametersPanel = new MethodParametersPanel(this.methodHolder,  getWorkbenchContextHolder());
		parametersPanel.setBorder(BorderFactory.createCompoundBorder(buildTitledBorder("PARAMETERS_TABLE_LABEL"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 0, 0, 0);
		add(parametersPanel, constraints);

		addHelpTopicId(parametersPanel, helpTopicId() + ".parameters");
		
		addHelpTopicId(this, helpTopicId());

	}
	
	
	// **************** common ************************************************
	
	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return MethodPropertiesPanel.this.getMethod().getRepository();
			}
		};
	}
	
	//*********** dimensionality **********

	private JSpinner buildDimensionalitySpinner() {
		JSpinner spinner = new JSpinner(buildDimensionalitySpinnerModel());
		spinner.setPreferredSize(new Dimension(50, spinner.getPreferredSize().height));
		spinner.setEnabled(false);
		this.methodHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildMethodListener(spinner));
		this.returnTypeHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildReturnTypeListener(spinner));
		
		return spinner;
	}
	
	private SpinnerModel buildDimensionalitySpinnerModel() {
		return new NumberSpinnerModelAdapter(buildDimensionalityHolder(), new Integer(0), null, new Integer(1), new Integer(0));
	}
	
	private PropertyValueModel buildDimensionalityHolder() {
		return new PropertyAspectAdapter(this.methodHolder, MWMethod.RETURN_TYPE_DIMENSIONALITY_PROPERTY) {
			protected Object getValueFromSubject() {
				if (((MWMethod) subject).isConstructor()) {
					return new Integer(0);
				}
				return new Integer(((MWMethod) subject).getReturnTypeDimensionality());
			}
			protected void setValueOnSubject(Object value) {
				((MWMethod) subject).setReturnTypeDimensionality(((Integer) value).intValue());
			}
		};
	}


	private PropertyChangeListener buildMethodListener(final Component component) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(getMethod() != null && !getMethod().isConstructor());	
			}
		};	
	}

	private PropertyChangeListener buildReturnTypeListener(final Component component) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(evt.getNewValue() == null ? false : !((MWClass) evt.getNewValue()).isVoid());	
			}
		};	
	}

	//*********** convenience methods **********

	private MWMethod getMethod() {
		return (MWMethod) this.methodHolder.getValue();
	}

}
