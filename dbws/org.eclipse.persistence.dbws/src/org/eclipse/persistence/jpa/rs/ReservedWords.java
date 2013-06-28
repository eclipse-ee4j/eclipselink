/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
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

public class ReservedWords {
    // Note: list grouping names need to be configurable (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=396430)
    // For backwards compatibility default is "List", but user will be able to change it once the bug mentioned above is fixed.
    @Deprecated
    public static final String JPARS_LIST_GROUPING_NAME = "List";

    public static final String JPARS_LIST_ITEM_NAME = "item";
    public static final String JPARS_LIST_ITEMS_NAME = "items";

    public static final String JPARS_LINK_NAME = "link";
    public static final String JPARS_LINKS_NAME = "links";

    // Link relationships 
    public static final String JPARS_REL_NEXT = "next";
    public static final String JPARS_REL_PREV = "prev";
    public static final String JPARS_REL_SELF = "self";

    public static final String NO_ROUTE_JAXB_ELEMENT_LABEL = "result";
    // 

    public static final String ERROR_RESPONSE_LABEL = "error";
}
