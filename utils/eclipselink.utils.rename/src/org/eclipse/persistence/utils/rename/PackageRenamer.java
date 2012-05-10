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
package org.eclipse.persistence.utils.rename;

import java.io.*;
import java.util.*;

/**
 * This class performs package renaming. It demonstrates the following:
 * 
 * a) Reading the properties file to be a reference for changing the package
 * name from your source code.
 * 
 * b) Traverse source root directory for creating a corresponding output
 * directory and finding the java source file(s) to be changing the package
 * name.
 * 
 * c) Search and replace the old TopLink package name(s) with new one(s)
 * according to the reference.
 * 
 * You will be able to see the logging message at the command line window where
 * the PackageRenamer is running.
 * 
 */
public class PackageRenamer {
	private static int BUFSIZ = 1024 * 4;

	private List<File> ignoreFiles = new ArrayList<File>();

	private List<RenameValue> renameValues = new ArrayList<RenameValue>();

	private int numberOfTotalFile = 0;

	private int numberOfChangedFile = 0;

	// contains the source-root-directory
	File sourceRootDirFile;

	// contains the destination-root-directory
	File destinationRootDir;

	public PackageRenamer(String sourceFolder, String targetFolder,
			Properties properties) {
		this.sourceRootDirFile = buildAndCheckExistingDirFile(sourceFolder);
		this.destinationRootDir = buildAndCheckDestinationFile(targetFolder);
		initialize(this.sourceRootDirFile, properties);
	}

	public List<File> getIgnoreFiles() {
		return ignoreFiles;
	}

	public List<RenameValue> getRenameValues() {
		return renameValues;
	}

	/**
	 * Do a binary copy of the file byte buffer by byte buffer.
	 */
	public void binaryCopy(File inFile, File outFile)
			throws FileNotFoundException, IOException {
		byte[] buf = new byte[BUFSIZ];
		FileInputStream in = new FileInputStream(inFile);

		// make sure the directories under this file are available
		String parent = outFile.getParent();
		if (parent != null) {
			File parentFile = new File(parent);
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
		}
		FileOutputStream out = new FileOutputStream(outFile);

		int nBytesRead;
		while ((nBytesRead = in.read(buf)) != -1) {
			out.write(buf, 0, nBytesRead);
		}
		in.close();
		out.close();
	}

	private boolean bufferContainsNullChar(byte[] buffer, int bufferLength) {
		for (int i = 0; i < bufferLength; i++) {
			if (buffer[i] == 0) {
				return true;
			}
		}
		return false;
	}

	public File buildAndCheckDestinationFile(String aDirString) {
		if (aDirString == null) {
			throw new RuntimeException("Invalid destination directory entered.");
		}

		File aDirFile = new File(aDirString);

		// Check if the destination directory is within the source directory.
		// This would create an infinite loop.
		if (directoryIsSubdirectory(sourceRootDirFile, aDirFile)) {
			throw new RuntimeException("Invalid destination directory entered:"
					+ "  '" + aDirString + "'"
					+ "It cannot be a sub-directory of the source directory.");
		}

		return aDirFile;
	}

	public File buildAndCheckExistingDirFile(String aDirString) {
		if (aDirString == null) {
			throw new RuntimeException("Invalid source directory entered.");
		}

		File aDirFile = new File(aDirString);

		if (!aDirFile.exists() || !aDirFile.isDirectory()) {
			throw new RuntimeException("Input Directory:  '" + aDirString + "'"
					+ "does not exist or is not a directory.");
		}

		return aDirFile;
	}

