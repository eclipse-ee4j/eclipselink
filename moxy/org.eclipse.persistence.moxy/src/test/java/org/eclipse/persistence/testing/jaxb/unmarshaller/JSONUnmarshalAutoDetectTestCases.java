/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.unmarshaller;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;

public class JSONUnmarshalAutoDetectTestCases extends JSONUnmarshalTestCases{
    private static final String XML_RESOURCE_VALID = "org/eclipse/persistence/testing/jaxb/unmarshaller/validMarshal.xml";
    private static final String JSON_RESOURCE_VALID = "org/eclipse/persistence/testing/jaxb/unmarshaller/validMarshal.json";


    public JSONUnmarshalAutoDetectTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE_VALID);
        setControlDocument(XML_RESOURCE_VALID);
        setClasses(new Class<?>[]{TestObject.class});
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE, true);
    }

}
