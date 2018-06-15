/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     mmacivor - Initial implementation
package org.eclipse.persistence.testing.oxm.deploymentxml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.deferred.DeferredContentHandlerTestCases;

public class DeploymentXMLXsiTypeTestSuite extends TestCase {
        public static Test suite() {
            TestSuite suite = new TestSuite("DeploymentXMLXsiTypeTestSuite");

            suite.addTestSuite(DeploymentXMLXsiTypeTestCases.class);
            return suite;
        }

}
