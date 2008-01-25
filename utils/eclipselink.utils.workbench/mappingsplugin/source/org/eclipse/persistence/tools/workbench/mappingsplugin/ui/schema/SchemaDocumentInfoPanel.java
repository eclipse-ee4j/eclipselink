/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceSpecification;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.LabelPanel;


final class SchemaDocumentInfoPanel 
	extends ScrollablePropertiesPage
{
	public SchemaDocumentInfoPanel(PropertyValueModel schemaNodeHolder, WorkbenchContextHolder contextHolder) {
		super(schemaNodeHolder, contextHolder);
	}
	
	protected Component buildPage() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel page = new JPanel(new GridBagLayout());
		page.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JComponent schemaSourceWidgets = this.buildSchemaSourcePanel();

		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		page.add(schemaSourceWidgets, constraints);
		addHelpTopicId(schemaSourceWidgets, "schema.docInfo.source");

		JComponent schemaNamespacesWidgets = this.buildSchemaNamespacesPanel();

		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);

		page.add(schemaNamespacesWidgets, constraints);
		addHelpTopicId(schemaNamespacesWidgets, "schema.docInfo.namespaces");

		addHelpTopicId(page, "schema.docInfo");
		
		return page;
	}
	
	private JPanel buildSchemaSourcePanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder(
								this.resourceRepository().getString("SCHEMA_SOURCE_LABEL")),
							BorderFactory.createEmptyBorder(0, 5, 5, 5)));
	
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 5, 0);
		panel.add(this.buildSchemaSourceLabelPanel(), constraints);
		
		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_END;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(this.buildEditSchemaSourceButton(), constraints);
		
		return panel;
	}
	
	private JPanel buildSchemaSourceLabelPanel() {
		JPanel panel = new LabelPanel(this.buildSchemaSourceLabelAdapter());
		Insets margin = UIManager.getInsets("TextField.margin");
		panel.setBorder(BorderFactory.createCompoundBorder(
							UIManager.getBorder("TextField.border"),
							BorderFactory.createEmptyBorder(margin.top, margin.left, margin.bottom, margin.right)));
		return panel;
	}

	private PropertyValueModel buildSchemaSourceLabelAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), MWXmlSchema.SCHEMA_SOURCE_PROPERTY) {
			protected Object getValueFromSubject() {
				ResourceSpecification schemaSource = ((MWXmlSchema) this.subject).getSchemaSource();
				if (schemaSource == null) {
					return null;
				}
				String key = schemaSource.getSourceKey() + "_LABEL";
				return SchemaDocumentInfoPanel.this.resourceRepository().getString(key, schemaSource.getLocation());
			}
		};
	}
	
	private JButton buildEditSchemaSourceButton() {
		JButton editSchemaSourceButton = new JButton(this.buildEditSchemaSourceAction());
		editSchemaSourceButton.setText(this.resourceRepository().getString("EDIT_SCHEMA_SOURCE_BUTTON_TEXT"));
		return editSchemaSourceButton;
	}
	
	private Action buildEditSchemaSourceAction() {
		return ((XmlSchemaNode) this.getNode()).getSchemaPropertiesAction(getWorkbenchContext());
	}
	
	private JPanel buildSchemaNamespacesPanel() {
		SchemaNamespacesPanel panel = new SchemaNamespacesPanel(this.getNodeHolder(), getWorkbenchContextHolder());
		panel.setBorder(buildTitledBorder("NAMESPACES_PANEL_TITLE"));
		return panel;
	}
}
