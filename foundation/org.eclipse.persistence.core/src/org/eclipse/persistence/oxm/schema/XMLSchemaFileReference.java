/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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

    public URL getURL() {
        try {
            return this.getFile().toURI().toURL();
        } catch (Exception e) {
            throw XMLMarshalException.errorResolvingXMLSchema(e);
        }
    }
}
