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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class PersonalVitals implements Serializable {
    private int age;
    private double weight;
    private double height;
    
    public PersonalVitals() {}
    
    @Column(name="AGE")
    public int getAge() {
        return age;
    }
    
    @Column(name="WEIGHT")
    public double getWeight() {
        return weight;
    }
    
    @Column(name="HEIGHT")
    public double getHeight() {
        return height;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public String toString() {
        return "Personal Vitals: " + getWeight() + " lbs,  " + getHeight() + " meters, " + getAge() + " years old";
    }
}
