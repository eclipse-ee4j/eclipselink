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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ClassIndicatorDictionarySubPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 *
 */
public final class RelationalClassIndicatorDictionarySubPanel
	extends ClassIndicatorDictionarySubPanel  {
	
	public RelationalClassIndicatorDictionarySubPanel(PropertyValueModel classIndicatorPolicyHolder, WorkbenchContextHolder contextHolder) {
		super(classIndicatorPolicyHolder, contextHolder);
		initializeLayout();
	}

	private void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		// Create the combo box
		indicatorTypeListChooser = buildIndicatorTypeChooser();
		JPanel indicatorTypePanel = new AccessibleTitledPanel(new BorderLayout(5, 0));
		typeLabel = buildLabel("INDICATOR_TYPE");
		typeLabel.setLabelFor(indicatorTypeListChooser);

		Spacer spacer = new Spacer();
		addAlignRight(spacer);

		indicatorTypePanel.add(typeLabel, BorderLayout.LINE_START);
		indicatorTypePanel.add(indicatorTypeListChooser, BorderLayout.CENTER);
		indicatorTypePanel.add(spacer, BorderLayout.LINE_END);
		
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(indicatorTypePanel, constraints);
		addAlignLeft(typeLabel);
		helpManager().addTopicID(indicatorTypePanel, "mapping.variableOneToOne.indicatorType");

		JPanel tablePanel = new AccessibleTitledPanel(new GridBagLayout());

		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 1;
		constraints.fill 		= GridBagConstraints.BOTH;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(5, 0, 0, 0);

		// Create the table view
		JTable table = buildClassIndicatorValuesTable();
		tableScrollPane = new JScrollPane(table);
		tableScrollPane.getViewport().setBackground(table.getBackground());
		tablePanel.add(tableScrollPane, constraints);
	
			constraints.gridx		= 1;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.PAGE_START;
			constraints.insets		= new Insets(5, 5, 0, 0);

			tablePanel.add(getEditButton(), constraints);
			this.addAlignRight(getEditButton());

		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);

		add(tablePanel, constraints);
		addHelpTopicId(tablePanel, "mapping.variableOneToOne.indicatorValues");
		
		addHelpTopicId(this, helpTopicId());
	}
	
}
