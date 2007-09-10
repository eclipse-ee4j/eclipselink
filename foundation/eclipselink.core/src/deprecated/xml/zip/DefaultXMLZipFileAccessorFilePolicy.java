/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml.zip;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import deprecated.xml.*;

/**
 * Default implementation of accessor file policy.
 *
 * @see XMLZipFileAccessor
 *
 * @author Big Country
 * @since TOPLink/Java 4.5
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class DefaultXMLZipFileAccessorFilePolicy extends DefaultXMLFileAccessorFilePolicy {

    /** The extension to be appended to the generated ZIP file names. */
    private String zipFileNameExtension;
    public static String DEFAULT_ZIP_FILE_NAME_EXTENSION = ".zip";

    // constructors

    /**
     * Default constructor.
     */
    public DefaultXMLZipFileAccessorFilePolicy() {
        super();
    }

    // instance methods

    /**
     * Return the default standard file name extension that will be
     * appended to the root element name to generate the
     * complete ZIP file name.
     */
    protected String buildDefaultZipFileNameExtension() {
        return DEFAULT_ZIP_FILE_NAME_EXTENSION;
    }

    /**
     * Return a file filter that will return true for any
     * ZIP entry that has the appropriate file extension (.xml, by default).
     * Override to build a ZIP filter.
     */
    protected FileFilter buildExtensionFileFilter(String fileNameExtension) {
        return new ZipExtensionFileFilter(fileNameExtension);
    }

    /**
     * Check whether the specified document directory exists.
     * If appropriate, create it.
     * Override to check for the appropriate ZIP file.
     */
    protected void checkDocumentDirectory(File directory) throws XMLDataStoreException {
        // need more helpful exceptions here...
        File zipFile = this.convertToZipFile(directory);
        super.checkDocumentDirectory(zipFile.getParentFile());
        if (zipFile.exists()) {
            if (!zipFile.isFile()) {
                throw XMLDataStoreException.notADirectory(zipFile);
            }
        } else {
            if (this.createsDirectoriesAsNeeded()) {
                try {
                    if (!zipFile.createNewFile()) {
                        throw XMLDataStoreException.directoryCouldNotBeCreated(zipFile);
                    }
                } catch (IOException ex) {
                    throw XMLDataStoreException.directoryCouldNotBeCreated(zipFile);
                }
            } else {
                throw XMLDataStoreException.directoryNotFound(zipFile);
            }
        }
    }

    /**
     * Convert the specified ZIP file into the corresponding directory.
     */
    protected File convertToDirectory(File zipFile) {
        File parentDirectory = zipFile.getParentFile();
        String subDirectoryName = zipFile.getName();
        if (subDirectoryName.endsWith(this.getZipFileNameExtension())) {
            // chop off the extension
            subDirectoryName = subDirectoryName.substring(0, subDirectoryName.length() - this.getZipFileNameExtension().length());
        }
        return this.buildFile(parentDirectory, subDirectoryName);
    }

    /**
     * Convert the specified directory into the corresponding ZIP file.
     */
    protected File convertToZipFile(File directory) {
        File zipDirectory = directory.getParentFile();
        String zipFileName = directory.getName() + this.getZipFileNameExtension();
        return this.buildFile(zipDirectory, zipFileName);
    }

    /**
     * Create the specified directory if necessary.
     * Override to create the appropriate ZIP file.
     */
    protected void createFileSource(File directory) throws XMLDataStoreException {
        // need more helpful exceptions here...
        File zipFile = this.convertToZipFile(directory);
        super.createFileSource(zipFile.getParentFile());
        if (zipFile.exists()) {
            if (!zipFile.isFile()) {
                throw XMLDataStoreException.notADirectory(zipFile);
            }
        } else {
            try {
                if (!zipFile.createNewFile()) {
                    throw XMLDataStoreException.directoryCouldNotBeCreated(zipFile);
                }
            } catch (IOException ex) {
                throw XMLDataStoreException.directoryCouldNotBeCreated(zipFile);
            }
        }
    }

    /**
     * Drop the specified directory if necessary.
     * Override to simply delete the appropriate ZIP file.
     */
    protected void dropFileSource(File directory) throws XMLDataStoreException {
        // need more helpful exceptions here...
        File zipFile = this.convertToZipFile(directory);
        if (zipFile.exists()) {
            if (zipFile.isFile()) {
                zipFile.delete();
            } else {
                throw XMLDataStoreException.notADirectory(zipFile);
            }
        }
    }

    /**
     * Return the standard file name extension that will be
     * appended to the root element name to generate the
     * complete ZIP file name.
     */
    public String getZipFileNameExtension() {
        return zipFileNameExtension;
    }

    /**
     * Return an array of the files in the specified directory that
     * match the specified filter.
     * Override to convert the ZIP file entries into files,
     * for later conversion back into ZIP file entries(!).
     */
    protected File[] listFiles(File directory, FileFilter fileFilter) throws XMLDataStoreException {
        Collection files = new ArrayList();
        try {
            ZipFile zipFile = new ZipFile(this.convertToZipFile(directory));
            for (Enumeration stream = zipFile.entries(); stream.hasMoreElements();) {
                ZipEntry entry = (ZipEntry)stream.nextElement();
                File entryFile = new File(directory, entry.getName());
                if (fileFilter.accept(entryFile)) {
                    files.add(entryFile);
                }
            }
            zipFile.close();
        } catch (IOException ex) {
            throw XMLDataStoreException.ioException(ex);
        }
        return (File[])files.toArray(new File[files.size()]);
    }

    /**
     *    Initialize the policy:<ul>
     * <li>initialize the ZIP file extension
     * </ul>
     * Extend to initalize ZIP stuff.
     */
    protected void initialize() {
        super.initialize();
        this.setZipFileNameExtension(this.buildDefaultZipFileNameExtension());
    }

    /**
     * Set the standard file name extension that will be
     * appended to the root element name to generate the
     * complete ZIP file name.
     */
    public void setZipFileNameExtension(String zipFileNameExtension) {
        this.zipFileNameExtension = zipFileNameExtension;
    }

    // inner classes

    /**
     * This file filter extends the one found in
     * <code>DefaultXMLFileAccessorFilePolicy</code>.
     */
    protected class ZipExtensionFileFilter extends ExtensionFileFilter {
        public ZipExtensionFileFilter(String fileNameExtension) {
            super(fileNameExtension);
        }

        // override to always return true...
        protected boolean fileIsValid(File file) {
            return true;
        }
    }
}