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
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     07/15/2010-2.2 Guy Pelletier
//       -311395 : Multiple lifecycle callback methods for the same lifecycle event
//     08/11/2010-2.2 Guy Pelletier
//       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Temporal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.TemporalType.DATE;

@Entity
@Table(name="CMP3_ALPINE")
@TableGenerator(
  name="USED_TO_TEST_A_LOG_MESSAGE",
  //table="leave this commented out",
  pkColumnName="SEQ_NAME",
  valueColumnName="SEQ_COUNT",
  pkColumnValue="BEVERAGE_SEQ")
public class Alpine extends Beer<Integer, Double, Alpine> implements Cloneable, Serializable {
    public enum Classification { STRONG, BITTER, SWEET, NONE }

    private static final long serialVersionUID = -6105968902208325992L;
    private Date bestBeforeDate;
    private Classification classification;
    private SerialNumber serialNumber;
    private List<Date> inspectionDates;

    public static int ALPINE_PRE_PERSIST_COUNT = 0;
    public static int ALPINE_POST_PERSIST_COUNT = 0;

    protected Alpine(){}

    public Alpine(SerialNumber serialNumber) {
        inspectionDates = new ArrayList<>();
        setId(serialNumber.getNumber());
        serialNumber.setAlpine(this);
        setSerialNumber(serialNumber);
    }

    @Override
    public Alpine clone() throws CloneNotSupportedException {
        return (Alpine)super.clone();
    }

    public void addInspectionDate(Date date) {
        getInspectionDates().add(date);
    }

    @Override
    @PrePersist
    public void celebrate() {
        if (classification != null && classification == Classification.NONE) {
            classification = Classification.STRONG;
        }

        ALPINE_PRE_PERSIST_COUNT++;
    }

    @Override
    @PostPersist
    public void celebrateAgain() {
        ALPINE_POST_PERSIST_COUNT++;
    }

    @Column(name="BB_DATE")
    @Temporal(DATE)
    public Date getBestBeforeDate() {
        return bestBeforeDate;
    }

    public Classification getClassification() {
        return classification;
    }

    @Column(name="I_DATES")
    public List<Date> getInspectionDates() {
        return inspectionDates;
    }

    @OneToOne(cascade=ALL)
    @PrimaryKeyJoinColumn
    public SerialNumber getSerialNumber() {
        return serialNumber;
    }

    public void setBestBeforeDate(Date bestBeforeDate) {
        this.bestBeforeDate = bestBeforeDate;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public void setInspectionDates(List<Date> inspectionDates) {
        this.inspectionDates = inspectionDates;
    }

    protected void setSerialNumber(SerialNumber serialNumber) {
        this.serialNumber = serialNumber;
    }
}
