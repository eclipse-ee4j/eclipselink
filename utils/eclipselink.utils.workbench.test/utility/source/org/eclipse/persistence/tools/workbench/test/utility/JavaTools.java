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
package org.eclipse.persistence.tools.workbench.test.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.NullOutputStream;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * Some tools for executing, compiling, JARing, etc.
 * Possibly obviously, this stuff might not work on all platforms. It seems
 * to work OK on Windows and Linux.
 */
public class JavaTools {

	private static final String CR = System.getProperty("line.separator");
	private static final String FS = System.getProperty("file.separator");
	private static final String JAVA_HOME = System.getProperty("java.home");
	private static final String JAVA_CLASSPATH = System.getProperty("java.class.path");
	private static final String[] EMPTY_STRING_ARRAY = new String[0];


	/**
	 * Compile the specified source file, using the current system classpath.
	 */
	public static void compile(File sourceFile) throws IOException, InterruptedException {
		compile(sourceFile, JAVA_CLASSPATH);
	}

	/**
	 * Compile the specified source file with the specified classpath.
	 * The resulting class file will be put in the same directory as the source file.
	 * Throw a RuntimeException for any non-zero exit value.
	 */
	public static void compile(File sourceFile, String classpath) throws IOException, InterruptedException {
		List cmd = new ArrayList();
		cmd.add(javaCompiler());
		if ((classpath != null) && (classpath.length() != 0)) {
			cmd.add("-classpath");
			cmd.add(classpath);
		}
		cmd.add(sourceFile.getAbsolutePath());
		exec((String[]) cmd.toArray(new String[cmd.size()]));
	}

	/**
	 * Compile all the specified source files (must be of type File),
	 * using the current system classpath.
	 */
	public static void compile(Collection sourceFiles) throws IOException, InterruptedException {
		compile(sourceFiles.iterator());
	}

	/**
	 * Compile all the specified source files (must be of type File),
	 * using the current system classpath.
	 */
	public static void compile(Collection sourceFiles, String classpath) throws IOException, InterruptedException {
		compile(sourceFiles.iterator(), classpath);
	}

	/**
	 * Compile all the specified source files (must be of type File),
	 * using the current system classpath.
	 */
	public static void compile(Iterator sourceFiles) throws IOException, InterruptedException {
		compile(sourceFiles, JAVA_CLASSPATH);
	}

	/**
	 * Compile all the specified source files (must be of type File) with
	 * the specified classpath. The resulting class files will be put in
	 * the same directories as the source files.
	 * Throw a RuntimeException for any non-zero exit value.
	 */
	public static void compile(Iterator sourceFiles, String classpath) throws IOException, InterruptedException {
		List cmd = new ArrayList();
		cmd.add(javaCompiler());
		cmd.add("-source 1.4");
		if ((classpath != null) && (classpath.length() != 0)) {
			cmd.add("-classpath");
			cmd.add(classpath);
		}
		CollectionTools.addAll(cmd, javaFileNames(sourceFiles));
		exec((String[]) cmd.toArray(new String[cmd.size()]));
	}

	/**
	 * Return the names of the files in the collection that end with .java.
	 */
	private static Iterator javaFileNames(Iterator files) {
		return new TransformationIterator(javaFiles(files)) {
			protected Object transform(Object next) {
				return ((File) next).getAbsolutePath();
			}
		};
	}

	/**
	 * Return the files in the collection whose names that end with .java.
	 */
	private static Iterator javaFiles(Iterator files) {
		return new FilteringIterator(files) {
			protected boolean accept(Object next) {
				File file = (File) next;
				return file.isFile() && file.getPath().endsWith(".java");
			}
		};
	}

	/**
	 * Add to the specified archive all the files in and below
	 * the specified directory.
	 * Throw a RuntimeException for any non-zero exit value.
	 * c = create
	 * f = file
	 */
	public static void jar(File jarFile, File directory) throws IOException, InterruptedException {
		jar("cf", jarFile, directory);
	}

	/**
	 * Add to the specified zip file all the files in and below
	 * the specified directory.
	 * Throw a RuntimeException for any non-zero exit value.
	 * c = create
	 * M = no manifest
	 * f = file
	 */
	public static void zip(File zipFile, File directory) throws IOException, InterruptedException {
		jar("cMf", zipFile, directory);
	}

	private static void jar(String jarOptions, File jarFile, File directory) throws IOException, InterruptedException {
		exec(
			new String[] {
				jarUtility(),
				jarOptions,
				jarFile.getAbsolutePath(),
				"-C",
				directory.getAbsolutePath(),
				"."
			}
		);
	}

	/**
	 * Execute the specified class's main method, using the current system classpath.
	 */
	public static void java(String className) throws IOException, InterruptedException {
		java(className, JAVA_CLASSPATH);
	}

	/**
	 * Execute the specified class's main method with the specified classpath.
	 * Throw a RuntimeException for any non-zero exit value.
	 */
	public static void java(String className, String classpath) throws IOException, InterruptedException {
		java(className, classpath, EMPTY_STRING_ARRAY);
	}

	/**
	 * Execute the specified class's main method with the specified classpath.
	 * Throw a RuntimeException for any non-zero exit value.
	 */
	public static void java(String className, String classpath, String[] args) throws IOException, InterruptedException {
		java(className, classpath, EMPTY_STRING_ARRAY, args);
	}

