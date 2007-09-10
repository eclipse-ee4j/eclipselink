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

import java.io.*;
import java.util.*;
import deprecated.xml.*;

/**
 * This class extends the <code>XMLFileAccessor</code> class;
 * supplanting subdirectories with ZIP files. i.e. The base directory
 * will contain a collection of ZIP files, each containing the
 * files that would normally be held in the corresponding
 * subdirectories.
 *
 * @author Les Davis
 * @since TopLink/Java 3.5
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLZipFileAccessor extends XMLFileAccessor {
    // constructors

    /**
     * Default constructor.
     */
    public XMLZipFileAccessor() {
        super();
    }

    // instance methods

    /**
     * Override because multiple file policies is unsupported.
     */
    public void addFilePolicy(String rootElementName, XMLFileAccessorFilePolicy filePolicy) {
        throw new UnsupportedOperationException();
    }

    /**
     * Override to return the ZIP version of the file policy.
     */
    protected XMLFileAccessorFilePolicy buildDefaultDefaultFilePolicy() {
        return new DefaultXMLZipFileAccessorFilePolicy();
    }

    /**
     * Build and return a stream policy for the specified files.
     * Override to convert the normal files into a ZIP file
     * and entry names.
     */
    protected XMLStreamPolicy buildStreamPolicy(Enumeration files) {
        File zipFile = null;
        Vector zipEntryNames = new Vector();
        while (files.hasMoreElements()) {
            File file = (File)files.nextElement();
            if (zipFile == null) {
                zipFile = this.convertToZipFile(file.getParentFile());
            }
            zipEntryNames.addElement(file.getName());
        }
        return this.buildStreamPolicy(zipFile, zipEntryNames.elements());
    }

    /**
     * Build and return a stream policy for the specified file.
     * Override to convert the normal file into a ZIP file
     * and entry name.
     */
    protected XMLStreamPolicy buildStreamPolicy(File file) {
        return this.buildStreamPolicy(this.convertToZipFile(file.getParentFile()), file.getName());
    }

    /**
     * Build and return a stream policy for the specified ZIP file
     * and entries.
     */
    protected XMLStreamPolicy buildStreamPolicy(File zipFile, Enumeration zipEntryNames) {
        return new XMLZipFileStreamPolicy(zipFile, zipEntryNames);
    }

    /**
     * Build and return a stream policy for the specified ZIP file
     * and entry.
     */
    protected XMLStreamPolicy buildStreamPolicy(File zipFile, String zipEntryName) {
        return new XMLZipFileStreamPolicy(zipFile, zipEntryName);
    }

    /**
     * Convert the specified ZIP file into the corresponding directory.
     */
    protected File convertToDirectory(File zipFile) {
        return this.getZipFilePolicy().convertToDirectory(zipFile);
    }

    /**
     * Convert the specified directory into the corresponding ZIP file.
     */
    protected File convertToZipFile(File directory) {
        return this.getZipFilePolicy().convertToZipFile(directory);
    }

    /**
     * Return the ZIP-specific file policy.
     */
    protected DefaultXMLZipFileAccessorFilePolicy getZipFilePolicy() {
        return (DefaultXMLZipFileAccessorFilePolicy)this.getDefaultFilePolicy();
    }
}