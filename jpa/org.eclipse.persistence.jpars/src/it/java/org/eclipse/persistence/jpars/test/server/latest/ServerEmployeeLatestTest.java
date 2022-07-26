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
package org.eclipse.persistence.jpars.test.server.latest;

import org.eclipse.persistence.jpars.test.server.ServerEmployeeTestBase;
import org.eclipse.persistence.jpars.test.server.v2.ServerEmployeeV2Test;
import org.junit.BeforeClass;

/**
 * ServerEmployeeTestBase from JPARS 2.0 adapted for 'latest' version.
 * {@link ServerEmployeeTestBase}
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ServerEmployeeLatestTest extends ServerEmployeeV2Test {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_employee-static", "latest");
    }
}
