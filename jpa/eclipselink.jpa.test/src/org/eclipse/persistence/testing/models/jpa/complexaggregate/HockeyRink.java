/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
//
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_HOCKEY_RINK")
public class HockeyRink {
    
    @Id
    protected int id;
    
    @OneToOne
    @JoinColumn(name="HOCKEY_PUCK_ID")
    protected HockeyPuck puck;
    
    public HockeyRink() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HockeyPuck getPuck() {
        return puck;
    }

    public void setPuck(HockeyPuck puck) {
        this.puck = puck;
    }

    public String toString() {
        return getClass().getSimpleName() + " id:[" + id + "] hashcode:[" + System.identityHashCode(this) + "]";
    }
    
}
