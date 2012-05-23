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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.util.Collection;
import java.util.Collections;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWError;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.StatusDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.DescriptorCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ProjectCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;



abstract class AbstractGenerateTablesFromDescriptorsAction extends AbstractFrameworkAction {

	AbstractGenerateTablesFromDescriptorsAction(WorkbenchContext context) {
		super(context);
	}

	protected void generateTablesFromDescriptors(Collection descriptors) {
		ApplicationNode selectedNode = selectedNodes()[0];
		MWProject project = (MWProject) selectedNode.getProjectRoot().getValue();
		boolean projectDirty = project.isDirtyBranch();
		int result = notifyClassDefinitionMayChange(projectDirty);

		if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
			return;
		}
		
		if (!projectDirty && result == JOptionPane.NO_OPTION) {
			return;
		}

		if ((result == JOptionPane.YES_OPTION) && projectDirty) {
			selectedNode.save(null, getWorkbenchContext());
		}

		TableGenerator generator = new TableGenerator(getWorkbenchContext());
		StatusDialog.Status status = generator.generateTablesFromDescriptors(descriptors);
		showResult(status);
	}

	private int notifyClassDefinitionMayChange(boolean projectDirty) {
		String messageKey;
		if (projectDirty)
			messageKey = "AUTO_GENERATING_TABLE_DEFINITIONS_STATUS_MESSAGE_SAVE";
		else
			messageKey = "AUTO_GENERATING_TABLE_DEFINITIONS_STATUS_MESSAGE";

		LabelArea label = new LabelArea(resourceRepository().getString(messageKey));

		return JOptionPane.showConfirmDialog(
			currentWindow(), 
			label,
			application().getShortProductName(),
			projectDirty ? JOptionPane.YES_NO_CANCEL_OPTION : JOptionPane.YES_NO_OPTION,
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	private void showResult(StatusDialog.Status status) {
		StatusDialog dialog = new StatusDialog(
			getWorkbenchContext(),
			Collections.singletonList(status),
			"AUTO_GENERATING_TABLE_DEFINITIONS_STATUS_DIALOG_TITLE")
		{
			protected CellRendererAdapter buildNodeRenderer(Object value) {
				if (value instanceof MWProject) {
					return new ProjectCellRendererAdapter(resourceRepository());
				}

				if (value instanceof MWDescriptor) {
					return new DescriptorCellRendererAdapter(resourceRepository());
				}

				if (value instanceof MWError) {
					return new MWErrorCellRendererAdapter() {
						public Icon buildIcon(Object value) {
							MWError error = (MWError) value;

							if (error.getErrorId().endsWith("ASSUMPTION"))
								return resourceRepository().getIcon("ignore");

							if (error.getErrorId().endsWith("URGENT"))
								return resourceRepository().getIcon("urgent");

							return super.buildIcon(value);
						}
					};
				}

				return super.buildNodeRenderer(value);
			}
		};

		dialog.setVisible(true);
	}
}
