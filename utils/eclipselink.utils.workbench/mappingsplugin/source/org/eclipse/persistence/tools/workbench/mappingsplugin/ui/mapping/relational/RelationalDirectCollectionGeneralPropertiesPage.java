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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.IndirectableContainerMappingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MethodAccessingPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class RelationalDirectCollectionGeneralPropertiesPage extends ScrollablePropertiesPage
{
	RelationalDirectCollectionGeneralPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	protected Component buildPage() {
		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(container);
		offset.left += 5; offset.right += 5;

		// Target Table chooser
		JComponent targetTableWidgets = 
			buildLabeledComponent(
				"DIRECT_COLLECTION_TARGET_TABLE_CHOOSER",
				RelationalMappingComponentFactory.buildTargetTableChooser(
													getSelectionHolder(), 
													getWorkbenchContextHolder())
			);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, offset.left, 0, offset.right);

		container.add(targetTableWidgets, constraints);
		helpManager().addTopicID(targetTableWidgets, "mapping.directCollection.targetTable");

		// Direct Field chooser
		JComponent directFieldWidgets = 
			buildLabeledComponent(
				"DIRECT_COLLECTION_DIRECT_FIELD_CHOOSER",
				RelationalMappingComponentFactory.buildDirectValueColumnChooser(
													getSelectionHolder(), 
													getWorkbenchContextHolder())
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, offset.left, 0, offset.right);

		container.add(directFieldWidgets, constraints);
		helpManager().addTopicID(directFieldWidgets, "mapping.directCollection.directValueField");

		// Use Method accessing widgets
		MethodAccessingPanel methodAccessingPanel = 
			new MethodAccessingPanel(getSelectionHolder(), getWorkbenchContextHolder());
		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);
		container.add(methodAccessingPanel, constraints);
		addPaneForAlignment(methodAccessingPanel);
		
		// Read Only
		JCheckBox readOnlyCheckBox = 
			RelationalMappingComponentFactory.buildReadOnlyCheckBox(this.getSelectionHolder(), this.getApplicationContext());
		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 0);
		container.add(readOnlyCheckBox, constraints);
		
		// Use Batch Reading
		JCheckBox useBatchReadingCheckBox = 
			RelationalMappingComponentFactory.buildBatchReadingCheckBox(getSelectionHolder(), resourceRepository());
		constraints.gridx       = 0;
		constraints.gridy       = 4;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 0);
		container.add(useBatchReadingCheckBox, constraints);
		addHelpTopicId(useBatchReadingCheckBox, "mapping.useBatchReading");
		
		//join fetching
		JComponent joinFetchWidgets =
			buildLabeledComponent("JOIN_FETCHING_CHOOSER_LABEL", RelationalMappingComponentFactory.buildJoinFetchingCombobox(getSelectionHolder(), getWorkbenchContextHolder()));
		constraints.gridx       = 0;
		constraints.gridy       = 5;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, offset.left, 0, offset.right);
		container.add(joinFetchWidgets, constraints);
		addHelpTopicId(joinFetchWidgets, "mapping.joinFetch");
			
		// Indirectable
		IndirectableContainerMappingPanel indirectablePanel = 
			new IndirectableContainerMappingPanel(getSelectionHolder(), getApplicationContext());
		constraints.gridx       = 0;
		constraints.gridy       = 6;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);
		container.add(indirectablePanel, constraints);

		// Collection Options Advanced button
		JPanel advancedPanel = MappingComponentFactory.buildCollectionContainerPolicyOptionsBrowser(
			getWorkbenchContextHolder(),
			getSelectionHolder(),
			"mapping.directCollection.options");
		constraints.gridx       = 0;
		constraints.gridy       = 7;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 0);
		container.add(advancedPanel, constraints);

		// comment
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx       = 0;
		constraints.gridy       = 8;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.FIRST_LINE_START;
		constraints.insets      = new Insets(5, 5, 0, 5);
		container.add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "mapping.comment");
		
		addHelpTopicId(container, "mapping.directCollection");
		return container;
	}
}
