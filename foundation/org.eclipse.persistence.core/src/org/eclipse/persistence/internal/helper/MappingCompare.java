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
package org.eclipse.persistence.internal.helper;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * INTERNAL:
 * Use to Sort The mappings in ClassDescriptor, Mappings are either DirectToField, which must be at the top
 * or other
 * Avoid using this class as sun.misc is not part of many VM's like Netscapes.
 *
 */
public class MappingCompare implements Comparator, Serializable {

    private static final long serialVersionUID = -2749222441763925989L;

    @Override
    public int compare(Object arg1, Object arg2) {
        int arg1Value = ((DatabaseMapping)arg1).getWeight().intValue();
        int arg2Value = ((DatabaseMapping)arg2).getWeight().intValue();
        if (arg1Value == arg2Value) {
            int result = ((DatabaseMapping)arg1).getClass().getName().compareTo(((DatabaseMapping)arg2).getClass().getName());
            // For same classes, compare attribute names.
            if (result == 0) {
                // Can be null for TransformationMapping.
                if (((DatabaseMapping)arg1).getAttributeName() != null && ((DatabaseMapping)arg2).getAttributeName() != null) {
                    result = ((DatabaseMapping)arg1).getAttributeName().compareTo(((DatabaseMapping)arg2).getAttributeName());
                }
            }
            return result;
        }
        return (arg1Value - arg2Value);
    }
}
