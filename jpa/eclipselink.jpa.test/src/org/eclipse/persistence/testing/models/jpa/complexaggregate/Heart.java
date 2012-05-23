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
 *     12/17/2010-2.2 Guy Pelletier 
 *       - 330755: Nested embeddables can't be used as embedded ids
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Heart {
    @Column(name="H_SIZE")
    public int size;

    public Heart() {}
    
    public Heart(int size) {
        this.size = size;
    }
    
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof Heart) {
            Heart heart = (Heart) objectToCompare;

            return size == heart.getSize();
        }

        return false;
    }
    
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
