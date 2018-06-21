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

/**
 * Represents a mapping that can employ basic value holder indirection
 */
public interface MWIndirectableMapping extends MWNode
{
    /** No indirection is employed for the mapping */
    boolean usesNoIndirection();
    void setUseNoIndirection();

        /** May be used in implementation of the above for this interface */
        public final static String NO_INDIRECTION = "no-indirection";


    /** Value Holder indirection is employed for the mapping */
    boolean usesValueHolderIndirection();
    void setUseValueHolderIndirection();

        /** May be used in implementation of the above for this interface */
        public final static String VALUE_HOLDER_INDIRECTION = "value-holder-indirection";


    /** Property change notification for indirection */
    public final static String INDIRECTION_PROPERTY = "indirection";
}
