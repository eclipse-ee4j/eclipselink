/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation.
package org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table (name="ENTITYZ_CP")
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
