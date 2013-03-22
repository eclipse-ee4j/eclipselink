/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Redirectors allow EclipseLink queries to be intercepted and pre/post processed or redirected.
 * They provide opportunities to extend query functionality beyond standard EclipseLink support.
 * 
 * @see org.eclipse.persistence.queries.QueryRedirector
 * 
 * @author Gordon Yorke
 * @since EclipseLink 1.0
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface QueryRedirectors {
    
    /**
     * This AllQueries Query Redirector will be applied to any executing object query
     * that does not have a more precise redirector (like the 
     * ReadObjectQuery Redirector) or a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * 
     */
    Class allQueries() default void.class;
    
    /**
     * A Default ReadAll Query Redirector will be applied to any executing
     * ReadAllQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * For users executing a JPA Query through the getResultList() API this is the redirector that will be invoked
     *      */
    Class readAll() default void.class;
    
    /**
     * A Default ReadObject Query Redirector will be applied to any executing
     * ReadObjectQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * For users executing a JPA Query through the getSingleResult() API or EntityManager.find() this is the redirector that will be invoked     */
    Class readObject() default void.class;
    
    /**
     * A Default ReportQuery Redirector will be applied to any executing
     * ReportQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     * For users executing a JPA Query that contains agregate functions or selects multiple entities this is the redirector that will be invoked     */
    Class report() default void.class;
    
    /**
     * A Default Update Query Redirector will be applied to any executing
     * UpdateObjectQuery or UpdateAllQuery that does not have a redirector set directly on the query.
     * In EclipseLink an UpdateObjectQuery is executed whenever flushing changes to the datasource.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     */ 
    Class update() default void.class;
    
    /**
     * A Default Insert Query Redirector will be applied to any executing
     * InsertObjectQuery that does not have a redirector set directly on the query.
     * In EclipseLink an InsertObjectQuery is executed when persisting an object to the datasource.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     */
    Class insert() default void.class;
    
    /**
     * A Default Delete Object Query Redirector will be applied to any executing
     * DeleteObjectQuery or DeleteAllQuery that does not have a redirector set directly on the query.
     * Query redirectors allow the user to intercept query execution preventing
     * it or alternately performing some side effect like auditing.
     */
    Class delete() default void.class;

}
