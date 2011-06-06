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

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ModifierComponentGroup.Verifier;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


final class ClassModifiersPanel extends AbstractSubjectPanel {

	ClassModifiersPanel(ValueModel typeHolder, ApplicationContext applicationContext) {
		super(typeHolder, applicationContext);
	}
	
	protected void initializeLayout() {
		// labeled border
		this.setBorder(
				BorderFactory.createCompoundBorder(
					this.buildTitledBorder("MODIFIER_PANEL_TITLE"),
					BorderFactory.createEmptyBorder(0, 5, 5, 5)
				)
			);
		GridBagConstraints constraints = new GridBagConstraints();
		ModifierComponentGroup modifierComponentGroup = new ModifierComponentGroup(Verifier.NULL_INSTANCE, this.getSubjectHolder(), this.getApplicationContext());

		//	access modifiers panel
		JPanel accessModifiersPanel = modifierComponentGroup.getAccessModifiersPanel();
		accessModifiersPanel.setLayout(new GridBagLayout());
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		this.add(accessModifiersPanel, constraints);

			// public
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.LINE_START;
			constraints.insets = new Insets(0, 0, 0, 0);
			accessModifiersPanel.add(modifierComponentGroup.getPublicAccessRadioButton(), constraints);
	
			// (Default)
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.LINE_START;
			constraints.insets = new Insets(0, 0, 0, 0);
			accessModifiersPanel.add(modifierComponentGroup.getDefaultAccessRadioButton(), constraints);
		
		// other modifiers panel
		JPanel otherModifiersPanel = modifierComponentGroup.getOtherModifiersPanel();
		otherModifiersPanel.setLayout(new GridBagLayout());
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 0, 0, 0);
		this.add(otherModifiersPanel, constraints);	

			// abstract
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.LINE_START;
			constraints.insets = new Insets(0, 0, 0, 0);
			otherModifiersPanel.add(modifierComponentGroup.getAbstractCheckBox(), constraints);

			// final
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.LINE_START;
			constraints.insets = new Insets(0, 0, 0, 0);
			otherModifiersPanel.add(modifierComponentGroup.getFinalCheckBox(), constraints);	
	}
	
}
