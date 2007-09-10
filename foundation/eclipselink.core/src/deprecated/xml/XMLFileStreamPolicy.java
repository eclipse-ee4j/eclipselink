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

import java.io.*;
import java.util.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.localization.ToStringLocalization;
import org.eclipse.persistence.sessions.Record;

/**
 * This implementation of the <code>XMLStreamPolicy</code> interface
 * simply wraps the supplied file (or files) in the appropriate stream (or
 * enumeration of streams).
 *
 * @see XMLCall
 * @see XMLAccessor
 * @see XMLFileAccessor
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLFileStreamPolicy implements XMLStreamPolicy {

    /** The single file to be wrapped by the stream. */
    private File file;

    /** An enumeration of files to be wrapped by streams. */
    private Enumeration files;

    /** Performance tweak: The maximum size of the read buffer used when reading XML files. */
    public static int MAX_READ_BUFFER_SIZE = 32768;

    /** Performance tweak: The minimum and maximum sizes of the write buffer used when writing XML files. */
    public static int MIN_WRITE_BUFFER_SIZE = 1024;
    public static int MAX_WRITE_BUFFER_SIZE = 32768;

    /**
     * Default constructor.
     */
    protected XMLFileStreamPolicy() {
        super();
    }

    /**
     * Construct a policy for the specified file.
     */
    public XMLFileStreamPolicy(File file) {
        this();
        this.file = file;
    }

    /**
     * Construct a policy for the specified file.
     */
    public XMLFileStreamPolicy(String fileName) {
        this(new File(fileName));
    }

    /**
     * Construct a policy for the specified files.
     */
    public XMLFileStreamPolicy(Enumeration files) {
        this();
        this.files = files;
    }

    /**
     * Return a read stream on the policy's file.
     */
    protected Reader buildReadStream() {
        return this.buildReadStream(this.getFile());
    }

    /**
     * Return a read stream on the specified file.
     */
    protected Reader buildReadStream(File readFile) {
        int bufferSize = this.calculateReadBufferSize(readFile);
        try {
            return new BufferedReader(new FileReader(readFile), bufferSize);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Return an enumeration on a collection of read streams,
     * one for every file in the policy's enumeration of files.
     */
    protected Enumeration buildReadStreams() throws XMLDataStoreException {
        return new Enumeration() {
                public boolean hasMoreElements() {
                    return XMLFileStreamPolicy.this.getFiles().hasMoreElements();
                }

                public Object nextElement() {
                    return XMLFileStreamPolicy.this.buildReadStream((File)XMLFileStreamPolicy.this.getFiles().nextElement());
                }
            };
    }

    /**
     * Calculate and return the size of the buffer to be allocated
     * for reading the specified file.
     */
    protected int calculateReadBufferSize(File readFile) {
        int bufferSize = MAX_READ_BUFFER_SIZE;
        long fileLength = readFile.length();
        if ((fileLength < bufferSize) && (fileLength != 0)) {
            bufferSize = (int)fileLength;// don't allocate any more than necessary
        }
        return bufferSize;
    }

    /**
     * Calculate and return the size of the buffer to be allocated
     * for writing to the specified file.
     */
    protected int calculateWriteBufferSize(File writeFile, boolean fileActuallyExists) {
        int bufferSize = MAX_WRITE_BUFFER_SIZE;

        // if the file already exists, use its current size to estimate a buffer size
        if (fileActuallyExists) {
            long estimate = writeFile.length() * 2;
            if ((estimate < bufferSize) && (estimate != 0)) {
                bufferSize = (int)estimate;
            }
            if (bufferSize < MIN_WRITE_BUFFER_SIZE) {
                bufferSize = MIN_WRITE_BUFFER_SIZE;
            }
        }
        return bufferSize;
    }

    /**
     * Delete the data for the specified root element and primary key.
     * Return the stream count (1 or 0).
     * Simply delete the file, if it exists.
     */
    public Integer deleteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        if (this.getFile().exists()) {
            this.getFile().delete();
            return new Integer(1);
        } else {
            return new Integer(0);
        }
    }

    /**
     * If it exists, return a read stream on the data for the specified
     * root element and primary key.
     * If it does not exist, return null.
     */
    public Reader getExistenceCheckStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.getReadStream(rootElementName, row, orderedPrimaryKeyElements, accessor);
    }

    /**
     * Return a write stream that will overwrite the data for the specified
     * root element and primary key.
     */
    public Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.getWriteStream(true);
    }

    /**
     * Return the file used for the stream.
     */
    protected File getFile() {
        return file;
    }

    /**
     * Return the files used for the streams.
     */
    protected Enumeration getFiles() {
        return files;
    }

    /**
     * Return a new write stream for the specified
     * root element and primary key.
     */
    public Writer getNewWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.getWriteStream(false);
    }

    /**
     * Return a read stream on the data for the specified
     * root element and primary key.
     * If the stream is not found return null.
     */
    public Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.buildReadStream();
    }

    /**
     * Return an enumeration on a collection of streams,
     * one for every specified foreign key.
     * If a particular stream is not found the
     * enumeration will return null in its place.
     */
    public Enumeration getReadStreams(String rootElementName, Vector foreignKeys, Vector orderedForeignKeyElements, Accessor accessor) throws XMLDataStoreException {
        return this.buildReadStreams();
    }

    /**
     * Return an enumeration on a collection of read streams,
     * one for *every* document with the specified root element.
     */
    public Enumeration getReadStreams(String rootElementName, Accessor accessor) throws XMLDataStoreException {
        return this.buildReadStreams();
    }

    /**
     * Return a write stream for the file, checking whether
     * the stream already exists.
     */
    protected Writer getWriteStream(boolean fileShouldExist) throws XMLDataStoreException {
        boolean fileActuallyExists = this.getFile().exists();
        if (fileShouldExist != fileActuallyExists) {
            if (fileActuallyExists) {
                throw XMLDataStoreException.fileAlreadyExists(this.getFile());
            } else {
                throw XMLDataStoreException.fileNotFound(this.getFile(), null);
            }
        }

        // calculate buffer size beforehand - because the file size is set to zero once the FileWriter is created
        int bufferSize = this.calculateWriteBufferSize(this.getFile(), fileActuallyExists);
        try {
            return new BufferedWriter(new FileWriter(this.getFile()), bufferSize);
        } catch (IOException e) {
            throw XMLDataStoreException.unableToCreateWriteStream(this.getFile(), e);
        }
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        writer.write(org.eclipse.persistence.internal.helper.Helper.getShortClassName(this));
        writer.write("(");

        if ((this.getFile() == null) && (this.getFiles() == null)) {
            writer.write(ToStringLocalization.buildMessage("no_files", (Object[])null));
        } else if (this.getFile() == null) {
            writer.write(ToStringLocalization.buildMessage("mulitple_files", (Object[])null));
        } else {
            writer.write(this.getFile().toString());
        }

        writer.write(")");
        return sw.toString();
    }
}