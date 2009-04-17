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
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/

package javax.persistence;

import javax.persistence.criteria.Expression;

/**
 * Type for query parameters.
 * 
 * @param <T>
 *            the type of the parameter
 *            
 * @since Java Persistence 2.0
 */
public interface Parameter<T> extends Expression<T> {
    /**
     * Return the parameter name, or null if the parameter is not a named
     * parameter.
     * 
     * @return parameter name
     */
    String getName();

    /**
     * Return the parameter position, or null if the parameter is not a
     * positional parameter.
     * 
     * @return position of parameter
     */
    Integer getPosition();
}