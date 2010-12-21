/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.models.jpa.inheritance;

import static javax.persistence.CascadeType.*;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="CMP3_ENGINEER")
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

    @ManyToMany(cascade={PERSIST, MERGE})
    @JoinTable(
        name="CMP3_ENGINEER_LAPTOP",
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
    
    @ManyToMany(cascade={PERSIST, MERGE})
    @JoinTable(
        name="CMP3_ENGINEER_DESKTOP",
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
