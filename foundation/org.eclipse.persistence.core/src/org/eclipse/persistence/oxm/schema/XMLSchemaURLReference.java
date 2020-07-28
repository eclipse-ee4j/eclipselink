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
