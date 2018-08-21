/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     gonural - Initial implementation
package org.eclipse.persistence.exceptions;

public class JPARSErrorCodes {
    // Next range should start from LAST_ERROR_CODE (62000).
    // The JPA-RS uses error codes between 61000-61999 (both inclusive).
    public static final int ENTITY_NOT_FOUND = 61000;
    public static final int OBJECT_REFERRED_BY_LINK_DOES_NOT_EXIST = 61001;
    public static final int INVALID_CONFIGURATION = 61002;
    public static final int ENTITY_NOT_IDEMPOTENT = 61003;
    public static final int PERSISTENCE_CONTEXT_COULD_NOT_BE_BOOTSTRAPPED = 61004;
    public static final int CLASS_OR_CLASS_DESCRIPTOR_COULD_NOT_BE_FOUND = 61005;
    public static final int DATABASE_MAPPING_COULD_NOT_BE_FOUND_FOR_ENTITY_ATTRIBUTE = 61006;
    public static final int ATTRIBUTE_COULD_NOT_BE_FOUND_FOR_ENTITY = 61007;
    public static final int SELECTION_QUERY_FOR_ATTRIBUTE_COULD_NOT_BE_FOUND_FOR_ENTITY = 61008;
    public static final int INVALID_PAGING_REQUEST = 61009;
    public static final int ATTRIBUTE_COULD_NOT_BE_UPDATED = 61010;
    public static final int INVALID_ATTRIBUTE_REMOVAL_REQUEST = 61011;
    public static final int RESPONSE_COULD_NOT_BE_BUILT_FOR_FIND_ATTRIBUTE_REQUEST = 61012;
    public static final int SESSION_BEAN_COULD_NOT_BE_FOUND = 61013;
    public static final int RESPONSE_COULD_NOT_BE_BUILT_FOR_NAMED_QUERY_REQUEST = 61014;
    public static final int INVALID_SERVICE_VERSION = 61015;
    public static final int PAGINATION_PARAMETER_USED_FOR_NOT_PAGEABLE_RESOURCE = 61016;
    public static final int FIELDS_FILTERING_BOTH_PARAMETERS_PRESENT = 61017;
    public static final int INVALID_PARAMETER = 61018;
    public static final int JNDI_NAME_IS_INVALID = 61019;

    //
    public static final int AN_EXCEPTION_OCCURRED = 61999;
    // end marker for JPA-RS error codes
    public static final int LAST_ERROR_CODE = 62000;
}
