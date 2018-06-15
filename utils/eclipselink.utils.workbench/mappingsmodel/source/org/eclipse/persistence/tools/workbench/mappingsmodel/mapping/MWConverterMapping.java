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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.utility.Model;



public interface MWConverterMapping
    extends MWNode, Model
{
    MWConverter getConverter();

    MWNullConverter setNullConverter();

    MWObjectTypeConverter setObjectTypeConverter();

    MWSerializedObjectConverter setSerializedObjectConverter();

    MWTypeConversionConverter setTypeConversionConverter();

        static final String CONVERTER_PROPERTY = "converter";
}
