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
package org.eclipse.persistence.tools.workbench.mappingsmodel;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;

/**
 * Used as a convenience for holding a Set of TopLinkOptions.
 * Use addConversionValuesForTopLink(ObjectTypeConveter) for persistence.
 * Use toplinkOptionForMWModelOption(String) as a convenience for
 * returning the TopLinkOption that has the given mwModelOption String
 */
public final class TopLinkOptionSet {

    private List topLinkOptions;

    public TopLinkOptionSet(List topLinkOptions) {
        this.topLinkOptions = topLinkOptions;
    }

    public ListIterator toplinkOptions() {
        return this.topLinkOptions.listIterator();
    }

    public TopLinkOption topLinkOptionForMWModelOption(String mwModelOption) {
         for (Iterator i = toplinkOptions(); i.hasNext(); ) {
            TopLinkOption option = (TopLinkOption) i.next();
            if (option.getMWModelOption() == mwModelOption) {
                return option;
            }
        }
        throw new IllegalArgumentException("mwModelOption is not valid");
    }

    /**
     * Call this for persistence using an ObjectTypeConverter
     */
    public void addConversionValuesForTopLinkTo(ObjectTypeConverter converter) {
        for (Iterator i = toplinkOptions(); i.hasNext();) {
            TopLinkOption model = (TopLinkOption) i.next();
            converter.addConversionValue(model.getMWModelOption(), model);
        }
    }
}
