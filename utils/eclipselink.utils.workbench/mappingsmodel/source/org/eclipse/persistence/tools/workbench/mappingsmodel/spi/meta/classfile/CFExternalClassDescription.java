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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile;

import java.io.File;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.ClassFile;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Derive most of the type's state from the class name.
 * Build the external class only on demand.
 */
final class CFExternalClassDescription
	implements ExternalClassDescription
{

	/** The name of the type. */
	private final String name;

	/** The classpath entry that contains the class file corresponding to the type. */
	private final File classpathEntry;

	/** The repository used by the description to find other descriptions. */
	private final CFExternalClassRepository repository;

	/** The external class corresponding to the description. This is null until needed. */
	private ExternalClass externalClass;		// pseudo-final


	// ********** Constructors **********

	/**
	 * Package-accessible constructor.
	 */
	CFExternalClassDescription(String name, String classpathEntry, CFExternalClassRepository repository) {
		super();
		this.name = name;
		this.classpathEntry = new File(classpathEntry);
		this.repository = repository;
	}

	/**
	 * Package-accessible constructor.
	 * Used for referenced types and "array" types that
	 * do not have a classpath entry and should not ever
	 * build an external class.
	 */
	CFExternalClassDescription(String name, CFExternalClassRepository repository) {
		super();
		this.name = name;
		this.classpathEntry = null;
		this.repository = repository;
	}


	// ********** ExternalClassDescription implementation **********
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription#getArrayDepth()
	 */
	public int getArrayDepth() {
		return ClassTools.arrayDepthForClassNamed(this.name);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription#getAdditionalInfo()
	 */
	public String getAdditionalInfo() {
		return this.classpathEntry.getPath();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription#getElementTypeName()
	 */
	public String getElementTypeName() {
		return ClassTools.elementTypeNameForClassNamed(this.name);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription#getExternalClass()
	 */
	public synchronized ExternalClass getExternalClass() throws ExternalClassNotFoundException {
		if (this.externalClass == null) {
			this.externalClass = this.buildExternalClass();
		}
		return this.externalClass;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription#getName()
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription#isSynthetic()
	 */
	public boolean isSynthetic() {
		return ClassTools.classNamedIsAnonymous(this.name);
	}


	// ********** standard methods **********

	public String toString() {
		return StringTools.buildToStringFor(this, this.getName());
	}


	// ********** package-accessible methods **********
	
	ExternalClassDescription classDescriptionNamed(String className) {
		return this.repository.getClassDescriptionNamed(className);
	}


	// ********** internal methods **********

	/**
	 * Build an external class corresponding to the external class description.
	 */
	private ExternalClass buildExternalClass() throws ExternalClassNotFoundException {
		// external array *class descriptions* are supported; but we do *not* support external array *classes*
		if (this.classpathEntry == null) {
			throw new IllegalStateException();
		}
		try {
			return new CFExternalClass(ClassFile.forClass(this.classpathEntry, this.name), this);
		} catch (Throwable t) {
			throw new ExternalClassNotFoundException(this.name, t);
		}
	}

}
