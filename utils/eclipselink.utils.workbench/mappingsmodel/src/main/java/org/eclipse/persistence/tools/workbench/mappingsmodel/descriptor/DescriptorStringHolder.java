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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.ObjectStringHolder;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public class DescriptorStringHolder extends ObjectStringHolder {

    public DescriptorStringHolder(MWDescriptor descriptor, StringConverter stringConverter) {
        super(descriptor, stringConverter);
    }

    public DescriptorStringHolder(MWDescriptor descriptor) {
        this(descriptor, DEFAULT_STRING_CONVERTER);
    }

    public MWDescriptor getDescriptor() {
        return (MWDescriptor) this.object;
    }


    // ********** static methods **********

    public static DescriptorStringHolder[] buildHolders(Iterator descriptors) {
        return buildHolders(CollectionTools.list(descriptors));
    }

    public static DescriptorStringHolder[] buildHolders(Collection descriptors) {
        MWDescriptor[] descriptorArray = (MWDescriptor[]) descriptors.toArray(new MWDescriptor[descriptors.size()]);
        DescriptorStringHolder[] holders = new DescriptorStringHolder[descriptorArray.length];
        for (int i = descriptorArray.length; i-- > 0; ) {
            holders[i] = new DescriptorStringHolder(descriptorArray[i]);
        }
        return holders;
    }


    // ********** constants **********

    public static final StringConverter DEFAULT_STRING_CONVERTER = new StringConverter() {
        public String convertToString(Object o) {
            return ((MWDescriptor) o).shortName().toLowerCase();
        }
    };

}
