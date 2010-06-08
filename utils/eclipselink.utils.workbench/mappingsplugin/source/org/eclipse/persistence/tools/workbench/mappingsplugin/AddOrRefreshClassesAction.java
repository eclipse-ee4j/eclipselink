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

import java.awt.EventQueue;
import java.awt.Frame;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.MultipleClassChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.WaitDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWError;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.DescriptorCreationFailureContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.ExternalClassLoadFailureContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.StatusDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ProjectCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorCreationFailuresDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ExternalClassDescriptionClassDesciptionAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ExternalClassDescriptionClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ExternalClassLoadFailuresDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.UiProjectBundle;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

/**
 * import classes from the external class repository,
 * adding or refreshing as appropriate
 */
public final class AddOrRefreshClassesAction
	extends AbstractFrameworkAction
{
	public static final String EJB_JAR_XML_EXCEPTION_ERROR = "EJB_JAR_XML_EXCEPTION_ERROR";
	public static final String EJB_JAR_XML_PROJECT_NOT_UPDATED_ERROR = "EJB_JAR_XML_PROJECT_NOT_UPDATED_ERROR";
	public static final String INVALID_DOC_TYPE_ERROR = "INVALID_DOC_TYPE_ERROR";

	AddOrRefreshClassesAction(WorkbenchContext context) {
		super(context.buildExpandedResourceRepositoryContext(UiProjectBundle.class));
	}

	protected void initialize() {
		super.initialize();
		this.initializeIcon("descriptor.refresh");
		this.initializeTextAndMnemonic("ADD_OR_REFRESH_CLASSES_ACTION");
		this.initializeToolTipText("ADD_OR_REFRESH_CLASSES_ACTION.toolTipText");
		this.initializeAccelerator("ADD_OR_REFRESH_CLASSES_ACTION.accelerator");
	}

	protected void execute() {
		ApplicationNode[] projectNodes = this.selectedProjectNodes();
		for (int i = 0; i < projectNodes.length; i++) {
			this.execute((ProjectNode) projectNodes[i]);
		}
	}

	/**
	 * prompt the user for classes, then check for any errors;
	 * if we have an EJB project, prompt the user to update from ejb-jar.xml
	 */
	protected void execute(ProjectNode projectNode) {
		MWProject project = projectNode.getProject();

		// Do NOT automatically refresh the types when opening the multiple class chooser dialog.
		// Allow the user to control when the list of available types is refreshed, with the <Refresh> button.
		// @see MWClassRepository.refreshExternalClassDescriptions()

		MultipleClassChooserDialog dialog = this.buildDialog(project.getRepository());
		dialog.show();
		if (dialog.wasConfirmed()) {
			this.startClassImporter(project, CollectionTools.collection(dialog.selectedClassDescriptions()));
		}
		// try to force all the objects generated by the dialog to be garbage-collected
		dialog = null;
		MultipleClassChooserDialog.gc();
	}

	/**
	 * display only the classes found on the "project" classpath
	 */
	private MultipleClassChooserDialog buildDialog(MWClassRepository repository) {
		return new MultipleClassChooserDialog(
				this.getWorkbenchContext(),
				new LocalClassDescriptionRepository(repository),
				ExternalClassDescriptionClassDesciptionAdapter.instance()
		);
	}

	/**
	 * Start a thread that will import classes from class files
	 */
	private void startClassImporter(MWProject project, Collection selectedClassDescriptions) {
		Thread thread = new Thread(new ClassImporter(project, selectedClassDescriptions), "Class Import");
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
	}


	// ********** inner classes **********

	/**
	 * tweak the external class description repos to only return the classes found on
	 * the "project" classpath
	 */
	private class LocalClassDescriptionRepository extends ExternalClassDescriptionClassDescriptionRepository {
		Set userClassNames;

		LocalClassDescriptionRepository(MWClassRepository repository) {
			super(repository);
			Classpath cp = new Classpath(CollectionTools.list(repository.fullyQualifiedClasspathEntries()));
			this.userClassNames = new HashSet(1000);		// start sorta big
			cp.addClassNamesTo(this.userClassNames);
		}

		protected boolean accept(String externalClassDescriptionName) {
			return super.accept(externalClassDescriptionName) &&
					this.userClassNames.contains(externalClassDescriptionName);
		}

	}


	/**
	 * display a "wait" dialog while the classes are being imported
	 */
	private class ClassImporter implements Runnable {
		private MWProject project;
		private Collection selectedClassDescriptions;

		ClassImporter(MWProject project, Collection selectedClassDescriptions) {
			super();
			this.project = project;
			this.selectedClassDescriptions = selectedClassDescriptions;
		}
		
		public void run() {
			WaitDialog waitDialog = this.buildWaitDialog();
			launchLater(waitDialog);

			DescriptorCreationFailureContainer descriptorFailures = new DescriptorCreationFailureContainer();
			// pause the validator to improve performance on large imports
			this.project.getValidator().pause();
			ExternalClassLoadFailureContainer classFailures;
			try {
				classFailures = this.project.addDescriptorsForExternalClassDescriptions(this.selectedClassDescriptions.iterator(), descriptorFailures);
			} finally {
				// if we don't resume the validator, things will be really whacked...
				this.project.getValidator().resume();
				waitDialog.dispose();
			}

			if (classFailures.containsFailures()) {
				launchAndWait(new ExternalClassLoadFailuresDialog(this.workbenchContext(), classFailures));
			}
			if (descriptorFailures.containsFailures()) {
				launchAndWait(new DescriptorCreationFailuresDialog(descriptorFailures, this.workbenchContext()));
			}

			// TODO expand the project/package nodes and select one of the new classes?
		}

		private WaitDialog buildWaitDialog() {
			return new WaitDialog(
				(Frame) this.workbenchContext().getCurrentWindow(),
				this.resourceRepository().getIcon("class.default"),
				this.resourceRepository().getString("CLASS_IMPORT_WAIT_DIALOG.TITLE"),
				this.resourceRepository().getString("CLASS_IMPORT_WAIT_MESSAGE")
			);
		}

		private boolean promptUserToUpdateProjectFromEjbJarXml() {
			class Prompt implements Runnable {
				int option = 0;
				public void run() {
					this.option = JOptionPane.showConfirmDialog(
							ClassImporter.this.workbenchContext().getCurrentWindow(),
							ClassImporter.this.resourceRepository().getString("UPDATE_PROJECT_FROM_EJB_JAR"),
							ClassImporter.this.resourceRepository().getString("UPDATE_PROJECT_FROM_EJB_JAR.title"),
							JOptionPane.YES_NO_OPTION
					);
				}
			}
			Prompt prompt = new Prompt();
			try {
				EventQueue.invokeAndWait(prompt);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
			return prompt.option == JOptionPane.YES_OPTION;
		}
	
		private boolean canReadWithProblems(LinkedHashMap problems) {
			StatusDialog.Status status = StatusDialog.createStatus(this.project, problems);
			StatusDialog dialog = new LocalStatusDialog(Collections.singleton(status));
			launchAndWait(dialog);
			return dialog.wasConfirmed();
		}
	
		private void showEJBResult(Collection results) {
			if (results.isEmpty()) {
				return;
			}
			StatusDialog dialog = new StatusDialog(
				this.workbenchContext(),
				results,
				"PROJECT_EJB_UPDATE_STATUS_DIALOG_TITLE",
				"PROJECT_EJB_UPDATE_STATUS_DIALOG_MESSAGE",
				"project.export.ejb-jar.xml")
			{
				protected CellRendererAdapter buildNodeRenderer(Object value) {
					if (value instanceof MWProject) {
						return new ProjectCellRendererAdapter(ClassImporter.this.resourceRepository());
					}
					return super.buildNodeRenderer(value);
				}
			};
			launchAndWait(dialog);
		}
	
		WorkbenchContext workbenchContext() {
			return AddOrRefreshClassesAction.this.getWorkbenchContext();
		}

		ResourceRepository resourceRepository() {
			return AddOrRefreshClassesAction.this.resourceRepository();
		}

	}


	/**
	 * display EJB project update "status"???
	 */
	private class LocalStatusDialog extends StatusDialog {
		private LocalStatusDialog(Collection projectStatus) {
			super(AddOrRefreshClassesAction.this.getWorkbenchContext(),
					projectStatus,
					"PROJECT_EJB_UPDATE_STATUS_DIALOG_TITLE",
					"PROJECT_UPDATE_STATUS_DIALOG_MESSAGE",
					"project.export.ejb-jar.xml");
		}

		protected Action buildOKAction() {
			Action action = super.buildOKAction();
			action.putValue(Action.NAME, this.resourceRepository().getString("EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_YES_BUTTON"));
			action.putValue(Action.MNEMONIC_KEY, new Integer(this.resourceRepository().getMnemonic("EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_YES_BUTTON")));
			return action;
		}

		protected Action buildCancelAction() {
			Action action = super.buildCancelAction();
			action.putValue(Action.NAME, this.resourceRepository().getString("EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_NO_BUTTON"));
			action.putValue(Action.MNEMONIC_KEY, new Integer(this.resourceRepository().getMnemonic("EJB_JAR_XML_VALIDATOR_STATUS_DIALOG_NO_BUTTON")));
			return action;
		}

		protected boolean cancelButtonIsVisible() {
			return true;
		}

		protected CellRendererAdapter buildNodeRenderer(Object value) {
			if (value instanceof MWProject) {
				return new ProjectCellRendererAdapter(this.resourceRepository());
			}
			return super.buildNodeRenderer(value);
		}

	}

}
