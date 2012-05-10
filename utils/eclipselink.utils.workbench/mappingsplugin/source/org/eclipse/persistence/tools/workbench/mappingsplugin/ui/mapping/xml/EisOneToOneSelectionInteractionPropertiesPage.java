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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml.InteractionPanel;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;


final class EisOneToOneSelectionInteractionPropertiesPage extends ScrollablePropertiesPage {

	EisOneToOneSelectionInteractionPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
		addHelpTopicId(this, "mappings.eis.selectionInteraction");
	}

	private PropertyValueModel buildInteractionHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWEisReferenceMapping.SELECTION_INTERACTION_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWEisReferenceMapping) this.subject).getSelectionInteraction();
			}
		};
	}

	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		int offset = SwingTools.checkBoxIconWidth();
		JPanel container = new JPanel(new GridBagLayout());

		// Reference Descriptor chooser
		JCheckBox useDescriptorReadObjectInteractionCheckBox = buildCheckBox
		(
			"USE_REFERENCE_DESCRIPTOR_READ_OBJECT_INTERACTION",
			buildUseDescriptorReadObjectInteractionCheckBoxAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 5);

		container.add(useDescriptorReadObjectInteractionCheckBox, constraints);
		addHelpTopicId(useDescriptorReadObjectInteractionCheckBox, "mapping.useDescriptorReadObjectInteraction");

		// Sub-pane
		InteractionPanel interactionPanel = new InteractionPanel(getApplicationContext(), buildInteractionHolder(), "mappings.eis.selectionInteraction");

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(0, offset, 0, 0);

		container.add(interactionPanel, constraints);
		buildInteractionPanelEnabler(interactionPanel.getComponents());
		addHelpTopicId(interactionPanel, "mapping.selectionInteraction");

		return container;
	}
	
	private ButtonModel buildUseDescriptorReadObjectInteractionCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildUseDescriptorReadObjectInteractionHolder());
	}

	private PropertyValueModel buildUseDescriptorReadObjectInteractionHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), MWEisOneToOneMapping.USE_DESCRIPTOR_READ_OBJECT_INTERACTION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWEisOneToOneMapping mapping = (MWEisOneToOneMapping) this.subject;
				return Boolean.valueOf(mapping.usesDescriptorReadObjectInteraction());
			}

			protected void setValueOnSubject(Object value)
			{
				MWEisOneToOneMapping mapping = (MWEisOneToOneMapping) this.subject;
				mapping.setUseDescriptorReadObjectInteraction(Boolean.TRUE.equals(value));
			}
		};
	}
	private ComponentEnabler buildInteractionPanelEnabler(Component[] components)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(buildUseDescriptorReadObjectInteractionHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null) return Boolean.FALSE;
				
				// the components should be enabled when the checkbox is _not_ checked. 
				return Boolean.valueOf(! ((Boolean) value).booleanValue());
			}
		};
		return new ComponentEnabler(booleanHolder, components);
	}
	
}
