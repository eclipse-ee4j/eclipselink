/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Version;

/*
 * intentionally not specified:
 * @Inheritance
 * @DiscriminiatorColumn
 */
@Entity
@Table(name = "TMP_REVIEW")
@SecondaryTable(name = "TMP_REVIEW_DETAILS", pkJoinColumns = @PrimaryKeyJoinColumn(name = "REVIEW_ID", referencedColumnName = "ID"))
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    protected int id;

    @Basic
    protected Date reviewDate;

    @Basic
    @Column(name = "TEXT")
    protected String reviewText;

    @Basic
    @Column(name = "SUCCESSRATE", table = "TMP_REVIEW_DETAILS")
    protected short successRate;

    @Version
    protected int version;

    public Review() {
    }

    public Review(int aId, Date aDate, String aText) {
        id = aId;
        reviewDate = aDate;
        reviewText = aText;
    }

    public int getId() {
        return id;
    }

    // for illegal tests only
    public void setId(int newId) {
        id = newId;
    }

    public short getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(short newRate) {
        successRate = newRate;
    }

    public int getVersion() {
        return version;
    }

    // for illegal tests only
    public void setVersion(int newVersion) {
        version = newVersion;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewDate(Date date) {
        reviewDate = date;
    }

    public void setReviewText(String string) {
        reviewText = string;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Review) {
            return ((Review) other).id == id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id;
    }
}
