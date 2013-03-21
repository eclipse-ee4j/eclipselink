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

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.persistence.exceptions.XMLMarshalException;

public class XMLSchemaURLReference extends XMLSchemaReference {
    public XMLSchemaURLReference() {
        super();
    }

    public XMLSchemaURLReference(URL url) {
        this(url.toString());
    }

    public XMLSchemaURLReference(String url) {
        super(url);
    }

    public URL getURL() {
        try {
            return new URL(this.getURLString());
        } catch (MalformedURLException e) {
            throw XMLMarshalException.errorResolvingXMLSchema(e);
        }
    }

    public void setURL(URL url) {
        this.setURLString(url.toString());
    }

    public String getURLString() {
        return this.getResource();
    }

    public void setURLString(String url) {
        this.setResource(url);
    }
}
