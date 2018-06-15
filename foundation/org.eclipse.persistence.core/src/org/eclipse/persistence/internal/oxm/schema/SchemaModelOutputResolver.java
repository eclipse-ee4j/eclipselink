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
// dmccann - May 25/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.internal.oxm.schema;

import javax.xml.transform.Result;

/**
 * Interface for controlling where schema files are generated.
 *
 */
public interface SchemaModelOutputResolver {
    /**
     * Determines the location where a given schema file (of the given namespace URI)
     * will be generated, and return it as a Result object.
     *
     * @param namespaceURI
     * @param suggestedFileName
     * @return schema file as a Result object
     * @throws java.io.IOException
     */
    Result createOutput(String namespaceURI, String suggestedFileName) throws java.io.IOException;
}
