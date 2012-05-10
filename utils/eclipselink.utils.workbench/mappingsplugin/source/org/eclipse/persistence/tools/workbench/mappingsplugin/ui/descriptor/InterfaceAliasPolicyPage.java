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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInterfaceAliasPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWInterfaceAliasDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * @version 10.0.3
 */
public class InterfaceAliasPolicyPage extends ScrollablePropertiesPage
{
	public final static int EDITOR_WEIGHT = 3;
	
	public InterfaceAliasPolicyPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}
	
	private String getHelpTopicId()
	{
		return "descriptor.interfacealias";
	}

	protected Component buildPage() 
	{
		//		 Interface alias panel
		JPanel interfaceAliasPanel = new JPanel(new GridBagLayout());
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints constraints = new GridBagConstraints();

		// Add the label
		JLabel interfaceChooserLabel = buildLabel("INTERFACE_ALIAS");

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		interfaceAliasPanel.add(interfaceChooserLabel, constraints);

		ClassChooserPanel interfaceAliasChooserPanel = ClassChooserTools.buildPanel(
						this.buildInterfaceAliasHolder(),
						this.buildClassRepositoryHolder(),
						ClassChooserTools.buildDeclarableReferenceFilter(),
						interfaceChooserLabel,
						this.getWorkbenchContextHolder()
		);
		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 0);

    	interfaceAliasPanel.add(interfaceAliasChooserPanel, constraints);
    	
    	JPanel emptyPanel = new JPanel();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		
		interfaceAliasPanel.add(emptyPanel, constraints);
 
		addHelpTopicId(interfaceAliasPanel, getHelpTopicId());
 	
    	return interfaceAliasPanel;
	}
	
	MWMappingDescriptor descriptor() {
		return (MWMappingDescriptor) this.selection();
	}

	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return InterfaceAliasPolicyPage.this.descriptor().getRepository();
			}
		};
	}
	
	private PropertyValueModel buildInterfaceAliasHolder() 
	{
		return new PropertyAspectAdapter(buildInterfaceAliasPolicyHolder(), MWDescriptorInterfaceAliasPolicy.INTERFACE_ALIAS_PROPERTY) {
			protected Object getValueFromSubject() 
			{
				return ((MWDescriptorInterfaceAliasPolicy) subject).getInterfaceAlias();
			}
			protected void setValueOnSubject(Object value) 
			{
				MWDescriptorInterfaceAliasPolicy policy  = (MWDescriptorInterfaceAliasPolicy)subject; 
				MWClass interfaceClass = (MWClass)value;
				boolean shouldContinueWithSelection = true;
				
				// if there is an interface descriptor present in the project, we need to 
				// tell the user that it will be removed.  
				if (policy.getProject().descriptorForType(interfaceClass) != null)
				{
					shouldContinueWithSelection = promptToRemoveInterfaceDescriptor();
				}
				if (shouldContinueWithSelection)
				{
					policy.setInterfaceAlias(interfaceClass);
				}
			}
		};
	}
	
	private boolean promptToRemoveInterfaceDescriptor()
	{
		return JOptionPane.showConfirmDialog(this, 
				new LabelArea(resourceRepository().getString("INTERFACE_ALIAS_REMOVE_DESCRIPTOR_MESSAGE")),
				resourceRepository().getString("INTERFACE_ALIAS_REMOVE_DESCRIPTOR_TITLE"),
				JOptionPane.YES_NO_OPTION) 
				== JOptionPane.YES_OPTION;
	}

	private PropertyValueModel buildInterfaceAliasPolicyHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(),
				MWTableDescriptor.INTERFACE_ALIAS_POLICY_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWDescriptorPolicy policy = ((MWInterfaceAliasDescriptor) this.subject).getInterfaceAliasPolicy();
				return policy.isActive() ? policy : null;
			}
		};
	}
}
