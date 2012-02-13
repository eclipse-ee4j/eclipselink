/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>:
 * Concrete class to represent the ConstructorResult structure as defined by
 * the JPA 2.1 Persistence specification. 
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.4
 */
public class ConstructorResult extends SQLResult {
    /** Stores the class of result  */
    protected Class targetClass;

    /** Stored the column results of this constructor result */
    protected List<ColumnResult> columnResults;
    
    protected Constructor constructor;
    protected Class[] constructorArgTypes;

    /**
     * Default constructor is protected. Users must initialize the constructor
     * result with a target class.
     */
    protected ConstructorResult() {
        columnResults = new ArrayList<ColumnResult>();
    }
    
    /**
     * Constructor accepting target class.
     */
    public ConstructorResult(Class targetClass){
        this();
        
        if (targetClass == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_value_for_constructor_result"));
        }
        
        this.targetClass = targetClass;
    }
    
    /**
     * Add a column result to this constructor result.
     */
    public void addColumnResult(ColumnResult columnResult) {
        columnResults.add(columnResult);
    }
    
    /**
     * Return the columns result of this constructor result.
     */
    public List<ColumnResult> getColumnResults() {
        return columnResults;
    }
    
    /**
     * INTERNAL:
     * This method is a convenience method for extracting values from results/
     */
    @Override
    public Object getValueFromRecord(DatabaseRecord record, ResultSetMappingQuery query) {
        if (constructor == null) {
            initialize(record, query);
        }
        
        int columnResultsSize = getColumnResults().size();
        Object[] constructorArgs = new Object[columnResultsSize];
        
        for (int i = 0; i < columnResultsSize; i++) {
            constructorArgs[i] = ConversionManager.getDefaultManager().convertObject(record.get(getColumnResults().get(i).getColumn()), constructorArgTypes[i]);
        }
        
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                try {
                    return AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, constructorArgs));
                } catch (PrivilegedActionException exception) {
                    throw QueryException.exceptionWhileInitializingConstructor(exception.getException(), query, targetClass);
                }
            } else {
                return PrivilegedAccessHelper.invokeConstructor(constructor, constructorArgs);
            }
        } catch (IllegalAccessException exception) {
            throw QueryException.exceptionWhileInitializingConstructor(exception, query, targetClass);
        } catch (java.lang.reflect.InvocationTargetException exception) {
            throw QueryException.exceptionWhileInitializingConstructor(exception, query, targetClass);
        } catch (InstantiationException exception) {
            throw QueryException.exceptionWhileInitializingConstructor(exception, query, targetClass);
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void initialize(DatabaseRecord record, ResultSetMappingQuery query) { 
        int columnResultsSize = getColumnResults().size();
        constructorArgTypes = new Class[columnResultsSize];
        
        for (int i = 0; i < columnResultsSize; i++) {
            ColumnResult result = getColumnResults().get(i);
            DatabaseField resultField = result.getColumn();
            
            if (resultField.getType() == null) {
                constructorArgTypes[i] = record.get(resultField).getClass();    
            } else {
                constructorArgTypes[i] = resultField.getType();
            }
        }

        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                try {
                    constructor = (Constructor) AccessController.doPrivileged(new PrivilegedGetConstructorFor(targetClass, constructorArgTypes, true));
                } catch (PrivilegedActionException exception) {
                    throw QueryException.exceptionWhileInitializingConstructor(exception.getException(), query, targetClass);
                }
            } else {
                constructor = PrivilegedAccessHelper.getConstructorFor(targetClass, constructorArgTypes, true);
            }
        } catch (NoSuchMethodException exception) {
            throw QueryException.exceptionWhileInitializingConstructor(exception, query, targetClass);
        }   
    }
    
    /**
     * Return true if this is a constructor result.
     */
    public boolean isConstructorResult(){
        return true;
    }
    
    /**
     * Set columns result of this constructor result.
     */
    public void setColumnResults(List<ColumnResult> columnResults) {
        this.columnResults = columnResults;
    }
}
