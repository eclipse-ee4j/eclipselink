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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


final class ManyToManyTableReferencePropertiesPage extends ScrollablePropertiesPage
{
	ManyToManyTableReferencePropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		Insets insets = BorderFactory.createTitledBorder("m").getBorderInsets(this);

		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		// Relation Table chooser
		JComponent relationTableWidgets = buildLabeledComponent
		(
			"MANY_TO_MANY_RELATION_TABLE_CHOOSER",
			buildRelationTableChooser()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, insets.left + 5, 0, insets.right + 5);

		addHelpTopicId(relationTableWidgets, "mapping.manyToMany.relationTable");
		
		container.add(relationTableWidgets, constraints);

		// Source Reference
		ManyToManySourceReferencePanel sourceReference = new ManyToManySourceReferencePanel
		(
			getSelectionHolder(),
			getWorkbenchContextHolder()
		);

		sourceReference.setBorder(new CompoundBorder
		(
			buildTitledBorder("MANY_TO_MANY_TABLE_REFERENCE_SOURCE_TITLE"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		container.add(sourceReference, constraints);
		addPaneForAlignment(sourceReference);

		// Target Reference
		ManyToManyTargetReferencePanel targetReference = new ManyToManyTargetReferencePanel
		(
			getSelectionHolder(),
			getWorkbenchContextHolder()
		);

		targetReference.setBorder(new CompoundBorder
		(
			buildTitledBorder("MANY_TO_MANY_TABLE_REFERENCE_TARGET_TITLE"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		container.add(targetReference, constraints);
		addPaneForAlignment(targetReference);

		return container;
	}

	/**
	 * allow the user to select any table in the database
	 * for the relation table, because s/he can add references
	 * if necessary
	 */
	private ListChooser buildRelationTableChooser() {
		return RelationalProjectComponentFactory.buildTableChooser(
						this.getSelectionHolder(),
						this.buildRelationTableHolder(),
						this.buildRelationTableChooserDialogBuilder(),
						this.getWorkbenchContextHolder()
					);
		
	}

	private DefaultListChooserDialog.Builder buildRelationTableChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("RELATION_TABLE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("RELATION_TABLE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildTableStringConverter());
		return builder;
	}
	
	
	private StringConverter buildTableStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWTable) o).getName();
			}
		};
	}

	private PropertyValueModel buildRelationTableHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), MWManyToManyMapping.RELATION_TABLE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWManyToManyMapping mapping = (MWManyToManyMapping) subject;
				return mapping.getRelationTable();
			}

			protected void setValueOnSubject(Object value)
			{
				MWManyToManyMapping mapping = (MWManyToManyMapping) subject;
				mapping.setRelationTable((MWTable) value);
			}
		};
	}

}
