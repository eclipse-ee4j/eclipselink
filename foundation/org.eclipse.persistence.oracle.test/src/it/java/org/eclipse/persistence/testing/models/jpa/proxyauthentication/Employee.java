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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.proxyauthentication;

import java.io.Serializable;
import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name="PAS_CONN.JPA_PROXY_EMPLOYEE")

public class Employee implements Serializable {

    private Integer id;

    private String m_lastName;
    private String m_firstName;

    public Employee () {
    }

    public Employee(String firstName, String lastName){
        this();
        this.m_firstName = firstName;
        this.m_lastName = lastName;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="JPA_PROXY_EMPLOYEE_TABLE_GENERATOR")
    @TableGenerator(
        name="JPA_PROXY_EMPLOYEE_TABLE_GENERATOR",
        table="PAS_CONN.PROXY_EMPLOYEE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="PROXY_EMPLOYEE_SEQ",
        initialValue=5
    )

    @Column(name="EMP_ID")
    public Integer getId() {
        return id;
    }

    @Column(name="F_NAME")
    public String getFirstName() {
        return m_firstName;
    }

    @Column(name="L_NAME")
    public String getLastName() {
        return m_lastName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFirstName(String name) {
        this.m_firstName = name;
    }

    public void setLastName(String name) {
        this.m_lastName = name;
    }
}
