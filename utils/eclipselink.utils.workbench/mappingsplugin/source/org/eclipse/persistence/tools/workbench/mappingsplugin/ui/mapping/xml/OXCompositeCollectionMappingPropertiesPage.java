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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UiMappingBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public final class OXCompositeCollectionMappingPropertiesPage
	extends TitledPropertiesPage
{
	static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiXmlBundle.class,
		UiMappingBundle.class,
		UiMappingXmlBundle.class
	};

	private JPanel mainPanel;
	
	// **************** Constructors ******************************************
	
	OXCompositeCollectionMappingPropertiesPage(WorkbenchContext context) {
		super(context);
	}
	
	
	// **************** Initialization ****************************************
	
	protected Component buildPage() {

		this.mainPanel = new JPanel(new GridBagLayout());
		this.mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.mainPanel.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		offset.left += 5; offset.right += 5;

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
		this.mainPanel.add(pane, constraints);
		this.addPaneForAlignment(pane);

		// reference descriptor chooser
		Component referenceDescritor = this.buildReferenceDescriptorPanel();
		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, offset.left, 0, offset.right);
		this.mainPanel.add(referenceDescritor, constraints);
		addHelpTopicId(referenceDescritor, "mapping.eis.referenceDescriptor");
		
		// method accessing panel
		pane = this.buildMethodAccessingPanel();
		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);
		this.mainPanel.add(pane, constraints);
		this.addPaneForAlignment(pane);
		
		// container accessor panel
		pane = this.buildContainerAccessorPanel();
		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);
		this.mainPanel.add(pane, constraints);
		this.addPaneForAlignment(pane);

		// read only check box
		JCheckBox readOnlyCheckBox = this.buildReadOnlyCheckBox();
		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 5, 0, 0);
		this.mainPanel.add(readOnlyCheckBox, constraints);

		// Collection Options Advanced button
		JPanel advancedPanel = MappingComponentFactory.buildContainerPolicyOptionsBrowser(
			getWorkbenchContextHolder(),
			getSelectionHolder(),
			"mapping.collectionOptions");
		constraints.gridx       = 0;
		constraints.gridy       = 5;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 0);
		this.mainPanel.add(advancedPanel, constraints);

		// comment
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx      = 0;
		constraints.gridy      = 6;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, offset.left, 0, offset.right);
		this.mainPanel.add(commentPanel, constraints);
		this.addAlignLeft(commentPanel);
		this.addHelpTopicId(commentPanel, "mapping.comment");

		this.addHelpTopicId(this, "mapping.compositeCollection");

		return this.mainPanel;
	}
	
	private AbstractXmlFieldPanel buildXmlFieldPanel() {
		return new ElementTypeableXmlFieldPanel(getSelectionHolder(), this.buildXmlFieldHolder(), this.getWorkbenchContextHolder(), MWCompositeCollectionMapping.ELEMENT_TYPE_PROPERTY);
	}
	
	private ValueModel buildXmlFieldHolder() {
		return new PropertyAspectAdapter(this.getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWCompositeCollectionMapping) this.subject).getXmlField();
			}
		};
	}
	
	private Component buildReferenceDescriptorPanel() {
		return this.buildLabeledComponent(
			"COMPOSITE_COLLECTION_MAPPING_REFERENCE_DESCRIPTOR_CHOOSER", 
			MappingComponentFactory.buildReferenceDescriptorChooser(this.getSelectionHolder(), this.getWorkbenchContextHolder())
		);
	}
	
	private MethodAccessingPanel buildMethodAccessingPanel() {
		return new MethodAccessingPanel(this.getSelectionHolder(), this.getWorkbenchContextHolder());
	}
	
	private UseContainerAccessorPanel buildContainerAccessorPanel() {
		return new UseContainerAccessorPanel(this.getSelectionHolder(), this.getWorkbenchContextHolder());
	}
		
	private JCheckBox buildReadOnlyCheckBox() {
		return MappingComponentFactory.buildReadOnlyCheckBox(this.getSelectionHolder(), this.getApplicationContext());
	}
	
}
