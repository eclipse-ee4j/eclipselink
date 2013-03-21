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
* dmccann - May 25/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.jaxb;

import java.io.IOException;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;

import org.eclipse.persistence.internal.oxm.schema.SchemaModelOutputResolver;

/**
 * Implementation of a SchemaModelOutputResolver that wraps a 
 * javax.xml.bind.SchemaOutputResolver instance.
 *
 */
public class JAXBSchemaOutputResolver implements SchemaModelOutputResolver {
    SchemaOutputResolver outputResolver;

    /**
     * This constructor sets the underlying SchemaOutputResolver to be used 
     * during createOutput operation.
     * 
     * @param outputResolver
     */
    public JAXBSchemaOutputResolver(SchemaOutputResolver outputResolver) {
        this.outputResolver = outputResolver;
    }

    /**
     * Determines the location where a given schema file (of the given namespace URI)
     * will be generated, and return it as a Result object.
     *  
     * @param namespaceURI
     * @param suggestedFileName
     * @return schema file as a Result object
     * @throws java.io.IOException
     */
    public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
        return outputResolver.createOutput(namespaceURI, suggestedFileName);
    }
}
