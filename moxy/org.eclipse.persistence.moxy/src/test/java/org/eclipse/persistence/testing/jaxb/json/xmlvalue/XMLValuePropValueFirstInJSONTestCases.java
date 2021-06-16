/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - bug fix 501058 (version 2.7.5)
package org.eclipse.persistence.testing.jaxb.json.xmlvalue;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

public class XMLValuePropValueFirstInJSONTestCases extends XMLValuePropTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/xmlvalue/personUnmarshalValueFirst.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/xmlvalue/personMarshal.json";

    public XMLValuePropValueFirstInJSONTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Person.class});
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
    }

    public void setUp() throws Exception{
        super.setUp();
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, "marshalWrapper");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_VALUE_WRAPPER, "unmarshalWrapper");
    }
}
