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
package org.eclipse.persistence.tools.workbench.test.utility.classfile;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.classfile.ClassFile;


/**
 * rip through some JARs, parsing the byte codes of all
 * the contained .class files, and logging some
 * performance statistics
 */
public class ClassFileJarScanner {
	private String jarFileName;
	private int totalEntries = 0;
	private int nonClassFiles = 0;
	private float matches = 0;
	private int mismatches = 0;
	private float elapsedSeconds = 0;

	public static void main(String[] args) throws IOException {
		System.out.println("begin...");
		System.out.println();

		ClassFileJarScanner scanner = new ClassFileJarScanner(toplinkJarFileName());
		scanner.scan();
		scanner.printSummary();

		System.out.println();

		scanner = new ClassFileJarScanner(Classpath.rtJarName());
		scanner.scan();
		scanner.printSummary();

		System.out.println();
		System.out.println("...end");
	}

	public static String toplinkJarFileName() {
		return Classpath.javaClasspath().entryForFileNamed("toplink_g.jar").fileName();
	}

	public ClassFileJarScanner(String jarFileName) {
		super();
		this.jarFileName = jarFileName;
	}

	public void scan() throws IOException {
		long startTime = (new Date()).getTime();
		this.scanUntimed();
		long endTime = (new Date()).getTime();
		this.elapsedSeconds = (endTime - startTime) / 1000f;
	}

	public void scanUntimed() throws IOException {
		JarFile jarFile = new JarFile(this.jarFileName);
		try {
			this.scanJarFile(jarFile);
		} finally {
			jarFile.close();
		}
	}
	
	private void scanJarFile(JarFile jarFile) {
		for (Enumeration entries = jarFile.entries(); entries.hasMoreElements(); ) {
			this.scanJarEntry(jarFile, (JarEntry) entries.nextElement());
		}
	}
	
	private void scanJarEntry(JarFile jarFile, JarEntry jarEntry) {
		this.totalEntries++;
		String jarEntryName = jarEntry.getName();
		if (jarEntryName.endsWith(".class")) {
			this.scanStream(jarFile, jarEntry, jarEntryName);
		} else {
			this.nonClassFiles++;
		}
	}

	private void scanStream(JarFile jarFile, JarEntry jarEntry, String jarEntryName) {
		boolean validClassStream = true;
		ClassFile classFile = null;
		try {
			classFile = ClassFile.fromArchiveEntry(jarFile, jarEntry);
		} catch (IOException ex) {
			System.out.println("IOException: " + ex);
			validClassStream = false;
		}
		// look for a "synthetic" class...
//		if (classFile.isSynthetic()) {
//			System.out.println("\"synthetic\" class: " + classFile.className());
//		}
		if (validClassStream && classFile.className().equals(jarEntryName.substring(0, jarEntryName.length() - 6).replace('/', '.'))) {
			this.matches++;
		} else {
			this.mismatches++;
			System.out.println("mismatch: " + jarEntryName + " vs. " + classFile.className());
		}
	}
	
	public void printSummary() {
		System.out.println("JAR: " + this.jarFileName);
		System.out.println("totalEntries: " + this.totalEntries);
		System.out.println("matches: " + this.matches);
		System.out.println("mismatches: " + this.mismatches);
		System.out.println("non-class files: " + this.nonClassFiles);
		System.out.println("elapsed time: " + this.elapsedSeconds + " seconds");
		System.out.println("performance: " + (this.matches/this.elapsedSeconds) + " class files/second");
		System.out.println("(approx. 1000/sec?)");
	}
	
}
