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
//     Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.helpercontext;

import junit.framework.TestCase;
import org.eclipse.persistence.sdo.helper.ApplicationAccessWLS;

/**
 * A set of ApplicationAccessWLS tests.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ApplicationAccessWLSTest extends TestCase {

    /**
     * This test checks that new ComponentInvocationContext method is used to get application name
     * in ApplicationAccessWLS. Mocked ComponentInvocationContext and ComponentInvocationContextManager are used.
     */
    public void testInitUsingCic() {
        final ApplicationAccessWLS appAccess = new ApplicationAccessWLS();
        assertEquals(appAccess.getApplicationName(Thread.currentThread().getContextClassLoader()), "ComponentInvocationContext");
    }

    /**
     * This test checks the situation there ComponentInvocationContext is not available. In this case
     * ApplicationAccess class is used. Mocked ApplicationAccess is used here.
     */
    public void testInitUsingApplicationAccess() throws Exception {
        final ApplicationAccessWLS appAccess = new ApplicationAccessWLS() {
            @Override
            public boolean initUsingCic() {
                return false;
            }
        };
        assertEquals(appAccess.getApplicationName(Thread.currentThread().getContextClassLoader()), "ApplicationAccess#1.0");
    }
}
