/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.5.1 - Initial Implementation
package org.eclipse.persistence.jaxb.json;

import javax.xml.bind.SchemaOutputResolver;

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
