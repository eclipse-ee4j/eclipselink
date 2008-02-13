/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.io.*;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;

/**
 * <b>Purpose</b>: A single row (type) result for a ReportQuery<p>
 *
 * <b>Description</b>: Represents a single row of attribute values (converted using mapping) for
 * a ReportQuery. The attributes can be from various objects.
 *
 * <b>Responsibilities</b>:<ul>
 * <li> Converted field values into object attribute values.
 * <li> Provide access to values by index or item name
 * </ul>
 *
 * @author Doug Clarke
 * @since TOPLink/Java 2.0
 */
public class ReportQueryResult implements Serializable, Map {

    /** Item names to lookup result values */
    protected Vector names;

    /** Actual converted attribute values */
    protected Vector results;

    /** PK values if the retrievPKs flag was set on the ReportQuery. These can be used to get the actual object */
    protected Vector primaryKeyValues;
    
    /** If an objectLevel distinct is used then generate unique key for this result */
    // GF_ISSUE_395
    protected StringBuffer key;

    /**
     * INTERNAL:
     * Used to create test results
     */
    public ReportQueryResult(Vector results, Vector primaryKeyValues) {
        this.results = results;
        this.primaryKeyValues = primaryKeyValues;
    }

    public ReportQueryResult(ReportQuery query, AbstractRecord row, Vector toManyResults) {
        super();
        this.names = query.getNames();
        buildResult(query, row, toManyResults);
    }

