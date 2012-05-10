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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.app.EditorNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


/**
 * This view lays out a swappable properties page in a JPanel.
 * When the selected node changes, the newly-selected node is asked for
 * its properties page; and the previously selected node is notified that its
 * properties page is no longer needed. The selected nodes must implement
 * the EditorNode interface.
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.EditorNode
 */
abstract class AbstractEditorView {

	private WorkbenchContext context;

	/**
	 * The node whose properties page is currently
	 * displayed by the view. The node should never be null.
	 */
	private ValueModel nodeHolder;
	EditorNode node;
	private PropertyChangeListener nodeListener;

	/**
	 * The currently displayed properties page.
	 */
	private Component propertiesPage;

	/**
	 * The panel holding the current properties page.
	 */
	private JPanel panel;


	// ********** constructors/initialization **********

	AbstractEditorView(ValueModel nodeHolder, WorkbenchContext context) {
		super();
		if (nodeHolder == null) {
			throw new NullPointerException();
		}

		this.nodeHolder = nodeHolder;
		this.node = (EditorNode) this.nodeHolder.getValue();
		this.nodeListener = this.buildNodeListener();
		nodeHolder.addPropertyChangeListener(ValueModel.VALUE, this.nodeListener);

		this.context = context;

		this.propertiesPage = this.node.propertiesPage(null);
	}

	private PropertyChangeListener buildNodeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				AbstractEditorView.this.nodeChanged((EditorNode) e.getNewValue());
			}
		};
	}


	// ********** queries **********

	/**
	 * lazy-initialize the panel to give subclasses a chance to
	 * pass in stuff via the constructor
	 */
	Component getComponent() {
		if (this.panel == null) {
			this.panel = this.buildPanel();
			this.installNewPropertiesPage();
		}
		return this.panel;
	}

	ResourceRepository resourceRepository() {
		return this.context.getApplicationContext().getResourceRepository();
	}

    Component getPropertiesPage() {
        return this.propertiesPage;
    }

	// ********** behavior **********

	/**
	 * partially build the panel, the properties page will be added later...
	 */
	private JPanel buildPanel() {
		JPanel result = new JPanel(new BorderLayout());
		result.setBorder(BorderFactory.createEtchedBorder());
		result.setMinimumSize(new Dimension(0, 0));

		JLabel label = this.buildLabel();
		label.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, result.getBackground().brighter()),
					BorderFactory.createMatteBorder(0, 0, 1, 0, result.getBackground().darker())
				),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)
			)
		);
		label.setLabelFor(result);
		result.add(label, BorderLayout.PAGE_START);

		return result;
	}

	/**
	 * Build and return a label for the top of the view.
	 */
	abstract JLabel buildLabel();

	/**
	 * the node has changed, remove the old properties page and
	 * install the new one
	 */
	void nodeChanged(EditorNode newNode) {
		this.node.releasePropertiesPage(this.propertiesPage);
		this.node = newNode;
		Component newPropertiesPage = this.node.propertiesPage(this.context);
		// if the node returns the same properties page as what we already have,
		// do nothing; the page's contents will have been refreshed by the node
		if (newPropertiesPage != this.propertiesPage) {
			this.panel.remove(this.propertiesPage);
			this.propertiesPage = newPropertiesPage;
			this.installNewPropertiesPage();
		}
	}

	/**
	 * the properties page has changed, put it in the panel
	 */
	void installNewPropertiesPage() {
		this.panel.add(this.propertiesPage, BorderLayout.CENTER);

		// since the panel is already displayed, we need to refresh it
		if (this.panel.isVisible()) {
			this.panel.revalidate();
			this.panel.repaint();
		}
	}
	
	/**
	 * This is called when the window containing the editor is closed.
	 */
	void close() {
		this.nodeHolder.removePropertyChangeListener(ValueModel.VALUE, this.nodeListener);
	}

}
