/*
 * Copyright (c) 2012, 2026 Oracle and/or its affiliates. All rights reserved.
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
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/24/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     02/06/2013-2.5 Guy Pelletier
//       - 382503: Use of @ConstructorResult with createNativeQuery(sqlString, resultSetMapping) results in NullPointerException
package org.eclipse.persistence.queries;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.sessions.DatabaseRecord;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;

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
    protected String targetClassName;
    protected transient Class<?> targetClass;

    /** Stored the column results of this constructor result */
    protected List<ColumnResult> columnResults;

    /** Entity arguments follow all column arguments in declaration order. */
    protected List<EntityResult> entityResults;

    protected transient Constructor<?> constructor;
    protected Class<?>[] constructorArgTypes;

    /**
     * Default constructor is protected. Users must initialize the constructor
     * result with a target class.
     */
    protected ConstructorResult() {
        columnResults = new ArrayList<>();
        entityResults = new ArrayList<>();
    }

    /**
     * Constructor accepting target class.
     */
    public ConstructorResult(Class<?> targetClass){
        this();

        if (targetClass == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_value_for_constructor_result"));
        }

        this.targetClass = targetClass;
        this.targetClassName = targetClass.getName();
    }

    /**
     * Constructor accepting target class name.
     */
    public ConstructorResult(String targetClassName){
        this();

        if (targetClassName == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_value_for_constructor_result"));
        }

        this.targetClassName = targetClassName;
    }

    /**
     * Add a column result to this constructor result.
     */
    public void addColumnResult(ColumnResult columnResult) {
        columnResults.add(columnResult);
    }

    /**
     * Add an entity argument to this constructor result.
     */
    public void addEntityResult(EntityResult entityResult) {
        entityResults.add(entityResult);
        entityResult.setSQLResultMapping(getSQLResultMapping());
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this query to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     */
    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader){
        super.convertClassNamesToClasses(classLoader);

        // Make sure the column results have been converted.
        for (ColumnResult columnResult : columnResults) {
            columnResult.convertClassNamesToClasses(classLoader);
        }
        for (EntityResult entityResult : entityResults) {
            entityResult.convertClassNamesToClasses(classLoader);
        }

        //no need to get the class if we already have it
        if (targetClass == null && targetClassName!=null) {
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    try {
                        targetClass = AccessController.doPrivileged(new PrivilegedClassForName<>(targetClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(targetClassName, exception.getException());
                    }
                } else {
                    targetClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(targetClassName, true, classLoader);
                }
            } catch (ClassNotFoundException exc){
                throw ValidationException.classNotFoundWhileConvertingClassNames(targetClassName, exc);
            }
        }
    }

    /** INTERNAL: Return the initialized constructor target class. */
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * Return the columns result of this constructor result.
     */
    public List<ColumnResult> getColumnResults() {
        return columnResults;
    }

    /**
     * Return the ordered entity arguments of this constructor result.
     */
    public List<EntityResult> getEntityResults() {
        return entityResults;
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
        Object[] constructorArgs = new Object[columnResultsSize + getEntityResults().size()];

        for (int i = 0; i < columnResultsSize; i++) {
            constructorArgs[i] = ConversionManager.getDefaultManager().convertObject(record.get(getColumnResults().get(i).getColumn()), constructorArgTypes[i]);
        }
        for (int i = 0; i < getEntityResults().size(); i++) {
            constructorArgs[columnResultsSize + i] = getEntityResults().get(i).getValueFromRecord(record, query);
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
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException exception) {
            throw QueryException.exceptionWhileInitializingConstructor(exception, query, targetClass);
        }
    }

    /**
     * INTERNAL:
     */
    protected void initialize(DatabaseRecord record, ResultSetMappingQuery query) {
        int columnResultsSize = getColumnResults().size();
        constructorArgTypes = new Class<?>[columnResultsSize + getEntityResults().size()];

        for (int i = 0; i < columnResultsSize; i++) {
            ColumnResult result = getColumnResults().get(i);
            DatabaseField resultField = result.getColumn();

            if (resultField.getType() == null) {
                Object recordResultField = record.get(resultField);

                if (recordResultField == null) {
                    throw QueryException.columnResultNotFound(resultField);
                } else {
                    constructorArgTypes[i] = recordResultField.getClass();
                }
            } else {
                constructorArgTypes[i] = resultField.getType();
            }
        }

        for (int i = 0; i < getEntityResults().size(); i++) {
            constructorArgTypes[columnResultsSize + i] = getEntityResults().get(i).getEntityClass();
        }

        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                try {
                    constructor = AccessController.doPrivileged(new PrivilegedGetConstructorFor<>(targetClass, constructorArgTypes, true));
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
    @Override
    public boolean isConstructorResult(){
        return true;
    }

    /**
     * Set columns result of this constructor result.
     */
    public void setColumnResults(List<ColumnResult> columnResults) {
        this.columnResults = columnResults;
    }

    /**
     * Set the ordered entity arguments of this constructor result.
     */
    public void setEntityResults(List<EntityResult> entityResults) {
        this.entityResults = entityResults;
        for (EntityResult entityResult : entityResults) {
            entityResult.setSQLResultMapping(getSQLResultMapping());
        }
    }

    @Override
    public void setSQLResultMapping(SQLResultSetMapping mapping) {
        super.setSQLResultMapping(mapping);
        for (EntityResult entityResult : entityResults) {
            entityResult.setSQLResultMapping(mapping);
        }
    }
}
