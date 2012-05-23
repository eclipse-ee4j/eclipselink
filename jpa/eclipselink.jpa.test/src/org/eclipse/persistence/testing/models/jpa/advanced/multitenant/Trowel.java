/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     14/05/2012-2.4 Guy Pelletier  
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;

import static javax.persistence.TemporalType.DATE;
import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;

@Entity
@Table(name="JPA_TROWEL")
@Multitenant(TABLE_PER_TENANT)
// Default to suffix.
public class Trowel {
    public int id;
    public String type;
    public Mason mason;

    public Trowel() {}
    
    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }
    
    @OneToOne(mappedBy="trowel")
    public Mason getMason() {
        return mason;
    }
    
    @Column(name="TROWEL_TYPE")
    public String getType() {
        return type;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setMason(Mason mason) {
        this.mason = mason;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}

