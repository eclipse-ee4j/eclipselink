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
package org.eclipse.persistence.tools.workbench.test.framework;

import java.awt.Component;
import java.awt.Window;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.action.ActionRepository;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.AbstractApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.AbstractWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.internal.FrameworkIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.internal.FrameworkResourceBundle;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.IconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepositoryWrapper;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * useful for testing components;
 * includes the framework resources and a help manager, by default;
 * has setters for adding anything else that's needed for testing
 */
public class TestWorkbenchContext extends AbstractWorkbenchContext {
	Application application;
	Preferences preferences;
	ResourceRepository resourceRepository;
	NodeManager nodeManager;
	HelpManager helpManager;

	private Window currentWindow;
	private NavigatorSelectionModel navigatorSelectionModel;
	private ActionRepository actionRepository;
    private Component propertiesPage;


	public TestWorkbenchContext() {
		this(null, null);
	}

	public TestWorkbenchContext(Class bundleClass, String iconClassName) {
		super();
		this.resourceRepository = new DefaultResourceRepository(FrameworkResourceBundle.class, new FrameworkIconResourceFileNameMap());
		if (bundleClass != null) {
			this.resourceRepository = new ResourceRepositoryWrapper(this.resourceRepository, bundleClass);
		}
		if (iconClassName != null) {
			this.resourceRepository = new ResourceRepositoryWrapper(this.resourceRepository, this.buildIconResourceFileNameMap(iconClassName));
		}
		this.preferences = Preferences.userNodeForPackage(this.getClass());
		this.helpManager = this.buildNullHelpManager();
		this.nodeManager = this.buildNodeManager(); 
	}

	private IconResourceFileNameMap buildIconResourceFileNameMap(String iconClassName) {
		try {
			return (IconResourceFileNameMap) ClassTools.newInstance(iconClassName);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	private NodeManager buildNodeManager() {
		return new NodeManager() {
			public void addProjectNode(ApplicationNode node) {
				// do nothing
			}
			public boolean save(ApplicationNode node, WorkbenchContext workbenchContext) {
				return false;
			}

			public void removeProjectNode(ApplicationNode node) {
				// do nothing
			}
			public ApplicationNode[] projectNodesFor(Plugin plugin) {
				return new ApplicationNode[0];
			}
			public TreeNodeValueModel getRootNode() {
				return new AbstractApplicationNode(TestWorkbenchContext.this.getApplicationContext()) {
					public Component propertiesPage(WorkbenchContext workbenchContext) {
						return null;
					}
					public void releasePropertiesPage(Component c) {
						// do nothing
					}
					protected String buildDisplayString() {
						return "";
					}
					protected boolean buildDirtyFlag() {
						return false;
					}	
					public String helpTopicId() {
						return "default";
					}
					public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext) {
						return new ToolBarDescription();
					}
					public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext) {
						return new RootMenuDescription();
					}
				};
			}
		};
	}

	private HelpManager buildNullHelpManager() {
		try {
			return (HelpManager) ClassTools.newInstance("org.eclipse.persistence.tools.workbench.framework.help.NullHelpManager");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public ApplicationContext getApplicationContext() {
		return new AbstractApplicationContext() {
			public Application getApplication() {
				return TestWorkbenchContext.this.application;
			}

			public HelpManager getHelpManager() {
				return TestWorkbenchContext.this.helpManager;
			}

			public NodeManager getNodeManager() {
				return TestWorkbenchContext.this.nodeManager;
			}

			public Preferences getPreferences() {
				return TestWorkbenchContext.this.preferences;
			}

			public ResourceRepository getResourceRepository() {
				return TestWorkbenchContext.this.resourceRepository;
			}
		};
	}
	// ********** ApplicationContext **********

	public void setApplication(Application application) {
		this.application = application;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	public void setResourceRepository(ResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}
	
	public void setNodeManager(NodeManager nodeManager) {
		this.nodeManager = nodeManager;
	}

	public void setHelpManager(HelpManager helpManager) {
		this.helpManager = helpManager;
	}


	// ********** WorkbenchContext **********

	public Window getCurrentWindow() {
		return this.currentWindow;
	}
	
	public void setCurrentWindow(Window currentWindow) {
		this.currentWindow = currentWindow;
	}
	
	public NavigatorSelectionModel getNavigatorSelectionModel() {
		return this.navigatorSelectionModel;
	}

	public void setNavigatorSelectionModel(NavigatorSelectionModel navigatorSelectionModel) {
		this.navigatorSelectionModel = navigatorSelectionModel;
	}

	public ActionRepository getActionRepository() {
		return this.actionRepository;
	}

	public void setActionRepository(ActionRepository actionRepository) {
		this.actionRepository = actionRepository;
	}

    public Component getPropertiesPage() {
        return this.propertiesPage;
    }
    
    public void setPropertiesPage(Component propertiesPage) {
        this.propertiesPage = propertiesPage;
    }
}
