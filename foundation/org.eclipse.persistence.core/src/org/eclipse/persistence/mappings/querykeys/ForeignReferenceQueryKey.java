/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings.querykeys;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 * <p>
 * <b>Purpose</b>: Define an alias to a foreign object.
 * <p>
 * <b> Responsibilities</b>:
 * <ul>
 * <li> Define the reference class of the foreign object.
 * </ul>
 */
public class ForeignReferenceQueryKey extends QueryKey {
    protected Class referenceClass;
    protected String referenceClassName;
    protected Expression joinCriteria;

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this project to actual class-based
     * settings
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader){
        Class referenceClass = null;
        try{
            if (referenceClassName != null){
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        referenceClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(referenceClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(referenceClassName, exception.getException());
                    }
                } else {
                    referenceClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(referenceClassName, true, classLoader);
                }
            }
            setReferenceClass(referenceClass);
        } catch (ClassNotFoundException exc){
            throw ValidationException.classNotFoundWhileConvertingClassNames(referenceClassName, exc);
        }
    }

    /**
     * PUBLIC:
     * Return the join expression for the relationship defined by the query key.
     */
    public Expression getJoinCriteria() {
        return joinCriteria;
    }

    /**
     * PUBLIC:
     * Return the reference class of the relationship.
     */
    public Class getReferenceClass() {
        return referenceClass;
    }
    
    /**
     * PUBLIC:
     * Return the reference class name of the relationship.
     */
    public String getReferenceClassName() {
        if (referenceClassName == null && referenceClass != null){
            referenceClassName = referenceClass.getName();
        }
        return referenceClassName;
    }

    /**
     * INTERNAL:
     * override the isForeignReferenceQueryKey() method in the superclass to return true.
     * @return boolean
     */
    public boolean isForeignReferenceQueryKey() {
        return true;
    }

    /**
     * PUBLIC:
     * Set the join expression for the relationship defined by the query key.
     * <p>Example:
     * <pre><blockquote>
     *     builder.getField("ADDRESS.ADDRESS_ID").equal(builder.getParameter("EMPLOYEE.ADDR_ID");
     * </blockquote></pre>
     */
    public void setJoinCriteria(Expression joinCriteria) {
        this.joinCriteria = joinCriteria;
    }

    /**
     * PUBLIC:
     * Set the reference class of the relationship.
     * This is not required for direct collection query keys.
     */
    public void setReferenceClass(Class referenceClass) {
        this.referenceClass = referenceClass;
    }
    
    /**
     * PUBLIC:
     * Set the reference class name for this relationship
     * This is used when projects are built without using classes
     * @param referenceClassName 
     */
    public void setReferenceClassName(String referenceClassName) {
        this.referenceClassName = referenceClassName;
    }
    
    /**
     * PUBLIC:
     * Returns the source table.
     */
    public DatabaseTable getSourceTable() { 
        // TODO: Should extract the target table from joinCriteria (if it's not null),
        // like ManyToManyQueryKey.getRelationTable does.
        return this.descriptor.getTables().firstElement();
    }

    /**
     * PUBLIC:
     * Returns the reference table.
     */
    public DatabaseTable getReferenceTable(ClassDescriptor desc) { 
        // TODO: This won't work for direct collection.
        // Should extract the target table from joinCriteria (if it's not null),
        // like ManyToManyQueryKey.getRelationTable does.
        return desc.getTables().firstElement();
    }
}