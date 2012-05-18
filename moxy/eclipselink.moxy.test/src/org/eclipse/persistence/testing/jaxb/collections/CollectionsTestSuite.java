/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.collections;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CollectionsTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Collections Test Suite");
        suite.addTestSuite(CollectionHolderTestCases.class);
        suite.addTestSuite(CollectionHolderPopulatedTestCases.class);
        suite.addTestSuite(CollectionHolderInitializedTestCases.class);
        suite.addTestSuite(CollectionHolderNillableTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersInitializedTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersNillableTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersNillableInitializedTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersOverrideTestCases.class);       
        suite.addTestSuite(CollectionHolderWrappersPopulatedTestCases.class);
        suite.addTestSuite(CollectionHolderInitializedELTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersNillableInitializedELTestCases.class);
        suite.addTestSuite(CollectionHolderWrappersInitializedELTestCases.class);
        suite.addTestSuite(CollectionHolderELTestCases.class);
        return suite;
    }

}