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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.Component;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.uitools.OSplitPane;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


/**
 * A workspace view is typically contained inside a workbench window.
 * A workspace view contains a navigator view, an editor view, and
 * a problems view.
 * 
 * @see WorkbenchWindow
 * @see NavigatorView
 * @see EditorView
 * @see ProblemsView
 */
final class WorkspaceView {

	/**
	 * this holds the navigator and editor on the top
	 * and the problems on the bottom
	 */
	private JSplitPane splitPane;

	/**
	 * This holds the navigator on the "left" and the
	 * editor on the "right" (depending on the component orientation).
	 */
	private JSplitPane topSplitPane;

	/**
	 * Listen to the tree selection so we can update
	 * the selected node etc.
	 */
	private TreeSelectionListener treeSelectionListener;

	/** the top "left" side view */
	private NavigatorView navigatorView;

	/** the top "right" side view */
	private EditorView editorView;

	/** the bottom view */
	private ProblemsView problemsView;

	/** this holds either the (single) selected node or one of the "pseudo-nodes" */
	private PropertyValueModel selectedNodeHolder;

	/** the pseudo-node used when there are multiple nodes selected in the tree */
	private MultiSelectionPseudoNode multiSelectionNode;

	/** the pseudo-node used when none of the nodes in the tree are selected */
	private ApplicationNode emptySelectionNode;


	// ********** construction/initialization **********

	WorkspaceView(WorkbenchContext context, ValueModel selectionMenuDescriptionHolder) {
		super();
		this.multiSelectionNode = new MultiSelectionPseudoNode(context.getApplicationContext());
		this.emptySelectionNode = new EmptySelectionPseudoNode(context.getApplicationContext());
		this.selectedNodeHolder = new SimplePropertyValueModel(this.emptySelectionNode);

		this.navigatorView = new NavigatorView(context.getApplicationContext(), selectionMenuDescriptionHolder);
		this.treeSelectionListener = this.buildTreeSelectionListener();
		this.navigatorView.addTreeSelectionListener(this.treeSelectionListener);

		this.editorView = new EditorView(this.selectedNodeHolder, context);

		this.problemsView = new ProblemsView(this.selectedNodeHolder, this.navigatorSelectionModel(), context);

		this.topSplitPane = buildTopSplitPane();
		this.splitPane = this.buildSplitPane();
	}

	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				ApplicationNode[] selectedNodes = ((NavigatorSelectionModel) e.getSource()).getSelectedNodes();
				WorkspaceView.this.selectedNodesChanged(selectedNodes);
			}
		};
	}

	/**
	 * This split pane will have the navigator and editor on top
	 * and the problems on the bottom.
	 */
	private JSplitPane buildSplitPane() {
		JSplitPane sPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		sPane.setDoubleBuffered(false);
		SwingTools.setSplitPaneDividerBorder(sPane, BorderFactory.createEmptyBorder());
		sPane.setDividerSize(3);
		sPane.setResizeWeight(1);
		sPane.setContinuousLayout(false);

		sPane.setTopComponent(this.topSplitPane);
		sPane.setBottomComponent(this.problemsView.getComponent());
		sPane.setDividerLocation(475);

		return sPane;
	}

	/**
	 * This split pane will have the navigator on the "left" and the
	 * editor on the "right" (depending on the component orientation).
	 */
	private JSplitPane buildTopSplitPane() {
		JSplitPane sPane = new OSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		sPane.setBorder(BorderFactory.createEmptyBorder());
		sPane.setDoubleBuffered(false);
		SwingTools.setSplitPaneDividerBorder(sPane, BorderFactory.createEmptyBorder());
		sPane.setDividerSize(3);
		sPane.setResizeWeight(0);
		sPane.setContinuousLayout(false);
		
		sPane.setLeftComponent(this.navigatorView.getComponent());
		sPane.setRightComponent(this.editorView.getComponent());
		sPane.setDividerLocation(250);

		return sPane;
	}


	// ********** queries **********

	Component getComponent() {
		return this.splitPane;
	}

	/**
	 * This is provided to clients via the WorkbenchContext.
	 */
	NavigatorSelectionModel navigatorSelectionModel() {
		return this.navigatorView.getSelectionModel();
	}

	boolean problemsAreVisible() {
		return this.splitPane.getBottomComponent() != null;
	}

	/**
	 * the problem report action is shared by the problems
	 * view and the workbench window
	 */
	Action problemReportAction() {
		return this.problemsView.getProblemReportAction();
	}

    Component getPropertiesPage() {
        return this.editorView.getPropertiesPage();
    }

	// ********** behavior **********

	/**
	 * Whenever nodes are selected or deselected, notify interested parties.
	 */
	void selectedNodesChanged(ApplicationNode[] selectedNodes) {
		this.multiSelectionNode.setSelectedNodes(selectedNodes);
		ApplicationNode selectedNode;
		switch (selectedNodes.length) {
			case 0:  selectedNode = this.emptySelectionNode; break;
			case 1:  selectedNode = selectedNodes[0]; break;
			default: selectedNode = this.multiSelectionNode; break;
		}
		this.selectedNodeHolder.setValue(selectedNode);
	}

	/**
	 * Return the new setting.
	 */
	boolean toggleShowProblems() {
		if (this.problemsAreVisible()) {
			this.splitPane.setBottomComponent(null);
			return false;
		}
		this.splitPane.setBottomComponent(this.problemsView.getComponent());
		this.splitPane.setDividerLocation(0.8);
		return true;
	}

	void addNavigatorTreeSelectionListener(TreeSelectionListener listener) {
		this.navigatorView.addTreeSelectionListener(listener);
	}

	void removeNavigatorTreeSelectionListener(TreeSelectionListener listener) {
		this.navigatorView.removeTreeSelectionListener(listener);
	}

	void copySettingsFrom(WorkspaceView workspace) {
		this.splitPane.setDividerLocation(workspace.splitPane.getDividerLocation());
		this.topSplitPane.setDividerLocation(workspace.topSplitPane.getDividerLocation());
		if ( ! workspace.problemsAreVisible()) {
			this.toggleShowProblems();
		}
	}
	
	void saveState(Preferences windowsPreferences) {
		windowsPreferences.putInt("split pane divider location", this.splitPane.getDividerLocation());
		windowsPreferences.putInt("top split pane divider location", this.topSplitPane.getDividerLocation());
	}
	
	void restoreState(Preferences windowsPreferences) {
		int dividerLocation = windowsPreferences.getInt("split pane divider location", this.splitPane.getDividerLocation());		
		this.splitPane.setDividerLocation(dividerLocation);

		int topDividerLocation = windowsPreferences.getInt("top split pane divider location", this.topSplitPane.getDividerLocation());		
		this.topSplitPane.setDividerLocation(topDividerLocation);
	}
	
	void saveTreeExpansionState(Preferences windowsPreferences) {
		this.navigatorView.saveTreeExpansionState(windowsPreferences);
	}

	void restoreTreeExpansionState(Preferences windowsPreferences) {
		this.navigatorView.restoreTreeExpansionState(windowsPreferences);	
	}

	/**
	 * This is called when the window containing the workspace is closed.
	 */
	void close() {
		this.navigatorView.close();
		this.editorView.close();
		this.problemsView.close();
		this.navigatorView.removeTreeSelectionListener(this.treeSelectionListener);
	}

}
