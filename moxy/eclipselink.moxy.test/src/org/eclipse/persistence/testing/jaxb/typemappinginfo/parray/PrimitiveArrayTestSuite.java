/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 05 September 2012 - 2.4 - Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.jaxb.typemappinginfo.parray;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PrimitiveArrayTestSuite extends TestCase {

    public PrimitiveArrayTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Primitive Array TypeMappingInfo Test Suite");
        suite.addTestSuite(StringArrayTestsCases.class);
        suite.addTestSuite(StringArrayNilTestsCases.class);
        suite.addTestSuite(StringArraySingleNodeTestsCases.class);
        suite.addTestSuite(PrimitiveIntArrayTestsCases.class);
        return suite;
    }

}