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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;

/**
 * Static methods to assist in testing.
 */
public class SPIMetaTestTools {

	public static ExternalField fieldNamed(ExternalClass exClass, String fieldName) {
		ExternalField[] fields = exClass.getDeclaredFields();
		for (int i = fields.length; i-- > 0; ) {
			if (fields[i].getName().equals(fieldName)) {
				return fields[i];
			}
		}
		throw new IllegalArgumentException(fieldName);
	}

	public static Field fieldNamed(Class javaClass, String fieldName) {
		Field[] fields = javaClass.getDeclaredFields();
		for (int i = fields.length; i-- > 0; ) {
			if (fields[i].getName().equals(fieldName)) {
				return fields[i];
			}
		}
		throw new IllegalArgumentException(fieldName);
	}

	public static Constructor zeroArgumentConstructor(Class javaClass) {
		return constructor(javaClass, new String[0]);
	}

	public static Constructor oneArgumentConstructor(Class javaClass, String parmTypeName) {
		return constructor(javaClass, new String[] {parmTypeName});
	}

	public static Constructor constructor(Class javaClass, String[] parmTypeNames) {
		Constructor[] ctors = javaClass.getDeclaredConstructors();
		for (int i = ctors.length; i-- > 0; ) {
			if (parmNamesMatch(ctors[i].getParameterTypes(), parmTypeNames)) {
				return ctors[i];
			}
		}
		throw new IllegalArgumentException("<init>");
	}

	public static ExternalConstructor zeroArgumentConstructor(ExternalClass exClass) {
		return constructor(exClass, new String[0]);
	}

	public static ExternalConstructor oneArgumentConstructor(ExternalClass exClass, String parmTypeName) {
		return constructor(exClass, new String[] {parmTypeName});
	}

	public static ExternalConstructor constructor(ExternalClass exClass, String[] parmTypeNames) {
		ExternalConstructor[] ctors = exClass.getDeclaredConstructors();
		for (int i = ctors.length; i-- > 0; ) {
			if (parmNamesMatch(ctors[i].getParameterTypes(), parmTypeNames)) {
				return ctors[i];
			}
		}
		throw new IllegalArgumentException("<init>");
	}

	public static Method zeroArgumentMethodNamed(Class javaClass, String methodName) {
		return methodNamed(javaClass, methodName, new String[0]);
	}

	public static Method oneArgumentMethodNamed(Class javaClass, String methodName, String parmTypeName) {
		return methodNamed(javaClass, methodName, new String[] {parmTypeName});
	}

	public static Method methodNamed(Class javaClass, String methodName, String[] parmTypeNames) {
		Method[] methods = javaClass.getDeclaredMethods();
		for (int i = methods.length; i-- > 0; ) {
			if (methods[i].getName().equals(methodName) && parmNamesMatch(methods[i].getParameterTypes(), parmTypeNames)) {
				return methods[i];
			}
		}
		throw new IllegalArgumentException(methodName);
	}

	private static boolean parmNamesMatch(Class[] parmTypes, String[] parmTypeNames) {
		if (parmTypes.length != parmTypeNames.length) {
			return false;
		}
		for (int i = parmTypes.length; i-- > 0; ) {
			if ( ! parmTypes[i].getName().equals(parmTypeNames[i])) {
				return false;
			}
		}
		return true;
	}

	public static ExternalMethod zeroArgumentMethodNamed(ExternalClass exClass, String methodName) {
		return methodNamed(exClass, methodName, new String[0]);
	}

	public static ExternalMethod oneArgumentMethodNamed(ExternalClass exClass, String methodName, String parmTypeName) {
		return methodNamed(exClass, methodName, new String[] {parmTypeName});
	}

	public static ExternalMethod methodNamed(ExternalClass exClass, String methodName, String[] parmTypeNames) {
		ExternalMethod[] methods = exClass.getDeclaredMethods();
		for (int i = methods.length; i-- > 0; ) {
			if (methods[i].getName().equals(methodName) && parmNamesMatch(methods[i].getParameterTypes(), parmTypeNames)) {
				return methods[i];
			}
		}
		throw new IllegalArgumentException(methodName);
	}

	private static boolean parmNamesMatch(ExternalClassDescription[] parmTypes, String[] parmTypeNames) {
		if (parmTypes.length != parmTypeNames.length) {
			return false;
		}
		for (int i = parmTypes.length; i-- > 0; ) {
			if ( ! parmTypes[i].getName().equals(parmTypeNames[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean compareClasses(Class[] javaClasses, ExternalClassDescription[] exTypes) {
		return CollectionTools.collection(classNames(javaClasses)).equals(CollectionTools.collection(classNames(exTypes)));
	}

	public static Iterator classNames(Class[] javaClasses) {
		return new TransformationIterator(new ArrayIterator(javaClasses)) {
			protected Object transform(Object next) {
				return ((Class) next).getName();
			}
		};
	}

	public static Iterator classNames(ExternalClassDescription[] exTypes) {
		return new TransformationIterator(new ArrayIterator(exTypes)) {
			protected Object transform(Object next) {
				return ((ExternalClassDescription) next).getName();
			}
		};
	}

	public static boolean compareMembers(Member[] javaMembers, ExternalMember[] exMembers) {
		return CollectionTools.collection(memberNames(javaMembers)).equals(CollectionTools.collection(memberNames(exMembers)));
	}

	public static Iterator memberNames(Member[] javaMembers) {
		return new TransformationIterator(new ArrayIterator(javaMembers)) {
			protected Object transform(Object next) {
				return ((Member) next).getName();
			}
		};
	}

	public static Iterator memberNames(ExternalMember[] exMembers) {
		return new TransformationIterator(new ArrayIterator(exMembers)) {
			protected Object transform(Object next) {
				return ((ExternalMember) next).getName();
			}
		};
	}

	/**
	 * Suppress default constructor, ensuring non-instantiability.
	 */
	private SPIMetaTestTools() {
		super();
		throw new UnsupportedOperationException();
	}

}
