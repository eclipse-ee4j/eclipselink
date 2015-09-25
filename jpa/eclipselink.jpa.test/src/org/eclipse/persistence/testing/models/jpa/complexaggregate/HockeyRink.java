/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 *     
 ******************************************************************************/
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
