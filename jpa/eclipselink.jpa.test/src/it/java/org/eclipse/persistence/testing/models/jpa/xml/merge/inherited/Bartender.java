/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     07/06/2011-2.3.1 Guy Pelletier
//       - 349906: NPE while using eclipselink in the application
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import jakarta.persistence.Basic;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * This class is mapped in:
 * resource/eclipselink-ddl-generation-model/merge-inherited-consumer.xml
 */
@Entity
public class Bartender {
    @Id
    @GeneratedValue
    public int id;

    @Basic
    public String firstName;

    @Basic
    public String lastName;

    @Embedded
    public ProbationaryPeriod probationaryPeriod;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ProbationaryPeriod getProbationaryPeriod() {
        return probationaryPeriod;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setProbationaryPeriod(ProbationaryPeriod probationaryPeriod) {
        this.probationaryPeriod = probationaryPeriod;
    }

}
