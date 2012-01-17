/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.queries;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;

/**
 * Manager class that maintains the {@link JPAQueryBuilder} to be used in parsing
 * JPQL queries and converting them to {@link DatabaseQuery}s in the
 * the EclipseLink environment.  In the absence of designating a query builder
 * specifically ({@link JPAQueryBuilderManager#setQueryBuilder(JPAQueryBuilder)}),
 * the default builder is {@link ANTLRQueryBuilder}.
 *
 * @see JPAQueryBuilder
 * @see ANTLRQueryBuilder
 *
 * @version 2.2
 * @since 2.2
 * @author John Bracken
 */
public final class JPAQueryBuilderManager {

    /**
     * The {@link JPAQueryBuilder} that will be used for all
     * queries.
     */
    private static JPAQueryBuilder systemQueryBuilder;

    /**
     * Constructs the default {@link JPAQueryBuilder} instance.
     * 
     * @return the {@link ANTLRQueryBuilder}.
     */
    private static JPAQueryBuilder buildDefaultQueryBuilder() {
        try {
            Class parserClass = null;
            // use class.forName() to avoid loading parser classes for JAXB
            // Use Class.forName not thread class loader to avoid class loader issues.
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    //parserClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName("org.eclipse.persistence.internal.jpa.jpql.HermesParser"));
                    parserClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName("org.eclipse.persistence.queries.ANTLRQueryBuilder"));
                } catch (PrivilegedActionException exception) {
                }
            } else {
                //parserClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName("org.eclipse.persistence.internal.jpa.jpql.HermesParser");
                parserClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName("org.eclipse.persistence.queries.ANTLRQueryBuilder");
            }                  
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return (JPAQueryBuilder)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(parserClass));
                } catch (PrivilegedActionException exception) {
                }
            } else {
                return (JPAQueryBuilder)PrivilegedAccessHelper.newInstanceFromClass(parserClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not load the ANTLRQueryBuilder class." /* TODO: Localize string */, e);
        }
        // PERFORMANCE: class for name and newInstance() is an attempt to keep
        // the antlr-based impl classes from loading.
        // return new ANTLRQueryBuilder();
        return null;
    }
        
    /**
     * This method returns the {@link JPAQueryBuilder} that has been set for the 
     * EclipseLink environment.  If no query builder has been explicitly designated,
     * then the {@link ANTLRQueryBuilder} will be used.
     * 
     * @return the {@link JPAQueryBuilder} designated for the environment.
     */
    public static JPAQueryBuilder getQueryBuilder() {
        if (systemQueryBuilder == null) {
            systemQueryBuilder = buildDefaultQueryBuilder();
        }
        return systemQueryBuilder;
    }
    
    /**
     * Sets the system {@link JPAQueryBuilder} to be used across the 
     * EclipseLink environment.
     * 
     * @param queryBuilder The query builder to set.
     */
    public static void setQueryBuilder(JPAQueryBuilder queryBuilder) {
        systemQueryBuilder = queryBuilder;
    }
}
