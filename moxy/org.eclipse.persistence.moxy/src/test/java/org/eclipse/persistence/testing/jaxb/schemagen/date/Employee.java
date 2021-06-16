/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     bdoughan - March 16/2010 - 2.0.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.date;

import jakarta.xml.bind.annotation.XmlRootElement;

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