    /**
     * INTERNAL:
     * Create an array of attribute values (converted from raw field values using the mapping).
     */
    protected void buildResult(ReportQuery query, AbstractRecord row, Vector toManyData) {
        // GF_ISSUE_395
        if (query.shouldDistinctBeUsed() && (query.shouldFilterDuplicates())) {
            this.key = new StringBuffer();
        }
        List items = query.getItems();
        int itemSize = items.size();
        Vector results = new Vector(itemSize);

        if (query.shouldRetrievePrimaryKeys()) {
            setPrimaryKeyValues(query.getDescriptor().getObjectBuilder().extractPrimaryKeyFromRow(row, query.getSession()));
            // For bug 3115576 this is only used for EXISTS sub-selects so no result is needed.
        }
        for (int index = 0; index < itemSize; index++) {
            ReportItem item = (ReportItem)items.get(index);
            if (item.isConstructorItem()) {
                // For constructor items need to process each constructor argument.
                ConstructorReportItem constructorItem = (ConstructorReportItem)item;
                Class[] constructorArgTypes = constructorItem.getConstructorArgTypes();
                int numberOfArguments = constructorItem.getReportItems().size();
                Object[] constructorArgs = new Object[numberOfArguments];
                
                for (int argumentIndex = 0; argumentIndex < numberOfArguments; argumentIndex++) {
                    ReportItem argumentItem = (ReportItem)constructorItem.getReportItems().get(argumentIndex);
                    Object result = processItem(query, row, toManyData, argumentItem);
                    constructorArgs[argumentIndex] = ConversionManager.getDefaultManager().convertObject(result, constructorArgTypes[argumentIndex]);
                }
                try {
                    Constructor constructor = constructorItem.getConstructor();
                    Object returnValue = null;
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try {
                            returnValue = AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, constructorArgs));
                        } catch (PrivilegedActionException exception) {
                            throw QueryException.exceptionWhileUsingConstructorExpression(exception.getException(), query);                       }
                    } else {
                        returnValue = PrivilegedAccessHelper.invokeConstructor(constructor, constructorArgs);
                    }
                    results.add(returnValue);
                } catch (IllegalAccessException exc){
                    throw QueryException.exceptionWhileUsingConstructorExpression(exc, query);
                } catch (java.lang.reflect.InvocationTargetException exc){
                    throw QueryException.exceptionWhileUsingConstructorExpression(exc, query);
                } catch (InstantiationException exc){
                    throw QueryException.exceptionWhileUsingConstructorExpression(exc, query);
                }

            } else {
                // Normal items
                Object value = processItem(query, row, toManyData, item);
                results.addElement(value);
            }
        }

        setResults(results);
    }
    
    /**
     * INTERNAL:
     * Return a value from an item and database row (converted from raw field values using the mapping).
     */
    protected Object processItem(ReportQuery query, AbstractRecord row, Vector toManyData, ReportItem item) {
        JoinedAttributeManager joinManager = null;
        if (item.hasJoining()) {
            joinManager = item.getJoinedAttributeManager();
            if (joinManager.isToManyJoin()) {
                // PERF: Only reset data-result if unset, must only occur once per item, not per row (n vs n^2).
                if (joinManager.getDataResults_() == null) {
                    joinManager.setDataResults(new ArrayList(toManyData), query.getSession());
                }
            }
        }
        Object value = null;
        DatabaseMapping mapping = item.getMapping();
        int rowSize = row.size();
        int itemIndex = item.getResultIndex();
        ClassDescriptor descriptor = item.getDescriptor();
        if (!item.isPlaceHolder()) {
            if (mapping != null) {
                if (itemIndex >= rowSize) {
                    throw QueryException.reportQueryResultSizeMismatch(itemIndex + 1, rowSize);
                }
                // If mapping is not null then it must be a direct mapping - see Reportitem.init.
                value = row.getValues().get(itemIndex);
                value = ((AbstractDirectMapping)mapping).getAttributeValue(value, query.getSession());
                // GF_ISSUE_395
                if (this.key != null) {
                    this.key.append(value);
                    this.key.append("_");
                }
            } else if (descriptor != null) {
                // Item is for an object result.
                if ((itemIndex + descriptor.getAllFields().size()) > rowSize) {
                    throw QueryException.reportQueryResultSizeMismatch(itemIndex + descriptor.getAllFields().size(), rowSize);
                }
                AbstractRecord subRow = row;
                // Check if at the start of the row, then avoid building a subRow.
                if (itemIndex > 0) {
                    Vector trimedFields = new NonSynchronizedSubVector(row.getFields(), itemIndex, rowSize);
                    Vector trimedValues = new NonSynchronizedSubVector(row.getValues(), itemIndex, rowSize);
                    subRow = new DatabaseRecord(trimedFields, trimedValues);
                }
                value = descriptor.getObjectBuilder().buildObject(query, subRow, joinManager);
                // GF_ISSUE_395
                if (this.key != null) {
                    List list = descriptor.getObjectBuilder().extractPrimaryKeyFromRow(subRow, query.getSession());
                    if(list!=null){//GF3233 NPE is caused by processing the null PK being extracted from referenced target with null values in database.
                        for (Iterator iterator = list.iterator(); iterator.hasNext();){
                           this.key.append(iterator.next());
                           this.key.append("-");
                        }
                    }
                    this.key.append("_");
                }
            } else {
                value = row.getValues().get(itemIndex);
                // GF_ISSUE_395
                if (this.key != null) {
                    this.key.append(value);
                }
            }
        }
        return value;
    }

    /**
     * PUBLIC:
     * Clear the contents of the result.
     */
    public void clear() {
        this.names = new Vector();
        this.results = new Vector();
    }

    /**
     * PUBLIC:
     * Check if the value is contained in the result.
     */
    public boolean contains(Object value) {
        return containsValue(value);
    }

    /**
     * PUBLIC:
     * Check if the key is contained in the result.
     */
    public boolean containsKey(Object key) {
        return getNames().contains(key);
    }

    /**
     * PUBLIC:
     * Check if the value is contained in the result.
     */
    public boolean containsValue(Object value) {
        return getResults().contains(value);
    }

    /**
     * PUBLIC:
     * Return an enumeration of the result values.
     */
    public Enumeration elements() {
        return getResults().elements();
    }

    /**
     * PUBLIC:
     * Returns a set of the keys.
     */
    public Set entrySet() {
        // bug 2669127
        // implemented this method exactly the same way as Record.entrySet()
        int size = this.size();
        Map tempMap = new HashMap(size);
        for (int i = 0; i < size; i++) {
            tempMap.put(this.getNames().elementAt(i), this.getResults().elementAt(i));
        }
        return tempMap.entrySet();
    }

    /**
     * PUBLIC:
     * Compare if the two results are equal.
     */
    public boolean equals(Object anObject) {
        if (anObject instanceof ReportQueryResult) {
            return equals((ReportQueryResult)anObject);
        }

        return false;
    }

    /**
     * INTERNAL:
     * Used in testing to compare if results are correct.
     */
    public boolean equals(ReportQueryResult result) {
        if (this == result) {
            return true;
        }
        if (!Helper.compareOrderedVectors(getResults(), result.getResults())) {
            return false;
        }

        // Compare PKs
        if (getPrimaryKeyValues() != null) {
            if (result.getPrimaryKeyValues() == null) {
                return false;
            }
            return Helper.compareOrderedVectors(getPrimaryKeyValues(), result.getPrimaryKeyValues());
        }

        return true;
    }

    /**
     * PUBLIC:
     * Return the value for given item name.
     */
    public Object get(Object name) {
        if (name instanceof String) {
            return get((String)name);
        }

        return null;
    }

    /**
     * PUBLIC:
     * Return the value for given item name.
     */
    public Object get(String name) {
        int index = getNames().indexOf(name);
        if (index == -1) {
            return null;
        }

        return getResults().elementAt(index);
    }

    /**
     * PUBLIC:
     * Return the indexed value from result.
     */
    public Object getByIndex(int index) {
        return getResults().elementAt(index);
    }

    /**
     * INTERNAL:
     * Return the unique key for this result
     */
    public String getResultKey(){
        if (this.key != null){
            return this.key.toString();
        }
        return null;
    }
     
    /**
     * PUBLIC:
     * Return the names of report items, provided to ReportQuery.
     */
    public Vector getNames() {
        return names;
    }

    /**
     * PUBLIC:
     * Return the PKs for the corresponding object or null if not requested.
     */
    public Vector getPrimaryKeyValues() {
        return primaryKeyValues;
    }

    /**
     * PUBLIC:
     * Return the results.
     */
    public Vector getResults() {
        return results;
    }

    /**
     * PUBLIC:
     * Return if the result is empty.
     */
    public boolean isEmpty() {
        return getNames().isEmpty();
    }

    /**
     * PUBLIC:
     * Return an enumeration of the result names.
     */
    public Enumeration keys() {
        return getNames().elements();
    }

    /**
     * PUBLIC:
     * Returns a set of the keys.
     */
    public Set keySet() {
        return new HashSet(getNames());
    }

    /**
     * ADVANCED:
     * Set the value for given item name.
     */
    public Object put(Object name, Object value) {
        int index = getNames().indexOf(name);
        if (index == -1) {
            getNames().addElement(name);
            getResults().addElement(value);
            return null;
        }

        Object oldValue = getResults().elementAt(index);
        getResults().setElementAt(value, index);
        return oldValue;
    }

    /**
     * PUBLIC:
     * Add all of the elements.
     */
    public void putAll(Map map) {
        Iterator entriesIterator = map.entrySet().iterator();
        while (entriesIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)entriesIterator.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * PUBLIC:
     * If the PKs were retrieved with the attributes then this method can be used to read the real object from the database.
     */
    public Object readObject(Class javaClass, Session session) {
        if (getPrimaryKeyValues() == null) {
            throw QueryException.reportQueryResultWithoutPKs(this);
        }

        ReadObjectQuery query = new ReadObjectQuery(javaClass);
        query.setSelectionKey(getPrimaryKeyValues());

        return session.executeQuery(query);
    }

    /**
     * INTERNAL:
     * Remove the name key and value from the result.
     */
    public Object remove(Object name) {
        int index = getNames().indexOf(name);
        if (index >= 0) {
            getNames().removeElementAt(index);
            Object value = getResults().elementAt(index);
            getResults().removeElementAt(index);
            return value;
        }
        return null;
    }

    protected void setNames(Vector names) {
        this.names = names;
    }

    /**
     * INTERNAL:
     * Set the PK values for the result row's object.
     */
    protected void setPrimaryKeyValues(Vector primaryKeyValues) {
        this.primaryKeyValues = primaryKeyValues;
    }

    /**
     * INTERNAL:
     * Set the results.
     */
    public void setResults(Vector results) {
        this.results = results;
    }

    /**
     * PUBLIC:
     * Return the number of name/value pairs in the result.
     */
    public int size() {
        return getNames().size();
    }

    /**
     * INTERNAL:
     * Converts the ReportQueryResult to a simple array of values.
     */
    public Object[] toArray(){
       List list = getResults();
       return (list == null) ? null : list.toArray();
    }

    /**
     * INTERNAL:
     * Converts the ReportQueryResult to a simple list of values.
     */
    public List toList(){
        return this.getResults();
    }
    
    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();
        writer.write("ReportQueryResult(");
        for (int index = 0; index < getResults().size(); index++) {
            writer.write(String.valueOf(getResults().elementAt(index)));
            if (index < (getResults().size() - 1)) {
                writer.write(", ");
            }
        }
        writer.write(")");
        return writer.toString();
    }

    /**
     * PUBLIC:
     * Returns an collection of the values.
     */
    public Collection values() {
        return getResults();
    }
}