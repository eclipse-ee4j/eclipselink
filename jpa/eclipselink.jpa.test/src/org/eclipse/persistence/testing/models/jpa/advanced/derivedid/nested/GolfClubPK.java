/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/13/2010-2.2 Guy Pelletier 
 *       - 296078: JPA 2.0 with @MapsId, em.persist generates Internal Exception IllegalArgumentException
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid.nested;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable                                        
public class GolfClubPK {
    @Column(name="HEAD_ID")
    private int headId;

    @Column(name="SHAFT_ID")
    private int shaftId;

    public int getHeadId() {
        return headId;
    }

    public int getShaftId() {
        return shaftId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    public void setShaftId(int shaftId) {
        this.shaftId = shaftId;
    }
}