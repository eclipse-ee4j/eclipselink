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
//     Matt MacIvor - 2.5.1 - Initial Implementation
package org.eclipse.persistence.jaxb.json;

import jakarta.xml.bind.SchemaOutputResolver;

/**
 * PUBLIC:
 * <p><b>Purpose:</b>Provides a schema output resolver specifically for Json Schemas. By
 * passing a subclass of JsonSchemaOutputResolver in to the JAXBContext generateSchema method,
 * will indicate that a JsonSchema should be generated instead of an Xml Schema.
 *
 * @author mmacivor
 *
 */
public abstract class JsonSchemaOutputResolver extends SchemaOutputResolver {

    /**
     * Returns the root class of the schema to be generated. Json Schemas only have 1 root
     * level structure, so the class returned from this method will represent the root of the
     * json schema.
     */
    public abstract Class getRootClass();

}
