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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.TransformerEditingPanel.TransformerSpec;


public abstract class FieldTransformerAssociationEditingPanel
	extends AbstractPanel
{
	// **************** Variables *********************************************
	
	/** Used to govern how the field transformer association is created */
	protected FieldTransformerAssociationSpec associationSpec;
	
	
	// **************** Constructors ******************************************
	
	public FieldTransformerAssociationEditingPanel(FieldTransformerAssociationSpec associationSpec, WorkbenchContext context) {
		super(new DefaultWorkbenchContextHolder(context));
		this.associationSpec = associationSpec;
		this.initializeLayout();
	}
	
	
	// **************** Initialization ****************************************
	
	private void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		// field chooser panel
		JPanel fieldChooserPanel = this.buildFieldChooserPanel();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 5);
		this.add(fieldChooserPanel, constraints);
		
		// transformer panel
		TransformerEditingPanel transformerPanel = this.buildTransformerPanel();
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
		this.add(transformerPanel, constraints);
		addPaneForAlignment(transformerPanel);
	}
	
	protected abstract JPanel buildFieldChooserPanel();
	
	private TransformerEditingPanel buildTransformerPanel() {
		TransformerEditingPanel transformerPanel 
			= new TransformerEditingPanel(this.associationSpec.transformerSpec(), this.getWorkbenchContextHolder());
		transformerPanel.setBorder(
			BorderFactory.createCompoundBorder(
				buildTitledBorder("FIELD_TRANSFORMER_ASSOCIATION_PANEL_TRANSFORMER_PANEL"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)
			)
		);
		return transformerPanel;
	}
	
	// **************** Member classes ****************************************
	
	public static interface FieldTransformerAssociationSpec
	{
		/** Return a spec for the transformer part of the association */
		TransformerSpec transformerSpec();
	}
}

