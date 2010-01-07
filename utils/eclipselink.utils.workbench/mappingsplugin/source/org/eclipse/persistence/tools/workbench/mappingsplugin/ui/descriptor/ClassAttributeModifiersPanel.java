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
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ModifierComponentGroup.Verifier;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


final class ClassAttributeModifiersPanel extends AbstractSubjectPanel {

	/**
	 * we need the descriptor so we can remove any mappings for attributes
	 * the user marks 'static' or 'final'
	 */
	private ValueModel descriptorHolder;


	ClassAttributeModifiersPanel(ValueModel attributeHolder, ValueModel descriptorHolder, WorkbenchContextHolder contextHolder) {
		super(attributeHolder, contextHolder);
		this.descriptorHolder = descriptorHolder;
	}
	
	protected void initializeLayout() {
		// labeled border
		this.setBorder(BorderFactory.createTitledBorder(this.resourceRepository().getString("MODIFIER_PANEL_TITLE")));
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		ModifierComponentGroup modifierComponentGroup = new ModifierComponentGroup(this.buildVerifier(), this.getSubjectHolder(), this.getApplicationContext());
		
		//	access modifiers panel
		JPanel accessModifiersPanel = modifierComponentGroup.getAccessModifiersPanel();
		accessModifiersPanel.setLayout(new GridBagLayout());
		
			// public
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.3;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor= GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 0, 0);
			accessModifiersPanel.add(modifierComponentGroup.getPublicAccessRadioButton(), constraints);
		
			// protected
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.3;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor= GridBagConstraints.WEST;
			constraints.insets = new Insets(5, 5, 5, 0);
			accessModifiersPanel.add(modifierComponentGroup.getProtectedAccessRadioButton(), constraints);
			
			// private
			constraints.gridx = 1;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor= GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 0, 5);
			accessModifiersPanel.add(modifierComponentGroup.getPrivateAccessRadioButton(), constraints);
			
			// (Default)
			constraints.gridx = 1;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.anchor= GridBagConstraints.WEST;
			constraints.insets = new Insets(5, 5, 5, 5);
			accessModifiersPanel.add(modifierComponentGroup.getDefaultAccessRadioButton(), constraints);
		
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0.3;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor= GridBagConstraints.NORTH;
		constraints.insets = new Insets(0, 5, 5, 0);
		this.add(accessModifiersPanel, constraints);
		
		
		// other modifiers panel
		JPanel otherModifiersPanel = modifierComponentGroup.getOtherModifiersPanel();
		otherModifiersPanel.setLayout(new GridBagLayout());
		
			// final
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.3;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor= GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 0, 0);
			otherModifiersPanel.add(modifierComponentGroup.getFinalCheckBox(), constraints);
		
			// static
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.3;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor= GridBagConstraints.WEST;
			constraints.insets = new Insets(5, 5, 5, 0);
			otherModifiersPanel.add(modifierComponentGroup.getStaticCheckBox(), constraints);
		
			// transient
			constraints.gridx = 1;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor= GridBagConstraints.WEST;
			constraints.insets = new Insets(0, 5, 0, 5);
			otherModifiersPanel.add(modifierComponentGroup.getTransientCheckBox(), constraints);
		
			// volatile
			constraints.gridx = 1;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor= GridBagConstraints.WEST;
			constraints.insets = new Insets(5, 5, 5, 5);
			otherModifiersPanel.add(modifierComponentGroup.getVolatileCheckBox(), constraints);
		
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor= GridBagConstraints.NORTH;
		constraints.insets = new Insets(0, 5, 5, 5);
		this.add(otherModifiersPanel, constraints);	

		this.addHelpTopicId(this, this.helpTopicId());	

	}
	
	private String helpTopicId() {
		return "descriptor.classInfo.attributes.general.modifiers";	 
	}

	
	// ********** Verifier stuff **********

	private Verifier buildVerifier() {
		return new Verifier() {
			public boolean verifyFinalChange(boolean newModifierIsFinal) {
				return ClassAttributeModifiersPanel.this.verifyFinalOrStaticChange(newModifierIsFinal);
			}
			public boolean verifyStaticChange(boolean newModifierIsStatic) {
				return ClassAttributeModifiersPanel.this.verifyFinalOrStaticChange(newModifierIsStatic);
			}
		};
	}
	
	/**
	 * return whether the user OKs the new modifier value
	 */
	boolean verifyFinalOrStaticChange(boolean newModifierIsFinalOrStatic) {		
		if ( ! newModifierIsFinalOrStatic) {
			// if the new modifier isn't final or static, no need to prompt the user
			return true;
		}
		if (this.attribute().getModifier().isStatic() || this.attribute().getModifier().isFinal()) {
			// if the current attribute is already final or static, no need to prompt the user
			return true;
		}
		boolean result = this.promptToChangeStaticOrFinalModifier();
		// even if we have an unmapped mapping we still want to prompt the user
		// because this will cause the node to disappear from the tree
		if (this.descriptor().mappingForAttribute(this.attribute()) != null) {
			this.descriptor().removeMappingForAttribute(this.attribute());
		}
		return result;
	}
	
	private MWClassAttribute attribute() {
		return (MWClassAttribute) this.subject();
	}

	private boolean promptToChangeStaticOrFinalModifier() {
		int result = JOptionPane.showConfirmDialog(this.currentWindow(),
										new LabelArea(this.resourceRepository().getString("sTATICAndFINALAttributesCannot")),
										this.resourceRepository().getString("removeMapping?"),
										JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.INFORMATION_MESSAGE);

		return result == JOptionPane.OK_OPTION;
	}

	private MWMappingDescriptor descriptor() {
		return (MWMappingDescriptor) this.descriptorHolder.getValue();
	}
	
}
