/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     12/17/2010-2.2 Guy Pelletier 
 *       - 330755: Nested embeddables can't be used as embedded ids
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;

import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;

@Embeddable
public class Torso {
    @GeneratedValue
    public int count;
    
    @Embedded 
    public Heart heart;

    public Torso() {}
    
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof Torso) {
            Torso torso = (Torso) objectToCompare;

            if (count != torso.getCount()) {
                return false;
            }
            
            return heart.equals(torso.getHeart());
        }

        return false;
    }
    
    public Torso(int count, Heart heart) {
        this.count = count;
        this.heart = heart;
    }
    
    public int getCount() {
        return count;
    }

    public Heart getHeart() {
        return heart;
    }
    
    public void setCount(int count) {
        this.count = count;
    }

    public void setHeart(Heart heart) {
        this.heart = heart;
    }
    
}
