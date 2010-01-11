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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Consolidate the state and behavior common to the URL and
 * system external class repositories. They both maintain a collection of
 * class descriptions, keyed by name, and a class loader that can load the Java
 * classes needed by the external classes. Both of these are
 * "lazy-initialized".
 */
abstract class AbstractCLExternalClassRepository
	implements ExternalClassRepository
{

	/**
	 * The external class descriptions for this repository - keyed by name, since, with a single
	 * classloader, we can only support one class per class name.
	 */
	private Map classDescriptions;		// pseudo-final

	/**
	 * The classloader that should be able to load the classes
	 * corresponding to the class descriptions contained by this repository.
	 */
	private ClassLoader classLoader;		// pseudo-final

	/**
	 * The external array types for this repository - keyed by Java Class.
	 * This is built as the types are requested.
	 */
	private final Map arrayClassDescriptions;


	// ********** constructors **********

	/**
	 * Default constructor.
	 */
	AbstractCLExternalClassRepository() {	// private-protected
		super();
		this.arrayClassDescriptions = new HashMap();
	}


	// ********** ExternalClassRepository implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository#getExternalClassDescription(String)
	 */
	public ExternalClassDescription getClassDescription(String className) {
		// lazy initialize to postpone the suffering until required
		synchronized (this) {
			if (this.classDescriptions == null) {
				this.classDescriptions = this.buildClassDescriptions();
			}
		}
		return (ExternalClassDescription) this.classDescriptions.get(className);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository#getExternalClassDescriptions()
	 */
	public ExternalClassDescription[] getClassDescriptions() {
		// lazy initialize to postpone the suffering until required
		synchronized (this) {
			if (this.classDescriptions == null) {
				this.classDescriptions = this.buildClassDescriptions();
			}
		}
		return (ExternalClassDescription[]) this.classDescriptions.values().toArray(new ExternalClassDescription[this.classDescriptions.size()]);
	}


	// ********** package-accessible methods **********

	/**
	 * Build the master list of class descriptions, keyed by name.
	 */
	abstract Map buildClassDescriptions();	// private-protected

	/**
	 * Return the external class description that corresponds to the specified class.
	 * This is used by the various CLExternalObjects that need to resolve
	 * their references to other external class descriptions (e.g. CLExternalField has
	 * a type attribute).
	 */
	ExternalClassDescription getClassDescriptionFor(Class javaClass) {
		// this should never be called before the types have been initialized by #getExternalClassDescriptions()...
		if (javaClass.isArray()) {
			return this.getArrayClassDescriptionFor(javaClass);
		}
		// assume that, if we were able to load the specified class with a
		// URL classloader, it was discovered on the classpath and put in 'classDescriptions'
		ExternalClassDescription classDescription = (ExternalClassDescription) this.classDescriptions.get(javaClass.getName());
		if (classDescription == null) {
			// there is something wrong with our classpath...
			throw new IllegalStateException();
		}
		return classDescription;
	}

	/**
	 * Return the external [array] class description that corresponds
	 * to the specified [array] class.
	 */
	private ExternalClassDescription getArrayClassDescriptionFor(Class javaClass) {
		synchronized (this.arrayClassDescriptions) {
			ExternalClassDescription result = (ExternalClassDescription) this.arrayClassDescriptions.get(javaClass);
			if (result == null) {
				result = new CLExternalClassDescription(javaClass.getName(), this);
				this.arrayClassDescriptions.put(javaClass, result);
			}
			return result;
		}
	}

	/**
	 * Attempt to load the specified class. This is used by
	 * CLExternalClass when it is instantiated by
	 * CLExternalClassDescription.
	 */
	Class loadClass(String className) throws ExternalClassNotFoundException {
		// lazy initialize to postpone the suffering until required
		synchronized (this) {
			if (this.classLoader == null) {
				this.classLoader = this.buildClassLoader();
			}
		}
		try {
			return Class.forName(className, false, this.classLoader);
		} catch (Throwable t) {
			throw new ExternalClassNotFoundException(className, t);
		}
	}

	/**
	 * Build the class loader.
	 */
	abstract ClassLoader buildClassLoader();	// private-protected

	/**
	 * Helper method:
	 * Add all the "candidate" class descriptions found in the specified
	 * classpath to the specified collection of ExternalClassDescription objects.
	 */
	void addClassDescriptionsFromClasspathTo(Classpath classpath, Map map) {	// private-protected
		Classpath.Entry[] entries = classpath.getEntries();
		int len = entries.length;
		for (int i = 0; i < len; i++) {
			this.addClassDescriptionsFromClasspathEntryTo(entries[i], map);
		}
	}

	/**
	 * Add all the "candidate" class descriptions found in the specified
	 * classpath entry to the specified collection of ExternalClassDescriptions.
	 */
	private void addClassDescriptionsFromClasspathEntryTo(Classpath.Entry entry, Map map) {
		for (Iterator stream = entry.classNamesStream(); stream.hasNext(); ) {
			String name = (String) stream.next();
			// do *not* replace entries - the first one found takes precedence
			if ( ! map.containsKey(name)) {
				map.put(name, new CLExternalClassDescription(name, entry.canonicalFileName(), this));
			}
		}
	}

	/**
	 * Helper method:
	 * Add all the repository's class descriptions to the specified map,
	 * keyed by the class names.
	 */
	void addClassDescriptionsTo(Map map) {
		// lazy initialize to postpone the suffering until required
		synchronized (this) {
			if (this.classDescriptions == null) {
				this.classDescriptions = this.buildClassDescriptions();
			}
		}
		for (Iterator stream = this.classDescriptions.entrySet().iterator(); stream.hasNext(); ) {
			Map.Entry entry = (Map.Entry) stream.next();
			// do *not* replace entries - the first one found takes precedence
			if ( ! map.containsKey(entry.getKey())) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
	}


	// ********** standard methods **********

	public String toString() {
		String moreInfo;
		if (this.classDescriptions == null) {
			moreInfo = "uninitialized";
		} else {
			moreInfo = String.valueOf(this.classDescriptions.size()) + " types";
		}
		return StringTools.buildToStringFor(this, moreInfo);
	}

}
