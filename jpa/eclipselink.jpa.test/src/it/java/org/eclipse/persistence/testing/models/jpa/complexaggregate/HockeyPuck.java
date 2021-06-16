/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
//
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="CMP3_HOCKEY_PUCK")
public class HockeyPuck {

    @Id
    protected int id; 
    protected String name;
    @Embedded
    protected HockeySponsor sponsor;
    
    public HockeyPuck() {
        super();
        // the Embeddable must be instantiated with real values
        sponsor = new HockeySponsor();
        sponsor.setName("none");
        sponsor.setSponsorshipValue(1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return getClass().getSimpleName() + " id:[" + id + "] name:[" + name + "] hashcode:[" + System.identityHashCode(this) + "] embedded: " + sponsor;
    }

    public HockeySponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(HockeySponsor sponsor) {
        this.sponsor = sponsor;
    }
    
}
