/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static javax.persistence.CascadeType.PERSIST;

/**
 * This model tests Example #5 of the mapsId cases.
 * 
 * @author gpelleti
 */
@Entity
@Table(name="JPA_BRIGADIER_GENERAL")
public class BrigadierGeneral {
    @EmbeddedId 
    GeneralId id;
    
    @OneToOne(cascade=PERSIST)
    @JoinColumns({
        @JoinColumn(name="FIRST_NAME", referencedColumnName="F_NAME"),
        @JoinColumn(name="LAST_NAME", referencedColumnName="L_NAME")
    })
    @MapsId("id")
    MajorGeneral majorGeneral;
    
    public GeneralId getId() {
        return id;
    }
    
    public MajorGeneral getMajorGeneral() {
        return majorGeneral;
    }
    
    public void setId(GeneralId id) {
        this.id = id;
    }

    public void setMajorGeneral(MajorGeneral majorGeneral) {
        this.majorGeneral = majorGeneral;
    }
}

