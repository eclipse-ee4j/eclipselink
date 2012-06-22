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
 *     01/26/2011-2.3 Guy Pelletier 
 *       - 307664:  Lifecycle callbacks not called for object from IndirectSet
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inheritance.listeners;

import java.util.EventListener;
import javax.persistence.PostLoad;

import org.eclipse.persistence.testing.models.jpa.inheritance.DDD;
import org.eclipse.persistence.testing.models.jpa.inheritance.Vehicle;

public class DDDListener implements EventListener {
    @PostLoad
    public void postLoad(DDD ddd) {
        ddd.setCount2(ddd.getCount2()+1);
    }
}
