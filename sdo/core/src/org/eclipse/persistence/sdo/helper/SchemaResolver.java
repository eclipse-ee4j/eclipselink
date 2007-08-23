/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo.helper;

import javax.xml.transform.Source;

/**
 * <p><b>Purpose</b>: Interface that can be optionally implemented to resolve imported and included schemas.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Given the source schema and namespace and schemaLocation values from an import or include return the referenced Schema
 * <li> Used by XSDHelper define methods and ClassGenerator generate methods
 * </ul>
 *
 * @see org.eclipse.persistence.sdo.helper.DefaultSchemaResolver
 */
public interface SchemaResolver {

    /**
     * Given the source schema and namespace and schemaLocation values from an import or include return the referenced Schema
     * @param sourceXSD The Source object of the source schema
     * @param namespace The namespace portion of the import/include
     * @param schemaLocation The schemaLocation portion of the import/include
     * @return Source for the referenced Schema or null if processing the referenced schema should be skipped
     */
    public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation);
}