	/**
	 * Execute the specified class's main method with the specified classpath.
	 * Throw a RuntimeException for any non-zero exit value.
	 */
	public static void java(String className, String classpath, String[] javaOptions, String[] args) throws IOException, InterruptedException {
		List cmd = new ArrayList();
		cmd.add(javaVM());
		if ((classpath != null) && (classpath.length() != 0)) {
			cmd.add("-classpath");
			cmd.add(classpath);
		}
		CollectionTools.addAll(cmd, javaOptions);
		cmd.add(className);
		CollectionTools.addAll(cmd, args);
		exec((String[]) cmd.toArray(new String[cmd.size()]));
	}

	/**
	 * Execute the specified command line.
	 * Throw a RuntimeException for any non-zero exit value.
	 */
	public static void exec(String[] cmd) throws IOException, InterruptedException {
		// print(cmd);
		Process process = Runtime.getRuntime().exec(cmd);

		// fork a thread to consume stderr
		ByteArrayOutputStream stderrStream = new ByteArrayOutputStream(1000);
		Runnable stderrRunnable = new StreamReader(
				new BufferedInputStream(process.getErrorStream()),
				new BufferedOutputStream(stderrStream)
			);
		Thread stderrThread = new Thread(stderrRunnable, "stderr stream reader");
		stderrThread.start();

		// fork a thread to consume stdout
		ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream(1000);
		Runnable stdoutRunnable = new StreamReader(
				new BufferedInputStream(process.getInputStream()),
				new BufferedOutputStream(stdoutStream)
			);
		Thread stdoutThread = new Thread(stdoutRunnable, "stdout stream reader");
		stdoutThread.start();

		// wait for all the threads to die
		stderrThread.join();
		stdoutThread.join();
		int exitValue = process.waitFor();

		stderrStream.close();
		stdoutStream.close();

		if (!(exitValue == 0 || exitValue == 2)) {
			StringBuffer sb = new StringBuffer(2000);
			sb.append(CR);
			sb.append("**** exit value: ");
			sb.append(exitValue);
			sb.append(CR);
			sb.append("**** stderr: ");
			sb.append(CR);
			sb.append(stderrStream.toString());
			sb.append(CR);
			sb.append("**** stdout: ");
			sb.append(CR);
			sb.append(stdoutStream.toString());
			throw new RuntimeException(sb.toString());
		}
	}

	/**
	 * Return the name of the standard Java Home directory.
	 */
	public static String javaHomeDirectoryName() {
		return JAVA_HOME;
	}

	/**
	 * Return the standard Java Home directory.
	 */
	public static File javaHomeDirectory() {
		return new File(javaHomeDirectoryName());
	}
	
	/**
	 * Return the directory that holds the various Java tools
	 * (e.g. java, javac, jar).
	 */
	public static File javaToolDirectory() {
		return new File(javaHomeDirectory().getParentFile(), "bin");
	}

	/**
	 * Return the name of the directory that holds the various Java tools
	 * (e.g. java, javac, jar).
	 */
	public static String javaToolDirectoryName() {
		return javaToolDirectory().getPath();
	}

	/**
	 * Return the fully-qualified name of the Java VM.
	 */
	public static String javaVM() {
		return javaToolDirectoryName() + FS + "java";
	}

	/**
	 * Return the fully-qualified name of the Java compiler.
	 */
	public static String javaCompiler() {
		return javaToolDirectoryName() + FS + "javac";
	}

	/**
	 * Return the fully-qualified name of the JAR utility.
	 */
	public static String jarUtility() {
		return javaToolDirectoryName() + FS + "jar";
	}

	/**
	 * Print the specified "command line".
	 */
	static void print(String[] cmd) {
		for (int i = 0; i < cmd.length; i++) {
			System.out.print(cmd[i]);
			if (i + 1 < cmd.length) {
				System.out.print(" ");
			}
		}
		System.out.println();
	}

	/**
	 * Suppress default constructor, ensuring non-instantiability.
	 */
	private JavaTools() {
		super();
		throw new UnsupportedOperationException();
	}


	// ********** member class **********

	/**
	 * This class allows you to fork a thread to read an input stream
	 * asynchronously.
	 */
	private static class StreamReader implements Runnable {
		private InputStream inputStream;
		private OutputStream outputStream;

		/**
		 * Construct a stream reader that reads the specified input stream
		 * and copies its data to the specified output stream.
		 */
		StreamReader(InputStream inputStream, OutputStream outputStream) {
			super();
			this.inputStream = inputStream;
			this.outputStream = outputStream;
		}

		/**
		 * Construct a stream reader that reads the specified stream
		 * and discards the data.
		 */
		StreamReader(InputStream inputStream) {
			this(inputStream, NullOutputStream.instance());
		}

		/**
		 * @see Runnable#run()
		 */
		public void run() {
			try {
				for (int b = -1; (b = this.inputStream.read()) != -1; ) {
					this.outputStream.write(b);
				}
			} catch (IOException ex) {
				// hmmm - not sure what to do here, but this seems good enough
				throw new RuntimeException(ex);
			}
		}

		/**
		 * @see Object#toString()
		 */
		public String toString() {
			return StringTools.buildToStringFor(this);
		}

	}

}
