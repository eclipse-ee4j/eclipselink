/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.schema;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SchemaAPITests
    extends TestCase
{

    public static Test suite() {
        return new TestSuite(SchemaAPITests.class);
    }

    public SchemaAPITests(String name) {
        super(name);
    }


    /** Change this to change what schema you're test-loading */
    private final String schemaLocation = "file://C:/Paul/Documents/XMLSchema/TestJaxbGroup.xsd";
        /**
         choices:
             http://www.w3.org/2001/XMLSchema.xsd
             file://C:/Paul/Documents/XMLSchema/TestJaxbGroup.xsd
             file://C:/Paul/XMLSpy/Examples/ipo.xsd
             file://C:/Paul/XMLSpy/Examples/OrgChart.xsd
             file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/BasicAttributeGroup.xsd
             file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/BasicContentModels.xsd
             file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/BasicSubstitutionGroup.xsd
            file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/SchemaWithIdentityConstraints.xsd
            file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/TempForTesting.xsd
        */

    private URL schemaUrl;

    private URL schemaUrl()
        throws MalformedURLException
    {
        return new URL(this.schemaLocation);
    }

}
