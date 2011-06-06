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

import java.awt.Component;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationProblemContainer;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.IconBuilder;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemListListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemStateListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This is a pseudo-node that is "selected" whenever multiple nodes
 * on the tree are selected. It supplies a properties page that
 * simply lists the selected nodes in a list box.
 * 
 * There is one multi-selection "pseudo-node" per workspace view.
 */
final class MultiSelectionPseudoNode
	extends AbstractApplicationNode
{
	/**
	 * The selected nodes are added and removed by the workspace
	 * view, which is monitoring the navigator.
	 */
	private SimpleListValueModel selectedNodesHolder;

	/** cache the properties page so we don't have to rebuild it repeatedly */
	private LocalPropertiesPage propertiesPage;


	// ************ constructor/initialization ************

	/**
	 * use the super-secret, framework-only constructor...
	 */
	MultiSelectionPseudoNode(ApplicationContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		this.selectedNodesHolder = new SimpleListValueModel();
		ListValueModel lvm = new ItemListListValueModelAdapter(this.selectedNodesHolder, ApplicationProblemContainer.BRANCH_APPLICATION_PROBLEMS_LIST);
		lvm.addListChangeListener(ValueModel.VALUE, this.buildBranchApplicationProblemsListener());
	}

	private ListChangeListener buildBranchApplicationProblemsListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				MultiSelectionPseudoNode.this.branchProblemsChanged();
			}
			public void itemsRemoved(ListChangeEvent e) {
				MultiSelectionPseudoNode.this.branchProblemsChanged();
			}
			public void itemsReplaced(ListChangeEvent e) {
				MultiSelectionPseudoNode.this.branchProblemsChanged();
			}
			public void listChanged(ListChangeEvent e) {
				MultiSelectionPseudoNode.this.branchProblemsChanged();
			}
			public String toString() {
				return StringTools.buildToStringFor(this, "branch app problems listener");
			}
		};
	}


	// ********** AbstractTreeNodeValueModel overrides **********

	public boolean equals(Object o) {
		return this == o;
	}

	public int hashCode() {
		return System.identityHashCode(this);
	}


	// ********** AbstractApplicationNode overrides **********

	/**
	 * this node does not have a value; do not call this method
	 * willy-nilly on a collection of heterogeneous nodes  ~bjv
	 */
	public Object getValue() {
		throw new UnsupportedOperationException();
	}

	protected String buildDisplayString() {
		return this.resourceRepository().getString("MULTI_SELECTION_DISPLAY_STRING");
	}

	/**
	 * we don't have an icon
	 */
	protected IconBuilder buildIconBuilder() {
		return IconBuilder.NULL_INSTANCE;
	}

	/**
	 * we are never dirty
	 */
	protected boolean buildDirtyFlag() {
		return false;
	}

	/**
	 * increase visibility slightly for listener
	 */
	protected void branchProblemsChanged() {
		super.branchProblemsChanged();
	}

	/**
	 * delegate to the selected nodes
	 */
	protected void addExclusiveApplicationProblemsTo(List list) {
		for (Iterator stream = this.selectedNodes(); stream.hasNext(); ) {
			((ApplicationProblemContainer) stream.next()).addApplicationProblemsTo(list);
		}
	}

	/**
	 * delegate to the selected nodes
	 */
	public void addBranchApplicationProblemsTo(List list) {
		for (Iterator stream = this.selectedNodes(); stream.hasNext(); ) {
			((ApplicationProblemContainer) stream.next()).addBranchApplicationProblemsTo(list);
		}
	}

	/**
	 * should never be called...
	 */
	public boolean containsBranchApplicationProblemFor(Problem problem) {
		throw new UnsupportedOperationException();
	}

	public void printBranchApplicationProblemsOn(IndentingPrintWriter writer) {
		if (this.branchApplicationProblemsSize() == 0) {
			return;
		}
		for (Iterator stream = this.selectedNodes(); stream.hasNext(); ) {
			((ApplicationProblemContainer) stream.next()).printBranchApplicationProblemsOn(writer);		// recurse
		}
	}

	/**
	 * the problems view will want to listen to our list of problems,
	 * which is OK, but we don't have a value that we need to listen to;
	 * our list of problems is driven by the list of selected nodes
	 */
	protected void engageValueBranchProblems() {
		// do nothing since we don't have a value
	}
	protected void disengageValueBranchProblems() {
		// do nothing since we don't have a value
	}

	/**
	 * the properties page title label view will want to listen to our icon and text,
	 * which is OK, but we don't have a value that we need to listen to;
	 * the icon and text never change
	 */
	protected void engageValuePropertiesPageTitleIcon() {
		// nothing to engage
		this.rebuildPropertiesPageTitleIconBuilder();
		this.rebuildPropertiesPageTitleIcon();
	}
	protected void disengageValuePropertiesPageTitleIcon() {
		// nothing to disengage
	}
	protected void engageValuePropertiesPageTitleText() {
		// nothing to engage
		this.rebuildPropertiesPageTitleText();
	}
	protected void disengageValuePropertiesPageTitleText() {
		// nothing to disengage
	}


	// ********** ApplicationNode implementation **********

	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext) {
		throw new UnsupportedOperationException();
	}

	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext) {
		throw new UnsupportedOperationException();
	}	


	// ********** EditorNode implementation **********

	/**
	 * the workspace view should only have one multi-select node,
	 * so we only need one properties page
	 */
	public Component propertiesPage(WorkbenchContext workbenchContext) {
		if (this.propertiesPage == null) {
			WorkbenchContext ctx = this.buildLocalWorkbenchContext(workbenchContext);
			this.propertiesPage = new LocalPropertiesPage(ctx);
			this.propertiesPage.setNode(this, ctx);
		}
		return this.propertiesPage;
	}

	public void releasePropertiesPage(Component page) {
		// do nothing
	}


	// ********** miscellaneous **********

	/**
	 * this is how the workspace view notifies us that
	 * the selection has changed
	 */
	void setSelectedNodes(ApplicationNode[] newNodes) {
		this.selectedNodesHolder.clear();
		this.selectedNodesHolder.addItems(0, Arrays.asList(newNodes));
	}

	ListValueModel getSelectedNodesHolder() {
		return this.selectedNodesHolder;
	}

	private Iterator selectedNodes() {
		return (Iterator) this.selectedNodesHolder.getValue();
	}

	public void toString(StringBuffer sb) {
		sb.append("[multiple nodes]");
	}


	// ********** inner classes **********

	/**
	 * This is the properties page displayed when the user has selected
	 * multiple nodes in the "navigator" tree. Unlike most properties pages,
	 * this one is not shared among the workbench windows. There is
	 * one multi-selection "pseudo" node and properties page per
	 * workbench window.
	 */
	private class LocalPropertiesPage extends TitledPropertiesPage {

		LocalPropertiesPage(WorkbenchContext context) {
			super(context);
		}

		protected Component buildPage() {
			JList listBox = SwingComponentFactory.buildList(this.buildSelectedNodesListModel());
			listBox.setBorder(BorderFactory.createEmptyBorder());
			listBox.setCellRenderer(new DisplayableListCellRenderer());
	
			listBox.setBackground(UIManager.getColor("Panel.background"));
			listBox.setForeground(UIManager.getColor("List.foreground"));
			listBox.setSelectionBackground(UIManager.getColor("ScrollPane.background"));
			listBox.setSelectionForeground(listBox.getForeground());
	
			JScrollPane scrollPane = new JScrollPane(listBox);
			scrollPane.setBorder(new EmptyBorder(5, 0, 0, 0));
			scrollPane.getVerticalScrollBar().setUnitIncrement(10);
			scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
	
			return scrollPane;
		}

		/**
		 * keep the nodes in the same order as they appear in the navigator;
		 * this also keeps them in the same order as their problems in
		 * the ProblemsView
		 */
		private ListModel buildSelectedNodesListModel() {
			return new ListModelAdapter(this.buildStateChangeAdapter());
			// use the following bit of code to sort the nodes by display string:
//			return new ListModelAdapter(new SortedListValueModelAdapter(this.buildStateChangeAdapter()));
		}

		private ListValueModel buildStateChangeAdapter() {
			return new ItemStateListValueModelAdapter(MultiSelectionPseudoNode.this.getSelectedNodesHolder());
		}

	}

}
