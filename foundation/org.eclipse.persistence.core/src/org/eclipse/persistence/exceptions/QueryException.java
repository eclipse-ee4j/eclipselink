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
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 *     05/24/2011-2.3 Guy Pelletier 
 *       - 345962: Join fetch query when using tenant discriminator column fails.
 *     06/30/2011-2.3.1 Guy Pelletier 
 *       - 341940: Add disable/enable allowing native queries
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls 
 *     02/06/2013-2.5 Guy Pelletier 
 *       - 382503: Use of @ConstructorResult with createNativeQuery(sqlString, resultSetMapping) results in NullPointerException
 ******************************************************************************/  
package org.eclipse.persistence.exceptions;

import java.util.List;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.querykeys.ManyToManyQueryKey;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;
import org.eclipse.persistence.sessions.Record;

/**
 * <p><b>Purpose</b>: This exception is used for any problem that is detected with a query.
 */
public class QueryException extends ValidationException {
    protected transient DatabaseQuery query;
    protected transient AbstractRecord queryArguments;
    public final static int ADDITIONAL_SIZE_QUERY_NOT_SPECIFIED = 6001;
    public final static int AGGREGATE_OBJECT_CANNOT_BE_DELETED = 6002;
    public final static int ARGUMENT_SIZE_MISMATCH_IN_QUERY_AND_QUERY_DEFINITION = 6003;
    public final static int BACKUP_CLONE_IS_ORIGINAL_FROM_PARENT = 6004;
    public final static int BACKUP_CLONE_IS_ORIGINAL_FROM_SELF = 6005;
    public final static int BATCH_READING_NOT_SUPPORTED = 6006;
    public final static int DESCRIPTOR_IS_MISSING = 6007;
    public final static int DESCRIPTOR_IS_MISSING_FOR_NAMED_QUERY = 6008;
    public final static int INCORRECT_SIZE_QUERY_FOR_CURSOR_STREAM = 6013;
    public final static int INVALID_QUERY = 6014;
    public final static int INVALID_QUERY_KEY_IN_EXPRESSION = 6015;
    public final static int INVALID_QUERY_ON_SERVER_SESSION = 6016;
    public final static int NO_CONCRETE_CLASS_INDICATED = 6020;
    public final static int NO_CURSOR_SUPPORT = 6021;
    public final static int OBJECT_TO_INSERT_IS_EMPTY = 6023;
    public final static int OBJECT_TO_MODIFY_NOT_SPECIFIED = 6024;
    public final static int QUERY_NOT_DEFINED = 6026;
    public final static int QUERY_SENT_TO_INACTIVE_UNIT_OF_WORK = 6027;
    public final static int READ_BEYOND_QUERY = 6028;
    public final static int REFERENCE_CLASS_MISSING = 6029;
    public final static int REFRESH_NOT_POSSIBLE_WITHOUT_CACHE = 6030;
    public final static int SIZE_ONLY_SUPPORTED_ON_EXPRESSION_QUERIES = 6031;
    public final static int SQL_STATEMENT_NOT_SET_PROPERLY = 6032;
    public final static int INVALID_QUERY_ITEM = 6034;
    public final static int SELECTION_OBJECT_CANNOT_BE_NULL = 6041;
    public final static int UNNAMED_QUERY_ON_SESSION_BROKER = 6042;
    public final static int REPORT_RESULT_WITHOUT_PKS = 6043;
    public final static int NULL_PRIMARY_KEY_IN_BUILDING_OBJECT = 6044;
    public final static int NO_DESCRIPTOR_FOR_SUBCLASS = 6045;
    public final static int CANNOT_DELETE_READ_ONLY_OBJECT = 6046;
    public final static int INVALID_OPERATOR = 6047;
    public final static int ILLEGAL_USE_OF_GETFIELD = 6048;
    public final static int ILLEGAL_USE_OF_GETTABLE = 6049;
    public final static int REPORT_QUERY_RESULT_SIZE_MISMATCH = 6050;
    public final static int CANNOT_CACHE_PARTIAL_OBJECT = 6051;
    public final static int OUTER_JOIN_ONLY_VALID_FOR_ONE_TO_ONE = 6052;
    public final static int CANNOT_ADD_TO_CONTAINER = 6054;
    public static final int METHOD_INVOCATION_FAILED = 6055;
    public static final int CANNOT_CREATE_CLONE = 6056;
    public static final int METHOD_NOT_VALID = 6057;
    public static final int METHOD_DOES_NOT_EXIST_IN_CONTAINER_CLASS = 6058;
    public static final int COULD_NOT_INSTANTIATE_CONTAINER_CLASS = 6059;
    public static final int MAP_KEY_NOT_COMPARABLE = 6060;
    public static final int CANNOT_ACCESS_METHOD_ON_OBJECT = 6061;
    public static final int CALLED_METHOD_THREW_EXCEPTION = 6062;
    public final static int INVALID_OPERATION = 6063;
    public final static int CANNOT_REMOVE_FROM_CONTAINER = 6064;
    public final static int CANNOT_ADD_ELEMENT = 6065;
    public static final int BACKUP_CLONE_DELETED = 6066;
    public final static int CANNOT_ACCESS_FIELD_ON_OBJECT = 6067;
    public final static int CANNOT_COMPARE_TABLES_IN_EXPRESSION = 6068;
    public final static int INVALID_TABLE_FOR_FIELD_IN_EXPRESSION = 6069;
    public final static int INVALID_USE_OF_TO_MANY_QUERY_KEY_IN_EXPRESSION = 6070;
    public final static int INVALID_USE_OF_ANY_OF_IN_EXPRESSION = 6071;
    public final static int CANNOT_QUERY_ACROSS_VARIABLE_ONE_TO_ONE_MAPPING = 6072;
    public final static int ILL_FORMED_EXPRESSION = 6073;
    public final static int CANNOT_CONFORM_EXPRESSION = 6074;
    public final static int INVALID_OPERATOR_FOR_OBJECT_EXPRESSION = 6075;
    public final static int UNSUPPORTED_MAPPING_FOR_OBJECT_COMPARISON = 6076;
    public final static int OBJECT_COMPARISON_CANNOT_BE_PARAMETERIZED = 6077;
    public final static int INCORRECT_CLASS_FOR_OBJECT_COMPARISON = 6078;
    public final static int CANNOT_COMPARE_TARGET_FOREIGN_KEYS_TO_NULL = 6079;
    public final static int INVALID_DATABASE_CALL = 6080;
    public final static int INVALID_DATABASE_ACCESSOR = 6081;
    public final static int METHOD_DOES_NOT_EXIST_ON_EXPRESSION = 6082;
    public final static int IN_CANNOT_BE_PARAMETERIZED = 6083;
    public final static int REDIRECTION_CLASS_OR_METHOD_NOT_SET = 6084;
    public final static int REDIRECTION_METHOD_NOT_DEFINED_CORRECTLY = 6085;
    public final static int REDIRECTION_METHOD_ERROR = 6086;
    public final static int EXAMPLE_AND_REFERENCE_OBJECT_CLASS_MISMATCH = 6087;
    public final static int NO_ATTRIBUTES_FOR_REPORT_QUERY = 6088;
    public final static int NO_EXPRESSION_BUILDER_CLASS_FOUND = 6089;
    public final static int CANNOT_SET_REPORT_QUERY_TO_CHECK_CACHE_ONLY = 6090;
    public final static int TYPE_MISMATCH_BETWEEN_ATTRIBUTE_AND_CONSTANT_ON_EXPRESSION = 6091;
    public final static int MUST_INSTANTIATE_VALUEHOLDERS = 6092;
    public final static int INVALID_TYPE_EXPRESSION = 6093;
    public final static int PARAMETER_NAME_MISMATCH = 6094;
    public final static int CLONE_METHOD_REQUIRED = 6095;
    public final static int CLONE_METHOD_INACCESSIBLE = 6096;
    public final static int CLONE_METHOD_THORW_EXCEPTION = 6097;
    public final static int UNEXPECTED_INVOCATION = 6098;
    public final static int JOINING_ACROSS_INHERITANCE_WITH_MULTIPLE_TABLES = 6099;
    public final static int MULTIPLE_ROWS_DETECTED_FROM_SINGLE_OBJECT_READ = 6100;
    public final static int HISTORICAL_QUERIES_MUST_PRESERVE_GLOBAL_CACHE = 6101;
    public final static int HISTORICAL_QUERIES_ONLY_SUPPORTED_ON_ORACLE = 6102;
    public final static int INVALID_QUERY_ON_HISTORICAL_SESSION = 6103;
    public final static int OBJECT_DOES_NOT_EXIST_IN_CACHE = 6104;
    public final static int MUST_USE_CURSOR_STREAM_POLICY = 6105;
    public final static int CLASS_PK_DOES_NOT_EXIST_IN_CACHE = 6106;
    public final static int UPDATE_STATEMENTS_NOT_SPECIFIED = 6107;
    public final static int INHERITANCE_WITH_MULTIPLE_TABLES_NOT_SUPPORTED = 6108;
    public final static int QUERY_FETCHGROUP_NOT_DEFINED_IN_DESCRIPTOR = 6109;
    public final static int CANNOT_CONFORM_UNFETCHED_ATTRIBUTE = 6110;
    public final static int FETCH_GROUP_ATTRIBUTE_NOT_MAPPED = 6111;
    public final static int FETCH_GROUP_NOT_SUPPORT_ON_REPORT_QUERY = 6112;
    public final static int FETCH_GROUP_NOT_SUPPORT_ON_PARTIAL_ATTRIBUTE_READING = 6113;
    public final static int FETCHGROUP_VALID_ONLY_IF_FETCHGROUP_MANAGER_IN_DESCRIPTOR = 6114;
    public final static int ISOLATED_QUERY_EXECUTED_ON_SERVER_SESSION = 6115;
    public final static int NO_CALL_OR_INTERACTION_SPECIFIED = 6116;
    public final static int CANNOT_CACHE_CURSOR_RESULTS_ON_QUERY = 6117;
    public final static int CANNOT_CACHE_ISOLATED_DATA_ON_QUERY = 6118;
    public final static int MAPPING_FOR_EXPRESSION_DOES_NOT_SUPPORT_JOINING = 6119;
    public final static int SPECIFIED_PARTIAL_ATTRIBUTE_DOES_NOT_EXIST = 6120;
    public final static int INVALID_BUILDER_IN_QUERY = 6121;
    public final static int INVALID_EXPRESSION = 6122;
    public final static int INVALID_CONTAINER_CLASS = 6123;
    public final static int INCORRECT_QUERY_FOUND = 6124;
    public final static int CLEAR_QUERY_RESULTS_NOT_SUPPORTED = 6125;
    public final static int CANNOT_CONFORM_AND_CACHE_QUERY_RESULTS = 6126;
    public final static int REFLECTIVE_CALL_ON_TOPLINK_CLASS_FAILED = 6127;
    public final static int BATCH_READING_NOT_SUPPORTED_WITH_CALL = 6128;
    public final static int REFRESH_NOT_POSSIBLE_WITH_CHECK_CACHE_ONLY = 6129;
    public final static int DISCRIMINATOR_COLUMN_NOT_SELECTED = 6130;
    public final static int DELETE_ALL_QUERY_SPECIFIES_OBJECTS_BUT_NOT_SELECTION_CRITERIA = 6131;
    public final static int NAMED_ARGUMENT_NOT_FOUND_IN_QUERY_PARAMETERS = 6132;
    public final static int UPDATE_ALL_QUERY_ADD_UPDATE_FIELD_IS_NULL = 6133;
    public final static int UPDATE_ALL_QUERY_ADD_UPDATE_DOES_NOT_DEFINE_FIELD = 6134;
    public final static int UPDATE_ALL_QUERY_ADD_UPDATE_DEFINES_WRONG_FIELD = 6135;
    public final static int POLYMORPHIC_REPORT_ITEM_NOT_SUPPORTED = 6136;
    public final static int EXCEPTION_WHILE_USING_CONSTRUCTOR_EXPRESSION = 6137;
    public final static int TEMP_TABLES_NOT_SUPPORTED = 6138;
    public final static int MAPPING_FOR_FIELDRESULT_NOT_FOUND = 6139;
    public final static int JOIN_EXPRESSIONS_NOT_APPLICABLE_ON_NON_OBJECT_REPORT_ITEM = 6140;
    public final static int CLASS_NOT_FOUND_WHILE_USING_QUERY_HINT = 6141;
    public final static int QUERY_HINT_NAVIGATED_ILLEGAL_RELATIONSHIP = 6142;    
    public final static int QUERY_HINT_NAVIGATED_NON_EXISTANT_RELATIONSHIP = 6143;  
    public final static int QUERY_HINT_DID_NOT_CONTAIN_ENOUGH_TOKENS = 6144;  
    public final static int DISTINCT_COUNT_ON_OUTER_JOINED_COMPOSITE_PK = 6145;
    public final static int QUERY_HINT_CONTAINED_INVALID_INTEGER_VALUE = 6146;
    public final static int EXPRESSION_DOES_NOT_SUPPORT_PARTIAL_ATTRIBUTE_READING = 6147;    
    public final static int ADD_ARGS_NOT_SUPPORTED = 6148;
    public final static int UNNAMED_ARG_NOT_SUPPORTED = 6149;
    public final static int MAP_KEY_IS_NULL = 6150;
    public final static int UNABLE_TO_SET_REDIRECTOR_FROM_HINT = 6151;
    public final static int ERROR_INSTANTIATING_CLASS_FOR_QUERY_HINT = 6152;
    public final static int COMPATIBLE_TYPE_NOT_SET = 6153;
    public final static int TYPE_NAME_NOT_SET = 6154;
    public final static int NO_RELATION_TABLE_IN_MANY_TO_MANY_QUERY_KEY = 6155;
    public final static int EXCEPTION_WHILE_READING_MAP_KEY = 6156;
    public final static int CANNOT_ADD_ELEMENT_WITHOUT_KEY_TO_MAP = 6157;
    public final static int CANNOT_UNWRAP_NON_MAP_MEMBERS = 6158;
    public final static int NO_MAPPING_FOR_MAP_ENTRY_EXPRESSION = 6159;
    public final static int MAP_ENTRY_EXPRESSION_FOR_NON_COLLECTION = 6160;
    public final static int MAP_ENTRY_EXPRESSION_FOR_NON_MAP = 6161;
    public final static int LIST_ORDER_FIELD_WRONG_VALUE = 6162;
    public final static int INDEX_REQUIRES_QUERY_KEY_EXPRESSION = 6163;
    public final static int INDEX_REQUIRES_COLLECTION_MAPPING_WITH_LIST_ORDER_FIELD = 6164;
    public final static int BATCH_IN_REQUIRES_SINGLETON_PK = 6165;
    public final static int COULD_NOT_FIND_CAST_DESCRIPTOR = 6166;
    public final static int CAST_MUST_USE_INHERITANCE = 6167;
    public final static int PREPARE_FAILED = 6168;
    public final static int ORIGINAL_QUERY_MUST_USE_IN = 6169;
    public final static int PARTIONING_NOT_SUPPORTED = 6171;
    public final static int MISSING_CONNECTION_POOL = 6172;
    public final static int FAILOVER_FAILED = 6173;
    public final static int MISSING_CONTEXT_PROPERTY_FOR_PROPERTY_PARAMETER_EXPRESSION = 6174;
    public static final int NATIVE_SQL_QUERIES_ARE_DISABLED = 6175;
    public final static int EXCEPTION_WHILE_LOADING_CONSTRUCTOR = 6176;
    public final static int COLUMN_RESULT_NOT_FOUND = 6177;
    public final static int RESULT_SET_ACCESS_OPTIMIZATION_IS_NOT_POSSIBLE = 6178;
    
