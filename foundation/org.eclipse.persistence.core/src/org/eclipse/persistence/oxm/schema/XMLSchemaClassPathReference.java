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

import java.net.URL;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * A schema reference for accessing an XML Schema from the class path.
 */
public class XMLSchemaClassPathReference extends XMLSchemaReference {
    ClassLoader loader;
    
    public XMLSchemaClassPathReference() {
        super();
    }

    public XMLSchemaClassPathReference(String resource) {
        super(resource);
    }

    public void initialize(AbstractSession session) {
        loader = session.getDatasourcePlatform().getConversionManager().getLoader();
    }
    
    public URL getURL() {
        try {
            // The URL must be passed to the resource, not just the input stream, as it is 
            // required to resolve the relative URL for imports and includes.
            if(null == loader) {
                return Thread.currentThread().getContextClassLoader().getResource(this.getResource());
            }
            return loader.getResource(this.getResource());
        } catch(Exception e) {
            throw XMLMarshalException.errorResolvingXMLSchema(e);
       }
    }
}
