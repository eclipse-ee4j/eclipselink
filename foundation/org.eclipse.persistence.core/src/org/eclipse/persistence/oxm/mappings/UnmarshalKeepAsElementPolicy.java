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
package org.eclipse.persistence.oxm.mappings;
/**
 * <p><b>Purpose:</b> Used in conjunction with XMLAnyObject/CollectionMapping and XMLCompositeObject/CollectionMapping
 * to specify when (if at all) to keep parts of the unmarshalled document as nodes.
 *
 * <table summary=""><tr><td>KEEP_ALL_AS_ELEMENT</td><td>Any xml matching this mapping will be brough into the object
 * model as a node.</td></tr><tr><td>KEEP_UNKNOWN_AS_ELEMENT</td><td>Any XML with an unknown type that matches the
 * mapping in question will be a kept as an element. Those with a known type will be processed normally.</td>
 * <tr><td>KEEP_NONE_AS_ELEMENT</td><td>No xml shall be brought into the object as an element. Elements with unknown
 * types will be ignored</td></tr></table>
 *
 */

public enum UnmarshalKeepAsElementPolicy implements org.eclipse.persistence.internal.oxm.mappings.UnmarshalKeepAsElementPolicy {

    KEEP_ALL_AS_ELEMENT, KEEP_NONE_AS_ELEMENT, KEEP_UNKNOWN_AS_ELEMENT;

    @Override
    public boolean isKeepAllAsElement() {
        return this == KEEP_ALL_AS_ELEMENT;
    }

    @Override
    public boolean isKeepNoneAsElement() {
        return this == KEEP_NONE_AS_ELEMENT;
    }

    @Override
    public boolean isKeepUnknownAsElement() {
        return this == KEEP_UNKNOWN_AS_ELEMENT;
    }

}
