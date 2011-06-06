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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


final class ClassAttributePanel extends AbstractPanel {

	private ValueModel attributeHolder;

	/** we need to pass the descriptor to the modifiers sub-pane */
	private ValueModel descriptorHolder;
	
	
	ClassAttributePanel(ValueModel attributeHolder, ValueModel descriptorHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.attributeHolder = attributeHolder;
		this.descriptorHolder = descriptorHolder;
		initializeLayout();
	}
	
	private void initializeLayout() {
		setLayout(new GridBagLayout());
				
		GridBagConstraints constraints = new GridBagConstraints();
		
		//
		//	Action: Create the tabbed pane
		//
		JTabbedPane tabbedPane = new JTabbedPane();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);
		this.add(tabbedPane, constraints);
		
		
		//
		//	Action: Create the general page
		//
		JPanel generalPage = new JPanel();
		generalPage.setBorder(BorderFactory.createEmptyBorder());
		generalPage.setLayout(new GridBagLayout());
		
			//
			// Action:	Create the modifers panel
			//
			ClassAttributeModifiersPanel modifiersPanel = new ClassAttributeModifiersPanel(this.attributeHolder, this.descriptorHolder, getWorkbenchContextHolder());
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(5, 5, 0, 5);
			generalPage.add(modifiersPanel, constraints);
			
			//
			// Action:	Create the types panel
			//
			ClassAttributeTypesPanel typesPanel = new ClassAttributeTypesPanel(this.attributeHolder, getWorkbenchContextHolder());
			constraints.gridx		= 0;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 1;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.PAGE_START;
			constraints.insets		= new Insets(5, 5, 5, 5);
			generalPage.add(typesPanel, constraints);
			
		addHelpTopicId(generalPage, helpTopicId() + ".general");

		tabbedPane.addTab(resourceRepository().getString("ATTRIBUTE_GENERAL_PAGE"), generalPage);
		
		//
		//	Action: Create the accessor page
		//
		JPanel accessorsPage = new JPanel(new GridBagLayout());
		
			//
			// Action:	Create the accessors panel
			//
			ClassAttributeAccessorsPanel accessorsPanel = 
				new ClassAttributeAccessorsPanel(this.attributeHolder, this.getWorkbenchContextHolder());
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 1;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.PAGE_START;
			constraints.insets		= new Insets(5, 5, 5, 5);
			accessorsPage.add(accessorsPanel, constraints);
			
		addHelpTopicId(accessorsPage, helpTopicId() + ".accessors");
		tabbedPane.addTab(resourceRepository().getString("ATTRIBUTE_ACCESSORS_PAGE"), accessorsPage);
	}
	
	private String helpTopicId() {
		return "descriptor.classInfo.attributes";
	}
}
