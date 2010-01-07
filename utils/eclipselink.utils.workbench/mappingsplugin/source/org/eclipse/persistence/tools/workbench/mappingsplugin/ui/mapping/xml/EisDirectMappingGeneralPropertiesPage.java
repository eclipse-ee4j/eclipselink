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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.NullValuePolicyPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;


final class EisDirectMappingGeneralPropertiesPage
	extends ScrollablePropertiesPage
{
	// **************** Constructors ******************************************
	
	EisDirectMappingGeneralPropertiesPage(PropertyValueModel xmlDirectMappingNodeHolder, WorkbenchContextHolder contextHolder) {
		super(xmlDirectMappingNodeHolder, contextHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		offset.left += 5; offset.right += 5;

		// xpath panel
		AbstractPanel pane = this.buildXmlFieldPanel();
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
		panel.add(pane, constraints);
		this.addPaneForAlignment(pane);
		
		// method accessing panel
		pane = this.buildMethodAccessingPanel();
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
		panel.add(pane, constraints);
		this.addPaneForAlignment(pane);
		
		// null value panel
		pane = this.buildNullValuePanel();
		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);
		panel.add(pane, constraints);
		this.addPaneForAlignment(pane);
		
		// read only check box
		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 5, 0, 0);
		panel.add(this.buildReadOnlyCheckBox(), constraints);
		
		// comment
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(this.getSelectionHolder(), this.resourceRepository());
		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, offset.left, 0, offset.right);
		panel.add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "mapping.comment");

		return panel;
	}
	
	private AbstractXmlFieldPanel buildXmlFieldPanel() {
		return new DirectMappingXmlFieldPanel(this.buildXmlFieldHolder(), this.getWorkbenchContextHolder());
	}
	
	private ValueModel buildXmlFieldHolder() {
		return new PropertyAspectAdapter(this.getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWXmlDirectMapping) this.subject).getXmlField();
			}
		};
	}
	
	private MethodAccessingPanel buildMethodAccessingPanel() {
		return new MethodAccessingPanel(this.getSelectionHolder(), this.getWorkbenchContextHolder());
	}
	
	private NullValuePolicyPanel buildNullValuePanel() {
		return new NullValuePolicyPanel(this.getSelectionHolder(), this.getWorkbenchContextHolder());
	}
	
	private Component buildReadOnlyCheckBox() {
		return MappingComponentFactory.buildReadOnlyCheckBox(this.getSelectionHolder(), this.getApplicationContext());
	}
	
}
