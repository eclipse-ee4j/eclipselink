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
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

/**
 * This model tests Example #2 of the mapsId cases.
 * 
 * @author gpelleti
 */
@Entity
@Table(name="JPA_CAPTAIN")
public class Captain {
    @EmbeddedId 
    @AttributeOverride(name="name", column=@Column(name="someOtherName"))
    CaptainId id;
    
    @ManyToOne 
    @JoinColumns({
        @JoinColumn(name="FK1", referencedColumnName="F_NAME"),
        @JoinColumn(name="FK2", referencedColumnName="L_NAME")
    })
    @MapsId // use the default value of major to look up the id field.
    Major major;
    
    public CaptainId getId() {
        return id;
    }
    
    public Major getMajor() {
        return major;
    }
    
    public void setId(CaptainId id) {
        this.id = id;
    }

    public void setMajor(Major major) {
        this.major = major;
    }
}
