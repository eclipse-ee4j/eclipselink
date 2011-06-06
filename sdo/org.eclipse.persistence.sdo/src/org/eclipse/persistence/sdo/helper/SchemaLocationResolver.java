/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.Type;

/**
 * <p><b>Purpose</b>: Interface that can be optionally implemented to resolve the
 * value for the schemaLocation attribute of generated imports and includes when generating schemas.
 * Provides a more flexible solution than the optional namespaceToSchemaLocation Map that
 * can be given to the generate method.
 *
 * @see org.eclipse.persistence.sdo.helper.DefaultSchemaLocationResolver
 */
public interface SchemaLocationResolver {

    /**
     * Return the value for the schemaLocation attribute of the generated Import
     * @param sourceType the source type
     * @param targetType the target type
     * @return the value for the schemaLocation attribute of the generated Import
     */
    public String resolveSchemaLocation(Type sourceType, Type targetType);
}
