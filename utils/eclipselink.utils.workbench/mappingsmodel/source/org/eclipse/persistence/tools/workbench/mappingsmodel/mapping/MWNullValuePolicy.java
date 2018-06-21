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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;

import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

public interface MWNullValuePolicy
    extends MWNode
{
    boolean usesNullValue();

    String getNullValue();
    void setNullValue(String newValue);
        public final static String NULL_VALUE_PROPERTY = "nullValue";

    MWTypeDeclaration getNullValueType();
    void setNullValueType(MWTypeDeclaration newNullType);
        public final static String NULL_VALUE_TYPE_PROPERTY = "nullValueType";


    // ************* Runtime Conversion **************

    void adjustRuntimeMapping(AbstractDirectMapping mapping);


    // **************** TopLink Methods *******************

    MWNullValuePolicy getValueForTopLink();
}
