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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui;

import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JMenuItem;

import org.eclipse.persistence.tools.workbench.framework.OpenException;
import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.UnsupportedFileException;
import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ComponentContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.preferences.PlatformsPreferencesNode;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository.DatabasePlatformRepositoryNode;
import org.eclipse.persistence.tools.workbench.utility.SimpleStack;
import org.eclipse.persistence.tools.workbench.utility.Stack;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * Plug-in implementation for Platforms. 
 * This also holds the platforms actions shared within a workbench window.
 */
public final class PlatformsPlugin implements Plugin {

	/** properties pages, keyed by the properties page's class */
	private Map propertiesPageCache;

	private static final String PLATFORMS_PREFERENCES_NODE = "platforms";

	public static final String VISIBLE_IN_PRODUCTION_PREFERENCE = "visible in production";
		public static final boolean VISIBLE_IN_PRODUCTION_PREFERENCE_DEFAULT = false;

	private static final String FILE_NAME_EXTENSION = ".dpr";	// Datasource Platform Repository

	private static final JMenuItem[] EMPTY_MENU_ITEMS = new JMenuItem[0];
	private static final PreferencesNode[] EMPTY_PREFERENCES_NODES = new PreferencesNode[0];


	// ********** constructor/initialization **********

	public PlatformsPlugin() {
		super();
		this.initialize();
	}

	private void initialize() {
		this.propertiesPageCache = new HashMap();
	}


	// ********** Plugin implementation **********

	/**
	 * we only return actions for this plug-in in development mode
	 * @see org.eclipse.persistence.tools.workbench.framework.Plugin#buildNewMenuItemWrappers(org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext)
	 */
	public JMenuItem[] buildNewMenuItems(WorkbenchContext context) {
		if (this.isVisibleIn(context)) {
			return new JMenuItem[] {new JMenuItem(this.getNewDatabasePlatformRepositoryAction(this.wrap(context)))};
		}
		return EMPTY_MENU_ITEMS;
	}

	public JMenuItem[] buildMigrateMenuItems(WorkbenchContext context) {
		return EMPTY_MENU_ITEMS;
	}

	/**
	 * TODO disable this in production???
	 * @see org.eclipse.persistence.tools.workbench.framework.Plugin#open(java.io.File, org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext)
	 */
	public ApplicationNode open(File file, WorkbenchContext context) throws UnsupportedFileException, OpenException {
		if ( ! this.fileIsSupported(file)) {
			throw new UnsupportedFileException();
		}

		try {
			return this.buildRepositoryNode(new DatabasePlatformRepository(file), this.wrap(context));
		} catch (Throwable t) {
			throw new OpenException(t);
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.Plugin#buildToolBarDescription(org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext)
	 */
	public ComponentContainerDescription buildToolBarDescription(WorkbenchContext context) {
		return new ToolBarButtonGroupDescription();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.Plugin#buildMenuDescription(org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext)
	 */
	public ComponentContainerDescription buildMenuDescription(WorkbenchContext context) {
		return new MenuGroupDescription();
	}

	/**
	 * we only return a preference node for this plug-in in development mode
	 * @see org.eclipse.persistence.tools.workbench.framework.Plugin#buildPreferencesNodes(org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext)
	 */
	public PreferencesNode[] buildPreferencesNodes(PreferencesContext context) {
		if (this.isVisibleIn(context)) {
			return new PreferencesNode[] {new PlatformsPreferencesNode((PreferencesContext) this.wrap(context))};
		}
		return EMPTY_PREFERENCES_NODES;
	}


	// ********** queries **********

	/**
	 * return whether the plug-in should be visible to the user;
	 * the platforms plug-in is not typically visible in production
	 */
	private boolean isVisibleIn(WorkbenchContext context) {
		return this.isVisibleIn(context.getApplicationContext());
	}

	/**
	 * return whether the plug-in should be visible to the user;
	 * the platforms plug-in is not typically visible in production
	 */
	private boolean isVisibleIn(ApplicationContext context) {
		return context.getApplication().isDevelopmentMode() ||
				this.isVisibleInProduction(context.getPreferences().node(PLATFORMS_PREFERENCES_NODE));
	}

	/**
	 * this should probably only return true on a developer's machine
	 * where the developer has set the preference to true, which can
	 * only happen in development mode...
	 */
	private boolean isVisibleInProduction(Preferences preferences) {
		return preferences.getBoolean(VISIBLE_IN_PRODUCTION_PREFERENCE, VISIBLE_IN_PRODUCTION_PREFERENCE_DEFAULT);
	}

	/**
	 * return whether the specified file is supported by the plug-in
	 */
	public boolean fileIsSupported(File file) {
		return FileTools.extension(file).equalsIgnoreCase(FILE_NAME_EXTENSION);
	}

	public FrameworkAction getNewDatabasePlatformRepositoryAction(WorkbenchContext context) {
		return new NewDatabasePlatformRepositoryAction(PlatformsPlugin.this, context);
	}
	
	public FrameworkAction getHelpAction(WorkbenchContext context) {
		return new HelpAction(context);
	}


	// ********** behavior **********

	WorkbenchContext wrap(WorkbenchContext context) {
		return context.buildExpandedApplicationContextWorkbenchContext(this.wrap(context.getApplicationContext()));
	}

	private ApplicationContext wrap(ApplicationContext context) {
		context = context.buildExpandedResourceRepositoryContext(PlatformsPluginResourceBundle.class, new PlatformsPluginIconResourceFileNameMap());
		return context.buildRedirectedPreferencesContext(PLATFORMS_PREFERENCES_NODE);
	}

	ApplicationNode buildRepositoryNode(DatabasePlatformRepository repository, WorkbenchContext context) {
		return new DatabasePlatformRepositoryNode(repository, context.getApplicationContext(), this);
	}


	// ********** properties page cache **********

	/**
	 * Return the properties page for the specified key.
	 * If the requested page is in the cache remove it and
	 * return it, otherwise return null.
	 */
	Component getPropertiesPage(Class propertiesPageClass) {
		Stack stack = this.getPropertiesPagesStack(propertiesPageClass);
		return stack.isEmpty() ? null : (Component) stack.pop();
	}

	/**
	 * Put the specified properties page back into the cache,
	 * using the specified key. For now, replace the existing
	 * properties page, allowing it to be garbage-collected.
	 */
	void releasePropertiesPage(Class propertiesPageClass, Component propertiesPage) {
		this.getPropertiesPagesStack(propertiesPageClass).push(propertiesPage);
	}

	private Stack getPropertiesPagesStack(Class propertiesPageClass) {
		Stack stack = (Stack) this.propertiesPageCache.get(propertiesPageClass);
		if (stack == null) {
			stack = new SimpleStack();
			this.propertiesPageCache.put(propertiesPageClass, stack);
		}
		return stack;
	}

}
