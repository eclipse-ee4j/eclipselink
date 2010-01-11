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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationProblemContainer;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.AutomappableNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.RemovableNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;


/**
 * This is not a "typical" node in that it is a "virtual" node that does
 * not have a corresponding object in the model. Descriptor Package
 * Nodes are created and destroyed by the Project Node as it monitors
 * the adding, removing, and renaming of Descriptors in a Project.
 */
public abstract class DescriptorPackageNode
	extends MappingsApplicationNode
	implements ProjectNode.Child, UnmappablePackageNode, AutomappableNode, RemovableNode
{
	/**
	 * We don't have a corresponding object in the model,
	 * we just have a name.
	 */
	private String name;

	/**
	 * Clients must supply us with a descriptor node builder
	 * that will build the appropriate descriptor nodes for us.
	 */
	private DescriptorNodeBuilder descriptorNodeBuilder;

	/**
	 * The descriptor nodes are added and removed by our parent
	 * project node, which is monitoring the project's 'descriptors'
	 * collection.
	 */
	private SimpleCollectionValueModel descriptorNodesHolder;

	/**
	 * This is a wrapper around the descriptorNodesHolder that
	 * is sorted and re-sorts whenever a descriptor's name changes.
	 */
	private ListValueModel childrenModel;

	/**
	 * We track whether a descriptor node has been removed.
	 * If it has, we mark the package node dirty, even if none of
	 * the remaining descriptor nodes are dirty.
	 */
	private boolean descriptorNodeHasBeenRemoved;

	/**
	 * When the project node has been marked clean, we can
	 * reset the descriptorNodeHasBeenRemoved flag.
	 */
	private PropertyChangeListener projectNodeDirtyFlagListener;


	// ********** constructor/initialization ************

	protected DescriptorPackageNode(String name, ProjectNode parent, DescriptorNodeBuilder descriptorNodeBuilder) {
		// no model!
		super(null, parent, parent.getPlugin(), parent.getApplicationContext());
		this.name = name;
		this.descriptorNodeBuilder = descriptorNodeBuilder;
	}

	protected void initialize() {
		super.initialize();
		this.descriptorNodesHolder = new SimpleCollectionValueModel();
		this.childrenModel = this.buildChildrenModel();
		this.descriptorNodeHasBeenRemoved = false;
		this.projectNodeDirtyFlagListener = this.buildProjectNodeDirtyFlagListener();
	}

	/**
	 * keep the descriptor nodes sorted
	 */
	protected ListValueModel buildChildrenModel() {
		return new SortedListValueModelAdapter(this.buildDescriptorsDisplayStringAdapter());
	}

	/**
	 * the display string (name) of each descriptor node can change
	 * and trigger a re-sort of the list
	 */
	protected ListValueModel buildDescriptorsDisplayStringAdapter() {
		return new ItemPropertyListValueModelAdapter(this.descriptorNodesHolder, DISPLAY_STRING_PROPERTY);
	}

	protected PropertyChangeListener buildProjectNodeDirtyFlagListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				DescriptorPackageNode.this.projectNodeDirtyFlagChanged(e);
			}
			public String toString() {
				return "project node dirty flag listener";
			}
		};
	}


	// ********** AbstractTreeNodeValueModel overrides **********

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o.getClass() != this.getClass()) {
			return false;
		}
		DescriptorPackageNode other = (DescriptorPackageNode) o;
		return this.getParent().equals(other.getParent()) &&
					this.name.equals(other.name);
	}

	public int hashCode() {
		return this.name.hashCode();
	}
	
	public void toString(StringBuffer sb) {
		sb.append(this.name);
	}

	
	// ********** AbstractApplicationNode overrides **********

	/**
	 * this node does not have a value; do not call this method
	 * willy-nilly on a collection of heterogeneous nodes  ~bjv
	 */
	public Object getValue() {
		throw new UnsupportedOperationException();
	}

	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}

	public String helpTopicID() {
		return "package";
	}


	/**
	 * the name will temporarily be null during intialization
	 */
	protected String buildDisplayString() {
		return (this.name == null) ? "" : (this.name.length() == 0) ? this.resourceRepository().getString("DEFAULT_PACKAGE") : this.name;
	}

	protected String buildIconKey() {
		return "package";
	}

	/**
	 * delegate to the descriptor nodes; if none of the descriptor nodes is dirty,
	 * check whether a descriptor node was removed earlier
	 */
	protected boolean buildDirtyFlag() {
		for (Iterator stream = this.descriptors(); stream.hasNext(); ) {
			if (((MWDescriptor) stream.next()).isDirtyBranch()) {
				return true;
			}
		}
		return this.descriptorNodeHasBeenRemoved;
	}

	/**
	 * delegate to the descriptors
	 */
	protected boolean valueHasBranchProblems() {
		for (Iterator stream = this.descriptors(); stream.hasNext(); ) {
			if (((MWDescriptor) stream.next()).hasBranchProblems()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * packages don't have "exclusive" application problems,
	 * they only have "branch" application problems
	 */
	protected void addExclusiveApplicationProblemsTo(List list) {
		// no problems
	}

	/**
	 * delegate to the descriptor nodes
	 */
	public boolean containsBranchApplicationProblemFor(Problem problem) {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			if (((DescriptorNode) stream.next()).containsBranchApplicationProblemFor(problem)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * we don't have a value, so engage the descriptor nodes
	 */
	protected void engageValueDisplayString() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.engageDescriptorNodeDisplayString((DescriptorNode) stream.next());
		}
		this.rebuildDisplayString();
	}

	protected void engageDescriptorNodeDisplayString(DescriptorNode descriptorNode) {
		descriptorNode.addPropertyChangeListener(DISPLAY_STRING_PROPERTY, this.getValueDisplayStringListener());
	}

	/**
	 * we don't have a value, so engage the descriptor nodes
	 */
	protected void engageValueIcon() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.engageDescriptorNodeIcon((DescriptorNode) stream.next());
		}
		this.rebuildIconBuilder();
		this.rebuildIcon();
	}

	protected void engageDescriptorNodeIcon(DescriptorNode descriptorNode) {
		descriptorNode.addPropertyChangeListener(ICON_PROPERTY, this.getValueIconListener());
	}

	/**
	 * we don't have a value, so engage the descriptor nodes;
	 * in certain situations, the project node being marked clean
	 * can cause our dirty flag to be cleared also
	 */
	protected void engageValueDirty() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.engageDescriptorNodeDirty((DescriptorNode) stream.next());
		}
		this.getProjectNode().addPropertyChangeListener(DIRTY_PROPERTY, this.projectNodeDirtyFlagListener);
		this.rebuildDirtyFlag();
	}

	protected void engageDescriptorNodeDirty(DescriptorNode descriptorNode) {
		descriptorNode.addPropertyChangeListener(DIRTY_PROPERTY, this.getValueDirtyListener());
	}

	/**
	 * we don't have a value, so engage the descriptor nodes
	 */
	protected void engageValuePropertiesPageTitleIcon() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.engageDescriptorNodePropertiesPageTitleIcon((DescriptorNode) stream.next());
		}
		this.rebuildPropertiesPageTitleIconBuilder();
		this.rebuildPropertiesPageTitleIcon();
	}

	protected void engageDescriptorNodePropertiesPageTitleIcon(DescriptorNode descriptorNode) {
		descriptorNode.addPropertyChangeListener(PROPERTIES_PAGE_TITLE_ICON_PROPERTY, this.getValuePropertiesPageTitleIconListener());
	}

	/**
	 * we don't have a value, so engage the descriptor nodes
	 */
	protected void engageValuePropertiesPageTitleText() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.engageDescriptorNodePropertiesPageTitleText((DescriptorNode) stream.next());
		}
		this.rebuildPropertiesPageTitleText();
	}

	protected void engageDescriptorNodePropertiesPageTitleText(DescriptorNode descriptorNode) {
		descriptorNode.addPropertyChangeListener(PROPERTIES_PAGE_TITLE_TEXT_PROPERTY, this.getValuePropertiesPageTitleTextListener());
	}

	/**
	 * we don't have a value, so engage the descriptor nodes;
	 * Listen to the node instead of the model. The problems are built based
	 * on the nodes, so it is possible that the package node could be informed
	 * before the descriptor node. This would cause the problems to be out of synch.
	 */
	protected void engageValueBranchProblems() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.engageDescriptorNodeBranchProblems((DescriptorNode) stream.next());
		}
		this.rebuildApplicationProblems();
		this.rebuildBranchApplicationProblems();
	}

	protected void engageDescriptorNodeBranchProblems(DescriptorNode descriptorNode) {
		descriptorNode.addListChangeListener(ApplicationProblemContainer.BRANCH_APPLICATION_PROBLEMS_LIST, this.getValueBranchProblemsListener());
	}


	/**
	 * we don't have a value, so disengage the descriptor nodes
	 */
	protected void disengageValueDisplayString() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.disengageDescriptorNodeDisplayString((DescriptorNode) stream.next());
		}
	}

	protected void disengageDescriptorNodeDisplayString(DescriptorNode descriptorNode) {
		descriptorNode.removePropertyChangeListener(DISPLAY_STRING_PROPERTY, this.getValueDisplayStringListener());
	}

	/**
	 * we don't have a value, so disengage the descriptor nodes
	 */
	protected void disengageValueIcon() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.disengageDescriptorNodeIcon((DescriptorNode) stream.next());
		}
	}

	protected void disengageDescriptorNodeIcon(DescriptorNode descriptorNode) {
		descriptorNode.removePropertyChangeListener(ICON_PROPERTY, this.getValueIconListener());
	}

	/**
	 * we don't have a value, so disengage the descriptor nodes
	 */
	protected void disengageValueDirty() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.disengageDescriptorNodeDirty((DescriptorNode) stream.next());
		}
		this.getProjectNode().removePropertyChangeListener(DIRTY_PROPERTY, this.projectNodeDirtyFlagListener);
	}

	protected void disengageDescriptorNodeDirty(DescriptorNode descriptorNode) {
		descriptorNode.removePropertyChangeListener(DIRTY_PROPERTY, this.getValueDirtyListener());
	}

	/**
	 * we don't have a value, so disengage the descriptor nodes
	 */
	protected void disengageValuePropertiesPageTitleIcon() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.disengageDescriptorNodePropertiesPageTitleIcon((DescriptorNode) stream.next());
		}
	}

	protected void disengageDescriptorNodePropertiesPageTitleIcon(DescriptorNode descriptorNode) {
		descriptorNode.removePropertyChangeListener(PROPERTIES_PAGE_TITLE_ICON_PROPERTY, this.getValuePropertiesPageTitleIconListener());
	}

	/**
	 * we don't have a value, so disengage the descriptor nodes
	 */
	protected void disengageValuePropertiesPageTitleText() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.disengageDescriptorNodePropertiesPageTitleText((DescriptorNode) stream.next());
		}
	}

	protected void disengageDescriptorNodePropertiesPageTitleText(DescriptorNode descriptorNode) {
		descriptorNode.removePropertyChangeListener(PROPERTIES_PAGE_TITLE_TEXT_PROPERTY, this.getValuePropertiesPageTitleTextListener());
	}

	/**
	 * we don't have a value, so disengage the descriptor nodes
	 */
	protected void disengageValueBranchProblems() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			this.disengageDescriptorNodeBranchProblems((DescriptorNode) stream.next());
		}
	}

	protected void disengageDescriptorNodeBranchProblems(DescriptorNode descriptorNode) {
		descriptorNode.removeListChangeListener(ApplicationProblemContainer.BRANCH_APPLICATION_PROBLEMS_LIST, this.getValueBranchProblemsListener());
	}


	public void addValuePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		// do nothing, no model to listen to
	}

	public void removeValuePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		// do nothing, no model to unlisten :) to
	}

	/**
	 * Returns a string that can add more description to the rendered object when
	 * the text is not sufficient, if <code>null</code> is returned, then the
	 * text is used as the accessible text.
	 */
	public String accessibleName() {
		if ((this.name == null) || (this.name.length() == 0)) {
			return this.resourceRepository().getString("DEFAULT_PACKAGE");
		}
		return this.resourceRepository().getString("ACCESSIBLE_PACKAGE_NODE", this.displayString());
	}


	// ********** MappingsApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return DescriptorPackagePropertiesPage.class;
	}

	public String candidatePackageName() {
		return this.name;
	}

	public boolean isAutoMappable() {
		return true;
	}

	public void addDescriptorsTo(Collection descriptors) {
		CollectionTools.addAll(descriptors, this.descriptors());
	}

	public Iterator descriptors() {
		return new TransformationIterator(this.descriptorNodes()) {
			protected Object transform(Object next) {
				return ((DescriptorNode) next).getDescriptor();
			}
		};
	}


	// ********** ApplicationNode implementation **********

	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext) {
		RootMenuDescription desc = new RootMenuDescription();
		this.addToMenuDescription(desc, buildLocalWorkbenchContext(workbenchContext));
		return desc;
	}

	protected abstract void addToMenuDescription(GroupContainerDescription menuDescription, WorkbenchContext context);

	protected MenuGroupDescription buildClassActionGroup(WorkbenchContext context) {
		MenuGroupDescription classActionGroup = new MenuGroupDescription();
		classActionGroup.add(this.getMappingsPlugin().getRefreshClassesAction(context));
		classActionGroup.add(this.getMappingsPlugin().getAddOrRefreshClassesAction(context));
		classActionGroup.add(this.getMappingsPlugin().getCreateNewClassAction(context));
		return classActionGroup;
	}

	protected MenuGroupDescription buildRemoveActionGroup(WorkbenchContext context) {
		MenuGroupDescription removeActionGroup = new MenuGroupDescription();
		removeActionGroup.add(this.getMappingsPlugin().getRemoveAction(context));
		removeActionGroup.add(this.getRenamePackageAction(context));
		return removeActionGroup;
	}

	protected MenuGroupDescription buildUnmapActionGroup(WorkbenchContext context) {
		MenuGroupDescription unMapGroup = new MenuGroupDescription();
		unMapGroup.add(this.getUnmapAllDescriptorsInPackageAction(context));
		return unMapGroup;
	}

	protected MenuGroupDescription buildExportJavaSourceActionGroup(WorkbenchContext context) {
		MenuGroupDescription exportJavaSourceGroup = new MenuGroupDescription();
		exportJavaSourceGroup.add(this.getMappingsPlugin().getExportSpecificDescriptorModelJavaSourceAction(context));
		return exportJavaSourceGroup;
	}

	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext) {
		return new ToolBarDescription();
	}

	private FrameworkAction getUnmapAllDescriptorsInPackageAction(WorkbenchContext context) {
		return new UnmapAllDescriptorsInPackageAction(context);
	}

	private FrameworkAction getRenamePackageAction(WorkbenchContext context) {
		return new RenamePackageAction(context);
	}


	// *********** ProjectNode.Child implementation ***********

	/**
	 * packages go first under the project
	 */
	public int getProjectNodeChildPriority() {
		return 0;
	}


	// ********** AutomappableNode implementation **********

	public String getAutomapSuccessfulStringKey() {
		return "AUTOMAP_PACKAGE_SUCCESSFUL";
	}
	

	// ********** UnmappablePackageNode implementation **********

	public void unmapEntirePackage() {
		this.unmap();
	}

	public void unmap() {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			((DescriptorNode) stream.next()).unmap();
		}
	}


	// ********** RemovableNode implementation **********
	
	public void remove() {
		for (Iterator stream = CollectionTools.collection(this.descriptorNodes()).iterator(); stream.hasNext(); ) {
			((DescriptorNode) stream.next()).remove();
		}
	}
	
	public String getName() {
		return this.name;
	}


	// ********** accessors **********

	public Iterator descriptorNodes() {
		return (Iterator) this.descriptorNodesHolder.getValue();
	}

	public int descriptorNodesSize() {
		return this.descriptorNodesHolder.size();
	}

	private void addDescriptorNode(DescriptorNode descriptorNode) {
		this.descriptorNodesHolder.addItem(descriptorNode);
	}

	private void removeDescriptorNode(DescriptorNode descriptorNode) {
		this.descriptorNodesHolder.removeItem(descriptorNode);
	}

	public DescriptorNode descriptorNodeFor(MWDescriptor descriptor) {
		for (Iterator stream = this.descriptorNodes(); stream.hasNext(); ) {
			DescriptorNode node = (DescriptorNode) stream.next();
			if (node.getDescriptor() == descriptor) {
				return node;
			}
		}
		return null;
	}

	public void addDescriptorNodeFor(MWDescriptor descriptor) {
		DescriptorNode descriptorNode = this.descriptorNodeBuilder.buildDescriptorNode(descriptor, this);
		this.addDescriptorNode(descriptorNode);
		boolean hasAnyStateChangeListeners = this.hasAnyStateChangeListeners();
		if (hasAnyStateChangeListeners || this.hasAnyPropertyChangeListeners(DISPLAY_STRING_PROPERTY)) {
			this.engageDescriptorNodeDisplayString(descriptorNode);
		}
		if (hasAnyStateChangeListeners || this.hasAnyPropertyChangeListeners(ICON_PROPERTY)) {
			this.engageDescriptorNodeIcon(descriptorNode);
		}
		if (hasAnyStateChangeListeners || this.hasAnyPropertyChangeListeners(DIRTY_PROPERTY)) {
			this.engageDescriptorNodeDirty(descriptorNode);
		}
		if (hasAnyStateChangeListeners || this.hasAnyPropertyChangeListeners(PROPERTIES_PAGE_TITLE_ICON_PROPERTY)) {
			this.engageDescriptorNodePropertiesPageTitleIcon(descriptorNode);
		}
		if (hasAnyStateChangeListeners || this.hasAnyPropertyChangeListeners(PROPERTIES_PAGE_TITLE_TEXT_PROPERTY)) {
			this.engageDescriptorNodePropertiesPageTitleText(descriptorNode);
		}
		if (hasAnyStateChangeListeners || this.hasAnyListChangeListeners(BRANCH_APPLICATION_PROBLEMS_LIST)) {
			this.engageDescriptorNodeBranchProblems(descriptorNode);
		}
		this.iconChanged();
		this.dirtyChanged();
		this.propertiesPageTitleIconChanged();
		this.propertiesPageTitleTextChanged();
		this.branchProblemsChanged();
	}

	public void removeDescriptorNodeFor(MWDescriptor descriptor) {
		DescriptorNode descriptorNode = this.descriptorNodeFor(descriptor);
		boolean hasAnyStateChangeListeners = this.hasAnyStateChangeListeners();
		if (hasAnyStateChangeListeners || this.hasAnyPropertyChangeListeners(DISPLAY_STRING_PROPERTY)) {
			this.disengageDescriptorNodeDisplayString(descriptorNode);
		}
		if (hasAnyStateChangeListeners || this.hasAnyPropertyChangeListeners(ICON_PROPERTY)) {
			this.disengageDescriptorNodeIcon(descriptorNode);
		}
		if (hasAnyStateChangeListeners || this.hasAnyPropertyChangeListeners(DIRTY_PROPERTY)) {
			this.disengageDescriptorNodeDirty(descriptorNode);
		}
		if (hasAnyStateChangeListeners || this.hasAnyPropertyChangeListeners(PROPERTIES_PAGE_TITLE_ICON_PROPERTY)) {
			this.disengageDescriptorNodePropertiesPageTitleIcon(descriptorNode);
		}
		if (hasAnyStateChangeListeners || this.hasAnyPropertyChangeListeners(PROPERTIES_PAGE_TITLE_TEXT_PROPERTY)) {
			this.disengageDescriptorNodePropertiesPageTitleText(descriptorNode);
		}
		if (hasAnyStateChangeListeners || this.hasAnyListChangeListeners(BRANCH_APPLICATION_PROBLEMS_LIST)) {
			this.disengageDescriptorNodeBranchProblems(descriptorNode);
		}
		this.removeDescriptorNode(descriptorNode);
		this.descriptorNodeHasBeenRemoved = true;
		this.iconChanged();
		this.dirtyChanged();
		this.propertiesPageTitleIconChanged();
		this.propertiesPageTitleTextChanged();
		this.branchProblemsChanged();
	}

	/**
	 * If the project has been saved and the project node marked clean;
	 * reset the flag indicating whether a descriptor node has been removed.
	 */
	void projectNodeDirtyFlagChanged(PropertyChangeEvent e) {
		boolean oldDirtyFlagValue = ((Boolean) e.getOldValue()).booleanValue();
		boolean newDirtyFlagValue = ((Boolean) e.getNewValue()).booleanValue();
		if ((oldDirtyFlagValue == true) && (newDirtyFlagValue == false)) {
			// dirty => clean
			this.descriptorNodeHasBeenRemoved = false;
			this.dirtyChanged();
		}
	}


	// ********** nested interface **********

 	/**
	 * This allows a descriptor package node to be configured to
	 * build different child descriptor nodes, depending on the client
	 * (which is typically a project node).
 	 */
 	public interface DescriptorNodeBuilder {

 		/**
 		 * Build and return a node for the specified descriptor.
 		 */
		DescriptorNode buildDescriptorNode(MWDescriptor descriptor, DescriptorPackageNode descriptorPackageNode);

 	}

}
