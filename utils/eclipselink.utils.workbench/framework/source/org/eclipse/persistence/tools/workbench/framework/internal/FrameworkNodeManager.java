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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.OpenException;
import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.UnsupportedFileException;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.CancelException;
import org.eclipse.persistence.tools.workbench.uitools.PreferencesRecentFilesManager;
import org.eclipse.persistence.tools.workbench.uitools.RecentFilesManager;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.SynchronizedBoolean;
import org.eclipse.persistence.tools.workbench.utility.events.AWTChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.events.DefaultChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.PluggableValidator;
import org.eclipse.persistence.tools.workbench.utility.node.RunnableValidation;


/**
 * This is the model that corresponds to the root node in all the
 * navigator trees. It's a little different than a typical model, in that it
 * holds on to application nodes directly, as opposed to holding on
 * to the models corresponding to those application nodes.
 */
final class FrameworkNodeManager
	extends AbstractNodeModel
	implements NodeManager
{

	/** Backpointer to the application. */
	private FrameworkApplication application;

	private TreeNodeValueModel rootNode;

	private Collection projectNodes;
		public static final String PROJECT_NODES_COLLECTION = "projectNodes";

	/** The validation threads and flags, keyed by the project nodes. */
	private Map validationThreads;
	private Map continueValidationThreadFlags;

	/** The project nodes with synchronous validators - used only at development time. */
	private Collection synchronousProjectNodes;
	private boolean projectNodesAreAddedWithSynchronousValidators;

	/** Maintain a list of the recently-opened files - save as a preference. */
	private RecentFilesManager recentFilesManager;


	private static final String RECENT_FILES_PREFERENCES_NODE = "recent files";
	static final String RECENT_FILES_MAX_SIZE_PREFERENCE = "recent files max size";
		static final int RECENT_FILES_MAX_SIZE_PREFERENCE_DEFAULT = RecentFilesManager.DEFAULT_MAX_SIZE;

	private static final String MOST_RECENT_SAVE_LOCATION_PREFERENCE = "recent save location";
		private static final String MOST_RECENT_SAVE_LOCATION_PREFERENCE_DEFAULT = FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();
	

	// ********** constructors/initialization **********

	FrameworkNodeManager(FrameworkApplication application) {
		super(null);	// this node never has a parent
		this.application = application;
		this.recentFilesManager = this.buildRecentFilesManager();
	}

	protected void initialize() {
		super.initialize();
		this.rootNode = new FrameworkRootNode(this);
		this.projectNodes = new ArrayList();
		this.validationThreads = new HashMap();
		this.continueValidationThreadFlags = new HashMap();
		this.synchronousProjectNodes = new HashBag();
		this.projectNodesAreAddedWithSynchronousValidators = false;
	}

	/**
	 * Build a recent files manager that stores the recently-opened
	 * files in a preferences node.
	 */
	private RecentFilesManager buildRecentFilesManager() {
		Preferences baseNode = this.application.generalPreferences();
		Preferences recentFilesNode = baseNode.node(RECENT_FILES_PREFERENCES_NODE);
		return new PreferencesRecentFilesManager(recentFilesNode, baseNode, RECENT_FILES_MAX_SIZE_PREFERENCE);
	}

	protected void checkParent(Node parent) {
		if (parent != null) {
			throw new IllegalArgumentException(ClassTools.shortClassNameForObject(this) + " should not have a parent");
		}
	}


	// ********** NodeManager implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.NodeManager#addProjectNode(org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode)
	 * Install the appropriate validator on the "project-level" node
	 * and add it to our collection of "project-level" nodes.
	 */
	public void addProjectNode(ApplicationNode projectNode) {
		// adding the project node to the tree will trigger the building
		// of all the application nodes;
		// so do this *before* kicking off the validation thread, so
		// that all the nodes' "application" problems are in synch with and
		// listening to the "model" problems
		this.addItemToCollection(projectNode, this.projectNodes, PROJECT_NODES_COLLECTION);
		((Node) projectNode.getValue()).setChangeNotifier(AWTChangeNotifier.instance());
		if (this.projectNodesAreAddedWithSynchronousValidators) {
			this.installSynchronousValidatorOn(projectNode);
		} else {
			this.installAsynchronousValidatorOn(projectNode);
		}
	}

	/**
	 * Build, cache, and start a validation thread for the specified "project-level" node.
	 * Hook up the thread to the node via an asynchronous validator.
	 */
	private void installAsynchronousValidatorOn(ApplicationNode projectNode) {
		Node node = (Node) projectNode.getValue();

		// the "validate" flag is shared by the node's validator and the validation thread;
		// initialize it to true so the the node is immediately validated
		SynchronizedBoolean validateFlag = new SynchronizedBoolean(true);
		node.setValidator(PluggableValidator.buildAsynchronousValidator(validateFlag));

		// the "continue" flag is shared by this node manager and the validation thread
		SynchronizedBoolean continueFlag = new SynchronizedBoolean(true);
		this.continueValidationThreadFlags.put(projectNode, continueFlag);

		Thread validationThread =
			new Thread(
				new RunnableValidation(
					node,
					validateFlag,
					continueFlag,
					this.logger(),
					Level.WARNING,
					"VALIDATION_EXCEPTION"
				),
				"Validation Thread : " + node.displayString()
			);
		validationThread.setPriority(Thread.MIN_PRIORITY);

		this.validationThreads.put(projectNode, validationThread);
		validationThread.start();
	}

	/**
	 * Install a "synchronous" validator on the specified "project-level" node.
	 * This simplifies debugging of model validation.
	 * This should only be used during development.
	 */
	private void installSynchronousValidatorOn(ApplicationNode projectNode) {
		Node node = (Node) projectNode.getValue();
		node.setValidator(PluggableValidator.buildSynchronousValidator(node));
		this.synchronousProjectNodes.add(projectNode);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.NodeManager#removeProjectNode(org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode)
	 */
	public void removeProjectNode(ApplicationNode projectNode) {
		this.removeItemFromCollection(projectNode, this.projectNodes, PROJECT_NODES_COLLECTION);

		if ( ! this.synchronousProjectNodes.remove(projectNode)) {
			SynchronizedBoolean continueFlag = (SynchronizedBoolean) this.continueValidationThreadFlags.remove(projectNode);
			continueFlag.setFalse();
	
			Thread validationThread = (Thread) this.validationThreads.remove(projectNode);
			validationThread.interrupt();
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.NodeManager#projectNodesFor(org.eclipse.persistence.tools.workbench.framework.Plugin)
	 */
	public ApplicationNode[] projectNodesFor(Plugin plugin) {
		Collection nodes = new ArrayList();
		for (Iterator stream = this.projectNodes(); stream.hasNext(); ) {
			ApplicationNode node = ((ApplicationNode) stream.next());
			if (node.getPlugin() == plugin) {
				nodes.add(node);
			}
		}	
		return (ApplicationNode[]) nodes.toArray(new ApplicationNode[nodes.size()]);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.NodeManager#getRootNode()
	 */
	public TreeNodeValueModel getRootNode() {
		return this.rootNode;
	}


	// ********** AbstractNodeModel implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.node.Node#displayString()
	 */
	public String displayString() {
		// this node should never be visible
		return null;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel#getChangeNotifier()
	 */
	public ChangeNotifier getChangeNotifier() {
		return DefaultChangeNotifier.instance();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel#getValidator()
	 */
	public Validator getValidator() {
		return Node.NULL_VALIDATOR;
	}



	// ********** queries **********

	/**
	 * Return the "project-level" nodes held by the node manager.
	 */
	Iterator projectNodes() {
		return this.projectNodes.iterator();
	}

	/**
	 * Return the number of "project-level" nodes held by the node manager.
	 */
	int projectNodesSize() {
		return this.projectNodes.size();
	}

	/**
	 * Return the recent files manager that maintains a list
	 * of the recently-opened files.
	 */
	RecentFilesManager getRecentFilesManager() {
		return this.recentFilesManager;
	}

	boolean projectNodesAreAddedWithSynchronousValidators() {
		return this.projectNodesAreAddedWithSynchronousValidators;
	}

	/**
	 * Return the new setting.
	 */
	boolean toggleAddProjectNodesWithSynchronousValidators() {
		this.projectNodesAreAddedWithSynchronousValidators = ! this.projectNodesAreAddedWithSynchronousValidators;
		return this.projectNodesAreAddedWithSynchronousValidators;
	}

	private Logger logger() {
		return this.application.getLogger();
	}


	// ********** opening nodes **********

	/**
	 * Prevent the user from the opening the same file twice.
	 * Ask the user to revert to saved if the file is already open and modified.
	 */
	void open(File file, WorkbenchContext context) {
		ApplicationNode projectNode = this.projectNodeFor(file);
		if (projectNode == null) {
			this.openNew(file, context);
			return;
		}

		if (projectNode.isDirty() && this.userWantsToRevert(file, context)) {
			// remove the node and re-read the file
			context.getNavigatorSelectionModel().pushExpansionState();
			this.removeProjectNode(projectNode);
			this.openNew(file, context);
			context.getNavigatorSelectionModel().popAndRestoreExpansionState();
		} else {
			// select the node corresponding to the selected file
			context.getNavigatorSelectionModel().setSelectedNode(projectNode);
		}

	}

	/**
	 * Return the "project-level" node corresponding to the specified file.
	 * Return null if there is no corresponding node.
	 */
	private ApplicationNode projectNodeFor(File file) {
		for (Iterator stream = this.projectNodes(); stream.hasNext(); ) {
			ApplicationNode projectNode = (ApplicationNode) stream.next();
			File saveLocation = projectNode.saveFile();
			if ((saveLocation != null) && saveLocation.equals(file)) {
				return projectNode;
			}
		}
		return null;
	}

	/**
	 * Fork off a thread to load the project for the specified file,
	 * allowing us to return control to the caller (typically an action).
	 */
	private void openNew(File file, WorkbenchContext context) {
		Thread thread = new Thread(new RunnableProjectLoader(this, file, context), "Project Loader");
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
	}

	/**
	 * This is called by the ProjectLoader once the "Wait..." dialog has
	 * been launched. Synchronize this method so multiple
	 * ProjectLoaders will not interfere with each other.
	 */
	synchronized ApplicationNode openCallback(File file, WorkbenchContext context) throws UnsupportedFileException, OpenException {
		return this.application.open(file, context);
	}

	/**
	 * Open the specified file and add the new node to the node manager's
	 * "project-level" nodes. Also select the new node in the current navigator
	 * and add the file to the recent files list.
	 * This is called by the ProjectLoader once a project has
	 * been successfully read in. This method should not need
	 * to be synchronized since it should always be called in
	 * the AWT event dispatcher thread.
	 */
	void addProjectNodeCallback(ApplicationNode node, File file, WorkbenchContext context) {
		this.addProjectNode(node);
		context.getNavigatorSelectionModel().expandNode(node);
		context.getNavigatorSelectionModel().setSelectedNode(node);
		this.recentFilesManager.setMostRecentFile(file);
	}

	/**
	 * Ask the user whether she wants to revert to the saved file.
	 * Return whether she answers yes.
	 */
	private boolean userWantsToRevert(File file, WorkbenchContext context) {
		int option = JOptionPane.showConfirmDialog(
											context.getCurrentWindow(),
											context.getApplicationContext().getResourceRepository().getString("REVERT_TO_SAVED.message", file),
											context.getApplicationContext().getResourceRepository().getString("REVERT_TO_SAVED.title"),
											JOptionPane.YES_NO_OPTION
		);
		return (option == JOptionPane.YES_OPTION);
	}
	

	// ********** closing nodes **********

	/**
	 * First check for any dirty projects; if there are any, prompt the
	 * user to save them. Then exit the application.
	 * This method is called in 2 situations:
	 *     - when the user selects File -> Exit
	 *     - when the user closes the last window
	 */
	void exit(WorkbenchContext context) {
		Collection nodes = new ArrayList(this.projectNodes);
		this.application.saveTreeExpansionStates();
		if (this.closeAll(context)) {
			// TODO - something else once we have multi-window support ~kfm
			saveProjectsState(nodes.iterator());
			nodes = null;
			this.application.exit();
		}
	}
	
	//TODO possibly maintain this as we go along?
	private void saveProjectsState(Iterator nodes) {
		Preferences projectPreferences =  this.application.generalPreferences().node("projects");
		try {
			projectPreferences.clear();
		} catch (BackingStoreException e) {
			//do nothing if this occurs
		}
		int i = 0;
		while (nodes.hasNext()) {
			ApplicationNode projectNode = (ApplicationNode) nodes.next();
			if (projectNode.saveFile() != null) {
				projectPreferences.put(String.valueOf(i++), projectNode.saveFile().getAbsolutePath());
			}
		}
		
	}
	
	protected void restoreProjectsState(WorkbenchWindow window, Preferences windowPreferences) {
		Preferences projectPreferences = this.application.generalPreferences().node("projects");
		String[] keys;
		try {
			keys = projectPreferences.keys();
		} catch (BackingStoreException e) {
			return;
		}
		for (int i = 0; i < keys.length; i++) {
			String projectLocation = projectPreferences.get(keys[i], null);
			if (projectLocation != null) {
				File projectFile = new File(projectLocation);
				if (projectFile.exists()) {
					open(projectFile, window.getContext());
				}
			}
		}
		window.restoreTreeExpansionState(windowPreferences);
	}
	
	/**
	 * Close all the nodes currently held by the node manager.
	 * Return whether the nodes were (saved and) closed successfully.
	 */
	boolean closeAll(WorkbenchContext context) {
		return close((ApplicationNode[]) this.projectNodes.toArray(new ApplicationNode[this.projectNodes.size()]), context);
	}

	/**
	 * Close all the nodes currently held by the node manager.
	 * Return whether the nodes were (saved and) closed successfully.
	 */
	boolean close(ApplicationNode[] nodes, WorkbenchContext context) {	 
		Collection dirtyNodesToSave;
		try {
			dirtyNodesToSave = this.promptToSave(this.dirtyNodesFrom(nodes), context);
		} catch (CancelException e) {
			// if the user cancels the save dialog, cancel the entire close process
			return false;
		}

		for (Iterator stream = dirtyNodesToSave.iterator(); stream.hasNext(); ) {
			if ( ! this.save((ApplicationNode) stream.next(), context)) {
				// if any of the saves fail, cancel the entire close process
				return false;
			}
		}

		for (int i = nodes.length; i-- > 0; ) {
			this.removeProjectNode(nodes[i]);
		}
		return true;	
	}

	/**
	 * Extract a collection of the dirty nodes from the specified
	 * collection of nodes.
	 */
	private Collection dirtyNodesFrom(ApplicationNode[] nodes) {
		Collection dirtyNodes = new ArrayList(nodes.length);
		for (int i = nodes.length; i-- > 0; ) {
			ApplicationNode node = nodes[i];
			if (node.isDirty()) {
				dirtyNodes.add(node);
			}
		}	
		return dirtyNodes;
	}

	/**
	 * Display a dialog to the user with a list of the "projects" that are dirty
	 * and need to be saved. Return a collection of the dirty "projects"
	 * selected by the user to be saved.
	 */
	private Collection promptToSave(Collection dirtyNodes, WorkbenchContext context) {
		if (dirtyNodes.isEmpty()) {
			return Collections.EMPTY_SET;
		}
		SaveModifiedProjectsDialog dialog = new SaveModifiedProjectsDialog(context, dirtyNodes);
		dialog.show();

		if (dialog.wasCanceled()) {
			throw new CancelException();
		}

		return dialog.selectedNodes();
	}


	// ********** saving nodes **********

	/**
	 * Save the specified node and, if it was saved successfully,
	 * add it to the recent files list. Return whether the node was saved.
	 */
	public boolean save(ApplicationNode node, WorkbenchContext workbenchContext) {

		boolean saved = node.save(getMostRecentSaveDirectory(), workbenchContext);
		if (saved) {
			this.recentFilesManager.setMostRecentFile(node.getProjectRoot().saveFile());
			setMostRecentSaveDirectory(node.getProjectRoot().saveFile());
		}
		return saved;
	}

	private File getMostRecentSaveDirectory() {
		return new File(this.application.generalPreferences().get(MOST_RECENT_SAVE_LOCATION_PREFERENCE, MOST_RECENT_SAVE_LOCATION_PREFERENCE_DEFAULT));	
	}
	
	private void setMostRecentSaveDirectory(File saveLocation) {
		this.application.generalPreferences().put(MOST_RECENT_SAVE_LOCATION_PREFERENCE, saveLocation.getParentFile().getAbsolutePath());
	}	
	
	/**
	 * Save the specified node in a new location and, if it was saved successfully,
	 * add it to the recent files list. Return whether the node was saved.
	 */
	boolean saveAs(ApplicationNode node,  WorkbenchContext context) {
		boolean saved = node.saveAs(getMostRecentSaveDirectory(), context);
		if (saved) {
			this.recentFilesManager.setMostRecentFile(node.getProjectRoot().saveFile());
			setMostRecentSaveDirectory(node.getProjectRoot().saveFile());
		}
		return saved;
	}

	/**
	 * Save all the dirty nodes and, if they were saved successfully,
	 * add them to the recent files list.
	 */
	void saveAll(WorkbenchContext workbenchContext) {
		for (Iterator stream = this.projectNodes(); stream.hasNext(); ) {
			ApplicationNode node = (ApplicationNode) stream.next();
			if (node.isDirty()) {
				this.save(node, workbenchContext);
			}
		}
	}

}
