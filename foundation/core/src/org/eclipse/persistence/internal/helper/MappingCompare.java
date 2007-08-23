/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * INTERNAL:
 * Use to Sort The mappings in ClassDescriptor, Mappings are either DirectToField, which must be at the top
 * or other
 * Avoid using this class as sun.misc is not part of many VM's like Netscapes.
 *
 */
public class MappingCompare implements TOPComparison {
    public int compare(Object arg1, Object arg2) {
        int arg1Value = ((DatabaseMapping)arg1).getWeight().intValue();
        int arg2Value = ((DatabaseMapping)arg2).getWeight().intValue();
        return (arg1Value - arg2Value);
    }
}