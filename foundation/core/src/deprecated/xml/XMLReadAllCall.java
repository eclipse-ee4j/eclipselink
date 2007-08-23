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
import deprecated.sdk.SDKObjectCollectionMapping;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * XMLReadAllCall can perform one of two types of read:<ol>
 * <li>Read <em>all</em> the XML documents for a given root element name.
 * <li>Read the XML documents for a given set of foreign keys,
 * specified by a 1:n mapping relationship.
 * </ol>
 *
 * @see deprecated.sdk.SDKObjectCollectionMapping
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLReadAllCall extends XMLCall {

    /** If this is present, read only the data for the relationship. */
    private SDKObjectCollectionMapping mapping;

    /**
     * Default constructor.
     */
    public XMLReadAllCall() {
        super();
    }

    /**
     * Constructor. Specify the associated mapping.
     */
    public XMLReadAllCall(SDKObjectCollectionMapping mapping) {
        this();
        this.initialize(mapping);
    }

    /**
     * Read and return the necessary rows of data.
     * If the mapping is missing, this call will simply read *all*
     * the XML documents.
     * If the mapping is present, this call will read all
     * the XML documents for the keys specified in the "nested" rows.
     */
    public Object execute(AbstractRecord translationRow, Accessor accessor) throws XMLDataStoreException {
        Enumeration stream = null;
        if (this.getMapping() == null) {
            // read everything
            stream = this.getStreamPolicy().getReadStreams(this.getRootElementName(), accessor);
        } else {
            // read the data for the "nested" rows
            stream = this.getStreamPolicy().getReadStreams(this.getRootElementName(), this.extractForeignKeyRows(translationRow), this.getOrderedForeignKeyElements(), accessor);
        }

        Vector result = new Vector();

        while (stream.hasMoreElements()) {
            Reader xmlStream = (Reader)stream.nextElement();
            if (xmlStream != null) {
            	Record row = this.getXMLTranslator().read(xmlStream);
                result.addElement(this.getFieldTranslator().translateForRead(row));
            }
        }
        return result;
    }

    /**
     * Extract the collection of rows holding the
     * foreign keys from the specified row.
     */
    protected Vector extractForeignKeyRows(AbstractRecord translationRow) {
        return this.getMapping().extractForeignKeyRows(translationRow, this.getSession());
    }

    /**
     * Return the mapping the call fetches data for.
     */
    protected SDKObjectCollectionMapping getMapping() {
        return mapping;
    }

    /**
     * Convenience method.
     * Return the appropriate foreign key elements,
     * in the same order as they are stored in the
     * descriptor.
     */
    protected Vector getOrderedForeignKeyElements() {
        return this.getMapping().getOrderedForeignKeyFields();
    }

    protected void initialize(SDKObjectCollectionMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * Set the mapping the call fetches data for.
     */
    public void setMapping(SDKObjectCollectionMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * Append call-specific data to the specified writer.
     */
    protected void writeLogData(PrintWriter writer) {
        if (this.getMapping() != null) {
            Vector foreignKeyRows = this.extractForeignKeyRows(this.getTranslationRow());
            for (Enumeration rows = foreignKeyRows.elements(); rows.hasMoreElements();) {
                writer.write(Helper.cr());
                writer.write("\t");
                this.write((AbstractRecord)rows.nextElement(), writer, 0);
            }
        }
    }

    /**
     * Append a string describing the call to the specified writer.
     */
    protected void writeLogDescription(PrintWriter writer) {
        writer.write(TraceLocalization.buildMessage("XML_read_all", (Object[])null));
        if (this.getMapping() != null) {
            writer.write("(");
            writer.print(this.getMapping());
            writer.write(")");
        }
    }
}