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
//      Dmitry Kornilov - initial implementation
package org.eclipse.persistence.jpars.test.server.latest;

import org.eclipse.persistence.jpars.test.server.v2.ServerPageableTest;
import org.junit.BeforeClass;

/**
 * This class tests paging feature introduced in V2.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ServerPageableLatestTest extends ServerPageableTest {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_basket-static", "latest");
        initData();
    }
}
