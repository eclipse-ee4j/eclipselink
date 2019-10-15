/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2.2 - initial implementation
//     rfelcman - 2.7.2 - withExtension parameter
package org.eclipse.persistence.testing.jaxb.dynamic.util;

import java.io.File;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CustomEntityResolver implements EntityResolver {

    private static final String RESOURCE_DIR = "org/eclipse/persistence/testing/jaxb/dynamic/";

    private static boolean withExtension = false;

    public CustomEntityResolver(boolean withExtension) {
        this.withExtension = withExtension;
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        // Grab only the filename part from the full path
        File f = new File(systemId);

        String correctedId = RESOURCE_DIR + f.getName() + (this.withExtension ? "" : ".xsd");

        InputSource is = new InputSource(ClassLoader.getSystemResourceAsStream(correctedId));
        is.setSystemId(correctedId);
        return is;
    }

}
