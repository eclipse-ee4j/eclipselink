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

import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.uitools.OSplitPane;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


/**
 * A preferences view is placed inside a preferences dialog.
 * There is a single preferences view per application that is
 * shared among multiple preferences dialogs (although, there
 * should only be *one* preferences dialog instantiated at a time).
 * A preferences view contains a preferences navigator view and
 * a preferences editor view.
 * 
 * @see PreferencesDialog
 * @see PreferencesNavigatorView
 * @see PreferencesEditorView
 */
final class PreferencesView {

	/** save the root preferences node so we can import/export */
	private Preferences rootPreferences;

	/** the trigger pulled by the dialog's OK button */
	private BufferedPropertyValueModel.Trigger bufferTrigger;

	/** this holds the navigator on the "left" and the editor on the "right" */
	private JSplitPane splitPane;

	/** the "left" side view */
	private PreferencesNavigatorView navigatorView;

	/** the "right" side view */
	private PreferencesEditorView editorView;

	/** this holds either the selected node or an empty "pseudo-node" */
	private PropertyValueModel selectedNodeHolder;

	/** the pseudo-node used when none of the nodes in the tree are selected */
	private PreferencesNode emptySelectionNode;


	// ********** construction/initialization **********

	PreferencesView(AbstractPreferencesNode rootNode, BufferedPropertyValueModel.Trigger bufferTrigger) {
		super();
		this.initialize(rootNode, bufferTrigger);
	}

	private void initialize(AbstractPreferencesNode rootNode, BufferedPropertyValueModel.Trigger bufferTrigger) {
		rootPreferences = rootNode.getPreferences();
		this.bufferTrigger = bufferTrigger;
		emptySelectionNode = new PreferencesEmptySelectionPseudoNode();
		selectedNodeHolder = new SimplePropertyValueModel(emptySelectionNode);

		navigatorView = new PreferencesNavigatorView(rootNode);
		navigatorView.addTreeSelectionListener(this.buildTreeSelectionListener());

		editorView = new PreferencesEditorView(selectedNodeHolder);

		splitPane = this.buildSplitPane();
		navigatorView.selectFirstChild();
	}

	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				PreferencesView.this.selectedNodeChanged(e.getNewLeadSelectionPath());
			}
		};
	}

	private JSplitPane buildSplitPane() {
		JSplitPane splitPane = new OSplitPane();
		splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		splitPane.setDoubleBuffered(false);
		splitPane.setSize(new Dimension(750, 550));
		splitPane.setDividerLocation(0.2);
		SwingTools.setSplitPaneDividerBorder(splitPane, BorderFactory.createEmptyBorder());
		splitPane.setDividerSize(3);
		splitPane.setContinuousLayout(false);

		splitPane.setLeftComponent(navigatorView.getComponent());
		splitPane.setRightComponent(editorView.getComponent());

		return splitPane;
	}


	// ********** queries **********

	Component getComponent() {
		return splitPane;
	}
	
	PreferencesNavigatorView getView() {
		return navigatorView;
	}

	Component initialFocusComponent() {
		return navigatorView.initialFocusComponent();
	}


	// ********** behavior **********

	/**
	 * Whenever a node is selected or deselected, notify interested parties.
	 */
	private void selectedNodeChanged(TreePath path) {
		if (path == null) {
			selectedNodeHolder.setValue(emptySelectionNode);
		} else {
			selectedNodeHolder.setValue(path.getLastPathComponent());
		}
	}

	/**
	 * Trigger that the OK button has been pressed.
	 */
	void triggerAccept() {
		bufferTrigger.accept();
	}

	/**
	 * Trigger that the Reset button has been pressed.
	 */
	void triggerReset() {
		bufferTrigger.reset();
	}

	/**
	 * Export the entire tree of preferences to the specified file.
	 */
	void exportPreferences(File file) {
		OutputStream stream;
		try {
			stream = new BufferedOutputStream(new FileOutputStream(file), 2048);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		try {
			rootPreferences.exportSubtree(stream);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Import the specified file of preferences.
	 */
	void importPreferences(File file) {
		InputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		try {
			Preferences.importPreferences(stream);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}


	// ********** inner classes **********

	/**
	 * This is the node placed in the selectedNodeHolder when nothing
	 * in the navigator is selected.
	 */
	private static final class PreferencesEmptySelectionPseudoNode extends AbstractPreferencesNode {
		PreferencesEmptySelectionPseudoNode() {
			super(null);	// no context needed here
		}
		protected Component buildPropertiesPage() {
			return new JPanel();
		}
		protected String buildDisplayString() {
			return " ";	// put a space so the label preserves its size
		}
		public String helpTopicId() {
			return "preferences";
		}
	}

}
