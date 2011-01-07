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
package org.eclipse.persistence.tools.workbench.utility.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.string.XMLStringEncoder;


/**
 * Assorted file tools:
 * - delete entire trees of directories and files
 * - build iterators on entire trees of directories and files
 * - build a temporary directory
 * - "canonize" files
 */
public final class FileTools {

	public static final String USER_HOME_DIRECTORY_NAME = System.getProperty("user.home");
	public static final String USER_TEMPORARY_DIRECTORY_NAME = System.getProperty("java.io.tmpdir");
	public static String DEFAULT_TEMPORARY_DIRECTORY_NAME = "tmpdir";
	public static final String CURRENT_WORKING_DIRECTORY_NAME = System.getProperty("user.dir");

    /** A list of some invalid file name characters.
				: is the filename separator in MacOS and the drive indicator in DOS
				* is a DOS wildcard character
				| is a DOS redirection character
				& is our own escape character
				/ is the filename separator in Unix and the command option tag in DOS
				\ is the filename separator in DOS/Windows and the escape character in Unix
				; is ???
				? is a DOS wildcard character
				[ is ???
				] is ???
				= is ???
				+ is ???
				< is a DOS redirection character
				> is a DOS redirection character
				" is used by DOS to delimit file names with spaces
				, is ???
     */
	public static final char[] INVALID_FILENAME_CHARACTERS = { ':', '*', '|', '&', '/', '\\', ';', '?', '[', ']', '=', '+', '<', '>', '"', ',' };

	/** This encoder will convert strings into valid file names. */
	public static final XMLStringEncoder FILE_NAME_ENCODER = new XMLStringEncoder(INVALID_FILENAME_CHARACTERS);

	/** Windows files that are redirected to devices etc. */
	private static final String[] WINDOWS_RESERVED_FILE_NAMES = {
		"con",
		"aux",
		"com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8", "com9",
		"lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9",
		"prn",
		"nul"
	};

	/** The default length of a shortened file name. */
	public static final int MAXIMUM_SHORTENED_FILE_NAME_LENGTH = 60;


	// ********** deleting directories **********

	/**
	 * Delete the specified directory and all of its contents.
	 * <em>USE WITH CARE.</em>
	 * File#deleteAll()?
	 */
	public static void deleteDirectory(String directoryName) {
		deleteDirectory(new File(directoryName));
	}
	
	/**
	 * Delete the specified directory and all of its contents.
	 * <em>USE WITH CARE.</em>
	 * File#deleteAll()?
	 */
	public static void deleteDirectory(File directory) {
		deleteDirectoryContents(directory);
		if ( ! directory.delete()) {
			throw new RuntimeException("unable to delete directory: " + directory.getAbsolutePath());
		}
	}
	
	/**
	 * Delete the contents of the specified directory
	 * (but not the directory itself).
	 * <em>USE WITH CARE.</em>
	 * File#deleteFiles()
	 */
	public static void deleteDirectoryContents(String directoryName) {
		deleteDirectoryContents(new File(directoryName));
	}
	
