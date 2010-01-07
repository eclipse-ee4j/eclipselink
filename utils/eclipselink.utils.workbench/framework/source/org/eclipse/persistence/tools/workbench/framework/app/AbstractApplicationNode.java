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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.SwingConstants;

import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContextWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.ShellWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.internal.FrameworkIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultIconRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.NullListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;
import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.NodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Subclasses should consider implementing the following methods:
 * 
 * #getChildrenModel()    [TreeNodeValueModel]
 *     "branch" nodes must return a list value model of the children nodes;
 *     "leaf" nodes can simply inherit the default implementation (an empty list)
 * 
 * #helpTopicID()
 *     return Help Topic ID that can be used for Context-Sensitive Help;
 *     the default is, naturally, the "default" Topic ID
 * 
 * #expandContext(WorkbenchContext)
 *     add any bundles or icon repository to the original context
 * 
 * #buildDisplayString()    [Displayable]
 *     return a display string appropriate to the value's current state;
 *     the default is to delegate to the value's #displayString() method
 * 
 * #displayStringPropertyNames()    [Displayable]
 *     return the names of the value's properties that affect the node's label;
 *     the default is an empty array
 * 
 * #buildIconBuilder() or #buildIconKey()    [Displayable]
 *     return an icon builder appropriate to the value's current state;
 *     the default is no icon and decorated with a warning sign if the node
 *     has any problems in its branch
 * 
 * #iconPropertyNames()    [Displayable]
 *     return the names of the value's properties that affect the node's icon;
 *     the default includes whether the value has any branch problems
 * 
 * #buildDirtyFlag()
 *     return a dirty flag appropriate to the value's current state
 *     the default is to delegate to the value's #isDirtyBranch() method
 * 
 * #dirtyPropertyNames()
 *     return the names of the value's properties that affect whether
 *     the node is "dirty"; the default includes whether the node is a
 *     "dirty branch"
 * 
 * #buildMenuDescription()
 *     return a description that will be used to build the selection menu items
 * 
 * #buildToolBarDescription()
 *     return a description that will be used to build the selection toolbar items
 * 
 * #propertiesPage()    [EditorNode]
 *     return a properties page to be shown in the editor view
 * 
 * #buildPropertiesPageTitleText()    [EditorNode]
 *     return a title string appropriate to the value's current state;
 *     the default is the node's display string
 * 
 * #propertiesPageTitleTextPropertyNames()    [EditorNode]
 *     return the names of the value's properties that affect the node's properties page title text;
 *     the default is the node's display string property names
 * 
 * #buildPropertiesPageTitleIconBuilder()    [EditorNode]
 *     return a title icon builder appropriate to the value's current state;
 *     the default is the node's icon builder
 * 
 * #propertiesPageTitleIconPropertyNames()    [EditorNode]
 *     return the names of the value's properties that affect the node's properties page title icon;
 *     the default is the node's icon property names
 * 
 * #releasePropertiesPage(Component)    [EditorNode]
 *     release the specified properties page, it is no longer needed
 *     by the properties page
 * 
 * #rebuildApplicationProblems()    [ApplicationProblemContainer]
 *     rebuild the list of "exclusive" application problems based on the value's
 *     branch problems
 * 
 * 
 * If the subclass is for a "project root" node override the following methods:
 * 
 * #save(File, WorkbenchContext)
 *     save the project
 * 
 * #saveAs(File, WorkbenchContext)
 *     prompt the user to save the project in another location
 * 
 * #saveLocation()
 *     return the project's save location
 * 
 */
