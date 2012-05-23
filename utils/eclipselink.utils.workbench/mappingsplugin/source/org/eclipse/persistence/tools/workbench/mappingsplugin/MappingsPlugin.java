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

import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.OpenException;
import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.UnsupportedFileException;
import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ComponentContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuItemDescription;
import org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.context.ShellWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.EmptyPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsio.FileNotFoundListener;
import org.eclipse.persistence.tools.workbench.mappingsio.LegacyProjectReadCallback;
import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.preferences.MappingsPreferencesNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.ExportTableCreatorJavaSourceAction;
import org.eclipse.persistence.tools.workbench.uitools.CancelException;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Command;
import org.eclipse.persistence.tools.workbench.utility.Stack;
import org.eclipse.persistence.tools.workbench.utility.SynchronizedObject;
import org.eclipse.persistence.tools.workbench.utility.SynchronizedStack;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Plug-in implementation for Mappings. This initializes and holds the
 * I/O Manager for reading and writing Mapping Workbench Projects (.mwp).
 * This also holds the mappings actions shared within a workbench window.
 */
// TODO possibly move properties page building and caching to separate (framework?) class
public final class MappingsPlugin
	implements Plugin
{
	/** the app context built during construction */
	private ApplicationContext mwApplicationContext;

	/** this reads and writes Mapping Workbench projects */
	private SynchronizedObject synchronizedIOManager;

	/**
	 * properties page cache entries, keyed by the properties page's class;
	 * access to this map must be synchronized
	 */
	private Map propertiesPageCache;

	/**
	 * a stack of properties page classes; the properties page builder thread
	 * will loop indefinitely, popping classes from this stack and building the
	 * corresponding properties pages
	 */
	SynchronizedStack propertiesPageBuilderStack;


	private static final String MAPPINGS_PREFERENCES_NODE = "mappings";

	// Preference options
	public static final String REMOVE_EJB_2X_INFO_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE = "persistence type remove ejb2x info";
	public static final String REMOVE_EJB_INFO_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE = "persistence type remove ejb info";
	public static final String CHANGE_QUERY_TYPE_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE ="query change query type";
	public static final String CHANGE_QUERY_FORMAT_DO_NOT_SHOW_THIS_AGAIN_PREFERENCE ="query change query format";
	public static final String EXPORT_LOCATION_PREFERENCE = "export location";

	/**
	 * The properties pages will be pre-built in the order they are listed here.
	 * These classes are stored here by name so we don't have to make
	 * them public. Whether this is a good idea is open to debate.  ~bjv
	 */
	private static final String[] PRE_BUILT_PROPERTIES_PAGES = {
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.TableDescriptorTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.DatabasePropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.TableTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackagePropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.AggregateDescriptorTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.InterfaceDescriptorPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.EisCompositeDescriptorTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.EisRootDescriptorTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.OXDescriptorTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.framework.ui.view.EmptyPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.AggregateMappingTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.DirectToFieldMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.DirectToXmlTypePropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.ManyToManyMappingTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.OneToManyMappingTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.OneToOneMappingTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalDirectCollectionMappingTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalDirectMapMappingTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalTransformationMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.VariableOneToOneMappingTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisCompositeCollectionMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OXCompositeCollectionMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisCompositeObjectMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OXCompositeObjectMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisOneToManyTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisOneToOneTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OxDirectCollectionMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisDirectCollectionMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OxDirectMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisDirectMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisTransformationMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OXTransformationMappingPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.EisProjectTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.OXProjectTabbedPropertiesPage",
		"org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema.XmlSchemaRepositoryPanel",
	};


	// ********** constructor/initialization **********

	MappingsPlugin(ApplicationContext context) {
		super();
		this.mwApplicationContext = this.wrap(context);
		this.startInitializeSynchronizedIOManager();
		this.initializePreferences();
		this.initializePropertiesPageBuilder();
	}

	// ********** I/O manager **********

	/**
	 * Build an empty "synchronized" i/o manager and
	 * start a thread to initialize it with a i/o manager.
	 */
	private void startInitializeSynchronizedIOManager() {
		// put the synchronized object in place -
		// #getIOManager() will wait until it is initialized and unlocked
		this.synchronizedIOManager = new SynchronizedObject();
		Thread t = new Thread(this.buildInitializeSynchronizedIOManagerRunnable(), "Initialize Mappings I/O Manager");
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	/**
	 * Build a Runnable that will initialize the "synchronized" i/o manager.
	 */
	private Runnable buildInitializeSynchronizedIOManagerRunnable() {
		return new Runnable() {
			public void run() {
				MappingsPlugin.this.initializeSynchronizedIOManager();
			}
		};
	}

	/**
	 * Hold the i/o manager lock while building the
	 * i/o manager and putting it in the synchronized object.
	 */
	void initializeSynchronizedIOManager() {
		try {
			this.synchronizedIOManager.execute(this.buildInitializeIOManagerCommand());
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);	// shouldn't happen
		}
	}

	/**
	 * Build the command that will be executed while the "synchronized"
	 * i/o manager is locked.
	 */
	private Command buildInitializeIOManagerCommand() {
		return new Command() {
			public void execute() {
				MappingsPlugin.this.initializeIOManager();
			}
		};
	}

	/**
	 * Build the appropriate i/o manager and put it in the
	 * synchronized object that wraps it.
	 */
	void initializeIOManager() {
		this.synchronizedIOManager.setValue(new ProjectIOManager());
	}

	// ********** Preferences ***********

	private void initializePreferences() {
		Preferences preferences = this.mwApplicationContext.getPreferences();

		// Remove EJB Info
		String value = preferences.get(REMOVE_EJB_INFO_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE, TriStateBoolean.UNDEFINED.toString());
		preferences.put(REMOVE_EJB_INFO_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE, value);

		// Remove EJB 2.x Info
		value = preferences.get(REMOVE_EJB_2X_INFO_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE, TriStateBoolean.UNDEFINED.toString());
		preferences.put(REMOVE_EJB_2X_INFO_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE, value);
	}

	// ********** properties page cache **********

	/**
	 * Build the properties page cache and builder stack, prime the stack,
	 * and start the properties page builder thread.
	 */
	private void initializePropertiesPageBuilder() {
		// we can use a HashMap because we synchronize manually
		this.propertiesPageCache = new HashMap(PRE_BUILT_PROPERTIES_PAGES.length);
		this.propertiesPageBuilderStack = new SynchronizedStack();
		// push the classes on to the stack in reverse order so they get built in the expected order
		for (int i = PRE_BUILT_PROPERTIES_PAGES.length; i-- > 0; ) {
			this.propertiesPageBuilderStack.push(this.classForName(PRE_BUILT_PROPERTIES_PAGES[i]));
		}
		new PropertiesPageBuilder().start();
	}

	// checked exceptions suck
	private Class classForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException(className);
		}
	}


	// ********** Plugin implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.Plugin#buildNewMenuItems(org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext)
	 */
	public JMenuItem[] buildNewMenuItems(WorkbenchContext context) {

		context = this.wrap(context);
		JMenuItem[] menuItems = new JMenuItem[1];

		menuItems[0] = new JMenuItem(this.buildNewProjectAction(context));

		return menuItems;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.Plugin#open(java.io.File, org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext)
	 */
	public ApplicationNode open(File file, WorkbenchContext context) throws UnsupportedFileException, OpenException {
		if ( ! FileTools.extension(file).equalsIgnoreCase(MWProject.FILE_NAME_EXTENSION)) {
			throw new UnsupportedFileException();
		}
		
		MWProject project = null;
		// the context passed in is the application-wide context, we need the "mappings" context
		Preferences preferences = context.getApplicationContext().getPreferences().node(MAPPINGS_PREFERENCES_NODE);
		context = this.wrap(context);

		LocalCallback callback = new LocalCallback(context);
		try {
			// TODO build FileNotFoundListener and notify listener of any "missing" files
			project = this.getIOManager().read(file, preferences, FileNotFoundListener.NULL_INSTANCE, callback);
		} catch (CancelException ex) {
			throw ex;
		} catch (Throwable t) {
			throw new OpenException(t);
		}

		ApplicationNode projectNode = this.buildProjectNode(project, context);

		if (callback.saveLegacyProjectNow()) {
			if ( ! projectNode.saveAs(file, context)) {
				throw new CancelException();
			}
			JOptionPane.showMessageDialog(context.getCurrentWindow(), 
					new LabelArea(context.getApplicationContext().getResourceRepository().getString("LEGACY_MIGRATION_COMPLETE.MESSAGE")),
					context.getApplicationContext().getResourceRepository().getString("LEGACY_MIGRATION_COMPLETE.TITLE"),
					JOptionPane.INFORMATION_MESSAGE);
		}

		return projectNode;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.Plugin#buildToolBarDescription(org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext)
	 */
	public ComponentContainerDescription buildToolBarDescription(WorkbenchContext context) {
		context = this.wrap(context);
		ToolBarDescription tbd = new ToolBarDescription();
		ToolBarButtonGroupDescription tbbgd1 = new ToolBarButtonGroupDescription();
		tbbgd1.add(new ToolBarButtonDescription(this.getExportDeploymentXmlAction(context)));
		tbd.add(tbbgd1);
		ToolBarButtonGroupDescription tbbgd2 = new ToolBarButtonGroupDescription();
		tbbgd2.add(new ToolBarButtonDescription(this.getRefreshClassesAction(context)));
		tbbgd2.add(new ToolBarButtonDescription(this.getAddOrRefreshClassesAction(context)));
		tbbgd2.add(new ToolBarButtonDescription(this.getCreateNewClassAction(context)));
		tbd.add(tbbgd2);
		return tbd;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.Plugin#buildMenuDescription(org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext)
	 */
	public ComponentContainerDescription buildMenuDescription(WorkbenchContext context) {
		context = this.wrap(context);
		RootMenuDescription md = new RootMenuDescription();
		MenuGroupDescription mgd1 = new MenuGroupDescription();
		mgd1.add(this.buildExportMenuDescription(context));
		md.add(mgd1);
		MenuGroupDescription mgd2 = new MenuGroupDescription();
		mgd2.add(new MenuItemDescription(this.getRefreshClassesAction(context)));
		mgd2.add(new MenuItemDescription(this.getAddOrRefreshClassesAction(context)));
		mgd2.add(new MenuItemDescription(this.getCreateNewClassAction(context)));
		mgd2.add(new MenuItemDescription(this.getManageNonDescriptorClassesAction(context)));
		md.add(mgd2);
		return md;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.Plugin#buildPreferencesNodes(org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext)
	 */
	public PreferencesNode[] buildPreferencesNodes(PreferencesContext context) {
		return new PreferencesNode[] {new MappingsPreferencesNode((PreferencesContext) this.wrap(context))};
	}


	// ********** queries **********

	/**
	 * return the i/o manager once it has been built
	 */
	public ProjectIOManager getIOManager() {
		// once the i/o mgr is initialized, it should never change back to null
		try {
			this.synchronizedIOManager.waitUntilNotNull();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);	// shouldn't happen
		}
		return (ProjectIOManager) this.synchronizedIOManager.getValue();
	}
	
	public AddOrRefreshClassesAction getAddOrRefreshClassesAction(WorkbenchContext context) {
		return new AddOrRefreshClassesAction(context);
	}
	
	public AutomapAction getAutomapAction(WorkbenchContext context) {
		return new AutomapAction(context);
	}

	public CreateNewClassAction getCreateNewClassAction(WorkbenchContext context) {
		return new CreateNewClassAction(context);
	}
	
	public ManageNonDescriptorClassesAction getManageNonDescriptorClassesAction(WorkbenchContext context) {
		return new ManageNonDescriptorClassesAction(context);
	}
	
	public ExportDeploymentXmlAction getExportDeploymentXmlAction(WorkbenchContext context) {
		return new ExportDeploymentXmlAction(context);
	}
    
    public ExportDeploymentXmlAndInitializeRuntimeDescriptorsAction getExportDeploymentXmlAndInitializeRuntimeDescriptorsAction(WorkbenchContext context) {
        return new ExportDeploymentXmlAndInitializeRuntimeDescriptorsAction(context);
    }
    
	public ExportJavaSourceAction getExportProjectJavaSourceAction(WorkbenchContext context) {
		return new ExportJavaSourceAction(context);
	}
	
	public ExportModelJavaSourceAction getExportModelJavaSourceAction(WorkbenchContext context) {
		return new ExportModelJavaSourceAction(context);
	}
	
	public ExportSpecificDescriptorModelJavaSourceAction getExportSpecificDescriptorModelJavaSourceAction(WorkbenchContext context) {
		return new ExportSpecificDescriptorModelJavaSourceAction(context);
	}
	
	public RefreshClassesAction getRefreshClassesAction(WorkbenchContext context) {
		return new RefreshClassesAction(context);
	}
	
	public RemoveAction getRemoveAction(WorkbenchContext context) {
		return new RemoveAction(context);
	}

	public OracleHelpAction getHelpAction(WorkbenchContext context) {
		return new OracleHelpAction(context);
	}

	public ExportTableCreatorJavaSourceAction getExportTableCreatorJavaSourceAction(WorkbenchContext context) {
		return new ExportTableCreatorJavaSourceAction(context);
	}

    /**
     * return whether the application is being run in development mode
     */
    public boolean isDevelopmentModeIn(WorkbenchContext context) {
        return context.getApplicationContext().getApplication().isDevelopmentMode();
    }

    
    // ********** behavior **********

	private WorkbenchContext wrap(WorkbenchContext context) {
		return context.buildExpandedApplicationContextWorkbenchContext(wrap(context.getApplicationContext()));
	}
	
	/**
	 * wrap the specified context with the MW-specific
	 * resources and preferences
	 */
	ApplicationContext wrap(ApplicationContext context) {
		ApplicationContext expandedContext = context.buildExpandedResourceRepositoryContext(MappingsPluginResourceBundle.class, new MappingsPluginIconResourceFileNameMap());
		expandedContext = expandedContext.buildExpandedResourceRepositoryContext(ProblemsBundle.class);
		return expandedContext.buildRedirectedPreferencesContext(MAPPINGS_PREFERENCES_NODE);
	}
	
	FrameworkAction buildNewProjectAction(WorkbenchContext context) {
		return new NewProjectAction(this, context);
	}

	ApplicationNode buildProjectNode(MWProject project, WorkbenchContext context) {
		return ProjectNode.forProject(project, context.getApplicationContext(), this);
	}

	private MenuDescription buildExportMenuDescription(WorkbenchContext context) {
		MenuDescription md = new MenuDescription(
				context.getApplicationContext().getResourceRepository().getString("EXPORT_MENU"),
				null,
				context.getApplicationContext().getResourceRepository().getMnemonic("EXPORT_MENU"),
				context.getApplicationContext().getResourceRepository().getIcon("file.export"));
		MenuGroupDescription mgd = new MenuGroupDescription();
		mgd.add(new MenuItemDescription(this.getExportDeploymentXmlAction(context)));
        if (isDevelopmentModeIn(context)) {
            mgd.add(new MenuItemDescription(this.getExportDeploymentXmlAndInitializeRuntimeDescriptorsAction(context)));
        }
		mgd.add(new MenuItemDescription(this.getExportProjectJavaSourceAction(context)));
		mgd.add(new MenuItemDescription(this.getExportModelJavaSourceAction(context)));
		mgd.add(new MenuItemDescription(this.getExportTableCreatorJavaSourceAction(context)));
		md.add(mgd);
		return md;
	}


	// ********** properties page cache **********

	/**
	 * Build and return the specified properties page.
	 */
	public Component buildPropertiesPage(Class propertiesPageClass) {
		return this.getPropertiesPageCacheEntry(propertiesPageClass).buildPropertiesPage();
	}

	/**
	 * Release the specified properties page back to the cache.
	 */
	public void releasePropertiesPage(Component propertiesPage) {
		this.getPropertiesPageCacheEntry(propertiesPage.getClass()).releasePropertiesPage(propertiesPage);
	}

	/**
	 * Return the cache entry for the specified properties page,
	 * building it if necessary.
	 */
	PropertiesPageCacheEntry getPropertiesPageCacheEntry(Class propertiesPageClass) {
		synchronized (this.propertiesPageCache) {
			PropertiesPageCacheEntry entry = (PropertiesPageCacheEntry) this.propertiesPageCache.get(propertiesPageClass);
			if (entry == null) {
				entry = new PropertiesPageCacheEntry(propertiesPageClass, this.mwApplicationContext, this.propertiesPageBuilderStack);
				this.propertiesPageCache.put(propertiesPageClass, entry);
			}
			return entry;
		}
	}


	// **************** member types ***************

	/**
	 * All kinds of performance-related hackery here....  ~bjv
	 * A properties page cache entry is constructed with a properties page class,
	 * an application context, and the properties page builder stack.
	 * We use reflection to fetch the class's list of required resource bundles.
	 * Then we use these bundles to expand the application context appropriately
	 * for when the properties page builder needs to instantiate the properties
	 * page, using reflection.
	 */
	private static class PropertiesPageCacheEntry {
		private final Class propertiesPageClass;
		private final WorkbenchContext workbenchContext;
		private final Stack propertiesPageBuilderStack;
		private final SynchronizedStack propertiesPages;

		PropertiesPageCacheEntry(Class propertiesPageClass, ApplicationContext applicationContext, Stack propertiesPageBuilderStack) {
			super();
			this.propertiesPageClass = propertiesPageClass;
			this.workbenchContext = this.buildWorkbenchContext(applicationContext);
			this.propertiesPageBuilderStack = propertiesPageBuilderStack;
			this.propertiesPages = new SynchronizedStack();
		}

		private WorkbenchContext buildWorkbenchContext(ApplicationContext applicationContext) {
			Class[] resourceBundleClasses = (Class[]) ClassTools.getStaticFieldValue(this.propertiesPageClass, "REQUIRED_RESOURCE_BUNDLES");
			for (int i = 0; i < resourceBundleClasses.length; i++) {
				applicationContext = applicationContext.buildExpandedResourceRepositoryContext(resourceBundleClasses[i]);
			}
			return new ShellWorkbenchContext(applicationContext);
		}

		/**
		 * if the stack is empty, notify the builder and wait for a page to be
		 * pushed on the stack; if we pop the last page off the stack,
		 * trigger another one to be built asynchronously in the background
		 */
		Component buildPropertiesPage() {
			synchronized (this.propertiesPages) {
				if (this.propertiesPages.isEmpty()) {
					this.propertiesPageBuilderStack.push(this.propertiesPageClass);
				}
				Component propertiesPage;
				try {
					propertiesPage = (Component) this.propertiesPages.waitToPop();
				} catch (InterruptedException ex) {
					// should only happen during shutdown
					throw new RuntimeException(ex);
				}
				if (this.propertiesPages.isEmpty()) {
					this.propertiesPageBuilderStack.push(this.propertiesPageClass);
				}
				return propertiesPage;
			}
		}

		void releasePropertiesPage(Component propertiesPage) {
			this.propertiesPages.push(propertiesPage);
		}

		/**
		 * this is called by the properties page builder when our class
		 * reaches the top of the stack; if there are any problems,
		 * force *something* on to the stack so the AWT Event Queue
		 * does not hang on the #waitToPop() in #buildPropertiesPage()
		 */
		void installPropertiesPage() throws Throwable {
			try {
				// System.out.println(ClassTools.shortNameFor(propertiesPageClass));
				this.propertiesPages.push(ClassTools.newInstance(this.propertiesPageClass, WorkbenchContext.class, this.workbenchContext));
			} catch (Throwable ex) {
				try {
					this.propertiesPages.push(new EmptyPropertiesPage(this.workbenchContext));
				} catch (Throwable ex2) {
					// we're in trouble now...trigger a NPE in the AWT Event Queue (just don't hang it!)
					this.propertiesPages.push(null);
					throw ex2;
				}
				throw ex;
			}
		}

	}


	/**
	 * This thread loops continuously, waiting for a properties page class
	 * to be pushed on to the stack. Once a class has been pushed on to the
	 * stack, the builder pops it off and delegates to the appropriate
	 * properties page cache entry for instantiation.
	 */
	private class PropertiesPageBuilder extends Thread {

		PropertiesPageBuilder() {
			super("Properties Page Builder");
			this.setPriority(Thread.MIN_PRIORITY);
		}

		public void run() {
			// loop until we are interrupted
			while (true) {
				Class propertiesPageClass = null;
				try {
					// wait for a class to be pushed on to the stack
					propertiesPageClass = (Class) MappingsPlugin.this.propertiesPageBuilderStack.waitToPop();
				} catch (InterruptedException ex) {
					// we were interrupted while waiting, must be quittin' time
					return;
				}
				this.buildPropertiesPage(propertiesPageClass);
			}
		}

		private void buildPropertiesPage(Class propertiesPageClass) {
			try {
				MappingsPlugin.this.getPropertiesPageCacheEntry(propertiesPageClass).installPropertiesPage();
			} catch (Throwable ex) {
				// if we have any problems building the page, start a new thread and let this one die;
				// the ThreadGroup will handle the runtime exception if appropriate
				new PropertiesPageBuilder().start();
				throw new RuntimeException(ex);
			}
		}

	}


	// ********** misc??? **********

	/**
	 * Given the current value of a project's "export" directory (e.g. the
	 * project source directory), calculate an appropriate directory to
	 * start in when prompting the user.
	 * 
	 * I'm not so sure where this code should go, so I'm
	 * dumping it here for the time being.  ~bjv
	 */
	public static File buildExportDirectory(MWProject project, String directoryName, Preferences preferences) {
		File directory = new File(directoryName);
		if (StringTools.stringIsEmpty(directoryName) || directory.isAbsolute()) {
			if ( ! directory.exists()) {
				// the project's current export directory does not exist,
				// try to use the project save location
				directory = project.getSaveDirectory();
			}
			if (directory == null) {
				// a newly-created project does not have a save directory,
				// try to use the user preference directory
				directory = new File(preferences.get(EXPORT_LOCATION_PREFERENCE, ""));
			}
			if ( ! directory.exists()) {
				// the user preference directory does not exist,
				// use the user home
				directory = FileTools.userHomeDirectory();
			}
		} else {
			// the directory is supposed to be relative to the project save directory
			File projectSaveDirectory = project.getSaveDirectory();
			if (projectSaveDirectory == null) {
				// if we don't have a project save directory yet
				// (a newly-created project does not have a save directory),
				// we have to ignore a directory that's supposed to be relative to it;
				// use the preference or user home directory instead
				directory = new File(preferences.get(EXPORT_LOCATION_PREFERENCE, ""));
				if ( ! directory.exists()) {
					directory = FileTools.userHomeDirectory();
				}
			} else {
				directory = new File(projectSaveDirectory, directory.getPath());
				if ( ! directory.exists()) {
					// if the fully-expanded directory does not exist,
					// revert to the project save directory
					directory = projectSaveDirectory;
				}
			}
		}
		return directory;
	}


	// ********** private member class **********

	/**
	 * When a legacy project is being read, this callback prompts
	 * the user with three options:
	 *     - save now
	 *     - save later
	 *     - cancel
	 */
	private static class LocalCallback implements LegacyProjectReadCallback {
		private WorkbenchContext context;
		private boolean saveLegacyProjectNow;

		LocalCallback(WorkbenchContext context) {
			super();
			this.context = context;
			this.saveLegacyProjectNow = false;
		}

		public void checkLegacyRead(String schemaVersion) {
			LegacyProjectMigrationDialog dialog = new LegacyProjectMigrationDialog(this.context);
			dialog.show();
			if (dialog.wasCanceled()) {
				throw new CancelException();
			}
			// either "save now" or "save later" was pressed
			this.saveLegacyProjectNow = ! dialog.saveLater();
		}

		boolean saveLegacyProjectNow() {
			return this.saveLegacyProjectNow;
		}

	}

}
