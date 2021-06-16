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
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Temporal;

import static jakarta.persistence.GenerationType.*;
import static jakarta.persistence.TemporalType.DATE;

@Entity
@Table(name="CMP3_SERIAL_NUMBER")
public class SerialNumber implements Serializable{
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
