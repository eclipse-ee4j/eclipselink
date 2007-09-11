/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import java.util.EventListener;

import javax.persistence.PostPersist;

public class BeerListener implements EventListener {
    // Listener class used to test the mapped-superclass entity-listener XML
    // element.
    
    public static int POST_PERSIST_COUNT = 0;

    public BeerListener() {
        super();
    }
    
    public void postPersist(Object obj) {
        POST_PERSIST_COUNT++;
    }    
}
