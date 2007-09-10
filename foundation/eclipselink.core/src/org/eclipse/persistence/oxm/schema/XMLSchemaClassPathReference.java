/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.schema;

import java.net.URL;
import org.eclipse.persistence.internal.helper.ConversionManager;

/**
 * A schema reference for accessing an XML Schema from the class path.
 */
public class XMLSchemaClassPathReference extends XMLSchemaReference {
    public XMLSchemaClassPathReference() {
        super();
    }

    public XMLSchemaClassPathReference(String resource) {
        super(resource);
    }

    public URL getURL() {
        // The URL must be passed to the resource, not just the input stream as it is require to
        // resolve relative URL for imports and includes.
        return ConversionManager.getDefaultManager().getLoader().getResource(this.getResource());
    }
}