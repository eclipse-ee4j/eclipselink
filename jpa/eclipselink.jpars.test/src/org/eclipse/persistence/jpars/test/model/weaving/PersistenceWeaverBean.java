/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke/tware - initial 
 ******************************************************************************/
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
}
