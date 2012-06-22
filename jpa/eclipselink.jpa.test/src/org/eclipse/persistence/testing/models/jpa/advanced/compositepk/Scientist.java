/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import static javax.persistence.GenerationType.*;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;

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
