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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This factory builds listeners that reflectively forward ChangeEvents.
 * If you are worried about having too many little classes that have to be
 * loaded and maintained by the class loader, you can use one of these.
 * Of course, this comes with the additional overhead of reflection....
 * Also note that the validity of the method name is not checked at compile
 * time, but at runtime; although we *do* check the method as soon as the
 * listener is instantiated.
 */
public abstract class ReflectiveChangeListener {

	/** the target object on which we will invoke the method */
	protected Object target;

	protected static final Class StateChangeEvent_class = StateChangeEvent.class;
	protected static final Class[] STATE_CHANGE_EVENT_CLASS_ARRAY = new Class[] {StateChangeEvent_class};
	protected static final StateChangeEvent[] EMPTY_STATE_CHANGE_EVENT_ARRAY = new StateChangeEvent[0];

	protected static final Class PropertyChangeEvent_class = PropertyChangeEvent.class;
	protected static final Class[] PROPERTY_CHANGE_EVENT_CLASS_ARRAY = new Class[] {PropertyChangeEvent_class};
	protected static final PropertyChangeEvent[] EMPTY_PROPERTY_CHANGE_EVENT_ARRAY = new PropertyChangeEvent[0];

	protected static final Class CollectionChangeEvent_class = CollectionChangeEvent.class;
	protected static final Class[] COLLECTION_CHANGE_EVENT_CLASS_ARRAY = new Class[] {CollectionChangeEvent_class};
	protected static final CollectionChangeEvent[] EMPTY_COLLECTION_CHANGE_EVENT_ARRAY = new CollectionChangeEvent[0];

	protected static final Class ListChangeEvent_class = ListChangeEvent.class;
	protected static final Class[] LIST_CHANGE_EVENT_CLASS_ARRAY = new Class[] {ListChangeEvent_class};
	protected static final ListChangeEvent[] EMPTY_LIST_CHANGE_EVENT_ARRAY = new ListChangeEvent[0];

	protected static final Class TreeChangeEvent_class = TreeChangeEvent.class;
	protected static final Class[] TREE_CHANGE_EVENT_CLASS_ARRAY = new Class[] {TreeChangeEvent_class};
	protected static final TreeChangeEvent[] EMPTY_TREE_CHANGE_EVENT_ARRAY = new TreeChangeEvent[0];



	// ********** helper methods **********

	/**
	 * Find and return a method implemented by the target that can be invoked
	 * reflectively when a change event occurs.
	 */
	private static Method findChangeListenerMethod(Object target, String methodName, Class[] eventClassArray) {
		Method method;
		try {
			method = ClassTools.method(target, methodName, eventClassArray);
		} catch (NoSuchMethodException ex1) {
			try {
				method = ClassTools.method(target, methodName);
			} catch (NoSuchMethodException ex2) {
				throw new RuntimeException(ex2);	// "checked" exceptions bite
			}
		}
		return method;
	}

	/**
	 * Check whether the specified method is suitable for being invoked when a
	 * change event has occurred. Throw an exception if it is not suitable.
	 */
	private static void checkChangeListenerMethod(Method method, Class eventClass) {
		Class[] parmTypes = method.getParameterTypes();
		int parmTypesLength = parmTypes.length;
		if (parmTypesLength == 0) {
			return;
		}
		if ((parmTypesLength == 1) && parmTypes[0].isAssignableFrom(eventClass)) {
			return;
		}
		throw new IllegalArgumentException(method.toString());
	}


	// ********** factory methods: StateChangeListener **********

	/**
	 * Construct a state change listener that will invoke the specified method
	 * on the specified target.
	 */
	public static StateChangeListener buildStateChangeListener(Object target, Method method) {
		checkChangeListenerMethod(method, StateChangeEvent_class);
		return new SingleMethodReflectiveChangeListener(target, method);
	}

	/**
	 * Construct a state change listener that will invoke the specified method
	 * on the specified target. If a single-argument method with the specified
	 * name and appropriate argument is found, it will be invoked; otherwise,
	 * a zero-argument method with the specified name will be invoked.
	 */
	public static StateChangeListener buildStateChangeListener(Object target, String methodName) {
		return buildStateChangeListener(target, findChangeListenerMethod(target, methodName, STATE_CHANGE_EVENT_CLASS_ARRAY));
	}


