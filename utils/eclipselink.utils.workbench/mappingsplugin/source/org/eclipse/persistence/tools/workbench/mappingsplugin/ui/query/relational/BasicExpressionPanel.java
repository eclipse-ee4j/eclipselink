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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;


/**
 * This panel is the bottom half of the ExpressionBuilderDialog.
 * When the user chooses an expression from the tree, this panel is populated accordingly.
 * 
 * It contains a 2 ExpressionArgumentPanels, one for choosing the first argument(a queryable argument)
 * The other for choosing the second argument(queryable, literal, or parameter).  
 * 
 * The second argument will be null if the user chooses a unary operator from the operator combo box
 * The secondArgumentPanel will be disabled if the second argument is null
 */

final class BasicExpressionPanel extends AbstractPanel
{	
	private PropertyValueModel basicExpressionHolder;
	
	private PropertyValueModel firstArgumentHolder;
	private PropertyValueModel secondArgumentHolder;	
	
	private FirstArgumentPanel firstArgumentPanel;
	private SecondArgumentPanel secondArgumentPanel;
	
	private JComboBox operatorComboBox;
	
	private ValueModel basicExpressionEnablerModel;
	
	
	//Binary operators
	public static final String EQUAL = "EQUAL";
	public static final String EQUALS_IGNORE_CASE = "EQUALS IGNORE CASE";
	public static final String GREATER_THAN = "GREATER THAN";
    public static final String GREATER_THAN_EQUAL = "GREATER THAN EQUAL";
    public static final String LESS_THAN = "LESS THAN";
    public static final String LESS_THAN_EQUAL = "LESS THAN EQUAL";
    public static final String LIKE = "LIKE";
    public static final String LIKE_IGNORE_CASE = "LIKE IGNORE CASE";
    public static final String NOT_EQUAL = "NOT EQUAL";
    public static final String NOT_LIKE = "NOT LIKE";
	
	//Unary operators
	public static final String IS_NULL = "IS NULL";
    public static final String NOT_NULL = "NOT NULL";


	BasicExpressionPanel(PropertyValueModel selectedExpressionHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		initialize(selectedExpressionHolder);
	}

	private void initialize(PropertyValueModel selectedExpressionHolder) {
		this.basicExpressionHolder = buildBasicExpressionHolder(selectedExpressionHolder);
		this.firstArgumentHolder = buildFirstArgumentHolder();
		this.secondArgumentHolder = buildSecondArgumentHolder();
		this.basicExpressionEnablerModel = buildBasicExpressionEnablerModel();
		initializeLayout();	
	}
	
	private PropertyValueModel buildFirstArgumentHolder() {
		return new PropertyAspectAdapter(this.basicExpressionHolder, MWBasicExpression.FIRST_ARUGMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWBasicExpression) this.subject).getFirstArgument();
			}
		};		
	}
	
	private PropertyValueModel buildSecondArgumentHolder() {
		return new PropertyAspectAdapter(this.basicExpressionHolder, MWBasicExpression.SECOND_ARGUMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWBasicExpression) this.subject).getSecondArgument();
			}
		};		
	}
	
	private PropertyValueModel buildBasicExpressionHolder(PropertyValueModel selectedExpressionHolder) {
		return new FilteringPropertyValueModel(selectedExpressionHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWBasicExpression;
			}
		};		
	}

	private ValueModel buildBasicExpressionEnablerModel() {
		return new PropertyAspectAdapter(this.basicExpressionHolder) {
			protected Object buildValue() {
				if (basicExpressionHolder.getValue() == null) {
					return Boolean.FALSE;
				}
				return Boolean.TRUE;
			}
		};
	}

	private Iterator allowableOperatorTypes() {
		Collection operatorTypes = new ArrayList();
		operatorTypes.add(EQUAL);
 		operatorTypes.add(NOT_EQUAL);
 		operatorTypes.add(EQUALS_IGNORE_CASE);
 		operatorTypes.add(GREATER_THAN);
 		operatorTypes.add(GREATER_THAN_EQUAL);
 		operatorTypes.add(LESS_THAN);
 		operatorTypes.add(LESS_THAN_EQUAL);
 		operatorTypes.add(LIKE);
 		operatorTypes.add(NOT_LIKE);
 		operatorTypes.add(LIKE_IGNORE_CASE);
 		operatorTypes.add(IS_NULL);
 		operatorTypes.add(NOT_NULL);		
		
		return operatorTypes.iterator();	
	}
		
	private void initializeLayout()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		setBorder(BorderFactory.createTitledBorder(""));

		this.firstArgumentPanel = new FirstArgumentPanel(this.firstArgumentHolder, this.basicExpressionEnablerModel, getWorkbenchContextHolder());
		this.firstArgumentPanel.setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("FIRST_ARGUMENT_PANEL_TITLE.title")));
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(this.firstArgumentPanel, constraints);
		

		Collection enablingComponents = new ArrayList();			
		JPanel operatorPanel = new JPanel(new GridBagLayout());

			JLabel operatorLabel =  new JLabel(resourceRepository().getString("OPERATOR_LABEL:"));
			operatorLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("OPERATOR_LABEL:"));
			enablingComponents.add(operatorLabel);
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.WEST;
			constraints.insets		= new Insets(5, 5, 5, 5);
			operatorPanel.add(operatorLabel, constraints);
		
			this.operatorComboBox = buildOperatorComboBox();
			enablingComponents.add(this.operatorComboBox);
			new ComponentEnabler(this.basicExpressionEnablerModel, enablingComponents);
			constraints.gridx		= 0;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.NORTH;
			constraints.insets		= new Insets(5, 5, 5, 5);
			operatorLabel.setLabelFor(this.operatorComboBox);
			operatorPanel.add(this.operatorComboBox, constraints);

		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(operatorPanel, constraints);

		
		this.secondArgumentPanel = new SecondArgumentPanel(this.secondArgumentHolder, this.basicExpressionHolder, getWorkbenchContextHolder());
		this.secondArgumentPanel.setBorder(BorderFactory.createTitledBorder(resourceRepository().getString("SECOND_ARGUMENT_PANEL_TITLE.title")));
		
		constraints.gridx		= 2;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(this.secondArgumentPanel, constraints);
		
