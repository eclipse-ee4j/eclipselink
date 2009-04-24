/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedById;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="JPA_LIEUTENANT_GENERAL")
public class LieutenantGeneral {
    @Id
    Integer id;

    @OneToOne
    @MappedById
    General general;
    
    public General getGeneral() {
        return general;
    }
    
    public Integer getId() {
        return id;
    }

    public void setGeneral(General general) {
        this.general = general;
        this.id = general.getGeneralId();
    }
    
    public void setId(Integer id) {
        this.id = id;
    }    
}
