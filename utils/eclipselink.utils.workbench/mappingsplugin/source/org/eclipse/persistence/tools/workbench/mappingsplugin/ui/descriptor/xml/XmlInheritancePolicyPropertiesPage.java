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
import java.util.Collection;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.InheritancePolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


abstract class XmlInheritancePolicyPropertiesPage
	extends InheritancePolicyPropertiesPage {

	XmlInheritancePolicyPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}

	protected Component buildPage() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel contentPanel = new JPanel(new GridBagLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Is Child widgets
		JRadioButton isChildRadioButton = buildRadioButton("IS_CHILD_DESC", buildIsChildDescriptorRadioButtonModel(getIsRootHolder()));
		addHelpTopicId(isChildRadioButton, helpTopicId() + ".isChild");

		JPanel isChildPanel = buildIsChildPanel(getIsRootHolder().getValue() == Boolean.FALSE);
		addHelpTopicId(isChildPanel, helpTopicId() + ".isChild");
		getIsRootHolder().addPropertyChangeListener(buildIsChildListener(isChildPanel));
		
		// Is Root widgets
		JRadioButton isRootRadioButton = buildRadioButton("IS_ROOT_DESC", buildIsRootParentDescriptorRadioButtonModel(getIsRootHolder()));
		addHelpTopicId(isRootRadioButton, helpTopicId() + ".isRoot");

		Collection isRootListeners = new Vector();
		JPanel isRootPanel = buildIsRootPanel(isRootListeners);
		isRootListeners.add(classIndicatorPolicyPanel);	
	
		// Add everything to the container
		GroupBox groupBox = new GroupBox
		(
			isChildRadioButton, isChildPanel,
			isRootRadioButton,  isRootPanel
		);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		contentPanel.add(groupBox, constraints);

		addHelpTopicId(getClassIndicatorPolicyPanel(), helpTopicId() + ".isRoot");
		addHelpTopicId(contentPanel, helpTopicId());
		addAllRootListeners(isRootListeners);		
				
		return contentPanel;
	}

	private JPanel buildIsRootPanel(Collection isRootListeners) {
		classIndicatorPolicyPanel = new XmlClassIndicatorPolicySubPanel(getSelectionHolder(), getInheritancePolicyHolder(), getWorkbenchContextHolder(), isRootListeners);
		return classIndicatorPolicyPanel;
	}

	protected String helpTopicIdPrefix() {
		return "descriptor.xml";
	}
}
