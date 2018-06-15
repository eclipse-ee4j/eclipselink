/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     06/20/2014-2.5.2 Rick Curtis
//       - 437760: AttributeOverride with no column name defined doesn't work.
package org.eclipse.persistence.testing.models.jpa22.advanced;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class EmploymentPeriod implements Serializable, Cloneable {
    @Column(name="NON_DEFAULT_START_DATE")
    private Date startDate;
    private Date endDate;
    
    @OneToMany
    private Collection<LargeProject> largeProjectsWorkedOn;
    
    @OneToMany
    private Collection<SmallProject> smallProjectsWorkedOn;

    public EmploymentPeriod() {
        largeProjectsWorkedOn = new ArrayList<>();
        smallProjectsWorkedOn = new ArrayList<>();
    }

    public EmploymentPeriod(Date theStartDate, Date theEndDate) {
        startDate = theStartDate;
        endDate = theEndDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmploymentPeriod that = (EmploymentPeriod) o;

        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (largeProjectsWorkedOn != null ? !largeProjectsWorkedOn.equals(that.largeProjectsWorkedOn) : that.largeProjectsWorkedOn != null)
            return false;
        if (smallProjectsWorkedOn != null ? !smallProjectsWorkedOn.equals(that.smallProjectsWorkedOn) : that.smallProjectsWorkedOn != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startDate != null ? startDate.hashCode() : 0;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (largeProjectsWorkedOn != null ? largeProjectsWorkedOn.hashCode() : 0);
        result = 31 * result + (smallProjectsWorkedOn != null ? smallProjectsWorkedOn.hashCode() : 0);
        return result;
    }

    @Column(name="E_DATE")
    public Date getEndDate() {
        return endDate;
    }

    @Column(name="S_DATE")
    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date date) {
        this.endDate = date;
    }

    public void setStartDate(Date date) {
        this.startDate = date;
    }

    public Collection<LargeProject> getLargeProjectsWorkedOn() {
        return largeProjectsWorkedOn;
    }

    public void setLargeProjectsWorkedOn(Collection<LargeProject> largeProjectsWorkedOn) {
        this.largeProjectsWorkedOn = largeProjectsWorkedOn;
    }

    public Collection<SmallProject> getSmallProjectsWorkedOn() {
        return smallProjectsWorkedOn;
    }

    public void setSmallProjectsWorkedOn(Collection<SmallProject> smallProjectsWorkedOn) {
        this.smallProjectsWorkedOn = smallProjectsWorkedOn;
    }

    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();
        writer.write("EmploymentPeriod: ");

        if (getStartDate() != null) {
            writer.write(this.getStartDate().toString());
        }

        writer.write("-");

        if (getEndDate() != null) {
            writer.write(this.getEndDate().toString());
        }

        writer.write("-");
        
        if (getSmallProjectsWorkedOn() != null) {
            writer.write('{');
            for (Project project : getSmallProjectsWorkedOn()) {
                writer.write(project.toString());
                writer.write(',');
            }
            writer.write('}');
        }

        writer.write("-");
        
        if (getLargeProjectsWorkedOn() != null) {
            writer.write('{');
            for (Project project : getLargeProjectsWorkedOn()) {
                writer.write(project.toString());
                writer.write(',');
            }
            writer.write('}');
        }

        return writer.toString();
    }
}
