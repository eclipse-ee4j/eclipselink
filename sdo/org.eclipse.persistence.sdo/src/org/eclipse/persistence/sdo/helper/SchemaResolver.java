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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sdo.helper;

import javax.xml.transform.Source;

import org.xml.sax.EntityResolver;

/**
 * <p><b>Purpose</b>: Interface that can be optionally implemented to resolve imported and included schemas.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Given the source schema and namespace and schemaLocation values from an import or include return the referenced Schema
 * <li> Used by XSDHelper define methods and ClassGenerator generate methods
 * <li> Given the publicId and systemId of an external entity, return the file associated with that entity
 * </ul>
 *
 * @see org.eclipse.persistence.sdo.helper.DefaultSchemaResolver
 */
public interface SchemaResolver extends EntityResolver {

    /**
     * Given the source schema and namespace and schemaLocation values from an import or include return the referenced Schema
     * @param sourceXSD The Source object of the source schema
     * @param namespace The namespace portion of the import/include
     * @param schemaLocation The schemaLocation portion of the import/include
     * @return Source for the referenced Schema or null if processing the referenced schema should be skipped.  If this method
     * throws an exception, processing will stop.
     */
    public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation);
}
