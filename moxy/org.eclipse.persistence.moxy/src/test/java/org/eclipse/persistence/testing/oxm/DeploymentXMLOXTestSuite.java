/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.converter.ConverterTestSuite;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.RootElementTestSuite;
import org.eclipse.persistence.testing.oxm.documentpreservation.DocumentPreservationTestSuite;
import org.eclipse.persistence.testing.oxm.inheritance.InheritanceTestSuite;
import org.eclipse.persistence.testing.oxm.readonly.ReadOnlyTestSuite;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.*;
import org.eclipse.persistence.testing.oxm.mappings.DeploymentXMLMappingTestSuite;

public class DeploymentXMLOXTestSuite extends TestCase
{

    public DeploymentXMLOXTestSuite(String name){
        super(name);
    }

  public static Test suite(){
    TestSuite suite = new TestSuite("Deployment XML OX Test Suite");

    suite.addTest(RootElementTestSuite.suite());
    suite.addTest(InheritanceTestSuite.suite());
        suite.addTest(ConverterTestSuite.suite());
        suite.addTest(DocumentPreservationTestSuite.suite());
        suite.addTest(ReadOnlyTestSuite.suite());
        suite.addTestSuite(XMLMarshalTestCases.class);
        suite.addTestSuite(XMLUnmarshalTestCases.class);

    return suite;
  }
}

