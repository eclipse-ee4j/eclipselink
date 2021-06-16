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
// dmccann - May 25/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.internal.jaxb;

import java.io.IOException;

import jakarta.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;

import org.eclipse.persistence.internal.oxm.schema.SchemaModelOutputResolver;

/**
 * Implementation of a SchemaModelOutputResolver that wraps a
 * jakarta.xml.bind.SchemaOutputResolver instance.
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
    @Override
    public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
        return outputResolver.createOutput(namespaceURI, suggestedFileName);
    }
}
