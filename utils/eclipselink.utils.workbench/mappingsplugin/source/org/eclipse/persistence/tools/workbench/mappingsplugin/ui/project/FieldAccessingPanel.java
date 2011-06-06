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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


final class FieldAccessingPanel extends AbstractSubjectPanel 
{	
	FieldAccessingPanel(ValueModel projectDefaultsHolder, ApplicationContext context) {
		super(projectDefaultsHolder, context);
	}
	
	
	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("FIELD_ACCESSING_PANEL_BORDER_TEXT")));
				
		JRadioButton methodAccessingRadioButton = buildMethodAccessingRadioButton();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 5, 0, 5);
		add(methodAccessingRadioButton, constraints);
		
		JRadioButton fieldAccessingRadioButton = buildFieldAccessingRadioButton();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 5, 0, 5);
		add(fieldAccessingRadioButton, constraints);
	}
	
	private JRadioButton buildMethodAccessingRadioButton() {
		return buildRadioButton("METHOD_ACCESSING_RADIO_BUTTON_TEXT", buildRadioButtonModelAdapter(this.buildMethodAccessingHolder(), Boolean.TRUE));
	}

	private JRadioButton buildFieldAccessingRadioButton() {
		return buildRadioButton("FIELD_ACCESSING_RADIO_BUTTON_TEXT", buildRadioButtonModelAdapter(this.buildMethodAccessingHolder(), Boolean.FALSE));
	}
	
	private PropertyValueModel buildMethodAccessingHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWProjectDefaultsPolicy.METHOD_ACCESSING_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWProjectDefaultsPolicy)subject).isMethodAccessing());
			}
			protected void setValueOnSubject(Object value) {
				((MWProjectDefaultsPolicy) subject).setMethodAccessing(((Boolean) value).booleanValue());
			}
		};
	}
	
	private ButtonModel buildRadioButtonModelAdapter(PropertyValueModel methodAccessingHolder, Boolean methodAccessing) {
		return new RadioButtonModelAdapter(methodAccessingHolder, methodAccessing);
	}
}
