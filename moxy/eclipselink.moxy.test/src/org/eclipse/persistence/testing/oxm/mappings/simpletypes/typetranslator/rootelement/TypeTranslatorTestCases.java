/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.typetranslator.rootelement;

// JDK imports
import java.nio.ByteBuffer;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
// TopLink imports

public class TypeTranslatorTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/simpletypes/typetranslator/RootElementTypeTranslatorTest.xml";

    private XMLMarshaller xmlMarshaller;

    public TypeTranslatorTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new ByteHolderProject());
    }

    protected Object getControlObject() {
        ByteHolder byteHolder = new ByteHolder();

        ByteBuffer myBuffer = ByteBuffer.allocate(4);

        myBuffer.putInt(15);
        byte bytes[] = myBuffer.array();

        Byte[] byteObjects = new Byte[bytes.length];
        for(int i=0; i<bytes.length; i++){
            byteObjects[i] =  new Byte(bytes[i]);
        }

        byteHolder.setBytes(byteObjects);
    return byteHolder;
    }

}
