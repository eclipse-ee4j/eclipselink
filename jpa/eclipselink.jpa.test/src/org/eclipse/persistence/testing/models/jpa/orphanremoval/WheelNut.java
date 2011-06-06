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
 *     05/1/2009-2.0 Guy Pelletier/David Minsky
 *       - 249033: JPA 2.0 Orphan removal
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.orphanremoval;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import static javax.persistence.GenerationType.TABLE;

@Entity(name="OR_WheelNut")
@Table(name="JPA_OR_WHEEL_NUT")
public class WheelNut {
    @Id
    @GeneratedValue(strategy=TABLE, generator="JPA_OR_WHEEL_NUT_TABLE_GENERATOR")
    @TableGenerator(
        name="JPA_OR_WHEEL_NUT_TABLE_GENERATOR", 
        table="JPA_ORPHAN_REMOVAL_SEQUENCE",
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="WHEEL_NUT_SEQ"
    )
    protected int id;
    
    @ManyToOne
    protected Wheel wheel;
    
    public WheelNut() {
        super();
    }

    public int getId() {
        return id;
    }

    public Wheel getWheel() {
        return wheel;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setWheel(Wheel wheel) {
        this.wheel = wheel;
    }    
    
    public String toString() {
        return "WheelNut ["+ id +"]";
    }
}
