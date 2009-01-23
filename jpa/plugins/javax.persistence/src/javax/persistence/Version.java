/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     pkrogh -        Java Persistence API 2.0 Public Draft
 *                     Specification and licensing terms available from
 *                     http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation specifies the version field or property of 
 * an entity class that serves as its optimistic lock value. 
 * The version is used to ensure integrity when performing the 
 * merge operation and for optimistic concurrency control. 
 *
 * <p> Only a single <code>Version</code> property or field 
 * should be used per class; applications that use more than one 
 * <code>Version</code> property or field will not be portable. 
 * 
 * <p> The <code>Version</code> property should be mapped to 
 * the primary table for the entity class; applications that 
 * map the <code>Version</code> property to a table other than 
 * the primary table will not be portable.
 * 
 * <p> The following types are supported for version properties: 
 * <code>int</code>, {@link Integer}, <code>short</code>, 
 * {@link Short}, <code>long</code>, {@link Long}, 
 * {@link java.sql.Timestamp Timestamp}.
 *
 * <pre>
 *    Example:
 *
 *    &#064;Version
 *    &#064;Column(name="OPTLOCK")
 *    protected int getVersionNum() { return versionNum; }
 * </pre>
 *
 * @since Java Persistence API 1.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)

public @interface Version {}
