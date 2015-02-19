/*******************************************************************************
 * Copyright (c) 2014, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - Initial implementation
 ******************************************************************************/
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
