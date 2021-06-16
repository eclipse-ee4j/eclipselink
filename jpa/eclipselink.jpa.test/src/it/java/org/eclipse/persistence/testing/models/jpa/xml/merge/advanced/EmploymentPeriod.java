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
package org.eclipse.persistence.testing.models.jpa.xml.merge.advanced;

import java.sql.Date;
import java.io.*;
import jakarta.persistence.*;

/**
 * This class is used to test XML and annotation merging. This class is mapped
 * in: eclipselink-xml-merge-model/orm-annotation-merge-advanced-entity-mappings.xml
 *
 * This class is currently marked as metadata-complete=true meaning all the
 * annotations defined here should be ignored (somewhat defeating the purpose
 * of XML and Annotation merging testing)
 *
 * Also there are no automated tests that go along with these models, see the
 * test suite: EntityMappingsMergeAdvancedJUnitTestCase. It tests through
 * inspecting descriptor settings only and by no means does extensive
 * validation of all the metadata and defaults.
 */
@Embeddable
@Table(name="CMP3_ANN_MERGE_EMPLOYEE")
public class EmploymentPeriod implements Serializable {
    private Date startDate;
    private Date endDate;

    public EmploymentPeriod() {}

    /**
     * Return a new employment period instance.
     * The constructor's purpose is to allow only valid instances of a class to
     * be created. Valid means that the get/set and clone/toString methods
     * should work on the instance. Arguments to constructors should be avoided
     * unless those arguments are required to put the instance into a valid
     * state, or represent the entire instance definition.
     */
    public EmploymentPeriod(Date theStartDate, Date theEndDate) {
        startDate = theStartDate;
        endDate = theEndDate;
    }

    @Column(name="ANN_MERGE_S_DATE")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date date) {
        this.startDate = date;
    }

    @Column(name="ANN_MERGE_E_DATE")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date date) {
        this.endDate = date;
    }

    /**
     * Print the start & end date
     */
    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();
        writer.write("EmploymentPeriod: ");

        if (this.getStartDate() != null) {
            writer.write(this.getStartDate().toString());
        }

        writer.write("-");

        if (this.getEndDate() != null) {
            writer.write(this.getEndDate().toString());
        }

        return writer.toString();
    }
}
