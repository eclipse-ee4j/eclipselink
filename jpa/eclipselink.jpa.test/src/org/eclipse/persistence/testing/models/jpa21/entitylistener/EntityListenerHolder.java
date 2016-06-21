/*******************************************************************************
 * Copyright (c) 2012, 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial contribution for Bug 366748 - JPA 2.1 Injectable Entity Listeners
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa21.entitylistener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ENT_LIS_HOLD")
@EntityListeners({EntityListener.class})
public class EntityListenerHolder implements EntityListenerHolderInterface {

    @Id
    @GeneratedValue
    private int id;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
