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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ModifierComponentGroup.Verifier;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


final class MethodModifiersPanel extends AbstractSubjectPanel {
			
	
	MethodModifiersPanel(ValueModel methodHolder, ApplicationContext context) {
		super(methodHolder, context);
	}
		
	protected void initializeLayout() {
		// labeled border
		this.setBorder(BorderFactory.createTitledBorder(this.resourceRepository().getString("MODIFIER_PANEL_TITLE")));
		GridBagConstraints constraints = new GridBagConstraints();
		ModifierComponentGroup modifierComponentGroup = new ModifierComponentGroup(Verifier.NULL_INSTANCE, this.getSubjectHolder(), this.getApplicationContext());

		//	access modifiers panel
		JPanel accessModifiersPanel = modifierComponentGroup.getAccessModifiersPanel();
		accessModifiersPanel.setLayout(new GridBagLayout());
		
			// public
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.3;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.NORTH;
			constraints.insets = new Insets(0, 5, 0, 0);
			accessModifiersPanel.add(modifierComponentGroup.getPublicAccessRadioButton(), constraints);
			
			// private
			constraints.gridx = 1;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.NORTH;
			constraints.insets = new Insets(0, 5, 0, 5);
			accessModifiersPanel.add(modifierComponentGroup.getPrivateAccessRadioButton(), constraints);
			
			// protected
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.3;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.NORTH;
			constraints.insets = new Insets(5, 5, 5, 0);
			accessModifiersPanel.add(modifierComponentGroup.getProtectedAccessRadioButton(), constraints);
			
			// (Default)
			constraints.gridx = 1;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.NORTH;
			constraints.insets = new Insets(5, 5, 5, 5);
			accessModifiersPanel.add(modifierComponentGroup.getDefaultAccessRadioButton(), constraints);
		
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0.3;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.insets = new Insets(0, 5, 5, 0);
		this.add(accessModifiersPanel, constraints);
		
		// other modifiers panel
		JPanel otherModifiersPanel = modifierComponentGroup.getOtherModifiersPanel();
		otherModifiersPanel.setLayout(new GridBagLayout());
		
			// abstract
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.3;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 0, 0);
			otherModifiersPanel.add(modifierComponentGroup.getAbstractCheckBox(), constraints);

			// static
			constraints.gridx = 1;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 0, 5);
			otherModifiersPanel.add(modifierComponentGroup.getStaticCheckBox(), constraints);
		
			// final
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.3;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(5, 5, 0, 0);
			otherModifiersPanel.add(modifierComponentGroup.getFinalCheckBox(), constraints);

			// native
			constraints.gridx = 1;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(5, 5, 0, 5);
			otherModifiersPanel.add(modifierComponentGroup.getNativeCheckBox(), constraints);
			
			// synchronized
			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.3;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor = GridBagConstraints.WEST;
			constraints.insets = new Insets(5, 5, 5, 0);
			otherModifiersPanel.add(modifierComponentGroup.getSynchronizedCheckBox(), constraints);
		
			
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.insets = new Insets(0, 5, 5, 5);
		this.add(otherModifiersPanel, constraints);	
	}
	
}
