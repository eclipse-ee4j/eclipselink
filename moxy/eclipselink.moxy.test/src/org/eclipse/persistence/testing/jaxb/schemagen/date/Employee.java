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
//     bdoughan - March 16/2010 - 2.0.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employee {

    private java.util.Date utilDateProperty;
    private java.sql.Date sqlDateProperty;
    private java.sql.Time sqlTimeProperty;
    private java.sql.Timestamp sqlTimestampProperty;

    public java.util.Date getUtilDateProperty() {
        return utilDateProperty;
    }

    public void setUtilDateProperty(java.util.Date utilDateProperty) {
        this.utilDateProperty = utilDateProperty;
    }

    public java.sql.Date getSqlDateProperty() {
        return sqlDateProperty;
    }

    public void setSqlDateProperty(java.sql.Date sqlDateProperty) {
        this.sqlDateProperty = sqlDateProperty;
    }

    public java.sql.Time getSqlTimeProperty() {
        return sqlTimeProperty;
    }

    public void setSqlTimeProperty(java.sql.Time sqlTimeProperty) {
        this.sqlTimeProperty = sqlTimeProperty;
    }

    public java.sql.Timestamp getSqlTimestampProperty() {
        return sqlTimestampProperty;
    }

    public void setSqlTimestampProperty(java.sql.Timestamp sqlTimestampProperty) {
        this.sqlTimestampProperty = sqlTimestampProperty;
    }

}
