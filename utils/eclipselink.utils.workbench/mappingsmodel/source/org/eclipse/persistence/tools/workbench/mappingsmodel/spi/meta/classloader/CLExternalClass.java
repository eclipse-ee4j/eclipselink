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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classloader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Wrap a java.lang.Class.
 */
final class CLExternalClass
	implements ExternalClass
{

	/** The wrapped Java class. */
	private final Class javaClass;

	/** The repository used to find other types. */
	private final CLExternalClassDescription classDescription;
	

	// ********** Constructors **********
	
	/**
	 * Package-accessible constructor.
	 */
	CLExternalClass(Class javaClass, CLExternalClassDescription classDescription) {
		super();
		this.javaClass = javaClass;
		this.classDescription = classDescription;
	}
	
	// ********** ExternalClass implementation **********
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getDeclaredClasses()
	 */
	public ExternalClassDescription[] getDeclaredClasses() {
		return this.buildClassDescriptionArray(this.javaClass.getDeclaredClasses());
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getDeclaredConstructors()
	 */
	public ExternalConstructor[] getDeclaredConstructors() {
		return this.buildConstructorArray(this.javaClass.getDeclaredConstructors());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getDeclaredFields()
	 */
	public ExternalField[] getDeclaredFields() {
		return this.buildFieldArray(this.javaClass.getDeclaredFields());
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getDeclaredMethods()
	 */
	public ExternalMethod[] getDeclaredMethods() {
		return this.buildMethodArray(this.javaClass.getDeclaredMethods());
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getDeclaringClass()
	 */
	public ExternalClassDescription getDeclaringClass() {
		return this.classDescriptionFor(this.javaClass.getDeclaringClass());
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getInterfaces()
	 */
	public ExternalClassDescription[] getInterfaces() {
		return this.buildClassDescriptionArray(this.javaClass.getInterfaces());
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getModifiers()
	 */
	public int getModifiers() {
		return this.javaClass.getModifiers();
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getName()
	 */
	public String getName() {
		return this.javaClass.getName();
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getSuperclass()
	 */
	public ExternalClassDescription getSuperclass() {
		return this.classDescriptionFor(this.javaClass.getSuperclass());
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#isInterface()
	 */
	public boolean isInterface() {
		return this.javaClass.isInterface();
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#isPrimitive()
	 */
	public boolean isPrimitive() {
		return this.javaClass.isPrimitive();
	}
	
	// ********** standard methods **********
	
	public String toString() {
		return StringTools.buildToStringFor(this, this.getName());
	}
	
	// ********** package-accessible methods **********

	ExternalClassDescription classDescriptionFor(Class clazz) {
		if (clazz == null) {
			return null;
		}
		return this.classDescription.classDescriptionFor(clazz);
	}

	// ********** internal methods **********
	
	private ExternalClassDescription[] buildClassDescriptionArray(Class[] classes) {
		ExternalClassDescription[] classDescriptions = new ExternalClassDescription[classes.length];
		for (int i = classes.length; i-- > 0; ) {
			classDescriptions[i] = this.classDescriptionFor(classes[i]);
		}
		return classDescriptions;
	}
	
	private ExternalConstructor[] buildConstructorArray(Constructor[] constructors) {
		ExternalConstructor[] externalConstructors = new ExternalConstructor[constructors.length];
		for (int i = constructors.length; i-- > 0; ) {
			externalConstructors[i] = new CLExternalConstructor(constructors[i], this);
		}
		return externalConstructors;
	}

	private ExternalField[] buildFieldArray(Field[] fields) {
		ExternalField[] externalFields = new ExternalField[fields.length];
		for (int i = fields.length; i-- > 0; ) {
			externalFields[i] = new CLExternalField(fields[i], this);
		}
		return externalFields;
	}

	private ExternalMethod[] buildMethodArray(Method[] methods) {
		ExternalMethod[] externalMethods = new ExternalMethod[methods.length];
		for (int i = methods.length; i-- > 0; ) {
			externalMethods[i] = new CLExternalMethod(methods[i], this);
		}
		return externalMethods;
	}

}
