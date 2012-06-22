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
 *     tware - added cascaded locking testing
 ******************************************************************************/
package org.eclipse.persistence.testing.models.optimisticlocking;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

public class Toy {

    private int id;
    private int version;
    private String name;
    private ValueHolderInterface owner;
    
    public Toy(){
        owner = new ValueHolder();
    }
    
    public ValueHolderInterface getOwnerVH() {
        return owner;
    }
    public void setOwnerVH(ValueHolderInterface owner) {
        this.owner = owner;
    }
    public Animal getOwner() {
        return (Animal)owner.getValue();
    }
    public void setOwner(Animal owner) {
        this.owner.setValue(owner);
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
