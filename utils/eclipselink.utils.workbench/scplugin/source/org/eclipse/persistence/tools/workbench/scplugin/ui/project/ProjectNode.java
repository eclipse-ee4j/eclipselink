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
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsPropertiesManager;
import org.eclipse.persistence.tools.workbench.scplugin.ui.broker.SessionBrokerNode;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Main SC Node that holds all the session nodes in a SC project.
 */
public final class ProjectNode extends SessionsNode {

	private boolean firstTimeReadOnlyFlagWasDetected;

	// ********** constructors/initialization **********

	public ProjectNode( TopLinkSessionsAdapter topLinkSessions, TreeNodeValueModel parent, SCPlugin plugin, ApplicationContext context) {

		super( topLinkSessions, parent, plugin, context);
		this.firstTimeReadOnlyFlagWasDetected = topLinkSessions.getPath().canWrite();
	}
	
	// **************** factory methods ****************************************

	protected AbstractApplicationNode buildChildNode( SessionAdapter session) {
		AbstractApplicationNode node = null;
		try {
			node = super.buildChildNode( session);
		}
		catch( IllegalArgumentException e) {
			if( session instanceof SessionBrokerAdapter) 
				node = new SessionBrokerNode(( SessionBrokerAdapter)session, this, ( SCPlugin)this.getPlugin(), this.getApplicationContext());
			else
				throw new IllegalArgumentException( "Invalid Session");
		}
		return node;
	}

