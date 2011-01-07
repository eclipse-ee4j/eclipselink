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
 *     tware - added cascaded locking testing
 ******************************************************************************/
package org.eclipse.persistence.testing.models.optimisticlocking;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

public class VetAppointment {

    private int id;
    private int cost;
    private int version;
    private ValueHolderInterface animal;

    public VetAppointment(){
        animal = new ValueHolder();
    }
    
    public ValueHolderInterface getAnimal() {
        return animal;
    }

    public void setAnimal(ValueHolderInterface animalvh) {
        this.animal = animalvh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }

}
