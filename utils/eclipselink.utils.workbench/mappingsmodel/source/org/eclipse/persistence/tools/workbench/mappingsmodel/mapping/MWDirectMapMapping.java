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


public interface MWDirectMapMapping extends MWConverterMapping {

    MWConverter getDirectKeyConverter();

    MWNullConverter setNullDirectKeyConverter();
    MWObjectTypeConverter setObjectTypeDirectKeyConverter();
    MWSerializedObjectConverter setSerializedObjectDirectKeyConverter();
    MWTypeConversionConverter setTypeConversionDirectKeyConverter();

    static final String DIRECT_KEY_CONVERTER_PROPERTY = "directKeyConverter";

}
