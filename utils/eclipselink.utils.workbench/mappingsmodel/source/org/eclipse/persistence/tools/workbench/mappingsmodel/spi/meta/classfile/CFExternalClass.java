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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;
import org.eclipse.persistence.tools.workbench.utility.classfile.ClassFile;
import org.eclipse.persistence.tools.workbench.utility.classfile.Field;
import org.eclipse.persistence.tools.workbench.utility.classfile.Method;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Wrap a ClassFile.
 */
final class CFExternalClass
	implements ExternalClass
{

	/** The wrapped class file. */
	private final ClassFile classFile;

	/** The repository used to find other types. */
	private final CFExternalClassDescription classDescription;


	// ********** Constructors **********

	/**
	 * Package-accessible constructor.
	 */
	CFExternalClass(ClassFile classFile, CFExternalClassDescription classDescription) {
		super();
		this.classFile = classFile;
		this.classDescription = classDescription;
	}


	// ********** ExternalClass implementation **********
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getDeclaredClasses()
	 */
	public ExternalClassDescription[] getDeclaredClasses() {
		return this.buildClassDescriptionArray(this.classFile.declaredMemberClassNames());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getDeclaredConstructors()
	 */
	public ExternalConstructor[] getDeclaredConstructors() {
		return this.buildConstructorArray(this.classFile.getMethodPool().declaredConstructors());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getDeclaredFields()
	 */
	public ExternalField[] getDeclaredFields() {
		return this.buildFieldArray(this.classFile.getFieldPool().declaredFields());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getDeclaredMethods()
	 */
	public ExternalMethod[] getDeclaredMethods() {
		return this.buildMethodArray(this.classFile.getMethodPool().declaredMethods());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getDeclaringClass()
	 */
	public ExternalClassDescription getDeclaringClass() {
		return this.classDescriptionNamed(this.classFile.declaringClassName());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getInterfaces()
	 */
	public ExternalClassDescription[] getInterfaces() {
		return this.buildClassDescriptionArray(this.classFile.interfaceNames());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getModifiers()
	 */
	public int getModifiers() {
		return this.classFile.standardAccessFlags();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getName()
	 */
	public String getName() {
		return this.classFile.className();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#getSuperclass()
	 */
	public ExternalClassDescription getSuperclass() {
		return this.classDescriptionNamed(this.classFile.superClassName());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#isInterface()
	 */
	public boolean isInterface() {
		return this.classFile.isInterface();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass#isPrimitive()
	 */
	public boolean isPrimitive() {
		return false;
	}


	// ********** standard methods **********

	public String toString() {
		return StringTools.buildToStringFor(this, this.getName());
	}


	// ********** package-accessible methods **********

	ExternalClassDescription classDescriptionNamed(String className) {
		if (className == null) {
			return null;
		}
		return this.classDescription.classDescriptionNamed(className);
	}


	// ********** internal methods **********

	private ExternalClassDescription[] buildClassDescriptionArray(String[] classNames) {
		ExternalClassDescription[] classDescriptions = new ExternalClassDescription[classNames.length];
		for (int i = classNames.length; i-- > 0; ) {
			classDescriptions[i] = this.classDescriptionNamed(classNames[i]);
		}
		return classDescriptions;
	}

	private ExternalConstructor[] buildConstructorArray(Method[] constructors) {
		ExternalConstructor[] externalConstructors = new ExternalConstructor[constructors.length];
		for (int i = constructors.length; i-- > 0; ) {
			externalConstructors[i] = new CFExternalConstructor(constructors[i], this);
		}
		return externalConstructors;
	}

	private ExternalField[] buildFieldArray(Field[] fields) {
		ExternalField[] externalFields = new ExternalField[fields.length];
		for (int i = fields.length; i-- > 0; ) {
			externalFields[i] = new CFExternalField(fields[i], this);
		}
		return externalFields;
	}

	private ExternalMethod[] buildMethodArray(Method[] methods) {
		ExternalMethod[] externalMethods = new ExternalMethod[methods.length];
		for (int i = methods.length; i-- > 0; ) {
			externalMethods[i] = new CFExternalMethod(methods[i], this);
		}
		return externalMethods;
	}

}
