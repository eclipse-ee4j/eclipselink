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

import org.eclipse.persistence.sessions.Record;
import deprecated.xml.*;
import org.eclipse.persistence.internal.databaseaccess.Accessor;

/**
 * This implementation of the <code>XMLStreamPolicy</code> interface
 * provides streams to XML documents stored in ZIP file archives.
 * It also controls the access to these ZIP files: adding, deleting,
 * and updating the entries in the ZIP files.
 *
 * @see XMLZipFileAccessor
 *
 * @author Les Davis
 * @since TOPLink/Java 3.5
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLZipFileStreamPolicy implements XMLStreamPolicy {

    /** The ZIP file containing the entry (or entries) to be
        wrapped by the stream. */
    private File file;

    /** Another reference to the ZIP file containing the entry
        (or entries) to be wrapped by the stream. This is
        only instantiated on demand. */
    private ZipFile zipFile;

    /** The name of the single entry to be wrapped by the stream. */
    private String zipEntryName;

    /** An enumeration of names of entries to be wrapped by streams. */
    private Enumeration zipEntryNames;

    /**
     * Default constructor.
     */
    protected XMLZipFileStreamPolicy() {
        super();
    }

    /**
     * Construct a policy for the specified ZIP file and entry.
     */
    public XMLZipFileStreamPolicy(File file, String zipEntryName) {
        this();
        this.file = file;
        this.zipEntryName = zipEntryName;
    }

    /**
     * Construct a policy for the specified ZIP file and entry.
     */
    public XMLZipFileStreamPolicy(String fileName, String zipEntryName) {
        this(new File(fileName), zipEntryName);
    }

    /**
     * Construct a policy for the specified ZIP file and entry.
     */
    public XMLZipFileStreamPolicy(File file, Enumeration zipEntryNames) {
        this();
        this.file = file;
        this.zipEntryNames = zipEntryNames;
    }

    /**
     * Construct a policy for the specified ZIP file and entries.
     */
    public XMLZipFileStreamPolicy(String fileName, Enumeration zipEntryNames) {
        this(new File(fileName), zipEntryNames);
    }

    /**
     * Return the ZIP file entry wrapped in a stream.
     */
    protected Reader buildReadStream() throws XMLDataStoreException {
        return this.buildReadStream(this.getZipEntryName());
    }

    /**
     * Return the specified ZIP file entry wrapped in a stream.
     */
    protected Reader buildReadStream(String readZipEntryName) throws XMLDataStoreException {
        try {
            ZipInputStream inStream = this.buildZipInputStream();
            ZipEntry entry = inStream.getNextEntry();
            while (entry != null) {
                if (readZipEntryName.equals(entry.getName())) {
                    return new InputStreamReader(inStream);
                }
                entry = inStream.getNextEntry();
            }
            inStream.close();
        } catch (IOException ex) {
            throw XMLDataStoreException.ioException(ex);
        }
        return null;
    }

    /**
     * Build and return streams for the ZIP file entries.
     */
    protected Enumeration buildReadStreams() {
        return new Enumeration() {
                public boolean hasMoreElements() {
                    return XMLZipFileStreamPolicy.this.getZipEntryNames().hasMoreElements();
                }

                public Object nextElement() throws XMLDataStoreException {
                    return XMLZipFileStreamPolicy.this.buildReadStream((String)XMLZipFileStreamPolicy.this.getZipEntryNames().nextElement());
                }
            };
    }

    /**
     * Build and return a temporary file in the same directory
     * and with the same extension as the current ZIP file.
     */
    protected File buildTempFile() throws IOException {
        return this.buildTempFile(this.getFile());
    }

    /**
     * Build and return a temporary file in the same directory
     * and with the same extension as the specified file.
     */
    protected File buildTempFile(File file) throws IOException {
        int i = file.getName().lastIndexOf('.');
        String ext = ((i == -1) ? ".zip" : file.getName().substring(i));
        File tempFile = File.createTempFile("temp", ext, file.getParentFile());
        return tempFile;
    }

    /**
     * Rename the ZIP file to a temporary file, then copy its
     * entries back into the original ZIP file, leaving the stream
     * open and returning it.
     */
    protected Writer buildWriteStream(boolean entryShouldExist) throws XMLDataStoreException {
        try {
            File inFile = this.buildTempFile();
            inFile.delete();// the file actually gets created - delete it
            boolean renameSucceeded = this.getFile().renameTo(inFile);
            ZipInputStream inStream = this.buildZipInputStream(inFile);
            ZipOutputStream outStream = this.buildZipOutputStream();

            boolean entryActuallyExists = false;
            byte[] buffer = new byte[32768];
            ZipEntry entry = inStream.getNextEntry();
            while (entry != null) {
                boolean weWantToWriteTheEntry = true;
                if (this.getZipEntryName().equals(entry.getName())) {
                    entryActuallyExists = true;

                    // if we were expecting the entry, skip it;
                    // if we were NOT expecting the entry, allow it to be rewritten
                    // so the file is restored to its original condition
                    if (entryShouldExist) {
                        weWantToWriteTheEntry = false;
                    }
                }
                if (weWantToWriteTheEntry) {
                    outStream.putNextEntry(entry);
                    int byteCount;
                    while ((byteCount = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, byteCount);
                    }
                    outStream.closeEntry();
                }
                entry = inStream.getNextEntry();
            }

            // close and delete the temporary file
            inStream.close();
            inFile.delete();
            // check for invalid state
            if (entryShouldExist != entryActuallyExists) {
                outStream.close();// close it since we will not be returning it
                // need more helpful exceptions here
                if (entryActuallyExists) {
                    throw XMLDataStoreException.fileAlreadyExists(new File(this.getZipEntryName()));
                } else {
                    throw XMLDataStoreException.fileNotFound(new File(this.getZipEntryName()), null);
                }
            }
            outStream.putNextEntry(new ZipEntry(this.getZipEntryName()));
            return new OutputStreamWriter(outStream);
        } catch (IOException ex) {
            throw XMLDataStoreException.ioException(ex);
        }
    }

    /**
     * Build and return a ZIP input stream on the current ZIP file.
     */
    protected ZipInputStream buildZipInputStream() throws FileNotFoundException {
        return this.buildZipInputStream(this.getFile());
    }

    /**
     * Build and return a ZIP input stream on the specified ZIP file.
     */
    protected ZipInputStream buildZipInputStream(File file) throws FileNotFoundException {
        return new ZipInputStream(new FileInputStream(file));
    }

    /**
     * Build and return a ZIP output stream on the current ZIP file.
     */
    protected ZipOutputStream buildZipOutputStream() throws FileNotFoundException {
        return this.buildZipOutputStream(this.getFile());
    }

    /**
     * Build and return a ZIP output stream on the specified ZIP file.
     */
    protected ZipOutputStream buildZipOutputStream(File file) throws FileNotFoundException {
        return new ZipOutputStream(new FileOutputStream(file));
    }

    /**
     * Loop through the ZIP file, copying its entries
     * into a new, temporary ZIP file, skipping the entry to be deleted.
     * Then replace the original file with the new one.
     */
    protected Integer deleteEntry() throws XMLDataStoreException {
        try {
            ZipInputStream inStream = this.buildZipInputStream();
            File outFile = this.buildTempFile();
            ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(outFile));

            byte[] buffer = new byte[32768];
            int inCount = 0;
            int outCount = 0;

            // copy all the entries except the one to be deleted
            ZipEntry entry = inStream.getNextEntry();
            while (entry != null) {
                inCount++;
                if (!this.getZipEntryName().equals(entry.getName())) {
                    outCount++;
                    outStream.putNextEntry(entry);
                    int byteCount;
                    while ((byteCount = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, byteCount);
                    }
                    outStream.closeEntry();
                }
                entry = inStream.getNextEntry();
            }
            inStream.close();
            if (outCount == 0) {
                // add a dummy record to an empty file so we can close it
                // this is required by ZipOutputStream
                outStream.putNextEntry(new ZipEntry("delete.txt"));
                outStream.write("This file is a place-holder. The containing ZIP file should be deleted.".getBytes());
                outStream.closeEntry();
            }
            outStream.close();
            if (outCount == inCount) {
                // no entries were removed - just delete the temp file
                outFile.delete();
            } else {
                // at least one entry removed - delete the original file
                this.getFile().delete();
                if (outCount == 0) {
                    // NO entries remain - just delete the temp file too
                    outFile.delete();
                } else {
                    // entries remain - replace original file with temp file
                    outFile.renameTo(this.getFile());
                }
            }
            return new Integer(inCount - outCount);// should be 0 or 1
        } catch (IOException ex) {
            throw XMLDataStoreException.ioException(ex);
        }
    }

    /**
     * This implementation loops through the ZIP file, copying its entries
     * into a new ZIP file, skipping the entry to be deleted.
     */
    public Integer deleteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.deleteEntry();
    }

    /**
     * Return the ZIP file entry wrapped in a stream.
     */
    public Reader getExistenceCheckStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.getReadStream(rootElementName, row, orderedPrimaryKeyElements, accessor);
    }

    /**
     * This implementation removes and adds an entry to the ZIP file and
     * positions the stream at the end of the ZIP file.
     */
    public Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.buildWriteStream(true);
    }

    /**
     * This implementation adds an entry to the ZIP file and
     * positions the stream at the end of the ZIP file.
     */
    public Writer getNewWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.buildWriteStream(false);
    }

    /**
     * Return the ZIP file entry wrapped in a stream.
     */
    public Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.buildReadStream();
    }

    /**
     * Return the ZIP file entries wrapped in streams.
     */
    public Enumeration getReadStreams(String rootElementName, Vector foreignKeys, Vector orderedForeignKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.buildReadStreams();
    }

    /**
     * Return the ZIP file entries wrapped in streams.
     */
    public Enumeration getReadStreams(String rootElementName, Accessor accessor) throws XMLDataStoreException {
        return this.buildReadStreams();
    }

    /**
     * Return the ZIP file that contains the entry(ies) to be
     * used for the stream(s).
     */
    protected File getFile() {
        return file;
    }

    /**
     * Return the name of the entry in the ZIP file to be used for the stream.
     */
    protected String getZipEntryName() {
        return zipEntryName;
    }

    /**
     * Return the names of the entries in the ZIP file to be used for the streams.
     */
    protected Enumeration getZipEntryNames() {
        return zipEntryNames;
    }

    /**
     * Return the ZIP file that contains the entry(ies) to be
     * used for the stream(s).
     */
    protected ZipFile getZipFile() throws IOException {
        if (zipFile == null) {
            zipFile = new ZipFile(this.getFile());
        }
        return zipFile;
    }
}