//		 Set a minimum size for both panels 
		Dimension dimension1 = firstArgumentPanel.getPreferredSize(); 
		Dimension dimension2 = secondArgumentPanel.getPreferredSize(); 
		 
		dimension1.width  = Math.max(dimension1.width,  dimension2.height); 
		dimension1.height = Math.max(dimension1.height, dimension2.height) + 8; 
		 
		firstArgumentPanel.setMinimumSize(dimension1); 
		firstArgumentPanel.setPreferredSize(dimension1); 
		 
		secondArgumentPanel.setMinimumSize(dimension1); 
		secondArgumentPanel.setPreferredSize(dimension1); 
	}
	
	
	// *********** operator ************
	
	private JComboBox buildOperatorComboBox() {
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(buildOperatorComboBoxModel());
		return comboBox;
	}
	private ComboBoxModel buildOperatorComboBoxModel() {
		return new ComboBoxModelAdapter(buildOperatorTypeCollectionHolder(), buildOperatorAdapter());
	}
	
	private CollectionValueModel buildOperatorTypeCollectionHolder() {
		return new AbstractReadOnlyCollectionValueModel() {
			public Object getValue() {
				return allowableOperatorTypes();
			}
		};
	}

	private PropertyValueModel buildOperatorAdapter() {
		return new PropertyAspectAdapter(this.basicExpressionHolder, MWBasicExpression.OPERATOR_TYPE_PROPERTY) {

			protected Object getValueFromSubject() {
				if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.EQUAL)
					return EQUAL;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.NOT_EQUAL)
					return NOT_EQUAL;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.EQUALS_IGNORE_CASE)
					return EQUALS_IGNORE_CASE;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.GREATER_THAN)
					return GREATER_THAN;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.GREATER_THAN_EQUAL)
					return GREATER_THAN_EQUAL;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.LESS_THAN)
					return LESS_THAN;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.LESS_THAN_EQUAL)
					return LESS_THAN_EQUAL;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.LIKE)
					return LIKE;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.NOT_LIKE)
					return NOT_LIKE;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.LIKE_IGNORE_CASE)
					return LIKE_IGNORE_CASE;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.IS_NULL)
					return IS_NULL;
				else if (((MWBasicExpression) this.subject).getOperatorType() == MWBasicExpression.NOT_NULL)
					return NOT_NULL;
					
				throw new IllegalArgumentException();
			}

			protected void setValueOnSubject(Object value) {
		
				if (value == EQUAL)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.EQUAL);
				else if (value == NOT_EQUAL)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.NOT_EQUAL);
				else if (value == EQUALS_IGNORE_CASE)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.EQUALS_IGNORE_CASE);
				else if (value == GREATER_THAN)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.GREATER_THAN);
				else if (value == GREATER_THAN_EQUAL)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.GREATER_THAN_EQUAL);
				else if (value == LESS_THAN)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.LESS_THAN);
				else if (value == LESS_THAN_EQUAL)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.LESS_THAN_EQUAL);
				else if (value == LIKE)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.LIKE);
				else if (value == NOT_LIKE)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.NOT_LIKE);
				else if (value == LIKE_IGNORE_CASE)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.LIKE_IGNORE_CASE);
				else if (value == IS_NULL)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.IS_NULL);
				else if (value == NOT_NULL)
					((MWBasicExpression) this.subject).setOperatorType(MWBasicExpression.NOT_NULL);
			}
		};
		
	}
}
