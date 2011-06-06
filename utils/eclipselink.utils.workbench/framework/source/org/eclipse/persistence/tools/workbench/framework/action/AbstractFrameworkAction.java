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
package org.eclipse.persistence.tools.workbench.framework.action;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.ShellWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;


/**
 * This extension of AbstractAction ties an action to an application context.
 * Subclasses have access to the selected nodes via the application context's
 * node manager.
 * 
 * Subclasses must override #initialize() and at least one of the
 * following #execute() methods:
 * 
 * #execute()  Override this method if the action is not related to
 * 	the tree selection or you would like to do something other
 * 	than iterate through all the selected nodes.
 * 
 * #execute(Node)  Override this method if you would like to perform
 * 	the same thing on each selected node. This method will be
 * 	called for each of the selected nodes.
 * 
 * This class also adds helper methods for setting the following properties:
 * 	- text
 * 	- mnemonic	
 * 	- accelerator
 * 	- icon
 * 	- tool-tip text
 */
public abstract class AbstractFrameworkAction extends AbstractAction implements FrameworkAction {
	/** this is where we get the various properties from */
	private WorkbenchContext workbenchContext;

	protected static final Icon EMPTY_ICON = new EmptyIcon(16);


	// ********** constructor/initialization **********

	/**
	 * This constructor can be used by "typical" actions that are associated
	 * with a single workbench window (e.g. the drop-down menus, the tool
	 * bars, the navigator pop-up menus)
	 */
	protected AbstractFrameworkAction(WorkbenchContext workbenchContext) {
		super();
		this.workbenchContext = workbenchContext;
		this.initialize();
	}

	/**
	 * This constructor can be used by actions that are associated with a
	 * properties page or panel that can be associated with multiple workbench
	 * windows (e.g. "add" buttons on nested panels); the action itself cannot
	 * use its *own* workbench context (since it will be a "shell" workbench
	 * context), but it can make calls to its panel, which *will* have the correct
	 * workbench context (since the panel is using a workbench context holder
	 * that is updated when the panel is installed on a different workbench
	 * window)
	 */
	protected AbstractFrameworkAction(ApplicationContext applicationContext) {
		this(new ShellWorkbenchContext(applicationContext));
	}

	/**
	 * Initialize the action's text, mnemonic, accelerator,
	 * icon, tool-tip text, etc.
	 */
	protected void initialize() {
		//call super.initialize() when overriding this method
	}

	protected void initializeText(String key) {
		this.setText(this.resourceRepository().getString(key));
	}

	protected void initializeMnemonic(String key) {
		this.setMnemonic(this.resourceRepository().getMnemonic(key));
	}

	/**
	 * most of the time the text and mnemonic share the same key
	 */
	protected void initializeTextAndMnemonic(String key) {
		this.initializeText(key);
		this.initializeMnemonic(key);
	}

	protected void initializeAccelerator(String key) {
		this.setAccelerator(this.resourceRepository().getAccelerator(key));
	}

	protected void initializeIcon(String key) {
		this.setIcon(this.resourceRepository().getIcon(key));
	}

	protected void initializeToolTipText(String key) {
		this.setToolTipText(this.resourceRepository().getString(key));
	}

	public void setUp() {
		// no listeners to add, see subclasses
	}
	
	public void tearDown() {
		// no listeners to remove, see subclasses
	}
	
	
	// ********** ActionListener implementation **********

	/**
	 * By default we invoke the #execute() method.
	 */
	public void actionPerformed(ActionEvent e) {	
		this.currentWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.execute();
		this.currentWindow().setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * This method is called by default from #actionPerformed(ActionEvent).
	 * Subclasses can override this method if the action is not related to
	 * the tree selection or the action performs something on all
	 * the nodes (not just the selected nodes).
	 * 
	 * Loop through the selected nodes, calling #execute(ApplicationNode).
	 */
	protected void execute() {
		ApplicationNode[] nodes = this.selectedNodes();
		for (int i = nodes.length; i-- > 0; ) {
			this.execute(nodes[i]);
		}
	}

	/**
	 * This method is called once for each node selected in the tree.
	 * Unsupported - Subclasses must override either this method or #execute().
	 */
	protected void execute(ApplicationNode selectedNode) {
		throw new UnsupportedOperationException();
	}
		

	// ********** FrameworkAction implementation **********

	public String getText() {
		return (String) this.getValue(NAME);
	}

	public int getMnemonic() {
		Integer mnemonic = (Integer) this.getValue(MNEMONIC_KEY);
		return (mnemonic == null) ? - 1 : mnemonic.intValue();
	}

	public KeyStroke getAccelerator() {
		return (KeyStroke) this.getValue(ACCELERATOR_KEY);
	}

	public Icon getIcon() {
		return (Icon) this.getValue(SMALL_ICON);
	}

	public String getToolTipText() {
		return (String) this.getValue(SHORT_DESCRIPTION);
	}


	// ********** public setters **********

	public void setIcon(Icon icon) {
		this.putValue(SMALL_ICON, icon);
	}

	public void setText(String text) {
		this.putValue(NAME, text);
	}

	public void setToolTipText(String toolTipText) {
		this.putValue(SHORT_DESCRIPTION, toolTipText);
	}

	public void setAccelerator(KeyStroke accelerator) {
		this.putValue(ACCELERATOR_KEY, accelerator);
	}

	public void setMnemonic(int mnemonic) {
		this.putValue(MNEMONIC_KEY, new Integer(mnemonic));
	}


	// ********** convenience methods **********

	public ApplicationContext getApplicationContext() {
		return this.getWorkbenchContext().getApplicationContext();
	}

	public WorkbenchContext getWorkbenchContext() {
		return this.workbenchContext;
	}

	public Application application() {
		return this.getApplicationContext().getApplication();
	}

	public Preferences preferences() {
		return this.getApplicationContext().getPreferences();
	}

	public ResourceRepository resourceRepository() {
		return this.getApplicationContext().getResourceRepository();
	}

	public NodeManager nodeManager() {
		return this.getApplicationContext().getNodeManager();
	}

	public HelpManager helpManager() {
		return this.getApplicationContext().getHelpManager();
	}

	public Window currentWindow() {
		return this.getWorkbenchContext().getCurrentWindow();
	}
	
	public NavigatorSelectionModel navigatorSelectionModel() {
		return this.getWorkbenchContext().getNavigatorSelectionModel();
	}

	public ApplicationNode[] selectedNodes() {
		return this.navigatorSelectionModel().getSelectedNodes();
	}

	public ApplicationNode[] selectedProjectNodes() {
		return this.navigatorSelectionModel().getSelectedProjectNodes();
	}
	
	/**
	 * Default classification for a FrameworkAction is simply the 
	 * class name.
	 */
	public String getClassification() {
		return this.getClass().getName();
	}

	/**
	 * Build a launcher that can be dispatched via the AWT EventQueue.
	 */
	public static Runnable buildDialogLauncher(final Dialog d) {
		return new Runnable() {
			public void run() {
				d.show();
			}
		};
	}

	/**
	 * Asynchronously launch the specified dialog via the AWT EventQueue.
	 * This is useful for opening a "wait" dialog while an action is executing.
	 */
	public static void launchLater(Dialog d) {
		EventQueue.invokeLater(buildDialogLauncher(d));
	}

	/**
	 * Synchronously launch the specified dialog via the AWT EventQueue.
	 * This is useful for opening a dialog in another thread.
	 */
	public static void launchAndWait(Dialog d) {
		try {
			EventQueue.invokeAndWait(buildDialogLauncher(d));
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
