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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Iterator;

public interface MWSchemaTypeDefinition
    extends MWSchemaContextComponent
{
    MWSchemaTypeDefinition getBaseType();

    boolean isComplex();

    /**
     * Return the type definitions of the built in type that this type is based on
     * (Used for runtime conversion)
     */
    Iterator baseBuiltInTypes();
}
