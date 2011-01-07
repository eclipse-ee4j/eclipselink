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
package org.eclipse.persistence.tools.workbench.framework.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.EditorNode;
import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.LabelPanel;


/**
 * Each ApplicationNode must provide a PropertiesPage that will be shown in
 * the EditorView when the node is selected in the NavigatorView.
 * 
 * Most likely you will want to subclass TitledPropertiesPage,
 * TabbedPropertiesPage, or ScrollablePropertiesPage, as opposed to
 * extending this class directly.
 * 
 * @see ScrollablePropertiesPage
 * @see TabbedPropertiesPage
 * @see TitledPropertiesPage
 * @see ApplicationNode#propertiesPage(WorkbenchContext)
 * @see ApplicationNode#releasePropertiesPage(Component)
 */
public abstract class AbstractPropertiesPage
	extends AbstractPanel
{

	/**
	 * This holds the application node currently
	 * associated with the properties page.
	 */
	private PropertyValueModel nodeHolder;

	/**
	 * This holds the application node's "user object".
	 * This is typically used as the subject holder for the
	 * page's various aspect adapters.
	 */
	private PropertyValueModel selectionHolder;


	// ********** constructors/initialization **********

	/**
	 * Constructor for a "root" properties page.
	 * Border layout manager is the default.
	 */
	protected AbstractPropertiesPage(WorkbenchContext context) {
		this(new SimplePropertyValueModel(), new DefaultWorkbenchContextHolder(context));
	}

	/**
	 * Constructor for a "sub" properties page.
	 * Border layout manager is the default.
	 */
	protected AbstractPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		this(nodeHolder, contextHolder, new BorderLayout());
	}

	/**
	 * Constructor for a "sub" properties page.
	 */
	protected AbstractPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder, LayoutManager layoutManager) {
		super(layoutManager, contextHolder);
		this.initialize(nodeHolder);
		this.initializeLayout();
	}


	protected void initialize(PropertyValueModel selectionNodeHolder) {
		if (selectionNodeHolder == null) {
			throw new NullPointerException();
		}
		this.nodeHolder = selectionNodeHolder;
		this.selectionHolder = this.buildSelectionHolder();
	}

	/**
	 * Wrap the node holder with another value model that will
	 * return the "user object" held by the node.
	 */
	protected PropertyValueModel buildSelectionHolder() {
		return new TransformationPropertyValueModel(this.nodeHolder) {
			protected Object transform(Object value) {
				// this will return the "user object" corresponding to the node
				return (value == null) ? null : ((ApplicationNode) value).getValue();
			}
			protected Object reverseTransform(Object value) {
				// for now, the properties page cannot set the value of the "user object";
				// it is completely controlled by the node and framework
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Subclasses should implement this abstract method and build
	 * the appropriate components and add them to this properties page
	 * by calling the various add(Component) methods inherited from JPanel.
	 */
	protected abstract void initializeLayout();


	// ********** convenience methods **********

	/**
	 * Return a value model holding the application node in the tree
	 * currently associated with this properties page.
	 * The node may be null.
	 */
	protected PropertyValueModel getNodeHolder() {
		return this.nodeHolder;
	}

	/**
	 * Return the application node in the tree
	 * currently associated with this properties page.
	 * The node may be null.
	 */
	protected ApplicationNode getNode() {
		return (ApplicationNode) this.nodeHolder.getValue();
	}

	/**
	 * The properties page is being installed in an editor.
	 * Set the application node and workbench context
	 * currently associated with the properties page.
	 * The node and context may both be null.
	 */
	public void setNode(ApplicationNode node, WorkbenchContext context) {
		this.getDefaultWorkbenchContextHolder().setWorkbenchContext(context);
		this.nodeHolder.setValue(node);
	}

	// this can be renamed to getWorkbenchContextHolder() when we move to jdk 1.5
	private DefaultWorkbenchContextHolder getDefaultWorkbenchContextHolder() {
		return (DefaultWorkbenchContextHolder) this.getWorkbenchContextHolder();
	}

	/**
	 * Return a value model holding the "user object" associated
	 * with the application node in the tree
	 * currently associated with this properties page.
	 * The node may be null; as a result, the "user object" may
	 * also be null. This is typically used as the
	 * subject holder for the page's various aspect adapters.
	 */
	protected PropertyValueModel getSelectionHolder() {
		return this.selectionHolder;
	}

	/**
	 * Return the "user object" associated with the application node
	 * in the tree currently associated with this properties page.
	 * The node may be null; as a result, the "user object" may
	 * also be null.
	 */
	protected Object selection() {
		return this.getSelectionHolder().getValue();
	}

	/**
	 * Build an adapter that returns an icon that can be used in
	 * the properties page title label.
	 */
	protected PropertyValueModel buildPropertiesPageIconAdapter() {
		return new PropertyAspectAdapter(this.nodeHolder, EditorNode.PROPERTIES_PAGE_TITLE_ICON_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((EditorNode) this.subject).propertiesPageTitleIcon();
			}
		};
	}

	/**
	 * Build an adapter that returns a string that can be used in
	 * the properties page title label.
	 */
	protected PropertyValueModel buildPropertiesPageTitleAdapter() {
		return new PropertyAspectAdapter(this.nodeHolder, EditorNode.PROPERTIES_PAGE_TITLE_TEXT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((EditorNode) this.subject).propertiesPageTitleText();
			}
		};
	}

	protected Component buildTitlePanel() {
		JPanel titlePanel = new LabelPanel(this.buildPropertiesPageIconAdapter(), this.buildPropertiesPageTitleAdapter());
		CompoundBorder innerBorder =
			new CompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
			);
		CompoundBorder outerBorder =
			new CompoundBorder(
				BorderFactory.createEmptyBorder(2, 2, 2, 2),
				innerBorder
			);
		titlePanel.setBorder(outerBorder);
		return titlePanel;
	}

}
