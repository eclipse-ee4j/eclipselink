/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 *  * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - testing for query downcasting
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_JALOPY")
@DiscriminatorValue("J")
public class Jalopy extends Car {

    protected int percentRust = 0;

    public int getPercentRust() {
        return percentRust;
    }

    public void setPercentRust(int percentRust) {
        this.percentRust = percentRust;
    }
    
    
}
