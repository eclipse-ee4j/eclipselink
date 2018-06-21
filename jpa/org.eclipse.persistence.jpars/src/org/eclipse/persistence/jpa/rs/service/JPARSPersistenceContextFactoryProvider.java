/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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

