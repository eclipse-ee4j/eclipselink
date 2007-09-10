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
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * XMLDataReadCall reads up the specified data and, if necessary,
 * extracts the specified result elements and converts them to
 * the specified types.
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLDataReadCall extends XMLDataCall {

    /** The elements to be returned to the caller. */
    private Vector resultElements;

    /** The types the returned elements should be converted to, if necessary. */
    private Hashtable resultElementTypes;

    /**
     * Default constructor.
     */
    public XMLDataReadCall() {
        super();
    }

    /**
     * Add a result element.
     */
    protected void addResultElement(DatabaseField resultElement) {
        this.getResultElements().addElement(resultElement);
    }

    /**
     * Add a result element name.
     */
    public void addResultElementName(String resultElementName) {
        this.addResultElement(new DatabaseField(resultElementName, this.getRootElement()));
    }

    /**
     * Add a result element type.
     */
    public void addResultElementType(String resultElementName, Class resultElementType) {
        this.getResultElementTypes().put(new DatabaseField(resultElementName, this.getRootElement()), resultElementType);
    }

    /**
     * Clear out the result elements.
     */
    protected void clearResultElements() {
        this.getResultElements().removeAllElements();
    }

    /**
     * Read and return the necessary rows of data.
     * The translation row holds the primary key for the data.
     */
    public Object execute(AbstractRecord translationRow, Accessor accessor) throws XMLDataStoreException {
        Enumeration stream = null;
        if (this.getOrderedPrimaryKeyElements().isEmpty()) {
            // read everything
            stream = this.getStreamPolicy().getReadStreams(this.getRootElementName(), accessor);
        } else {
            // read the data for the specified primary key
            Vector temp = new Vector(1);
            Object readStream = this.getStreamPolicy().getReadStream(this.getRootElementName(), translationRow, this.getOrderedPrimaryKeyElements(), accessor);
            if (readStream != null) {
                temp.addElement(readStream);
            }
            stream = temp.elements();
        }

        Vector result = new Vector();
        while (stream.hasMoreElements()) {
            Reader xmlStream = (Reader)stream.nextElement();
            if (xmlStream != null) {
            	Record row = this.getXMLTranslator().read(xmlStream);
                row = this.getFieldTranslator().translateForRead(row);
                row = this.extractResultElements((AbstractRecord)row);
                result.addElement(row);
            }
        }
        return result;
    }

    /**
     * Extract the required elements from the row.
     */
    protected Record extractResultElements(AbstractRecord row) {
        AbstractRecord result1 = null;

        // move the requested fields into the result row
        if (this.getResultElements().isEmpty()) {
            result1 = row;
        } else {
            result1 = new DatabaseRecord(this.getResultElements().size());
            for (Enumeration stream = this.getResultElements().elements();
                     stream.hasMoreElements();) {
                DatabaseField resultElement = (DatabaseField)stream.nextElement();
                Object value = row.get(resultElement);
                result1.put(resultElement, value);
            }
        }

        // now convert the fields that need to be converted
        AbstractRecord result2 = new DatabaseRecord(result1.size());
        for (Enumeration stream = result1.getFields().elements(); stream.hasMoreElements();) {
            DatabaseField field = (DatabaseField)stream.nextElement();
            Object value = result1.get(field);
            Class resultElementType = (Class)this.getResultElementTypes().get(field);
            if (resultElementType != null) {
                value = this.getSession().getDatasourcePlatform().convertObject(value, resultElementType);
            }
            result2.put(field, value);
        }
        return result2;
    }

    /**
     * Return the result elements.
     */
    protected Vector getResultElements() {
        return resultElements;
    }

    /**
     * Return the result element types.
     */
    protected Hashtable getResultElementTypes() {
        return resultElementTypes;
    }

    /**
     * Initialize the newly-created instance.
     */
    protected void initialize() {
        super.initialize();
        resultElements = new Vector();
        resultElementTypes = new Hashtable(10);
    }

    /**
     * Set the singular result element.
     */
    protected void setResultElement(DatabaseField resultElement) {
        this.clearResultElements();
        this.addResultElement(resultElement);
    }

    /**
     * Set the name of the singular result element.
     */
    public void setResultElementName(String resultElementName) {
        this.clearResultElements();
        this.addResultElementName(resultElementName);
    }

    /**
     * Set the result element names.
     */
    public void setResultElementNames(String[] resultElementNames) {
        this.clearResultElements();
        for (int i = 0; i < resultElementNames.length; i++) {
            this.addResultElementName(resultElementNames[i]);
        }
    }

    /**
     * Set the result element names.
     */
    public void setResultElementNames(Vector resultElementNames) {
        this.clearResultElements();
        for (Enumeration stream = resultElementNames.elements(); stream.hasMoreElements();) {
            this.addResultElementName((String)stream.nextElement());
        }
    }

    /**
     * Set the result elements.
     */
    protected void setResultElements(Vector resultElements) {
        this.resultElements = resultElements;
    }

    /**
     * Append a string describing the call to the specified writer.
     */
    protected void writeLogDescription(PrintWriter writer) {
        writer.write(TraceLocalization.buildMessage("XML_data_read", (Object[])null));
    }
}