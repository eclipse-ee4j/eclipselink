/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Temporal;

import java.io.Serializable;
import java.util.Date;

import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.TemporalType.DATE;

@Entity
@Table(name="CMP3_SERIAL_NUMBER")
public class SerialNumber implements Serializable {

    private static final long serialVersionUID = 3026613265580571501L;
    private Alpine alpine;
    private Integer number;
    private Date issueDate;

    public static int SERIAL_NUMBER_PRE_PERSIST_COUNT = 0;

    public SerialNumber() {}

    @Id
    @Column(name="S_NUMBER")
    @GeneratedValue(strategy=TABLE, generator="SERIALNUMBER_TABLE_GENERATOR")
    @TableGenerator(
        name="SERIALNUMBER_TABLE_GENERATOR",
        table="CMP3_SERIAL_SEQ",
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
    @Temporal(DATE)
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
