/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke/tware - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.service;

import java.util.Map;

import org.eclipse.persistence.jpa.rs.PersistenceContextFactory;
import org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;

public class JPARSPersistenceContextFactoryProvider implements
        PersistenceContextFactoryProvider {

    protected static PersistenceContextFactory factory = new PersistenceFactoryBase();
    
    @Override
    public PersistenceContextFactory getPersistenceContextFactory(Map<String, Object> properteis) {
        return factory;
    }

}

