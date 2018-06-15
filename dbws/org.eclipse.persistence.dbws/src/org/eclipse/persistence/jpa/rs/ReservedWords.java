/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - Initial implementation
package org.eclipse.persistence.jpa.rs;

/**
 * This class contains constants used in JPARS.
 *
 * @author gonural
 */
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
    public static final String JPARS_REL_CANONICAL = "canonical";
    public static final String JPARS_REL_ALTERNATE = "alternate";
    public static final String JPARS_REL_DESCRIBES = "describes";
    public static final String JPARS_REL_DESCRIBED_BY = "describedby";
    public static final String JPARS_REL_CREATE = "create";
    public static final String JPARS_REL_FIND = "find";
    public static final String JPARS_REL_UPDATE = "update";
    public static final String JPARS_REL_DELETE = "delete";
    public static final String JPARS_REL_EXECUTE = "execute";

    public static final String NO_ROUTE_JAXB_ELEMENT_LABEL = "result";

    public static final String ERROR_RESPONSE_LABEL = "error";
}
