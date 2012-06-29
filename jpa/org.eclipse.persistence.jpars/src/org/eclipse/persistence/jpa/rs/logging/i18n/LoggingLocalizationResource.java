/****************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.logging.i18n;

import java.util.ListResourceBundle;

public class LoggingLocalizationResource extends ListResourceBundle {

    static final Object[][] contents = {
        { "jpars_could_not_find_session_bean", "A call is being made to a session bean with JNDI Name: [{0}].  That bean can not be found."},
        { "jpars_could_not_find_persistence_context", "A JPA-RS call is requesting persistence context: [{0}].  That persistence context is not found."},
        { "jpars_could_not_find_class_in_persistence_unit", "Type: [{0}] cannot be found in persistence unit: [{1}]."},
        { "jpars_could_bootstrap_persistence_context", "Persistence Context: [{0}] could not be bootstrapped."},
        { "exception_marshalling_persitence_unit_metadata", "An exception was caught while marshalling the metadata for persistence unit [{0}]: [{1}]"},
        { "exception_marshalling_query_metadata", "An exception was caught while marshalling the metadata for the queries in persistence unit [{0}]: [{1}]"},
        { "exception_marshalling_individual_query_metadata", "An exception was caught while marshalling the metadata for query [{0}] in persistence unit [{1}]: [{2}]"},
        { "exception_marshalling_entity_metadata", "An exception was caught while marshalling the metadata for entity [{0}] in persistence unit [{1}]: [{2}]"},
        { "exception_creating_persistence_context", "An Exception was thrown while creating a JPA persistence context for persistence unit: [{0}]: [{1}]"},
        { "exception_while_updating_attribute", "An Exception was thrown while updating an entity of type [{0}] on persistence unit [{1}]: [{2}]"},
        { "exception_while_ummarhalling_entity", "An Exception was thrown while unmarshalling an entity of type [{0}] in persitence unit [{1}]: [{2}]"},
        { "exception_creating_jaxb_context", "An Exception was thrown while creating a JAXBContext for persistence unit [{0}]: [{1}]"},
        { "exception_thrown_while_creating_dynamic_entity", "An Exception was thrown creating a dyamic entity of type [{0}]: [{1}]"},
        { "jpars_could_not_find_entity_type", "Entity type [{0}] could not be found in persistence unit: [{1}]."},
        { "jpars_could_not_entity_for_key", "Entity of type [{0}] and id [{1}] could not be found in persistence unit: [{2}]."},
        { "jpars_could_not_entity_for_attribute", "Attribute [{0}] for entity of type [{1}] and id [{2}] could not be found in persistence unit: [{3}]."},
        { "jpars_could_find_appropriate_mapping_for_update", "Attribute [{0}] for entity of type [{1}] in persistence unit: [{2}] is not the appropriate type for an update or delete."},
        { "jpars_could_not_update_attribute", "Attribute [{0}] for entity of type [{1}] with key [{2}] in persistence unit: [{3}] could not be updated or deleted."},
        { "jpars_put_not_itempotent", "An Entity of type [{0}] in persistence unit [{1}] is being created with a PUT, but the Entity is not itempotent due to either sequence generation or cascading."},
        { "jpars_could_not_marshal_serializing", "JPARS is serializing an object because it was unable to marshal it."},
        { "jaxb_exception_while_marshalling", "JPARS got a JAXBException while marshalling."},
        { "jpars_could_not_find_descriptor", "JPARS could not find the descriptor for [{0}] while subscribing for event notification."},
        { "jpars_caught_exception", "An exception was thrown in JPA RS."},
        { "jpars_could_not_add_listener", "JPA RS was unable to add a change listener"},
        { "weaving_required_for_relationships", "JPA RS can only handle relationships if your persistence unit is weaved.  Ensure weaving is not disabled and either deploy on a Java EE compliant server of statically weave your classes."},
        { "jersey.container.cant.load", "Unable to instantiate Jersey ServletContainer."},
        { "jersey.not.available", "No Jersey runtime found on the application classpath"}
    };
    
    @Override
    protected Object[][] getContents() {
        // TODO Auto-generated method stub
        return contents;
    }

}
