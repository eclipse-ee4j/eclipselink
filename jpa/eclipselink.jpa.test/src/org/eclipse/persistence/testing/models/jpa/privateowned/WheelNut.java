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
 *     02/19/09 dminsky - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.privateowned;

import javax.persistence.*;

@Entity(name="PO_WheelNut")
@Table(name="CMP3_PO_WHEEL_NUT")
public class WheelNut {

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CMP3_PO_WHEEL_NUT_TABLE_GENERATOR")
    @TableGenerator(
        name="CMP3_PO_WHEEL_NUT_TABLE_GENERATOR", 
        table="CMP3_PRIVATE_OWNED_SEQUENCE",
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

    public void setId(int id) {
        this.id = id;
    }

    public Wheel getWheel() {
        return wheel;
    }

    public void setWheel(Wheel wheel) {
        this.wheel = wheel;
    }
    
}
