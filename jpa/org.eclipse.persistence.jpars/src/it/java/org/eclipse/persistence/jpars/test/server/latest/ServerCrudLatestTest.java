/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//      Dmitry Kornilov - Initial implementation
//      Dmitry Kornilov - Upgrade to Jersey 2.x
package org.eclipse.persistence.jpars.test.server.latest;

import org.eclipse.persistence.jpars.test.server.ServerCrudTestBase;
import org.eclipse.persistence.jpars.test.server.v2.ServerCrudV2Test;
import org.eclipse.persistence.jpars.test.util.StaticModelDatabasePopulator;
import org.junit.BeforeClass;

/**
 * ServerCrudTestBase modified for JPARS v2.0.
 * {@link ServerCrudTestBase}
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ServerCrudLatestTest extends ServerCrudV2Test {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_auction-static", "latest");
        StaticModelDatabasePopulator.populateDB(emf);
    }
}
