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
package org.eclipse.persistence.tools.workbench.utility.classfile.tools;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.jar.JarFile;

import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.classfile.ClassFile;


/**
 * This iterator will convert all the Java .class files found on a specified
 * classpath entry (which can be either a directory or a JAR) to ClassFiles.
 */
public class ClassFileIterator
	implements Iterator
{
	/** the classpath entry */
	private File file;

	/** the JAR file corresponding to the classpath entry, if appropriate */
	private JarFile jarFile;

	/** true if the classpath entry is a .jar or .zip file */
	private boolean archive;

	/** the names of the classes found in the classpath entry */
	private Iterator classNames;


	// ********** constructor/initialization **********

	/**
	 * Construct a class file iterator on the specified classpath
	 * entry, which can be either a JAR or a directory.
	 */
	public ClassFileIterator(String classpathEntry) {
		super();
		this.file = new File(classpathEntry);
		this.archive = Classpath.fileNameIsArchive(classpathEntry);
		if (this.archive) {
			this.jarFile = this.buildJarFile();
		}
		this.classNames = this.buildClassNames(classpathEntry);
	}

	private JarFile buildJarFile() {
		try {
			return new JarFile(this.file);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected Iterator buildClassNames(String classpathEntry) {
		Classpath cp = new Classpath(new String[] {classpathEntry});
		return cp.classNamesStream();
	}


	// ********** Iterator implementation **********

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.classNames.hasNext();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		String className = (String) this.classNames.next();
		ClassFile classFile = null;
		try {
			if (this.archive) {
				classFile = this.buildClassFileFromArchive(className);
			} else {
				classFile = this.buildClassFileFromDirectory(className);
			}
		} catch (IOException ex) {
			throw new RuntimeException(className, ex);
		}
		// close the JAR if necessary
		if (this.archive && ( ! this.hasNext())) {
			this.closeJarFile();
		}
		return classFile;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}


	// ********** internal methods **********

	private ClassFile buildClassFileFromArchive(String className) throws IOException {
		return ClassFile.fromArchiveEntry(this.jarFile, className);
	}

	private ClassFile buildClassFileFromDirectory(String className) throws IOException {
		return ClassFile.fromClassFile(this.file, className);
	}

	private void closeJarFile() {
		try {
			this.jarFile.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
