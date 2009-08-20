/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.localization.i18n;

import java.util.ListResourceBundle;

/**
 * English ResourceBundle for ExceptionLocalization messages.
 *
 * @author Shannon Chen
 * @since TOPLink/Java 5.0
 */
public class ExceptionLocalizationResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "directory_not_exist", "Directory {0} does not exist." },
                                           { "jar_not_exist", "Jar file {0} does not exist." },
                                           { "may_not_contain_xml_entry", "{0} may not contain {1}." },
                                           { "not_jar_file", "{0} is not a jar file." },
                                           { "file_not_exist", "File {0} does not exist." },
                                           { "can_not_move_directory", "Can''t move directories." },
                                           { "can_not_create_file", "Could not create file {0}." },
                                           { "can_not_create_directory", "Could not create directory {0}." },
                                           { "file_exists", "The file {0} already exists." },
                                           { "create_insertion_failed", "Create insertion failed." },
                                           { "finder_query_failed", "Finder query failed:" },
                                           { "bean_not_found_on_database", "The bean ''{0}'' was not found on the database." },
                                           { "remove_deletion_failed", "Remove deletion failed:" },
                                           { "error_reading_jar_file", "Error reading jar file: {0} entry: {1}" },
                                           { "parsing_warning", "parsing warning" },
                                           { "parsing_error", "parsing error" },
                                           { "parsing_fatal_error", "parsing fatal error" },
                                           { "input_source_not_found", "Input Source not found, or null" },
                                           { "invalid_method_hash", "Invalid method hash" },
                                           { "interface_hash_mismatch", "Interface hash mismatch" },
                                           { "error_marshalling_return", "Error marshalling return" },
                                           { "error_unmarshalling_arguments", "Error unmarshalling arguments" },
                                           { "invalid_method_number", "Invalid method number" },
                                           { "undeclared_checked_exception", "Undeclared checked exception" },
                                           { "error_marshalling_arguments", "Error marshalling arguments" },
                                           { "error_unmarshalling_return", "error unmarshalling return" },
                                           { "null_jar_file_names", "Null jar file names" },
                                           { "weblogic_mbean_runtime_exception", "An exception occurred while creating a WebLogic runtime service for exposing EclipseLink session information, exception is: {0}" },                                           
                                           { "error_loading_resources", "Error loading resources {0} from the classpath" },
                                           { "error_parsing_resources", "Error parsing resources {0}" },
                                           { "unexpect_argument", "Unexpected input argument {0}" },                                           
                                           { "error_executing_jar_process", "Error executing jar process" },
                                           { "error_invoking_deploy", "Error invoking Deploy" },
                                           { "bean_definition_vector_arguments_are_of_different_sizes", "Bean definition vector arguments are of different sizes" },
                                           { "missing_toplink_bean_definition_for", "Missing TopLink bean definition for {0}" },
                                           { "argument_collection_was_null", "Argument collection was null" },
                                           { "no_entities_retrieved_for_get_single_result", "getSingleResult() did not retrieve any entities." },
                                           { "no_entities_retrieved_for_get_reference", "Could not find entity for id: {0}" },
                                           { "too_many_results_for_get_single_result", "More than one result was returned from Query.getSingleResult()" },
                                           { "negative_start_position", "Negative Start Position is not allowed" },
                                           { "incorrect_hint", "Incorrect object type specified for hint: {0}." },
                                           { "negative_max_result", "Negative MaxResult is not allowed." },
                                           { "cant_persist_detatched_object", "Cannot PERSIST detached object, possible duplicate primary key: {0}." },
                                           { "unknown_entitybean_name", "Unknown Entity Bean name: {0}" },
                                           { "unknown_bean_class", "Unknown entity bean class: {0}, please verify that this class has been marked with the @Entity annotation." },
                                           { "new_object_found_during_commit", "During synchronization a new object was found through a relationship that was not marked cascade PERSIST: {0}." },
                                           { "cannot_remove_removed_entity", "Entity is already removed: {0}"},
                                           { "cannot_remove_detatched_entity", "Entity must be managed to call remove: {0}, try merging the detached and try the remove again."},
                                           { "cannot_merge_removed_entity", "Cannot merge an entity that has been removed: {0}"},
                                           { "not_an_entity", "Object: {0} is not a known entity type."},
                                           { "unable_to_find_named_query", "NamedQuery of name: {0} not found."},
                                           { "null_values_for_field_result", "Both Attribute Name and Column Name must be provided for a FieldResult"},
                                           { "null_value_for_column_result", "Column Name must be provided for a ColumnResult"},
                                           { "null_value_for_entity_result", "Entity Class name must be provided for Entity Result"},
                                           { "null_value_in_sqlresultsetmapping", "A name must be provided for the SQLResultSetMapping.  This name is used to reference the SQLResultSetMapping from a query."},
                                           { "null_sqlresultsetmapping_in_query", "The ResultSetMappingQuery must have a SQLResultSetMapping set to be valid"},
                                           { "called_get_entity_manager_from_non_jta", "getEntityManager() is being called from a non-JTA enable EntityManagerFactory.  Please ensure JTA is properly set-up on your EntityManagerFactory."},
                                           { "illegal_state_while_closing", "Attempting to close an EntityManager with a transaction state other than NO_TRANSACTION, COMMITTED, or ROLLEDBACK."},
                                           { "operation_on_closed_entity_manager", "Attempting to execute an operation on a closed EntityManager."},
                                           { "wrap_ejbql_exception", "An exception occurred while creating a query in EntityManager"},
                                           { "cant_refresh_not_managed_object", "Can not refresh not managed object: {0}." },
                                           { "entity_no_longer_exists_in_db", "Entity no longer exists in the database: {0}." },
                                           { "incorrect_query_for_get_result_list", "You cannot call getResultList() on this query.  It is the incorrect query type." },
                                           { "incorrect_query_for_get_result_collection", "You cannot call getResultCollection() on this query.  It is the incorrect query type." },
                                           { "incorrect_query_for_get_single_result", "You cannot call getSingleResult() on this query.  It is the incorrect query type." },
                                           { "incorrect_query_for_execute_update", "You cannot call executeUpdate() on this query.  It is the incorrect query type." },
                                           { "pk_class_not_found", "Unable to load Primary Key Class {0}"},
                                           { "null_pk", "An instance of a null PK has been incorrectly provided for this find operation."},
                                           { "invalid_pk_class", "You have provided an instance of an incorrect PK class for this find operation.  Class expected : {0}, Class received : {1}." },
                                           { "ejb30-wrong-argument-name", "You have attempted to set a parameter value using a name of {0} that does not exist in the query string {1}."},
                                           { "ejb30-incorrect-parameter-type", "You have attempted to set a value of type {1} for parameter {0} with expected type of {2} from query string {3}."},
                                           { "ejb30-wrong-argument-index", "You have attempted to set a parameter at position {0} which does not exist in this query string {1}."},
                                           { "lock_called_without_version_locking", "Calls to entityManager.lock(Object entity, LockModeType lockMode) require that Version Locking be enabled."},
                                           { "missing_parameter_value", "Query argument {0} not found in the list of parameters provided during query execution."},
                                           { "operation_on_closed_entity_manager_factory", "Attempting to execute an operation on a closed EntityManagerFactory."},
                                           { "join_trans_called_on_entity_trans", "joinTransaction has been called on a resource-local EntityManager which is unable to register for a JTA transaction."},
                                           { "rollback_because_of_rollback_only", "Transaction 'rolled back' because transaction was set to RollbackOnly."},
                                           { "ejb30-wrong-query-hint-value", "Query {0}, query hint {1} has illegal value {2}."},
                                           { "ejb30-wrong-type-for-query-hint", "Query {0}, query hint {1} is not valid for this type of query."},
                                           { "ejb30-default-for-unknown-property", "Can't return default value for unknown property {0}."},
                                           { "ejb30-illegal-property-value", "Property {0} has an illegal value {1}."},
                                           { "ejb30-wrong-lock_called_without_version_locking-index", "Invalid lock mode type on for an entity that does not have a version locking index. Only a PESSIMISTIC lock mode type can be used when there is no version locking index."},
                                           { "jpa_helper_invalid_report_query", "The query of type {0} is not an EclipseLink report query and therefore, could not be converted."},
                                           { "jpa_helper_invalid_read_all_query", "The query of type {0} is not an EclipseLink read all query and therefore, could not be converted."},
                                           { "jpa_helper_invalid_query", "The query of type {0} is not an EclipseLink query and therefore, could not be converted."},
                                           { "jpa_helper_invalid_entity_manager_factory", "The JPA entity manager factory {0} is not an EclipseLink entity manager factory and therefore, could not be converted."},
                                           { "null_not_supported_identityweakhashmap", "The IdentityWeakHashMap does not support 'null' as a key or value."},
                                           { "entity_manager_properties_conflict_default_connector_vs_jndi_connector", "EntityManager properties' conflict: javax.persistence.driver and/or javax.persistence.url require DefaultConnector, but javax.persistence.jtaDataSource and/or javax.persistence.nonjtaDataSource require JNDIConnector."},
                                           { "entity_manager_properties_conflict_default_connector_vs_external_transaction_controller", "EntityManager properties' conflict: javax.persistence.driver and/or javax.persistence.url require DefaultConnector, but persistence unit uses external transaction controller, therefore JNDIConnector is required."},
                                           { "invalid_lock_query", "A lock type can only be used with a select query (which allows the database to be locked where necessary)."},
                                           { "cant_lock_not_managed_object", "Entity must be managed to call lock: {0}, try merging the detached and try the lock again."},
                                           { "metamodel_managed_type_attribute_not_present", "The attribute [{0}] from the managed type [{1}] is not present." },
                                           { "metamodel_managed_type_attribute_type_incorrect", "Expected attribute type [{2}] on the existing attribute [{0}] on the managed type [{1}] but found attribute type [{3}]." },
                                           { "metamodel_identifiable_version_attribute_type_incorrect", "Expected version attribute type [{2}] on the existing version attribute [{0}] on the identifiable type [{1}] but found attribute type [{3}]." },
                                           { "metamodel_identifiable_id_attribute_type_incorrect", "Expected id attribute type [{2}] on the existing id attribute [{0}] on the identifiable type [{1}] but found attribute type [{3}]." },
                                           { "metamodel_managed_type_declared_attribute_not_present_but_is_on_superclass", "The declared attribute [{0}] from the managed type [{1}] is not present - however, it is declared on a superclass." },
                                           { "metamodel_managed_type_attribute_return_type_incorrect", "Expected attribute return type [{2}] on the existing attribute [{0}] on the managed type [{1}] but found attribute return type [{3}]." },
                                           { "metamodel_incompatible_persistence_config_for_getIdType", "Incompatible persistence configuration getting Metamodel Id Type for the ManagedType [{0}]." },
                                           { "metamodel_class_incorrect_type_instance", "The type [{2}] is not the expected [{1}] for the key class [{0}]." },
                                           { "metamodel_interface_inheritance_not_supported", "The descriptor [{0}] using ({1} inheritance) is not currently supported during metamodel generation, try using Entity or MappedSuperclass (Abstract class) inheritance." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
