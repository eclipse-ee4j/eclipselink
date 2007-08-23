/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.sdk;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * <code>SDKFieldValue</code> is a container for nested
 * (and possibly repeating) elements that might occur
 * within a database row. These elements can have a type name; and
 * they can be either nested database rows or "direct" values
 * (e.g. <code>String</code>s or <code>Date</code>s).
 *
 * @see org.eclipse.persistence.session.Record
 * @see SDKDescriptor
 * @see SDKAggregateCollectionMapping
 * @see SDKAggregateObjectMapping
 * @see SDKDirectCollectionMapping
 * @see SDKObjectCollectionMapping
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public class SDKFieldValue {

    /** The collection of values for a database field. */
    protected Vector elements;

    /** The "data type" associated with each element in the collection. */
    protected String elementDataTypeName;

    /** Whether the collection is "direct" values or nested database rows. */
    protected boolean isDirectCollection;

    /**
     * Default constructor.
     */
    public SDKFieldValue() {
        super();
        this.initialize();
    }

    /**
     * Construct a field value with the specified collection
     * of elements and element "data type". The elements
     * vector is either a direct collection of values or a collection
     * of database rows.
     */
    public SDKFieldValue(Vector elements, String elementDataTypeName, boolean isDirectCollection) {
        super();
        this.initialize(elements, elementDataTypeName, isDirectCollection);
    }

    /**
     * Add an element to the collection of elements.
     */
    public void addElement(Object element) {
        this.getElements().addElement(element);
    }

    /**
     * Return a clone of the field value with a new
     * collection of elements.
     */
    public SDKFieldValue clone(Vector elements) {
        return new SDKFieldValue(elements, this.getElementDataTypeName(), this.isDirectCollection());
    }

    /**
     * Construct a field value with the specified collection
     * of database rows and element "data type".
     */
    public static SDKFieldValue forRecords(Vector elements, String elementDataTypeName) {
        return new SDKFieldValue(elements, elementDataTypeName, false);
    }

    /**
     * Construct a field value with the specified collection
     * of direct values and element "data type".
     */
    public static SDKFieldValue forDirectValues(Vector elements, String elementDataTypeName) {
        return new SDKFieldValue(elements, elementDataTypeName, true);
    }

    /**
     * Return the "data type" associated with each element
     * in the collection.
     */
    public String getElementDataTypeName() {
        return elementDataTypeName;
    }

    /**
     * Return the collection of elements.
     * These are either direct values or database rows.
     */
    public Vector getElements() {
        return elements;
    }

    /**
     * Initialize the instance.
     */
    protected void initialize() {
        this.setElements(new Vector());
        this.setElementDataTypeName("");
        this.setIsDirectCollection(false);
    }

    /**
     * Initialize the instance.
     */
    protected void initialize(Vector elements, String elementDataTypeName, boolean isDirectCollection) {
        this.setElements(elements);
        this.setElementDataTypeName(elementDataTypeName);
        this.setIsDirectCollection(isDirectCollection);
    }

    /**
     * Return whether elements is a "direct" collection
     * of values or a collection of database rows.
     */
    public boolean isDirectCollection() {
        return isDirectCollection;
    }

    /**
     * Set the "data type" associated with each element
     * in the collection.
     */
    public void setElementDataTypeName(String elementDataTypeName) {
        this.elementDataTypeName = elementDataTypeName;
    }

    /**
     * Set the collection of elements.
     */
    public void setElements(Vector elements) {
        this.elements = elements;
    }

    /**
     * Set whether elements is a "direct" collection
     * of values or a collection of database rows.
     */
    public void setIsDirectCollection(boolean isDirectCollection) {
        this.isDirectCollection = isDirectCollection;
    }

    /**
     *
     */
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        writer.write(Helper.getShortClassName(this));
        writer.write("(");
        writer.write(this.getElementDataTypeName());
        writer.write(" - ");
        writer.print(this.getElements());
        writer.write(")");
        return sw.toString();
    }
}