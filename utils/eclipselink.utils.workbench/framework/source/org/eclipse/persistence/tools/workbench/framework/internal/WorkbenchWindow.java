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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ActionRepository;
import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ComponentContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.context.AbstractWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpFacade;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.DropDownButton;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;


/**
 * This is the main window opened by the application.
 * 
 * TODO allow multiple workspaces per workbench
 */
final class WorkbenchWindow 
	extends JFrame 
{
	/** A backpointer to the application that created this workbench window. */
	private FrameworkApplication application;

	/** The context passed to just about everybody. */
	private WorkbenchContext context;

	/** The main part of the window: a navigator, an editor, and a problem list. */
	private WorkspaceView workspace;

	/** Keep the File menu in synch with the list of recently-opened files. */
	private JMenu fileMenu;
	private int recentFilesSize;
	private StateChangeListener recentFilesListener;

	/** This holds the actions shared by the menus and toolbars and the plug-ins. */
	private FrameworkActionRepository actionRepository;

	/** This allows us to control the "Show Problems" check box menu item. */
	private ButtonModel showProblemsButtonModel;

	/**
	 * This allows us to control the development-time-only
	 * "Synchronous Problems" check box menu item.
	 */
	private ButtonModel synchronousProblemsButtonModel;

	/** Wrap the menu description corresponding to the selected nodes */
	private PropertyValueModel selectionMenuDescriptionHolder;

	/**
	 * Listen to the tree selection so we can update
	 * the menus, tool bars, and actions, below.
	 */
	private TreeSelectionListener treeSelectionListener;

	/**
	 * These menus and toolbars are updated with every
	 * change to the tree selection.
	 */
	private JMenu workbenchMenu;
	private JMenu selectionMenu;

	/** Collections of actions, keyed by plug-in. */
	private Map pluginToolBarActions;
	private JToolBar selectionToolBar;
	
	/**
	 * Hold the actions associated with the selected node(s) so we
	 * can tear them down before replacing them with new actions
	 * when the node selection changes.
	 */
	private Set selectionActions;
	
	private Set workbenchActions;

	/** hold the listener so we can remove it */
	private WindowListener windowListener;


	private static final Icon EMPTY_ICON = new EmptyIcon(16);

//	private static final Border STATUS_BAR_BORDER =
//		BorderFactory.createCompoundBorder(
//			new EmptyBorder(2, 0, 0, 0),
//			BorderFactory.createCompoundBorder(
//				new ThinBevelBorder(BevelBorder.LOWERED),
//				new EmptyBorder(0, 2, 0, 2)
//			)
//		);


	// ********** constructors **********

	/**
	 * The workbench window works closely with the framework application.
	 */
	WorkbenchWindow(FrameworkApplication application) {
		super();
		this.application = application;
		this.initialize();
	}
	
	/**
	 * Construct a workbench window that takes its initial settings from
	 * an existing workbench window.
	 */
	WorkbenchWindow(FrameworkApplication application, WorkbenchWindow workbenchWindow) {
		this(application);
		this.copySettingsFrom(workbenchWindow);
	}


	// ********** initialization **********

	private void initialize() {
		this.recentFilesSize = 0;
		this.recentFilesListener = this.buildRecentFilesListener();
		this.application.recentFilesManager().addStateChangeListener(this.recentFilesListener);

		this.context = new LocalWorkbenchContext();
		this.selectionMenuDescriptionHolder = new SimplePropertyValueModel();
		this.workspace = new WorkspaceView(this.context, this.selectionMenuDescriptionHolder);
		this.actionRepository = new FrameworkActionRepository(this);
		this.selectionActions = new HashSet();
		this.workbenchActions = new HashSet();

		this.setBounds(50, 50, 800, 725);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.initializeTitleBar();
		this.windowListener = new LocalWindowListener();
		this.addWindowListener(this.windowListener);

		this.getRootPane().setJMenuBar(this.buildMenuBar());
		this.getContentPane().add(this.buildToolBarPanel(), BorderLayout.NORTH);
		this.getContentPane().add(this.workspace.getComponent(), BorderLayout.CENTER);

		this.treeSelectionListener = this.buildTreeSelectionListener();
		this.getWorkspace().addNavigatorTreeSelectionListener(this.treeSelectionListener);
	}

	private void initializeTitleBar() {	
		this.setTitle(this.application.getFullProductName());

		Icon icon = this.resourceRepository().getIcon("oracle.logo.large");
		if (icon instanceof ImageIcon) {
			this.setIconImage(((ImageIcon) icon).getImage());
		}
	}

	private void copySettingsFrom(WorkbenchWindow workbenchWindow) {
		this.setSize(workbenchWindow.getWidth(), workbenchWindow.getHeight());
		this.setExtendedState(workbenchWindow.getExtendedState());

		// "cascade" the window
		int titleBarHeight = workbenchWindow.getLocationOnScreen().y - workbenchWindow.getRootPane().getLocationOnScreen().y;
		int x = workbenchWindow.getLocationOnScreen().x - titleBarHeight;
		int y = workbenchWindow.getLocationOnScreen().y - titleBarHeight;

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = this.getSize();
		if ((x + windowSize.width > screenSize.width) || (y + windowSize.height > screenSize.height)) {
			this.setLocation(0, 0);
		} else {
			this.setLocation(x, y);
		}		

		this.workspace.copySettingsFrom(workbenchWindow.getWorkspace());
	}
	
	
	// ********** menus **********

	private JMenuBar buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// hold on to the "File" menu so we can manipulate the recent file list
		this.fileMenu = this.buildFileMenu();
		this.rebuildRecentFilesMenuItems();
		menuBar.add(this.fileMenu);

		// hold on to the "Workbench" and "Selected" menus so we can
		// disable them when appropriate
		this.workbenchMenu = this.buildWorkbenchMenu();
		menuBar.add(this.workbenchMenu);

		this.selectionMenu = this.buildSelectionMenu();
		menuBar.add(this.selectionMenu);

		menuBar.add(this.buildToolsMenu());
		menuBar.add(this.buildWindowMenu());
		menuBar.add(this.buildHelpMenu());
		
		return menuBar;
	}

	private JMenu buildFileMenu() {		
		JMenu menu = new JMenu();
		menu.setText(resourceRepository().getString("FILE_MENU"));
		menu.setMnemonic(resourceRepository().getMnemonic("FILE_MENU"));

		menu.add(this.buildNewMenu());
		menu.add(new JMenuItem(this.actionRepository.getOpenAction()));
		menu.addSeparator();
		menu.add(new JMenuItem(new WorkbenchSaveAction(getContext(), nodeManager())));
		menu.add(new JMenuItem(new WorkbenchSaveAsAction(getContext(), nodeManager())));
		menu.add(new JMenuItem(this.actionRepository.getSaveAllAction()));
		menu.addSeparator();
		menu.add(new JMenuItem(new WorkbenchCloseAction(getContext(), nodeManager())));
		menu.add(new JMenuItem(this.actionRepository.getCloseAllAction()));
		menu.addSeparator();
	//	menu.add(this.buildMigrateMenu());
		menu.addSeparator();
		// the recent files will be put here later - @see #rebuildRecentFilesMenuItems()
		menu.addSeparator();
		menu.add(new JMenuItem(this.actionRepository.getExitAction()));

		return menu;
	}

	private JMenu buildNewMenu() {
		JMenu menu = new JMenu();
		menu.setIcon(this.resourceRepository().getIcon("file"));
		menu.setText(this.resourceRepository().getString("file.new"));
		menu.setMnemonic(this.resourceRepository().getMnemonic("file.new"));
		menu.setToolTipText(this.resourceRepository().getString("file.new.toolTipText"));

		Plugin[] plugins = this.plugins();
		for (int i = 0; i < plugins.length; i++) {
			JMenuItem[] menuItems = plugins[i].buildNewMenuItems(this.context);
			for (int j = 0; j < menuItems.length; j++) {
				menu.add(menuItems[j]);
			}
		}
		return menu;
	}
	
	private JMenu buildMigrateMenu() {
		JMenu menu = new JMenu();
		menu.setIcon(EMPTY_ICON);
		menu.setText(this.resourceRepository().getString("file.migrate"));
		menu.setMnemonic(this.resourceRepository().getMnemonic("file.migrate"));
		menu.setToolTipText(this.resourceRepository().getString("file.migrate.toolTipText"));

		Plugin[] plugins = this.plugins();
	//	for (int i = 0; i < plugins.length; i++) {
	//		JMenuItem[] menuItems = plugins[i].buildMigrateMenuItems(this.context);
	//		for (int j = 0; j < menuItems.length; j++) {
	//			menu.add(menuItems[j]);
	//		}
	//	}
		return menu;
	}

	/**
	 * the "Workbench" menu starts out empty and gets populated
	 * when a node is selected
	 */
	private JMenu buildWorkbenchMenu() {	
		JMenu menu = new JMenu();
		menu.setText(this.resourceRepository().getString("WORKBENCH_MENU"));
		menu.setMnemonic(this.resourceRepository().getMnemonic("WORKBENCH_MENU"));
		menu.setEnabled(false);
		return menu;		
	}	

	/**
	 * the "Selected" menu starts out empty and gets populated
	 * when a node is selected
	 */
	private JMenu buildSelectionMenu() {	
		JMenu menu = new JMenu();
		menu.setText(this.resourceRepository().getString("SELECTED_MENU"));
		menu.setMnemonic(this.resourceRepository().getMnemonic("SELECTED_MENU"));
		menu.setEnabled(false);
		return menu;		
	}	

	private JMenu buildToolsMenu() {		
		JMenu menu = new JMenu();
		menu.setText(this.resourceRepository().getString("TOOLS_MENU"));			
		menu.setMnemonic(this.resourceRepository().getMnemonic("TOOLS_MENU"));

		menu.add(new GoToAction(this.getContext()));
		menu.add(this.workspace.problemReportAction());
		menu.add(new PreferencesAction(this.getContext(), this.application));
		if (this.application.isDevelopmentMode()) {
			menu.add(this.buildDevelopmentMenu());
		}

		return menu;		
	}

	/**
	 * This menu should only appear in "development" mode.
	 */
	private JMenu buildDevelopmentMenu() {
		JMenu menu = new JMenu();
		menu.setIcon(EMPTY_ICON);
		menu.setText(this.resourceRepository().getString("DEV_MENU"));			
		menu.setMnemonic(this.resourceRepository().getMnemonic("DEV_MENU"));

		menu.add(new DevelopmentConsoleAction(this.getContext(), this.application));
		menu.add(HelpFacade.buildHelpTopicIDWindowAction(this.getContext()));
		menu.add(new ThreadBrowserAction(this.getContext()));
		menu.add(new JavaHeapInformationAction(this.getContext()));
		menu.add(this.buildSynchronousProblemsMenuItem());

		return menu;		
	}

	private JMenuItem buildSynchronousProblemsMenuItem() {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(new SynchronousProblemsAction(this));
		this.synchronousProblemsButtonModel = item.getModel();
		// the workspace is already built by now
		this.synchronousProblemsButtonModel.setSelected(this.application.getNodeManager().projectNodesAreAddedWithSynchronousValidators());
		return item;
	}

	private JMenu buildWindowMenu() {	
		JMenu menu = new JMenu();
		menu.setText(this.resourceRepository().getString("WINDOW_MENU"));
		menu.setMnemonic(this.resourceRepository().getMnemonic("WINDOW_MENU"));

		menu.add(new NewWindowAction(this.getContext(), this.application));
		menu.add(this.buildShowProblemsMenuItem());

		return menu;		
	}

	private JMenuItem buildShowProblemsMenuItem() {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(new ShowProblemsAction(this));
		this.showProblemsButtonModel = item.getModel();
		// the workspace is already built by now
		this.showProblemsButtonModel.setSelected(this.workspace.problemsAreVisible());
		return item;
	}
		
	private JMenu buildHelpMenu() {	
		JMenu menu = new JMenu();
		menu.setText(this.resourceRepository().getString("HELP_MENU"));
		menu.setMnemonic(this.resourceRepository().getMnemonic("HELP_MENU"));

		menu.add(new HomeAction(this.getContext()));
		menu.add(new UsersGuideAction(this.getContext()));
		menu.add(new ExamplesAction(this.getContext()));
		menu.add(new JavaDocAction(this.getContext()));	
		//menu.addSeparator();
		//menu.add(new ReleaseNotesAction(this.getContext()));		
		menu.addSeparator();
		menu.add(new AboutAction(this.getContext()));

		return menu;		
	}


	// ********** toolbars **********

	private JPanel buildToolBarPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.add(this.buildMainToolBar());
		this.selectionToolBar = this.buildToolBar("SELECTION_TOOL_BAR");
		panel.add(this.selectionToolBar);
		return panel;
	}

	private JToolBar buildMainToolBar() {
		JToolBar toolBar = this.buildToolBar("MAIN_TOOL_BAR");
		toolBar.add(this.buildNewToolBarButton());
		JButton button = toolBar.add(this.actionRepository.getOpenAction());
		button.setMnemonic('\0');
		button = toolBar.add(new WorkbenchSaveAction(getContext(), nodeManager()));
		button.setMnemonic('\0');
		button = toolBar.add(new WorkbenchSaveAsAction(getContext(), nodeManager()));
		button.setMnemonic('\0');
		button = toolBar.add(this.actionRepository.getSaveAllAction());
		button.setMnemonic('\0');
		button = toolBar.add(new WorkbenchCloseAction(getContext(), nodeManager()));
		button.setMnemonic('\0');
		button = toolBar.add(this.actionRepository.getCloseAllAction());
		button.setMnemonic('\0');

		this.pluginToolBarActions = new HashMap();
		Plugin[] plugins = this.plugins();
		for (int i = 0; i < plugins.length; i++) {
			Plugin plugin = plugins[i];
			ComponentContainerDescription ccd = plugin.buildToolBarDescription(this.getContext());
			if (ccd.hasComponents()) {
				toolBar.addSeparator();
			}
			for (Iterator stream = ccd.components(); stream.hasNext(); ) {
				toolBar.add((Component) stream.next());
			}
			Collection actions = new ArrayList();
			for (Iterator stream = ccd.actions(); stream.hasNext(); ) {
				Action action = (Action) stream.next();
				action.setEnabled(false);
				actions.add(action);
			}
			this.pluginToolBarActions.put(plugin, actions);
		}

		toolBar.addSeparator();
		button = toolBar.add(new HelpAction(this.getContext()));
		button.setMnemonic('\0');

		return toolBar;
	}

	private DropDownButton buildNewToolBarButton() {
		DropDownButton button = new DropDownButton();
		// the client property must be set before the action is set
		button.putClientProperty("hideActionText", Boolean.TRUE);
		button.setAction(this.buildNewToolBarAction());
		button.setMenu(this.buildNewMenu().getPopupMenu());
		button.setMnemonic('\0');
		return button;
	}

	private FrameworkAction buildNewToolBarAction() {
		return new AbstractFrameworkAction(this.getContext()) {
			protected void initialize() {
				this.initializeIcon("file");		
				this.initializeText("file.new");
				this.initializeToolTipText("file.new.toolTipText");
			}
			protected void execute() {
				// do nothing - the DropDownButton will display the pop-up menu
			}
		};
	}
	
	private JToolBar buildToolBar(String key) {
		JToolBar toolBar = new JToolBar(this.resourceRepository().getString(key));
		
		Border shadow = BorderFactory.createMatteBorder(0, 0, 1, 0, toolBar.getBackground().darker());
		Border highlight = BorderFactory.createMatteBorder(0, 0, 1, 0, toolBar.getBackground().brighter());

		toolBar.setBorder(BorderFactory.createCompoundBorder(highlight, shadow));

		toolBar.setRollover(true);

		// TODO should we allow the user to undock toolbars?
		// the panel will collapse sometimes when you undock the toolbar;
		// need to look at the border if we allow this, since some L&Fs 
		// show the border differently if it is floatable (we build a custom
 		// border above)
		toolBar.setFloatable(false);

		return toolBar;
	}



	// ********** plug-in and selection actions **********

	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				WorkbenchWindow.this.treeSelectionChanged();
			}
		};
	}

	/**
	 * Synchronize the "Workbench" and "Selected" menus and the tool bars with
	 * the navigator tree selection.
	 */
	void treeSelectionChanged() {
		ApplicationNode[] selectedNodes = this.navigatorSelectionModel().getSelectedNodes();
		Plugin selectedPlugin = this.selectedPlugin(selectedNodes);

		this.updateWorkbenchActions(selectedPlugin);

		this.enablePluginToolBarActions(selectedPlugin); 

		this.updateSelectionActions(selectedNodes, selectedPlugin);
	}
	
	/**
	 * Return the single plug-in associated with the specified nodes.
	 * If all the selected nodes are associated with a single plug-in,
	 * return that plug-in. If no nodes are selected or if the selected
	 * nodes span multiple plug-ins, return null.
	 */
	private Plugin selectedPlugin(ApplicationNode[] selectedNodes) {
		int len = selectedNodes.length;
		if (len == 0) {
			return null;
		}
		// grab the first and compare to the rest
		Plugin selectedPlugin = selectedNodes[0].getPlugin();
		for (int i = 1; i < len; i++) {		// note we start with 1
			if (selectedNodes[i].getPlugin() != selectedPlugin) {
				return null;
			}
		}
		return selectedPlugin;
	}

	/**
	 * Enable the buttons in the "plug-in" tool bar (the top tool bar)
	 * corresponding to the specified plug-in.
	 */
	private void enablePluginToolBarActions(Plugin selectedPlugin) {
		for (Iterator stream1 = this.pluginToolBarActions.entrySet().iterator(); stream1.hasNext(); ) {
			Map.Entry entry = (Map.Entry) stream1.next();
			boolean enabled = entry.getKey() == selectedPlugin;
			for (Iterator stream2 = ((Collection) entry.getValue()).iterator(); stream2.hasNext(); ) {
				((Action) stream2.next()).setEnabled(enabled);
			}
		}
	}

	private void updateWorkbenchActions(Plugin selectedPlugin) {
		this.tearDownWorkbenchActions();
		this.workbenchActions.clear();
		
		this.workbenchMenu.removeAll();
		if (selectedPlugin != null) {
			ComponentContainerDescription workbenchMenuDescription = selectedPlugin.buildMenuDescription(this.getContext());
			for (Iterator stream = workbenchMenuDescription.components(); stream.hasNext(); ) {
				this.workbenchMenu.add((Component) stream.next());
			}
			CollectionTools.addAll(this.workbenchActions, workbenchMenuDescription.actions());
			this.setUpWorkbenchActions();
		}	
		this.workbenchMenu.setEnabled(this.workbenchMenu.getMenuComponentCount() > 0);
	}
	
	private void setUpWorkbenchActions() {
		for (Iterator stream = this.workbenchActions.iterator(); stream.hasNext(); ) {
			((FrameworkAction) stream.next()).setUp();
		}
	}

	private void tearDownWorkbenchActions() {
		for (Iterator stream = this.workbenchActions.iterator(); stream.hasNext(); ) {
			((FrameworkAction) stream.next()).tearDown();
		}
	}

	/**
	 * Tear down the previous set of "selection" actions, rebuild the
	 * "selection" menu and tool bar, and set up the new set of
	 * "selection" actions.
	 */
	private void updateSelectionActions(ApplicationNode[] selectedNodes, Plugin selectedPlugin) {
		this.tearDownSelectionActions();
		this.selectionActions.clear();
		this.updateSelectionMenu(selectedNodes, selectedPlugin);
		this.updateSelectionToolBar(selectedNodes, selectedPlugin);
		this.setUpSelectionActions();
	}

	private void setUpSelectionActions() {
		for (Iterator stream = this.selectionActions.iterator(); stream.hasNext(); ) {
			((FrameworkAction) stream.next()).setUp();
		}
	}

	private void tearDownSelectionActions() {
		for (Iterator stream = this.selectionActions.iterator(); stream.hasNext(); ) {
			((FrameworkAction) stream.next()).tearDown();
		}
	}

	private void updateSelectionMenu(ApplicationNode[] selectedNodes, Plugin selectedPlugin) {
		this.selectionMenu.removeAll();
		if (selectedPlugin == null) {
			this.selectionMenu.setEnabled(false);
			this.selectionMenuDescriptionHolder.setValue(new RootMenuDescription());
			return;
		}
		// at this point we know we have 1 or more nodes from the same plug-in
		GroupContainerDescription selectionMenuDescription = selectedNodes[0].buildMenuDescription(this.context);
		for (int i = 1; i < selectedNodes.length; i++) {		// note we start with 1
			selectionMenuDescription.mergeWith(selectedNodes[i].buildMenuDescription(this.context));
		}
		for (Iterator stream = selectionMenuDescription.components(); stream.hasNext(); ) {
			this.selectionMenu.add((Component) stream.next());
		}
		this.selectionMenu.setEnabled(this.selectionMenu.getMenuComponentCount() > 0);
		CollectionTools.addAll(this.selectionActions, selectionMenuDescription.actions());
		this.selectionMenuDescriptionHolder.setValue(selectionMenuDescription);
	}

	private void updateSelectionToolBar(ApplicationNode[] selectedNodes, Plugin selectedPlugin) {
		this.selectionToolBar.removeAll();
		this.selectionToolBar.revalidate();
		this.selectionToolBar.repaint();
		if (selectedPlugin == null) {
			return;
		}
		// at this point we know we have 1 or more nodes from the same plug-in
		GroupContainerDescription selectionToolBarDescription = selectedNodes[0].buildToolBarDescription(this.context);
		for (int i = 1; i < selectedNodes.length; i++) {		// note we start with 1
			selectionToolBarDescription.mergeWith(selectedNodes[i].buildToolBarDescription(this.context));
		}
		for (Iterator stream = selectionToolBarDescription.components(); stream.hasNext(); ) {
			this.selectionToolBar.add((Component) stream.next());
		}
		CollectionTools.addAll(this.selectionActions, selectionToolBarDescription.actions());
	}


	// ********** recent files **********

	private StateChangeListener buildRecentFilesListener() {
		return new StateChangeListener() {
			public void stateChanged(StateChangeEvent e) {
				WorkbenchWindow.this.recentFilesChanged();
			}
		};
	}

	void recentFilesChanged() {
		this.rebuildRecentFilesMenuItems();
	}

	private void rebuildRecentFilesMenuItems() {
		// point just beyond the last menu item (exit)
		int base = this.fileMenu.getMenuComponentCount();
		// jump to the last separator
		base = base - 2;
		// jump to the first recent file menu item
		base = base - this.recentFilesSize;

		// remove all the old entries
		for (int i = 0; i < this.recentFilesSize; i++) {
			this.fileMenu.remove(base);
		}

		// now insert the new entries
		File[] recentFiles = this.application.recentFilesManager().getRecentFiles();
		this.recentFilesSize = recentFiles.length;
		for  (int i = 0; i < this.recentFilesSize; i++) {
			this.fileMenu.insert(this.buildRecentFileMenuItem(i + 1, recentFiles[i]), base + i);
		}

		// Show the last separator if there are any recent files
		this.fileMenu.getMenuComponent(this.fileMenu.getMenuComponentCount() - 2).setVisible(recentFiles.length > 0);
	}

	private JMenuItem buildRecentFileMenuItem(int count, File recentFile) {
		return new JMenuItem(new OpenRecentFileAction(this, count, recentFile));
	}


	// ********** saving/restoring window state **********

	void saveState(Preferences windowsPreferences) {
		windowsPreferences.putInt("x-location", this.getLocation().x);
		windowsPreferences.putInt("y-location", this.getLocation().y);
		windowsPreferences.putInt("height", this.getSize().height);
		windowsPreferences.putInt("width", this.getSize().width);
		windowsPreferences.putInt("extended state", this.getExtendedState());
		this.workspace.saveState(windowsPreferences);
		windowsPreferences.putBoolean("problems view visible", this.workspace.problemsAreVisible());
	}
	
	void restoreState(Preferences windowsPreferences) {
		int x = windowsPreferences.getInt("x-location", this.getLocation().x);
		int y = windowsPreferences.getInt("y-location", this.getLocation().y);
		this.setLocation(x, y);

		int height = windowsPreferences.getInt("height", this.getSize().height);
		int width = windowsPreferences.getInt("width", this.getSize().width);
		this.setSize(width, height);

		this.setExtendedState(windowsPreferences.getInt("extended state", this.getExtendedState()));

		this.workspace.restoreState(windowsPreferences);

		if ( ! windowsPreferences.getBoolean("problems view visible", true)) {
			this.toggleShowProblems();
		}
	}
	
	void saveTreeExpansionState(Preferences windowsPreferences) {
		this.workspace.saveTreeExpansionState(windowsPreferences);
	}

	void restoreTreeExpansionState(Preferences windowsPreferences) {
		this.workspace.restoreTreeExpansionState(windowsPreferences);	
	}
	

	// ********** miscellaneous behavior **********

	/**
	 * Delegate to the workspace: if the problems are currently
	 * displayed, hide them; if they are hidden, display them.
	 * Synchronize the "Show Problems" menu item check box.
	 */
	void toggleShowProblems() {
		this.showProblemsButtonModel.setSelected(this.workspace.toggleShowProblems());
	}

	/**
	 * Delegate to the node manager: if projects are currently added
	 * with synchronous validators, start adding them with asynchronous
	 * validators, and vice versa.
	 * Synchronize the "Synchronous Problems" menu item check box.
	 */
	void toggleSynchronousProblems() {
		this.synchronousProblemsButtonModel.setSelected(this.application.getNodeManager().toggleAddProjectNodesWithSynchronousValidators());
	}

	/**
	 * Ask the application to close the window.
	 */
	void closing() {
		this.application.close(this);
	}

	/**
	 * The window was actually closed.
	 */
	void closed() {
		this.application.recentFilesManager().removeStateChangeListener(this.recentFilesListener);
		this.getWorkspace().removeNavigatorTreeSelectionListener(this.treeSelectionListener);	
		this.tearDownSelectionActions();
		this.workspace.close();
		// stop listening to the window, or, for some odd reason,
		// we will receive the WINDOW_CLOSED event twice...
		this.removeWindowListener(this.windowListener);
	}

	// ********** queries **********

	FrameworkApplication getApplication() {
		return this.application;
	}

	WorkbenchContext getContext() {
		return this.context;
	}

	WorkspaceView getWorkspace() {
		return this.workspace;
	}
	
	ActionRepository getActionRepository() {
		return this.actionRepository;
	}

	Plugin[] plugins() {
		return this.application.getPlugins();
	}	

	ResourceRepository resourceRepository() {
		return this.application.getResourceRepository();
	}	

	ApplicationContext applicationContext() {
		return this.application.getRootApplicationContext();
	}

	FrameworkNodeManager nodeManager() {
		return this.application.getNodeManager();
	}

	TreeNodeValueModel rootNode() {
		return this.nodeManager().getRootNode();
	}

	NavigatorSelectionModel navigatorSelectionModel() {
		return this.workspace.navigatorSelectionModel();
	}

    Component getPropertiesPage() {
        return this.workspace.getPropertiesPage();
    }
    
    
	// ******************** member classes ********************

	private class LocalWorkbenchContext
		extends AbstractWorkbenchContext
	{
		public ApplicationContext getApplicationContext() {
			return WorkbenchWindow.this.applicationContext();
		}
		public Window getCurrentWindow() {
			return WorkbenchWindow.this;
		}
		public NavigatorSelectionModel getNavigatorSelectionModel() {
			return WorkbenchWindow.this.navigatorSelectionModel();
		}
		public ActionRepository getActionRepository() {
			return WorkbenchWindow.this.getActionRepository();
		}
        public Component getPropertiesPage() {
            return WorkbenchWindow.this.getPropertiesPage();
        }
	}


	private class LocalWindowListener extends WindowAdapter  {
		public void windowClosing(WindowEvent e) {
			WorkbenchWindow.this.closing();
		}
		public void windowClosed(WindowEvent e) {
			WorkbenchWindow.this.closed();
		}
	}

}
