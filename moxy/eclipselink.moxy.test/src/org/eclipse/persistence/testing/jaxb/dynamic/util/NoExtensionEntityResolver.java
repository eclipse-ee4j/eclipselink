/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic.util;

import java.io.File;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class NoExtensionEntityResolver implements EntityResolver {

    private static final String RESOURCE_DIR = "org/eclipse/persistence/testing/jaxb/dynamic/";

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        // Grab only the filename part from the full path
        File f = new File(systemId);

        String correctedId = RESOURCE_DIR + f.getName() + ".xsd";

        InputSource is = new InputSource(ClassLoader.getSystemResourceAsStream(correctedId));
        is.setSystemId(correctedId);
        return is;
    }

}