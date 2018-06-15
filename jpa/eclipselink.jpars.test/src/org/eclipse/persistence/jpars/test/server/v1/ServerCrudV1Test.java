/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - Initial implementation, upgrade to Jersey 2.x
package org.eclipse.persistence.jpars.test.server.v1;

import org.eclipse.persistence.jpars.test.server.noversion.ServerCrudTest;
import org.eclipse.persistence.jpars.test.util.StaticModelDatabasePopulator;
import org.junit.BeforeClass;

/**
 * ServerCrudTest modified for JPARS v1.0.
 * {@see ServerCrudTest}
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ServerCrudV1Test extends ServerCrudTest {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_auction-static", "v1.0");
        StaticModelDatabasePopulator.populateDB(emf);
    }
}
