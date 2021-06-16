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
package org.eclipse.persistence.oxm.schema;

import java.io.File;
import java.net.URL;
import org.eclipse.persistence.exceptions.XMLMarshalException;

/**
 * A schema reference for accessing an XML Schema from a file.
 */
public class XMLSchemaFileReference extends XMLSchemaReference {
    public XMLSchemaFileReference() {
        super();
    }

    public XMLSchemaFileReference(File file) {
        this(file.getAbsolutePath());
    }

    public XMLSchemaFileReference(String fileName) {
        super(fileName);
    }

    public File getFile() {
        return new File(this.getFileName());
    }

    public void setFile(File file) {
        this.setFileName(file.getAbsolutePath());
    }

    public String getFileName() {
        return this.getResource();
    }

    public void setFileName(String filename) {
        this.setResource(filename);
    }

    @Override
    public URL getURL() {
        try {
            return this.getFile().toURI().toURL();
        } catch (Exception e) {
            throw XMLMarshalException.errorResolvingXMLSchema(e);
        }
    }
}
