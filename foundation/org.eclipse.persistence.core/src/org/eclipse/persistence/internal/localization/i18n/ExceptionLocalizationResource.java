/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     07/13/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     08/24/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     11/05/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     01/11/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     01/24/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     09 Jan 2013-2.5 Gordon Yorke
 *       - 397772: JPA 2.1 Entity Graph Support
 *     02/19/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 ******************************************************************************/  
package org.eclipse.persistence.internal.localization.i18n;

import java.util.ListResourceBundle;

import org.eclipse.persistence.config.PersistenceUnitProperties;

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
                                           { "jmx_enabled_platform_mbean_runtime_exception", "An exception occurred while calling a JMX MBean runtime service function on {0} that exposes EclipseLink session information, exception is: {1}" },                                           
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
                                           { "null_value_for_constructor_result", "Target Class name must be provided for Constructor Result"},
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
                                           { "incorrect_spq_query_for_execute", "You cannot call execute() on this stored procedure query.  It is the incorrect query type." },
                                           { "incorrect_spq_query_for_execute_update", "You cannot call executeUpdate() on this stored procedure query since it returns a result set and not only an update count." },
                                           { "incorrect_spq_query_for_get_result_list", "You cannot call getResultList() on this stored procedure query since it does not return a result set." },
                                           { "incorrect_spq_query_for_get_single_result", "You cannot call getSingleResult() on this stored procedure query since it does not return a result set." },
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
                                           { "jpa_helper_invalid_entity_manager_factory_for_refresh", "The JPA entity manager factory {0} is not an EclipseLink entity manager factory and therefore, could not have its metadata refreshed."},
                                           { "null_not_supported_identityweakhashmap", "The IdentityWeakHashMap does not support 'null' as a key or value."},
                                           { "entity_manager_properties_conflict_default_connector_vs_jndi_connector", "EntityManager properties' conflict: javax.persistence.driver and/or javax.persistence.url require DefaultConnector, but javax.persistence.jtaDataSource and/or javax.persistence.nonjtaDataSource require JNDIConnector."},
                                           { "entity_manager_properties_conflict_default_connector_vs_external_transaction_controller", "EntityManager properties' conflict: javax.persistence.driver and/or javax.persistence.url require DefaultConnector, but persistence unit uses external transaction controller, therefore JNDIConnector is required."},
                                           { "invalid_lock_query", "A lock type can only be used with a select query (which allows the database to be locked where necessary)."},
                                           { "cant_lock_not_managed_object", "Entity must be managed to call lock: {0}, try merging the detached and try the lock again."},
                                           { "metamodel_managed_type_attribute_not_present", "The attribute [{0}] is not present in the managed type [{1}]." },
                                           { "metamodel_managed_type_attribute_type_incorrect", "Expected attribute type [{2}] on the existing attribute [{0}] on the managed type [{1}] but found attribute type [{3}]." },
                                           { "metamodel_identifiable_version_attribute_type_incorrect", "Expected version attribute type [{2}] on the existing version attribute [{0}] on the identifiable type [{1}] but found attribute type [{3}]." },
                                           { "metamodel_identifiable_id_attribute_type_incorrect", "Expected id attribute type [{2}] on the existing id attribute [{0}] on the identifiable type [{1}] but found attribute type [{3}]." },
                                           { "metamodel_managed_type_declared_attribute_not_present_but_is_on_superclass", "The declared attribute [{0}] from the managed type [{1}] is not present - however, it is declared on a superclass." },
                                           { "metamodel_managed_type_attribute_return_type_incorrect", "Expected attribute return type [{2}] on the existing attribute [{0}] on the managed type [{1}] but found attribute return type [{3}]." },
                                           { "metamodel_incompatible_persistence_config_for_getIdType", "Incompatible persistence configuration getting Metamodel Id Type for the ManagedType [{0}]." },
                                           // 338837:
                                           { "metamodel_class_incorrect_type_instance", "The type [{2}] is not the expected [{1}] for the key class [{0}].  Please verify that the [{2}] class was referenced in persistence.xml using a specific <class/> property or a global <exclude-unlisted-classes>false</exclude-unlisted-classes> element." },
                                           { "metamodel_class_null_type_instance", "No [{1}] was found for the key class [{0}] in the Metamodel - please verify that the [{2}] class was referenced in persistence.xml using a specific <class>{0}</class> property or a global <exclude-unlisted-classes>false</exclude-unlisted-classes> element." },
                                           { "metamodel_class_null_type_instance_for_null_key", "No [{0}] was found for the null key class parameter in the Metamodel - please specify the correct key class for the metamodel [{1}] class and verify that the key class was referenced in persistence.xml using a specific <class/> property or a global <exclude-unlisted-classes>false</exclude-unlisted-classes> element." },                                           
                                           { "metamodel_interface_inheritance_not_supported", "The descriptor [{0}] using ({1} inheritance) is not currently supported during metamodel generation, try using Entity or MappedSuperclass (Abstract class) inheritance." },
                                           { "sdo_helper_invalid_type", "The provided Type [{0}] is not an EclipseLink SDOType, and therefore could not be converted." },
                                           { "sdo_helper_invalid_property", "The provided Property [{0}] is not an EclipseLink SDOProperty, and therefore could not be converted." },
                                           { "sdo_helper_invalid_dataobject", "The provided DataObject [{0}] is not an EclipseLink SDODataObject, and therefore could not be converted." },
                                           { "sdo_helper_invalid_changesummary", "The provided ChangeSummary [{0}] is not an EclipseLink SDOChangeSummary, and therefore could not be converted." },
                                           { "sdo_helper_invalid_sequence", "The provided Sequence [{0}] is not an EclipseLink SDOSequence, and therefore could not be converted." },
                                           { "sdo_helper_invalid_helpercontext", "The provided HelperContext [{0}] is not an EclipseLink SDOHelperContext, and therefore could not be converted." },
                                           { "sdo_helper_invalid_copyhelper", "The provided CopyHelper [{0}] is not an EclipseLink SDOCopyHelper, and therefore could not be converted." },
                                           { "sdo_helper_invalid_datafactory", "The provided DataFactory [{0}] is not an EclipseLink SDODataFactory, and therefore could not be converted." },
                                           { "sdo_helper_invalid_datahelper", "The provided DataHelper [{0}] is not an EclipseLink SDODataHelper, and therefore could not be converted." },
                                           { "sdo_helper_invalid_equalityhelper", "The provided EqualityHelper [{0}] is not an EclipseLink SDOEqualityHelper, and therefore could not be converted." },
                                           { "sdo_helper_invalid_typehelper", "The provided TypeHelper [{0}] is not an EclipseLink SDOTypeHelper, and therefore could not be converted." },
                                           { "sdo_helper_invalid_xmlhelper", "The provided XMLHelper [{0}] is not an EclipseLink SDOXMLHelper, and therefore could not be converted." },
                                           { "sdo_helper_invalid_xsdhelper", "The provided XSDHelper [{0}] is not an EclipseLink SDOXSDHelper, and therefore could not be converted." },
                                           { "sdo_helper_invalid_target_for_type", "The provided target Class [{0}] must be one of EclipseLink SDOType, SDOTypeType, SDOPropertyType, SDOChangeSummaryType, SDODataObjectType, SDODataType, SDOOpenSequencedType, SDOObjectType, SDOWrapperType, or SDOXMLHelperLoadOptionsType." },
                                           { "sdo_helper_invalid_target_for_property", "The provided target Class [{0}] must be EclipseLink SDOProperty class." },
                                           { "sdo_helper_invalid_target_for_dataobject", "The provided target Class [{0}] must be EclipseLink SDODataObject class." },
                                           { "sdo_helper_invalid_target_for_changesummary", "The provided target Class [{0}] must be EclipseLink SDOChangeSummary class." },
                                           { "sdo_helper_invalid_target_for_sequence", "The provided target Class [{0}] must be EclipseLink SDOSequence class." },
                                           { "sdo_helper_invalid_target_for_helpercontext", "The provided target Class [{0}] must be EclipseLink SDOHelperContext class." },
                                           { "sdo_helper_invalid_target_for_copyhelper", "The provided target Class [{0}] must be EclipseLink SDOCopyHelper class." },
                                           { "sdo_helper_invalid_target_for_datafactory", "The provided target Class [{0}] must be EclipseLink SDODataFactory class." },
                                           { "sdo_helper_invalid_target_for_datahelper", "The provided target Class [{0}] must be EclipseLink SDODataHelper class." },
                                           { "sdo_helper_invalid_target_for_equalityhelper", "The provided target Class [{0}] must be EclipseLink SDOEqualityHelper class." },
                                           { "sdo_helper_invalid_target_for_typehelper", "The provided target Class [{0}] must be EclipseLink SDOTypeHelper class." },
                                           { "sdo_helper_invalid_target_for_xmlhelper", "The provided target Class [{0}] must be one of EclipseLink SDOXMLHelper, EclipseLink XMLMarshaller or EclipseLink XMLUnmarshaller." },
                                           { "sdo_helper_invalid_target_for_xsdhelper", "The provided target Class [{0}] must be EclipseLink SDOXSDHelper class." },
                                           { "jaxb_helper_invalid_jaxbcontext", "The provided JAXBContext [{0}] is not an EclipseLink JAXBContext, and therefore could not be converted." },
                                           { "jaxb_helper_invalid_unmarshaller", "The provided Unmarshaller [{0}] is not an EclipseLink JAXBUnmarshaller, and therefore could not be converted." },
                                           { "jaxb_helper_invalid_marshaller", "The provided Marshaller [{0}] is not an EclipseLink JAXBMarshaller, and therefore could not be converted." },
                                           { "jaxb_helper_invalid_binder", "The provided Binder [{0}] is not an EclipseLink JAXBBinder, and therefore could not be converted." },
                                           { "jaxb_helper_invalid_target_for_jaxbcontext", "The provided target Class [{0}] must be one of EclipseLink JAXBContext or EclipseLink XMLContext." },
                                           { "jaxb_helper_invalid_target_for_unmarshaller", "The provided target Class [{0}] must be one of EclipseLink JAXBUnmarshaller or EclipseLink XMLUnmarshaller." },
                                           { "jaxb_helper_invalid_target_for_marshaller", "The provided target Class [{0}] must be one of EclipseLink JAXBMarshaller or EclipseLink XMLMarshaller." },
                                           { "jaxb_helper_invalid_target_for_binder", "The provided target Class [{0}] must be one of EclipseLink JAXBBinder or EclipseLink XMLBinder." },
                                           { "jpa_persistence_util_non_persistent_class", "PersistenceUtil.getIdentifier(entity) was called with object [{0}] which is not a persistent object." },
                                           { "metamodel_identifiable_type_has_no_idclass_attribute", "No @IdClass attributes exist on the IdentifiableType [{0}].  There still may be one or more @Id or an @EmbeddedId on type." },
                                           { "metamodel_identifiable_no_version_attribute_present", "No @Version attribute exists on the identifiable type [{0}]." },
                                           { "metamodel_identifiable_no_id_attribute_present", "No @Id attribute exists on the identifiable type [{0}]." },
                                           { "metamodel_identifiable_id_attribute_is_incorrect_idclass", "The expected single @Id attribute for the identifiable type [{0}] is part of an unexpected @IdClass." },
                                           { "criteria_no_constructor_found", "An exception occured looking on class: {0} for constructor using selection criteria types as arguments.  If this CriteriaQuery was not intended to be a constructor query please verify that the selection matches the return type."},
                                           { "MULTIPLE_SELECTIONS_PASSED_TO_QUERY_WITH_PRIMITIVE_RESULT", "'multiSelect' was invoked on a CriteriaQuery with a primitive result type.  Either the return type is incorrect or 'select' should be used instead."},
                                           { "CRITERIA_NON_LITERAL_PASSED_TO_IN", "'in(Expression<?>... values)' was invoked with an expression type: {0} that was neither a literal nor a parameter.  This is not supported."},
                                           { "CRITERIA_NO_ROOT_FOR_COMPOUND_QUERY", "Attempting to create a Criteria query with no Root."},
                                           { "NO_PARAMETER_WITH_NAME", "No parameter with name : {0} was found within the query: {1}."},
                                           { "NO_PARAMETER_WITH_INDEX", "No parameter with index : {0} was found within the query: {1}."},
                                           { "INCORRECT_PARAMETER_TYPE", "The parameter at index : {0} was not of type: {1}."},
                                           { "PARAMETER_NILL_NOT_FOUND", "Null parameter passed to getParameterValue()"},
                                           { "NO_VALUE_BOUND", "No value was bound to parameter named: {0}"},
                                           { "NULL_PARAMETER_PASSED_TO_SET_PARAMETER", "Null parameter was passed to 'setParameter'.  Can not index parameters by 'Null'."},
                                           { "position_bound_param_not_found", "There was no bound parameter at position: {0}."},
                                           { "position_param_not_found", "There was no parameter at position: {0}."},
                                           { "pathnode_is_primitive_node", "Criteria expression is of primitive type and can not be further navigated."},
                                           { "pathnode_type_does_not_apply_to_primitive_node", "Criteria expression is of primitive type and can not be further navigated.  Primitive Expressions do not allow 'type'."},
                                           { "cache_impl_class_has_no_descriptor_is_not_a_persistent_type", "The class [{0}] is not a persistent type - it has no associated descriptor."},                                           
                                           { "cache_impl_object_has_no_descriptor_is_not_a_persistent_type", "The object [{0}] is not of a persistent type - it has no associated descriptor."}, //
                                           { "cache_impl_object_descriptor_has_no_cmppolicy_set", "The object [{0}] with descriptor [{1}] does not have a CMPPolicy set, we are unable to return an Id."}, //
                                           { "cache_descriptor_has_no_cmppolicy_set_cannot_create_primary_key", "The class [{0}] with descriptor [{1}] does not have a CMPPolicy set, we are unable create a primary key instance for the id type [{2}]."},                                           
                                           { "cannot_update_entity_fetch-group", "Attempt to add or remove attribute [{1}] to {0} - EntityFetchGroup object is immutable."},                                           
                                           { "cannot_get_unfetched_attribute", "Cannot get unfetched attribute [{1}] from detached object {0}."},
                                           { "jpa21_invalid_parameter_name", "Invalid output parameter name : {0}. {1}"},
                                           { "jpa21_invalid_parameter_position", "Invalid output parameter position : {0}. {1}."},
                                           { "jpa21_invalid_call_on_un_executed_query", "The query must be executed before calling this method."},
                                           { "jpa21_invalid_call_with_no_output_parameters", "Invalid call on a query that does not return OUT parameters."},
                                           { "jpa21_invalid_call_with_no_result_sets_returned", "Invalid call on a query that does not return result sets."},
                                           { "jpa21-ddl-source-script-not-found", "The source script: {0} for the generateSchema call was not found. Ensure you have specified a valid string URL that uses the 'file:' protocol or that the string file name represents a valid resource available from the classpath."},
                                           { "jpa21-ddl-source-script-sql-exception", "An error occured executing {0} from the source ddl generation script: {1}."},
                                           { "jpa21-ddl-source-script-io-exception", "An IO error occured with the source ddl generation script: {0}."},
                                           { "jpa21-ddl-invalid-source-script-type", "The source script provided {0} is of an invalid type {0}. Valid source script types are: java.io.Reader or a string designating a file URL."},
                                           { "jpa21-ddl-invalid-target-script-type", "The target script provided {0} is of an invalid type {0}. Valid target script types are: java.io.Writer or a string designating a file URL."},
                                           { "jpa21-ddl-drop-script-target-not-specified", "When generating DDL to scripts, a drop script target must be specified using the ["+ PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET+"] property."},
                                           { "jpa21-ddl-create-script-target-not-specified", "When generating DDL to scripts, a create script target must be specified using the ["+ PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET+"] property."},
                                           //criteria API IllegalArgumentExceptions
                                           { "jpa_criteriaapi_no_corresponding_element_in_result", "Element {0} does not correspond to an element in the query result."},
                                           { "jpa_criteriaapi_invalid_result_index", "index {0} invalid for result list of size {1}."},
                                           { "jpa_criteriaapi_invalid_result_type", "Element {0} type {1} is invalid for result {2}."},
                                           { "jpa_criteriaapi_null_literal_value", "Null value passed into CriteriaBuilder.literal().  Please use nullLiteral(Class<T> resultClass) instead."},
                                           { "jpa_criteriaapi_illegal_tuple_or_array_value", "Illegal tuple or array-valued selection item found. Argument found: {0}"},
                                           { "jpa_criteriaapi_alias_reused", "More than one selection item uses the same alias name.  Duplicate names used were: {0}"},
                                           { "cannot_read_through_txn_for_unsynced_pc", "The property was set to join this persistence context to the currently active transaction but this is not a SYNCHRONIZED persistence context."},
                                           { "cannot_use_transaction_on_unsynced_pc", "Cannot call methods requiring a transaction if the entity manager has not been joined to the current transaction."},
                                           { "unable_to_unwrap_jpa", "Provider does not support unwrapping {0} to {1}"},
                                           { "argument_keyed_named_query_with_JPA", "Multiple queries with name: {0} exist but names must be unique when using EntityManagerFactory.addNamedQuery()"},
                                           { "null_argument_get_attributegroup", "Search name for AttributeGroup must not be null."},
                                           { "add_attribute_key_was_null", "When specifying an AttributeGroup for a subclass of an attribute's type the type parameter must not be null"},
                                           { "managed_component_not_found", "An attribute: {1} listed in entity graph: {0} references a subgraph named: {2} which can not be found."},
                                           { "only_one_root_subgraph", "Only the root subgraph may be listed without a type.  Any subgraphs that represent subclasses must have the type set."},
                                           { "subclass_sought_not_a_managed_type", "subgraph type sought: {0} is not a managed type for this attribute: {1}."},
                                           { "attribute_is_not_map_with_managed_key", "Can not added key subgraph to entity graph as attribute : {0} in class : {1} is not a Map with a managed type key."},
                                           { "no_entity_graph_of_name", "No EntityGraph exists with name {0}"},
                                           { "not_usable_passed_to_entitygraph_hint", "value {1} passed to query hint {0} is not appropriate for this query hint"},
                                           { "operation_not_supported", "Calling {0} on a {1} is not supported by the specification."},
                                           { "pu_configured_for_resource_local", "Unable to create EntityManager with SynchronizationType because PersistenceUnit is configured with resource-local transactions."},
                                           { "getpersistenceunitutil_called_on_closed_emf", "getPersistenceUnitUtil() was called on a closed EntityManagerFactory."},
                                           { "named_entity_graph_exists", "NamedEntityGraph with name {0} found on {1} already exists in this persistence unit."},
                                           { "cannot_get_from_non_correlated_query", "getCorrelationParent() called on a from-clause that was not obtained through correlation." },
                                           { "wrap_convert_exception", "An exception occurred while calling {0} on converter class {1} with value {2}"}
                                           
										};
    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}

