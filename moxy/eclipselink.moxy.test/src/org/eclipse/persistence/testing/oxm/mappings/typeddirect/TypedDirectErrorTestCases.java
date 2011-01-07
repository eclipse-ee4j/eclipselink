/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.typeddirect;

import java.io.InputStream;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class TypedDirectErrorTestCases extends OXTestCase {
    private String xmlResource = "org/eclipse/persistence/testing/oxm/mappings/typeddirect/testObjectError.xml";

    public TypedDirectErrorTestCases(String name) {
        super(name);
    }

    public void testInvalidDateFormat() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);

        TypedDirectMappingTestProject project = new TypedDirectMappingTestProject();
        XMLContext context = this.getXMLContext(project);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        try {
            unmarshaller.unmarshal(inputStream);
        } catch (ConversionException e) {
            return;
        }

        fail("A conversion exception should have been thrown but wasn't");
    }
}
