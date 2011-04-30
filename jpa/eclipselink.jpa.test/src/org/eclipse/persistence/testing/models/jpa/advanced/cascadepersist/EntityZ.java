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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table (name="EntityZ_CP")
public class EntityZ {
    @Id
    private int id;
    
    private String zname;
    
    @OneToMany (mappedBy="yzEntityRelation", cascade=CascadeType.PERSIST)
    private List<EntityY> ylist;

    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public void setYlist(List<EntityY> ylist) {
        this.ylist = ylist;
    }

    public List<EntityY> getYlist() {
        return ylist;
    }

    public void setZname(String zname) {
        this.zname = zname;
    }

    public String getZname() {
        return zname;
    }
    
    public void addYToList(EntityY y) {
        if (ylist == null) {
            ylist = new ArrayList<EntityY>();
        }
        
        ylist.add(y);
    }
    
    public void removeYFromList(EntityY y) {
        if (ylist == null) {
            ylist = new ArrayList<EntityY>();
        }
        
        ylist.remove(y);
    }
}
