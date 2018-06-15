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
// Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.unmarshaller.autodetect;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AutoDetectFailsTestCases extends AutoDetectMediaTypeTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmarshaller/autodetect/employee-collection-fails.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmarshaller/autodetect/employee-collection-fails.json";

    public AutoDetectFailsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
    }

    public MediaType getXMLUnmarshalMediaType(){
          return MediaType.APPLICATION_XML;
    }

    public MediaType getJSONUnmarshalMediaType(){
       return MediaType.APPLICATION_JSON;
    }


}
