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
package org.eclipse.persistence.tools.workbench.utility;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * TODO
 */
public class Classpath
	implements Serializable
{
	/** The entries in the classpath */
	private final Entry[] entries;

	private static final long serialVersionUID = 1L;


	// ********** static methods **********

	// ***** factory methods for "standard" classpaths *****

	/**
	 * Return the Java "boot" classpath. This includes rt.jar.
	 */
	public static Classpath bootClasspath() {
		return new Classpath(System.getProperty("sun.boot.class.path"));
	}
	
	/**
	 * Return a "virtual classpath" that contains all the jars
	 * that would be used by the Java Extension Mechanism.
	 */
	public static Classpath javaExtensionClasspath() {
		File[] dirs = javaExtensionDirectories();
		int len = dirs.length;
		List jarFileNames = new ArrayList();
		for (int i = 0; i < len; i++) {
			File dir = dirs[i];
			if (dir.isDirectory()) {
				addJarFileNamesTo(dir, jarFileNames);
			}
		}
		return new Classpath(jarFileNames);
	}

	/**
	 * Return the Java "system" classpath.
	 */
	public static Classpath javaClasspath() {
		return new Classpath(System.getProperty("java.class.path"));
	}

	/**
	 * Return the unretouched "complete" classpath.
	 * This includes the boot classpath, the Java Extension
	 * Mechanism classpath, and the normal "system" classpath.
	 */
	public static Classpath completeClasspath() {
		return new Classpath(new Classpath[] {
				bootClasspath(),
				javaExtensionClasspath(),
				javaClasspath()
		});
	}

	/**
	 * Return a classpath that contains the location of the specified class.
	 */
	public static Classpath classpathFor(Class javaClass) {
		return new Classpath(locationFor(javaClass));
	}


	// ***** file => class *****

	/**
	 * Convert a relative file name to a class name; this will work for
	 * any file that has a single extension beyond the base
	 * class name.
	 * e.g. "java/lang/String.class" is converted to "java.lang.String"
	 * e.g. "java/lang/String.java" is converted to "java.lang.String"
	 */
	public static String convertToClassName(String classFileName) {
		String className = FileTools.stripExtension(classFileName);
		// do this for archive entry names
		className = className.replace('/', '.');
		// do this for O/S-specific file names
		if (File.separatorChar != '/') {
			className = className.replace(File.separatorChar, '.');
		}
		return className;
	}

	/**
	 * Convert a file to a class name;
	 * e.g. File(java/lang/String.class) is converted to "java.lang.String"
	 */
	public static String convertToClassName(File classFile) {
		return convertToClassName(classFile.getPath());
	}

	/**
	 * Convert a relative file name to a class;
	 * e.g. "java/lang/String.class" is converted to java.lang.String.class
	 */
	public static Class convertToClass(String classFileName) throws ClassNotFoundException {
		return Class.forName(convertToClassName(classFileName));
	}

	/**
	 * Convert a relative file to a class;
	 * e.g. File(java/lang/String.class) is converted to java.lang.String.class
	 */
	public static Class convertToClass(File classFile) throws ClassNotFoundException {
		return convertToClass(classFile.getPath());
	}


	// ***** class => JAR entry *****

	/**
	 * Convert a class name to an archive entry name base;
	 * e.g. "java.lang.String" is converted to "java/lang/String"
	 */
	public static String convertToArchiveEntryNameBase(String className) {
		return className.replace('.', '/');
	}
	
	/**
	 * Convert a class to an archive entry name base;
	 * e.g. java.lang.String.class is converted to "java/lang/String"
	 */
	public static String convertToArchiveEntryNameBase(Class javaClass) {
		return convertToArchiveEntryNameBase(javaClass.getName());
	}
	
	/**
	 * Convert a class name to an archive class file entry name;
	 * e.g. "java.lang.String" is converted to "java/lang/String.class"
	 */
	public static String convertToArchiveClassFileEntryName(String className) {
		return convertToArchiveEntryNameBase(className) + ".class";
	}
	
	/**
	 * Convert a class to an archive class file entry name;
	 * e.g. java.lang.String.class is converted to "java/lang/String.class"
	 */
	public static String convertToArchiveClassFileEntryName(Class javaClass) {
		return convertToArchiveClassFileEntryName(javaClass.getName());
	}
	

	// ***** class => file (.class or .java) *****

	/**
	 * Convert a class name to a file name base for the current O/S;
	 * e.g. "java.lang.String" is converted to "java/lang/String" on Unix
	 * and "java\\lang\\String" on Windows
	 */
	public static String convertToFileNameBase(String className) {
		return className.replace('.', File.separatorChar);
	}
	
	/**
	 * Convert a class to a file name base for the current O/S;
	 * e.g. java.lang.String.class is converted to "java/lang/String" on Unix
	 * and "java\\lang\\String" on Windows
	 */
	public static String convertToFileNameBase(Class javaClass) {
		return convertToFileNameBase(javaClass.getName());
	}
	
	/**
	 * Convert a class name to a class file name for the current O/S;
	 * e.g. "java.lang.String" is converted to "java/lang/String.class" on Unix
	 * and "java\\lang\\String.class" on Windows
	 */
	public static String convertToClassFileName(String className) {
		return convertToFileNameBase(className) + ".class";
	}
	
	/**
	 * Convert a class to a class file name for the current O/S;
	 * e.g. java.lang.String.class is converted to "java/lang/String.class" on Unix
	 * and "java\\lang\\String.class" on Windows
	 */
	public static String convertToClassFileName(Class javaClass) {
		return convertToClassFileName(javaClass.getName());
	}
	
	/**
	 * Convert a class name to a class file for the current O/S;
	 * e.g. "java.lang.String" is converted to File(java/lang/String.class)
	 */
	public static File convertToClassFile(String className) {
		return new File(convertToClassFileName(className));
	}
	
	/**
	 * Convert a class to a class file for the current O/S;
	 * e.g. java.lang.String.class is converted to File(java/lang/String.class)
	 */
	public static File convertToClassFile(Class javaClass) {
		return convertToClassFile(javaClass.getName());
	}
	
	/**
	 * Convert a class name to a java file name for the current O/S;
	 * e.g. "java.lang.String" is converted to "java/lang/String.java" on Unix
	 * and "java\\lang\\String.java" on Windows
	 */
	public static String convertToJavaFileName(String className) {
		return convertToFileNameBase(className) + ".java";
	}

	/**
	 * Convert a class to a java file name for the current O/S;
	 * e.g. java.lang.String.class is converted to "java/lang/String.java" on Unix
	 * and "java\\lang\\String.java" on Windows
	 */
	public static String convertToJavaFileName(Class javaClass) {
		return convertToJavaFileName(javaClass.getName());
	}

	/**
	 * Convert a class name to a java file for the current O/S;
	 * e.g. "java.lang.String" is converted to File(java/lang/String.java)
	 */
	public static File convertToJavaFile(String className) {
		return new File(convertToJavaFileName(className));
	}

	/**
	 * Convert a class to a java file for the current O/S;
	 * e.g. java.lang.String.class is converted to File(java/lang/String.java)
	 */
	public static File convertToJavaFile(Class javaClass) {
		return convertToJavaFile(javaClass.getName());
	}


	// ***** class => resource *****

	/**
	 * Convert a class to a resource name;
	 * e.g. java.lang.String.class is converted to "/java/lang/String.class".
	 */
	public static String convertToResourceName(Class javaClass) {
		return '/' + convertToArchiveClassFileEntryName(javaClass);
	}

	/**
	 * Convert a class to a resource;
	 * e.g. java.lang.String.class is converted to
	 * URL(jar:file:/C:/jdk/1.4.2_04/jre/lib/rt.jar!/java/lang/String.class).
	 */
	public static URL convertToResource(Class javaClass) {
		return javaClass.getResource(convertToResourceName(javaClass));
	}


	// ***** utilities *****

	/**
	 * Return whether the specified file is an archive file;
	 * i.e. its name ends with ".zip" or ".jar"
	 */
	public static boolean fileNameIsArchive(String fileName) {
		String ext = FileTools.extension(fileName).toLowerCase();
		return ext.equals(".jar") || ext.equals(".zip");
	}
	
	/**
	 * Return whether the specified file is an archive file;
	 * i.e. its name ends with ".zip" or ".jar"
	 */
	public static boolean fileIsArchive(File file) {
		return fileNameIsArchive(file.getName());
	}
	
	/**
	 * Return what should be the fully-qualified file name
	 * for the JRE runtime JAR;
	 * e.g. "C:\jdk1.4.2_04\jre\lib\rt.jar".
	 */
	public static String rtJarName() {
		return locationFor(java.lang.Object.class);
	}
	
	/**
	 * Return the location from where the specified class was loaded.
	 */
	public static String locationFor(Class javaClass) {
		URL url = convertToResource(javaClass);
		String path;
		try {
			path = FileTools.buildFile(url).getPath();
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
		String protocol = url.getProtocol().toLowerCase();
		if (protocol.equals("jar")) {
			// if the class is in a JAR, the URL will look something like this:
			//     jar:file:/C:/jdk/1.4.2_04/jre/lib/rt.jar!/java/lang/String.class
			return path.substring(0, path.indexOf('!'));
		} else if (protocol.equals("file")) {
			// if the class is in a directory, the URL will look something like this:
			//     file:/C:/dev/main/mwdev/class/oracle/toplink/workbench/utility/ClasspathTools.class
			return path.substring(0, path.length() - convertToClassFileName(javaClass).length() - 1);
		}

		throw new IllegalStateException(url.toString());
	}
	
	/**
	 * Return the directories used by the Java Extension Mechanism.
	 */
	public static File[] javaExtensionDirectories() {
		return convertToFiles(javaExtensionDirectoryNames());
	}

	/**
	 * Return the directory names used by the Java Extension Mechanism.
	 */
	public static String[] javaExtensionDirectoryNames() {
		return System.getProperty("java.ext.dirs").split(File.pathSeparator);
	}


	// ***** internal *****

	private static File[] convertToFiles(String[] fileNames) {
		File[] files = new File[fileNames.length];
		for (int i = fileNames.length; i-- > 0; ) {
			files[i] = new File(fileNames[i]);
		}
		return files;
	}

	private static void addJarFileNamesTo(File dir, List jarFileNames) {
		File[] jarFiles = jarFilesIn(dir);
		int len = jarFiles.length;
		for (int i = 0; i < len; i++) {
			jarFileNames.add(FileTools.canonicalFile(jarFiles[i]).getPath());
		}
	}

	private static File[] jarFilesIn(File directory) {
		return directory.listFiles(jarFileFilter());
	}

	private static FileFilter jarFileFilter() {
		return new FileFilter() {
			public boolean accept(File file) {
				return FileTools.extension(file.getName()).toLowerCase().equals(".jar");
			}
		};
	}


	// ********** constructors **********

	/**
	 * Construct a classpath with the specified entries.
	 */
	private Classpath(Entry[] entries) {
		super();
		this.entries = entries;
	}

	/**
	 * Construct a classpath with the specified entries.
	 */
	public Classpath(String[] fileNames) {
		this(buildEntries(fileNames));
	}

	/**
	 * Skip empty file names because they will end up expanding to the current
	 * working directory, which is not what we want. Empty file names actually
	 * occur with some frequency; such as when the classpath has been built up
	 * dynamically with too many separators. For example:
	 *     "C:\dev\foo.jar;;C:\dev\bar.jar"
	 * will be parsed into three file names:
	 *     { "C:\dev\foo.jar", "", "C:\dev\bar.jar" }
	 */
	private static Entry[] buildEntries(String[] fileNames) {
		int len = fileNames.length;
		List entries = new ArrayList();
		for (int i = 0; i < len; i++) {
			String fileName = fileNames[i];
			if ((fileName != null) && (fileName.length() != 0)) {
				entries.add(new Entry(fileName));
			}
		}
		return (Entry[]) entries.toArray(new Entry[entries.size()]);
	}

	/**
	 * Construct a classpath with the specified path.
	 */
	public Classpath(String path) {
		this(path.split(File.pathSeparator));
	}

	/**
	 * Construct a classpath with the specified entries.
	 */
	public Classpath(List fileNames) {
		this((String[]) fileNames.toArray(new String[fileNames.size()]));
	}

	/**
	 * Consolidate the specified classpaths into a single classpath.
	 */
	public Classpath(Classpath[] classpaths) {
		this(consolidateEntries(classpaths));
	}

	private static Entry[] consolidateEntries(Classpath[] classpaths) {
		int len = classpaths.length;
		List entries = new ArrayList();
		for (int i = 0; i < len; i++) {
			CollectionTools.addAll(entries, classpaths[i].getEntries());
		}
		return (Entry[]) entries.toArray(new Entry[entries.size()]);
	}


	// ********** public API **********

	/**
	 * Return the classpath's entries.
	 */
	public Entry[] getEntries() {
		return this.entries;
	}

	/**
	 * Return the classpath's path.
	 */
	public String path() {
		Entry[] localEntries = this.entries;
		int max = localEntries.length - 1;
		if (max == -1) {
			return "";
		}
		StringBuffer sb = new StringBuffer(2000);
		for (int i = 0; i < max; i++) {
			sb.append(localEntries[i].fileName());
			sb.append(File.pathSeparatorChar);
		}
		sb.append(localEntries[max].fileName());
		return sb.toString();
	}

	/**
	 * Search the classpath for the specified (unqualified) file
	 * and return its entry. Return null if an entry is not found.
	 * For example, you could use this method to find the entry
	 * for "rt.jar" or "toplink.jar".
	 */
	public Entry entryForFileNamed(String shortFileName) {
		Entry[] localEntries = this.entries;
		int len = localEntries.length;
		for (int i = 0; i < len; i++) {
			if (localEntries[i].file().getName().equals(shortFileName)) {
				return localEntries[i];
			}
		}
		return null;
	}

	/**
	 * Return the first entry file in the classpath
	 * that contains the specified class.
	 * Return null if an entry is not found.
	 */
	public Entry entryForClassNamed(String className) {
		String relativeClassFileName = convertToClassFileName(className);
		String archiveEntryName = convertToArchiveClassFileEntryName(className);
		Entry[] localEntries = this.entries;
		int len = localEntries.length;
		for (int i = 0; i < len; i++) {
			if (localEntries[i].contains(relativeClassFileName, archiveEntryName)) {
				return localEntries[i];
			}
		}
		return null;
	}

	/**
	 * Return the names of all the classes discovered on the classpath,
	 * with duplicates removed.
	 */
	public String[] classNames() {
		return this.classNames(Filter.NULL_INSTANCE);
	}

	/**
	 * Return the names of all the classes discovered on the classpath
	 * and accepted by the specified filter, with duplicates removed.
	 */
	public String[] classNames(Filter filter) {
		Collection classNames = new HashSet(10000);
		this.addClassNamesTo(classNames, filter);
		return (String[]) classNames.toArray(new String[classNames.size()]);
	}

	/**
	 * Add the names of all the classes discovered on the classpath
	 * to the specified collection.
	 */
	public void addClassNamesTo(Collection classNames) {
		this.addClassNamesTo(classNames, Filter.NULL_INSTANCE);
	}

	/**
	 * Add the names of all the classes discovered on the classpath
	 * and accepted by the specified filter to the specified collection.
	 */
	public void addClassNamesTo(Collection classNames, Filter filter) {
		Entry[] localEntries = this.entries;
		int len = localEntries.length;
		for (int i = 0; i < len; i++) {
			localEntries[i].addClassNamesTo(classNames, filter);
		}
	}

	/**
	 * Return the names of all the classes discovered on the classpath.
	 * Just a bit more performant than #classNames().
	 */
	public Iterator classNamesStream() {
		return this.classNamesStream(Filter.NULL_INSTANCE);
	}

	/**
	 * Return the names of all the classes discovered on the classpath
	 * that are accepted by the specified filter.
	 * Just a bit more performant than #classNames(Filter).
	 */
	public Iterator classNamesStream(Filter filter) {
		return new CompositeIterator(this.entryClassNamesStreams(filter));
	}

	private Iterator entryClassNamesStreams(final Filter filter) {
		return new TransformationIterator(new ArrayIterator(this.entries)) {
			protected Object transform(Object next) {
				return ((Entry) next).classNamesStream(filter);
			}
		};
	}

	/**
	 * Return a "compressed" version of the classpath with its
	 * duplicate entries eliminated.
	 */
	public Classpath compressed() {
		return new Classpath((Entry[]) CollectionTools.removeDuplicateElements(this.entries));
	}

	/**
	 * Convert the classpath to an array of URLs
	 * (that can be used to instantiate a URLClassLoader).
	 */
	public URL[] urls() {
		Entry[] localEntries = this.entries;
		int len = localEntries.length;
		URL[] urls = new URL[len];
		for (int i = 0; i < len; i++) {
			urls[i] = localEntries[i].url();
		}
		return urls;
	}


	// ********** inner class **********

	/**
	 * TODO
	 */
	public static class Entry implements Serializable {
		private final String fileName;
		private final File file;
		private final File canonicalFile;

		private static final long serialVersionUID = 1L;

		Entry(String fileName) {
			super();
			if ((fileName == null) || (fileName.length() == 0)) {
				throw new IllegalArgumentException("'fileName' must be non-empty");
			}
			this.fileName = fileName;
			this.file = new File(fileName);
			this.canonicalFile = FileTools.canonicalFile(this.file);
		}

		public String fileName() {
			return this.fileName;
		}

		public File file() {
			return this.file;
		}

		public File canonicalFile() {
			return this.canonicalFile;
		}

		public String canonicalFileName() {
			return this.canonicalFile.getAbsolutePath();
		}

		public boolean equals(Object o) {
			if ( ! (o instanceof Entry)) {
				return false;
			}
			return ((Entry) o).canonicalFile.equals(this.canonicalFile);
		}

		public int hashCode() {
			return this.canonicalFile.hashCode();
		}

		/**
		 * Return the entry's "canonical" URL.
		 */
		public URL url() {
			try {
				return this.canonicalFile.toURL();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}

		/**
		 * Return whether the entry contains the specified class.
		 */
		public boolean contains(Class javaClass) {
			return this.contains(javaClass.getName());
		}

		/**
		 * Return whether the entry contains the specified class.
		 */
		public boolean contains(String className) {
			return this.contains(convertToClassFileName(className), convertToArchiveClassFileEntryName(className));
		}

		/**
		 * Return whether the entry contains either the specified relative
		 * class file or the specified archive entry.
		 * Not the prettiest signature, but it's internal....
		 */
		boolean contains(String relativeClassFileName, String archiveEntryName) {
			if ( ! this.canonicalFile.exists()) {
				return false;
			}
			if (this.canonicalFile.isDirectory() && (new File(this.canonicalFile, relativeClassFileName)).exists()) {
				return true;
			}
			return (fileIsArchive(this.canonicalFile) && this.archiveContainsEntry(archiveEntryName));
		}

		/**
		 * Return whether the entry's archive contains the specified entry.
		 */
		private boolean archiveContainsEntry(String zipEntryName) {
			ZipFile zipFile = null;
			ZipEntry zipEntry = null;
			try {
				zipFile = new ZipFile(this.canonicalFile);
				zipEntry = zipFile.getEntry(zipEntryName);
			} catch (IOException ex) {
				zipEntry = null;	// something is wrong, clear out the entry
			} finally {
				try {
					if (zipFile != null) {
						zipFile.close();
					}
				} catch (IOException ex) {
					zipEntry = null;	// something is wrong, clear out the entry
				}
			}
			return zipEntry != null;
		}

		/**
		 * Return the names of all the classes discovered in the entry.
		 */
		public String[] classNames() {
			return this.classNames(Filter.NULL_INSTANCE);
		}

		/**
		 * Return the names of all the classes discovered in the entry
		 * and accepted by the specified filter.
		 */
		public String[] classNames(Filter filter) {
			Collection classNames = new ArrayList(2000);
			this.addClassNamesTo(classNames, filter);
			return (String[]) classNames.toArray(new String[classNames.size()]);
		}

		/**
		 * Add the names of all the classes discovered in the entry
		 * to the specified collection.
		 */
		public void addClassNamesTo(Collection classNames) {
			this.addClassNamesTo(classNames, Filter.NULL_INSTANCE);
		}

		/**
		 * Add the names of all the classes discovered in the entry
		 * and accepted by the specified filter to the specified collection.
		 */
		public void addClassNamesTo(Collection classNames, Filter filter) {
			if (this.canonicalFile.exists()) {
				if (this.canonicalFile.isDirectory()) {
					this.addClassNamesForDirectoryTo(classNames, filter);
				} else if (fileIsArchive(this.canonicalFile)) {
					this.addClassNamesForArchiveTo(classNames, filter);
				}
			}
		}

		/**
		 * Add the names of all the classes discovered
		 * under the entry's directory and accepted by
		 * the specified filter to the specified collection.
		 */
		private void addClassNamesForDirectoryTo(Collection classNames, Filter filter) {
			int start = this.canonicalFile.getAbsolutePath().length() + 1;
			for (Iterator stream = this.classFilesForDirectory(); stream.hasNext(); ) {
				String className = convertToClassName(((File) stream.next()).getAbsolutePath().substring(start));
				if (filter.accept(className)) {
					classNames.add(className);
				}
			}
		}

		/**
		 * Return an iterator on all the class files discovered
		 * under the entry's directory.
		 */
		private Iterator classFilesForDirectory() {
			return new FilteringIterator(FileTools.filesInTree(this.canonicalFile)) {
				protected boolean accept(Object next) {
					return Entry.this.fileNameMightBeForClassFile(((File) next).getName());
				}
			};
		}

		/**
		 * Add the names of all the classes discovered
		 * in the entry's archive file and accepted by the
		 * specified filter to the specified collection.
		 */
		private void addClassNamesForArchiveTo(Collection classNames, Filter filter) {
			ZipFile zipFile = null;
			try {
				zipFile = new ZipFile(this.canonicalFile);
			} catch (IOException ex) {
				return;
			}
			for (Enumeration stream = zipFile.entries(); stream.hasMoreElements(); ) {
				ZipEntry zipEntry = (ZipEntry) stream.nextElement();
				String zipEntryName = zipEntry.getName();
				if (this.fileNameMightBeForClassFile(zipEntryName)) {
					String className = convertToClassName(zipEntryName);
					if (filter.accept(className)) {
						classNames.add(className);
					}
				}
			}
			try {
				zipFile.close();
			} catch (IOException ex) {
				return;
			}
		}

		/**
		 * Return whether the specified file might be a Java class file.
		 * The file name must at least end with ".class" and contain no spaces.
		 * (Neither class names nor package names may contain spaces.)
		 * Whether it actually is a class file will need to be determined by
		 * a class loader.
		 */
		boolean fileNameMightBeForClassFile(String name) {
			return FileTools.extension(name).toLowerCase().equals(".class")
					&& (name.indexOf(' ') == -1);
		}

		/**
		 * Return the names of all the classes discovered on the classpath.
		 * Just a bit more performant than #classNames().
		 */
		public Iterator classNamesStream() {
			return this.classNamesStream(Filter.NULL_INSTANCE);
		}

		/**
		 * Return the names of all the classes discovered on the classpath
		 * that are accepted by the specified filter.
		 * Just a bit more performant than #classNames(Filter).
		 */
		public Iterator classNamesStream(Filter filter) {
			if (this.canonicalFile.exists()) {
				if (this.canonicalFile.isDirectory()) {
					return this.classNamesForDirectory(filter);
				}
				if (fileIsArchive(this.canonicalFile)) {
					return this.classNamesForArchive(filter);
				}
			}
			return NullIterator.instance();
		}

		/**
		 * Return the names of all the classes discovered
		 * under the entry's directory and accepted by
		 * the specified filter.
		 */
		private Iterator classNamesForDirectory(Filter filter) {
			return new FilteringIterator(this.classNamesForDirectory(), filter);
		}

		/**
		 * Transform the class files to class names.
		 */
		private Iterator classNamesForDirectory() {
			final int start = this.canonicalFile.getAbsolutePath().length() + 1;
			return new TransformationIterator(this.classFilesForDirectory()) {
				protected Object transform(Object next) {
					return convertToClassName(((File) next).getAbsolutePath().substring(start));
				}
			};
		}

		/**
		 * Return the names of all the classes discovered
		 * in the entry's archive file and accepted by the
		 * specified filter.
		 */
		private Iterator classNamesForArchive(Filter filter) {
			// we can't simply wrap iterators here because we need to close the archive file...
			ZipFile zipFile = null;
			try {
				zipFile = new ZipFile(this.canonicalFile);
			} catch (IOException ex) {
				return NullIterator.instance();
			}
			Collection classNames = new HashSet(zipFile.size());
			for (Enumeration stream = zipFile.entries(); stream.hasMoreElements(); ) {
				ZipEntry zipEntry = (ZipEntry) stream.nextElement();
				String zipEntryName = zipEntry.getName();
				if (this.fileNameMightBeForClassFile(zipEntryName)) {
					String className = convertToClassName(zipEntryName);
					if (filter.accept(className)) {
						classNames.add(className);
					}
				}
			}
			try {
				zipFile.close();
			} catch (IOException ex) {
				return NullIterator.instance();
			}
			return classNames.iterator();
		}

	}

}
