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
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.TableGenerator;

@Entity
@Table(name="CMP3_BECKS_TAG")
public class BecksTag implements Cloneable {
    private Integer id;
    private String callNumber;
    
    public BecksTag() {}
    
    public BecksTag clone() throws CloneNotSupportedException {
        return (BecksTag) super.clone();
    }
    
    public boolean equals(Object anotherBecksTag) {
        if (anotherBecksTag.getClass() != BecksTag.class) {
            return false;
        }
        
        return (getId().equals(((BecksTag)anotherBecksTag).getId()));
    }
    
    @Column(name="CALL_NUMBER")
    public String getCallNumber() {
        return callNumber;
    }
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="BECKS_TAG_TABLE_GENERATOR")
    @TableGenerator(
        name="BECKS_TAG_TABLE_GENERATOR", 
        table="CMP3_BECKS_TAG_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="BECKS_TAG_SEQ")
    public Integer getId() {
        return id;
    }
    
    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
}
