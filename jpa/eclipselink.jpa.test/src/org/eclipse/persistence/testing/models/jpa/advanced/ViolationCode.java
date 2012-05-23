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
 *     06/16/2010-2.2 Guy Pelletier 
 *       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="VIOLATION_CODE")
public class ViolationCode {
    public enum ViolationCodeId {A, B, C, D}
    
    @Id
    @Enumerated
    public ViolationCodeId id;    
    
    @Column(name="DESCRIP")
    public String description;

    public String getDescription() {
        return description;
    }
    
    public ViolationCodeId getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setId(ViolationCodeId id) {
        this.id = id;
    }   
}
