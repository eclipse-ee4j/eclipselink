/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="JPA21_ENGINEER")
@DiscriminatorValue("2")
public class Engineer extends Person {
    private String title;
    private Company company;
    private List<Laptop> laptops;
    private List<Desktop> desktops;

    @ManyToOne
    public Company getCompany() {
        return company;
    }

    @Column(name="TITLE")
    public String getTitle() {
        return title;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name="JPA21_ENGINEER_LAPTOP",
        joinColumns=@JoinColumn(name="ENGINEER_ID", referencedColumnName="ID"),
        inverseJoinColumns={
            @JoinColumn(name="LAPTOP_MFR", referencedColumnName = "MFR"),
            @JoinColumn(name="LAPTOP_SNO", referencedColumnName = "SNO")
        }
    )
    public List<Laptop> getLaptops() {
        return laptops;
    }

    public void setLaptops(List<Laptop> laptops) {
        this.laptops = laptops;
    }

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name="JPA21_ENGINEER_DESKTOP",
        joinColumns=@JoinColumn(name="ENGINEER_ID", referencedColumnName="ID"),
        inverseJoinColumns={
            @JoinColumn(name="DESKTOP_MFR", referencedColumnName="MFR"),
            @JoinColumn(name="DESKTOP_SNO", referencedColumnName="DT_SNO")
        }
    )
    public List<Desktop> getDesktops() {
        return desktops;
    }

    public void setDesktops(List<Desktop> desktops) {
        this.desktops = desktops;
    }
}
