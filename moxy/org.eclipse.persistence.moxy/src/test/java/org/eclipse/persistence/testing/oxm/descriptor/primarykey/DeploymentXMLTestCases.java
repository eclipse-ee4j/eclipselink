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
//     rbarkhouse - 2009-09-28 11:27:00 - initial implementation
package org.eclipse.persistence.testing.oxm.descriptor.primarykey;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;

import junit.framework.TestCase;

public class DeploymentXMLTestCases extends TestCase {

    private final static String DEPLOYMENT_XML = "org/eclipse/persistence/testing/oxm/descriptor/primarykey/project.xml";

    /**
     * Test to ensure that we can read in EclipseLink 1.1-era PK information.
     * In 1.1 PKs were written to Deployment XML using xsi:type="column", which
     * was changed in 1.2 to use xsi:type="node".
     */
    public void testDescriptorPKBackwardsCompatibility() {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(DEPLOYMENT_XML);
            Project project = XMLProjectReader.read(new InputStreamReader(inputStream));

            XMLContext xmlContext = new XMLContext(project);

            ClassDescriptor customerDescriptor = project.getDescriptorForAlias("Customer");
            List<DatabaseField> pkFields = customerDescriptor.getPrimaryKeyFields();

            assertTrue("PK fields not set as expected.", pkFields.size() == 1);
        } catch (IntegrityException e) {
            fail("Error initializing project from Deployment XML.");
        }
    }

}