public abstract class AbstractApplicationNode
	extends AbstractTreeNodeValueModel
	implements ApplicationNode
{

	/** ValueModel.getValue() */
	private NodeModel value;

	/** TreeNodeValueModel.getParent() */
	private TreeNodeValueModel parent;

	/** Node.getPlugin() */
	private Plugin plugin;

	/** needed by subclasses etc. */
	private ApplicationContext context;
		
	/** cache the node's display string */
	private String displayString;
	/** this listens to the value's properties that affect the node's display string */
	private PropertyChangeListener valueDisplayStringListener;
	/** the model properties that affect the node's display string property */
	protected static final String[] DEFAULT_DISPLAY_STRING_PROPERTY_NAMES = {};

	/** cache the node's icon builder */
	private IconBuilder iconBuilder;
	/** cache the node's icon also */
	private Icon icon;
	/** this listens to the value's properties that affect the node's icon */
	private PropertyChangeListener valueIconListener;
	/** the model properties that affect the node's icon property */
	protected static final String[] DEFAULT_ICON_PROPERTY_NAMES = {Node.HAS_BRANCH_PROBLEMS_PROPERTY};
	
	/** cache the node's dirty flag */
	private boolean dirty;
	/** this listens to the value's properties that affect the node's dirty flag */
	private PropertyChangeListener valueDirtyListener;
	/** the model properties that affect the node's dirty property */
	protected static final String[] DEFAULT_DIRTY_PROPERTY_NAMES = {Node.DIRTY_BRANCH_PROPERTY};

	/** this holds the "application" problems that are "exclusive" to the node */
	private List applicationProblems;
	/** this holds the node's branch "application" problems */
	private List branchApplicationProblems;
	/** this listens to the value's branch problems so we can synch up the "application" problems, above */
	private ListChangeListener valueBranchProblemsListener;
	/** the model properties that affect the node's branch problems */
	protected static final String[] DEFAULT_BRANCH_PROBLEMS_LIST_NAMES = {Node.BRANCH_PROBLEMS_LIST};

	/** cache the node's properties page title text */
	private String propertiesPageTitleText;
	/** this listens to the value's properties that affect the node's properties page title text */
	private PropertyChangeListener valuePropertiesPageTitleTextListener;

	/** cache the node's properties page title icon builder */
	private IconBuilder propertiesPageTitleIconBuilder;
	/** cache the node's properties page title icon also */
	private Icon propertiesPageTitleIcon;
	/** this listens to the value's properties that affect the node's properties page title icon */
	private PropertyChangeListener valuePropertiesPageTitleIconListener;

	private static final Icon PROBLEM_ICON = new DefaultIconRepository(new FrameworkIconResourceFileNameMap()).getIcon("problem.small");
	protected static final Icon EMPTY_ICON = new EmptyIcon(16);


	// *************** constructors ***************

	/**
	 * Only "pseudo-nodes" should use this constructor; and those
	 * are "framework internal".
	 */
	protected AbstractApplicationNode(ApplicationContext context) {
		this(null, null, null, context);
	}

	/**
	 * "Normal" subclasses should use this constructor.
	 */
	protected AbstractApplicationNode(NodeModel value, TreeNodeValueModel parent, Plugin plugin, ApplicationContext context) {
		super();
		this.value = value;
		this.context = this.expandContext(context);
		this.plugin = plugin;
		this.parent = parent;

		// build the various listeners, but don't start listening yet
		this.valueDisplayStringListener = this.buildValueDisplayStringListener();
		this.valueIconListener = this.buildValueIconListener();
		this.valueDirtyListener = this.buildValueDirtyListener();
		this.valueBranchProblemsListener = this.buildValueBranchProblemsListener();
		this.applicationProblems = new ArrayList();
		this.branchApplicationProblems = new ArrayList();
		this.valuePropertiesPageTitleTextListener = this.buildValuePropertiesPageTitleTextListener();
		this.valuePropertiesPageTitleIconListener = this.buildValuePropertiesPageTitleIconListener();
	}


	// *************** initialization ***************

	/**
	 * Expand the context passed into the node upon construction.
	 * By default, return the context unchanged.
	 * 
	 * NB: Call super.expandContext(WorkbenchContext)
	 * 	when overriding this method.
	 * NB2: Assume that this method will return a different context
	 * 	than the context passed in, even if they *may* end up being
	 * 	the same.
	 */
	protected ApplicationContext expandContext(ApplicationContext ctx) {
		return ctx;
	}
	
	/**
	 * Use this in places where a workbenchContext has been passed in to a subclass method.
	 * An example would be the buidMenuDescription(WorkbenchContext).  We want the passed in
	 * WorkbenchContext but the node's ApplicationContext
	 */
	protected WorkbenchContext buildLocalWorkbenchContext(WorkbenchContext workbenchContext) {
		return new ApplicationContextWorkbenchContext(workbenchContext, this.getApplicationContext());
	}

	protected WorkbenchContext buildShellWorkbenchContext() {
		return new ShellWorkbenchContext(this.getApplicationContext());
	}

	private PropertyChangeListener buildValueDisplayStringListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				AbstractApplicationNode.this.displayStringChanged();
			}
			public String toString() {
				return StringTools.buildToStringFor(this, "display string listener");
			}
		};
	}

	private PropertyChangeListener buildValueIconListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				AbstractApplicationNode.this.iconChanged();
			}
			public String toString() {
				return StringTools.buildToStringFor(this, "icon listener");
			}
		};
	}

	private PropertyChangeListener buildValueDirtyListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				AbstractApplicationNode.this.dirtyChanged();
			}
			public String toString() {
				return StringTools.buildToStringFor(this, "dirty listener");
			}
		};
	}

	private ListChangeListener buildValueBranchProblemsListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				AbstractApplicationNode.this.branchProblemsChanged();
			}
			public void itemsRemoved(ListChangeEvent e) {
				AbstractApplicationNode.this.branchProblemsChanged();
			}
			public void itemsReplaced(ListChangeEvent e) {
				AbstractApplicationNode.this.branchProblemsChanged();
			}
			public void listChanged(ListChangeEvent e) {
				AbstractApplicationNode.this.branchProblemsChanged();
			}
			public String toString() {
				return StringTools.buildToStringFor(this, "branch problems listener");
			}
		};
	}

	private PropertyChangeListener buildValuePropertiesPageTitleTextListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				AbstractApplicationNode.this.propertiesPageTitleTextChanged();
			}
			public String toString() {
				return StringTools.buildToStringFor(this, "properties page title text listener");
			}
		};
	}

	private PropertyChangeListener buildValuePropertiesPageTitleIconListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				AbstractApplicationNode.this.propertiesPageTitleIconChanged();
			}
			public String toString() {
				return StringTools.buildToStringFor(this, "properties page title icon listener");
			}
		};
	}

	
	// ********** AbstractTreeNodeValueModel implementation **********

	/**
	 * when we get here we know there are no "state" change listeners;
	 * now check for apect-specific listeners before engaging the
	 * various aspects of the value
	 */
	protected void engageValue() {
		this.engageValueProperties();
		this.engageValueLists();
	}

	protected void engageValueProperties() {
		if (this.hasNoPropertyChangeListeners(DISPLAY_STRING_PROPERTY)) {
			this.engageValueDisplayString();
		}
		if (this.hasNoPropertyChangeListeners(ICON_PROPERTY)) {
			this.engageValueIcon();
		}
		if (this.hasNoPropertyChangeListeners(DIRTY_PROPERTY)) {
			this.engageValueDirty();
		}
		if (this.hasNoPropertyChangeListeners(PROPERTIES_PAGE_TITLE_TEXT_PROPERTY)) {
			this.engageValuePropertiesPageTitleText();
		}
		if (this.hasNoPropertyChangeListeners(PROPERTIES_PAGE_TITLE_ICON_PROPERTY)) {
			this.engageValuePropertiesPageTitleIcon();
		}
	}

	protected void engageValueLists() {
		if (this.hasNoListChangeListeners(APPLICATION_PROBLEMS_LIST) && this.hasNoListChangeListeners(BRANCH_APPLICATION_PROBLEMS_LIST)) {
			this.engageValueBranchProblems();
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (this.hasNoStateChangeListeners()) {
			this.engageValueProperties();
		}
		super.addPropertyChangeListener(listener);
	}

	/**
	 * if a "state" change listener has not been previously added,
	 * check for property-specific listeners before engaging the
	 * various properties of the value
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (this.hasNoStateChangeListeners()) {
			if (this.hasNoPCLs(propertyName, DISPLAY_STRING_PROPERTY)) {
				this.engageValueDisplayString();
			}
			if (this.hasNoPCLs(propertyName, ICON_PROPERTY)) {
				this.engageValueIcon();
			}
			if (this.hasNoPCLs(propertyName, DIRTY_PROPERTY)) {
				this.engageValueDirty();
			}
			if (this.hasNoPCLs(propertyName, PROPERTIES_PAGE_TITLE_TEXT_PROPERTY)) {
				this.engageValuePropertiesPageTitleText();
			}
			if (this.hasNoPCLs(propertyName, PROPERTIES_PAGE_TITLE_ICON_PROPERTY)) {
				this.engageValuePropertiesPageTitleIcon();
			}
		}
		super.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * return whether the properties match and we have no
	 * listeners for the property
	 */
	protected boolean hasNoPCLs(String listenerPropertyName, String propertyName) {
		return (listenerPropertyName == propertyName) &&
					this.hasNoPropertyChangeListeners(propertyName);
	}

	public void addListChangeListener(ListChangeListener listener) {
		if (this.hasNoStateChangeListeners()) {
			this.engageValueLists();
		}
		super.addListChangeListener(listener);
	}

	/**
	 * if a "state" change listener has not been previously added,
	 * check for list-specific listeners before engaging the
	 * various lists of the value
	 */
	public void addListChangeListener(String listName, ListChangeListener listener) {
		if (this.hasNoStateChangeListeners()) {
			if (this.hasNoProblemListeners(listName)) {
				this.engageValueBranchProblems();
			}
		}
		super.addListChangeListener(listName, listener);
	}

	private boolean hasNoProblemListeners(String propertyName) {
		if ((propertyName == APPLICATION_PROBLEMS_LIST) || (propertyName == BRANCH_APPLICATION_PROBLEMS_LIST)) {
			return this.hasNoListChangeListeners(APPLICATION_PROBLEMS_LIST) &&
						this.hasNoListChangeListeners(BRANCH_APPLICATION_PROBLEMS_LIST);
		}
		return false;
	}

	protected void engageValue(String[] propertyNames, PropertyChangeListener listener) {
		this.engage(this.value, propertyNames, listener);
	}

	protected void engage(Model model, String[] propertyNames, PropertyChangeListener listener) {
		for (int i = propertyNames.length; i-- > 0; ) {
			model.addPropertyChangeListener(propertyNames[i], listener);
		}
	}

	protected void engageValueDisplayString() {
		this.engageValue(this.displayStringPropertyNames(), this.valueDisplayStringListener);
		this.rebuildDisplayString();
	}

	/**
	 * Return the names of the value's properties that affect
	 * the node's label.
	 */
	protected String[] displayStringPropertyNames() {
		return DEFAULT_DISPLAY_STRING_PROPERTY_NAMES;
	}

	protected void engageValueIcon() {
		this.engageValue(this.iconPropertyNames(), this.valueIconListener);
		this.rebuildIconBuilder();
		this.rebuildIcon();
	}

	/**
	 * Return the names of the value's properties that affect
	 * the node's icon.
	 */
	protected String[] iconPropertyNames() {
		return DEFAULT_ICON_PROPERTY_NAMES;
	}

	protected void engageValueDirty() {
		this.engageValue(this.dirtyPropertyNames(), this.valueDirtyListener);
		this.rebuildDirtyFlag();
	}

	/**
	 * Return the names of the value's properties that affect whether
	 * the node is "dirty".
	 */
	protected String[] dirtyPropertyNames() {
		return DEFAULT_DIRTY_PROPERTY_NAMES;
	}

	protected void engageValuePropertiesPageTitleText() {
		this.engageValue(this.propertiesPageTitleTextPropertyNames(), this.valuePropertiesPageTitleTextListener);
		this.rebuildPropertiesPageTitleText();
	}

	/**
	 * Return the names of the value's properties that affect
	 * the node's properties page title text.
	 */
	protected String[] propertiesPageTitleTextPropertyNames() {
		return this.displayStringPropertyNames();
	}

	protected void engageValuePropertiesPageTitleIcon() {
		this.engageValue(this.propertiesPageTitleIconPropertyNames(), this.valuePropertiesPageTitleIconListener);
		this.rebuildPropertiesPageTitleIconBuilder();
		this.rebuildPropertiesPageTitleIcon();
	}

	/**
	 * Return the names of the value's properties that affect
	 * the node's properties page title icon.
	 */
	protected String[] propertiesPageTitleIconPropertyNames() {
		return this.iconPropertyNames();
	}

	protected void engageValue(String[] listNames, ListChangeListener listener) {
		this.engage(this.value, listNames, listener);
	}

	protected void engage(Model model, String[] listNames, ListChangeListener listener) {
		for (int i = listNames.length; i-- > 0; ) {
			model.addListChangeListener(listNames[i], listener);
		}
	}

	/**
	 * Return the names of the value's lists that affect the
	 * node's branch problems.
	 */
	protected String[] branchProblemsListNames() {
		return DEFAULT_BRANCH_PROBLEMS_LIST_NAMES;
	}

	protected void engageValueBranchProblems() {
		this.engageValue(this.branchProblemsListNames(), this.valueBranchProblemsListener);
		this.rebuildApplicationProblems();
		this.rebuildBranchApplicationProblems();
	}


	/**
	 * when we get here we know there are no "state" change listeners;
	 * now check for aspect-specific listeners before disengaging the
	 * various aspects of the value
	 */
	protected void disengageValue() {
		this.disengageValueLists();
		this.disengageValueProperties();
	}

	protected void disengageValueLists() {
		if (this.hasNoListChangeListeners(APPLICATION_PROBLEMS_LIST) && this.hasNoListChangeListeners(BRANCH_APPLICATION_PROBLEMS_LIST)) {
			this.disengageValueBranchProblems();
		}
	}

	protected void disengageValueProperties() {
		if (this.hasNoPropertyChangeListeners(PROPERTIES_PAGE_TITLE_ICON_PROPERTY)) {
			this.disengageValuePropertiesPageTitleIcon();
		}
		if (this.hasNoPropertyChangeListeners(PROPERTIES_PAGE_TITLE_TEXT_PROPERTY)) {
			this.disengageValuePropertiesPageTitleText();
		}
		if (this.hasNoPropertyChangeListeners(DIRTY_PROPERTY)) {
			this.disengageValueDirty();
		}
		if (this.hasNoPropertyChangeListeners(ICON_PROPERTY)) {
			this.disengageValueIcon();
		}
		if (this.hasNoPropertyChangeListeners(DISPLAY_STRING_PROPERTY)) {
			this.disengageValueDisplayString();
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		if (this.hasNoStateChangeListeners()) {
			this.disengageValueProperties();
		}
	}

	/**
	 * if there are no more "state" change listeners remaining,
	 * check for property-specific listeners before disengaging the
	 * various properties of the value
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		super.removePropertyChangeListener(propertyName, listener);
		if (this.hasNoStateChangeListeners()) {
			if (this.hasNoPCLs(propertyName, PROPERTIES_PAGE_TITLE_ICON_PROPERTY)) {
				this.disengageValuePropertiesPageTitleIcon();
			}
			if (this.hasNoPCLs(propertyName, PROPERTIES_PAGE_TITLE_TEXT_PROPERTY)) {
				this.disengageValuePropertiesPageTitleText();
			}
			if (this.hasNoPCLs(propertyName, DIRTY_PROPERTY)) {
				this.disengageValueDirty();
			}
			if (this.hasNoPCLs(propertyName, ICON_PROPERTY)) {
				this.disengageValueIcon();
			}
			if (this.hasNoPCLs(propertyName, DISPLAY_STRING_PROPERTY)) {
				this.disengageValueDisplayString();
			}
		}
	}

	public void removeListChangeListener(ListChangeListener listener) {
		super.removeListChangeListener(listener);
		if (this.hasNoStateChangeListeners()) {
			this.disengageValueLists();
		}
	}

	/**
	 * if there are no more "state" change listeners remaining,
	 * check for list-specific listeners before disengaging the
	 * various lists of the value
	 */
	public void removeListChangeListener(String listName, ListChangeListener listener) {
		super.removeListChangeListener(listName, listener);
		if (this.hasNoStateChangeListeners()) {
			if (this.hasNoProblemListeners(listName)) {
				this.disengageValueBranchProblems();
			}
		}
	}

	protected void disengageValue(String[] propertyNames, PropertyChangeListener listener) {
		this.disengage(this.value, propertyNames, listener);
	}

	protected void disengage(Model model, String[] propertyNames, PropertyChangeListener listener) {
		for (int i = propertyNames.length; i-- > 0; ) {
			model.removePropertyChangeListener(propertyNames[i], listener);
		}
	}

	protected void disengageValue(String[] listNames, ListChangeListener listener) {
		this.disengage(this.value, listNames, listener);
	}

	protected void disengage(Model model, String[] listNames, ListChangeListener listener) {
		for (int i = listNames.length; i-- > 0; ) {
			model.removeListChangeListener(listNames[i], listener);
		}
	}

	protected void disengageValueBranchProblems() {
		this.disengageValue(this.branchProblemsListNames(), this.valueBranchProblemsListener);
		this.applicationProblems.clear();
		this.branchApplicationProblems.clear();
	}

	protected void disengageValuePropertiesPageTitleIcon() {
		this.disengageValue(this.propertiesPageTitleIconPropertyNames(), this.valuePropertiesPageTitleIconListener);
		this.propertiesPageTitleIconBuilder = null;
		this.propertiesPageTitleIcon = null;
	}

	protected void disengageValuePropertiesPageTitleText() {
		this.disengageValue(this.propertiesPageTitleTextPropertyNames(), this.valuePropertiesPageTitleTextListener);
		this.propertiesPageTitleText = null;
	}

	protected void disengageValueDirty() {
		this.disengageValue(this.dirtyPropertyNames(), this.valueDirtyListener);
		this.dirty = false;
	}

	protected void disengageValueIcon() {
		this.disengageValue(this.iconPropertyNames(), this.valueIconListener);
		this.iconBuilder = null;
		this.icon = null;
	}

	protected void disengageValueDisplayString() {
		this.disengageValue(this.displayStringPropertyNames(), this.valueDisplayStringListener);
		this.displayString = null;
	}


	protected PropertyChangeListener getValueDisplayStringListener() {
		return this.valueDisplayStringListener;
	}

	protected PropertyChangeListener getValueIconListener() {
		return this.valueIconListener;
	}

	protected PropertyChangeListener getValueDirtyListener() {
		return this.valueDirtyListener;
	}

	protected ListChangeListener getValueBranchProblemsListener() {
		return this.valueBranchProblemsListener;
	}

	protected PropertyChangeListener getValuePropertiesPageTitleTextListener() {
		return this.valuePropertiesPageTitleTextListener;
	}

	protected PropertyChangeListener getValuePropertiesPageTitleIconListener() {
		return this.valuePropertiesPageTitleIconListener;
	}


	// ********** factories **********

	protected final void rebuildDisplayString() {
		this.displayString = this.buildDisplayString();
	}

	/**
	 * The default is to take a display string directly from the value.
	 */
	protected String buildDisplayString() {
		return this.value.displayString();
	}

	protected final void rebuildIconBuilder() {
		this.iconBuilder = this.buildIconBuilder();
	}

	/**
	 * The default is to have no icon and to indicate
	 * whether the node has any "branch" problems.
	 */
	protected IconBuilder buildIconBuilder() {
		return new CompositeIconBuilder(
				this.buildBaseIconBuilder(),
				this.valueHasBranchProblems(),
				PROBLEM_ICON,
				-21,	// gap - overlay the "leading" edge of the unadorned icon
				SwingConstants.HORIZONTAL,	// orientation
				SwingConstants.BOTTOM,	// alignment
				null	// description
		);
	}

	/**
	 * The default is to have no icon and to indicate
	 * whether the node has any "branch" problems.
	 */
	protected IconBuilder buildBaseIconBuilder() {
		return new SimpleIconBuilder(this.resourceRepository().getIcon(this.buildIconKey()));
	}

	/**
	 * The default is to have no icon.
	 */
	protected String buildIconKey() {
		return null;
	}

	protected final void rebuildIcon() {
		this.icon = this.buildIcon();
	}

	/**
	 * The default is to have no icon and to indicate
	 * whether the node has any "branch" problems.
	 */
	protected final Icon buildIcon() {
		return this.iconBuilder.buildIcon();
	}

	protected final void rebuildDirtyFlag() {
		this.dirty = this.buildDirtyFlag();
	}

	/**
	 * The default is to use the value's dirty branch setting.
	 */
	protected boolean buildDirtyFlag() {
		return this.value.isDirtyBranch();
	}

	protected final void rebuildPropertiesPageTitleText() {
		this.propertiesPageTitleText = this.buildPropertiesPageTitleText();
	}

	/**
	 * The default is to use the display string.
	 */
	protected String buildPropertiesPageTitleText() {
		return this.buildDisplayString();
	}

	protected final void rebuildPropertiesPageTitleIconBuilder() {
		this.propertiesPageTitleIconBuilder = this.buildPropertiesPageTitleIconBuilder();
	}

	/**
	 * The default is to use the normal icon builder.
	 */
	protected IconBuilder buildPropertiesPageTitleIconBuilder() {
		return this.buildIconBuilder();
	}

	protected final void rebuildPropertiesPageTitleIcon() {
		this.propertiesPageTitleIcon = this.buildPropertiesPageTitleIcon();
	}

	/**
	 * The default builder will build the normal icon.
	 */
	protected final Icon buildPropertiesPageTitleIcon() {
		return this.propertiesPageTitleIconBuilder.buildIcon();
	}

	/**
	 * Rebuild the list of "exclusive" application problems based on the value's
	 * branch problems.
	 */
	protected final void rebuildApplicationProblems() {
		this.applicationProblems.clear();
		this.addExclusiveApplicationProblemsTo(this.applicationProblems);
	}

	protected void addExclusiveApplicationProblemsTo(List list) {
		for (ListIterator stream = this.value.branchProblems(); stream.hasNext(); ) {
			Problem problem = (Problem) stream.next();
			if (this.ownsExclusively(problem)) {
				list.add(this.buildApplicationProblem(problem));
			}
		}
	}

	/**
	 * Return whether the application node is the "exclusive owner" of
	 * the specified problem; i.e. the problem belongs to the node's branch
	 * but not to any children application nodes either.
	 * NB: There is NOT a 1:1 correspondence between application nodes
	 * and model nodes. There are many more model nodes than there are
	 * application nodes; but the application node containment hierarchies
	 * is roughly parallel to the model node containment hierarchy (with
	 * the exception of MWClass, which requires review until we have a
	 * visible Class Repository).
	 */
	private boolean ownsExclusively(Problem problem) {
		return this.value.containsBranchProblem(problem) && ( ! this.childrenContain(problem));
	}

	/**
	 * Return whether the application node's children "contain"
	 * the specified problem.
	 */
	private boolean childrenContain(Problem problem) {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			if (((ApplicationProblemContainer) stream.next()).containsBranchApplicationProblemFor(problem)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return an application problem for the specified model problem.
	 */
	ApplicationProblem buildApplicationProblem(Problem problem) {
		return new DefaultApplicationProblem(
			this,
			problem.getMessageKey(),
			this.resourceRepository().getString(problem.getMessageKey(), problem.getMessageArguments())
		);
	}

	/**
	 * Rebuild the list of "branch" application problems based on the value's
	 * branch problems.
	 */
	protected final void rebuildBranchApplicationProblems() {
		this.branchApplicationProblems.clear();
		this.addBranchApplicationProblemsTo(this.branchApplicationProblems);
	}


	// ********** queries **********

	public Iterator children() {
		return (Iterator) this.getChildrenModel().getValue();
	}

	protected boolean valueHasBranchProblems() {
		return this.value.hasBranchProblems();
	}

	public NodeManager nodeManager() {
		return this.getApplicationContext().getNodeManager();
	}

	protected ResourceRepository resourceRepository() {
		return this.getApplicationContext().getResourceRepository();
	}

	protected Preferences preferences() {
		return this.getApplicationContext().getPreferences();
	}


	// ********** behavior **********

	/**
	 * Select the descendant node with the specified value.
	 */
	public void selectDescendantNodeForValue(Node node, NavigatorSelectionModel nsm) {
		nsm.setSelectedNode(this.descendantNodeForValue(node));
	}
	
	/**
	 * replace the cached value and notify listeners
	 */
	protected void displayStringChanged() {
		Object old = this.displayString;
		this.rebuildDisplayString();
		this.firePropertyChanged(DISPLAY_STRING_PROPERTY, old, this.displayString);
		if (this.attributeValueHasChanged(old, this.displayString)) {
			this.fireStateChanged();
		}
	}

	/**
	 * replace the cached value and notify listeners
	 */
	protected void iconChanged() {
		Object oldBuilder = this.iconBuilder;
		this.rebuildIconBuilder();
		if ( ! this.iconBuilder.equals(oldBuilder)) {
			Object oldIcon = this.icon;
			this.rebuildIcon();
			this.firePropertyChanged(ICON_PROPERTY, oldIcon, this.icon);
			this.fireStateChanged();
		}
	}

	/**
	 * replace the cached value and notify listeners
	 */
	protected void dirtyChanged() {
		boolean old = this.dirty;
		this.rebuildDirtyFlag();
		this.firePropertyChanged(DIRTY_PROPERTY, old, this.dirty);
		if (this.dirty != old) {
			this.fireStateChanged();
		}
	}

	/**
	 * replace the cached value and notify listeners
	 */
	protected void propertiesPageTitleTextChanged() {
		Object old = this.propertiesPageTitleText;
		this.rebuildPropertiesPageTitleText();
		this.firePropertyChanged(PROPERTIES_PAGE_TITLE_TEXT_PROPERTY, old, this.propertiesPageTitleText);
		if (this.attributeValueHasChanged(old, this.propertiesPageTitleText)) {
			this.fireStateChanged();
		}
	}

	/**
	 * replace the cached value and notify listeners
	 */
	protected void propertiesPageTitleIconChanged() {
		Object oldBuilder = this.propertiesPageTitleIconBuilder;
		this.rebuildPropertiesPageTitleIconBuilder();
		if ( ! this.propertiesPageTitleIconBuilder.equals(oldBuilder)) {
			Object oldIcon = this.propertiesPageTitleIcon;
			this.rebuildPropertiesPageTitleIcon();
			this.firePropertyChanged(PROPERTIES_PAGE_TITLE_ICON_PROPERTY, oldIcon, this.propertiesPageTitleIcon);
			this.fireStateChanged();
		}
	}

	/**
	 * rebuild the cached values and notify listeners;
	 * completely rebuild the lists (we should only get
	 * here when the list of problems has *actually* changed)
	 */
	protected void branchProblemsChanged() {
		this.rebuildApplicationProblems();
		this.fireListChanged(APPLICATION_PROBLEMS_LIST);

		this.rebuildBranchApplicationProblems();
		this.fireListChanged(BRANCH_APPLICATION_PROBLEMS_LIST);
	}


	// ********** ValueModel implementation **********

	public Object getValue() {
		return this.value;
	}


	// ********** TreeNodeValueModel implementation **********

	public TreeNodeValueModel getParent() {
		return this.parent;
	}

	public ListValueModel getChildrenModel() {
		return NullListValueModel.instance();
	}


	// ********** ApplicationNode implementation **********

	public ApplicationContext getApplicationContext() {
		return this.context;
	}
	
	public Plugin getPlugin() {
		return this.plugin;
	}

	/**
	 * The project root nodes are the direct children of
	 * the "real" root node.
	 */
	public ApplicationNode getProjectRoot() {
		if (this.getParent() == this.nodeManager().getRootNode()) {
			return this;
		}
		return ((ApplicationNode) this.getParent()).getProjectRoot();
	}
	
	public final boolean isDirty() {
		return this.dirty;
	}

	public String helpTopicID() {
		return "default";
	}

	public boolean save(File mostRecentSaveDirectory, WorkbenchContext workbenchContext) {
		return this.getProjectRoot().save(mostRecentSaveDirectory, workbenchContext);
	}
	
	public boolean saveAs(File mostRecentSaveDirectory, WorkbenchContext workbenchContext) {
		return this.getProjectRoot().saveAs(mostRecentSaveDirectory, workbenchContext);
	}
	
	public File saveFile() {
		return this.getProjectRoot().saveFile();
	}


	public void addValuePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.value.addPropertyChangeListener(propertyName, listener);
	}

	public void removeValuePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.value.removePropertyChangeListener(propertyName, listener);
	}
	
	public final ApplicationNode descendantNodeForValue(Node node) {
		if (this.value == node) {
			return this;
		}
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			ApplicationNode appNode = ((ApplicationNode) stream.next()).descendantNodeForValue(node);	// recurse
			if (appNode != null) {
				return appNode;
			}
		}
		return null;
	}

	
	// ********** ApplicationProblemContainer implementation **********

	public final ListIterator applicationProblems() {
		return this.applicationProblems.listIterator();
	}

	public final int applicationProblemsSize() {
		return this.applicationProblems.size();
	}

	public final void addApplicationProblemsTo(List list) {
		list.addAll(this.applicationProblems);
	}

	public final ListIterator branchApplicationProblems() {
		return this.branchApplicationProblems.listIterator();
	}

	public final int branchApplicationProblemsSize() {
		return this.branchApplicationProblems.size();
	}

	public void addBranchApplicationProblemsTo(List list) {
		this.addApplicationProblemsTo(list);
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((ApplicationProblemContainer) stream.next()).addBranchApplicationProblemsTo(list);		// recurse
		}
	}

	public boolean containsBranchApplicationProblemFor(Problem problem) {
		return this.value.containsBranchProblem(problem);
	}

	/**
	 * Print the node's problem title, if appropriate; print the node's problems;
	 * then print the node's children's problems, recursively.
	 */
	public void printBranchApplicationProblemsOn(IndentingPrintWriter writer) {
		if (this.branchApplicationProblems.size() == 0) {
			return;
		}
		this.printBranchApplicationProblemsHeaderOn(writer);
		writer.println();
		writer.indent();
		for (Iterator stream = this.applicationProblems(); stream.hasNext(); ) {
			((ApplicationProblem) stream.next()).printOn(writer);
			writer.println();
		}
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((ApplicationProblemContainer) stream.next()).printBranchApplicationProblemsOn(writer);		// recurse
		}
		writer.undent();
	}

	/**
	 * Write a "problem header", if appropriate.
	 */
	protected void printBranchApplicationProblemsHeaderOn(IndentingPrintWriter writer) {
		writer.print(this.displayString());
	}


	// ********** EditorNode implementation **********

	public final String propertiesPageTitleText() {
		return this.propertiesPageTitleText;
	}

	public final Icon propertiesPageTitleIcon() {
		return this.propertiesPageTitleIcon;
	}


	// ********** Displayable implementation **********

	public final String displayString() {
		return this.displayString;
	}

	public final Icon icon() {
		return this.icon;
	}


	// ********** Comparable implementation **********

	public int compareTo(Object o) {
		// use the Comparator defined in Displayable, which compares displayStrings
		return DEFAULT_COMPARATOR.compare(this, o);
	}


	// ********** AccessibleNode implementation **********

	/**
	 * Returns a string that can add more description to the rendered object when
	 * the text is not sufficient, if <code>null</code> is returned, then the
	 * text is used as the accessible text.
	 */
	public String accessibleName() {
		return this.resourceRepository().getString(this.accessibleNameKey(), this.displayString());
	}

	protected String accessibleNameKey() {
		return "ACCESSIBLE_NODE";
	}

}
