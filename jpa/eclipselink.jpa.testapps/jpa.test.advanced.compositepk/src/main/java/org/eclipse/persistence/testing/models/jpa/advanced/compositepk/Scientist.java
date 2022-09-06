/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name="CMP3_SCIENTIST")
@IdClass(org.eclipse.persistence.testing.models.jpa.advanced.compositepk.ScientistPK.class)
public class Scientist {
    private int idNumber;
    private String firstName;
    private String lastName;
    private Cubicle cubicle;
    private Department department;

    public Scientist() {}

    @Id
    @GeneratedValue(strategy=SEQUENCE, generator="SCIENTIST_SEQUENCE_GENERATOR")
    @SequenceGenerator(name="SCIENTIST_SEQUENCE_GENERATOR", sequenceName="SCIENTIST_SEQ", allocationSize=1)
    @Column(name="ID_NUMBER")
    public int getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    @Id
    @Column(name="F_NAME")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Id
    @Column(name="L_NAME")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="DEPT_NAME", referencedColumnName="NAME"),
        @JoinColumn(name="DEPT_ROLE", referencedColumnName="DROLE"),
        @JoinColumn(name="DEPT_LOCATION", referencedColumnName="LOCATION")
    })
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @OneToOne
    @JoinColumns({
        @JoinColumn(name="CUBE_ID", referencedColumnName="ID"),
        @JoinColumn(name="CUBE_CODE", referencedColumnName="CODE")
    })
    public Cubicle getCubicle() {
        return cubicle;
    }

    public void setCubicle(Cubicle cubicle) {
        this.cubicle = cubicle;
    }

    public ScientistPK getPK() {
        return new ScientistPK(idNumber, firstName, lastName);
    }
}
