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
import deprecated.xml.zip.*;
import org.eclipse.persistence.internal.localization.TraceLocalization;

/**
 * <code>XMLCall</code> does not do much. But it does provide a number of convenience methods for its
 * assorted subclasses and begins the transition from database "fields" to XML "elements".
 * It also provides for a stream policy that supplies the necessary streams for reading and
 * writing XML documents.
 * <p>Subclasses are still required to implement <code>#execute(Record, Accessor)</code>.
 *
 * @see SDKQueryMechanism
 * @see XMLAccessorStreamPolicy
 * @see DefaultXMLTranslator
 * @see org.eclipse.persistence.queries.DatabaseQuery
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public abstract class XMLCall extends deprecated.sdk.AbstractSDKCall {

    /** Allow the call to override the default stream source, which is the accessor. */
    private XMLStreamPolicy streamPolicy;

    /** This will translate an XML document to/from a database row. */
    private XMLTranslator xmlTranslator;

    /**
     * Default constructor.
     */
    protected XMLCall() {
        super();
    }

    /**
     * Build and return the default stream policy.
     */
    protected XMLStreamPolicy buildDefaultStreamPolicy() {
        return new XMLAccessorStreamPolicy();
    }

    /**
     * Build and return the a stream policy for the specified file.
     */
    protected XMLStreamPolicy buildStreamPolicy(File file) {
        return new XMLFileStreamPolicy(file);
    }

    /**
     * Build and return the a stream policy for the specified files.
     */
    protected XMLStreamPolicy buildFileStreamPolicy(Enumeration files) {
        return new XMLFileStreamPolicy(files);
    }

    /**
     * Build and return the a stream policy for the specified file.
     */
    protected XMLStreamPolicy buildStreamPolicy(File zipFile, String zipEntryName) {
        return new XMLZipFileStreamPolicy(zipFile, zipEntryName);
    }

    /**
     * Build and return the a stream policy for the specified file.
     */
    protected XMLStreamPolicy buildStreamPolicy(File zipFile, Enumeration zipEntryNames) {
        return new XMLZipFileStreamPolicy(zipFile, zipEntryNames);
    }

    /**
     * Build and return the a stream policy for the specified read stream.
     */
    protected XMLStreamPolicy buildStreamPolicy(Reader reader) {
        return new XMLStreamStreamPolicy(reader);
    }

    /**
     * Build and return the a stream policy for the specified write stream.
     */
    protected XMLStreamPolicy buildStreamPolicy(Writer writer) {
        return new XMLStreamStreamPolicy(writer);
    }

    /**
     * Build and return the a stream policy for the specified streams.
     */
    protected XMLStreamPolicy buildStreamStreamPolicy(Enumeration readers) {
        return new XMLStreamStreamPolicy(readers);
    }

    /**
     * Convenience method.
     * Return the primary key elements, in the same order
     * as they are stored in the descriptor.
     */
    protected Vector getOrderedPrimaryKeyElements() {
        return this.getOrderedPrimaryKeyFields();
    }

    /**
     * Convenience method.
     * Return the name of the root element for the call.
     */
    protected String getRootElementName() {
        return this.getTableName();
    }

    /**
     * Return the stream policy.
     */
    public XMLStreamPolicy getStreamPolicy() {
        return streamPolicy;
    }

    /**
     * Return the XML translator.
     */
    public XMLTranslator getXMLTranslator() {
        if (xmlTranslator == null) {
            return ((XMLAccessor)this.getAccessor()).getXMLTranslator();
        }
        return xmlTranslator;
    }

    /**
     * Initialize the newly-created instance.
     */
    protected void initialize() {
        super.initialize();
        this.setStreamPolicy(this.buildDefaultStreamPolicy());
    }

    /**
     * Set the file to be wrapped by the stream.
     */
    public void setFile(File file) {
        this.setStreamPolicy(this.buildStreamPolicy(file));
    }

    /**
     * Set the file to be wrapped by the stream.
     */
    public void setFile(File zipFile, String zipEntryName) {
        this.setStreamPolicy(this.buildStreamPolicy(zipFile, zipEntryName));
    }

    /**
     * Set the file to be wrapped by the stream.
     */
    public void setFileName(String fileName) {
        this.setFile(new File(fileName));
    }

    /**
     * Set the file to be wrapped by the stream.
     */
    public void setFileName(String zipFileName, String zipEntryName) {
        this.setFile(new File(zipFileName), zipEntryName);
    }

    /**
     * Set the files to be wrapped by the streams.
     */
    public void setFiles(Enumeration files) {
        this.setStreamPolicy(this.buildFileStreamPolicy(files));
    }

    /**
     * Set the files to be wrapped by the stream.
     */
    public void setFiles(File zipFile, Enumeration zipEntryNames) {
        this.setStreamPolicy(this.buildStreamPolicy(zipFile, zipEntryNames));
    }

    /**
     * Set the stream.
     */
    public void setReader(Reader reader) {
        this.setStreamPolicy(this.buildStreamPolicy(reader));
    }

    /**
     * Set the read streams.
     */
    public void setReaders(Enumeration readers) {
        this.setStreamPolicy(this.buildStreamStreamPolicy(readers));
    }

    /**
     * Set the stream.
     */
    public void setWriter(Writer writer) {
        this.setStreamPolicy(this.buildStreamPolicy(writer));
    }

    /**
     * Set the stream policy.
     */
    public void setStreamPolicy(XMLStreamPolicy streamPolicy) {
        this.streamPolicy = streamPolicy;
    }

    /**
     * Set the XML translator.
     */
    public void setXMLTranslator(XMLTranslator xmlTranslator) {
        this.xmlTranslator = xmlTranslator;
    }

    /**
     * Append a string describing the call to the specified writer.
     */
    protected void writeLogDescription(PrintWriter writer) {
        writer.write(TraceLocalization.buildMessage("XML_call", (Object[])null));
    }
}