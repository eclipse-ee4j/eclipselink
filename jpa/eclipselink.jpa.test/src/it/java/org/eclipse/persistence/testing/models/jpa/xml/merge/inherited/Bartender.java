/*
 * Copyright (c) 2011, 2020 Oracle and/or its affiliates. All rights reserved.
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