    /**
     * INTERNAL:
     * Constructor.
     */
    protected QueryException(String message) {
        super(message);
    }

    /**
     * INTERNAL:
     * Constructor.
     */
    protected QueryException(String message, DatabaseQuery query) {
        super(message);
        this.query = query;
    }

    /**
     * INTERNAL:
     * Constructor.
     */
    protected QueryException(String message, DatabaseQuery query, Exception internalException) {
        super(message,internalException);
        this.query = query;
    }

    
    public static QueryException additionalSizeQueryNotSpecified(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, ADDITIONAL_SIZE_QUERY_NOT_SPECIFIED, args), query);
        queryException.setErrorCode(ADDITIONAL_SIZE_QUERY_NOT_SPECIFIED);
        return queryException;
    }

    public static QueryException aggregateObjectCannotBeDeletedOrWritten(ClassDescriptor descriptor, DatabaseQuery query) {
        Object[] args = { descriptor.toString(), CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, AGGREGATE_OBJECT_CANNOT_BE_DELETED, args), query);
        queryException.setErrorCode(AGGREGATE_OBJECT_CANNOT_BE_DELETED);
        return queryException;
    }

    public static QueryException argumentSizeMismatchInQueryAndQueryDefinition(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, ARGUMENT_SIZE_MISMATCH_IN_QUERY_AND_QUERY_DEFINITION, args), query);
        queryException.setErrorCode(ARGUMENT_SIZE_MISMATCH_IN_QUERY_AND_QUERY_DEFINITION);
        return queryException;
    }
    
    public static QueryException missingContextPropertyForPropertyParameterExpression(DatabaseQuery query, String argumentName) {
        Object[] args = { argumentName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, MISSING_CONTEXT_PROPERTY_FOR_PROPERTY_PARAMETER_EXPRESSION, args), query);
        queryException.setErrorCode(MISSING_CONTEXT_PROPERTY_FOR_PROPERTY_PARAMETER_EXPRESSION);
        return queryException;
    }
    
    public static QueryException namedArgumentNotFoundInQueryParameters(String argumentName) {
        Object[] args = {argumentName};
        
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, NAMED_ARGUMENT_NOT_FOUND_IN_QUERY_PARAMETERS, args));
        queryException.setErrorCode(NAMED_ARGUMENT_NOT_FOUND_IN_QUERY_PARAMETERS);
        return queryException;
    }
    
    public static QueryException nativeSQLQueriesAreDisabled(DatabaseQuery query) {
        Object[] args = {};        
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, NATIVE_SQL_QUERIES_ARE_DISABLED, args), query);
        queryException.setErrorCode(NATIVE_SQL_QUERIES_ARE_DISABLED);
        return queryException;
    }
    
    public static QueryException backupCloneIsDeleted(Object clone) {
        Object[] args = { clone, clone.getClass(), Integer.valueOf(System.identityHashCode(clone)), CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, BACKUP_CLONE_DELETED, args));
        queryException.setErrorCode(BACKUP_CLONE_DELETED);
        return queryException;
    }

    public static QueryException backupCloneIsOriginalFromParent(Object clone) {
        // need to be verified
        Object[] args = { clone, clone.getClass(), Integer.valueOf(System.identityHashCode(clone)), CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, BACKUP_CLONE_IS_ORIGINAL_FROM_PARENT, args));
        queryException.setErrorCode(BACKUP_CLONE_IS_ORIGINAL_FROM_PARENT);
        return queryException;
    }

    public static QueryException backupCloneIsOriginalFromSelf(Object clone) {
        Object[] args = { clone, clone.getClass(), Integer.valueOf(System.identityHashCode(clone)), CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, BACKUP_CLONE_IS_ORIGINAL_FROM_SELF, args));
        queryException.setErrorCode(BACKUP_CLONE_IS_ORIGINAL_FROM_SELF);
        return queryException;
    }

    public static QueryException batchReadingNotSupported(DatabaseMapping mapping, DatabaseQuery query) {
        Object[] args = { mapping };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, BATCH_READING_NOT_SUPPORTED, args), query);
        queryException.setErrorCode(BATCH_READING_NOT_SUPPORTED);
        return queryException;
    }

    public static QueryException batchReadingNotSupported(DatabaseQuery query) {
        Object[] args = { };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, BATCH_READING_NOT_SUPPORTED_WITH_CALL, args), query);
        queryException.setErrorCode(BATCH_READING_NOT_SUPPORTED_WITH_CALL);
        return queryException;
    }
    
    public static QueryException calledMethodThrewException(java.lang.reflect.Method aMethod, Object object, Exception ex) {
        Object[] args = { aMethod, object, object.getClass() };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CALLED_METHOD_THREW_EXCEPTION, args));
        queryException.setErrorCode(CALLED_METHOD_THREW_EXCEPTION);
        queryException.setInternalException(ex);
        return queryException;
    }

    public static ValidationException cannotAccessFieldOnObject(java.lang.reflect.Field aField, Object anObject) {
        Object[] args = { aField, anObject, anObject.getClass() };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_ACCESS_FIELD_ON_OBJECT, args));
        validationException.setErrorCode(CANNOT_ACCESS_FIELD_ON_OBJECT);
        return validationException;
    }
    
    public static ValidationException cannotAccessMethodOnObject(java.lang.reflect.Method aMethod, Object anObject) {
        Object[] args = { aMethod, anObject, anObject.getClass() };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_ACCESS_METHOD_ON_OBJECT, args));
        validationException.setErrorCode(CANNOT_ACCESS_METHOD_ON_OBJECT);
        return validationException;
    }

    public static QueryException cannotAddElement(Object anObject, Object aContainer, Exception ex) {
        Object[] args = { anObject, anObject.getClass(), aContainer.getClass() };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_ADD_ELEMENT, args));
        queryException.setErrorCode(CANNOT_ADD_ELEMENT);
        queryException.setInternalException(ex);
        return queryException;
    }

    public static QueryException cannotAddToContainer(Object anObject, Object aContainer, ContainerPolicy policy) {
        Object[] args = { anObject, anObject.getClass(), aContainer.getClass(), policy };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_ADD_TO_CONTAINER, args));
        queryException.setErrorCode(CANNOT_ADD_TO_CONTAINER);
        return queryException;
    }

    public static QueryException cannotCacheCursorResultsOnQuery(DatabaseQuery query) {
        Object[] args = {  };
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_CACHE_CURSOR_RESULTS_ON_QUERY, args));
        queryException.setQuery(query);
        queryException.setErrorCode(CANNOT_CACHE_CURSOR_RESULTS_ON_QUERY);
        return queryException;
    }

    public static QueryException cannotCacheIsolatedDataOnQuery(DatabaseQuery query) {
        Object[] args = {  };
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_CACHE_ISOLATED_DATA_ON_QUERY, args));
        queryException.setQuery(query);
        queryException.setErrorCode(CANNOT_CACHE_ISOLATED_DATA_ON_QUERY);
        return queryException;
    }

    public static QueryException cannotCachePartialObjects(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_CACHE_PARTIAL_OBJECT, args));
        queryException.setQuery(query);
        queryException.setErrorCode(CANNOT_CACHE_PARTIAL_OBJECT);
        return queryException;
    }

    public static QueryException cannotCompareTablesInExpression(Object data) {
        Object[] args = { data };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_COMPARE_TABLES_IN_EXPRESSION, args));
        queryException.setErrorCode(CANNOT_COMPARE_TABLES_IN_EXPRESSION);
        return queryException;
    }

    public static QueryException cannotCompareTargetForeignKeysToNull(Expression expression, Object value, DatabaseMapping mapping) {
        Object[] args = { expression, mapping, value, CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_COMPARE_TARGET_FOREIGN_KEYS_TO_NULL, args));
        queryException.setErrorCode(CANNOT_COMPARE_TARGET_FOREIGN_KEYS_TO_NULL);
        return queryException;
    }

    public static QueryException cannotConformAndCacheQueryResults(ReadQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_CONFORM_AND_CACHE_QUERY_RESULTS, args), query);
        queryException.setErrorCode(CANNOT_CONFORM_AND_CACHE_QUERY_RESULTS);
        return queryException;
    }

    public static QueryException cannotConformExpression() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_CONFORM_EXPRESSION, args));
        queryException.setErrorCode(CANNOT_CONFORM_EXPRESSION);
        return queryException;
    }

    public static QueryException cannotCreateClone(ContainerPolicy policy, Object anObject) {
        Object[] args = { anObject, anObject.getClass(), policy };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_CREATE_CLONE, args));
        queryException.setErrorCode(CANNOT_CREATE_CLONE);
        return queryException;
    }

    public static QueryException cannotDeleteReadOnlyObject(Object anObject) {
        Object[] args = { anObject.getClass().toString() };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_DELETE_READ_ONLY_OBJECT, args));
        queryException.setErrorCode(CANNOT_DELETE_READ_ONLY_OBJECT);
        return queryException;
    }

    public static QueryException cannotQueryAcrossAVariableOneToOneMapping(DatabaseMapping mapping, ClassDescriptor descriptor) {
        Object[] args = { descriptor.toString(), mapping.toString(), CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_QUERY_ACROSS_VARIABLE_ONE_TO_ONE_MAPPING, args));
        queryException.setErrorCode(CANNOT_QUERY_ACROSS_VARIABLE_ONE_TO_ONE_MAPPING);
        return queryException;
    }

    public static QueryException cannotRemoveFromContainer(Object anObject, Object aContainer, ContainerPolicy policy) {
        Object[] args = { anObject, anObject.getClass(), aContainer.getClass(), policy };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_REMOVE_FROM_CONTAINER, args));
        queryException.setErrorCode(CANNOT_REMOVE_FROM_CONTAINER);
        return queryException;
    }

    public static QueryException cannotSetShouldCheckCacheOnlyOnReportQuery() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_SET_REPORT_QUERY_TO_CHECK_CACHE_ONLY, args));
        queryException.setErrorCode(CANNOT_SET_REPORT_QUERY_TO_CHECK_CACHE_ONLY);
        return queryException;
    }

    public static QueryException couldNotInstantiateContainerClass(Class aClass, Exception exception) {
        Object[] args = { aClass.toString() };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, COULD_NOT_INSTANTIATE_CONTAINER_CLASS, args));
        queryException.setErrorCode(COULD_NOT_INSTANTIATE_CONTAINER_CLASS);
        queryException.setInternalException(exception);
        return queryException;
    }

    public static QueryException descriptorIsMissing(Class referenceClass, DatabaseQuery query) {
        Object[] args = { referenceClass };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, DESCRIPTOR_IS_MISSING, args), query);
        queryException.setErrorCode(DESCRIPTOR_IS_MISSING);
        return queryException;
    }

    public static QueryException descriptorIsMissingForNamedQuery(Class domainClass, String queryName) {
        Object[] args = { domainClass.getName(), queryName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, DESCRIPTOR_IS_MISSING_FOR_NAMED_QUERY, args));
        queryException.setErrorCode(DESCRIPTOR_IS_MISSING_FOR_NAMED_QUERY);
        return queryException;
    }

    public static QueryException discriminatorColumnNotSelected(String expectedColumn, String sqlResultSetMapping){
        Object[] args = { expectedColumn, sqlResultSetMapping };
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, DISCRIMINATOR_COLUMN_NOT_SELECTED, args));
        queryException.setErrorCode(DISCRIMINATOR_COLUMN_NOT_SELECTED);
        return queryException;
    }    
    /**
     * Oct 18, 2000 JED
     * Added this method and exception value
     */
    public static QueryException exampleAndReferenceObjectClassMismatch(Class exampleObjectClass, Class referenceObjectClass, DatabaseQuery query) {
        Object[] args = { exampleObjectClass, referenceObjectClass };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, EXAMPLE_AND_REFERENCE_OBJECT_CLASS_MISMATCH, args));
        queryException.setErrorCode(EXAMPLE_AND_REFERENCE_OBJECT_CLASS_MISMATCH);
        queryException.setQuery(query);
        return queryException;
    }

    /**
     * An exception was thrown while initializing the constructor from the class.
     */
    public static QueryException exceptionWhileInitializingConstructor(Exception thrownException, DatabaseQuery query, Class targetClass) {
        Object[] args = { targetClass, thrownException };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, EXCEPTION_WHILE_LOADING_CONSTRUCTOR, args));
        queryException.setErrorCode(EXCEPTION_WHILE_LOADING_CONSTRUCTOR);
        queryException.setInternalException(thrownException);
        queryException.setQuery(query);
        return queryException;
    }

    /**
     * An exception was throwing while using a ReportQuery with a constructor expression
     */
    public static QueryException exceptionWhileUsingConstructorExpression(Exception thrownException, DatabaseQuery query) {
        Object[] args = { thrownException };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, EXCEPTION_WHILE_USING_CONSTRUCTOR_EXPRESSION, args));
        queryException.setErrorCode(EXCEPTION_WHILE_USING_CONSTRUCTOR_EXPRESSION);
        queryException.setInternalException(thrownException);
        queryException.setQuery(query);
        return queryException;
    }

    /**
     * PUBLIC:
     * Return the exception error message.
     * TopLink error messages are multi-line so that detail descriptions of the exception are given.
     */
    public String getMessage() {
        if (getQuery() == null) {
            return super.getMessage();
        } else {
            return super.getMessage() + cr() + getIndentationString() + ExceptionMessageGenerator.getHeader("QueryHeader") + getQuery().toString();
        }
    }

    /**
     * PUBLIC:
     * Return the query in which the problem was detected.
     */
    public DatabaseQuery getQuery() {
        return query;
    }

    /**
     * PUBLIC:
     * Return the query argements used in the original query when exception is thrown
     */
    public Record getQueryArgumentsRecord() {
        return queryArguments;
    }

    public static QueryException illegalUseOfGetField(Object data) {
        Object[] args = { data };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, ILLEGAL_USE_OF_GETFIELD, args));
        queryException.setErrorCode(ILLEGAL_USE_OF_GETFIELD);
        return queryException;
    }

    public static QueryException illegalUseOfGetTable(Object data) {
        Object[] args = { data };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, ILLEGAL_USE_OF_GETTABLE, args));
        queryException.setErrorCode(ILLEGAL_USE_OF_GETTABLE);
        return queryException;
    }

    public static QueryException illFormedExpression(org.eclipse.persistence.expressions.Expression queryKey) {
        Object[] args = { queryKey };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, ILL_FORMED_EXPRESSION, args));
        queryException.setErrorCode(ILL_FORMED_EXPRESSION);
        return queryException;
    }

    public static QueryException inCannotBeParameterized(DatabaseQuery query) {
        Object[] args = {  };

        QueryException exception = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, IN_CANNOT_BE_PARAMETERIZED, args), query);
        exception.setErrorCode(IN_CANNOT_BE_PARAMETERIZED);
        return exception;
    }

    public static QueryException incorrectClassForObjectComparison(Expression expression, Object value, DatabaseMapping mapping) {
        Object[] args = { expression, mapping, value, CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INCORRECT_CLASS_FOR_OBJECT_COMPARISON, args));
        queryException.setErrorCode(INCORRECT_CLASS_FOR_OBJECT_COMPARISON);
        return queryException;
    }

    public static QueryException incorrectSizeQueryForCursorStream(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INCORRECT_SIZE_QUERY_FOR_CURSOR_STREAM, args), query);
        queryException.setErrorCode(INCORRECT_SIZE_QUERY_FOR_CURSOR_STREAM);
        return queryException;
    }

    public static QueryException incorrectQueryObjectFound(DatabaseQuery query, Class expectedQueryClass) {
        Object[] args = { expectedQueryClass, query.getClass() };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INCORRECT_QUERY_FOUND, args), query);
        queryException.setErrorCode(INCORRECT_QUERY_FOUND);
        return queryException;
    }

    public static QueryException invalidContainerClass(Class containerGiven, Class containerRequired) {
        Object[] args = { containerGiven, containerRequired };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_CONTAINER_CLASS, args));
        queryException.setErrorCode(INVALID_CONTAINER_CLASS);
        return queryException;
    }

    public static QueryException invalidDatabaseAccessor(org.eclipse.persistence.internal.databaseaccess.Accessor accessor) {
        Object[] args = { accessor };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_DATABASE_ACCESSOR, args));
        queryException.setErrorCode(INVALID_DATABASE_ACCESSOR);
        return queryException;
    }

    public static QueryException invalidDatabaseCall(Call call) {
        Object[] args = { call };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_DATABASE_CALL, args));
        queryException.setErrorCode(INVALID_DATABASE_CALL);
        return queryException;
    }

    public static QueryException invalidExpressionForQueryItem(Expression expression, DatabaseQuery owner) {
        Object[] args = { expression };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_QUERY_ITEM, args), owner);
        queryException.setErrorCode(INVALID_QUERY_ITEM);
        return queryException;
    }

    public static QueryException invalidOperation(String operation) {
        Object[] args = { operation };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_OPERATION, args));
        queryException.setErrorCode(INVALID_OPERATION);
        return queryException;
    }

    public static QueryException invalidOperator(Object data) {
        Object[] args = { data };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_OPERATOR, args));
        queryException.setErrorCode(INVALID_OPERATOR);
        return queryException;
    }

    public static QueryException invalidOperatorForObjectComparison(Expression expression) {
        Object[] args = { expression, CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_OPERATOR_FOR_OBJECT_EXPRESSION, args));
        queryException.setErrorCode(INVALID_OPERATOR_FOR_OBJECT_EXPRESSION);
        return queryException;
    }

    public static QueryException invalidQuery(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_QUERY, args), query);
        queryException.setErrorCode(INVALID_QUERY);
        return queryException;
    }

    public static QueryException invalidBuilderInQuery(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_BUILDER_IN_QUERY, args), query);
        queryException.setErrorCode(INVALID_BUILDER_IN_QUERY);
        return queryException;
    }

    public static QueryException invalidQueryKeyInExpression(Object data) {
        Object[] args = { data };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_QUERY_KEY_IN_EXPRESSION, args));
        queryException.setErrorCode(INVALID_QUERY_KEY_IN_EXPRESSION);
        return queryException;
    }

    public static QueryException invalidExpression(Object expression) {
        Object[] args = { expression };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_EXPRESSION, args));
        queryException.setErrorCode(INVALID_EXPRESSION);
        return queryException;
    }

    public static QueryException mappingForExpressionDoesNotSupportJoining(Object expression) {
        Object[] args = { expression };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, MAPPING_FOR_EXPRESSION_DOES_NOT_SUPPORT_JOINING, args));
        queryException.setErrorCode(MAPPING_FOR_EXPRESSION_DOES_NOT_SUPPORT_JOINING);
        return queryException;
    }
    
    public static QueryException mappingForFieldResultNotFound(String[] attributeNames, int currentString){
        String attributeName ="";
        for(int i=0; i<attributeNames.length;i++){
            attributeName=attributeName+attributeNames[i];
        }
        Object[] args = { attributeName, attributeNames[currentString] };
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, MAPPING_FOR_FIELDRESULT_NOT_FOUND, args));
        queryException.setErrorCode(MAPPING_FOR_FIELDRESULT_NOT_FOUND);
        return queryException;
    }  

    public static QueryException invalidQueryOnHistoricalSession(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_QUERY_ON_HISTORICAL_SESSION, args), query);
        queryException.setErrorCode(INVALID_QUERY_ON_HISTORICAL_SESSION);
        return queryException;
    }

    public static QueryException invalidQueryOnServerSession(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_QUERY_ON_SERVER_SESSION, args), query);
        queryException.setErrorCode(INVALID_QUERY_ON_SERVER_SESSION);
        return queryException;
    }

    public static QueryException invalidTableForFieldInExpression(Object data) {
        Object[] args = { data };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_TABLE_FOR_FIELD_IN_EXPRESSION, args));
        queryException.setErrorCode(INVALID_TABLE_FOR_FIELD_IN_EXPRESSION);
        return queryException;
    }
    
    public static QueryException invalidTypeExpression(Object expression) {
        Object[] args = { expression };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_TYPE_EXPRESSION, args));
        queryException.setErrorCode(INVALID_TYPE_EXPRESSION);
        return queryException;
    }

    public static QueryException invalidUseOfAnyOfInExpression(Object data) {
        Object[] args = { data };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_USE_OF_ANY_OF_IN_EXPRESSION, args));
        queryException.setErrorCode(INVALID_USE_OF_ANY_OF_IN_EXPRESSION);
        return queryException;
    }

    public static QueryException joinExpressionsNotApplicableOnNonObjectReportItem(String expressionType, String itemName) {
        Object[] args = { expressionType, itemName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, JOIN_EXPRESSIONS_NOT_APPLICABLE_ON_NON_OBJECT_REPORT_ITEM, args));
        queryException.setErrorCode(JOIN_EXPRESSIONS_NOT_APPLICABLE_ON_NON_OBJECT_REPORT_ITEM);
        return queryException;
    }

    public static QueryException joiningAcrossInheritanceClassWithMultipleTablesNotSupported(DatabaseQuery query, Class joinClass) {
        Object[] args = { query, joinClass };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, JOINING_ACROSS_INHERITANCE_WITH_MULTIPLE_TABLES, args));
        queryException.setErrorCode(JOINING_ACROSS_INHERITANCE_WITH_MULTIPLE_TABLES);
        return queryException;
    }

    public static QueryException invalidUseOfToManyQueryKeyInExpression(Object data) {
        Object[] args = { data };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INVALID_USE_OF_TO_MANY_QUERY_KEY_IN_EXPRESSION, args));
        queryException.setErrorCode(INVALID_USE_OF_TO_MANY_QUERY_KEY_IN_EXPRESSION);
        return queryException;
    }

    public static QueryException isolatedQueryExecutedOnServerSession() {
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, ISOLATED_QUERY_EXECUTED_ON_SERVER_SESSION, new Object[] {  }));
        queryException.setErrorCode(ISOLATED_QUERY_EXECUTED_ON_SERVER_SESSION);
        return queryException;
    }

    public static ValidationException mapKeyIsNull(Object element, Object container) {
        Object[] args = { element.getClass(), container.getClass() };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(QueryException.class, MAP_KEY_IS_NULL, args));
        validationException.setErrorCode(MAP_KEY_IS_NULL);
        return validationException;
    }
    
    public static ValidationException mapKeyNotComparable(Object anObject, Object aContainer) {
        String obj;
        String objType;
        
        if (anObject == null) {
            obj = "null";
            objType = "NULL";
        } else {
            obj = anObject.toString();
            objType = anObject.getClass().toString();
        }

        Object[] args = { obj, objType, aContainer, aContainer.getClass() };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(QueryException.class, MAP_KEY_NOT_COMPARABLE, args));
        validationException.setErrorCode(MAP_KEY_NOT_COMPARABLE);
        return validationException;
    }

    public static QueryException methodDoesNotExistInContainerClass(String methodName, Class aClass) {
        Object[] args = { methodName, aClass };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, METHOD_DOES_NOT_EXIST_IN_CONTAINER_CLASS, args));
        queryException.setErrorCode(METHOD_DOES_NOT_EXIST_IN_CONTAINER_CLASS);
        return queryException;
    }

    public static QueryException methodDoesNotExistOnExpression(String methodName, Class[] argTypes) {
        Object[] args = { methodName, argTypes };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, METHOD_DOES_NOT_EXIST_ON_EXPRESSION, args));
        queryException.setErrorCode(METHOD_DOES_NOT_EXIST_ON_EXPRESSION);
        return queryException;
    }

    public static QueryException methodInvocationFailed(java.lang.reflect.Method aMethod, Object anObject, Exception ex) {
        Object[] args = { aMethod, anObject, anObject.getClass() };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, METHOD_INVOCATION_FAILED, args));
        queryException.setErrorCode(METHOD_INVOCATION_FAILED);
        queryException.setInternalException(ex);
        return queryException;
    }

    public static QueryException methodNotValid(Object aReceiver, String methodName) {
        Object[] args = { methodName, aReceiver };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, METHOD_NOT_VALID, args));
        queryException.setErrorCode(METHOD_NOT_VALID);
        return queryException;
    }

    public static QueryException mustInstantiateValueholders() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, MUST_INSTANTIATE_VALUEHOLDERS, args));
        queryException.setErrorCode(MUST_INSTANTIATE_VALUEHOLDERS);
        return queryException;
    }

    /**
     * Oct 19, 2000 JED
     * Added this method and exception value
     */
    public static QueryException noAttributesForReportQuery(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, NO_ATTRIBUTES_FOR_REPORT_QUERY, args));
        queryException.setErrorCode(NO_ATTRIBUTES_FOR_REPORT_QUERY);
        queryException.setQuery(query);
        return queryException;
    }

    public static QueryException noConcreteClassIndicated(AbstractRecord row, DatabaseQuery query) {
        Object[] args = { row };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, NO_CONCRETE_CLASS_INDICATED, args), query);
        queryException.setErrorCode(NO_CONCRETE_CLASS_INDICATED);
        return queryException;
    }

    public static QueryException noCallOrInteractionSpecified() {
        Object[] args = {  };
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, QueryException.NO_CALL_OR_INTERACTION_SPECIFIED, args));
        queryException.setErrorCode(NO_CALL_OR_INTERACTION_SPECIFIED);
        return queryException;
    }

    public static QueryException noCursorSupport(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, NO_CURSOR_SUPPORT, args), query);
        queryException.setErrorCode(NO_CURSOR_SUPPORT);
        return queryException;
    }

    public static QueryException noDescriptorForClassFromInheritancePolicy(DatabaseQuery query, Class referenceClass) {
        Object[] args = { String.valueOf(referenceClass) };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, NO_DESCRIPTOR_FOR_SUBCLASS, args), query);
        queryException.setErrorCode(NO_DESCRIPTOR_FOR_SUBCLASS);
        return queryException;
    }

    public static QueryException noExpressionBuilderFound(Expression expression) {
        Object[] args = { expression, CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, NO_EXPRESSION_BUILDER_CLASS_FOUND, args));
        queryException.setErrorCode(NO_EXPRESSION_BUILDER_CLASS_FOUND);
        return queryException;
    }

    public static QueryException nullPrimaryKeyInBuildingObject(DatabaseQuery query, AbstractRecord databaseRow) {
        Object[] args = { databaseRow };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, NULL_PRIMARY_KEY_IN_BUILDING_OBJECT, args), query);
        queryException.setErrorCode(NULL_PRIMARY_KEY_IN_BUILDING_OBJECT);
        return queryException;
    }

    public static QueryException objectComparisonsCannotBeParameterized(Expression expression) {
        Object[] args = { expression, CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, OBJECT_COMPARISON_CANNOT_BE_PARAMETERIZED, args));
        queryException.setErrorCode(OBJECT_COMPARISON_CANNOT_BE_PARAMETERIZED);
        return queryException;
    }

    public static QueryException objectDoesNotExistInCache(Object object) {
        Object[] args = { object };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, OBJECT_DOES_NOT_EXIST_IN_CACHE, args));
        queryException.setErrorCode(OBJECT_DOES_NOT_EXIST_IN_CACHE);
        return queryException;
    }

    public static QueryException classPkDoesNotExistInCache(Class theClass, Object primaryKey) {
        Object[] args = { theClass, primaryKey };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CLASS_PK_DOES_NOT_EXIST_IN_CACHE, args));
        queryException.setErrorCode(CLASS_PK_DOES_NOT_EXIST_IN_CACHE);
        return queryException;
    }

    public static QueryException clearQueryResultsNotSupported(ReadQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CLEAR_QUERY_RESULTS_NOT_SUPPORTED, args), query);
        queryException.setErrorCode(CLEAR_QUERY_RESULTS_NOT_SUPPORTED);
        return queryException;
    }

    public static QueryException objectToInsertIsEmpty(DatabaseTable table) {
        Object[] args = { table };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, OBJECT_TO_INSERT_IS_EMPTY, args));
        queryException.setErrorCode(OBJECT_TO_INSERT_IS_EMPTY);
        return queryException;
    }

    public static QueryException objectToModifyNotSpecified(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, OBJECT_TO_MODIFY_NOT_SPECIFIED, args), query);
        queryException.setErrorCode(OBJECT_TO_MODIFY_NOT_SPECIFIED);
        return queryException;
    }

    public static QueryException outerJoinIsOnlyValidForOneToOneMappings(DatabaseMapping mapping) {
        Object[] args = { mapping.toString() };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, OUTER_JOIN_ONLY_VALID_FOR_ONE_TO_ONE, args));
        queryException.setErrorCode(OUTER_JOIN_ONLY_VALID_FOR_ONE_TO_ONE);
        return queryException;
    }

    public static QueryException queryNotDefined() {
        Object[] args = { "", "" };
        return queryNotDefined(args);
    }

    public static QueryException queryNotDefined(String queryName) {
        Object[] args = { queryName, "" };
        return queryNotDefined(args);
    }

    public static QueryException queryNotDefined(String queryName, Class domainClass) {
        Object[] args = { queryName, domainClass };
        return queryNotDefined(args);
    }

    private static QueryException queryNotDefined(Object[] args) {
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, QUERY_NOT_DEFINED, args));
        queryException.setErrorCode(QUERY_NOT_DEFINED);
        return queryException;
    }

    public static QueryException querySentToInactiveUnitOfWork(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, QUERY_SENT_TO_INACTIVE_UNIT_OF_WORK, args), query);
        queryException.setErrorCode(QUERY_SENT_TO_INACTIVE_UNIT_OF_WORK);
        return queryException;
    }

    public static QueryException readBeyondStream(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, READ_BEYOND_QUERY, args), query);
        queryException.setErrorCode(READ_BEYOND_QUERY);
        return queryException;
    }

    public static QueryException redirectionClassOrMethodNotSet(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, REDIRECTION_CLASS_OR_METHOD_NOT_SET, args), query);
        queryException.setErrorCode(REDIRECTION_CLASS_OR_METHOD_NOT_SET);
        return queryException;
    }
    
    public static QueryException unableToSetRedirectorOnQueryFromHint(DatabaseQuery query, String hint, String redirectorClass, Exception ex) {
        Object[] args = {hint, redirectorClass  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, UNABLE_TO_SET_REDIRECTOR_FROM_HINT, args), query);
        queryException.setInternalException(ex);
        queryException.setErrorCode(UNABLE_TO_SET_REDIRECTOR_FROM_HINT);
        return queryException;
    }

    public static QueryException redirectionMethodError(Exception exception, DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, REDIRECTION_METHOD_ERROR, args), query);
        queryException.setInternalException(exception);
        queryException.setErrorCode(REDIRECTION_METHOD_ERROR);
        return queryException;
    }

    public static QueryException redirectionMethodNotDefinedCorrectly(Class methodClass, String methodName, Exception exception, DatabaseQuery query) {
        Object[] args = { methodClass, methodName, CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, REDIRECTION_METHOD_NOT_DEFINED_CORRECTLY, args), query);
        queryException.setInternalException(exception);
        queryException.setErrorCode(REDIRECTION_METHOD_NOT_DEFINED_CORRECTLY);
        return queryException;
    }

    public static QueryException referenceClassMissing(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, REFERENCE_CLASS_MISSING, args), query);
        queryException.setErrorCode(REFERENCE_CLASS_MISSING);
        return queryException;
    }

    public static QueryException refreshNotPossibleWithoutCache(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, REFRESH_NOT_POSSIBLE_WITHOUT_CACHE, args), query);
        queryException.setErrorCode(REFRESH_NOT_POSSIBLE_WITHOUT_CACHE);
        return queryException;
    }

    public static QueryException reportQueryResultSizeMismatch(int expected, int retrieved) {
        Object[] args = { Integer.valueOf(expected), Integer.valueOf(retrieved) };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, REPORT_QUERY_RESULT_SIZE_MISMATCH, args));
        queryException.setErrorCode(REPORT_QUERY_RESULT_SIZE_MISMATCH);
        return queryException;
    }

    public static QueryException reportQueryResultWithoutPKs(ReportQueryResult result) {
        Object[] args = { result, CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, REPORT_RESULT_WITHOUT_PKS, args), null);
        queryException.setErrorCode(REPORT_RESULT_WITHOUT_PKS);
        return queryException;
    }

    public static QueryException parameterNameMismatch(String badParameterName) {
        Object[] args = { badParameterName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, PARAMETER_NAME_MISMATCH, args), null);
        queryException.setErrorCode(PARAMETER_NAME_MISMATCH);
        return queryException;
    }

    public static QueryException polymorphicReportItemWithMultipletableNotSupported(String itemName, Expression expression, DatabaseQuery owner) {
        Object[] args = { itemName, expression };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, POLYMORPHIC_REPORT_ITEM_NOT_SUPPORTED, args), owner);
        queryException.setErrorCode(POLYMORPHIC_REPORT_ITEM_NOT_SUPPORTED);
        return queryException;
    }

    public static QueryException selectionObjectCannotBeNull(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, SELECTION_OBJECT_CANNOT_BE_NULL, args), query);
        queryException.setErrorCode(SELECTION_OBJECT_CANNOT_BE_NULL);
        return queryException;
    }

    /**
     * INTERNAL:
     * Set the query in which the problem was detected.
     */
    public void setQuery(DatabaseQuery query) {
        this.query = query;
    }

    /**
     * INTERNAL:
     * Set the query argements used in the original query when exception is thrown
     */
    public void setQueryArguments(AbstractRecord queryArguments) {
        this.queryArguments = queryArguments;
    }

    public static QueryException sizeOnlySupportedOnExpressionQueries(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, SIZE_ONLY_SUPPORTED_ON_EXPRESSION_QUERIES, args), query);
        queryException.setErrorCode(SIZE_ONLY_SUPPORTED_ON_EXPRESSION_QUERIES);
        return queryException;
    }

    public static QueryException specifiedPartialAttributeDoesNotExist(DatabaseQuery query, String attributeName, String targetClassName) {
        Object[] args = { attributeName, targetClassName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, SPECIFIED_PARTIAL_ATTRIBUTE_DOES_NOT_EXIST, args), query);
        queryException.setErrorCode(SPECIFIED_PARTIAL_ATTRIBUTE_DOES_NOT_EXIST);
        return queryException;
    }

    public static QueryException sqlStatementNotSetProperly(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, SQL_STATEMENT_NOT_SET_PROPERLY, args), query);
        queryException.setErrorCode(SQL_STATEMENT_NOT_SET_PROPERLY);
        return queryException;
    }

    public static QueryException typeMismatchBetweenAttributeAndConstantOnExpression(Class constantClass, Class attributeClass) {
        Object[] args = { constantClass, attributeClass };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, TYPE_MISMATCH_BETWEEN_ATTRIBUTE_AND_CONSTANT_ON_EXPRESSION, args));
        queryException.setErrorCode(TYPE_MISMATCH_BETWEEN_ATTRIBUTE_AND_CONSTANT_ON_EXPRESSION);
        return queryException;
    }

    public static QueryException unnamedQueryOnSessionBroker(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, UNNAMED_QUERY_ON_SESSION_BROKER, args), query);
        queryException.setErrorCode(UNNAMED_QUERY_ON_SESSION_BROKER);
        return queryException;
    }

    public static QueryException unsupportedMappingForObjectComparison(DatabaseMapping mapping, Expression expression) {
        Object[] args = { mapping, expression, CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, UNSUPPORTED_MAPPING_FOR_OBJECT_COMPARISON, args));
        queryException.setErrorCode(UNSUPPORTED_MAPPING_FOR_OBJECT_COMPARISON);
        return queryException;
    }

    public static QueryException updateStatementsNotSpecified() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, UPDATE_STATEMENTS_NOT_SPECIFIED, args));
        queryException.setErrorCode(UPDATE_STATEMENTS_NOT_SPECIFIED);
        return queryException;
    }

    public static QueryException inheritanceWithMultipleTablesNotSupported() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INHERITANCE_WITH_MULTIPLE_TABLES_NOT_SUPPORTED, args));
        queryException.setErrorCode(INHERITANCE_WITH_MULTIPLE_TABLES_NOT_SUPPORTED);
        return queryException;
    }

    public static QueryException cloneMethodRequired() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CLONE_METHOD_REQUIRED, args));
        queryException.setErrorCode(CLONE_METHOD_REQUIRED);
        return queryException;
    }

    public static QueryException cloneMethodInaccessible() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CLONE_METHOD_INACCESSIBLE, args));
        queryException.setErrorCode(CLONE_METHOD_INACCESSIBLE);
        return queryException;
    }

    public static QueryException cloneMethodThrowException(Throwable exception) {
        Object[] args = { exception };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CLONE_METHOD_THORW_EXCEPTION, args));
        queryException.setErrorCode(CLONE_METHOD_THORW_EXCEPTION);
        return queryException;
    }

    public static QueryException unexpectedInvocation(String message) {
        Object[] args = { message };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, UNEXPECTED_INVOCATION, args));
        queryException.setErrorCode(UNEXPECTED_INVOCATION);
        return queryException;
    }

    public static QueryException multipleRowsDetectedFromReadObjectQuery() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, MULTIPLE_ROWS_DETECTED_FROM_SINGLE_OBJECT_READ, args));
        queryException.setErrorCode(MULTIPLE_ROWS_DETECTED_FROM_SINGLE_OBJECT_READ);
        return queryException;
    }

    // The following exceptions have been added for flashback...
    public static QueryException historicalQueriesMustPreserveGlobalCache() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, HISTORICAL_QUERIES_MUST_PRESERVE_GLOBAL_CACHE, args));
        queryException.setErrorCode(HISTORICAL_QUERIES_MUST_PRESERVE_GLOBAL_CACHE);
        return queryException;
    }

    public static QueryException historicalQueriesOnlySupportedOnOracle() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, HISTORICAL_QUERIES_ONLY_SUPPORTED_ON_ORACLE, args));
        queryException.setErrorCode(HISTORICAL_QUERIES_ONLY_SUPPORTED_ON_ORACLE);
        return queryException;
    }

    public static QueryException mustUseCursorStreamPolicy() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, MUST_USE_CURSOR_STREAM_POLICY, args));
        queryException.setErrorCode(MUST_USE_CURSOR_STREAM_POLICY);
        return queryException;
    }

    public static QueryException fetchGroupNotDefinedInDescriptor(String fetchGroupName) {
        Object[] args = { fetchGroupName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, QUERY_FETCHGROUP_NOT_DEFINED_IN_DESCRIPTOR, args));
        queryException.setErrorCode(QUERY_FETCHGROUP_NOT_DEFINED_IN_DESCRIPTOR);
        return queryException;
    }

    public static QueryException cannotConformUnfetchedAttribute(String attrbuteName) {
        Object[] args = { attrbuteName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_CONFORM_UNFETCHED_ATTRIBUTE, args));
        queryException.setErrorCode(CANNOT_CONFORM_UNFETCHED_ATTRIBUTE);
        return queryException;
    }

    public static QueryException fetchGroupAttributeNotMapped(String attrbuteName) {
        Object[] args = { attrbuteName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, FETCH_GROUP_ATTRIBUTE_NOT_MAPPED, args));
        queryException.setErrorCode(FETCH_GROUP_ATTRIBUTE_NOT_MAPPED);
        return queryException;
    }

    public static QueryException fetchGroupNotSupportOnReportQuery() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, FETCH_GROUP_NOT_SUPPORT_ON_REPORT_QUERY, args));
        queryException.setErrorCode(FETCH_GROUP_NOT_SUPPORT_ON_REPORT_QUERY);
        return queryException;
    }

    public static QueryException fetchGroupNotSupportOnPartialAttributeReading() {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, FETCH_GROUP_NOT_SUPPORT_ON_PARTIAL_ATTRIBUTE_READING, args));
        queryException.setErrorCode(FETCH_GROUP_NOT_SUPPORT_ON_PARTIAL_ATTRIBUTE_READING);
        return queryException;
    }

    public static QueryException fetchGroupValidOnlyIfFetchGroupManagerInDescriptor(String descriptorName, String queryName) {
        Object[] args = { descriptorName, queryName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, FETCHGROUP_VALID_ONLY_IF_FETCHGROUP_MANAGER_IN_DESCRIPTOR, args));
        queryException.setErrorCode(FETCHGROUP_VALID_ONLY_IF_FETCHGROUP_MANAGER_IN_DESCRIPTOR);
        return queryException;
    }

    public static QueryException reflectiveCallOnTopLinkClassFailed(String className, Exception e) {
        Object[] args = { className };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, REFLECTIVE_CALL_ON_TOPLINK_CLASS_FAILED, args));
        queryException.setErrorCode(REFLECTIVE_CALL_ON_TOPLINK_CLASS_FAILED);
        queryException.setInternalException(e);
        return queryException;
    }

    public static QueryException refreshNotPossibleWithCheckCacheOnly(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, REFRESH_NOT_POSSIBLE_WITH_CHECK_CACHE_ONLY, args), query);
        queryException.setErrorCode(REFRESH_NOT_POSSIBLE_WITH_CHECK_CACHE_ONLY);
        return queryException;
    }

    public static QueryException deleteAllQuerySpecifiesObjectsButNotSelectionCriteria(ClassDescriptor descriptor, DatabaseQuery query, String objects) {
        Object[] args = { descriptor.toString(), CR, objects };
  
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, DELETE_ALL_QUERY_SPECIFIES_OBJECTS_BUT_NOT_SELECTION_CRITERIA, args), query);
        queryException.setErrorCode(DELETE_ALL_QUERY_SPECIFIES_OBJECTS_BUT_NOT_SELECTION_CRITERIA);
        return queryException;
    }

    public static QueryException updateAllQueryAddUpdateFieldIsNull(DatabaseQuery query) {
        Object[] args = {  };
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, UPDATE_ALL_QUERY_ADD_UPDATE_FIELD_IS_NULL, args), query);
        queryException.setErrorCode(UPDATE_ALL_QUERY_ADD_UPDATE_FIELD_IS_NULL);
        return queryException;
    }

    public static QueryException updateAllQueryAddUpdateDoesNotDefineField(ClassDescriptor descriptor, DatabaseQuery query, String attributeNameOrExpression) {
        Object[] args = { descriptor.toString(), CR, attributeNameOrExpression };
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, UPDATE_ALL_QUERY_ADD_UPDATE_DOES_NOT_DEFINE_FIELD, args), query);
        queryException.setErrorCode(UPDATE_ALL_QUERY_ADD_UPDATE_DOES_NOT_DEFINE_FIELD);
        return queryException;
    }

    public static QueryException updateAllQueryAddUpdateDefinesWrongField(ClassDescriptor descriptor, DatabaseQuery query, String attributeNameOrExpression, String wrongField) {
        Object[] args = { descriptor.toString(), CR, attributeNameOrExpression, wrongField };
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, UPDATE_ALL_QUERY_ADD_UPDATE_DEFINES_WRONG_FIELD, args), query);
        queryException.setErrorCode(UPDATE_ALL_QUERY_ADD_UPDATE_DEFINES_WRONG_FIELD);
        return queryException;
    }

    public static QueryException tempTablesNotSupported(DatabaseQuery query, String platformClassName) {
        Object[] args = { platformClassName };
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, TEMP_TABLES_NOT_SUPPORTED, args), query);
        queryException.setErrorCode(TEMP_TABLES_NOT_SUPPORTED);
        return queryException;
    }
    
    public static QueryException classNotFoundWhileUsingQueryHint(DatabaseQuery query, Object hintValue, Exception exc) {
        Object[] args = { hintValue };
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CLASS_NOT_FOUND_WHILE_USING_QUERY_HINT, args), query);
        queryException.setErrorCode(CLASS_NOT_FOUND_WHILE_USING_QUERY_HINT);
        queryException.setInternalException(exc);
        return queryException;
    }
    
    public static QueryException queryHintNavigatedIllegalRelationship(DatabaseQuery query, String hintName, Object hintValue, String relationship) {
        Object[] args = { hintName, hintValue, relationship };
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, QUERY_HINT_NAVIGATED_ILLEGAL_RELATIONSHIP, args), query);
        queryException.setErrorCode(QUERY_HINT_NAVIGATED_ILLEGAL_RELATIONSHIP);
        return queryException;
    }
    
    public static QueryException queryHintNavigatedNonExistantRelationship(DatabaseQuery query, String hintName, Object hintValue, String relationship) {
        Object[] args = { hintName, hintValue, relationship };
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, QUERY_HINT_NAVIGATED_NON_EXISTANT_RELATIONSHIP, args), query);
        queryException.setErrorCode(QUERY_HINT_NAVIGATED_NON_EXISTANT_RELATIONSHIP);
        return queryException;
    }
    
    public static QueryException queryHintDidNotContainEnoughTokens(DatabaseQuery query, String hintName, Object hintValue) {
        Object[] args = { hintName, hintValue };
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, QUERY_HINT_DID_NOT_CONTAIN_ENOUGH_TOKENS, args), query);
        queryException.setErrorCode(QUERY_HINT_DID_NOT_CONTAIN_ENOUGH_TOKENS);
        return queryException;
    }

    public static QueryException distinctCountOnOuterJoinedCompositePK(
        ClassDescriptor descr, DatabaseQuery query) {
        Object[] args = { descr.getJavaClass().getName(), descr.toString() };
        
        QueryException queryException = new QueryException(
            ExceptionMessageGenerator.buildMessage(
                QueryException.class, DISTINCT_COUNT_ON_OUTER_JOINED_COMPOSITE_PK, args), 
            query);
        queryException.setErrorCode(DISTINCT_COUNT_ON_OUTER_JOINED_COMPOSITE_PK);
        return queryException;
    }

    public static QueryException queryHintContainedInvalidIntegerValue(String hintName, Object hintValue, Exception e) {
        Object[] args = { hintName, hintValue};
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, QUERY_HINT_CONTAINED_INVALID_INTEGER_VALUE, args),null,e);
        queryException.setErrorCode(QUERY_HINT_CONTAINED_INVALID_INTEGER_VALUE);
        return queryException;
    }
    
    
    public static QueryException expressionDoesNotSupportPartialAttributeReading(Expression expression) {
        Object[] args = {expression};
    
        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, EXPRESSION_DOES_NOT_SUPPORT_PARTIAL_ATTRIBUTE_READING, args));
        queryException.setErrorCode(EXPRESSION_DOES_NOT_SUPPORT_PARTIAL_ATTRIBUTE_READING);
        return queryException;
    }

    public static QueryException addArgumentsNotSupported(String argumentType) {
        Object[] args = { argumentType };

        QueryException queryException = 
            new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class,
                ADD_ARGS_NOT_SUPPORTED, args));
        queryException.setErrorCode(ADD_ARGS_NOT_SUPPORTED);
        return queryException;
    }

    public static QueryException unnamedArgumentsNotSupported() {
        Object[] args = { };

        QueryException queryException = 
            new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class,
		UNNAMED_ARG_NOT_SUPPORTED, args));
        queryException.setErrorCode(UNNAMED_ARG_NOT_SUPPORTED);
        return queryException;
    }

    public static QueryException columnResultNotFound(DatabaseField dbField) {
        Object[] args = { dbField };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, COLUMN_RESULT_NOT_FOUND, args));
        queryException.setErrorCode(COLUMN_RESULT_NOT_FOUND);
        return queryException;
    }
    
    public static QueryException compatibleTypeNotSet(DatabaseType type) {
        Object[] args = { type };

        QueryException queryException = 
            new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class,
                COMPATIBLE_TYPE_NOT_SET, args));
        queryException.setErrorCode(COMPATIBLE_TYPE_NOT_SET);
        return queryException;
    }

    public static QueryException typeNameNotSet(DatabaseType type) {
        Object[] args = { type };

        QueryException queryException = 
            new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class,
            		TYPE_NAME_NOT_SET, args));
        queryException.setErrorCode(TYPE_NAME_NOT_SET);
        return queryException;
    }

    public static QueryException errorInstantiatedClassForQueryHint(Exception exception, DatabaseQuery query, Class theClass, String hint) {
        Object[] args = { theClass, hint };

        QueryException queryException = 
            new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class,
                ERROR_INSTANTIATING_CLASS_FOR_QUERY_HINT, args), query);
        queryException.setErrorCode(ERROR_INSTANTIATING_CLASS_FOR_QUERY_HINT);
        queryException.setInternalException(exception);
        return queryException;
    }

    public static QueryException noRelationTableInManyToManyQueryKey(ManyToManyQueryKey queryKey, Expression expression) {
        Object[] args = { queryKey, expression, CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, NO_RELATION_TABLE_IN_MANY_TO_MANY_QUERY_KEY, args));
        queryException.setErrorCode(NO_RELATION_TABLE_IN_MANY_TO_MANY_QUERY_KEY);
        return queryException;
    }
    
    public static QueryException exceptionWhileReadingMapKey(Object object, Exception ex) {
        Object[] args = { object, ex };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, EXCEPTION_WHILE_READING_MAP_KEY, args));
        queryException.setErrorCode(EXCEPTION_WHILE_READING_MAP_KEY);
        queryException.setInternalException(ex);
        return queryException;
    }
    
    public static QueryException cannotAddElementWithoutKeyToMap(Object object) {
        Object[] args = { object };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_ADD_ELEMENT_WITHOUT_KEY_TO_MAP, args));
        queryException.setErrorCode(CANNOT_ADD_ELEMENT_WITHOUT_KEY_TO_MAP);
        return queryException;
    }
    
    public static QueryException cannotUnwrapNonMapMembers(Object object) {
        Object[] args = { object };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CANNOT_UNWRAP_NON_MAP_MEMBERS, args));
        queryException.setErrorCode(CANNOT_UNWRAP_NON_MAP_MEMBERS);
        return queryException;
    }
    
    public static QueryException noMappingForMapEntryExpression(Expression baseExpression){
        Object[] args = { baseExpression };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, NO_MAPPING_FOR_MAP_ENTRY_EXPRESSION , args));
        queryException.setErrorCode(NO_MAPPING_FOR_MAP_ENTRY_EXPRESSION );
        return queryException;
    }
    
    public static QueryException mapEntryExpressionForNonCollection(Expression baseExpression, DatabaseMapping mapping){
        Object[] args = { baseExpression, mapping };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, MAP_ENTRY_EXPRESSION_FOR_NON_COLLECTION  , args));
        queryException.setErrorCode(MAP_ENTRY_EXPRESSION_FOR_NON_COLLECTION  );
        return queryException;
    }
    
    public static QueryException mapEntryExpressionForNonMap(Expression baseExpression, DatabaseMapping mapping){
        Object[] args = { baseExpression, mapping };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, MAP_ENTRY_EXPRESSION_FOR_NON_MAP   , args));
        queryException.setErrorCode(MAP_ENTRY_EXPRESSION_FOR_NON_MAP   );
        return queryException;
    }

    public static QueryException listOrderFieldWrongValue(DatabaseQuery query, DatabaseField field, List wrongOrderValuesList) {
        Object[] args = { field, wrongOrderValuesList, CR };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, LIST_ORDER_FIELD_WRONG_VALUE, args), query);
        queryException.setErrorCode(LIST_ORDER_FIELD_WRONG_VALUE);
        return queryException;
    }
    
    public static QueryException indexRequiresQueryKeyExpression(Expression expression) {
        Object[] args = { expression };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INDEX_REQUIRES_QUERY_KEY_EXPRESSION, args));
        queryException.setErrorCode(INDEX_REQUIRES_QUERY_KEY_EXPRESSION);
        return queryException;
    }

    public static QueryException indexRequiresCollectionMappingWithListOrderField(Expression expression, DatabaseMapping mapping) {
        Object[] args = { expression, mapping };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, INDEX_REQUIRES_COLLECTION_MAPPING_WITH_LIST_ORDER_FIELD, args));
        queryException.setErrorCode(INDEX_REQUIRES_COLLECTION_MAPPING_WITH_LIST_ORDER_FIELD);
        return queryException;
    }

    public static QueryException batchReadingInRequiresSingletonPrimaryKey(DatabaseQuery query) {
        Object[] args = {  };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, BATCH_IN_REQUIRES_SINGLETON_PK, args), query);
        queryException.setErrorCode(BATCH_IN_REQUIRES_SINGLETON_PK);
        return queryException;
    }
    
    public static QueryException couldNotFindCastDescriptor(Class castClass, Expression base) {
        Object[] args = { castClass, base };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, COULD_NOT_FIND_CAST_DESCRIPTOR, args));
        queryException.setErrorCode(COULD_NOT_FIND_CAST_DESCRIPTOR);
        return queryException;
    }

    public static QueryException castMustUseInheritance(Expression base) {
        Object[] args = { base };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, CAST_MUST_USE_INHERITANCE, args));
        queryException.setErrorCode(CAST_MUST_USE_INHERITANCE);
        return queryException;
    }

    public static QueryException prepareFailed(Exception error, DatabaseQuery query) {
        Object[] args = { error };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, PREPARE_FAILED, args), query, error);
        queryException.setErrorCode(PREPARE_FAILED);
        return queryException;
    }

    public static QueryException originalQueryMustUseBatchIN(DatabaseMapping mapping, DatabaseQuery query) {
        Object[] args = { mapping };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, ORIGINAL_QUERY_MUST_USE_IN, args), query);
        queryException.setErrorCode(ORIGINAL_QUERY_MUST_USE_IN);
        return queryException;
    }

    public static QueryException partitioningNotSupported(AbstractSession session, DatabaseQuery query) {
        Object[] args = { session };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, PARTIONING_NOT_SUPPORTED, args), query);
        queryException.setErrorCode(PARTIONING_NOT_SUPPORTED);
        return queryException;
    }

    public static QueryException missingConnectionPool(String poolName, DatabaseQuery query) {
        Object[] args = { poolName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, MISSING_CONNECTION_POOL, args), query);
        queryException.setErrorCode(MISSING_CONNECTION_POOL);
        return queryException;
    }

    public static QueryException failoverFailed(String poolName) {
        Object[] args = { poolName };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, FAILOVER_FAILED, args));
        queryException.setErrorCode(FAILOVER_FAILED);
        return queryException;
    }
    
    public static QueryException resultSetAccessOptimizationIsNotPossible(DatabaseQuery query) {
        Object[] args = {};

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, RESULT_SET_ACCESS_OPTIMIZATION_IS_NOT_POSSIBLE, args), query);
        queryException.setErrorCode(RESULT_SET_ACCESS_OPTIMIZATION_IS_NOT_POSSIBLE);
        return queryException;
    }
}

