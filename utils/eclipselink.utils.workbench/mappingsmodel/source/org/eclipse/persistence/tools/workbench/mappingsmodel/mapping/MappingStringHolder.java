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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.ObjectStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public class MappingStringHolder extends ObjectStringHolder {

    public MappingStringHolder(MWMapping mapping, StringConverter stringConverter) {
        super(mapping, stringConverter);
    }

    public MappingStringHolder(MWMapping mapping) {
        this(mapping, DEFAULT_STRING_CONVERTER);
    }

    public MWMapping getMapping() {
        return (MWMapping) this.object;
    }


    // ********** static methods **********

    public static MappingStringHolder[] buildHolders(Iterator mappings) {
        return buildHolders(CollectionTools.list(mappings));
    }

    public static MappingStringHolder[] buildHolders(Collection mappings) {
        MWMapping[] mappingArray = (MWMapping[]) mappings.toArray(new MWMapping[mappings.size()]);
        MappingStringHolder[] holders = new MappingStringHolder[mappingArray.length];
        for (int i = mappingArray.length; i-- > 0; ) {
            holders[i] = new MappingStringHolder(mappingArray[i]);
        }
        return holders;
    }


    // ********** constants **********

    public static final StringConverter DEFAULT_STRING_CONVERTER = new StringConverter() {
        public String convertToString(Object o) {
            return ((MWMapping) o).getName().toLowerCase();
        }
    };

}