	// ********** factory methods: PropertyChangeListener **********

	/**
	 * Construct a property change listener that will invoke the specified method
	 * on the specified target.
	 */
	public static PropertyChangeListener buildPropertyChangeListener(Object target, Method method) {
		checkChangeListenerMethod(method, PropertyChangeEvent_class);
		return new SingleMethodReflectiveChangeListener(target, method);
	}

	/**
	 * Construct a property change listener that will invoke the specified method
	 * on the specified target. If a single-argument method with the specified
	 * name and appropriate argument is found, it will be invoked; otherwise,
	 * a zero-argument method with the specified name will be invoked.
	 */
	public static PropertyChangeListener buildPropertyChangeListener(Object target, String methodName) {
		return buildPropertyChangeListener(target, findChangeListenerMethod(target, methodName, PROPERTY_CHANGE_EVENT_CLASS_ARRAY));
	}


	// ********** factory methods: CollectionChangeListener **********

	/**
	 * Construct a collection change listener that will invoke the specified methods
	 * on the specified target.
	 */
	public static CollectionChangeListener buildCollectionChangeListener(Object target, Method addMethod, Method removeMethod, Method changeMethod) {
		checkChangeListenerMethod(addMethod, CollectionChangeEvent_class);
		checkChangeListenerMethod(removeMethod, CollectionChangeEvent_class);
		checkChangeListenerMethod(changeMethod, CollectionChangeEvent_class);
		return new MultiMethodReflectiveChangeListener(target, addMethod, removeMethod, changeMethod);
	}

	/**
	 * Construct a collection change listener that will invoke the specified method
	 * on the specified target for any change event.
	 */
	public static CollectionChangeListener buildCollectionChangeListener(Object target, Method method) {
		return buildCollectionChangeListener(target, method, method, method);
	}

	/**
	 * Construct a collection change listener that will invoke the specified methods
	 * on the specified target for change events. If a single-argument method
	 * with the specified name and appropriate argument is found, it will be invoked;
	 * otherwise, a zero-argument method with the specified name will be invoked.
	 */
	public static CollectionChangeListener buildCollectionChangeListener(Object target, String addMethodName, String removeMethodName, String changeMethodName) {
		return buildCollectionChangeListener(
				target,
				findChangeListenerMethod(target, addMethodName, COLLECTION_CHANGE_EVENT_CLASS_ARRAY),
				findChangeListenerMethod(target, removeMethodName, COLLECTION_CHANGE_EVENT_CLASS_ARRAY),
				findChangeListenerMethod(target, changeMethodName, COLLECTION_CHANGE_EVENT_CLASS_ARRAY)
		);
	}

	/**
	 * Construct a collection change listener that will invoke the specified method
	 * on the specified target for any change event. If a single-argument method
	 * with the specified name and appropriate argument is found, it will be invoked;
	 * otherwise, a zero-argument method with the specified name will be invoked.
	 */
	public static CollectionChangeListener buildCollectionChangeListener(Object target, String methodName) {
		return buildCollectionChangeListener(target, findChangeListenerMethod(target, methodName, COLLECTION_CHANGE_EVENT_CLASS_ARRAY));
	}


	// ********** factory methods: ListChangeListener **********

	/**
	 * Construct a list change listener that will invoke the specified methods
	 * on the specified target.
	 */
	public static ListChangeListener buildListChangeListener(Object target, Method addMethod, Method removeMethod, Method replaceMethod, Method changeMethod) {
		checkChangeListenerMethod(addMethod, ListChangeEvent_class);
		checkChangeListenerMethod(removeMethod, ListChangeEvent_class);
		checkChangeListenerMethod(replaceMethod, ListChangeEvent_class);
		checkChangeListenerMethod(changeMethod, ListChangeEvent_class);
		return new MultiMethodReflectiveChangeListener(target, addMethod, removeMethod, replaceMethod, changeMethod);
	}