	/**
	 * Returns the extension of the given file. Returns and empty string if none
	 * was found.
	 */
	protected static String parseFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) {
			return "";
		} else {
			return fileName.substring(index + 1);
		}
	}

	/**
	 * Return true if directory2 is contained within directory1. Both
	 * directories must be absolute.
	 */
	public static boolean directoryIsSubdirectory(File directory1,
			File directory2) {
		// System.out.println(directory1 + " contains " + directory2);
		if (directory2 == null) {
			return false;
		} else if (directory1.equals(directory2)) {
			return true;
		} else {
			return directoryIsSubdirectory(directory1, directory2
					.getParentFile());
		}
	}

	/**
	 * Return true if the PackageRenamer should work on the given file
	 * extension.
	 */
	public boolean isExtensionSupported(String extension) {
		return extension.equalsIgnoreCase("java")
				|| extension.equalsIgnoreCase("xml")
				|| extension.equalsIgnoreCase("mwp");
	}

	/**
	 * This run() method performs, reading the properties file into properties
	 * variable to be a reference for changing package name. creating an
	 * destination-root-direetory. and, calling traverseSourceDirectory()
	 * method.
	 */
	public void run() {
		System.out.println("LOG MESSAGES FROM packageRenamer");
		System.out.println("");
		System.out.println("INPUT: -----------------> "
				+ sourceRootDirFile.toString());
		System.out.println("OUTPUT: ----------------> "
				+ destinationRootDir.toString());

		// Listing the changed file(s)
		System.out.println("List of changed file(s): ");
		traverseSourceDirectory(sourceRootDirFile);

		System.out.println("Total Changed File(s): ------> "
				+ numberOfChangedFile);
		System.out.println("Total File(s):         ------> "
				+ numberOfTotalFile);
	}

	/**
	 * This runSearchAndReplacePackageName() reads an pre-rename source file all
	 * into string variable and replacing the old package names with the new
	 * ones according to the properties file.
	 */
	public void runSearchAndReplacePackageName(File sourceFile) {
		if (getIgnoreFiles().contains(sourceFile)) {
			return;
		}

		String stringContainAllFile = "";
		String sourceFileName = sourceFile.toString();
		String sourceFileNameWithoutRoot = sourceFile.toString().substring(
				sourceRootDirFile.toString().length() + 1);
		String destinationFileNameWithoutRoot = renameFile(sourceFileNameWithoutRoot);

		String destinationFileName = destinationRootDir.toString()
				+ File.separator + destinationFileNameWithoutRoot;
		File destFile = new File(destinationFileName);

		if (destFile.exists()) {
			System.out.println("WARNING: Skipping pre-existing: " + destFile);
			return;
		}
		System.out.print("MIGRATING: " + sourceFileName);

		// Reading file into string.
		// stringContainAllFile = readAllStringsFromFile(sourceFileName);
		try {

			FileInputStream fis = new FileInputStream(new File(sourceFileName));
			byte[] buf = new byte[BUFSIZ];
			StringBuffer strBuf = new StringBuffer((int) new File(
					sourceFileName).length());
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				if (bufferContainsNullChar(buf, i)) {
					// This is a binary file, just copy it byte by byte to the
					// new location. Do not do any renaming.
					fis.close();
					binaryCopy(sourceFile, new File(destinationFileName));
					return;
				}
				String str = new String(buf, 0, i);
				strBuf.append(str);
			}
			fis.close();
			stringContainAllFile = new String(strBuf);

		} catch (IOException ioException) {
			throw new RuntimeException(
					"Unexpected exception was thrown during file manipulation."
							+ ioException.getMessage());
		}

		System.out.print(" -> ");
		// Starting to rename.

		RenameFileData fileData = new RenameFileData(stringContainAllFile,
				false);
		// Make sure package is correct

		String sourcePackageName = null;
		String destPackageName = null;

		if (sourceFileNameWithoutRoot.lastIndexOf('\\') >= 0) {
			sourceFileNameWithoutRoot = sourceFileNameWithoutRoot.substring(0,
					sourceFileNameWithoutRoot.lastIndexOf('\\'));
			destinationFileNameWithoutRoot = destinationFileNameWithoutRoot
					.substring(0, destinationFileNameWithoutRoot
							.lastIndexOf('\\'));
		}
		sourcePackageName = sourceFileNameWithoutRoot.replace('\\', '.');
		destPackageName = destinationFileNameWithoutRoot.replace('\\', '.');
		fileData = new RenameValue("package " + sourcePackageName + ";",
				"package " + destPackageName + ";").replace(fileData);

		for (RenameValue rv : getRenameValues()) {
			fileData = rv.replace(fileData);
		}

		if (fileData.isChanged()) {
			this.numberOfChangedFile++;
		}

		System.out.println(destFile);
		// Writing output file.
		try {

			destFile.getParentFile().mkdirs();

			FileWriter writer = new FileWriter(destFile);
			java.io.PrintWriter out = new java.io.PrintWriter(writer);
			out.print(fileData.getFileContentsString());
			out.close();

		} catch (FileNotFoundException fileNotFoundException) {
			throw new RuntimeException("Could not find file to write:" + "  '"
					+ destinationFileName + "'"
					+ fileNotFoundException.getMessage());
		} catch (IOException ioException) {
			throw new RuntimeException(
					"Unexpected exception was thrown while writing the file: '"
							+ destinationFileName + "', "
							+ ioException.getMessage());
		}
	}

	public String renameFile(String source) {
		String extension = parseFileExtension(source);
		String resourceName = source.substring(0, source.length()
				- (extension.length() + 1));
		String packageName = resourceName;
		if ("java".equals(extension)) {
			packageName = resourceName.replace('\\', '.');
		}
		String targetPackageName = packageName;

		RenameFileData fileData = new RenameFileData(targetPackageName, true);
		for (RenameValue rv : getRenameValues()) {
			fileData = rv.replace(fileData);
		}

		if ("java".equals(extension)) {
			fileData.setFileContentsString(fileData.getFileContentsString().replace('.', '\\'));
		}

		if (extension != null && extension.length() > 0) {
			fileData.setFileContentsString(fileData.getFileContentsString()
					+ "." + extension);
		}

		return fileData.getFileContentsString();
	}

	/**
	 * This traverseSourceDirectory() traverse source-root-directory, creating
	 * an corresponding output directory, and calling another method for
	 * replacing old TopLink package name.
	 */
	public void traverseSourceDirectory(File directory) {
		File[] filesAndDirectories = directory.listFiles();

		for (int i = 0; i < filesAndDirectories.length; i++) {
			File fileOrDirectory = filesAndDirectories[i];

			if (fileOrDirectory.isDirectory()) {
				if (!fileOrDirectory.getName().equalsIgnoreCase(".svn")) {
					traverseSourceDirectory(fileOrDirectory);
				}
			} else {
				this.numberOfTotalFile++;
				// Check that it does not have an unsupported file extension
				String fileExtension = parseFileExtension(fileOrDirectory
						.getName());
				if (isExtensionSupported(fileExtension)) {
					runSearchAndReplacePackageName(fileOrDirectory);
				}
			}
		}
	}

	/**
	 * 
	 * @param srcRoot
	 * @param properties
	 * @return
	 */
	private void initialize(File srcRoot, Properties properties) {
		for (Object key : properties.keySet()) {
			String packageOrClassName = (String) key;
			String value = properties.getProperty(packageOrClassName);

			if (value.equalsIgnoreCase("ignore")) {
				File folder = new File(srcRoot, packageOrClassName.replace('.',
						'\\'));

				if (folder.exists() && folder.isDirectory()) {
					addIgnoreFolder(folder, ignoreFiles);
				} else {
					File file = new File(srcRoot, packageOrClassName.replace(
							'.', '\\')
							+ ".java");

					if (file.exists()) {
						ignoreFiles.add(file);
					}/*
						 * else { throw new IllegalArgumentException( "Could not
						 * find src to ignore: " + srcRoot + " :: " +
						 * packageOrClassName); }
						 */
				}
			} else {
				renameValues.add(new RenameValue(packageOrClassName, value));
			}
		}
		Collections.sort(renameValues, RenameValue.renameValueComparator());

		/*
		 * System.out.println("FILES TO IGNORE"); for (File file : ignoreFiles) {
		 * System.out.println("\t> " + file); }
		 * 
		 * System.out.println("\nRENAME VALUES"); for (RenameValue rv :
		 * renameValues) { System.out.println("\t" + rv); }
		 */
	}

	/**
	 * 
	 * @param folder
	 * @param ignoreFiles
	 */
	private void addIgnoreFolder(File folder, List<File> ignoreFiles) {
		File[] files = folder.listFiles();

		for (int index = 0; index < files.length; index++) {
			File file = files[index];

			if (file.isFile()
					&& isExtensionSupported(parseFileExtension(file.getName()))) {
				ignoreFiles.add(file);
			} else if (file.isDirectory()
					&& !file.getName().equalsIgnoreCase(".svn")) {
				addIgnoreFolder(file, ignoreFiles);
			}
		}
	}
}
