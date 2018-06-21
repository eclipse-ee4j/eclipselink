/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

public interface MWAttributeDeclaration
    extends MWXpathableSchemaComponent, MWNamedSchemaComponent
{
    /** Return the type of the attribute */
    MWSimpleTypeDefinition getType();

    /** Return the default value of the attribute */
    String getDefaultValue();

    /** Return the fixed value of the attribute */
    String getFixedValue();

    /** Return OPTIONAL, REQUIRED, or PROHIBITED */
    String getUse();
        final static String OPTIONAL     = "optional";
        final static String REQUIRED     = "required";
        final static String PROHIBITED     = "prohibited";
}
