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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWError;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.StatusDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;


public final class AutomapAction extends AbstractEnablableFrameworkAction {

	public AutomapAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("AUTOMAP_ACTION");
		this.initializeToolTipText("AUTOMAP_ACTION.TOOLTIP");
		this.initializeIcon("automap");
	}

	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return ((MappingsApplicationNode) selectedNode).isAutoMappable();
	}

	protected void execute() {
		ApplicationNode[] projectNodes = this.selectedProjectNodes();
		for (int i = 0; i < projectNodes.length; i++) {
			this.execute((ProjectNode) projectNodes[i]);
		}
	}

	private void execute(ProjectNode projectNode) {
		if (projectNode.canAutomapDescriptors()) {
			projectNode.getProject().automap(this.selectedDescriptorsFor(projectNode));
			this.showResults(this.prepareResults(this.selectedNodesFor(projectNode)));
		} else {
			// project must have some tables, and,
			// for now, XML projects do not support automap
			this.showError(projectNode.getCannotAutomapDescriptorsStringKey());
		}
	}

	private Collection selectedDescriptorsFor(ProjectNode projectNode) {
		Collection descriptors = new HashSet();
		for (Iterator stream = this.selectedNodesFor(projectNode).iterator(); stream.hasNext(); ) {
			MappingsApplicationNode node = (MappingsApplicationNode) stream.next();
			if (node.getProjectNode() == projectNode) {
				node.addDescriptorsTo(descriptors);
			}
		}
		return descriptors;
	}

	private Collection selectedNodesFor(ProjectNode projectNode) {
		Collection result = new ArrayList();
		ApplicationNode[] nodes = this.selectedNodes();
		for (int i = 0; i < nodes.length; i++) {
			MappingsApplicationNode node = (MappingsApplicationNode) nodes[i];
			if (node.getProjectNode() == projectNode) {
				result.add(node);
			}
		}
		return result;
	}

	private Collection prepareResults(Collection nodes) {
		Collection status = new ArrayList(nodes.size());

		for (Iterator stream = nodes.iterator(); stream.hasNext(); ) {
			ApplicationNode node = (ApplicationNode) stream.next();

			MWError error = this.buildError(node);
			LinkedHashMap errors = new LinkedHashMap();
			errors.put(error, error);

			StatusDialog.Status modelStatus = StatusDialog.createStatus(node, errors);
			status.add(modelStatus);
		}

		return status;
	}

	private MWError buildError(ApplicationNode node) {
		return new MWError(((AutomappableNode) node).getAutomapSuccessfulStringKey(), node.displayString());
	}

	private void showResults(Collection status) {
		StatusDialog dialog = new StatusDialog(
			this.getWorkbenchContext(),
			status,
			"AUTOMAP_STATUS_DIALOG_TITLE",
			"dialog.automap"
		) {
			protected CellRendererAdapter buildNodeRenderer(Object value) {
				return (value instanceof ApplicationNode) ?
					this.buildApplicationNodeAdapter()
				:
					super.buildNodeRenderer(value);
			}
			private CellRendererAdapter buildApplicationNodeAdapter() {
				return new AbstractCellRendererAdapter() {
					public String buildAccessibleName(Object value) {
						return ((ApplicationNode) value).accessibleName();
					}
					public Icon buildIcon(Object value) {
						return ((ApplicationNode) value).icon();
					}
					public String buildText(Object value) {
						return ((ApplicationNode) value).displayString();
					}
				};
			}
		};

		dialog.setVisible(true);
	}

	private void showError(String errorKey) {
		JOptionPane.showMessageDialog(
				this.getWorkbenchContext().getCurrentWindow(),
				this.resourceRepository().getString(errorKey),
				this.resourceRepository().getString(errorKey + ".title"),
				JOptionPane.WARNING_MESSAGE);
	}

}
