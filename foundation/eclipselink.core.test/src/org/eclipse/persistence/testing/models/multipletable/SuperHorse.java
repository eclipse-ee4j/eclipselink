/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributor:
 *     Andrei Ilitchev - bug 248858: Problem with Multiple tables in EclipseLink.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.multipletable;

public class SuperHorse extends Horse {
    protected int speed;
    
    public SuperHorse() {
        super();
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
 }
