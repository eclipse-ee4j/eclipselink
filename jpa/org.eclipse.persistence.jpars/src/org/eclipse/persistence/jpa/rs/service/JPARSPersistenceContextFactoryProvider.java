/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      dclarke/tware - initial implementation
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

