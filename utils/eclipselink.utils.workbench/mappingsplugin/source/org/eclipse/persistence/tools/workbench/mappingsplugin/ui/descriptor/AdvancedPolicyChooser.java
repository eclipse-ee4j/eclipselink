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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.CheckList;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;


final class AdvancedPolicyChooser extends AbstractDialog
{
	private final ListValueModel advancedPropertiesCollection;
	private final ObjectListSelectionModel selectedItems;
	private final CellRendererAdapter cellRenderer;

	AdvancedPolicyChooser(WorkbenchContext context, 
			ListValueModel advancedPropertiesCollection, ObjectListSelectionModel selectedItems, CellRendererAdapter cellRenderer) 
	{
		super(context, context.getApplicationContext().getResourceRepository().getString("ADVANCED_PROPERTY_CHOOSER_TITLE"));
		this.advancedPropertiesCollection = advancedPropertiesCollection;
		this.selectedItems = selectedItems;
		this.cellRenderer = cellRenderer;
	}

	protected JComponent buildAdvancedPropertiesPanel() 
	{
		CheckList checkList = new CheckList(this.advancedPropertiesCollection, this.selectedItems, this.cellRenderer);
		checkList.setBorder(SwingComponentFactory.buildStandardEmptyBorder());
		return checkList;
	}

	@Override
	protected Component buildMainPanel() 
	{
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel panel = new JPanel(new GridBagLayout());

		LabelArea instructions = new LabelArea(resourceRepository().getString("ADVANCED_PROPERTY_CHOOSER_PLEASE_SELECT_ALL"));
		instructions.setScrollable(true);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(instructions, constraints);

		// Add the advanced properties panel
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 0, 0, 0);
		panel.add(buildAdvancedPropertiesPanel(), constraints);

		return panel;
	}

	@Override
	protected String helpTopicId()
	{
		return "dialog.advancedProperties";
	}

}
