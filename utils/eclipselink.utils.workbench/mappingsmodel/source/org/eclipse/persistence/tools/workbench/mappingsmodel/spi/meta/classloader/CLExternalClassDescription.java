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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classloader;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Derive most of the type's state from the class name.
 * Build the external class only on demand.
 */
final class CLExternalClassDescription
	implements ExternalClassDescription
{

	/** The name of the type. */
	private final String name;

	/** The classpath entry that contains the class file corresponding to the type. */
	private final String classpathEntry;

	/** The repository used by the external class description to find other class descriptions. */
	private final AbstractCLExternalClassRepository repository;

	/** The external class corresponding to the type. This is null until needed. */
	private ExternalClass externalClass;		// pseudo-final


	// ********** Constructors **********

	/**
	 * Package-accessible constructor.
	 */
	CLExternalClassDescription(String name, String classpathEntry, AbstractCLExternalClassRepository repository) {
		super();
		this.name = name;
		this.classpathEntry = classpathEntry;
		this.repository = repository;
	}

	/**
	 * Package-accessible constructor.
	 * Used for "array" types that do not have a classpath entry.
	 */
	CLExternalClassDescription(String name, AbstractCLExternalClassRepository repository) {
		this(name, "", repository);
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
		return this.classpathEntry;
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
	
	ExternalClassDescription classDescriptionFor(Class javaClass) {
		return this.repository.getClassDescriptionFor(javaClass);
	}


	// ********** internal methods **********
	
	/**
	 * Build an external class corresponding to the external class description.
	 */
	private ExternalClass buildExternalClass() throws ExternalClassNotFoundException {
		// external array *types* are supported; but we do *not* support external array *classes*
		if (this.getArrayDepth() != 0) {
			throw new IllegalStateException();
		}
		return new CLExternalClass(this.repository.loadClass(this.name), this);
	}

}
