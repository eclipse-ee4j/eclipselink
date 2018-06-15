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
//      dclarke/tware - initial
package org.eclipse.persistence.jpars.test.model.weaving;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Forces weaving of persistence units
 * @author tware
 *
 */
@Startup
@Singleton
public class PersistenceWeaverBean {

    @SuppressWarnings("unused")
    @PersistenceUnit(unitName = "jpars_auction-static")
    private EntityManagerFactory emf;

    @SuppressWarnings("unused")
    @PersistenceUnit(unitName = "jpars_employee-static")
    private EntityManagerFactory emf2;

    @SuppressWarnings("unused")
    @PersistenceUnit(unitName = "jpars_traveler-static")
    private EntityManagerFactory emf3;

    @SuppressWarnings("unused")
    @PersistenceUnit(unitName = "jpars_basket-static")
    private EntityManagerFactory emf4;

}
