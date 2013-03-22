/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     ailitchev - 2010/08/19
 *          Bug 322960 - TWO TESTS IN CUSTOMFEATURESJUNITTESTSUITE FAILED WITH 11.2.0.2 DRIVER 
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import java.sql.SQLException;
import java.sql.SQLXML;

/**
 *  INTERNAL:
 *  Implements operations specific to JDK 1.6
 */
public class JDK16Platform extends JDK15Platform {

    /**
     * Indicates whether the passed object implements java.sql.SQLXML introduced in jdk 1.6
     */
    public boolean isSQLXML(Object object) {
        return (object instanceof SQLXML);
    }

    /**
     * Casts the passed object to SQLXML and calls getString and free methods
     */
    public String getStringAndFreeSQLXML(Object sqlXml) throws SQLException { 
        String str = ((SQLXML)sqlXml).getString();
        ((SQLXML)sqlXml).free();
        return str;
    }
}
