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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.schematype;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import javax.xml.namespace.QName;
import java.nio.ByteBuffer;

public class SchemaTypeBase64TestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/schematype/SchemaTypeBase64.xml";

    public SchemaTypeBase64TestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project project = new SchemaTypeProject();

        XMLField field = new XMLField("bytes/text()");

        field.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);

        ((XMLDirectMapping)project.getDescriptor(ByteHolder.class).getMappingForAttributeName("bytes")).setField(field);

        setProject(project);
    }

    protected Object getControlObject() {
        ByteHolder byteHolder = new ByteHolder();

        ByteBuffer myBuffer = ByteBuffer.allocate(4);

        myBuffer.putInt(15);
        byte[] bytes = myBuffer.array();

        Byte[] byteObjects = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            byteObjects[i] = new Byte(bytes[i]);
        }

        byteHolder.setBytes(byteObjects);
        return byteHolder;
    }
}
