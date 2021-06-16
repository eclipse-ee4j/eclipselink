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

import java.net.URL;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;

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

    @Override
    public void initialize(CoreAbstractSession session) {
        loader = session.getDatasourcePlatform().getConversionManager().getLoader();
    }

    @Override
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
