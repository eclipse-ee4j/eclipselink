/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.refresh;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RefreshTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Refresh Test Suite");
        suite.addTestSuite(RefreshTestCases.class);
        suite.addTestSuite(NonRefreshableMetadataTestCases.class);
        suite.addTestSuite(BinderTestCases.class);
        return suite;
    }

}