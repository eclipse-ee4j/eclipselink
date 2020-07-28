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
//      gonural - initial implementation
package org.eclipse.persistence.exceptions.i18n;

import org.eclipse.persistence.exceptions.JPARSErrorCodes;

import java.util.ListResourceBundle;

/*
 * English resource bundle for JPARSException
 *
 */
public class JPARSExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
            { String.valueOf(JPARSErrorCodes.ENTITY_NOT_FOUND), "An entity type {0} with id {1} could not be found in persistence unit {2}." },
            { String.valueOf(JPARSErrorCodes.OBJECT_REFERRED_BY_LINK_DOES_NOT_EXIST), "Entity referred by link does not exist, entity type {0}, id {1}." },
            { String.valueOf(JPARSErrorCodes.INVALID_CONFIGURATION), "Invalid configuration." },
            { String.valueOf(JPARSErrorCodes.ENTITY_NOT_IDEMPOTENT), "An entity type {0} in persistence unit {1} is being created with a PUT, but the entity is not idempotent due to either sequence generation or cascading." },
            { String.valueOf(JPARSErrorCodes.PERSISTENCE_CONTEXT_COULD_NOT_BE_BOOTSTRAPPED), "Persistence Context {0} could not be bootstrapped." },
            { String.valueOf(JPARSErrorCodes.CLASS_OR_CLASS_DESCRIPTOR_COULD_NOT_BE_FOUND), "Class or class descriptor could not be found for entity type {0} in persistence unit {1}." },
            { String.valueOf(JPARSErrorCodes.DATABASE_MAPPING_COULD_NOT_BE_FOUND_FOR_ENTITY_ATTRIBUTE), "Appropriate database mapping could not be found for attribute {0} for entity type {1} and id {2} in persistence unit {3}." },
            { String.valueOf(JPARSErrorCodes.ATTRIBUTE_COULD_NOT_BE_FOUND_FOR_ENTITY), "Attribute {0} for entity type {1} and id {2} could not be found in persistence unit {3}." },
            { String.valueOf(JPARSErrorCodes.SELECTION_QUERY_FOR_ATTRIBUTE_COULD_NOT_BE_FOUND_FOR_ENTITY), "Selection query for attribute {0}, for entity type {1} and id {2} could not be found in persistence unit {3}." },
            { String.valueOf(JPARSErrorCodes.INVALID_PAGING_REQUEST), "Invalid parameter(s) in paging request url." },
            { String.valueOf(JPARSErrorCodes.ATTRIBUTE_COULD_NOT_BE_UPDATED), "Attribute {0} for entity type {1} and id {2} in persistence unit {3} could not be updated." },
            { String.valueOf(JPARSErrorCodes.INVALID_ATTRIBUTE_REMOVAL_REQUEST), "Invalid parameter(s) in attribute removal request url for attribute {0} for entity type {1} and id {2} in persistence unit {3}." },
            { String.valueOf(JPARSErrorCodes.RESPONSE_COULD_NOT_BE_BUILT_FOR_FIND_ATTRIBUTE_REQUEST), "Response for find request for attribute {0} for entity type {1} and id {2} in persistence unit {3} could not be built successfully." },
            { String.valueOf(JPARSErrorCodes.INVALID_SERVICE_VERSION), "Invalid service version {0} in the request." },
            { String.valueOf(JPARSErrorCodes.SESSION_BEAN_COULD_NOT_BE_FOUND), "Session bean lookup with JNDI name {0} has failed." },
            { String.valueOf(JPARSErrorCodes.RESPONSE_COULD_NOT_BE_BUILT_FOR_NAMED_QUERY_REQUEST), "Response for find named query request for query {0} in persistence unit {1} could not be built successfully." },
            { String.valueOf(JPARSErrorCodes.AN_EXCEPTION_OCCURRED), "{0} occurred." },
            { String.valueOf(JPARSErrorCodes.PAGINATION_PARAMETER_USED_FOR_NOT_PAGEABLE_RESOURCE), "Pagination query parameter (limit or offset) used for non-pageable resource." },
            { String.valueOf(JPARSErrorCodes.FIELDS_FILTERING_BOTH_PARAMETERS_PRESENT), "Both \"fields\" and \"excludeFields\" cannot be used in the same request." },
            { String.valueOf(JPARSErrorCodes.INVALID_PARAMETER), "Invalid parameter value (\"{0}\" = \"{1}\")." },
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
