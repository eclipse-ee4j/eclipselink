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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooser;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


final class EisReferenceMappingFieldPairEditingPanel
	extends AbstractPanel
{
	// **************** Variables *********************************************
	
	/** Used to govern how the field pair is edited */
	private FieldPairSpec fieldPairSpec;
	
	
	// **************** Constructors ******************************************
	
	public EisReferenceMappingFieldPairEditingPanel(WorkbenchContext context, FieldPairSpec fieldPairSpec) {
		super(new DefaultWorkbenchContextHolder(context));
		this.fieldPairSpec = fieldPairSpec;
		this.initializeLayout();
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		// Source field label
		JLabel sourceFieldLabel = this.buildSourceFieldLabel();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		this.add(sourceFieldLabel, constraints);
		
		// Source Field component
		XpathChooser sourceFieldChooser = this.buildSourceFieldChooser();
		sourceFieldChooser.setAccessibleLabeler(sourceFieldLabel);
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		this.add(sourceFieldChooser, constraints);
		
		// Target field label
		JLabel targetFieldLabel = this.buildTargetFieldLabel();
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);
		this.add(targetFieldLabel, constraints);
		
		// Target Field component
		XpathChooser targetFieldChooser = this.buildTargetFieldChooser();
		targetFieldChooser.setAccessibleLabeler(targetFieldLabel);
		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		add(targetFieldChooser, constraints);
	}
	
	private JLabel buildSourceFieldLabel() {
		return this.buildLabel("EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_PANEL.SOURCE_FIELD_CHOOSER");
	}
	
	private XpathChooser buildSourceFieldChooser() {
		XpathChooser chooser = 
			new XpathChooser(
				this.getWorkbenchContextHolder(), 
				EisReferenceMappingFieldPairEditingPanel.this.fieldPairSpec.sourceXmlFieldHolder(),
				EisReferenceMappingFieldPairEditingPanel.this.fieldPairSpec.sourceXpathHolder()
			);
		chooser.setPreferredSize(new Dimension(400, chooser.getPreferredSize().height));
		this.putClientProperty("initialFocus", chooser);
		return chooser;
	}
	
	private JLabel buildTargetFieldLabel() {
		return this.buildLabel("EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_PANEL.TARGET_FIELD_CHOOSER");
	}
	
	private XpathChooser buildTargetFieldChooser() {
		XpathChooser chooser = 
			new XpathChooser (
				this.getWorkbenchContextHolder(), 
				EisReferenceMappingFieldPairEditingPanel.this.fieldPairSpec.targetXmlFieldHolder(),
				EisReferenceMappingFieldPairEditingPanel.this.fieldPairSpec.targetXpathHolder()
			);
		chooser.setPreferredSize(new Dimension(400, chooser.getPreferredSize().height));
		return chooser;
	}
	
	
	// **************** Member classes ****************************************
	
	public static interface FieldPairSpec
	{
		ValueModel sourceXmlFieldHolder();
		
		PropertyValueModel sourceXpathHolder();
		
		ValueModel targetXmlFieldHolder();
		
		PropertyValueModel targetXpathHolder();
	}
}
