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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * Interface defining Java metadata required by the
 * TopLink Mapping Workbench.
 * 
 * @see java.lang.Class
 * @see ExternalClassDescription
 */
public interface ExternalClass {
	
	/**
	 * Returns an array of ExternalClassDescription objects reflecting
	 * all the classes and interfaces declared as members
	 * of the class represented by this ExternalClass object.
	 * 
	 * @see java.lang.Class#getDeclaredClasses()
	 */
	ExternalClassDescription[] getDeclaredClasses();
	
	/**
	 * Returns an array of ExternalConstructor objects 
	 * reflecting all the constructors declared by the 
	 * class represented by this ExternalClass object.
	 * 
	 * @see java.lang.Class#getDeclaredConstructors()
	 */
	ExternalConstructor[] getDeclaredConstructors();
	
	/**
	 * Returns an array of ExternalField objects reflecting
	 * all the fields declared by the class or interface 
	 * represented by this ExternalClass object.
	 * 
	 * @see java.lang.Class#getDeclaredFields()
	 */
	ExternalField[] getDeclaredFields();
	
	/**
	 * Returns an array of ExternalMethod objects reflecting
	 * all the methods declared by the class or interface
	 * represented by this ExternalClass object.
	 * 
	 * @see java.lang.Class#getDeclaredMethods()
	 */
	ExternalMethod[] getDeclaredMethods();
	
	/**
	 * If the class or interface represented by this
	 * ExternalClass object is a member of another class,
	 * returns the ExternalClassDescription object representing
	 * the class in which it was declared.
	 * 
	 * @see java.lang.Class#getDeclaringClass()
	 */
	ExternalClassDescription getDeclaringClass();
	
	/**
	 * Determines the interfaces implemented by the
	 * class or interface represented by this object.
	 * 
	 * @see java.lang.Class#getInterfaces()
	 */
	ExternalClassDescription[] getInterfaces();
	
	/**
	 * Returns the Java language modifiers for this
	 * class or interface, encoded in an integer.
	 * 
	 * @see java.lang.Class#getModifiers()
	 * @see java.lang.reflect.Modifier
	 */
	int getModifiers();
	
	/**
	 * Returns the name of the entity (class, interface,
	 * array class, primitive type, or void) represented by
	 * this ExternalClass object, as a String.
	 * 
	 * @see java.lang.Class#getName()
	 */
	String getName();
	
	/**
	 * Returns the ExternalClassDescription representing the superclass
	 * of the entity (class, interface, primitive type or void)
	 * represented by this ExternalClass.
	 * 
	 * @see java.lang.Class#getSuperclass()
	 */
	ExternalClassDescription getSuperclass();
	
	/**
	 * Determines if the specified ExternalClass
	 * object represents an interface type.
	 * 
	 * @see java.lang.Class#isInterface()
	 */
	boolean isInterface();
	
	/**
	 * Determines if the specified ExternalClass
	 * object represents a primitive type.
	 * 
	 * @see java.lang.Class#isPrimitive()
	 */
	boolean isPrimitive();
	
}
