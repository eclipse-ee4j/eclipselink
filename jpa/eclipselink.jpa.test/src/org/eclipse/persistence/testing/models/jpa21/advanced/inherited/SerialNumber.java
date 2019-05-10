/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa21.advanced.inherited;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="JPA21_SERIAL_NUMBER")
public class SerialNumber implements Serializable{
    private Alpine alpine;
    private Integer number;
    private Date issueDate;

    public static int SERIAL_NUMBER_PRE_PERSIST_COUNT = 0;

    public SerialNumber() {}

    @Id
    @Column(name="S_NUMBER")
    @GeneratedValue(strategy=GenerationType.TABLE, generator="SERIALNUMBER_TABLE_GENERATOR")
    @TableGenerator(
        name="SERIALNUMBER_TABLE_GENERATOR",
        table="JPA21_SERIAL_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SERIAL_SEQ")
    public Integer getNumber() {
        return number;
    }

    @OneToOne(mappedBy="serialNumber")
    public Alpine getAlpine() {
        return alpine;
    }

    @Column(name="ISSUE_DATE")
    @Temporal(TemporalType.DATE)
    public Date getIssueDate() {
        return issueDate;
    }

    public void setAlpine(Alpine alpine) {
        this.alpine = alpine;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    protected void setNumber(Integer number) {
        this.number = number;
    }

    @PrePersist
    public void setupIssueDate() {
        if (issueDate == null) {
            setIssueDate(new Date());
        }

        SERIAL_NUMBER_PRE_PERSIST_COUNT++;
    }
}
