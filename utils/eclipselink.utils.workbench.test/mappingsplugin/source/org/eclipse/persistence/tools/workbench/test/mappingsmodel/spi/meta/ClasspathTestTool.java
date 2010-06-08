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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.spi.meta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.eclipse.persistence.tools.workbench.test.utility.JavaTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This tool can be used to create a two versions of the same class that
 * can be used to test class loading. Create the files by calling #setUp()
 * and delete them by calling #tearDown(). The various files and directories
 * are stored in public instance variables. The names used for these are
 * stored in public static variables.
 * 
 * The different versions will be compiled into:
 * 	- two different subdirectories (subdir1 and subdir2)
 * 	- two different jar files (jarFile1 and jarFile2)
 * 	- two different zip files (zipFile1 and zipFile2)
 * 
 * The two versions of the class will differ by the presence of a single
 * method: the version in subdir1/jarFile1/zipFile1 will have a single
 * method named #version1(); while, predictably, the version in
 * subdir2/jarFile2/zipFile2 will have a single method named #version2();
 * and the presence of a single attribute, similarly named.
 */
public class ClasspathTestTool {

	public String name;
	public File workingDirectory;

	public File subdir1;
	public File subdir2;

	public File jarFile1;
	public File jarFile2;

	public File zipFile1;
	public File zipFile2;

	public static final String PKG_QUALIFIER_1 = "foo";
	public static final String PKG_QUALIFIER_2 = "bar";
	public static final String PKG_NAME = PKG_QUALIFIER_1 + "." + PKG_QUALIFIER_2;
	public static final String SHORT_TEST_CLASS_NAME = ClassTools.shortNameFor(ClasspathTestTool.class) + "TestClass";
	public static final String TEST_CLASS_NAME = PKG_NAME + "." + SHORT_TEST_CLASS_NAME;
	public static final String VERSION_MEMBER_PREFIX = "version";


	public ClasspathTestTool(String name) {
		super();
		this.name = name;
	}

	public void setUp() throws Exception {
		this.workingDirectory = this.buildWorkingDirectory(this.name);
	}
	
	public void tearDown() throws Exception {
		FileTools.deleteDirectory(this.workingDirectory);
	}
	
	/**
	 * build a temporary directory with two different versions
	 * of a Java source file; then compile them and package
	 * them into jars and zips
	 */
	private File buildWorkingDirectory(String dirName) throws Exception {
		File dir = FileTools.emptyTemporaryDirectory(dirName);
	
		this.subdir1 = this.buildSourceSubdir(dir, 1);
		this.jarFile1 = buildJarFile(this.subdir1, 1);
		this.zipFile1 = buildZipFile(this.subdir1, 1);
	
		this.subdir2 = this.buildSourceSubdir(dir, 2);
		this.jarFile2 = buildJarFile(this.subdir2, 2);
		this.zipFile2 = buildZipFile(this.subdir2, 2);
	
		return dir;
	}
	
	/**
	 * build a subdirectory "foo\bar" with the .java and .class
	 * files for the class ClassPathEntryTestsTestClass
	 */
	private File buildSourceSubdir(File parentDir, int version) throws Exception {
		File subdir = new File(parentDir, "subdir" + version);
		subdir.mkdir();
		File fooDir = new File(subdir, PKG_QUALIFIER_1);
		fooDir.mkdir();
		File barDir = new File(fooDir, PKG_QUALIFIER_2);
		barDir.mkdir();
	
		File sourceFile = new File(barDir, SHORT_TEST_CLASS_NAME + ".java");
		IndentingPrintWriter pw = new IndentingPrintWriter(new FileWriter(sourceFile));
		this.printSourceOn(pw, version);
		pw.close();
	
		this.compile(sourceFile);
	
		return subdir;
	}
	
	private void printSourceOn(IndentingPrintWriter pw, int version) {
		printSourceOn(pw, version, this.buildSourceExtender());
	}
	
	/**
	 * subclasses can override this method if they want to add
	 * to the source code generated for testing
	 */
	protected SourceExtender buildSourceExtender() {
		return buildNullSourceExtender();
	}
	
	/**
	 * build an archive of the specified subdirectory
	 */
	private File buildJarFile(File subdir, int version) throws Exception {
		String jarFileName = subdir.getParentFile().getName() + version + ".jar";
		File jarFile = new File(subdir.getParentFile(), jarFileName);
		this.jar(jarFile, subdir);
		return jarFile;
	}
	
	/**
	 * build a zip of the specified subdirectory
	 */
	private File buildZipFile(File subdir, int version) throws Exception {
		String zipFileName = subdir.getParentFile().getName() + version + ".zip";
		File zipFile = new File(subdir.getParentFile(), zipFileName);
		this.zip(zipFile, subdir);
		return zipFile;
	}
	
	private void compile(File sourceFile) throws IOException, InterruptedException {
		JavaTools.compile(sourceFile);
	}
	
	private void jar(File jarFile, File directory) throws IOException, InterruptedException {
		JavaTools.jar(jarFile, directory);
	}
	
	private void zip(File zipFile, File directory) throws IOException, InterruptedException {
		JavaTools.zip(zipFile, directory);
	}
	
	public static SourceExtender buildNullSourceExtender() {
		return new SourceExtender() {
			public void extendSourceOn(IndentingPrintWriter pw, int version) {
				// do nothing
			}
		};
	}

	private static void printSourceOn(IndentingPrintWriter pw, int version, SourceExtender extender) {
		pw.print("package ");
		pw.print(PKG_NAME);
		pw.println(';');
	
		pw.print("public class ");
		pw.print(SHORT_TEST_CLASS_NAME);
		pw.println(" {");
		pw.indent();

			pw.print("public java.lang.String ");
			pw.print(VERSION_MEMBER_PREFIX);
			pw.print(version);
			pw.println(" = \"version" + version + "\";");

			pw.print("public void ");
			pw.print(VERSION_MEMBER_PREFIX);
			pw.print(version);
			pw.println("() {");

			pw.indent();
				pw.println("System.out.println(\"version" + version + "\");");
			pw.undent();
			pw.println("}");
		
			extender.extendSourceOn(pw, version);
		
		pw.undent();
		pw.println("}");
	}

	/**
	 * use this method to check the formatting of the source file
	 */
	public static String buildSource(int version) {
		StringWriter sw = new StringWriter(200);
		IndentingPrintWriter pw = new IndentingPrintWriter(sw);
		pw.println();
		pw.println();
		printSourceOn(pw, version, buildNullSourceExtender());
		pw.println();
		return sw.toString();
	}

public static interface SourceExtender {
	void extendSourceOn(IndentingPrintWriter pw, int version);
}

}