	protected CollectionValueModel buildSessionsAspectAdapter() {
		
		return new CollectionAspectAdapter( this, TopLinkSessionsAdapter.SESSIONS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return (( TopLinkSessionsAdapter)subject).sessions();
			}
			protected int sizeFromSubject() {
				return (( TopLinkSessionsAdapter)subject).sessionsSize();
			}
		};
	}

	protected AbstractPropertiesPage buildPropertiesPage(WorkbenchContext context) {

		return new ProjectPropertiesPage(context);
	}

	protected Object propertiesPageKey() {

		return ProjectPropertiesPage.class;
	}

	protected String buildIconKey() {
		return "SESSIONS_CONFIGURATION";
	}
	
	protected FrameworkAction buildRenameNodeAction(WorkbenchContext workbenchContext) {
		// No rename supported for now use SaveAs
		return null;
	}
	
	protected String buildDisplayString() {
		String displayString = super.buildDisplayString();
		File path = this.topLinkSessions().getPath();

		if (path.exists() && !path.canWrite()) {
			displayString = resourceRepository().getString("PROJECT_NODE_DISPLAY_STRING_READ_ONLY", displayString);
		}

		return displayString;
	}

	protected List buildDisplayStringPropertyNamesList() {
		
		List displayStrings = super.buildDisplayStringPropertyNamesList();
		displayStrings.add( TopLinkSessionsAdapter.NAME_PROPERTY );
		return displayStrings;
	}
	
	private FrameworkAction getAddSessionAction(WorkbenchContext workbenchContext) {
		return new AddNewSessionAction(workbenchContext);
	}
	
	private FrameworkAction getAddBrokerAction(WorkbenchContext workbenchContext) {
		return new AddNewBrokerAction(workbenchContext);
	}
	
	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext)
	{
		WorkbenchContext wrappedContext = buildLocalWorkbenchContext(workbenchContext);
		
		RootMenuDescription desc = new RootMenuDescription();
		
		MenuGroupDescription newGroup = new MenuGroupDescription();
		newGroup.add(getAddSessionAction(wrappedContext));
		newGroup.add(getAddBrokerAction(wrappedContext));
		MenuDescription newMenu = new MenuDescription(resourceRepository().getString("NEW_MENU"),
									resourceRepository().getString("NEW_MENU"),resourceRepository().getMnemonic("NEW_MENU"),
									EMPTY_ICON);
		
		newMenu.add(newGroup);
		
		MenuGroupDescription newItemGroup = new MenuGroupDescription();
		newItemGroup.add(newMenu);
		desc.add(newItemGroup);
		
		MenuGroupDescription closeGroup = new MenuGroupDescription();
		closeGroup.add(getCloseAction(workbenchContext));
		desc.add(closeGroup);
		
		MenuGroupDescription saveGroup = new MenuGroupDescription();
		saveGroup.add(getSaveAction(workbenchContext));
		saveGroup.add(getSaveAsAction(workbenchContext));
		desc.add(saveGroup);
		
		desc.add(buildHelpMenuGroup(wrappedContext));
		
		return desc;
	}

	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext)
	{
		WorkbenchContext wrappedContext = buildLocalWorkbenchContext(workbenchContext);

		ToolBarDescription desc = new ToolBarDescription();
		
		ToolBarButtonGroupDescription addGroup = new ToolBarButtonGroupDescription();
		addGroup.add(getAddSessionAction(wrappedContext));
		addGroup.add(getAddBrokerAction(wrappedContext));
		desc.add(addGroup);
		
		return desc;
	}

	protected FrameworkAction getCloseAction(WorkbenchContext workbenchContext) {
		return workbenchContext.getActionRepository().getCloseAction();
	}

	protected FrameworkAction buildDeleteNodeAction(WorkbenchContext workbenchContext) {
		return null; // Always null
	}

	protected FrameworkAction getSaveAction(WorkbenchContext workbenchContext) {
		return workbenchContext.getActionRepository().getSaveAction();
	}
	
	protected FrameworkAction getSaveAsAction(WorkbenchContext workbenchContext) {
		return workbenchContext.getActionRepository().getSaveAsAction();
	}

	TopLinkSessionsAdapter topLinkSessions() {
		
		return ( TopLinkSessionsAdapter)this.getValue();
	}

	public String helpTopicID() {
		return "navigator.scproject";
	}
	// **************** Initialization ****************************************

	public boolean save(File mostRecentSaveDirectory, WorkbenchContext workbenchContext) {
		TopLinkSessionsAdapter sessions = this.topLinkSessions();
		File path = sessions.getPath();
		boolean saved = false;

		// - There is no directory before the file name and it starts with Sessions,
		//   we assume this is an untitled sessions.xml
		// - The location might have been marked read only, always do a Save As
		if( path.getPath().startsWith( SCSessionsPropertiesManager.UNTITLED_FILE_NAME)) {
			saved = saveAs(mostRecentSaveDirectory, workbenchContext);
		}
		else {
			// The Read-Only flag was changed after the file was opened
			if (!path.canWrite()) {
				// Show an error message and ask if we should try again
				if (firstTimeReadOnlyFlagWasDetected) {
					showErrorReadOnlyMessage(workbenchContext);
					firstTimeReadOnlyFlagWasDetected = false;

					// We can try to save again, use Save As path
					if (canRetryToSave(workbenchContext)) {
						saved = saveAs(mostRecentSaveDirectory, workbenchContext);

						if (saved) {
							displayStringChanged();
						}
					}
				}
				// - A Read-Only file was opened and needs to be saved
				// - The save is invoked again on a Read-Only file, right away use the Save As path
				else {
					saved = saveAs(mostRecentSaveDirectory, workbenchContext);
				}
			}
			// The file is not an untitled file, the file is not marked as Read-Only
			// then attempt to save it
			else {
				saved = saveImp(path, workbenchContext);
				firstTimeReadOnlyFlagWasDetected = false;

				if (saved) {
					displayStringChanged();
				}
			}
		}
		return saved;
	}

	private boolean canRetryToSave(WorkbenchContext workbenchContext) {

		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
			workbenchContext.getCurrentWindow(),
			resourceRepository().getString("SAVE_RETRY_TO_SAVE_MESSAGE"),
			workbenchContext.getApplicationContext().getApplication().getShortProductName(),
			JOptionPane.YES_NO_OPTION
		);
	}

	private boolean saveImp( File savePath, WorkbenchContext workbenchContext) {
		TopLinkSessionsAdapter sessions = this.topLinkSessions();

		try {
			sessions.getProperties().saveAs( preferences(), savePath); // Need to be done before sessions.save()
			sessions.save( savePath);
			return true;
		}
		catch( Throwable e) {
			showErrorMessage( e, workbenchContext);
			return false;
		}
	}

	public boolean saveAs(File mostRecentSaveDirectory, WorkbenchContext workbenchContext) {

		File path = topLinkSessions().getPath();
		File directory = path.getParentFile();

		if (directory == null)
			directory = mostRecentSaveDirectory;

		JFileChooser chooser = new FileChooserDialog(workbenchContext);
		chooser.setDialogTitle(resourceRepository().getString( "SAVE_AS_DIALOG_TITLE"));
		chooser.setCurrentDirectory( directory);
		chooser.setSelectedFile( path);
		chooser.setMultiSelectionEnabled( false);
		int result = chooser.showSaveDialog( workbenchContext.getCurrentWindow());

		if( result != JFileChooser.APPROVE_OPTION)
			return false;

		workbenchContext.getNavigatorSelectionModel().pushExpansionState();

		boolean saved = saveImp(chooser.getSelectedFile(), workbenchContext);

		workbenchContext.getNavigatorSelectionModel().popAndRestoreExpansionState();

		return saved;
	}

	public File saveFile() {
		//The framework expects null if the project has never been saved before
		File saveLocation = this.topLinkSessions().getPath();
		return saveLocation.exists() ? saveLocation : null;
	}

	private void showErrorReadOnlyMessage(WorkbenchContext workbenchContext) {

		String fileName = FileTools.canonicalFile(topLinkSessions().getPath()).getPath();

		String message = resourceRepository().getString
		(
			"SAVE_READ_ONLY_ERROR_MESSAGE",
			workbenchContext.getApplicationContext().getApplication().getShortProductName(),
			fileName,
			StringTools.CR
		);

		LabelArea label = new LabelArea(message);

		JOptionPane.showMessageDialog
		(
				workbenchContext.getCurrentWindow(),
			label,
			workbenchContext.getApplicationContext().getApplication().getShortProductName(),
			JOptionPane.WARNING_MESSAGE
		);
	}

	private void showErrorMessage(Throwable exception, WorkbenchContext workbenchContext) {

		((SCPlugin) getPlugin()).showWarning(
			workbenchContext,
			resourceRepository().getString("SAVE_ERROR_MESSAGE", topLinkSessions().getPath().getPath()),
			exception
		);
	}

	/**
	 * This extension over the <code>JFileChooser</code> makes sure the selected
	 * file is valid, ie is not Read-Only, is not currently opened. If it's a
	 * different file, then prompt the user to make sure it is ok to overwrite it.
	 */
	private class FileChooserDialog extends JFileChooser
	{
		private WorkbenchContext workbenchContext;
		private FileChooserDialog(WorkbenchContext workbenchContext) {
			super();
			this.workbenchContext = workbenchContext;
		}
		/**
		 * Determines whether the selected file can be used as the new location to
		 * persist the document.
		 */
		public void approveSelection()
		{
			int result = canReplaceExistingFile();

			if (result == JOptionPane.YES_OPTION)
				super.approveSelection();
			else if (result == JOptionPane.CANCEL_OPTION)
				cancelSelection();
		}

		/**
		 * Verifies if the file (which is currently selected) can be replaced with
		 * the document to be saved.
		 *
		 * @return <code>JOptionPane.YES_OPTION</code> if the file is not
		 * Read-Only, not opened or can be replaced, <code>JOptionPane.NO_OPTION</code>
		 * if the document can't be saved because the selected file is Read-Only
		 * or the user said no to replace it, or <code><code>JOptionPane.NO_OPTION</code></code>
		 * if the user does not want to replace the file and canceled the confirm
		 * dialog
		 */
		private int canReplaceExistingFile()
		{
			File file = getSelectedFile();
			String applicationName = workbenchContext.getApplicationContext().getApplication().getShortProductName();

			// The file is actually opened
			if (isDocumentOpened(file))
			{
				String message = resourceRepository().getString("SAVE_AS_DIALOG_ALREADY_OPENED", applicationName, file, StringTools.CR);
				LabelArea label = new LabelArea(message);
				label.setPreferredWidth(800);

				JOptionPane.showMessageDialog
				(
					workbenchContext.getCurrentWindow(),
					label,
					applicationName,
					JOptionPane.WARNING_MESSAGE
				);

				return JOptionPane.NO_OPTION;
			}

			// The file exist but is marked as Read-Only, show we can't save it
			if (file.exists() && !file.canWrite())
			{
				String message = resourceRepository().getString("SAVE_AS_DIALOG_CANT_SAVE", file);
				LabelArea label = new LabelArea(message);
				label.setPreferredWidth(800);

				JOptionPane.showMessageDialog
				(
					workbenchContext.getCurrentWindow(),
					label,
					applicationName,
					JOptionPane.WARNING_MESSAGE
				);

				return JOptionPane.NO_OPTION;
			}

			// The file exist and is not the file to save, ask to replace it
			if (file.exists() && !topLinkSessions().getPath().equals(file))
			{
				String message = resourceRepository().getString("SAVE_AS_DIALOG_REPLACE", getSelectedFile().getPath());
				LabelArea label = new LabelArea(message);
				label.setPreferredWidth(800);

				return JOptionPane.showConfirmDialog
				(
					workbenchContext.getCurrentWindow(),
					label,
					applicationName,
					JOptionPane.YES_NO_CANCEL_OPTION
				);
			}

			return JOptionPane.YES_OPTION;
		}

		/**
		 * Determines whether a file with the given path is already opened. The
		 * location of this document is not considered during the check.
		 *
		 * @param file The file choosen to save this document
		 * @return <code>true</code> if the document at the given location is
		 * already opened; <code>false</code> otherwise
		 */
		private boolean isDocumentOpened(File file)
		{
			ApplicationNode[] appNodes = nodeManager().projectNodesFor(getPlugin());
			for (int i = 0; i < appNodes.length; i++) {

				ProjectNode node = (ProjectNode) appNodes[i];

				// This is this node, continue
				if (node == ProjectNode.this)
					continue;

				File anotherOpenedFile = node.topLinkSessions().getPath();

				if (file.equals(anotherOpenedFile))
					return true;
			}

			return false;
		}
	}
}