	/**
	 * Construct a list change listener that will invoke the specified method
	 * on the specified target for any change event.
	 */
	public static ListChangeListener buildListChangeListener(Object target, Method method) {
		return buildListChangeListener(target, method, method, method, method);
	}

	/**
	 * Construct a list change listener that will invoke the specified methods
	 * on the specified target for change events. If a single-argument method
	 * with the specified name and appropriate argument is found, it will be invoked;
	 * otherwise, a zero-argument method with the specified name will be invoked.
	 */
	public static ListChangeListener buildListChangeListener(Object target, String addMethodName, String removeMethodName, String replaceMethodName, String changeMethodName) {
		return buildListChangeListener(
				target,
				findChangeListenerMethod(target, addMethodName, LIST_CHANGE_EVENT_CLASS_ARRAY),
				findChangeListenerMethod(target, removeMethodName, LIST_CHANGE_EVENT_CLASS_ARRAY),
				findChangeListenerMethod(target, replaceMethodName, LIST_CHANGE_EVENT_CLASS_ARRAY),
				findChangeListenerMethod(target, changeMethodName, LIST_CHANGE_EVENT_CLASS_ARRAY)
		);
	}

	/**
	 * Construct a list change listener that will invoke the specified method
	 * on the specified target for any change event. If a single-argument method
	 * with the specified name and appropriate argument is found, it will be invoked;
	 * otherwise, a zero-argument method with the specified name will be invoked.
	 */
	public static ListChangeListener buildListChangeListener(Object target, String methodName) {
		return buildListChangeListener(target, findChangeListenerMethod(target, methodName, LIST_CHANGE_EVENT_CLASS_ARRAY));
	}


	// ********** factory methods: TreeChangeListener **********

	/**
	 * Construct a tree change listener that will invoke the specified methods
	 * on the specified target.
	 */
	public static TreeChangeListener buildTreeChangeListener(Object target, Method addMethod, Method removeMethod, Method changeMethod) {
		checkChangeListenerMethod(addMethod, TreeChangeEvent_class);
		checkChangeListenerMethod(removeMethod, TreeChangeEvent_class);
		checkChangeListenerMethod(changeMethod, TreeChangeEvent_class);
		return new MultiMethodReflectiveChangeListener(target, addMethod, removeMethod, changeMethod);
	}

	/**
	 * Construct a tree change listener that will invoke the specified method
	 * on the specified target for any change event.
	 */
	public static TreeChangeListener buildTreeChangeListener(Object target, Method method) {
		return buildTreeChangeListener(target, method, method, method);
	}

	/**
	 * Construct a tree change listener that will invoke the specified methods
	 * on the specified target for change events. If a single-argument method
	 * with the specified name and appropriate argument is found, it will be invoked;
	 * otherwise, a zero-argument method with the specified name will be invoked.
	 */
	public static TreeChangeListener buildTreeChangeListener(Object target, String addMethodName, String removeMethodName, String changeMethodName) {
		return buildTreeChangeListener(
				target,
				findChangeListenerMethod(target, addMethodName, TREE_CHANGE_EVENT_CLASS_ARRAY),
				findChangeListenerMethod(target, removeMethodName, TREE_CHANGE_EVENT_CLASS_ARRAY),
				findChangeListenerMethod(target, changeMethodName, TREE_CHANGE_EVENT_CLASS_ARRAY)
		);
	}

	/**
	 * Construct a tree change listener that will invoke the specified method
	 * on the specified target for any change event. If a single-argument method
	 * with the specified name and appropriate argument is found, it will be invoked;
	 * otherwise, a zero-argument method with the specified name will be invoked.
	 */
	public static TreeChangeListener buildTreeChangeListener(Object target, String methodName) {
		return buildTreeChangeListener(target, findChangeListenerMethod(target, methodName, TREE_CHANGE_EVENT_CLASS_ARRAY));
	}


	// ********** constructor **********

	/**
	 * Construct a listener that will invoke the specified method
	 * on the specified target.
	 */
	protected ReflectiveChangeListener(Object target) {
		super();
		this.target = target;
	}

}
