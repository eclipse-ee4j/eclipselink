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
package org.eclipse.persistence.tools.workbench.utility.events;

import java.lang.reflect.Method;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This class is used by ReflectiveChangeListener when the requested listener
 * needs to implement multiple methods (i.e. CollectionChangeListener,
 * ListChangeListener, or TreeChangeListener).
 */
class MultiMethodReflectiveChangeListener
	extends ReflectiveChangeListener 
	implements CollectionChangeListener, ListChangeListener, TreeChangeListener
{
	/** the methods we will invoke on the target object */
	private Method addMethod;
	private Method removeMethod;
	private Method replaceMethod;	// this can be null
	private Method changeMethod;


	/**
	 * The "replace" method is optional.
	 */
	MultiMethodReflectiveChangeListener(Object target, Method addMethod, Method removeMethod, Method replaceMethod, Method changeMethod) {
		super(target);
		this.addMethod = addMethod;
		this.removeMethod = removeMethod;
		this.replaceMethod = replaceMethod;
		this.changeMethod = changeMethod;
	}

	/**
	 * No "replace" method.
	 */
	MultiMethodReflectiveChangeListener(Object target, Method addMethod, Method removeMethod, Method changeMethod) {
		this(target, addMethod, removeMethod, null, changeMethod);
	}


	// ********** CollectionChangeListener implementation **********

	private void invoke(Method method, CollectionChangeEvent e) {
		if (method.getParameterTypes().length == 0) {
			ClassTools.invokeMethod(method, this.target, EMPTY_COLLECTION_CHANGE_EVENT_ARRAY);
		} else {
			ClassTools.invokeMethod(method, this.target, new CollectionChangeEvent[] {e});
		}
	}

	public void itemsAdded(CollectionChangeEvent e) {
		this.invoke(this.addMethod, e);
	}

	public void itemsRemoved(CollectionChangeEvent e) {
		this.invoke(this.removeMethod, e);
	}

	public void collectionChanged(CollectionChangeEvent e) {
		this.invoke(this.changeMethod, e);
	}


	// ********** ListChangeListener implementation **********

	private void invoke(Method method, ListChangeEvent e) {
		if (method.getParameterTypes().length == 0) {
			ClassTools.invokeMethod(method, this.target, EMPTY_LIST_CHANGE_EVENT_ARRAY);
		} else {
			ClassTools.invokeMethod(method, this.target, new ListChangeEvent[] {e});
		}
	}

	public void itemsAdded(ListChangeEvent e) {
		this.invoke(this.addMethod, e);
	}

	public void itemsRemoved(ListChangeEvent e) {
		this.invoke(this.removeMethod, e);
	}

	public void itemsReplaced(ListChangeEvent e) {
		this.invoke(this.replaceMethod, e);
	}

	public void listChanged(ListChangeEvent e) {
		this.invoke(this.changeMethod, e);
	}


	// ********** TreeChangeListener implementation **********

	private void invoke(Method method, TreeChangeEvent e) {
		if (method.getParameterTypes().length == 0) {
			ClassTools.invokeMethod(method, this.target, EMPTY_TREE_CHANGE_EVENT_ARRAY);
		} else {
			ClassTools.invokeMethod(method, this.target, new TreeChangeEvent[] {e});
		}
	}

	public void nodeAdded(TreeChangeEvent e) {
		this.invoke(this.addMethod, e);
	}

	public void nodeRemoved(TreeChangeEvent e) {
		this.invoke(this.removeMethod, e);
	}

	public void treeChanged(TreeChangeEvent e) {
		this.invoke(this.changeMethod, e);
	}

}
