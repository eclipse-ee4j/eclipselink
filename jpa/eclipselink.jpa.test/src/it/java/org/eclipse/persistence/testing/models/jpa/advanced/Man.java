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
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;

import jakarta.persistence.*;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.*;

@Entity
public class Man implements Serializable {
    private int id;
    private String firstName;
    private String lastName;
    private PartnerLink partnerLink;

    public Man() {
    }

    public Man(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    public int getId() {
        return id;
    }

    @Column(name = "F_NAME")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    @Column(name = "L_NAME")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    @OneToOne(mappedBy = "man", cascade = ALL)
    public PartnerLink getPartnerLink() {
        return partnerLink;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPartnerLink(PartnerLink partnerLink) {
        this.partnerLink = partnerLink;
    }

    @Override
    public String toString() {
        return "Man [id=" + id + ", firstName=" + firstName + ", lastName="
                + lastName + "]";
    }
}
