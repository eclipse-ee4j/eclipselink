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
//     Oracle - initial API and implementation from Oracle TopLink
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
