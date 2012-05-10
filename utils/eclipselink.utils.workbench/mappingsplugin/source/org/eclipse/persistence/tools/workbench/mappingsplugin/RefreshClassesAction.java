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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWError;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.DescriptorCreationFailureContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.ExternalClassLoadFailureContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.StatusDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ProjectCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorCreationFailuresDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ExternalClassLoadFailuresDialog;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public final class RefreshClassesAction extends AbstractFrameworkAction {

	public static final String EJB_JAR_XML_EXCEPTION_ERROR = "EJB_JAR_XML_EXCEPTION_ERROR";
	public static final String EJB_JAR_XML_PROJECT_NOT_UPDATED_ERROR = "EJB_JAR_XML_PROJECT_NOT_UPDATED_ERROR";
	public static final String INVALID_DOC_TYPE_ERROR = "INVALID_DOC_TYPE_ERROR";

	RefreshClassesAction(WorkbenchContext context) {
		super(context);
	}
	
	protected void initialize() {
		this.initializeTextAndMnemonic("REFRESH_CLASSES_ACTION");
		this.initializeAccelerator("REFRESH_CLASSES_ACTION.accelerator");
		this.initializeToolTipText("REFRESH_CLASSES_ACTION.toolTipText");
		this.initializeIcon("synchronize");
	}
		
	protected void execute() {
		this.navigatorSelectionModel().pushExpansionState();
		this.refreshDescriptors(this.selectedProjectNodes(), this.selectedNodes());
		this.navigatorSelectionModel().popAndRestoreExpansionState();
	}
	
	private void refreshDescriptors(ApplicationNode[] projectNodes, ApplicationNode[] selectedNodes) {
		// refresh the external class repositories
		for (int i = 0; i < projectNodes.length; i++) {
			((MWProject) projectNodes[i].getValue()).getRepository().refreshExternalClassDescriptions();
		}
		
		// refresh the descriptors
		ExternalClassLoadFailureContainer failures = new ExternalClassLoadFailureContainer();
		DescriptorCreationFailureContainer descriptorCreationFailures = new DescriptorCreationFailureContainer();
		
		HashBag descriptors = new HashBag();
		for (int i = 0; i < selectedNodes.length; i++) {
			((MappingsApplicationNode) selectedNodes[i]).addDescriptorsTo(descriptors);
		}
		
		//check for inherited attributes
		for (Iterator stream = descriptors.uniqueIterator(); stream.hasNext();) {
			boolean anyInheritedAttributes = false;
			if (((MWDescriptor) stream.next()).inheritedAttributesSize() > 0) {
				anyInheritedAttributes = true;
			}
			if (anyInheritedAttributes) {
				if (promptToRefreshWithInheritedAttributes(this.getWorkbenchContext())) {
					return;
				}
			}
		}
		
		for (Iterator stream = descriptors.uniqueIterator(); stream.hasNext();) {
			((MWDescriptor) stream.next()).refreshClass(failures, descriptorCreationFailures);
		}
		// show the classes not on classpath
		if (failures.containsFailures()) {
			new ExternalClassLoadFailuresDialog(getWorkbenchContext(), failures).show();
			return;
		}
		
		if (descriptorCreationFailures.containsFailures()) {
			new DescriptorCreationFailuresDialog(descriptorCreationFailures, getWorkbenchContext()).show();
		}
	}
	
	private boolean canReadWithProblems(MWProject project, LinkedHashMap problems) {
		StatusDialog.Status status = StatusDialog.createStatus(project, problems);
		StatusDialog dialog = new CustomizedStatusDialog(Collections.singleton(status));
		dialog.setVisible(true);
		return dialog.wasConfirmed();
	}

	private void showResult(Collection results)
	{
		StatusDialog dialog = new StatusDialog(
			getWorkbenchContext(),
			results,
			"PROJECT_EJB_UPDATE_STATUS_DIALOG_TITLE",
			"PROJECT_EJB_UPDATE_STATUS_DIALOG_MESSAGE",
			"project.export.ejb-jar.xml")
		{
			protected CellRendererAdapter buildNodeRenderer(Object value)
			{
				if (value instanceof MWProject)
					return new ProjectCellRendererAdapter(resourceRepository());

				return super.buildNodeRenderer(value);
			}
		};

		dialog.setVisible(true);
	}

	private boolean promptToUpdateProjectFromEjbJarXml(WorkbenchContext workbenchContext) {
		int option =
			JOptionPane.showConfirmDialog(
					workbenchContext.getCurrentWindow(),
					resourceRepository().getString("UPDATE_PROJECT_FROM_EJB_JAR"),
					resourceRepository().getString("UPDATE_PROJECT_FROM_EJB_JAR.title"),
					JOptionPane.YES_NO_OPTION
			);
		return option == JOptionPane.YES_OPTION;
	}
	
	private boolean promptToRefreshWithInheritedAttributes(WorkbenchContext workbenchContext) {
		int option = JOptionPane.showConfirmDialog(
				workbenchContext.getCurrentWindow(),
				resourceRepository().getString("REFRESH_DESCRIPTORS_WITH_INHERITED_ATTRIBUTES_WARNING", StringTools.CR),
				resourceRepository().getString("REFRESH_DESCRIPTORS_WITH_INHERITED_ATTRIBUTES_TITLE"),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE
			);
		return option == JOptionPane.NO_OPTION;
	}

	private class CustomizedStatusDialog extends StatusDialog
	{
		private CustomizedStatusDialog(Collection projectStatus)
		{
			super(RefreshClassesAction.this.getWorkbenchContext(),
					projectStatus,
					"PROJECT_EJB_UPDATE_STATUS_DIALOG_TITLE",
					"PROJECT_UPDATE_STATUS_DIALOG_MESSAGE",
					"project.export.ejb-jar.xml");
		}

		protected Action buildCancelAction()
		{
			AbstractAction action = (AbstractAction) super.buildCancelAction();
			action.putValue(Action.NAME, resourceRepository().getString("EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_NO_BUTTON"));
			action.putValue(Action.MNEMONIC_KEY, new Integer(resourceRepository().getMnemonic("EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_NO_BUTTON")));
			return action;
		}

		protected CellRendererAdapter buildNodeRenderer(Object value)
		{
			if (value instanceof MWProject)
				return new ProjectCellRendererAdapter(resourceRepository());

			return super.buildNodeRenderer(value);
		}

		protected Action buildOKAction()
		{
			AbstractAction action = (AbstractAction) super.buildOKAction();
			action.putValue(Action.NAME, resourceRepository().getString("EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_YES_BUTTON"));
			action.putValue(Action.MNEMONIC_KEY, new Integer(resourceRepository().getMnemonic("EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_YES_BUTTON")));
			return action;
		}

		protected boolean cancelButtonIsVisible()
		{
			return true;
		}
	}
}
