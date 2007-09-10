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
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * XMLDataCall provides the base state and behavior for XML calls
 * that will read or write raw data, as opposed to objects. It provides
 * a way to specify the "root element"/"document"/"table" and the
 * necessary primary key.  The derivative classes of <code>XMLDataCall<code>
 * will use the state in this class for primary key and root element/table
 * information.  The other corresponding calls in this package (e.g. XMLDataReadCall
 * corresponds to XMLReadCall) get this
 * information from the descriptor.
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public abstract class XMLDataCall extends XMLCall {

    /** The "root element"/"document"/"table" */
    private DatabaseTable rootElement;

    /** The primary key of the "document" to be manipulated. */
    private Vector orderedPrimaryKeyElements;

    /**
     * Default constructor.
     */
    public XMLDataCall() {
        super();
    }

    /**
     * Add a primary key element.
     * The order in which these elements are added may be significant.
     */
    protected void addPrimaryKeyElement(DatabaseField primaryKeyElement) {
        this.getOrderedPrimaryKeyElements().addElement(primaryKeyElement);
    }

    /**
     * Add a primary key element name.
     * The order in which these names are added may be significant.
     */
    public void addPrimaryKeyElementName(String primaryKeyElementName) {
        this.addPrimaryKeyElement(new DatabaseField(primaryKeyElementName, this.getRootElement()));
    }

    /**
     * Clear out the primary key elements.
     */
    protected void clearPrimaryKeyElements() {
        this.getOrderedPrimaryKeyElements().removeAllElements();
    }

    /**
     * Munge the table for all the fields in the row.
     */
    protected AbstractRecord convertToFullyQualifiedRow(AbstractRecord row) {
        DatabaseRecord result = new DatabaseRecord(row.size());
        for (Enumeration stream = row.keys(); stream.hasMoreElements();) {
            DatabaseField key = (DatabaseField)stream.nextElement();
            result.put(new DatabaseField(key.getName(), this.getRootElement()), row.get(key));
        }
        return result;
    }

    /**
     * Return the primary key elements, in the same order
     * as they were added to the call.
     */
    protected Vector getOrderedPrimaryKeyElements() {
        return orderedPrimaryKeyElements;
    }

    /**
     * Return the root element.
     */
    protected DatabaseTable getRootElement() {
        return rootElement;
    }

    /**
     * Return the root element name.
     */
    protected String getRootElementName() {
        return this.getRootElement().getName();
    }

    /**
     * Return the name of the table to be logged.
     */
    protected String getLogTableName() {
        return this.getRootElementName();
    }

    /**
     * Initialize the newly-created instance.
     */
    protected void initialize() {
        super.initialize();
        rootElement = new DatabaseTable("");
        orderedPrimaryKeyElements = new Vector();
    }

    /**
     * Set the primary key elements.
     * The order may be significant.
     */
    protected void setOrderedPrimaryKeyElements(Vector orderedPrimaryKeyElements) {
        this.orderedPrimaryKeyElements = orderedPrimaryKeyElements;
    }

    /**
     * Set the singular primary key element.
     */
    protected void setPrimaryKeyElement(DatabaseField primaryKeyElement) {
        this.clearPrimaryKeyElements();
        this.addPrimaryKeyElement(primaryKeyElement);
    }

    /**
     * Set the name of the singular primary key element.
     */
    public void setPrimaryKeyElementName(String primaryKeyFieldName) {
        this.clearPrimaryKeyElements();
        this.addPrimaryKeyElementName(primaryKeyFieldName);
    }

    /**
     * Set the primary key element names.
     * The order may be significant.
     */
    public void setPrimaryKeyElementNames(String[] primaryKeyElementNames) {
        this.clearPrimaryKeyElements();
        for (int i = 0; i < primaryKeyElementNames.length; i++) {
            this.addPrimaryKeyElementName(primaryKeyElementNames[i]);
        }
    }

    /**
     * Set the primary key element names.
     * The order may be significant.
     */
    public void setPrimaryKeyElementNames(Vector primaryKeyElementNames) {
        this.clearPrimaryKeyElements();
        for (Enumeration stream = primaryKeyElementNames.elements(); stream.hasMoreElements();) {
            this.addPrimaryKeyElementName((String)stream.nextElement());
        }
    }

    /**
     * Set the root element.
     */
    protected void setRootElement(DatabaseTable rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * Set the root element name.
     */
    public void setRootElementName(String rootElementName) {
        this.getRootElement().setName(rootElementName);
    }

    /**
     * Append a string describing the call to the specified writer.
     */
    protected void writeLogDescription(PrintWriter writer) {
        writer.write(TraceLocalization.buildMessage("XML_data_call", (Object[])null));
    }
}