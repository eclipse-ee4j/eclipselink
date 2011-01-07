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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.AbstractLockingPolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;


/**
 * @version 10.1.3
 */
public class EisLockingPolicyPropertiesPage extends AbstractLockingPolicyPropertiesPage
{	
	public EisLockingPolicyPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}
	
	protected Component buildPage()
	{
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints constraints = new GridBagConstraints();
			
        JRadioButton noneRadioButton = buildUseNoneButton();
        constraints.gridx           = 0;
        constraints.gridy           = 0;
        constraints.gridwidth   = 1;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 0;
        constraints.fill            = GridBagConstraints.HORIZONTAL;
        constraints.anchor      = GridBagConstraints.FIRST_LINE_START;
        constraints.insets      = new Insets(0, 5, 0, 0);
        mainPanel.add(noneRadioButton, constraints);
        addHelpTopicId(noneRadioButton, getHelpTopicId() + ".none");
 
        
        JPanel optimisticLockingPanel = buildOptimisticLockingPanel();
		buildOptimisticLockingPanelEnabler(collectComponentsAsArray(optimisticLockingPanel));
		
		// Create the panel
		GroupBox optimisticBox = new GroupBox(buildUseOptimisticButton(), optimisticLockingPanel);
		
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		addHelpTopicId(optimisticLockingPanel, getHelpTopicId() + ".optimistic");
				
		mainPanel.add(optimisticBox, constraints);
		
		JPanel pessimisticLockingPanel = buildPessimisticLockingPanel();
		buildPessimisticLockingPanelEnabler(pessimisticLockingPanel.getComponents());
		
		JRadioButton pessimisticButton = buildUsePessimisticButton();
		GroupBox pessimisticBox = new GroupBox(pessimisticButton, pessimisticLockingPanel);
				
		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);

		addHelpTopicId(pessimisticLockingPanel, getHelpTopicId() + ".pessimistic");
		
		mainPanel.add(pessimisticBox, constraints);

		addHelpTopicId(mainPanel, getHelpTopicId());
				
		return mainPanel;
	}


	protected JPanel buildOptimisticLockingPanel() {
		return new EisVersionLockingPanel(getLockingPolicyHolder(), getWorkbenchContextHolder());
	}
	
	private ComponentEnabler buildOptimisticLockingPanelEnabler(Component[] components)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(getLockingPolicyTypeHolder())
		{
			protected Object transform(Object value)
			{
				
				return Boolean.valueOf(MWDescriptorLockingPolicy.OPTIMISTIC_LOCKING.equals(value));
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}
		
	
	protected String getHelpTopicId()
	{
		return "descriptor.eis.locking";
	}


	
}
