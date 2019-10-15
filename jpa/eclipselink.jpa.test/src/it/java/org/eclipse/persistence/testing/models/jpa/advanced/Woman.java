/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;

import javax.persistence.*;

import static javax.persistence.GenerationType.*;

@Entity
public class Woman implements Serializable {
    private Integer id;
    private String firstName;
    private String lastName;
    private PartnerLink partnerLink;

    public Woman() {
    }

    public Woman(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    public Integer getId() {
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

    @OneToOne(mappedBy = "woman")
    public PartnerLink getPartnerLink() {
        return partnerLink;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPartnerLink(PartnerLink partnerLink) {
        this.partnerLink = partnerLink;
    }

    @Override
    public String toString() {
        return "Woman [id=" + id + ", firstName=" + firstName + ", lastName="
                + lastName + "]";
    }
}
