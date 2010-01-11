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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWLiteralArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWNullArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryParameterArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;



/**
 *   This is used for the right hand panel of the BasicExpressionPanel
 *   The first argument can only be a MWQueryableArgument whereas the secondArgument can be a 
 *   MWQueryableArgument, MWQueryParameterArgument, MWLiteralArgument
 */
final class SecondArgumentPanel 
	extends AbstractPanel
{

	private PropertyValueModel argumentHolder;
	private PropertyValueModel basicExpressionHolder;
	private PropertyValueModel argumentTypeHolder;
	
	private JRadioButton literalRadioButton;
	private JRadioButton queryKeyRadioButton;
	private JRadioButton parameterRadioButton;
		
	private JPanel emptyPanel; //for holding on to the activeArgumentPanel
	private ArgumentPanel activeArgumentPanel;
	
	private ParameterArgumentPanel parameterArgumentPanel;
		
	private QueryableArgumentPanel queryKeyArgumentPanel;
		
	private LiteralArgumentPanel literalArgumentPanel;
	
	private Map argumentTypeRadioButtonMap;
	private Map argumentTypeArgumentPanelMap;
	
	SecondArgumentPanel(PropertyValueModel argumentHolder,  PropertyValueModel basicExpressionHolder, WorkbenchContextHolder contextHolder) 
	{
		super(contextHolder);
		this.argumentHolder = argumentHolder;
		this.basicExpressionHolder = basicExpressionHolder;
		this.argumentTypeHolder = buildArgumentTypeHolder();
		initializeLayout();
		initializeMaps();
	}
	
	private void initializeMaps()
	{
		this.argumentTypeRadioButtonMap = new Hashtable();
		this.argumentTypeRadioButtonMap.put(MWQueryableArgument.class, this.queryKeyRadioButton);
		this.argumentTypeRadioButtonMap.put(MWLiteralArgument.class, this.literalRadioButton);
		this.argumentTypeRadioButtonMap.put(MWQueryParameterArgument.class, this.parameterRadioButton);

		this.argumentTypeArgumentPanelMap = new Hashtable();
		this.argumentTypeArgumentPanelMap.put(MWQueryableArgument.class, this.queryKeyArgumentPanel);
		this.argumentTypeArgumentPanelMap.put(MWLiteralArgument.class, this.literalArgumentPanel);
		this.argumentTypeArgumentPanelMap.put(MWNullArgument.class, this.literalArgumentPanel);
		this.argumentTypeArgumentPanelMap.put(MWQueryParameterArgument.class, this.parameterArgumentPanel);

	}
	private MWArgument getArgument() 
	{
		return (MWArgument) this.argumentHolder.getValue();
	}

	private ValueModel buildNullArgumentBooleanHolder() {
		return new PropertyAspectAdapter(this.argumentHolder) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(!(this.subject instanceof MWNullArgument));
			}
		};	
	}
	
	private void initializeLayout() {
		this.argumentHolder.addPropertyChangeListener(ValueModel.VALUE, buildArgumentListener());

		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
	
		Collection enablingComponents = new ArrayList();
		JPanel radioButtonPanel = new AccessibleTitledPanel(new GridBagLayout());
				
			int gridY = 0;
			this.literalRadioButton = buildRadioButton("LITERAL_RADIO_BUTTON_ON_SECOND_ARGUMENT_PANEL", buildRadioButtonModelAdapter(MWArgument.LITERAL_TYPE, true));	
			enablingComponents.add(this.literalRadioButton);

			constraints.gridx		= 0;
			constraints.gridy		= gridY++;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.NORTHWEST;
			constraints.insets		= new Insets(0,5,0,5);
			radioButtonPanel.add(this.literalRadioButton, constraints);

			this.queryKeyRadioButton = buildRadioButton("QUERY_KEY_RADIO_BUTTON_ON_SECOND_ARGUMENT_PANEL", buildRadioButtonModelAdapter(MWArgument.QUERY_KEY_TYPE, false));	
			enablingComponents.add(this.queryKeyRadioButton);

			constraints.gridx		= 0;
			constraints.gridy		= gridY++;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.NORTHWEST;
			constraints.insets		= new Insets(0,5,0,5);
			radioButtonPanel.add(this.queryKeyRadioButton, constraints);
			

			this.parameterRadioButton = buildRadioButton("PARAMETER_RADIO_BUTTON_ON_SECOND_ARGUMENT_PANEL", buildRadioButtonModelAdapter(MWArgument.PARAMETER_TYPE, false));	
			enablingComponents.add(this.parameterRadioButton);
	
			constraints.gridx		= 0;
			constraints.gridy		= gridY++;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.NORTHWEST;
			constraints.insets		= new Insets(0,5,0,5);
			radioButtonPanel.add(this.parameterRadioButton, constraints);
	
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.NORTHWEST;
		constraints.insets		= new Insets(5, 0, 5, 0);
		add(radioButtonPanel, constraints);	
	
		this.emptyPanel = new JPanel();
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		add(this.emptyPanel, constraints);	
		

		this.parameterArgumentPanel = new ParameterArgumentPanel(this.argumentHolder, getWorkbenchContextHolder(), enablingComponents);
		this.literalArgumentPanel = new LiteralArgumentPanel(this.argumentHolder, getWorkbenchContextHolder(), enablingComponents);
		this.queryKeyArgumentPanel = new QueryableArgumentPanel(this.argumentHolder, getWorkbenchContextHolder(), enablingComponents);

		setActiveArgumentPanel(this.literalArgumentPanel);
		new ComponentEnabler(buildNullArgumentBooleanHolder(), enablingComponents);
	}
	
	

	
	private void setActiveArgumentPanel(ArgumentPanel newArgumentPanel) 
	{
		JPanel oldActiveArgumentPanel = this.activeArgumentPanel;
		
		if (newArgumentPanel == oldActiveArgumentPanel) 
			return;
		
		this.activeArgumentPanel = newArgumentPanel;
		
		if (oldActiveArgumentPanel != null)
			this.emptyPanel.remove(oldActiveArgumentPanel);
			
		GridBagConstraints constraints = new GridBagConstraints();
		this.emptyPanel.setLayout(new GridBagLayout());
			
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 5);	
		this.emptyPanel.add(this.activeArgumentPanel, constraints);

		revalidate();
		repaint();
	}
	
	
	private PropertyChangeListener buildArgumentListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateActiveArgumentPanel();
			}
		};
	}
	
	private void updateActiveArgumentPanel() {
		MWArgument argument = getArgument();
		if (argument != null) {
			setActiveArgumentPanel((ArgumentPanel) this.argumentTypeArgumentPanelMap.get(argument.getClass()));
		}
		else {
		    setActiveArgumentPanel(this.literalArgumentPanel);
		}
	}
	
	private ButtonModel buildRadioButtonModelAdapter(String argumentType, boolean defaultValue) {
		return new RadioButtonModelAdapter(this.argumentTypeHolder, argumentType, defaultValue);
	}
	
	private PropertyValueModel buildArgumentTypeHolder() {
		PropertyAspectAdapter adapter =  new PropertyAspectAdapter(this.basicExpressionHolder, MWBasicExpression.SECOND_ARGUMENT_PROPERTY) {

			protected Object getValueFromSubject() {
				return ((MWBasicExpression) this.subject).getSecondArgument();
			}
			
			protected void setValueOnSubject(Object value) {
				if (value == MWArgument.LITERAL_TYPE) {
					((MWBasicExpression) this.subject).setSecondArgumentToLiteral();			
				}
				else if (value == MWArgument.QUERY_KEY_TYPE) {
					((MWBasicExpression) this.subject).setSecondArgumentToQueryable();			
				}
				else if (value == MWArgument.PARAMETER_TYPE) {
					((MWBasicExpression) this.subject).setSecondArgumentToParameter();			
				}
			}
		};
		
		return new TransformationPropertyValueModel(adapter) {
			protected Object transform(Object value) {
				if (value == null) {
					return null;
				}
				return ((MWArgument) value).getType();
			}
		};
	}

}
