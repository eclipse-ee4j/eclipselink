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
package org.eclipse.persistence.internal.helper;

import java.sql.SQLException;

/**
 *  INTERNAL:
 *  Interface which abstracts the version of the JDK we are on.
 *  This should only implement methods that are dependent on JDK version
 *  The implementers should implement the minimum amount of functionality required to
 *  allow support of multiple versions of the JDK.
 *  @see JDK15Platform
 *  @see JavaPlatform
 *  @author Tom Ware
 */
public interface JDKPlatform {

    /**
     * Conforming queries with LIKE will act differently in different JDKs.
     */
    Boolean conformLike(Object left, Object right);

    /**
     * Conforming queries with REGEXP will act differently in different JDKs.
     */
    Boolean conformRegexp(Object left, Object right);

    /**
     * Indicates whether the passed object implements java.sql.SQLXML introduced in jdk 1.6
     */
    boolean isSQLXML(Object object);

    /**
     * Casts the passed object to SQLXML and calls getString and free methods
     */
    String getStringAndFreeSQLXML(Object sqlXml) throws SQLException; 
}
