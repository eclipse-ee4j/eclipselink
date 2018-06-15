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
//      tware -
package org.eclipse.persistence.jpa.rs.logging.i18n;

import java.util.ListResourceBundle;

public class LoggingLocalizationResource extends ListResourceBundle {

    static final Object[][] contents = {
            /*
             *
             * EACH RESOURCE STRING, MUST HAVE "requestId: {0}" AS FIRST PARAMETER in RESOURCE STRING.
             *
             */
            { "jpars_invalid_jndi_name", "requestId: {0}. A call is being made to a session bean with JNDI Name: [{1}].  That JNDI Name is invalid." },
            { "jpars_could_not_find_session_bean", "requestId: {0}. A call is being made to a session bean with JNDI Name: [{1}].  That bean can not be found." },
            { "jpars_could_not_find_persistence_context", "requestId: {0}. A JPA-RS call is requesting persistence context: [{1}].  That persistence context is not found." },
            { "jpars_could_not_find_class_in_persistence_unit", "requestId: {0}. Type: [{1}] cannot be found in persistence unit: [{2}]." },
            { "jpars_could_not_bootstrap_persistence_context", "requestId: {0}. Persistence Context: [{1}] could not be bootstrapped." },
            { "exception_marshalling_persitence_unit_metadata", "requestId: {0}. An exception was caught while marshalling the metadata for persistence unit [{1}]: [{2}]" },
            { "exception_marshalling_query_metadata", "requestId: {0}. An exception was caught while marshalling the metadata for the queries in persistence unit [{1}]: [{2}]" },
            { "exception_marshalling_individual_query_metadata", "requestId: {0}. An exception was caught while marshalling the metadata for query [{1}] in persistence unit [{2}]: [{3}]" },
            { "exception_marshalling_entity_metadata", "requestId: {0}. An exception was caught while marshalling the metadata for entity [{1}] in persistence unit [{2}]: [{3}]" },
            { "exception_creating_persistence_context", "requestId: {0}. An Exception was thrown while creating a JPA persistence context for persistence unit: [{1}]: [{2}]" },
            { "exception_while_updating_attribute", "requestId: {0}. An Exception was thrown while updating an entity of type [{1}] on persistence unit [{2}]: [{3}]" },
            { "exception_while_removing_attribute", "requestId: {0}. An Exception was thrown while removing attribute [{1}] on entity of type [{2}] on persistence unit [{3}]: [{4}]" },
            { "exception_while_unmarhalling_entity", "requestId: {0}. An Exception was thrown while unmarshalling an entity of type [{1}] in persitence unit [{2}]: [{3}]" },
            { "exception_creating_jaxb_context", "requestId: {0}. An Exception was thrown while creating a JAXBContext for persistence unit [{1}]: [{2}]" },
            { "exception_thrown_while_creating_dynamic_entity", "requestId: {0}. An Exception was thrown creating a dynamic entity of type [{1}]: [{2}]" },
            { "jpars_could_not_find_entity_type", "requestId: {0}. Entity type [{1}] could not be found in persistence unit: [{2}]." },
            { "jpars_could_not_find_entity_for_key", "requestId: {0}. Entity of type [{1}] and id [{2}] could not be found in persistence unit: [{3}]." },
            { "jpars_could_not_find_entity_for_attribute", "requestId: {0}. Attribute [{1}] for entity of type [{2}] and id [{3}] could not be found in persistence unit: [{4}]." },
            { "jpars_could_not_find_appropriate_mapping_for_update", "requestId: {0}. Attribute [{1}] for entity of type [{2}] in persistence unit: [{3}] is not the appropriate type for an update or delete." },
            { "jpars_could_not_update_attribute", "requestId: {0}. Attribute [{1}] for entity of type [{2}] with key [{3}] in persistence unit: [{4}] could not be updated or deleted." },
            { "jpars_put_not_idempotent", "requestId: {0}. An Entity of type [{1}] in persistence unit [{2}] is being created with a PUT, but the Entity is not idempotent due to either sequence generation or cascading." },
            { "jpars_could_not_marshal_serializing", "requestId: {0}. JPARS is serializing an object because it was unable to marshal it." },
            { "jaxb_exception_while_marshalling", "requestId: {0}. JPARS got a JAXBException while marshalling." },
            { "jpars_could_not_find_descriptor", "requestId: {0}. JPARS could not find the descriptor for [{1}] while subscribing for event notification." },
            { "jpars_caught_exception", "requestId: {0}. An exception was thrown in JPA RS." },
            { "jpars_could_not_add_listener", "requestId: {0}. JPA RS was unable to add a change listener" },
            { "weaving_required_for_relationships", "requestId: {0}. JPA RS can only handle relationships if your persistence unit is weaved.  Ensure weaving is not disabled and either deploy on a Java EE compliant server of statically weave your classes." },
            { "jpars_could_not_marshal_requested_result_to_requested_type", "requestId: {0}. JPA RS could not marshall object [{1}] to the requested type." },
            { "object_referred_by_link_does_not_exist", "requestId: {0}. Entity referred by link does not exist, entity type:[{1}], id:[{2}]." },
            { "unsupported_service_version_in_the_request", "requestId: {0}. JPARS version [{1}] is not supported." },
            { "no_orderby_clause_for_paging", "requestId: {0}. No OrderBy clause is defined for query:[{1}]. As a result, no guarantee exists that paging will work deterministically." }

            /*
             *
             * EACH RESOURCE STRING, MUST HAVE "requestId: {0}" AS FIRST PARAMETER in RESOURCE STRING.
             *
             */
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }

}
