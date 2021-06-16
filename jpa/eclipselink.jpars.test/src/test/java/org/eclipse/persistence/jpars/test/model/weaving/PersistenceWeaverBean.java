/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      dclarke/tware - initial
package org.eclipse.persistence.jpars.test.model.weaving;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

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
