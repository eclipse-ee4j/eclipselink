/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools;

import java.util.*;
import java.io.*;

/**
 * This class performs package renaming. It demonstrates the following:
 *
 * a) Reading the properties file to be a reference for changing the package
 * name from your source code.
 *
 * b) Traverse source root directory for creating a corresponding output directory
 * and finding the java source file(s) to be changing the package name.
 *
 * c) Search and replace the old TopLink package name(s) with new one(s) according to the reference.
 *
 * You will be able to see the logging message at the command line window
 * where the PackageRenamer is running.
 *
 */
public class PackageRenamer {
    int numberOfTotalFile = 0;
    int numberOfChangedFile = 0;

    // contains the option of Log file.
    boolean specifyLogFile = false;
    java.io.PrintWriter outLog = null;

    // contains the Log File
    String logFileString = null;
    java.io.File logFile;

    // contains the source-root-directory
    java.io.File sourceRootDirFile;

    // contains the destination-root-directory
    java.io.File destinationRootDir;

    // contains the properties file path
    String sourceProperties;

    // contains a reference for renaming
    Properties properties = null;
    String propertiesFileName;
    boolean VERBOSE = true;
    protected static final String SYSTEM_OUT = "System.out";
    BufferedReader reader = null;
    String[] UNSUPPORTED_EXTENSIONS = { "jar", "zip", "ear", "war", "dll", "class", "exe" };
    int BUFSIZ = 1024 * 4;
    String CR = System.getProperty("line.separator");

    /**
    * The constructor of a PackageRenamer class.
    */
    public PackageRenamer() {
        this(getDefaultPropertiesFileName());
    }

    public PackageRenamer(String propertiesFileName) {
        System.out.println("");
        System.out.println("TopLink Package Renamer");
        System.out.println("-----------------------");
        System.out.println(bannerText());
        sourceRootDirFile = existingDirectoryFromPrompt();
        System.out.println("");
        destinationRootDir = promptForDestinationDirectory();
        System.out.println("");
        this.propertiesFileName = propertiesFileName;
        outLog = streamForNonExistentFilePrompt();
        properties = readChangesFile(propertiesFileName);
    }

    public PackageRenamer(String[] args) {
        this.propertiesFileName = args[0];
        sourceRootDirFile = buildAndCheckExistingDirFile(args[1]);
        destinationRootDir = buildAndCheckDestinationFile(args[2]);
        if (args.length == 4) {
            outLog = buildAndCheckLogWriter(args[3]);
        } else {
            outLog = buildAndCheckLogWriter(SYSTEM_OUT);
        }
        properties = readChangesFile(args[0]);
        logln(bannerText());
    }

    protected String bannerText() {
        StringBuffer stringBuffer = new StringBuffer(300);
        stringBuffer.append(CR);
        stringBuffer.append("NOTE: The package renamer is meant to be run on plain text files. ");
        stringBuffer.append(CR);
        stringBuffer.append("A rename will NOT be done on binary files.");
        stringBuffer.append(CR);
        return stringBuffer.toString();
    }

    /**
    * Do a binary copy of the file byte buffer by byte buffer.
    */
    public void binaryCopy(File inFile, File outFile) throws FileNotFoundException, IOException {
        byte[] buf = new byte[BUFSIZ];
        FileInputStream in = new FileInputStream(inFile);
        FileOutputStream out = new FileOutputStream(outFile);
        int nBytesRead;
        while ((nBytesRead = in.read(buf)) != -1) {
            out.write(buf, 0, nBytesRead);
        }
        in.close();
        out.close();
    }

