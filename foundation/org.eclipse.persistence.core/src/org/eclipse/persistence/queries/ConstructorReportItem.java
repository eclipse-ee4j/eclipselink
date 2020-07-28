/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     10/01/2018: Will Dazey
//       - #253: Add support for embedded constructor results with CriteriaBuilder
package org.eclipse.persistence.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.StringHelper;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;


/**
 * <b>Purpose</b>: An item specifying a class constructor method to be used in a ReportQuery's returned results.
 * <p>Example:
 * <blockquote><pre>
 *      ConstructorReportItem item = new ConstructorReportItem("Employee");
 *      item.setResultType(Employee.class);
 *      item.addAttribute("firstName", employees.get("firstName"));
 *      query.addConstructorReportItem(item);
 * </pre></blockquote>
 * <p>
 * When executed will return a collection of ReportQueryResults that contain Employee objects created using
 * the new Employee(firstname) constructor.
 *
 * @author Chris Delahunt
 * @since TopLink Essentials 1.0
 */
public class ConstructorReportItem extends ReportItem  {

    /**
     * String prefix of {@link #toString()} output.
     */
    private static final String TO_STR_PREFIX = "ConstructorReportItem(";

    /**
     * String to separate name and items array of {@link #toString()} output.
     */
    private static final String TO_STR_ARRAY = " -> [";

    /**
     * String suffix of {@link #toString()} output.
     */
    private static final String TO_STR_SUFFIX = "])";

    protected Class[] constructorArgTypes;
    protected List<DatabaseMapping> constructorMappings;
    protected List<ReportItem> reportItems;
    protected Constructor constructor;

    /**
     * Create a new constructor item.
     */
    public ConstructorReportItem() {
    }

    /**
     * Create a new constructor item.
     * @param name string used to look up this result in the ReportQueryResult.
     */
    public ConstructorReportItem(String name) {
        super(name, null);
    }

    /**
     * Method to add an expression to be used to return the parameter that is then passed into the constructor method.
     * Similar to ReportQuery's addAttribute method, but name is not needed.
     */
    public void addAttribute(Expression attributeExpression) {
        ReportItem item = new ReportItem(getName()+getReportItems().size(), attributeExpression);
        getReportItems().add(item);
    }

    /**
     * Add the attribute with joining.
     */
    public void addAttribute(String attributeName, Expression attributeExpression, List joinedExpressions) {
        ReportItem item = new ReportItem(attributeName, attributeExpression);
        item.getJoinedAttributeManager().setJoinedAttributeExpressions_(joinedExpressions);
        getReportItems().add(item);
    }

    public void addItem(ReportItem item) {
        getReportItems().add(item);
    }

    public Class[] getConstructorArgTypes(){
        return constructorArgTypes;
    }

    /**
     * INTERNAL:
     * Return the mappings for the items.
     */
    public List<DatabaseMapping> getConstructorMappings(){
        return constructorMappings;
    }

    /**
     * INTERNAL:
     * Return the constructor.
     */
    public Constructor getConstructor(){
        return constructor;
    }

    /**
     * INTERNAL:
     * Set the constructor.
     */
    public void setConstructor(Constructor constructor){
        this.constructor = constructor;
    }

    public List<ReportItem> getReportItems(){
        if (reportItems == null) {
            reportItems = new ArrayList<ReportItem>();
        }
        return reportItems;
    }

    /**
     * INTERNAL:
     * Looks up mapping for attribute during preExecute of ReportQuery
     */
    public void initialize(ReportQuery query) throws QueryException {
        int size= getReportItems().size();
        List<DatabaseMapping> mappings = new ArrayList<DatabaseMapping>();
        for (int index = 0; index < size; index++) {
            ReportItem item = reportItems.get(index);
            item.initialize(query);
            mappings.add(item.getMapping());
        }
        setConstructorMappings(mappings);

        int numberOfItems = getReportItems().size();
        // Arguments may be initialized depending on how the query was constructed, so types may be undefined though.
        if (getConstructorArgTypes() == null) {
            setConstructorArgTypes(new Class[numberOfItems]);
        }
        Class[] constructorArgTypes = getConstructorArgTypes();
        for (int index = 0; index < numberOfItems; index++) {
            if (constructorArgTypes[index] == null) {
                ReportItem argumentItem = getReportItems().get(index);
                if (mappings.get(index) != null) {
                    DatabaseMapping mapping = constructorMappings.get(index);
                    if (argumentItem.getAttributeExpression() != null && argumentItem.getAttributeExpression().isMapEntryExpression()){
                        if (((MapEntryExpression)argumentItem.getAttributeExpression()).shouldReturnMapEntry()){
                            constructorArgTypes[index] = Map.Entry.class;
                        } else {
                            constructorArgTypes[index] = (Class)((CollectionMapping)mapping).getContainerPolicy().getKeyType();
                        }
                    } else {
                        constructorArgTypes[index] = mapping.getAttributeClassification();
                    }
                } else if (argumentItem.getResultType() != null) {
                    constructorArgTypes[index] = argumentItem.getResultType();
                } else if (argumentItem.getDescriptor() != null) {
                    constructorArgTypes[index] = argumentItem.getDescriptor().getJavaClass();
                } else if (argumentItem.getAttributeExpression() != null && argumentItem.getAttributeExpression().isConstantExpression()){
                    constructorArgTypes[index] = ((ConstantExpression)argumentItem.getAttributeExpression()).getValue().getClass();
                } else {
                    // Use Object.class by default.
                    constructorArgTypes[index] = ClassConstants.OBJECT;
                }
            }
        }
        if (getConstructor() == null) {
            try {
                Constructor constructor = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        constructor = AccessController.doPrivileged(new PrivilegedGetConstructorFor(getResultType(), constructorArgTypes, true));
                    } catch (PrivilegedActionException exception) {
                        throw QueryException.exceptionWhileUsingConstructorExpression(exception.getException(), query);
                    }
                } else {
                    constructor = PrivilegedAccessHelper.getConstructorFor(getResultType(), constructorArgTypes, true);
                }
                setConstructor(constructor);
            } catch (NoSuchMethodException exception) {
                throw QueryException.exceptionWhileUsingConstructorExpression(exception, query);
            }
        }
    }

    public boolean isConstructorItem(){
        return true;
    }

    public void setConstructorArgTypes(Class[] constructorArgTypes){
        this.constructorArgTypes = constructorArgTypes;
    }

    /**
     * INTERNAL:
     * Return the mappings for the items.
     */
    public void setConstructorMappings(List<DatabaseMapping> constructorMappings){
        this.constructorMappings = constructorMappings;
    }

    public void setReportItems(List<ReportItem> reportItems){
        this.reportItems = reportItems;
    }

    public String toString() {
        String name = StringHelper.nonNullString(getName());
        // Calculate string length
        int length = TO_STR_PREFIX.length() + name.length()
                + TO_STR_ARRAY.length() + TO_STR_SUFFIX.length();
        int size = reportItems != null ? reportItems.size() : 0;
        String[] items = new String[size];
        for (int i=0; i < size; i++) {
            items[i] = StringHelper.nonNullString(reportItems.get(i).toString());
            length += items[i].length();
        }
        // Build string
        StringBuilder str = new StringBuilder(length);
        str.append(TO_STR_PREFIX).append(name).append(TO_STR_ARRAY);
        for (int i=0; i < size; i++) {
            str.append(items[i]);
        }
        str.append(TO_STR_SUFFIX);
        return str.toString();
    }

}
