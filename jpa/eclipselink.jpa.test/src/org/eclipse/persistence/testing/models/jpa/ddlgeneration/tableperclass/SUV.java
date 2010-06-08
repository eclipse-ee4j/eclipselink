/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/22/2009 - Guy Pelletier/Prakash Selvaraj - added tests for DDL generation of table per class
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
  @NamedQuery(name = "SUV.findAll", query = "select o from SUV o")
})
@Inheritance
@DiscriminatorValue("SUV")
public class SUV extends Car implements Serializable 
{
    
   private Float groundClearence;
   
   private Float wheelBase;
   
   
    public SUV() {
    }

    public void setGroundClearence(Float groundClearence) {
        this.groundClearence = groundClearence;
    }

    public Float getGroundClearence() {
        return groundClearence;
    }

    public void setWheelBase(Float wheelBase) {
        this.wheelBase = wheelBase;
    }

    public Float getWheelBase() {
        return wheelBase;
    }
}
