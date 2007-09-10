/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.Record;

/**
 * Default implementation of accessor file policy.
 *
 * @see XMLFileAccessor
 *
 * @author Big Country
 * @since TOPLink/Java 4.5
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class DefaultXMLFileAccessorFilePolicy implements XMLFileAccessorFilePolicy {

    /** The base directory for the tree of XML subdirectories/files. */
    private File baseDirectory;

    /** The extension to be appended to the generated XML file names. */
    private String fileNameExtension;
    public static String DEFAULT_FILE_NAME_EXTENSION = ".xml";

    /** Indicate whether to create directories if they are missing. */
    private boolean createsDirectoriesAsNeeded;

    /** Use this to "normalize" file names that are generated with
         invalid characters. */
    private FileNameNormalizer fileNameNormalizer;

    // ********** constructors **********

    /**
     * Default constructor. Initialize the newly-created instance.
     */
    public DefaultXMLFileAccessorFilePolicy() {
        super();
        this.initialize();
    }

    // ********** XMLFileAccessorFilePolicy implementation **********

    /**
     * @see XMLFileAccessorFilePolicy#createFileSource(String)
     * The name of this directory will typically take the form of
     *         [base dir]/[root element]
     */
    public void createFileSource(String rootElementName) throws XMLDataStoreException {
        this.createFileSource(this.buildDocumentDirectoryUnchecked(rootElementName));
    }

    /**
     * @see XMLFileAccessorFilePolicy#dropFileSource(String)
     * The name of this directory will typically take the form of
     *         [base dir]/[root element]
     */
    public void dropFileSource(String rootElementName) throws XMLDataStoreException {
        this.dropFileSource(this.buildDocumentDirectoryUnchecked(rootElementName));
    }

    /**
     * @see XMLFileAccessorFilePolicy#getAllFiles(String)
     * The name of these files will typically take the form of
     *         [base dir]/[root element]/*.xml
     */
    public Enumeration getAllFiles(String rootElementName) throws XMLDataStoreException {
        return new FileListEnumerator(this.listFiles(this.buildDocumentDirectory(rootElementName), this.buildExtensionFileFilter()));
    }

    /**
     * @see XMLFileAccessorFilePolicy#getFile(String, Record, Vector)
     * The name of this file will typically take the form of
     *         [base dir]/[root element]/[key].xml
     */
    public File getFile(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        return this.buildFile(this.buildDocumentDirectory(rootElementName), this.buildFileName(row, orderedPrimaryKeyElements));
    }

    /**
     * @see XMLFileAccessorFilePolicy#validateConfiguration()
     * Make sure the base directory is OK.
     */
    public void validateConfiguration() throws XMLDataStoreException {
        this.checkDirectory(this.getBaseDirectory());
    }

    /**
     * All the XML documents are stored in subdirectories
     * under this base directory.
     */
    protected File buildDefaultBaseDirectory() {
        return this.tempDirectory();
    }

    /**
     * Return whether the policy will create directories
     * as needed, if they do not exist already.
     */
    protected boolean buildDefaultCreatesDirectoriesAsNeeded() {
        return false;
    }

    /**
     * Return the default standard file name extension that will be
     * appended to the primary key element value(s) to generate the
     * complete file name.
     */
    protected String buildDefaultFileNameExtension() {
        return DEFAULT_FILE_NAME_EXTENSION;
    }

    /**
     * Return the default normalizer that will convert invalid file names
     * into something usable.
     */
    protected FileNameNormalizer buildDefaultFileNameNormalizer() {
        return new DefaultFileNameNormalizer();
    }

    /**
     * Return a file for the specified directory name.
     * <i>Assume</i> a valid directory name. This allows us to create
     * directories directly. In the future, we might want to check
     * for characters that are invalid for directory names.
     */
    protected File buildDirectory(String directoryName) {
        return new File(directoryName);
    }

    /**
     * Return the directory that holds all the documents
     * with the specified root element name.
     * Create it if necessary.
     */
    protected File buildDocumentDirectory(String rootElementName) throws XMLDataStoreException {
        File directory = this.buildDocumentDirectoryUnchecked(rootElementName);
        this.checkDocumentDirectory(directory);
        return directory;
    }

    /**
     * Return the directory that holds all the documents
     * with the specified root element name.
     * If it does not exist, do <i>not</i> create it.
     */
    protected File buildDocumentDirectoryUnchecked(String rootElementName) throws XMLDataStoreException {
        return this.buildFile(this.getBaseDirectory(), rootElementName);
    }

    /**
     * Return a file filter that will "accept" any
     * file that is not a directory and that has the appropriate
     * file extension (.xml, by default).
     */
    protected FileFilter buildExtensionFileFilter() {
        return this.buildExtensionFileFilter(this.getFileNameExtension());
    }

    /**
     * Return a file filter that will "accept" any
     * file that is not a directory and that has the specified
     * file extension (.xml, by default).
     */
    protected FileFilter buildExtensionFileFilter(String fileNameExtension) {
        return new ExtensionFileFilter(fileNameExtension);
    }

    /**
     * Return a file for the specified parent directory and child file name.
     * Handle any invalid characters in the child file name.
     */
    protected File buildFile(File parentDirectory, String childFileName) {
        return new File(parentDirectory, this.normalizeFileName(childFileName));
    }

    /**
     * Return a file name for the specified primary key.
     */
    protected String buildFileName(Record row, Vector orderedPrimaryKeyElements) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        for (Enumeration stream = this.extractPrimaryKeyValues(row, orderedPrimaryKeyElements);
                 stream.hasMoreElements();) {
            pw.print(stream.nextElement());
        }

        pw.write(this.getFileNameExtension());
        return sw.toString();
    }

    /**
     * Check whether the specified directory exists.
     * If appropriate, create it.
     */
    protected void checkDirectory(File directory) throws XMLDataStoreException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw XMLDataStoreException.notADirectory(directory);
            }
        } else {
            if (this.createsDirectoriesAsNeeded()) {
                if (!directory.mkdirs()) {
                    throw XMLDataStoreException.directoryCouldNotBeCreated(directory);
                }
            } else {
                throw XMLDataStoreException.directoryNotFound(directory);
            }
        }
    }

    /**
     * Check whether the specified document directory exists.
     * If appropriate, create it.
     */
    protected void checkDocumentDirectory(File directory) throws XMLDataStoreException {
        this.checkDirectory(directory);
    }

    /**
     * Create the specified directory if necessary.
     */
    protected void createFileSource(File directory) throws XMLDataStoreException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw XMLDataStoreException.notADirectory(directory);
            }
        } else {
            if (!directory.mkdirs()) {
                throw XMLDataStoreException.directoryCouldNotBeCreated(directory);
            }
        }
    }

    /**
     * Return whether the policy will create directories
     * as needed, if they do not exist already.
     */
    public boolean createsDirectoriesAsNeeded() {
        return createsDirectoriesAsNeeded;
    }

    /**
     * Drop the specified directory and all of its contents.
     * Use with care.
     */
    protected void dropDirectory(File directory) {
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                this.dropDirectory(file);// recurse through subdirectories
            } else {
                file.delete();
            }
        }
        directory.delete();
    }

    /**
     * Drop the specified directory if necessary.
     */
    protected void dropFileSource(File directory) throws XMLDataStoreException {
        if (directory.exists()) {
            if (directory.isDirectory()) {
                this.dropDirectory(directory);
            } else {
                throw XMLDataStoreException.notADirectory(directory);
            }
        }
    }

    /**
     * Extract the values for the specified primary key elements
     * from the specified row. Keep them in the same order
     * as the elements.
     */
    protected Enumeration extractPrimaryKeyValues(Record row, Vector orderedPrimaryKeyElements) {
        Vector result = new Vector(orderedPrimaryKeyElements.size());
        for (Enumeration stream = orderedPrimaryKeyElements.elements(); stream.hasMoreElements();) {
            result.addElement(row.get((DatabaseField)stream.nextElement()));
        }
        return result.elements();
    }

    /**
     * All the XML documents are stored in subdirectories
     * under this base directory.
     */
    public File getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Return the standard file name extension that will be
     * appended to the primary key element value(s) to generate the
     * complete file name.
     */
    public String getFileNameExtension() {
        return fileNameExtension;
    }

    /**
     * Return the normalizer that will convert invalid file names
     * into something usable.
     */
    public FileNameNormalizer getFileNameNormalizer() {
        return fileNameNormalizer;
    }

    /**
     *    Initialize the accessor:<ul>
     * <li>base directory
     * <li>file extension
     * <li>directory creation flag
     * <li>file name normalizer
     * </ul>
     */
    protected void initialize() {
        this.setBaseDirectory(this.buildDefaultBaseDirectory());
        this.setFileNameExtension(this.buildDefaultFileNameExtension());
        this.setCreatesDirectoriesAsNeeded(this.buildDefaultCreatesDirectoriesAsNeeded());
        this.setFileNameNormalizer(this.buildDefaultFileNameNormalizer());
    }

    /**
     * Return an array of the files in the specified directory that
     * match the specified filter.
     */
    protected File[] listFiles(File directory, FileFilter fileFilter) throws XMLDataStoreException {
        return directory.listFiles(fileFilter);
    }

    /**
     * Convert the specified (unqualified) file name into something
     * that should be palatable as a file name
     * (i.e. replace invalid characters with escape sequences).
     */
    protected String normalizeFileName(String unqualifiedFileName) {
        return this.getFileNameNormalizer().normalize(unqualifiedFileName);
    }

    /**
     * All the XML documents will be stored in subdirectories
     * under the specified base directory.
     */
    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * All the XML documents will be stored in subdirectories
     * under the specified base directory.
     */
    public void setBaseDirectoryName(String baseDirectoryName) {
        this.setBaseDirectory(this.buildDirectory(baseDirectoryName));
    }

    /**
     * Set whether the policy will create directories
     * as needed, if they do not exist already.
     */
    public void setCreatesDirectoriesAsNeeded(boolean createsDirectoriesAsNeeded) {
        this.createsDirectoriesAsNeeded = createsDirectoriesAsNeeded;
    }

    /**
     * Set the standard file name extension that will be
     * appended to the primary key element value(s) to generate the
     * complete file name.
     */
    public void setFileNameExtension(String fileNameExtension) {
        this.fileNameExtension = fileNameExtension;
    }

    /**
     * Set the normalizer that will convert invalid file names
     * into something usable.
     */
    public void setFileNameNormalizer(FileNameNormalizer fileNameNormalizer) {
        this.fileNameNormalizer = fileNameNormalizer;
    }

    /**
     * Return a file for the temporary directory.
     */
    protected File tempDirectory() {
        return this.buildDirectory(Helper.tempDirectory());
    }

    /**
     * Call <code>#toString(PrintWriter)</code>, to allow subclasses to
     * insert additional information.
     */
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.write(Helper.getShortClassName(this));
        pw.write("(");
        this.toString(pw);
        pw.write(")");
        return sw.toString();
    }

    /**
     * Append more information to the writer.
     */
    protected void toString(PrintWriter pw) {
        pw.print(this.getBaseDirectory());
    }

    // ********** inner classes **********

    /**
     * This file filter will "accept" all the normal files that have the file
     * extension specified in the constructor.
     */
    protected class ExtensionFileFilter implements FileFilter {
        protected String fileNameExtension;

        public ExtensionFileFilter(String fileNameExtension) {
            this.fileNameExtension = fileNameExtension;
        }

        public boolean accept(File file) {
            return this.fileIsValid(file) && file.getName().endsWith(fileNameExtension);
        }

        protected boolean fileIsValid(File file) {
            return file.isFile();
        }
    }
}