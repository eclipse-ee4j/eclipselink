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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This external class repository maintains a collection of class descriptions that
 * can generate external classes from Java class files.
 */
final class CFExternalClassRepository
	implements ExternalClassRepository
{

	/**
	 * Cache the "project" classpath with all its entries
	 * fully qualified and duplicates stripped out.
	 */
	private final Classpath classpath;

	/**
	 * The external class descriptions for this repository.
	 */
	private ExternalClassDescription[] classDescriptions;		// pseudo-final

	/**
	 * The "default" external class descriptions for this repository - keyed by name.
	 * The "default" class descriptions are the class descriptions returned when
	 * you request class descriptions by name.
	 * Typically we will return the first class description found on the
	 * classpath with the requested name. This should not be a
	 * problem since the important class descriptions are those that end up building
	 * external *classes* and those should be chosen explicitly, not
	 * requested by name.
	 */
	private Map defaultClassDescriptions;		// pseudo-final

	/**
	 * The "stub", non-array external class descriptions for this repository - keyed by name.
	 * This will hold the non-array class descriptions that
	 * are referenced by the external *classes* that were
	 * not included on the classpath.
	 * This is built as the class descriptions are requested.
	 */
	private final Map stubClassDescriptions;

	/**
	 * The external "array" class descriptions for this repository - keyed by name.
	 * This is built as the class descriptions are requested.
	 */
	private final Map arrayClassDescriptions;


	// ********** constructors **********

	/**
	 * Construct a class repository for the specified "project" classpath.
	 */
	CFExternalClassRepository(File[] classpath) {
		super();
		this.stubClassDescriptions = new HashMap();
		this.arrayClassDescriptions = new HashMap();
		this.classpath = new Classpath(this.fileNames(classpath)).compressed();
	}

	/**
	 * Return an array of the names of the specified files.
	 */
	private String[] fileNames(File[] files) {
		int len = files.length;
		String[] fileNames = new String[len];
		for (int i = 0; i < len; i++) {
			fileNames[i] = files[i].getAbsolutePath();
		}
		return fileNames;
	}


	// ********** ExternalClassRepository implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository#getClassDescription(String)
	 */
	public ExternalClassDescription getClassDescription(String typeName) {
		// lazy initialize to postpone the suffering until required
		synchronized (this) {
			if (this.classDescriptions == null) {
				this.initializeClassDescriptions();
			}
		}
		return (ExternalClassDescription) this.defaultClassDescriptions.get(typeName);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository#getClassDescriptions()
	 */
	public ExternalClassDescription[] getClassDescriptions() {
		// lazy initialize to postpone the suffering until required
		synchronized (this) {
			if (this.classDescriptions == null) {
				this.initializeClassDescriptions();
			}
		}
		return this.classDescriptions;
	}

	private static final int STARTING_SIZE = 20000;		// let's start large

	/**
	 * Build the master lists of class descriptions.
	 */
	private void initializeClassDescriptions() {
		List list = new ArrayList(STARTING_SIZE);
		this.defaultClassDescriptions = new HashMap(STARTING_SIZE);

		// first add all the primitives, since they do not show up on any classpath
		for (int i = PRIMITIVE_EXTERNAL_CLASS_DESCRIPTIONS.length; i-- > 0; ) {
			ExternalClassDescription classDescription = PRIMITIVE_EXTERNAL_CLASS_DESCRIPTIONS[i];
			list.add(classDescription);
			this.defaultClassDescriptions.put(classDescription.getName(), classDescription);
		}

		Classpath.Entry[] entries = this.classpath.getEntries();
		int len = entries.length;
		for (int i = 0; i < len; i++) {
			this.addClassDescriptionsFromClasspathEntryTo(entries[i], list, this.defaultClassDescriptions);
		}

		this.classDescriptions = (ExternalClassDescription[]) list.toArray(new ExternalClassDescription[list.size()]);
	}

	/**
	 * Add all the "candidate" class descriptions found in the specified
	 * classpath entry to the collections of ExternalClassDescriptions.
	 */
	private void addClassDescriptionsFromClasspathEntryTo(Classpath.Entry entry, List list, Map map) {
		for (Iterator stream = entry.classNamesStream(); stream.hasNext(); ) {
			String name = (String) stream.next();
			ExternalClassDescription classDescription = new CFExternalClassDescription(name, entry.canonicalFileName(), this);
			list.add(classDescription);
			// do *not* replace entries - the first one found takes precedence
			if ( ! map.containsKey(name)) {
				map.put(name, classDescription);
			}
		}
	}


	// ********** package-accessible methods **********

	/**
	 * Return the external class description that corresponds to the specified class.
	 * This is used by the various CFExternalObjects that need to resolve
	 * their references to other external class descriptions (e.g. CFExternalField has
	 * a type attribute).
	 */
	ExternalClassDescription getClassDescriptionNamed(String className) {
		// this should never be called before the types have been initialized by #getExternalClassDescriptions()...
		if (ClassTools.classNamedIsArray(className)) {
			return this.getArrayClassDescriptionNamed(className);
		}

		// if the requested class description was not on the classpath, put a "shell" class description in 'defaultClassDescriptions'
		ExternalClassDescription classDescription = (ExternalClassDescription) this.defaultClassDescriptions.get(className);
		if (classDescription == null) {
			classDescription = this.getStubClassDescriptionNamed(className);
		}
		return classDescription;
	}

	/**
	 * Return the "stub" external class description that corresponds
	 * to the specified class.
	 */
	private ExternalClassDescription getStubClassDescriptionNamed(String className) {
		synchronized (this.stubClassDescriptions) {
			ExternalClassDescription stubClassDescription = (ExternalClassDescription) this.stubClassDescriptions.get(className);
			if (stubClassDescription == null) {
				stubClassDescription = new CFExternalClassDescription(className, this);
				this.stubClassDescriptions.put(className, stubClassDescription);
			}
			return stubClassDescription;
		}
	}

	/**
	 * Return the external [array] class description that corresponds
	 * to the specified [array] class.
	 */
	private ExternalClassDescription getArrayClassDescriptionNamed(String className) {
		synchronized (this.arrayClassDescriptions) {
			ExternalClassDescription arrayClassDescription = (ExternalClassDescription) this.arrayClassDescriptions.get(className);
			if (arrayClassDescription == null) {
				arrayClassDescription = new CFExternalClassDescription(className, this);
				this.arrayClassDescriptions.put(className, arrayClassDescription);
			}
			return arrayClassDescription;
		}
	}


	// ********** standard methods **********

	public String toString() {
		String moreInfo;
		if (this.classDescriptions == null) {
			moreInfo = "uninitialized";
		} else {
			moreInfo = String.valueOf(this.classDescriptions.length) + " types";
		}
		return StringTools.buildToStringFor(this, moreInfo);
	}

}
