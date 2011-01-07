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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;


/**
 * This extension of PropertyAdapter provides TreeChange support.
 * 
 * The typical subclass will override the following methods:
 * #getValueFromSubject()
 *     at the very minimum, override this method to return an iterator
 *     on the subject's tree aspect; it does not need to be overridden if
 *     #getValue() is overridden and its behavior changed
 * #addNode(Object[], Object) and #removeNode(Object[])
 *     override these methods if the client code needs to *change* the contents of
 *     the subject's tree aspect; oftentimes, though, the client code
 *     (e.g. UI) will need only to *get* the value
 * #getValue()
 *     override this method only if returning an empty iterator when the
 *     subject is null is unacceptable
 */
public abstract class TreeAspectAdapter
	extends AspectAdapter
	implements TreeValueModel
{
	/**
	 * The name of the subject's tree that we use for the value.
	 */
	protected String treeName;

	/** A listener that listens to the subject's tree aspect. */
	protected TreeChangeListener treeChangeListener;


	// ********** constructors **********

	/**
	 * Construct a TreeAspectAdapter for the specified subject
	 * and tree.
	 */
	protected TreeAspectAdapter(String treeName, Model subject) {
		super(subject);
		this.treeName = treeName;
	}

	/**
	 * Construct a TreeAspectAdapter for the specified subject holder
	 * and tree.
	 */
	protected TreeAspectAdapter(ValueModel subjectHolder, String treeName) {
		super(subjectHolder);
		this.treeName = treeName;
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.treeChangeListener = this.buildTreeChangeListener();
	}

	/**
	 * The subject's tree aspect has changed, notify the listeners.
	 */
	protected TreeChangeListener buildTreeChangeListener() {
		// transform the subject's tree change events into VALUE tree change events
		return new TreeChangeListener() {
			public void nodeAdded(TreeChangeEvent e) {
				TreeAspectAdapter.this.nodeAdded(e);
			}
			public void nodeRemoved(TreeChangeEvent e) {
				TreeAspectAdapter.this.nodeRemoved(e);
			}
			public void treeChanged(TreeChangeEvent e) {
				TreeAspectAdapter.this.treeChanged(e);
			}
			public String toString() {
				return "tree change listener: " + TreeAspectAdapter.this.treeName;
			}
		};
	}


	// ********** ValueModel implementation **********

	/**
	 * Return the value of the subject's tree aspect.
	 * This should be an *iterator* on the tree.
	 * @see ValueModel#getValue()
	 */
	public Object getValue() {
		if (this.subject == null) {
			return NullIterator.instance();
		}
		return this.getValueFromSubject();
	}

	/**
	 * Return the value of the subject's tree aspect.
	 * This should be an *iterator* on the tree.
	 * At this point we can be sure that the subject is not null.
	 * @see ValueModel#getValue()
	 */
	protected Iterator getValueFromSubject() {	// private-protected
		throw new UnsupportedOperationException();
	}


	// ********** TreeValueModel implementation **********

	/**
	 * Insert the specified node in the subject's tree aspect.
	 * @see TreeValueModel#addNode(Object[])
	 */
	public void addNode(Object[] parentPath, Object node) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Remove the specified node from the subject's tree aspect.
	 * @see TreeValueModel#removeNode(Object[])
	 */
	public void removeNode(Object[] path) {
		throw new UnsupportedOperationException();
	}


	// ********** AspectAdapter implementation **********

	/**
	 * @see AspectAdapter#hasListeners()
	 */
	protected boolean hasListeners() {
		return this.hasAnyTreeChangeListeners(VALUE);
	}

	/**
	 * @see AspectAdapter#fireAspectChange(Object, Object)
	 */
	protected void fireAspectChange(Object oldValue, Object newValue) {
		this.fireTreeStructureChanged(VALUE);
	}

	/**
	 * @see AspectAdapter#engageNonNullSubject()
	 */
	protected void engageNonNullSubject() {
		((Model) this.subject).addTreeChangeListener(this.treeName, this.treeChangeListener);
	}

	/**
	 * @see AspectAdapter#disengageNonNullSubject()
	 */
	protected void disengageNonNullSubject() {
		((Model) this.subject).removeTreeChangeListener(this.treeName, this.treeChangeListener);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		sb.append(this.treeName);
	}


	// ********** behavior **********

	protected void nodeAdded(TreeChangeEvent e) {
		this.fireNodeAdded(VALUE, e.getPath());
	}

	protected void nodeRemoved(TreeChangeEvent e) {
		this.fireNodeRemoved(VALUE, e.getPath());
	}

	protected void treeChanged(TreeChangeEvent e) {
		this.fireTreeStructureChanged(VALUE, e.getPath());
	}

}
