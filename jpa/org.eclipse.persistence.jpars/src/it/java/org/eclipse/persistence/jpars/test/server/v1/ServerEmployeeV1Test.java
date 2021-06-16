/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
