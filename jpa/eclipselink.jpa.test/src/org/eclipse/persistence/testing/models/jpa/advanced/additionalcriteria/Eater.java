/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     15/08/2011-2.3.1 Guy Pelletier 
 *       - 298494: JPQL exists subquery generates unnecessary table join
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import static javax.persistence.FetchType.EAGER;

import org.eclipse.persistence.annotations.AdditionalCriteria;

@Entity
@Table(name="JPA_AC_EATER")
@DiscriminatorValue("E")
@PrimaryKeyJoinColumn(name="E_ID", referencedColumnName="P_ID")
@AdditionalCriteria("this.name like :EATER_NAME")
public class Eater extends Person {
    @Column(name="E_NAME")
    public String name;

    @OneToOne
    @JoinColumn(name="SANDWICH_ID")
    public Sandwich sandwhich;

    public String getName() {
        return name;
    }
    
    public Sandwich getSandwhich() {
        return sandwhich;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setSandwhich(Sandwich sandwhich) {
        this.sandwhich = sandwhich;
    }
}
