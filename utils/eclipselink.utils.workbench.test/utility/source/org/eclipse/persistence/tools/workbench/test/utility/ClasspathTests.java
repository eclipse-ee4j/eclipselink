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
package org.eclipse.persistence.tools.workbench.test.utility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

public class ClasspathTests extends TestCase {
	private static final String JAVA_HOME = System.getProperty("java.home");
	
	public static Test suite() {
		return new TestSuite(ClasspathTests.class);
	}
	
	public ClasspathTests(String name) {
		super(name);
	}
	
	public void testCompressed() {
		String path = "";

		// no changes	
		path = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar")).compressed().path();
		assertEquals(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar"), path);
	
		path = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar")).compressed().path();
		assertEquals(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar"), path);
	
		path = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar")).compressed().path();
		assertEquals(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar"), path);
	
		path = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar;C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar")).compressed().path();
		assertEquals(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar"), path);
	
		path = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\i18n.jar;C:\\jdk\\i18n.jar;C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar")).compressed().path();
		assertEquals(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar"), path);
	
		path = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar;C:\\jdk\\rt.jar;;;;C:\\jdk\\jaws.jar;C:\\jdk\\jaws.jar;C:\\jdk\\rt.jar;;;")).compressed().path();
		assertEquals(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar"), path);
	
		// no changes
		path = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar")).compressed().path();
		assertEquals(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar"), path);
	
		path = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar")).compressed().path();
		assertEquals(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar"), path);
	
		path = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\..\\jdk\\i18n.jar;C:\\jdk\\jaws.jar")).compressed().path();
		assertEquals(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar"), path);
	
		path = new Classpath(this.morph("C:\\jdk1\\jdk2\\jdk3\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk1\\jdk2\\jdk3\\..\\..\\..\\jdk1\\jdk2\\jdk3\\i18n.jar;C:\\jdk\\jaws.jar")).compressed().path();
		assertEquals(this.morph("C:\\jdk1\\jdk2\\jdk3\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar"), path);
	
	}
	
	public void testConvertToClassName() {
		String fileName = "java/lang/String.class";
		File file = new File(fileName);
		String className = Classpath.convertToClassName(file);
		assertEquals(java.lang.String.class.getName(), className);
	}
	
	public void testConvertToClass() throws ClassNotFoundException {
		String fileName = "java/lang/String.class";
		File file = new File(fileName);
		Class javaClass = Classpath.convertToClass(file);
		assertEquals(java.lang.String.class, javaClass);
	}
	
	public void testConvertToArchiveClassFileEntryName() {
		String fileName = Classpath.convertToArchiveClassFileEntryName(java.lang.String.class);
		assertEquals("java/lang/String.class", fileName);
	}
	
	public void testConvertToClassFileName() {
		char sc = File.separatorChar;
		String fileName = Classpath.convertToClassFileName(java.lang.String.class);
		assertEquals("java" + sc + "lang" + sc + "String.class", fileName);
	}
	
	public void testConvertToJavaFileName() {
		char sc = File.separatorChar;
		String fileName = Classpath.convertToJavaFileName(java.lang.String.class.getName());
		assertEquals("java" + sc + "lang" + sc + "String.java", fileName);
	}

	public void testConvertToURLs() {
		URL[] entries = new Classpath(this.morph("C:\\jdk\\rt.jar;C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar;C:\\foo\\classes;C:\\bar\\bar.jar")).urls();
		int i = 0;
		assertEquals(this.morphURL("/C:/jdk/rt.jar"), entries[i++].getPath());
		assertEquals(this.morphURL("/C:/jdk/i18n.jar"), entries[i++].getPath());
		assertEquals(this.morphURL("/C:/jdk/jaws.jar"), entries[i++].getPath());
		assertEquals(this.morphURL("/C:/foo/classes"), entries[i++].getPath());
		assertEquals(this.morphURL("/C:/bar/bar.jar"), entries[i++].getPath());
		assertEquals(i, entries.length);
	}
	
	public void testEntries() {
		Classpath cp = new Classpath(this.morph("C:\\jdk\\rt.jar;;.;C:\\jdk\\i18n.jar;;;C:\\jdk\\jaws.jar;;C:\\foo\\classes;C:\\bar\\bar.jar;C:\\bar\\bar.jar;"));
		Classpath.Entry[] entries = cp.getEntries();
		int i = 0;
		assertEquals(this.morph("C:\\jdk\\rt.jar"), entries[i++].fileName());
		assertEquals(this.morph("."), entries[i++].fileName());
		assertEquals(this.morph("C:\\jdk\\i18n.jar"), entries[i++].fileName());
		assertEquals(this.morph("C:\\jdk\\jaws.jar"), entries[i++].fileName());
		assertEquals(this.morph("C:\\foo\\classes"), entries[i++].fileName());
		assertEquals(this.morph("C:\\bar\\bar.jar"), entries[i++].fileName());
		assertEquals(this.morph("C:\\bar\\bar.jar"), entries[i++].fileName());
		assertEquals(i, entries.length);

		cp = cp.compressed();
		entries = cp.getEntries();
		i = 0;
		assertEquals(this.morph("C:\\jdk\\rt.jar"), entries[i++].fileName());
		assertEquals(this.morph("."), entries[i++].fileName());
		assertEquals(this.morph("C:\\jdk\\i18n.jar"), entries[i++].fileName());
		assertEquals(this.morph("C:\\jdk\\jaws.jar"), entries[i++].fileName());
		assertEquals(this.morph("C:\\foo\\classes"), entries[i++].fileName());
		assertEquals(this.morph("C:\\bar\\bar.jar"), entries[i++].fileName());
		assertEquals(i, entries.length);
	}
	
	public void testEntryForFileNamed() {
		Classpath.Entry entry = null;
	
		// in the middle - qualified
		entry = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\rt.jar;C:\\jdk\\jaws.jar;C:\\foo\\classes;C:\\bar\\bar.jar")).entryForFileNamed("rt.jar");
		assertEquals(this.morph("C:\\jdk\\rt.jar"), entry.fileName());
	
		// in the middle - unqualified
		entry = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar;rt.jar;C:\\foo\\classes;C:\\bar\\bar.jar")).entryForFileNamed("rt.jar");
		assertEquals("rt.jar", entry.fileName());
	
		// at the beginning - qualified
		entry = new Classpath(this.morph("C:\\jdk\\rt.jar;C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar;C:\\foo\\classes;C:\\bar\\bar.jar")).entryForFileNamed("rt.jar");
		assertEquals(this.morph("C:\\jdk\\rt.jar"), entry.fileName());
	
		// at the beginning - unqualified
		entry = new Classpath(this.morph("rt.jar;C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar;C:\\foo\\classes;C:\\bar\\bar.jar")).entryForFileNamed("rt.jar");
		assertEquals("rt.jar", entry.fileName());
	
		// at the end - qualified
		entry = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar;C:\\foo\\classes;C:\\bar\\bar.jar;C:\\jdk\\rt.jar")).entryForFileNamed("rt.jar");
		assertEquals(this.morph("C:\\jdk\\rt.jar"), entry.fileName());
	
		// at the end - unqualified
		entry = new Classpath(this.morph("C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar;C:\\foo\\classes;C:\\bar\\bar.jar;rt.jar")).entryForFileNamed("rt.jar");
		assertEquals("rt.jar", entry.fileName());
	
		// alone - qualified
		entry = new Classpath(this.morph("C:\\jdk\\rt.jar")).entryForFileNamed("rt.jar");
		assertEquals(this.morph("C:\\jdk\\rt.jar"), entry.fileName());
	
		// alone - unqualified
		entry = new Classpath("rt.jar").entryForFileNamed("rt.jar");
		assertEquals("rt.jar", entry.fileName());
	
		// trick entry at the beginning
		entry = new Classpath(this.morph("rt.jar.new;C:\\jdk\\rt.jar;C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar;C:\\foo\\classes;C:\\bar\\bar.jar")).entryForFileNamed("rt.jar");
		assertEquals(this.morph("C:\\jdk\\rt.jar"), entry.fileName());
	
		// trick entry in the middle
		entry = new Classpath(this.morph("rt.jar.new;C:\\jdk\\rtrtrt.jar;C:\\jdk\\rt.jar;C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar;C:\\foo\\classes;C:\\bar\\bar.jar")).entryForFileNamed("rt.jar");
		assertEquals(this.morph("C:\\jdk\\rt.jar"), entry.fileName());
	
		// trick entry at the end
		entry = new Classpath(this.morph("rt.jar.new;C:\\jdk\\rtrtrt.jar;C:\\jdk\\rt.jar;C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar;C:\\foo\\classes;C:\\bar\\bar.jar;C:\\jdk\\rtrtrt.jar")).entryForFileNamed("rt.jar");
		assertEquals(this.morph("C:\\jdk\\rt.jar"), entry.fileName());
	
		// missing
		entry = new Classpath(this.morph("rt.jar.new;C:\\jdk\\rtrtrt.jar;C:\\jdk\\i18n.jar;C:\\jdk\\jaws.jar;C:\\foo\\classes;C:\\bar\\bar.jar;C:\\jdk\\rtrtrt.jar")).entryForFileNamed("rt.jar");
		assertEquals("path entry should not be found", null, entry);
	
	}
	
	public void testLocationForClass() {
		Class javaClass = Classpath.class;
		File entry = new File(Classpath.locationFor(javaClass));
		assertTrue(entry.exists());
		if (entry.isDirectory()) {
			assertTrue(new File(entry, Classpath.convertToClassFileName(javaClass)).exists());
		}
	}
	
	public void testRtJarName() throws IOException {
		File rtFile = new File(Classpath.rtJarName());
		assertTrue("rt.jar does not exist", rtFile.exists());
	
		JarFile rtJarFile = new JarFile(rtFile);
		JarEntry entry = rtJarFile.getJarEntry("java/lang/Object.class");
		rtJarFile.close();
		assertTrue("bogus rt.jar", entry != null);
	}
	
	public void testJREClassNames() {
		assertTrue("Vector is missing from JRE class names", CollectionTools.contains(Classpath.bootClasspath().classNames(), java.util.Vector.class.getName()));
		assertTrue("File is missing from JRE class names", CollectionTools.contains(Classpath.bootClasspath().classNames(), java.io.File.class.getName()));
	}
	
	public void testJavaExtensionDirectoryNames() {
		char sep = File.separatorChar;
		String stdExtDirName = JAVA_HOME + sep + "lib" + sep + "ext";
		assertTrue("standard extension dir name missing: " + stdExtDirName,
				CollectionTools.contains(Classpath.javaExtensionDirectoryNames(), stdExtDirName));
	}

	public void testJavaExtensionDirectories() {
		char sep = File.separatorChar;
		File stdExtDir = new File(JAVA_HOME + sep + "lib" + sep + "ext");
		assertTrue("standard extension dir missing: " + stdExtDir.getParent(),
				CollectionTools.contains(Classpath.javaExtensionDirectories(), stdExtDir));
	}
	
	public void testJavaExtensionClasspathEntries() {
		char sep = File.separatorChar;
		String jdk = System.getProperty("java.version");
		if (jdk.startsWith("1.4") || jdk.startsWith("1.5") || jdk.startsWith("1.6")) {
			Collection jarNames = new ArrayList();
			Classpath.Entry[] entries = Classpath.javaExtensionClasspath().getEntries();
			for (int i = 0; i < entries.length; i++) {
				jarNames.add(entries[i].fileName());
			}
			String stdExtJarName = JAVA_HOME + sep + "lib" + sep + "ext" + sep + "dnsns.jar";
			assertTrue("jdk 1.4.x standard extension jar missing: " + stdExtJarName, jarNames.contains(stdExtJarName));
		} else {
			fail("we need to update this test for the current jdk: " + jdk);
		}
	}
	
	public void testJavaExtensionClassNames() {
		String jdk = System.getProperty("java.version");
		if (jdk.startsWith("1.4") || jdk.startsWith("1.5") || jdk.startsWith("1.6")) {
			String className = "sun.net.spi.nameservice.dns.DNSNameService";
			assertTrue("jdk 1.4.x standard extension class missing: " + className,
					CollectionTools.contains(Classpath.javaExtensionClasspath().classNames(), className));
		} else {
			fail("we need to update this test for the current jdk: " + jdk);
		}
	}
	
	public void testJavaClasspathClassNames() {
		String className = this.getClass().getName();
		ClassLoader cl = this.getClass().getClassLoader();
		// make sure we are running under the "normal" class loader;
		// when the tests are executed as an ANT task, they are run under
		// an ANT class loader and the "Java" classpath does not include this class
		if (cl.getClass().getName().startsWith("sun.misc")) {
			assertTrue("class missing: " + className,
					CollectionTools.contains(Classpath.javaClasspath().classNames(), className));
		}
	}

	/**
	 * morph the specified path to a platform-independent path
	 */	
	private String morph(String path) {
		String result = path;
		result = result.replace('\\', File.separatorChar);
		result = result.replace(';', File.pathSeparatorChar);
		if ( ! CollectionTools.contains(File.listRoots(), new File("C:\\"))) {
			result = result.replaceAll("C:", "");
		}
		return result;
	}

	/**
	 * morph the specified URL to a platform-independent path
	 */	
	private String morphURL(String url) {
		String result = url;
		if ( ! CollectionTools.contains(File.listRoots(), new File("C:\\"))) {
			result = result.replaceAll("/C:", "");
		}
		return result;
	}

}
