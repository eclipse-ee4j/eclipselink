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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import java.io.StringWriter;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLContextTestProject;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

public class XMLContextStoreXMLDescriptorSimpleType extends XMLContextConstructorUsingXMLSessionConfigLoader {
    public XMLContextStoreXMLDescriptorSimpleType(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLContextStoreXMLDescriptorSimpleType" };
        TestRunner.main(arguments);
    }

    public void testMultipleSessionNames1() {
        XMLContext context = new XMLContext(new XMLContextTestProject());

        assertNotNull(context);

        XMLMarshaller marshaller = context.createMarshaller();
        StringWriter writer1 = new StringWriter();
        marshaller.marshal(new EmailAddress(), writer1);
    }
}
