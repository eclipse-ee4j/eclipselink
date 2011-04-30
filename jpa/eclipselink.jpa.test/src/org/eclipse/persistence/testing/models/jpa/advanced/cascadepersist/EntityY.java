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
 *     Oracle - initial API and implementation.
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table (name="EntityY_CP")
public class EntityY {
    @Id
    private int id;
    
    private String yname;
    
    @OneToOne
    private EntityX entityX;
    
    @ManyToOne 
    private EntityZ yzEntityRelation;

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setYname(String yname) {
        this.yname = yname;
    }

    public String getYname() {
        return yname;
    }

    public void setEntityX(EntityX entityX) {
        this.entityX = entityX;
    }

    public EntityX getEntityX() {
        return entityX;
    }

    public void setYzEntityRelation(EntityZ yzEntityRelation) {
        this.yzEntityRelation = yzEntityRelation;
    }

    public EntityZ getYzEntityRelation() {
        return yzEntityRelation;
    }
}
