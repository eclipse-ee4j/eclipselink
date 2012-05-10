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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.AttributeTransformerPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class XmlTransformationMappingPropertiesPage 
	extends AbstractPropertiesPage
{

	// **************** Constructors ******************************************
	
	XmlTransformationMappingPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder, String topicId) {
		super(nodeHolder, contextHolder);
		initializeLayout(topicId);
	}

	// **************** Initialization ****************************************
	
	protected void initializeLayout() {
		// Done in initializeLayout(String)
	}

	protected void initializeLayout(String topicId) {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		offset.left += 5; offset.right += 5;
		
		// attribute transformer
		AttributeTransformerPanel attributeTransformerPanel = this.buildAttributeTransformerPanel();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, offset.left, 0, offset.right);
		add(attributeTransformerPanel, constraints);
		addPaneForAlignment(attributeTransformerPanel);
		
		// field transformers
		XmlFieldTransformerAssociationsPanel fieldTransformerAssociationsPanel = this.buildFieldTransformerAssociationsPanel();
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);
		add(fieldTransformerAssociationsPanel, constraints);
		addPaneForAlignment(fieldTransformerAssociationsPanel);
		
		// method accessing
		MethodAccessingPanel methodAccessingPanel = 
			new MethodAccessingPanel(this.getSelectionHolder(), this.getWorkbenchContextHolder());
		// (would LOVE to move this to a common factory)
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 0, 0, 0);
		add(methodAccessingPanel, constraints);
		addPaneForAlignment(methodAccessingPanel);
		
		// read only
		JCheckBox readOnlyCheckBox = this.buildReadOnlyCheckBox();
		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		add(readOnlyCheckBox, constraints);
		
		// mutable
		JCheckBox isMutableCheckBox = this.buildMutableCheckBox();
		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		add(isMutableCheckBox, constraints);
		
		// comment
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx		= 0;
		constraints.gridy		= 5;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, offset.left, 0, offset.right);
		add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "mapping.comment");
		
		this.addHelpTopicId(this, topicId);
	}
	
	
	// **************** Attribute transformer panel ***************************
	
	private AttributeTransformerPanel buildAttributeTransformerPanel() {
		return new AttributeTransformerPanel(this.getSelectionHolder(), this.getWorkbenchContextHolder());
	}
	
	
	// **************** Field transformer associations panel ******************
	
	private XmlFieldTransformerAssociationsPanel buildFieldTransformerAssociationsPanel() {
		return new XmlFieldTransformerAssociationsPanel(this.getSelectionHolder(), this.getWorkbenchContextHolder());
	}
	
	
	// **************** Read only *********************************************
	
	private JCheckBox buildReadOnlyCheckBox() {
		return MappingComponentFactory.buildReadOnlyCheckBox(
			this.getSelectionHolder(), 
			this.getApplicationContext()
		);
	}
	
	
	// **************** Mutable checkbox **************************************
	
	private JCheckBox buildMutableCheckBox() {
		return MappingComponentFactory.buildMutableCheckBox(
			this.getSelectionHolder(),
			this.resourceRepository(),
			this.helpManager()
		);
	}
}