	/**
	 * Delete the contents of the specified directory
	 * (but not the directory itself).
	 * <em>USE WITH CARE.</em>
	 * File#deleteFiles()
	 */
	public static void deleteDirectoryContents(File directory) {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				deleteDirectory(file);	// recurse through subdirectories
			} else {
				if ( ! file.delete()) {
					throw new RuntimeException("unable to delete file: " + file.getAbsolutePath());
				}
			}
		}
	}
	

	// ********** copying files **********

	/**
	 * Copies the content of the source file to the destination file.
	 * File#copy(File destinationFile)
	 */
	public static void copyToFile(File sourceFile, File destinationFile)
		throws IOException
	{
		FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel();
		FileChannel destinationChannel = new FileOutputStream(destinationFile).getChannel();
		try {
			destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} finally {
			sourceChannel.close();
			destinationChannel.close();
		}
	}
	
	/**
	 * Copies the content of the source file to a file by
	 * the same name in the destination directory.
	 * File#copyToDirectory(File destinationDirectory)
	 */
	public static void copyToDirectory(File sourceFile, File destinationDirectory)
		throws IOException
	{
		File destinationFile = new File(destinationDirectory, sourceFile.getName());
		destinationFile.createNewFile();
		copyToFile(sourceFile, destinationFile);
	}
	

	// ********** iteratoring over files and directories **********

	/**
	 * Return an iterator on all the files in the specified directory.
	 * The iterator will skip over subdirectories.
	 * File#files()
	 */
	public static Iterator filesIn(String directoryName) {
		return filesIn(new File(directoryName));
	}
	
	/**
	 * Return an iterator on all the files in the specified directory.
	 * The iterator will skip over subdirectories.
	 * File#files()
	 */
	public static Iterator filesIn(File directory) {
		return filesIn(directory.listFiles());
	}
	
	private static Iterator filesIn(File[] files) {
		return new FilteringIterator(new ArrayIterator(files)) {
			protected boolean accept(Object next) {
				return ((File) next).isFile();
			}
		};
	}
	
	/**
	 * Return an iterator on all the subdirectories
	 * in the specified directory.
	 * File#subDirectories()
	 */
	public static Iterator directoriesIn(String directoryName) {
		return directoriesIn(new File(directoryName));
	}
	
	/**
	 * Return an iterator on all the subdirectories
	 * in the specified directory.
	 * File#subDirectories()
	 */
	public static Iterator directoriesIn(File directory) {
		return directoriesIn(directory.listFiles());
	}
	
	private static Iterator directoriesIn(File[] files) {
		return new FilteringIterator(new ArrayIterator(files)) {
			protected boolean accept(Object next) {
				return ((File) next).isDirectory();
			}
		};
	}
	
	/**
	 * Return an iterator on all the files under the specified
	 * directory, recursing into subdirectories.
	 * The iterator will skip over the subdirectories themselves.
	 * File#filesRecurse()
	 */
	public static Iterator filesInTree(String directoryName) {
		return filesInTree(new File(directoryName));
	}
	
	/**
	 * Return an iterator on all the files under the specified
	 * directory, recursing into subdirectories.
	 * The iterator will skip over the subdirectories themselves.
	 * File#filesRecurse()
	 */
	public static Iterator filesInTree(File directory) {
		return filesInTreeAsSet(directory).iterator();
	}

	private static Set filesInTreeAsSet(File directory) {
		Set files = new HashSet(10000);
		addFilesInTreeTo(directory, files);
		return files;
	}

	private static void addFilesInTreeTo(File directory, Collection allFiles) {
		File[] files = directory.listFiles();
		for (int i = files.length; i-- > 0; ) {
			File file = files[i];
			if (file.isFile()) {
				allFiles.add(file);
			} else if (file.isDirectory()) {
				addFilesInTreeTo(file, allFiles);
			}
		}
	}

	/**
	 * Return an iterator on all the directories under the specified
	 * directory, recursing into subdirectories.
	 * File#subDirectoriesRecurse()
	 */
	public static Iterator directoriesInTree(String directoryName) {
		return directoriesInTree(new File(directoryName));
	}
	
	/**
	 * Return an iterator on all the directories under the specified
	 * directory, recursing into subdirectories.
	 * File#subDirectoriesRecurse()
	 */
	public static Iterator directoriesInTree(File directory) {
		File[] files = directory.listFiles();
		return new CompositeIterator(directoriesIn(files), directoriesInTrees(directoriesIn(files)));
	}
	
	private static Iterator directoriesInTrees(Iterator directories) {
		return new CompositeIterator(
			new TransformationIterator(directories) {
				protected Object transform(Object next) {
					return FileTools.directoriesInTree((File) next);
				}
			}
		);
	}
	

	// ********** short file name manipulation **********

	/**
	 * Strip the extension from the specified file name
	 * and return the result. If the file name has no
	 * extension, it is returned unchanged
	 * File#basePath()
	 */
	public static String stripExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) {
			return fileName;
		}
		return fileName.substring(0, index);
	}
	
	/**
	 * Strip the extension from the specified file's name
	 * and return the result. If the file's name has no
	 * extension, it is returned unchanged
	 * File#basePath()
	 */
	public static String stripExtension(File file) {
		return stripExtension(file.getPath());
	}

	/**
	 * Return the extension, including the dot, of the specified file name.
	 * If the file name has no extension, return an empty string.
	 * File#extension()
	 */
	public static String extension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) {
			return "";
		}
		return fileName.substring(index);
	}
	
	/**
	 * Return the extension, including the dot, of the specified file's name.
	 * If the file's name has no extension, return an empty string.
	 * File#extension()
	 */
	public static String extension(File file) {
		return extension(file.getPath());
	}


	// ********** temporary directories **********

	/**
	 * Build and return an empty temporary directory with the specified
	 * name. If the directory already exists, it will be cleared out.
	 * This directory will be a subdirectory of the Java temporary directory,
	 * as indicated by the System property "java.io.tmpdir".
	 */
	public static File emptyTemporaryDirectory(String name) {
		File dir = new File(userTemporaryDirectory(), name);
		if (dir.exists()) {
			deleteDirectoryContents(dir);
		} else {
			dir.mkdirs();
		}
		return dir;
	}
	
	/**
	 * Build and return an empty temporary directory with a
	 * name of "tmpdir". If the directory already exists, it will be cleared out.
	 * This directory will be a subdirectory of the Java temporary directory,
	 * as indicated by the System property "java.io.tmpdir".
	 */
	public static File emptyTemporaryDirectory() {
		return emptyTemporaryDirectory(DEFAULT_TEMPORARY_DIRECTORY_NAME);
	}
	
	/**
	 * Build and return a temporary directory with the specified
	 * name. If the directory already exists, it will be left unchanged;
	 * if it does not already exist, it will be created.
	 * This directory will be a subdirectory of the Java temporary directory,
	 * as indicated by the System property "java.io.tmpdir".
	 */
	public static File temporaryDirectory(String name) {
		File dir = new File(userTemporaryDirectory(), name);
		if ( ! dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
	
	/**
	 * Build and return a temporary directory with a name of
	 * "tmpdir". If the directory already exists, it will be left unchanged;
	 * if it does not already exist, it will be created.
	 * This directory will be a subdirectory of the Java temporary directory,
	 * as indicated by the System property "java.io.tmpdir".
	 */
	public static File temporaryDirectory() {
		return temporaryDirectory(DEFAULT_TEMPORARY_DIRECTORY_NAME);
	}
	
	/**
	 * Build and return a *new* temporary directory with the specified
	 * prefix. The prefix will be appended with a number that
	 * is incremented, starting with 1, until a non-pre-existing directory
	 * is found and successfully created. This directory will be a
	 * subdirectory of the Java temporary directory, as indicated by
	 * the System property "java.io.tmpdir".
	 */
	public static File newTemporaryDirectory(String prefix) {
		if ( ! prefix.endsWith(".")) {
			prefix = prefix + ".";
		}
		File dir;
		int i = 0;
		do {
			i++;
			dir = new File(userTemporaryDirectory(), prefix + i);
		} while ( ! dir.mkdirs());
		return dir;
	}
	
	/**
	 * Build and return a *new* temporary directory with a
	 * prefix of "tmpdir". This prefix will be appended with a number that
	 * is incremented, starting with 1, until a non-pre-existing directory
	 * is found and successfully created. This directory will be a
	 * subdirectory of the Java temporary directory, as indicated by
	 * the System property "java.io.tmpdir".
	 */
	public static File newTemporaryDirectory() {
		return newTemporaryDirectory(DEFAULT_TEMPORARY_DIRECTORY_NAME);
	}
	

	// ********** resource files **********

	/**
	 * Build and return a file for the specified resource.
	 * The resource name must be fully-qualified, i.e. it cannot be relative
	 * to the package name/directory.
	 * NB: There is a bug in jdk1.4.x the prevents us from getting
	 * a resource that has spaces (or other special characters) in
	 * its name.... (see Sun's Java bug 4466485)
	 */
	public static File resourceFile(String resourceName) throws URISyntaxException {
		if ( ! resourceName.startsWith("/")) {
			throw new IllegalArgumentException(resourceName);
		}
		return resourceFile(resourceName, FileTools.class);
	}
	
	/**
	 * Build and return a file for the specified resource.
	 * NB: There is a bug in jdk1.4.x the prevents us from getting
	 * a resource that has spaces (or other special characters) in
	 * its name.... (see Sun's Java bug 4466485)
	 */
	public static File resourceFile(String resourceName, Class javaClass) throws URISyntaxException {
		URL url = javaClass.getResource(resourceName);
		return buildFile(url);
	}
	
	/**
	 * Build and return a file for the specified URL.
	 * NB: There is a bug in jdk1.4.x the prevents us from getting
	 * a resource that has spaces (or other special characters) in
	 * its name.... (see Sun's Java bug 4466485)
	 */
	public static File buildFile(URL url) throws URISyntaxException {
		return buildFile(url.getFile());
	}
	
	/**
	 * Build and return a file for the specified file name.
	 * NB: There is a bug in jdk1.4.x the prevents us from getting
	 * a resource that has spaces (or other special characters) in
	 * its name.... (see Sun's Java bug 4466485)
	 */
	public static File buildFile(String fileName) throws URISyntaxException {
		URI uri = new URI(fileName);
		File file = new File(uri.getPath());
		return file;
	}
	

	// ********** "canonical" files **********

	/**
	 * Convert the specified file into a "canonical" file.
	 */
	public static File canonicalFile(File file) {
		try {
			return file.getCanonicalFile();
		} catch (IOException ioexception) {
			// settle for the absolute file
			return file.getAbsoluteFile();
		}
	}
	
	/**
	 * Build an iterator that will convert the specified files
	 * into "canonical" files.
	 */
	public static Iterator canonicalFiles(Iterator files) {
		return new TransformationIterator(files) {
			protected Object transform(Object next) {
				return canonicalFile((File) next);
			}
		};
	}
	
	/**
	 * Build an iterator that will convert the specified files
	 * into "canonical" files.
	 */
	public static Iterator canonicalFiles(Collection files) {
		return canonicalFiles(files.iterator());
	}
	
	/**
	 * Convert the specified file name into a "canonical" file name.
	 */
	public static String canonicalFileName(String fileName) {
		return canonicalFile(new File(fileName)).getAbsolutePath();
	}
	
	/**
	 * Build an iterator that will convert the specified file names
	 * into "canonical" file names.
	 */
	public static Iterator canonicalFileNames(Iterator fileNames) {
		return new TransformationIterator(fileNames) {
			protected Object transform(Object next) {
				return canonicalFileName((String) next);
			}
		};
	}
	
	/**
	 * Build an iterator that will convert the specified file names
	 * into "canonical" file names.
	 */
	public static Iterator canonicalFileNames(Collection fileNames) {
		return canonicalFileNames(fileNames.iterator());
	}
	

	// ********** file name validation **********

	/**
	 * Return whether the specified file name is invalid.
	 */
	public static boolean fileNameIsInvalid(String filename) {
		return ! fileNameIsValid(filename);
	}

	/**
	 * Return whether the specified file name is valid.
	 */
	public static boolean fileNameIsValid(String filename) {
		int len = filename.length();
		for (int i = 0; i < len; i++) {
			char filenameChar = filename.charAt(i);
			if (CollectionTools.contains(INVALID_FILENAME_CHARACTERS, filenameChar)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Convert the illegal characters in the specified file name to
	 * the specified character and return the result.
	 */
	public static String convertToValidFileName(String filename, char replacementChar) {
		int len = filename.length();
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			char filenameChar = filename.charAt(i);
			if (CollectionTools.contains(INVALID_FILENAME_CHARACTERS, filenameChar)) {
				sb.append(replacementChar);
			} else {
				sb.append(filenameChar);
			}
		}
		return sb.toString();
	}

	/**
	 * Convert the illegal characters in the specified file name to
	 * periods ('.') and return the result.
	 */
	public static String convertToValidFileName(String filename) {
		return convertToValidFileName(filename, '.');
	}

	/**
	 * Return whether the specified file name is "reserved"
	 * (i.e. it cannot be used for "user" files). Windows reserves
	 * a number of file names (e.g. CON, AUX, PRN).
	 */
	public static boolean fileNameIsReserved(String fileName) {
		if (executingOnWindows()) {
			return CollectionTools.contains(WINDOWS_RESERVED_FILE_NAMES, fileName.toLowerCase());
		}
		return false;	// Unix does not have any "reserved" file names (I think...)
	}

	/**
	 * Return whether the specified file contains any "reserved"
	 * components.
	 * Windows reserves a number of file names (e.g. CON, AUX, PRN);
	 * and these file names cannot be used for either the names of
	 * files or directories.
	 */
	public static boolean fileHasAnyReservedComponents(File file) {
		File temp = file;
		while (temp != null) {
			if (fileNameIsReserved(temp.getName())) {
				return true;
			}
			temp = temp.getParentFile();
		}
		return false;
	}


	// ********** shortened file names **********

	/**
	 * Return a shorter version of the absolute file name for the specified file.
	 * The shorter version will not be longer than the maximum length.
	 * The first directory (usually the drive letter) and the file name or the
	 * last directory will always be added to the generated string regardless of
	 * the maximum length allowed.
	 */
	public static String shortenFileName(URL url) {
		return shortenFileName(url, MAXIMUM_SHORTENED_FILE_NAME_LENGTH);
	}

	/**
	 * Return a shorter version of the absolute file name for the specified file.
	 * The shorter version will not be longer than the maximum length.
	 * The first directory (usually the drive letter) and the file name or the
	 * last directory will always be added to the generated string regardless of
	 * the maximum length allowed.
	 */
	public static String shortenFileName(URL url, int maxLength) {
		File file;
		try {
			file = buildFile(url);
		} catch (URISyntaxException e) {
			file = new File(url.getFile());
		}
		return shortenFileName(file, maxLength);
	}

	/**
	 * Return a shorter version of the absolute file name for the specified file.
	 * The shorter version will not be longer than the maximum length.
	 * The first directory (usually the drive letter) and the file name or the
	 * last directory will always be added to the generated string regardless of
	 * the maximum length allowed.
	 */
	public static String shortenFileName(File file) {
		return shortenFileName(file, MAXIMUM_SHORTENED_FILE_NAME_LENGTH);
	}

	/**
	 * Return a shorter version of the absolute file name for the specified file.
	 * The shorter version will not be longer than the maximum length.
	 * The first directory (usually the drive letter) and the file name or the
	 * last directory will always be added to the generated string regardless of
	 * the maximum length allowed.
	 */
	public static String shortenFileName(File file, int maxLength) {
		String absoluteFileName = canonicalFile(file).getAbsolutePath();
		if (absoluteFileName.length() <= maxLength) {
			// no need to shorten
			return absoluteFileName;
		}

		// break down the path into its components
		String fs = File.separator;
		String[] paths = absoluteFileName.split("\\" + fs);

		if (paths.length <= 1) {
			// e.g. "C:\"
			return paths[0];
		}

		if (paths.length == 2) {
			// e.g. "C:\MyReallyLongFileName.ext" or "C:\MyReallyLongDirectoryName"
			// return the complete file name since this is a minimum requirement,
			// regardless of the maximum length allowed
			return absoluteFileName;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(paths[0]);		// always add the first directory, which is usually the drive letter

		// Keep the index of insertion into the string buffer
		int insertIndex = sb.length();

		sb.append(fs);
		sb.append(paths[paths.length - 1]);		// append the file name or the last directory

		maxLength -= 4;                      // -4 for "/..."

		int currentLength = sb.length() - 4; // -4 for "/..."
		int leftIndex = 1;                   //  1 to skip the root directory
		int rightIndex = paths.length - 2;   // -1 for the file name or the last directory

		boolean canAddFromLeft = true;
		boolean canAddFromRight = true;

		// Add each directory, the insertion is going in both direction: left and
		// right, once a side can't be added, the other side is still continuing
		// until both can't add anymore
		while (true) {
			if (!canAddFromLeft && !canAddFromRight)
				break;

			if (canAddFromRight) {
				String rightDirectory = paths[rightIndex];
				int rightLength = rightDirectory.length();

				// Add the directory on the right side of the loop
				if (currentLength + rightLength + 1 <= maxLength) {
					sb.insert(insertIndex,     fs);
					sb.insert(insertIndex + 1, rightDirectory);

					currentLength += rightLength + 1;
					rightIndex--;

					// The right side is now overlapping the left side, that means
					// we can't add from the right side anymore
					if (leftIndex >= rightIndex) {
						canAddFromRight = false;
					}
				} else {
					canAddFromRight = false;
				}
			}

			if (canAddFromLeft) {
				String leftDirectory = paths[leftIndex];
				int leftLength = leftDirectory.length();

				// Add the directory on the left side of the loop
				if (currentLength + leftLength + 1 <= maxLength) {
					sb.insert(insertIndex,     fs);
					sb.insert(insertIndex + 1, leftDirectory);

					insertIndex += leftLength + 1;
					currentLength += leftLength + 1;
					leftIndex++;

					// The left side is now overlapping the right side, that means
					// we can't add from the left side anymore
					if (leftIndex >= rightIndex) {
						canAddFromLeft = false;
					}
				} else {
					canAddFromLeft = false;
				}
			}
		}

		if (leftIndex <= rightIndex) {
			sb.insert(insertIndex, fs);
			sb.insert(insertIndex + 1, "...");
		}

		return sb.toString();
	}


	// ********** system properties **********

	/**
	 * Return a file representing the user's home directory.
	 */
	public static File userHomeDirectory() {
		return new File(USER_HOME_DIRECTORY_NAME);
	}
	
	/**
	 * Return a file representing the user's temporary directory.
	 */
	public static File userTemporaryDirectory() {
		return new File(USER_TEMPORARY_DIRECTORY_NAME);
	}
	
	/**
	 * Return a file representing the current working directory.
	 */
	public static File currentWorkingDirectory() {
		return new File(CURRENT_WORKING_DIRECTORY_NAME);
	}
	

	// ********** miscellaneous **********

	private static boolean executingOnWindows() {
		return executingOn("Windows");
	}

//	private static boolean executingOnLinux() {
//		return executingOn("Linux");
//	}
//
	private static boolean executingOn(String osName) {
		return System.getProperty("os.name").indexOf(osName) != -1;
	}

	/**
	 * Return only the files that fit the filter.
	 * File#files(FileFilter fileFilter)
	 */
	public static Iterator filter(Iterator files, final FileFilter fileFilter) {
		return new FilteringIterator(files) {
			protected boolean accept(Object next) {
				return fileFilter.accept((File) next);
			}
		};
	}

	/**
	 * Return a file that is a re-specification of the specified
	 * file, relative to the specified directory.
	 *     Linux/Unix/Mac:
	 *         convertToRelativeFile(/foo/bar/baz.java, /foo)
	 *             => bar/baz.java
	 *     Windows:
	 *         convertToRelativeFile(C:\foo\bar\baz.java, C:\foo)
	 *             => bar/baz.java
	 * The file can be either a file or a directory; the directory
	 * *should* be a directory.
	 * If the file is already relative or it cannot be made relative
	 * to the directory, it will be returned unchanged.
	 * 
	 * NB: This method has been tested on Windows and Linux,
	 * but not Mac (but the Mac is Unix-based these days, so
	 * it shouldn't be a problem...).
	 */
	public static File convertToRelativeFile(final File file, final File dir) {
		// check whether the file is already relative
		if ( ! file.isAbsolute()) {
			return file;		// return unchanged
		}

		File cFile = canonicalFile(file);
		File cDir = canonicalFile(dir);

		// the two are the same directory
		if (cFile.equals(cDir)) {
			return new File(".");
		}

		File[] filePathFiles = pathFiles(cFile);
		File[] dirPathFiles = pathFiles(cDir);

		// Windows only (?): the roots are different - e.g. D:\ vs. C:\
		if ( ! dirPathFiles[0].equals(filePathFiles[0])) {
			return file;		// return unchanged
		}

		// at this point we know the root is the same, now find how much is in common
		int i = 0;		// this will point at the first miscompare
		while ((i < dirPathFiles.length) && (i < filePathFiles.length)) {
			if (dirPathFiles[i].equals(filePathFiles[i])) {
				i++;
			} else {
				break;
			}
		}
		// save our current position
		int firstMismatch = i;

		// check whether the file is ABOVE the directory: ../..
		if (firstMismatch == filePathFiles.length) {
			return relativeParentFile(dirPathFiles.length - firstMismatch);
		}

		// build a new file from the path beyond the matching portions
		File diff = new File(filePathFiles[i].getName());
		while (++i < filePathFiles.length) {
			diff = new File(diff, filePathFiles[i].getName());
		}

		// check whether the file is BELOW the directory: subdir1/subdir2/file.ext
		if (firstMismatch == dirPathFiles.length) {
			return diff;
		}

		// the file must be a PEER of the directory: ../../subdir1/subdir2/file.ext
		return new File(relativeParentFile(dirPathFiles.length - firstMismatch), diff.getPath());
	}

	/**
	 * Return a file that is a re-specification of the specified
	 * file, relative to the current working directory.
	 *     Linux/Unix/Mac (CWD = /foo):
	 *         convertToRelativeFile(/foo/bar/baz.java)
	 *             => bar/baz.java
	 *     Windows (CWD = C:\foo):
	 *         convertToRelativeFile(C:\foo\bar\baz.java)
	 *             => bar/baz.java
	 * The file can be either a file or a directory.
	 * If the file is already relative or it cannot be made relative
	 * to the directory, it will be returned unchanged.
	 * 
	 * NB: This method has been tested on Windows and Linux,
	 * but not Mac (but the Mac is Unix-based these days, so
	 * it shouldn't be a problem...).
	 */
	public static File convertToRelativeFile(final File file) {
		return convertToRelativeFile(file, currentWorkingDirectory());
	}

	/**
	 * Return an array of files representing the path to the specified
	 * file. For example:
	 *     C:/foo/bar/baz.txt =>
	 *     { C:/, C:/foo, C:/foo/bar, C:/foo/bar/baz.txt }
	 */
	private static File[] pathFiles(File file) {
		List path = new ArrayList();
		for (File f = file; f != null; f = f.getParentFile()) {
			path.add(f);
		}
		Collections.reverse(path);
		return (File[]) path.toArray(new File[path.size()]);
	}

	/**
	 * Return a file with the specified (non-zero) number of relative
	 * file names, e.g. xxx(3) => ../../..
	 */
	private static File relativeParentFile(int len) {
		if (len <= 0) {
			throw new IllegalArgumentException("length must be greater than zero: " + len);
		}
		File result = new File("..");
		for (int i = len - 1; i-- > 0; ) {
			result = new File(result, "..");
		}
		return result;
	}

	/**
	 * Return a file that is a re-specification of the specified
	 * file, absolute to the specified directory.
	 *     Linux/Unix/Mac:
	 *         convertToAbsoluteFile(bar/baz.java, /foo)
	 *             => /foo/bar/baz.java
	 *     Windows:
	 *         convertToAbsoluteFile(bar/baz.java, C:\foo)
	 *             => C:\foo\bar\baz.java
	 * The file can be either a file or a directory; the directory
	 * *should* be a directory.
	 * If the file is already absolute, it will be returned unchanged.
	 * 
	 * NB: This method has been tested on Windows and Linux,
	 * but not Mac (but the Mac is Unix-based these days, so
	 * it shouldn't be a problem...).
	 */
	public static File convertToAbsoluteFile(final File file, final File dir) {
		// check whether the file is already absolute
		if (file.isAbsolute()) {
			return file;		// return unchanged
		}
		return canonicalFile(new File(dir, file.getPath()));
	}

	/**
	 * Return a file that is a re-specification of the specified
	 * file, absolute to the current working directory.
	 *     Linux/Unix/Mac (CWD = /foo):
	 *         convertToAbsoluteFile(bar/baz.java)
	 *             => /foo/bar/baz.java
	 *     Windows (CWD = C:\foo):
	 *         convertToAbsoluteFile(bar/baz.java)
	 *             => C:\foo\bar\baz.java
	 * The file can be either a file or a directory.
	 * If the file is already absolute, it will be returned unchanged.
	 * 
	 * NB: This method has been tested on Windows and Linux,
	 * but not Mac (but the Mac is Unix-based these days, so
	 * it shouldn't be a problem...).
	 */
	public static File convertToAbsoluteFile(final File file) {
		return convertToAbsoluteFile(file, currentWorkingDirectory());
	}


	// ********** constructor **********

	/**
	 * Suppress default constructor, ensuring non-instantiability.
	 */
	private FileTools() {
		super();
		throw new UnsupportedOperationException();
	}

}
