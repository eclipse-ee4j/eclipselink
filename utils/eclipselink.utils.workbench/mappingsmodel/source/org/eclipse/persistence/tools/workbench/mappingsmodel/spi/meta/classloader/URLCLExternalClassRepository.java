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

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * This external class repository uses a URLClassLoader to load classes
 * on the "project" classpath.
 */
final class URLCLExternalClassRepository
	extends AbstractCLExternalClassRepository
{

	/**
	 * Cache the "project" classpath with all its entries
	 * fully qualified and duplicates stripped out, including
	 * any duplicates of entries on the "system" classpath.
	 */
	private final Classpath canonicalProjectClasspath;


	// ********** constructors **********
	
	/**
	 * Construct a class repository for the specified "project" classpath.
	 * The "system" classpath has already been determined and cannot be changed.
	 * Convert the classpath: fully-qualify the entries and eliminate any duplicates.
	 */
	URLCLExternalClassRepository(File[] classpath) {
		super();
		Classpath tempClasspath = new Classpath(this.fileNames(classpath)).compressed();
		Classpath completeClasspath = Classpath.completeClasspath();
		this.canonicalProjectClasspath = this.removeDuplicates(completeClasspath, tempClasspath);
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

	/**
	 * Remove the entries in classpath2 that are already in classpath1
	 * and return the resulting classpath.
	 */
	private Classpath removeDuplicates(Classpath classpath1, Classpath classpath2) {
		Set classpathEntries1 = CollectionTools.set(classpath1.getEntries());
		List fileNames = new ArrayList();
		Classpath.Entry[] entries2 = classpath2.getEntries();
		int len = entries2.length;
		for (int i = 0; i < len; i++) {
			Classpath.Entry entry2 = entries2[i];
			if ( ! classpathEntries1.contains(entry2)) {
				fileNames.add(entry2.fileName());
			}
		}
		return new Classpath(fileNames);
	}


	// ********** AbstractCLExternalClassRepository implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository#getExternalClassDescription(String)
	 */
	public ExternalClassDescription getClassDescription(String className) {
		// postponing suffering even more; some queries only need classes that are on the system classpath;
		// if the project classpath is large, buildClassDescriptions() in the superclass can hang...
		ExternalClassDescription ecd = SystemCLExternalClassRepository.instance().getClassDescription(className);
		if (ecd != null)
			return ecd;

		return super.getClassDescription(className);
	}

	/**
	 * @see AbstractCLExternalClassRepository#buildClassDescriptions()
	 */
	Map buildClassDescriptions() {
		Map classDescriptions = new HashMap(20000);	// let's start large
		// first add the "system" classes
		SystemCLExternalClassRepository.instance().addClassDescriptionsTo(classDescriptions);
		// then add the "project" classes
		this.addClassDescriptionsFromClasspathTo(this.canonicalProjectClasspath, classDescriptions);
		return classDescriptions;
	}

	/**
	 * @see AbstractCLExternalClassRepository#buildClassLoader()
	 */
	ClassLoader buildClassLoader() {
		return new URLClassLoader(this.canonicalProjectClasspath.urls());
	}

}
