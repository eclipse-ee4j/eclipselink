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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.FileChooserPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectOptionsPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class XmlProjectOptionsPropertiesPage extends ProjectOptionsPropertiesPage {

	private static final long serialVersionUID = 1L;


	XmlProjectOptionsPropertiesPage(PropertyValueModel oxProjectNodeHolder, WorkbenchContextHolder contextHolder) {
		super(oxProjectNodeHolder, contextHolder);
	}
	
	protected Component buildPage() {

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// create the deployment and source code generation panel
		JPanel deploymentAndCodeGenerationPanel = new JPanel(new GridBagLayout());
		deploymentAndCodeGenerationPanel.setBorder(buildTitledBorder("DEPLOYMENT_AND_CODE_GENERATION_PANEL_TITLE"));

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.NORTH;
		constraints.insets     = new Insets(0, 0, 0, 0);

		panel.add(deploymentAndCodeGenerationPanel, constraints);
				
			// create the project deployment xml panel
			JPanel projectXmlPanel = new JPanel(new GridBagLayout());
			projectXmlPanel.setBorder(buildTitledBorder("PROJECT_DEPLOYMENT_XML_PANEL_TITLE"));

			constraints.gridx      = 0;
			constraints.gridy      = 1;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(0, 5, 0, 5);

			deploymentAndCodeGenerationPanel.add(projectXmlPanel, constraints);
				
				// project deployment xml
				FileChooserPanel projectXmlFileChooserPanel = new FileChooserPanel(
					getApplicationContext(),
					buildDeploymentXMLFileNameHolder(getSelectionHolder()),
					"PROJECT_DEPLOYMENT_XML_FILE_LABEL",
					"BROWSE_BUTTON_1",
					JFileChooser.FILES_ONLY
				);

				projectXmlFileChooserPanel.setFileChooserRootFileHolder(this.buildFileChooserRootFileHolder());
				projectXmlFileChooserPanel.setFileChooserDefaultDirectoryHolder(this.buildFileChooserDefaultDirectoryHolder(PROJECT_DEPLOYMENT_XML_DIRECTORY_PREFERENCE));

				constraints.gridx      = 0;
				constraints.gridy      = 0;
				constraints.gridwidth  = 1;
				constraints.gridheight = 1;
				constraints.weightx    = 1;
				constraints.weighty    = 0;
				constraints.fill       = GridBagConstraints.HORIZONTAL;
				constraints.anchor     = GridBagConstraints.CENTER;
				constraints.insets     = new Insets(5, 5, 5, 5);

				projectXmlPanel.add(projectXmlFileChooserPanel, constraints);
				addPaneForAlignment(projectXmlFileChooserPanel);
	
			addHelpTopicId(projectXmlPanel, helpTopicId() + ".deployment");

			// create the model java source panel
			JPanel modelSourcePanel = new JPanel(new GridBagLayout());
			modelSourcePanel.setBorder(buildTitledBorder("MODEL_JAVA_SOURCE_PANEL_TITLE"));

			constraints.gridx      = 0;
			constraints.gridy      = 2;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 1;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.PAGE_START;
			constraints.insets     = new Insets(5, 5, 5, 5);

			deploymentAndCodeGenerationPanel.add(modelSourcePanel, constraints);
			
				// create the source code root directory chooser
				FileChooserPanel modelSourceRootDirChooserPanel = new FileChooserPanel(
					getApplicationContext(),
					buildModelSourceDirectoryNameHolder(getSelectionHolder()),
					"MODEL_SOURCE_ROOT_DIRECTORY_LABEL",
					"BROWSE_BUTTON_2",
					JFileChooser.DIRECTORIES_ONLY);

				modelSourceRootDirChooserPanel.setFileChooserRootFileHolder(this.buildFileChooserRootFileHolder());
				modelSourceRootDirChooserPanel.setFileChooserDefaultDirectoryHolder(this.buildFileChooserDefaultDirectoryHolder(MODEL_SOURCE_DIRECTORY_PREFERENCE));

				constraints.gridx      = 0;
				constraints.gridy      = 0;
				constraints.gridwidth  = 1;
				constraints.gridheight = 1;
				constraints.weightx    = 1;
				constraints.weighty    = 0;
				constraints.fill       = GridBagConstraints.HORIZONTAL;
				constraints.anchor     = GridBagConstraints.CENTER;
				constraints.insets     = new Insets(5, 5, 5, 5);

				modelSourcePanel.add(modelSourceRootDirChooserPanel, constraints);
				addPaneForAlignment(modelSourceRootDirChooserPanel);
		
		addHelpTopicId(modelSourcePanel, helpTopicId() + ".generation");				
		
		JCheckBox usesWeavingChecksBox = buildCheckBox("USES_WEAVING_CHECK_BOX", buildWeavingButtonModel());	
		addHelpTopicId(usesWeavingChecksBox, helpTopicId() + ".useWeaving");
		constraints.gridx 		= 0;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.NORTH;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		panel.add(usesWeavingChecksBox, constraints);

		addHelpTopicId(panel, helpTopicId());
		return panel;
	}
	
	protected String helpTopicId() {
		return "xmlproject.options";
	}
}
