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
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpars.test.server.v1;

import org.eclipse.persistence.jpars.test.server.noversion.ServerEmployeeTest;
import org.junit.BeforeClass;

/**
 * ServerEmployeeTest adapted for JPARS 1.0.
 * {@see ServerEmployeeTest}
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ServerEmployeeV1Test extends ServerEmployeeTest {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_employee-static", "v1.0");
    }
}
