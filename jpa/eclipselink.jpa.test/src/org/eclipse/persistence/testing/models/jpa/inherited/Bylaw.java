/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/18/2010-2.2 Guy Pelletier 
 *       - 300458: EclispeLink should throw a more specific exception than NPE
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Bylaw {
    public String city;
    public int number;
    
    @Id
    public String getCity() {
        return city;
    }
    
    @Id
    @Column(name="NUMB")
    @GeneratedValue
    public int getNumber() {
        return number;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
}
