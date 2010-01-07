/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-09-28 14:07:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.descriptor.primarykey;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbyname.RootElementIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbynamespace.RootElementIdentifiedByNamespaceTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsAlwaysWrapTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases2;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases3;

public class PrimaryKeyTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Primary Key Test Suite");
        suite.addTestSuite(DeploymentXMLTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.descriptor.primarykey.PrimaryKeyTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

}