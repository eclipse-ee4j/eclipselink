/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - initial
package org.eclipse.persistence.jpa.rs;

import org.eclipse.persistence.config.QueryHints;

public class QueryParameters extends QueryHints {
    // Currently, in JPA-RS, the separation between query parameters and matrix parameters
    // is done such a way that:
    // - the predefined attributes (i.e. eclipselink query hints) are treated as query parameters
    // - anything that user sets (such as parameters of named queries, etc.) are treated as matrix parameters.
    public static final String JPARS_LIST_ITEM_ID = "eclipselink.jpars.list.item-id";
    public static final String JPARS_RELATIONSHIP_PARTNER  = "eclipselink.jpars.partner";

    // Paging related query parameters
    public static final String JPARS_PAGING_OFFSET = "offset";
    public static final String JPARS_PAGING_LIMIT = "limit";

}
