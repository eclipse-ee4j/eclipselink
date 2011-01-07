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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate;

import java.util.Iterator;

public class Parent {
    
    protected int id;
    protected Aggregate aggregate;
    
    public Parent() {
        super();
        this.aggregate = new Aggregate();
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public Aggregate getAggregate() {
        return aggregate;
    }

    public void setAggregate(Aggregate aggregate) {
        this.aggregate = aggregate;
    }
    
    public void addChild(Child child) {
        if (child != null && !getAggregate().getChildren().contains(child)) {
            getAggregate().getChildren().add(child);
            child.setParent(this);
        }
    }
    
    public void removeChild(Child child) {
        if (child != null && getAggregate().getChildren().contains(child)) {
            getAggregate().getChildren().remove(child);
            child.setParent(null);
        }
    }
    
    public void addRelative(Relative relative) {
        if (relative != null && !getAggregate().getRelatives().contains(relative)) {
            getAggregate().getRelatives().add(relative);
        }
    }
    
    public void removeRelative(Relative relative) {
        if (relative != null && getAggregate().getRelatives().contains(relative)) {
            getAggregate().getRelatives().remove(relative);
        }
    }
    
    public void removeAllRelatives() {
        for (Iterator<Relative> iterator = getAggregate().getRelatives().iterator(); iterator.hasNext();) {
            removeRelative(iterator.next());
        }
    }

}
