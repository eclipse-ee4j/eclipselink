/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - initial
package org.eclipse.persistence.jpars.test.model.traveler;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@NamedQueries({
        @NamedQuery(
                name = "Traveler.deleteAll",
                query = "DELETE FROM Traveler t")
})
@Entity
@Table(name = "JPARS_TRAVELER")
@XmlRootElement(namespace = "http://example.org")
public class Traveler {

    @Id
    @Column(name = "TRV_ID")
    @GeneratedValue
    private int id;

    @Column(name = "F_NAME")
    @XmlElement(namespace = "http://example.org/fname")
    private String firstName;

    @Column(name = "L_NAME")
    private String lastName;

    @Version
    private Long version;

    @OneToOne(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    @JoinColumn(name = "RSRV_ID")
    private Reservation reservation;

    public Traveler() {
    }

    public Traveler(int id, String firstName, String lastName) {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
    }

    public int getId() {
        return id;
    }

    public void setId(int empId) {
        this.id = empId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String fName) {
        this.firstName = fName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lName) {
        this.lastName = lName;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String toString() {
        return "Traveler(" + id + ": " + firstName + ", " + lastName + ")";
    }
}
