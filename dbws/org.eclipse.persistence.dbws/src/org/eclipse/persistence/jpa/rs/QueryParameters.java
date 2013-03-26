/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs;

import org.eclipse.persistence.config.QueryHints;

public class QueryParameters extends QueryHints {
    private QueryParameters() {
    }

    // Currently, in JPA-RS, the separation between query parameters and matrix parameters
    // is done such a way that: 
    // - the predefined attributes (i.e. eclipselink query hints) are treated as query parameters
    // - anything that user sets (such as parameters of named queries, etc.) are treated as matrix parameters. 

    public static final String JPARS_LIST_ITEM_ID = "eclipselink.jpars.list.item-id";
    public static final String JPARS_RELATIONSHIP_PARTNER  = "eclipselink.jpars.partner";
}
