/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - March 16/2010 - 2.0.2 - Initial implementation
******************************************************************************/
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