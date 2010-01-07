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
 *     rbarkhouse - 2009-09-28 11:27:00 - initial implementation
 ******************************************************************************/
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