    protected boolean bufferContainsNullChar(byte[] buffer, int bufferLength) {
        for (int i = 0; i < bufferLength; i++) {
            if (buffer[i] == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * INTERNAL
     */
    public File buildAndCheckDestinationFile(String aDirString) {
        if (aDirString == null) {
            throw new PackageRenamerException("Invalid destination directory entered.");
        }

        File aDirFile = new File(aDirString);

        if (aDirFile.exists()) {
            if (aDirFile.listFiles().length != 0) {
                throw new PackageRenamerException("Output Directory:" + CR + "  '" + aDirString + "'" + CR + "exists and is not empty.");
            }
        }

        if (!aDirFile.isAbsolute()) {
            throw new PackageRenamerException("A relative destination directory was entered:" + CR + "  '" + aDirString + "'" + CR + "The directory must be absolute.");
        }

        // Check if the destination directory is within the source directory. This would create an infinite loop.
        if (directoryIsSubdirectory(sourceRootDirFile, aDirFile)) {
            throw new PackageRenamerException("Invalid destination directory entered:" + CR + "  '" + aDirString + "'" + CR + "It cannot be a sub-directory of the source directory.");
        }

        return aDirFile;
    }

    public File buildAndCheckExistingDirFile(String aDirString) {
        if (aDirString == null) {
            throw new PackageRenamerException("Invalid source directory entered.");
        }

        File aDirFile = new File(aDirString);

        if (!aDirFile.exists() || !aDirFile.isDirectory()) {
            throw new PackageRenamerException("Input Directory:" + CR + "  '" + aDirString + "'" + CR + "does not exist or is not a directory.");
        }

        if (!aDirFile.isAbsolute()) {
            throw new PackageRenamerException("A relative source directory was entered:" + CR + "  '" + aDirString + "'" + CR + "The directory must be absolute.");
        }

        return aDirFile;
    }

    public PrintWriter buildAndCheckLogWriter(String logFileString) {
        if (logFileString == null) {
            throw new PackageRenamerException("Invalid log file name entered.");
        }

        try {
            if (logFileString.equals(SYSTEM_OUT)) {
                return new PrintWriter(System.out);
            } else {
                File aLogFile = new File(logFileString);

                if (aLogFile.exists()) {
                    throw new PackageRenamerException("Specified log file cannot be created:" + CR + "  '" + logFileString + "'");
                }
                FileWriter writerLog = new FileWriter(logFileString);
                return new java.io.PrintWriter(writerLog);
            }
        } catch (IOException ioException) {
            throw new PackageRenamerException("Unhandled IOException occurred while configuring log file: '" + logFileString + "', " + ioException.getMessage());
        }
    }

    protected void cleanup() {
        //Closing the Log file in case it is being open.
        if (outLog != null) {
            outLog.close();
        }
    }

    /**
     * This method creates an output directory for post-rename file(s).
     */
    public void createDestinationDirectory(File aDirectory) {
        if (!aDirectory.exists()) {
            if (!aDirectory.mkdirs()) {
                throw new PackageRenamerException("Error while creating directory:" + CR + "  '" + aDirectory.toString() + "'");
            }
        } else {
            throw new PackageRenamerException("Error directory: '" + aDirectory.toString() + "' already exists but shouldn't.");
        }
    }

    /**
     * Return true if directory2 is contained within directory1. Both directories must be absolute.
     */
    public static boolean directoryIsSubdirectory(File directory1, File directory2) {
        //        System.out.println(directory1 + " contains " + directory2);
        if (directory2 == null) {
            return false;
        } else if (directory1.equals(directory2)) {
            return true;
        } else {
            return directoryIsSubdirectory(directory1, directory2.getParentFile());
        }
    }

    public File existingDirectoryFromPrompt() {
        System.out.print("Enter the path of the directory which contains the files to rename:" + CR + "> ");
        String aLine = null;
        try {
            aLine = getReader().readLine();
        } catch (IOException exception) {
            throw new PackageRenamerException("Error while reading the source directory: " + exception.getMessage());
        }
        return buildAndCheckExistingDirFile(aLine);
    }

    public static String getDefaultPropertiesFileName() {
        String currentDirectory = System.getProperty("user.dir");
        return currentDirectory + File.separator + "packageRename.properties";
    }

    public synchronized BufferedReader getReader() {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(System.in));
        }
        return reader;
    }

    /**
    * Return true if the PackageRenamer should work on the given file extension.
    */
    public boolean isExtensionSupported(String extension) {

        /* This was cut out because the binary check recognize these files and just copy them byte by byte.
        for (int i=0; i<UNSUPPORTED_EXTENSIONS.length; i++) {
            if ( UNSUPPORTED_EXTENSIONS[i].equalsIgnoreCase(extension)) {
                return false;
            }
        }
        */
        return true;
    }

    /**
    */
    public void logln(String str) {
        outLog.println(str);
        outLog.flush();
    }

    /**
    * Main method to run the PackageRenamer
    *
    */
    public static void main(String[] args) {
        // Check the accuracy of the arguments
        PackageRenamer instance = null;
        try {
            // Creates an instance of an PackageRenamer with some arguments.
            if (args.length == 0) {
                instance = new PackageRenamer();
                instance.run();
            } else if (args.length == 1) {
                instance = new PackageRenamer(args[0]);
                instance.run();
            } else if ((args.length == 3) || (args.length == 4)) {
                instance = new PackageRenamer(args);
                instance.run();
            } else {
                usage();
                System.exit(-1);
            }
        } catch (PackageRenamerException exception) {
            usage();
            System.err.println("**************************************************************************");
            System.err.println("Error during package rename. PACKAGE RENAME FAILED.");
            System.err.println(exception.getMessage());
            System.err.println("**************************************************************************");
            System.exit(-1);
        } catch (Throwable unknowException) {
            System.err.println("Unhandled exception was thrown during rename:");
            unknowException.printStackTrace();
            System.exit(-1);
        }

        instance.logln("");
        instance.logln("PACKAGE RENAME WAS SUCCESSFUL");

        instance.cleanup();

    }

    /**
    * Returns the extension of the given file. Returns and empty string if none was found.
    */
    public String parseFileExtension(File aFile) {
        int index = aFile.getName().lastIndexOf('.');
        if (index == -1) {
            return "";
        } else {
            return aFile.getName().substring(index + 1);
        }
    }

    /**
    * INTERNAL
    * Prompt from System.in for an empty or non-existent directory to use as the destination directory.
    */
    protected File promptForDestinationDirectory() {
        System.out.print("Enter the path of the directory to which files are to be copied:" + CR + "> ");
        String aLine = null;
        try {
            aLine = getReader().readLine();
        } catch (IOException exception) {
            throw new PackageRenamerException("Error while reading the destination directory specified: " + exception.getMessage());
        }
        return buildAndCheckDestinationFile(aLine);

    }

    /**
    * This readChangesFile() method reads the given properties file to be a reference
    * for renaming TopLink package name.
    */
    public Properties readChangesFile(String filename) {
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(filename);
            props.load(in);
        } catch (FileNotFoundException fileNotFoundException) {
            throw new PackageRenamerException("Properties file was not found:" + CR + "  '" + filename + "'");
        } catch (IOException ioException) {
            throw new PackageRenamerException("IO error occurred while reading the properties file:'" + filename + "'" + ioException.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        logln("Using properties file: " + filename);
        return props;
    }

    /**
    * This run() method performs,
    * reading the properties file into properties variable to be a reference for changing package name.
    * creating an destination-root-direetory.
    * and, calling traverseSourceDirectory() method.
    */
    public void run() {
        // Start loging.
        logln("LOG MESSAGES FROM packageRenamer");
        logln("" + new Date());
        logln("");
        logln("INPUT: -----------------> " + sourceRootDirFile.toString());
        logln("OUTPUT: ----------------> " + destinationRootDir.toString());
        //        logln("PROPERTIES FILE: -------> "+sourceProperties);
        //        logln("LOG FILE: --------------> "+writerLog);
        logln("");

        // Root output directory.
        logln("Verifying root output directory...");
        if (!(destinationRootDir.exists())) {
            // Create root directory for output first.
            logln("");
            logln("Creating root output directory...");
            createDestinationDirectory(destinationRootDir);
            logln("");
        }
        logln("Verifying root output directory...DONE");
        logln("");

        // Listing the changed file(s)
        logln("List of changed file(s): ");
        logln("");
        traverseSourceDirectory(sourceRootDirFile);
        logln("");
        logln("Total Changed File(s): ------> " + numberOfChangedFile);
        logln("Total File(s):         ------> " + numberOfTotalFile);
        logln("");

    }

    protected PrintWriter streamForNonExistentFilePrompt() {
        System.out.print("Enter the absolute path of the log file [Hit Enter for SYSTEM.OUT]:" + CR + "> ");
        String aLine = null;
        try {
            aLine = getReader().readLine();
        } catch (IOException exception) {
            throw new PackageRenamerException("Error while reading the name of the log file: " + exception.getMessage());
        }
        if ((aLine != null) && (aLine.length() == 0)) {
            return buildAndCheckLogWriter(SYSTEM_OUT);
        } else {
            return buildAndCheckLogWriter(aLine);
        }
    }

    /**
    * This runSearchAndReplacePackageName() reads an pre-rename source file all into string variable and
    * replacing the old package names with the new ones according to the properties file.
    */
    public void runSearchAndReplacePackageName(java.io.File sourceFile) {
        String stringContainAllFile = "";
        String sourceFileName = sourceFile.toString();
        String sourceFileNameWithoutRoot = sourceFile.toString().substring(sourceRootDirFile.toString().length() + 1);

        // Rename the file name if required.
        sourceFileNameWithoutRoot = returnNewFileNameIfRequired(sourceFileNameWithoutRoot);

        String destinationFileName = destinationRootDir.toString() + File.separator + sourceFileNameWithoutRoot;

        // Reading file into string.
        // stringContainAllFile = readAllStringsFromFile(sourceFileName);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new java.io.File(sourceFileName));
            byte[] buf = new byte[BUFSIZ];
            StringBuffer strBuf = new StringBuffer((int)new java.io.File(sourceFileName).length());
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                if (bufferContainsNullChar(buf, i)) {
                    // This is a binary file, just copy it byte by byte to the new location. Do not do any renaming.
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
            throw new PackageRenamerException("Unexpected exception was thrown during file manipulation." + ioException.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        // Sorting key package name.
        Vector aVector = new Vector();
        for (Enumeration e = properties.keys(); e.hasMoreElements();) {
            aVector.addElement(e.nextElement());
        }
        String[] aStringArrayOfSortedKeyPackageName = new String[aVector.size()];
        aVector.copyInto(aStringArrayOfSortedKeyPackageName);
        Arrays.sort(aStringArrayOfSortedKeyPackageName);

        // Starting to rename.
        boolean alreadyPrint = false;
        int index = aStringArrayOfSortedKeyPackageName.length;
        for (Enumeration enumtr = properties.keys(); enumtr.hasMoreElements();) {
            enumtr.nextElement();
            String key = aStringArrayOfSortedKeyPackageName[index - 1];
            String value = (String)properties.get(key);
            index -= 1;

            // Printing the changed file.
            int found = stringContainAllFile.indexOf(key);
            if ((found != -1) && (!alreadyPrint)) {
                alreadyPrint = true;
                logln((numberOfChangedFile + 1) + ". " + destinationFileName);
                numberOfChangedFile++;
            }

            // replacing the old package name.
            stringContainAllFile = replace(stringContainAllFile, key, value);
        }

        // Writing output file.
        try {
            FileWriter writer = new FileWriter(destinationFileName);
            java.io.PrintWriter out = new java.io.PrintWriter(writer);
            out.print(stringContainAllFile);
            out.close();

        } catch (FileNotFoundException fileNotFoundException) {
            throw new PackageRenamerException("Could not find file to write:" + CR + "  '" + destinationFileName + "'" + CR + fileNotFoundException.getMessage());
        } catch (IOException ioException) {
            throw new PackageRenamerException("Unexpected exception was thrown while writing the file: '" + destinationFileName + "', " + ioException.getMessage());
        }
    }

    /**
    * Do a search and replace in a string.
    * @return the modified String
    */
    public static String replace(String str, String oldChars, String newChars) {
        int len;
        int pos;
        int lastPos;

        len = newChars.length();
        pos = str.indexOf(oldChars);
        lastPos = pos;

        while (pos > -1) {
            String firstPart;
            String lastPart;

            firstPart = str.substring(0, pos);
            lastPart = str.substring(pos + oldChars.length(), str.length());
            str = firstPart + newChars + lastPart;
            lastPos = pos + len;
            pos = str.indexOf(oldChars, lastPos);
        }

        return (str);
    }

    /**
    */
    public String returnNewFileNameIfRequired(String aSourceFileNameWithoutRoot) {
        for (Enumeration enumtr = properties.keys(); enumtr.hasMoreElements();) {
            String key = (String)enumtr.nextElement();

            if (aSourceFileNameWithoutRoot.indexOf(key) != -1) {
                // replacing the old package name.
                aSourceFileNameWithoutRoot = replace(aSourceFileNameWithoutRoot, key, (String)properties.get(key));
            }
        }

        // just simply return whether it has been changed.
        return aSourceFileNameWithoutRoot;
    }

    /**
    * This traverseSourceDirectory() traverse source-root-directory, creating an corresponding output directory,
    * and calling another method for replacing old TopLink package name.
    */
    public void traverseSourceDirectory(java.io.File aDirectoryString) {
        java.io.File[] filesAndDirectories = aDirectoryString.listFiles();

        for (int i = 0; i < filesAndDirectories.length; i++) {
            java.io.File fileOrDirectory = filesAndDirectories[i];
            if (fileOrDirectory.isDirectory()) {
                String sourceDirectoryName = fileOrDirectory.toString();
                String destinationDirectoryName = destinationRootDir.toString() + sourceDirectoryName.substring(sourceRootDirFile.toString().length(), sourceDirectoryName.length());
                createDestinationDirectory(new java.io.File(destinationDirectoryName));
                traverseSourceDirectory(fileOrDirectory);
            } else {
                // it is a file.
                numberOfTotalFile++;
                // Check that it does not have an unsupported file extension
                String fileExtension = parseFileExtension(fileOrDirectory);
                if (isExtensionSupported(fileExtension)) {
                    runSearchAndReplacePackageName(fileOrDirectory);
                }
            }
        }
    }

    public static void usage() {
        System.out.println("");
        System.out.println("TopLink Package Renamer");
        System.out.println("-----------------------");
        System.out.println("");
        System.out.println("The package  renamer should  be run  once on user source code, configuration");
        System.out.println("files,  and   Mapping  Workbench  project  files  that  have  references  to");
        System.out.println("pre-Oracle 9iAS TopLink 9.0.3  API  packages.  The package renamer  works on");
        System.out.println("plain text files and should NOT be run on binary files such as JAR files.");
        System.out.println("");
        System.out.println("The package renamer supports two command line usages. A call which specifies");
        System.out.println("all the required arguments, and  a  call which takes only one parameter.  In");
        System.out.println("this last case, the user is prompted for the missing arguments.");
        System.out.println("");
        System.out.println("Usage:");
        System.out.println("");
        System.out.println("java org.eclipse.persistence.tools.PackageRenamer <properties-file>");
        System.out.println("");
        System.out.println("OR");
        System.out.println("");
        System.out.println("java org.eclipse.persistence.tools.PackageRenamer <properties-file> <source-root-directory> <destination-root-directory> [ <log-file> ]");
        System.out.println("");
        System.out.println("where:");
        System.out.println("\t<properties-file> - File containing  a list of  old and new package");
        System.out.println("\tnames.");
        System.out.println("");
        System.out.println("\t<source-root-directory> - Absolute path name of the directory which");
        System.out.println("\tcontains all the file to be converted.  The <source-root-directory>");
        System.out.println("\twill be searched recursively for files  to convert.  This directory");
        System.out.println("\tshould contain only the plain text files to be converted.");
        System.out.println("");
        System.out.println("\t<destination-root-directory> - Absolute path name of  the directory");
        System.out.println("\twhere the converted directory  structure will be copied.  All files");
        System.out.println("\twill be copied to  the new directory structure whether changes were");
        System.out.println("\tmade or not. This directory must either not exist or be empty.");
        System.out.println("");
        System.out.println("\t<log-file> - The logging  of  the  renaming process will be written");
        System.out.println("\tto the <log-file>.  If no  <log-file>  is  specified  then  logging");
        System.out.println("\twill be written to standard output.");
        System.out.println("");
    }

    public class PackageRenamerException extends RuntimeException {
        public PackageRenamerException(String aMessage) {
            super(aMessage);
        }
    }
}
