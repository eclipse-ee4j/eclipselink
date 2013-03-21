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
 *     04/05/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 3)
 *     04/21/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 5)
 *     06/30/2011-2.3.1 Guy Pelletier 
 *       - 341940: Add disable/enable allowing native queries
 *     07/11/2011-2.4 Guy Pelletier
 *       - 343632: Can't map a compound constraint because of exception: 
 *                 The reference column name [y] mapped on the element [field x] 
 *                 does not correspond to a valid field on the mapping reference
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType
 *     22/05/2012-2.4 Guy Pelletier  
 *       - 380008: Multitenant persistence units with a dedicated emf should force tenant property specification up front.
 *     10/09/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     10/30/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     11/28/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 ******************************************************************************/  
package org.eclipse.persistence.exceptions;

import java.util.HashSet;
import java.util.Vector;
import java.lang.reflect.*;

import org.eclipse.persistence.mappings.DatabaseMapping;// 78aclt
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * <p><b>Purpose</b>: This exception is used if incorrect state or method arguments are detected
 * in a general TopLink object.
 */
public class ValidationException extends EclipseLinkException {
    public static final int LOGIN_BEFORE_ALLOCATING_CLIENT_SESSIONS = 7001;
    public static final int POOL_NAME_DOES_NOT_EXIST = 7002;
    public static final int MAX_SIZE_LESS_THAN_MIN_SIZE = 7003;
    public static final int POOLS_MUST_BE_CONFIGURED_BEFORE_LOGIN = 7004;
    public static final int JAVA_TYPE_IS_NOT_A_VALID_DATABASE_TYPE = 7008;
    public static final int MISSING_DESCRIPTOR = 7009;
    public static final int START_INDEX_OUT_OF_RANGE = 7010;
    public static final int STOP_INDEX_OUT_OF_RANGE = 7011;
    public static final int FATAL_ERROR_OCCURRED = 7012;
    public static final int NO_PROPERTIES_FILE_FOUND = 7013;
    public static final int CHILD_DESCRIPTORS_DO_NOT_HAVE_IDENTITY_MAP = 7017;
    public static final int FILE_ERROR = 7018;
    public static final int INCORRECT_LOGIN_INSTANCE_PROVIDED = 7023;
    public static final int INVALID_MERGE_POLICY = 7024;
    public static final int ONLY_FIELDS_ARE_VALID_KEYS_FOR_DATABASE_ROWS = 7025;
    public static final int SEQUENCE_SETUP_INCORRECTLY = 7027;
    public static final int WRITE_OBJECT_NOT_ALLOWED_IN_UNIT_OF_WORK = 7028;
    public static final int CANNOT_SET_READ_POOL_SIZE_AFTER_LOGIN = 7030;
    public static final int CANNOT_ADD_DESCRIPTORS_TO_SESSION_BROKER = 7031;
    public static final int NO_SESSION_REGISTERED_FOR_CLASS = 7032;
    public static final int NO_SESSION_REGISTERED_FOR_NAME = 7033;
    public static final int CANNOT_ADD_DESCRIPTORS_TO_SESSION = 7034;
    public static final int CANNOT_LOGIN_TO_A_SESSION = 7035;
    public static final int CANNOT_LOGOUT_OF_A_SESSION = 7036;
    public static final int CANNOT_MODIFY_SCHEMA_IN_SESSION = 7037;
    public static final int LOG_IO_ERROR = 7038;
    public static final int CANNOT_REMOVE_FROM_READ_ONLY_CLASSES_IN_NESTED_UNIT_OF_WORK = 7039;
    public static final int CANNOT_MODIFY_READ_ONLY_CLASSES_SET_AFTER_USING_UNIT_OF_WORK = 7040;
    public static final int INVALID_READ_ONLY_CLASS_STRUCTURE_IN_UNIT_OF_WORK = 7041;
    public static final int PLATFORM_CLASS_NOT_FOUND = 7042;
    public static final int NO_TABLES_TO_CREATE = 7043;
    public static final int ILLEGAL_CONTAINER_CLASS = 7044;
    public static final int CONTAINER_POLICY_DOES_NOT_USE_KEYS = 7047;
    public static final int MAP_KEY_NOT_DECLARED_IN_ITEM_CLASS = 7048;
    public static final int MISSING_MAPPING = 7051;
    public static final int ILLEGAL_USE_OF_MAP_IN_DIRECTCOLLECTION = 7052;
    public static final int CANNOT_RELEASE_NON_CLIENTSESSION = 7053;
    public static final int CANNOT_ACQUIRE_CLIENTSESSION_FROM_SESSION = 7054;
    public static final int OPTIMISTIC_LOCKING_NOT_SUPPORTED = 7055;
    public static final int WRONG_OBJECT_REGISTERED = 7056;
    public static final int KEYS_MUST_MATCH = 7057;
    public static final int INVALID_CONNECTOR = 7058;
    public static final int INVALID_DATA_SOURCE_NAME = 7059;
    public static final int CANNOT_ACQUIRE_DATA_SOURCE = 7060;
    public static final int JTS_EXCEPTION_RAISED = 7061;
    public static final int FIELD_LEVEL_LOCKING_NOTSUPPORTED_OUTSIDE_A_UNIT_OF_WORK = 7062;
    public static final int EJB_CONTAINER_EXCEPTION_RAISED = 7063;
    public static final int EJB_PRIMARY_KEY_REFLECTION_EXCEPTION = 7064;
    public static final int EJB_CANNOT_LOAD_REMOTE_CLASS = 7065;
    public static final int EJB_MUST_BE_IN_TRANSACTION = 7066;
    public static final int EJB_INVALID_PROJECT_CLASS = 7068;
    public static final int PROJECT_AMENDMENT_EXCEPTION_OCCURED = 7069;
    public static final int EJB_TOPLINK_PROPERTIES_NOT_FOUND = 7070;
    public static final int CANT_HAVE_UNBOUND_IN_OUTPUT_ARGUMENTS = 7071;
    public static final int EJB_INVALID_PLATFORM_CLASS = 7072;
    public static final int ORACLE_OBJECT_TYPE_NOT_DEFINED = 7073;
    public static final int ORACLE_OBJECT_TYPE_NAME_NOT_DEFINED = 7074;
    public static final int ORACLE_VARRAY_MAXIMIM_SIZE_NOT_DEFINED = 7075;
    public static final int DESCRIPTOR_MUST_NOT_BE_INITIALIZED = 7076;
    public static final int EJB_INVALID_FINDER_ON_HOME = 7077;
    public static final int EJB_NO_SUCH_SESSION_SPECIFIED_IN_PROPERTIES = 7078;
    public static final int EJB_DESCRIPTOR_NOT_FOUND_IN_SESSION = 7079;
    public static final int EJB_FINDER_EXCEPTION = 7080;
    public static final int CANNOT_REGISTER_AGGREGATE_OBJECT_IN_UNIT_OF_WORK = 7081;
    public static final int MULTIPLE_PROJECTS_SPECIFIED_IN_PROPERTIES = 7082;
    public static final int NO_PROJECT_SPECIFIED_IN_PROPERTIES = 7083;
    public final static int INVALID_FILE_TYPE = 7084;
    public final static int SUB_SESSION_NOT_DEFINED_FOR_BROKER = 7085;
    public final static int EJB_INVALID_SESSION_TYPE_CLASS = 7086;
    public final static int EJB_SESSION_TYPE_CLASS_NOT_FOUND = 7087;
    public final static int CANNOT_CREATE_EXTERNAL_TRANSACTION_CONTROLLER = 7088;
    public final static int SESSION_AMENDMENT_EXCEPTION_OCCURED = 7089;
    public final static int SET_LISTENER_CLASSES_EXCEPTION = 7091;
    public final static int EXISTING_QUERY_TYPE_CONFLICT = 7092;
    public final static int QUERY_ARGUMENT_TYPE_NOT_FOUND = 7093;
    public final static int ERROR_IN_SESSION_XML = 7094;
    public final static int NO_SESSIONS_XML_FOUND = 7095;
    public final static int CANNOT_COMMIT_UOW_AGAIN = 7096;
    public static final int OPERATION_NOT_SUPPORTED = 7097;
    public static final int PROJECT_XML_NOT_FOUND = 7099;
    public static final int NO_SESSION_FOUND = 7100;
    public static final int NO_TOPLINK_EJB_JAR_XML_FOUND = 7101;
    public static final int NULL_CACHE_KEY_FOUND_ON_REMOVAL = 7102;
    public static final int NULL_UNDERLYING_VALUEHOLDER_VALUE = 7103;
    public static final int INVALID_SEQUENCING_LOGIN = 7104;

    // Security error codes
    public static final int INVALID_ENCRYPTION_CLASS = 7105;
    public static final int ERROR_ENCRYPTING_PASSWORD = 7106;
    public static final int ERROR_DECRYPTING_PASSWORD = 7107;
    public static final int NOT_SUPPORTED_FOR_DATASOURCE = 7108;
    public static final int PROJECT_LOGIN_IS_NULL = 7109;

    // for flashback:
    public static final int HISTORICAL_SESSION_ONLY_SUPPORTED_ON_ORACLE = 7110;
    public static final int CANNOT_ACQUIRE_HISTORICAL_SESSION = 7111;
    public static final int FEATURE_NOT_SUPPORTED_IN_JDK_VERSION = 7112;
    public static final int PLATFORM_DOES_NOT_SUPPORT_CALL_WITH_RETURNING = 7113;
    public static final int ISOLATED_DATA_NOT_SUPPORTED_IN_CLIENTSESSIONBROKER = 7114;
    public static final int CLIENT_SESSION_CANNOT_USE_EXCLUSIVE_CONNECTION = 7115;

    // general validation method's arguments
    public static final int INVALID_METHOD_ARGUMENTS = 7116;

    // customSQL and stored functiuons
    public static final int MULTIPLE_CURSORS_NOT_SUPPORTED = 7117;
    public static final int WRONG_USAGE_OF_SET_CUSTOM_SQL_ARGUMENT_TYPE_METOD = 7118;
    public static final int CANNOT_TRANSLATE_UNPREPARED_CALL = 7119;
    public static final int CANNOT_SET_CURSOR_FOR_PARAMETER_TYPE_OTHER_THAN_OUT = 7120;
    public static final int PLATFORM_DOES_NOT_SUPPORT_STORED_FUNCTIONS = 7121;
    public static final int EXCLUSIVE_CONNECTION_NO_LONGER_AVAILABLE = 7122;

    // From UnitOfWork writeChanges() feature
    public static final int UNIT_OF_WORK_IN_TRANSACTION_COMMIT_PENDING = 7123;
    public static final int UNIT_OF_WORK_AFTER_WRITE_CHANGES_FAILED = 7124;
    public static final int INACTIVE_UNIT_OF_WORK = 7125;
    public static final int CANNOT_WRITE_CHANGES_ON_NESTED_UNIT_OF_WORK = 7126;
    public static final int CANNOT_WRITE_CHANGES_TWICE = 7127;
    public static final int ALREADY_LOGGED_IN = 7128;

    // general validation method's arguments
    public static final int INVALID_NULL_METHOD_ARGUMENTS = 7129;
    public static final int NESTED_UOW_NOT_SUPPORTED_FOR_ATTRIBUTE_TRACKING = 7130;
    public static final int WRONG_COLLECTION_CHANGE_EVENT_TYPE = 7131;
    public static final int WRONG_CHANGE_EVENT = 7132;
    public static final int OLD_COMMIT_NOT_SUPPORTED_FOR_ATTRIBUTE_TRACKING = 7133;

    //ServerPlatform exceptions
    public static final int SERVER_PLATFORM_IS_READ_ONLY_AFTER_LOGIN = 7134;
    public static final int CANNOT_COMMIT_AND_RESUME_UOW_WITH_MODIFY_ALL_QUERIES = 7135;
    public static final int NESTED_UOW_NOT_SUPPORTED_FOR_MODIFY_ALL_QUERY = 7136;

    //fetch group
    public final static int UNFETCHED_ATTRIBUTE_NOT_EDITABLE = 7137;
    public final static int OBJECT_NEED_IMPL_TRACKER_FOR_FETCH_GROUP_USAGE = 7138;
    public static final int MODIFY_ALL_QUERIES_NOT_SUPPORTED_WITH_OTHER_WRITES = 7139;

    // Multiple sequences exceptions
    public static final int WRONG_SEQUENCE_TYPE = 7140;
    public static final int CANNOT_SET_DEFAULT_SEQUENCE_AS_DEFAULT = 7141;
    public static final int DEFAULT_SEQUENCE_NAME_ALREADY_USED_BY_SEQUENCE = 7142;
    public static final int SEQUENCE_NAME_ALREADY_USED_BY_DEFAULT_SEQUENCE = 7143;
    public static final int PLATFORM_DOES_NOT_SUPPORT_SEQUENCE = 7144;
    public static final int SEQUENCE_CANNOT_BE_CONNECTED_TO_TWO_PLATFORMS = 7145;
    public static final int QUERY_SEQUENCE_DOES_NOT_HAVE_SELECT_QUERY = 7146;
    public static final int CREATE_PLATFORM_DEFAULT_SEQUENCE_UNDEFINED = 7147;
    public static final int CANNOT_RESUME_SYNCHRONIZED_UOW = 7148;

    // EJB annotation processing validation exceptions
    public static final int INVALID_COMPOSITE_PK_ATTRIBUTE = 7149;
    public static final int INVALID_COMPOSITE_PK_SPECIFICATION = 7150;
    public static final int INVALID_TYPE_FOR_ENUMERATED_ATTRIBUTE = 7151;
    public static final int MAPPING_ANNOTATIONS_APPLIED_TO_TRANSIENT_ATTRIBUTE = 7153;
    public static final int NO_MAPPED_BY_ATTRIBUTE_FOUND = 7154;
    public static final int INVALID_TYPE_FOR_SERIALIZED_ATTRIBUTE = 7155;
    public static final int UNABLE_TO_LOAD_CLASS = 7156;
    public static final int INVALID_COLUMN_ANNOTATION_ON_RELATIONSHIP = 7157;
    public static final int ERROR_PROCESSING_NAMED_QUERY = 7158;
    public static final int COULD_NOT_FIND_MAP_KEY = 7159;
    public static final int UNI_DIRECTIONAL_ONE_TO_MANY_HAS_JOINCOLUMN_ANNOTATIONS = 7160;
    public static final int NO_PK_ANNOTATIONS_FOUND = 7161;
    public static final int MULTIPLE_EMBEDDED_ID_ANNOTATIONS_FOUND = 7162;
    public static final int EMBEDDED_ID_AND_ID_ANNOTATIONS_FOUND = 7163;
    public static final int INVALID_TYPE_FOR_LOB_ATTRIBUTE = 7164;
    public static final int INVALID_TYPE_FOR_TEMPORAL_ATTRIBUTE = 7165;
    public static final int TABLE_GENERATOR_RESERVED_NAME = 7166;
    public static final int SEQUENCE_GENERATOR_RESERVED_NAME = 7167;
    public static final int INVALID_TYPE_FOR_VERSION_ATTRIBUTE = 7168;
    public static final int ONLY_ONE_GENERATED_VALURE_IS_ALLOWED = 7169;
    public static final int ERROR_INSTANTIATING_CLASS = 7172;

    // Change Tracking
    public static final int WRONG_PROPERTY_NAME_IN_CHANGE_EVENT = 7173;

    // Added for BUG 4349991
    public static final int NO_CORRESPONDING_SETTER_METHOD_DEFINED = 7174;

    // Added for Cascaded Optimistic Locking support
    public static final int UNSUPPORTED_CASCADE_LOCKING_MAPPING = 7175;
    public static final int UNSUPPORTED_CASCADE_LOCKING_MAPPING_WITH_CUSTOM_QUERY = 7176;
    public static final int UNSUPPORTED_CASCADE_LOCKING_DESCRIPTOR = 7177;

    // Added for Proxy Authentication
    public static final int ORACLEOCIPROXYCONNECTOR_REQUIRES_ORACLEOCICONNECTIONPOOL = 7178;
    public static final int ORACLEJDBC10_1_0_2PROXYCONNECTOR_REQUIRES_ORACLECONNECTION = 7179;
    public static final int ORACLEJDBC10_1_0_2PROXYCONNECTOR_REQUIRES_ORACLECONNECTION_VERSION = 7180;
    public static final int ORACLEJDBC10_1_0_2PROXYCONNECTOR_REQUIRES_INT_PROXYTYPE = 7181;

    // JPA
    public static final int COULD_NOT_FIND_DRIVER_CLASS = 7182;
    public static final int ERROR_CLOSING_PERSISTENCE_XML = 7183;
    public static final int CONFIG_FACTORY_NAME_PROPERTY_NOT_SPECIFIED = 7184;
    public static final int CONFIG_FACTORY_NAME_PROPERTY_NOT_FOUND = 7185;
    public static final int CANNOT_INVOKE_METHOD_ON_CONFIG_CLASS = 7186;
    public static final int CONFIG_METHOD_NOT_DEFINED = 7187;
    public static final int CLASS_LIST_MUST_NOT_BE_NULL = 7188;
    public static final int CURRENT_LOADER_NOT_VALID = 7189;
    public static final int METHOD_FAILED = 7190;
    public static final int ENTITY_CLASS_NOT_FOUND = 7191;
    public static final int CLASS_FILE_TRANSFORMER_THROWS_EXCEPTION = 7192;
    public static final int JAR_FILES_IN_PERSISTENCE_XML_NOT_SUPPORTED = 7193;
    public static final int COULD_NOT_BIND_JNDI = 7194;
    public static final int EXCEPTION_CONFIGURING_EM_FACTORY = 7195;
    public static final int NULL_PK_IN_UOW_CLONE = 7197;
    public static final int CANNOT_CAST_TO_CLASS = 7196;
    public static final int CLASS_NOT_FOUND_WHILE_CONVERTING_CLASSNAMES = 7198;
    public static final int PRIMARY_TABLE_NOT_DEFINED_FOR_RELATIONSHIP = 7199;    
    public static final int EMBEDDABLE_ATTRIBUTE_OVERRIDE_NOT_FOUND = 7200;
    public static final int INVALID_ENTITY_MAPPINGS_DOCUMENT = 7201;    
    public static final int INVALID_ATTRIBUTE_OVERRIDE_NAME = 7202;    
    public static final int INVALID_COLLECTION_TYPE_FOR_RELATIONSHIP = 7203;
    // 7204-7206 deleted
    public static final int INVALID_CLASS_TYPE_FOR_BLOB_ATTRIBUTE = 7207;
    public static final int INVALID_CLASS_TYPE_FOR_CLOB_ATTRIBUTE = 7208;
    // 7209-7211 deleted
    public static final int NO_TEMPORAL_TYPE_SPECIFIED = 7212;
    public static final int CIRCULAR_MAPPED_BY_REFERENCES = 7213;
    public static final int UNABLE_TO_DETERMINE_TARGET_ENTITY = 7214;
    public static final int INVALID_FIELD_FOR_CLASS = 7215;
    public static final int INVALID_PROPERTY_FOR_CLASS = 7216;
    public static final int INVALID_ORDER_BY_VALUE = 7217;
    public static final int PLATFORM_DOES_NOT_OVERRIDE_GETCREATETEMPTABLESQLPREFIX = 7218;
    public static final int MAPPING_DOES_NOT_OVERRIDE_VALUEFROMROWINTERNALWITHJOIN = 7219;
    
    // EJB 3.0 JoinColumn(s) and PrimaryKeyJoinColumn(s) validation
    public static final int INCOMPLETE_JOIN_COLUMNS_SPECIFIED = 7220;
    public static final int INCOMPLETE_PRIMARY_KEY_JOIN_COLUMNS_SPECIFIED = 7222;
    public static final int EXCESSIVE_PRIMARY_KEY_JOIN_COLUMNS_SPECIFIED = 7223;
    
    // EJB 3.0 Callback validation
    public static final int INVALID_CALLBACK_METHOD = 7224;
    public static final int INVALID_CALLBACK_METHOD_NAME = 7225;
    public static final int INVALID_CALLBACK_METHOD_MODIFIER = 7226;
    public static final int MULTIPLE_CALLBACK_METHODS_DEFINED = 7227;
    public static final int INVALID_ENTITY_CALLBACK_METHOD_ARGUMENTS = 7228;
    public static final int INVALID_ENTITY_LISTENER_CALLBACK_METHOD_ARGUMENTS = 7229;

    // 7230 deleted
    public static final int CANNOT_PERSIST_MANAGED_OBJECT = 7231;
    public static final int UNSPECIFIED_COMPOSITE_PK_NOT_SUPPORTED = 7232;
    public static final int MAPPING_METADATA_APPLIED_TO_METHOD_WITH_ARGUMENTS = 7233;
    
    public static final int MISSING_FIELD_TYPE_FOR_DDL_GENERATION_OF_CLASS_TRANSFORMATION_ = 7234;
    public static final int MISSING_TRANSFORMER_METHOD_FOR_DDL_GENERATION_OF_CLASS_TRANSFORMATION = 7235;
    // 7236 deleted
    public static final int NON_UNIQUE_ENTITY_NAME = 7237;
    public static final int CONFLICTING_SEQUENCE_AND_TABLE_GENERATORS_SPECIFIED = 7238;
    // 7239 deleted
    public static final int CONFLICTING_SEQUENCE_NAME_AND_TABLE_PK_COLUMN_VALUE_SPECIFIED = 7240;
    // 7241 deleted
    public static final int INSTANTIATING_VALUEHOLDER_WITH_NULL_SESSION = 7242;
    public static final int CLASS_NOT_LISTED_IN_PERSISTENCE_UNIT = 7243;

    public static final int INVALID_MAPPING = 7244;
    public static final int CONFLICTNG_ACCESS_TYPE_FOR_EMBEDDABLE = 7245;
    public static final int INVALID_EMBEDDED_ATTRIBUTE = 7246;
    public static final int DERIVED_ID_CIRCULAR_REFERENCE = 7247;
    // 7248 deleted
    public static final int EMBEDDED_ID_CLASS_HAS_NO_ATTRIBUTES = 7249;
    public static final int NON_ENTITY_AS_TARGET_IN_RELATIONSHIP = 7250;

    public static final int PRIMARY_KEY_UPDATE_DISALLOWED = 7251;

    public static final int NON_UNIQUE_MAPPING_FILE_NAME = 7252;

    public static final int MAPPING_FILE_NOT_FOUND = 7253;
    
    public static final int MULTIPLE_OBJECT_VALUES_FOR_DATA_VALUE = 7254;
    public static final int INVALID_MAPPING_FOR_CONVERTER = 7255;
    public static final int CONVERTER_NOT_FOUND = 7256;
    public static final int ERROR_INSTANTIATING_CONVERSION_VALUE_DATA = 7257;
    public static final int ERROR_INSTANTIATING_CONVERSION_VALUE_OBJECT = 7258;
    public static final int NO_CONVERTER_DATA_TYPE_SPECIFIED = 7259;
    public static final int NO_CONVERTER_OBJECT_TYPE_SPECIFIED = 7260;
    public static final int INVALID_TYPE_FOR_BASIC_COLLECTION_ATTRIBUTE = 7261;
    public static final int INVALID_TYPE_FOR_BASIC_MAP_ATTRIBUTE = 7262;
    public static final int OPTIMISTIC_LOCKING_SELECTED_COLUMN_NAMES_NOT_SPECIFIED = 7263;
    public static final int OPTIMISTIC_LOCKING_VERSION_ELEMENT_NOT_SPECIFIED = 7264;
    public static final int CACHE_NOT_SUPPORTED_WITH_EMBEDDABLE = 7265;
    public static final int CACHE_EXPIRY_AND_EXPIRY_TIME_OF_DAY_BOTH_SPECIFIED = 7266;

    //Extended persistence properties validation
    public static final int INVALID_EXCEPTIONHANDLER_CLASS = 7267;
    public static final int INVALID_SESSIONEVENTLISTENER_CLASS = 7268;
    // 7269 deleted
    public static final int INVALID_CACHESTATEMENTS_SIZE_VALUE = 7270;
    public static final int INVALID_BOOLEAN_VALUE_FOR_SETTING_NATIVESQL = 7271;
    public static final int INVALID_BOOLEAN_VALUE_FOR_ENABLESTATMENTSCACHED = 7272;
    public static final int INVALID_BOOLEAN_VALUE_FOR_ADDINGNAMEDQUERIES = 7273;
    public static final int INVALID_LOGGING_FILE = 7274;
    public static final int CANNOT_INSTANTIATE_EXCEPTIONHANDLER_CLASS = 7275;
    public static final int CANNOT_INSTANTIATE_SESSIONEVENTLISTENER_CLASS = 7276;
    public static final int LOGGING_FILE_NAME_CANNOT_BE_EMPTY = 7277;
    public static final int INVALID_BOOLEAN_VALUE_FOR_PROPERTY = 7278;
    
    // Converters
    // 7279-7281 deleted
    public static final int INVALID_MAPPING_FOR_STRUCT_CONVERTER = 7282;
    public static final int TWO_STRUCT_CONVERTERS_ADDED_FOR_SAME_CLASS = 7283;
    
    public static final int INVALID_COMPARATOR_CLASS = 7284;

    public static final int INVALID_PROFILER_CLASS = 7285;
    public static final int CANNOT_INSTANTIATE_PROFILER_CLASS = 7286;
    
    // JPA Transformation mapping processing
    public static final int READ_TRANSFORMER_CLASS_DOESNT_IMPLEMENT_ATTRIBUTE_TRANSFORMER = 7287;    
    public static final int READ_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD = 7288;
    public static final int READ_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD = 7289;
    public static final int WRITE_TRANSFORMER_CLASS_DOESNT_IMPLEMENT_FIELD_TRANSFORMER = 7290;
    public static final int WRITE_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD = 7291;
    public static final int WRITE_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD = 7292;
    public static final int WRITE_TRANSFORMER_HAS_NO_COLUMN_NAME = 7293;
    public static final int MULTIPLE_CLASSES_FOR_THE_SAME_DISCRIMINATOR = 7294;
    
    // Copy Policy
    public static final int COPY_POLICY_MUST_SPECIFY_METHOD_OR_WORKING_COPY_METHOD = 7295;
    public static final int MULTIPLE_COPY_POLICY_ANNOTATIONS_ON_SAME_CLASS = 7296;   

    public static final int REFLECTIVE_EXCEPTION_WHILE_CREATING_CLASS_INSTANCE = 7297;
    public static final int INVALID_MAPPING_FOR_EMBEDDED_ID = 7298;    
    
    // Generic conflicting annotations and xml metadata
    public static final int CONFLICTING_NAMED_ANNOTATIONS = 7299;
    public static final int CONFLICTING_NAMED_XML_ELEMENTS = 7300;
    public static final int CONFLICTING_ANNOTATIONS = 7301;
    public static final int CONFLICTING_XML_ELEMENTS = 7302;
    
    // ProxyConnectionCustomizer
    public static final int EXPECTED_PROXY_PROPERTY_NOT_FOUND = 7303;
    public static final int UNKNOWN_PROXY_TYPE = 7304;
    
    public static final int ERROR_PARSING_MAPPING_FILE = 7305;
    
    // JPA 2.0 Access type
    public static final int INVALID_EXPLICIT_ACCESS_TYPE = 7306;
    
    // JPA - Internal exception
    public static final int MISSING_CONTEXT_STRING_FOR_CONTEXT = 7307;
    
    public static final int INVALID_VALUE_FOR_PROPERTY = 7308;
    public static final int INVALID_EMBEDDABLE_ATTRIBUTE_FOR_ATTRIBUTE_OVERRIDE = 7309;
    public static final int UNABLE_TO_DETERMINE_TARGET_CLASS = 7310;
    public static final int INVALID_TARGET_CLASS = 7311;
    public static final int INVALID_EMBEDDABLE_CLASS_FOR_ELEMENT_COLLECTION = 7312;
    public static final int EMBEDDABLE_ASSOCIATION_OVERRIDE_NOT_FOUND = 7313;
    public static final int MAP_KEY_CANNOT_USE_INDIRECTION = 7314;
    public static final int UNABLE_TO_DETERMINE_MAP_KEY_CLASS = 7315;
    public static final int INVALID_MAPPED_BY_ID_VALUE = 7316;
    public static final int LIST_ORDER_FIELD_NOT_SUPPORTED = 7317;
    public static final int COLLECTION_REMOVE_EVENT_WITH_NO_INDEX = 7318;
    
    public static final int INVALID_EMBEDDABLE_ATTRIBUTE_FOR_ASSOCIATION_OVERRIDE = 7319;
    public static final int INVALID_ATTRIBUTE_TYPE_FOR_ORDER_COLUMN = 7320;
    
    public static final int INVALID_DERIVED_ID_PRIMARY_KEY_FIELD = 7321;
    
    public static final int INVALID_ASSOCIATION_OVERRIDE_REFERENCE_COLUMN_NAME = 7322;
    public static final int MULTIPLE_UNIQUE_CONSTRAINTS_WITH_SAME_NAME_SPECIFIED = 7323;
    
    public static final int CLASS_EXTRACTOR_CAN_NOT_BE_SPECIFIED_WITH_DISCRIMINATOR = 7324;
    public static final int INVALID_SQL_RESULT_SET_MAPPING_NAME = 7325;
    
    // JPA dynamic persistence exceptions.
    public static final int NO_ATTRIBUTE_TYPE_SPECIFICATION = 7326;
    public static final int CONFLICTNG_ACCESS_METHODS_FOR_EMBEDDABLE = 7327;
    public static final int INVALID_CLASS_LOADER_FOR_DYNAMIC_PERSISTENCE = 7328;
    
    // fetch groups
    public static final int FETCH_GROUP_HAS_UNMAPPED_ATTRIBUTE = 7329;
    public static final int FETCH_GROUP_HAS_WRONG_REFERENCE_ATTRIBUTE = 7330;
    public static final int FETCH_GROUP_HAS_WRONG_REFERENCE_CLASS = 7331;
    
    public static final int INVALID_DERIVED_COMPOSITE_PK_ATTRIBUTE = 7332;
    
    public static final int PRIMARY_KEY_COLUMN_NAME_NOT_SPECIFIED = 7334;
    
    public static final int DUPLICATE_PARTITION_VALUE = 7335;
    
    public static final int MULTIPLE_CONTEXT_PROPERTY_FOR_TENANT_DISCRIMINATOR_FIELD = 7336;
    public static final int NON_READ_ONLY_MAPPED_TENANT_DISCRIMINATOR_FIELD = 7337;

    public static final int CANNOT_ADD_SEQUENCES_TO_SESSION_BROKER = 7338;
    public static final int SHARED_DESCRIPTOR_ALIAS = 7339;
    
    // XML Metadata Repository
    public static final int NON_UNIQUE_REPOSITORY_FILE_NAME = 7340;
    public static final int MISSING_XML_FILE_FOR_METADATA_SOURCE = 7341;

    public static final int INVALID_BOOLEAN_VALUE_FOR_SETTING_ALLOW_NATIVESQL_QUERIES = 7342;
    
    public static final int VPD_MULTIPLE_IDENTIFIERS_SPECIFIED = 7343;
    public static final int VPD_NOT_SUPPORTED = 7344;
    
    public static final int MISSING_PROPERTIES_FILE_FOR_METADATA_SOURCE = 7345;
    public static final int MULTITENANT_PROPERTY_FOR_NON_SHARED_EMF_NOT_SPECIFIED = 7346;
    
    public static final int MISSING_CONVERT_ATTRIBUTE_NAME = 7347;
    public static final int MISSING_MAPPING_CONVERT_ATTRIBUTE_NAME = 7348;
    public static final int EMBEDDABLE_ATTRIBUTE_NAME_FOR_CONVERT_NOT_FOUND = 7350;
    public static final int CONVERTER_CLASS_NOT_FOUND = 7351;
    public static final int CONVERTER_CLASS_MUST_IMPLEMENT_ATTRIBUTE_CONVERTER_INTERFACE = 7352;
    public static final int INVALID_MAPPING_FOR_CONVERT = 7353;
    public static final int INVALID_MAPPING_FOR_MAP_KEY_CONVERT = 7354;
    public static final int INVALID_MAPPING_FOR_CONVERT_WITH_ATTRIBUTE_NAME = 7355;
    public static final int MULTIPLE_OUT_PARAMS_NOT_SUPPORTED = 7356;
  
    /**
     * INTERNAL:
     * EclipseLink exceptions should only be thrown by EclipseLink.
     */
    public ValidationException() {
        super();
    }

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    protected ValidationException(String theMessage) {
        super(theMessage);
    }

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    protected ValidationException(String message, Throwable internalException) {
        super(message, internalException);
    }
    
    public static ValidationException alreadyLoggedIn(String sessionName) {
        Object[] args = { sessionName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ALREADY_LOGGED_IN, args));
        validationException.setErrorCode(ALREADY_LOGGED_IN);
        return validationException;
    }
    
    public static ValidationException cacheExpiryAndExpiryTimeOfDayBothSpecified(Object javaClass) {
        Object[] args = { javaClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CACHE_EXPIRY_AND_EXPIRY_TIME_OF_DAY_BOTH_SPECIFIED, args));
        validationException.setErrorCode(CACHE_EXPIRY_AND_EXPIRY_TIME_OF_DAY_BOTH_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException cacheNotSupportedWithEmbeddable(Object embeddableClass) {
        Object[] args = { embeddableClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CACHE_NOT_SUPPORTED_WITH_EMBEDDABLE, args));
        validationException.setErrorCode(CACHE_NOT_SUPPORTED_WITH_EMBEDDABLE);
        return validationException;
    }
    
    public static ValidationException cannotAcquireClientSessionFromSession() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_ACQUIRE_CLIENTSESSION_FROM_SESSION, args));
        validationException.setErrorCode(CANNOT_ACQUIRE_CLIENTSESSION_FROM_SESSION);
        return validationException;
    }

    public static ValidationException cannotAcquireDataSource(Object name, Exception exception) {
        Object[] args = { name };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_ACQUIRE_DATA_SOURCE, args), exception);
        validationException.setErrorCode(CANNOT_ACQUIRE_DATA_SOURCE);
        return validationException;
    }
    
    // The following is for flashback.
    public static ValidationException cannotAcquireHistoricalSession() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_ACQUIRE_HISTORICAL_SESSION, args));
        validationException.setErrorCode(CANNOT_ACQUIRE_HISTORICAL_SESSION);
        return validationException;
    }
    
    public static ValidationException cannotAddDescriptorsToSessionBroker() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_ADD_DESCRIPTORS_TO_SESSION_BROKER, args));
        validationException.setErrorCode(CANNOT_ADD_DESCRIPTORS_TO_SESSION_BROKER);
        return validationException;
    }
    
    public static ValidationException cannotCastToClass(Object ob, Object objectClass, Object castClass) {
        Object[] args = { ob, objectClass, castClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_CAST_TO_CLASS, args));
        validationException.setErrorCode(CANNOT_CAST_TO_CLASS);
        return validationException;
    }
    
    public static ValidationException cannotCommitAndResumeSynchronizedUOW(UnitOfWorkImpl uow) {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_RESUME_SYNCHRONIZED_UOW, args));
        validationException.setErrorCode(CANNOT_RESUME_SYNCHRONIZED_UOW);
        validationException.setSession(uow);
        return validationException;
    }
    
    public static ValidationException cannotCommitAndResumeUOWWithModifyAllQueries() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_COMMIT_AND_RESUME_UOW_WITH_MODIFY_ALL_QUERIES, args));
        validationException.setErrorCode(CANNOT_COMMIT_AND_RESUME_UOW_WITH_MODIFY_ALL_QUERIES);
        return validationException;
    }
    
    public static ValidationException cannotCommitUOWAgain() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_COMMIT_UOW_AGAIN, args));
        validationException.setErrorCode(CANNOT_COMMIT_UOW_AGAIN);
        return validationException;
    }
    
    public static ValidationException cannotCreateExternalTransactionController(String externalTransactionControllerName) {
        Object[] args = { externalTransactionControllerName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_CREATE_EXTERNAL_TRANSACTION_CONTROLLER, args));
        validationException.setErrorCode(CANNOT_CREATE_EXTERNAL_TRANSACTION_CONTROLLER);
        return validationException;
    }
    
    public static ValidationException notSupportedForDatasource() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NOT_SUPPORTED_FOR_DATASOURCE, args));
        validationException.setErrorCode(NOT_SUPPORTED_FOR_DATASOURCE);
        return validationException;
    }

    public static ValidationException cannotSetListenerClasses(Exception exception) {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, SET_LISTENER_CLASSES_EXCEPTION, args), exception);
        validationException.setErrorCode(SET_LISTENER_CLASSES_EXCEPTION);
        return validationException;
    }

    public static ValidationException cannotHaveUnboundInOutputArguments() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANT_HAVE_UNBOUND_IN_OUTPUT_ARGUMENTS, args));
        validationException.setErrorCode(CANT_HAVE_UNBOUND_IN_OUTPUT_ARGUMENTS);
        return validationException;
    }

    public static ValidationException cannotModifyReadOnlyClassesSetAfterUsingUnitOfWork() {
        Object[] args = { CR };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_MODIFY_READ_ONLY_CLASSES_SET_AFTER_USING_UNIT_OF_WORK, args));
        validationException.setErrorCode(CANNOT_MODIFY_READ_ONLY_CLASSES_SET_AFTER_USING_UNIT_OF_WORK);
        return validationException;
    }

    public static ValidationException cannotRegisterAggregateObjectInUnitOfWork(Object type) {
        Object[] args = { type };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_REGISTER_AGGREGATE_OBJECT_IN_UNIT_OF_WORK, args));
        validationException.setErrorCode(CANNOT_REGISTER_AGGREGATE_OBJECT_IN_UNIT_OF_WORK);
        return validationException;
    }

    public static ValidationException cannotReleaseNonClientSession() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_RELEASE_NON_CLIENTSESSION, args));
        validationException.setErrorCode(CANNOT_RELEASE_NON_CLIENTSESSION);
        return validationException;
    }

    public static ValidationException cannotRemoveFromReadOnlyClassesInNestedUnitOfWork() {
        Object[] args = { CR };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_REMOVE_FROM_READ_ONLY_CLASSES_IN_NESTED_UNIT_OF_WORK, args));
        validationException.setErrorCode(CANNOT_REMOVE_FROM_READ_ONLY_CLASSES_IN_NESTED_UNIT_OF_WORK);
        return validationException;
    }

    public static ValidationException cannotSetReadPoolSizeAfterLogin() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_SET_READ_POOL_SIZE_AFTER_LOGIN, args));
        validationException.setErrorCode(CANNOT_SET_READ_POOL_SIZE_AFTER_LOGIN);
        return validationException;
    }

    public static ValidationException childDescriptorsDoNotHaveIdentityMap() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CHILD_DESCRIPTORS_DO_NOT_HAVE_IDENTITY_MAP, args));
        validationException.setErrorCode(CHILD_DESCRIPTORS_DO_NOT_HAVE_IDENTITY_MAP);
        return validationException;
    }
    
    public static ValidationException circularMappedByReferences(Object cls1, String attributeName1, Object cls2, String attributeName2) {
        Object[] args = { cls1, attributeName1, cls2, attributeName2  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CIRCULAR_MAPPED_BY_REFERENCES, args));
        validationException.setErrorCode(CIRCULAR_MAPPED_BY_REFERENCES);
        return validationException;
    }

    public static ValidationException clientSessionCanNotUseExclusiveConnection() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CLIENT_SESSION_CANNOT_USE_EXCLUSIVE_CONNECTION, args));
        validationException.setErrorCode(CLIENT_SESSION_CANNOT_USE_EXCLUSIVE_CONNECTION);
        return validationException;
    }

    public static ValidationException containerPolicyDoesNotUseKeys(ContainerPolicy aPolicy, String methodName) {
        Object[] args = { aPolicy.getContainerClass().toString(), methodName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONTAINER_POLICY_DOES_NOT_USE_KEYS, args));
        validationException.setErrorCode(CONTAINER_POLICY_DOES_NOT_USE_KEYS);
        return validationException;
    }
    
    public static ValidationException converterNotFound(Object cls, String converterName, Object element) {
        Object[] args = { cls, converterName, element };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONVERTER_NOT_FOUND, args));
        validationException.setErrorCode(CONVERTER_NOT_FOUND);
        return validationException;
    }

    public static ValidationException descriptorMustBeNotInitialized(ClassDescriptor descriptor) {
        Object[] args = { descriptor, CR };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, DESCRIPTOR_MUST_NOT_BE_INITIALIZED, args));
        validationException.setErrorCode(DESCRIPTOR_MUST_NOT_BE_INITIALIZED);
        return validationException;
    }

    public static ValidationException ejbCannotLoadRemoteClass(Exception exception, Object beanClass, String remoteClass) {
        Object[] args = { beanClass, remoteClass, CR };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_CANNOT_LOAD_REMOTE_CLASS, args), exception);
        validationException.setErrorCode(EJB_CANNOT_LOAD_REMOTE_CLASS);
        return validationException;
    }

    public static ValidationException ejbContainerExceptionRaised(Exception exception) {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_CONTAINER_EXCEPTION_RAISED, args), exception);
        validationException.setErrorCode(EJB_CONTAINER_EXCEPTION_RAISED);
        return validationException;
    }

    /**
    * PUBLIC:
    * Possible cause:  The descriptor listed was not found in the session specified on the deployment descriptor.
    * Action:  Check that the project specified in the sessions.xml file is the desired project.
    */
    public static ValidationException ejbDescriptorNotFoundInSession(Object beanClass, String sessionName) {
        Object[] args = { beanClass, sessionName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_DESCRIPTOR_NOT_FOUND_IN_SESSION, args));
        validationException.setErrorCode(EJB_DESCRIPTOR_NOT_FOUND_IN_SESSION);
        return validationException;
    }

    public static ValidationException ejbFinderException(Object bean, Throwable finderException, Vector primaryKey) {
        Object[] args = { bean, bean.getClass(), primaryKey };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_FINDER_EXCEPTION, args));
        validationException.setErrorCode(EJB_FINDER_EXCEPTION);
        return validationException;
    }

    public static ValidationException ejbInvalidHomeInterfaceClass(Object homeClassName) {
        Object[] args = { homeClassName.toString() };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_INVALID_FINDER_ON_HOME, args));
        validationException.setErrorCode(EJB_INVALID_FINDER_ON_HOME);
        return validationException;
    }

    public static ValidationException ejbInvalidPlatformClass(String platformName, String projectName) {
        Object[] args = { platformName, projectName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_INVALID_PLATFORM_CLASS, args));
        validationException.setErrorCode(EJB_INVALID_PLATFORM_CLASS);
        return validationException;
    }

    /**
    * PUBLIC:
    * Possible cause:  The project class specified in the sessions.xml file for the session specified on the toplink_session_name environment variable can not be found.
    * Action: Check that the project class given in the exception is on the WebSphere dependent classpath.
    */
    public static ValidationException ejbInvalidProjectClass(String projectClassName, String projectName, Throwable aThrowable) {
        Object[] args = { projectClassName, projectName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_INVALID_PROJECT_CLASS, args));
        validationException.setInternalException(aThrowable);
        validationException.setErrorCode(EJB_INVALID_PROJECT_CLASS);
        return validationException;
    }

    /**
    * PUBLIC:
    * Possible cause:  The session type specified in the sessions.xml file for the session specified on the toplink_session_name environment variable is not a valid type.
    * Action: Check the session type must be either DatabaseSession or it's subclasses type (including the user-defined session type, which must be extended from DatabaseSession).
    */
    public static ValidationException ejbInvalidSessionTypeClass(String sessionType, String sessionName) {
        Object[] args = { sessionType, sessionName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_INVALID_SESSION_TYPE_CLASS, args));
        validationException.setErrorCode(EJB_INVALID_SESSION_TYPE_CLASS);
        return validationException;
    }

    /**
    * PUBLIC:
    * Possible cause:  An attempt was made to create or remove a been outside of a transaction.
    * Action:  Ensure that all removing and creating of beans is done within a transaction.
    */
    public static ValidationException ejbMustBeInTransaction(Object bean) {
        Object[] args = { bean, CR };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_MUST_BE_IN_TRANSACTION, args));
        validationException.setErrorCode(EJB_MUST_BE_IN_TRANSACTION);
        return validationException;
    }

    /**
    * PUBLIC:
    * Possible cause:  An incorrect primary key object is being used with a bean.
    * Action: Ensure that you are using the correct primary key object for a bean.
    */
    public static ValidationException ejbPrimaryKeyReflectionException(Exception exception, Object primaryKey, Object bean) {
        Object[] args = { primaryKey, bean, CR };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_PRIMARY_KEY_REFLECTION_EXCEPTION, args), exception);
        validationException.setErrorCode(EJB_PRIMARY_KEY_REFLECTION_EXCEPTION);
        return validationException;
    }

    /**
    * PUBLIC:
    * Possible cause:  The session type specified in the sessions.xml file for the session specified on the toplink_session_name environment variable is not found using the default class loader.
    * Action: Check that the session class given in the exception is on the app server classpath.
    */
    public static ValidationException ejbSessionTypeClassNotFound(String sessionType, String sessionName, Throwable exception) {
        Object[] args = { sessionType, sessionName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EJB_SESSION_TYPE_CLASS_NOT_FOUND, args));
        validationException.setErrorCode(EJB_SESSION_TYPE_CLASS_NOT_FOUND);
        validationException.setInternalException(exception);
        return validationException;
    }

    public static ValidationException errorParsingMappingFile(String mappingFileURL, Exception exception) {
        Object[] args = { mappingFileURL };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ERROR_PARSING_MAPPING_FILE, args), exception);
        validationException.setErrorCode(ERROR_PARSING_MAPPING_FILE);
        return validationException;
    }
    
    public static ValidationException errorProcessingNamedQuery(Object entityClass, String namedQuery, Exception exception) {
        Object[] args = { entityClass, namedQuery };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ERROR_PROCESSING_NAMED_QUERY, args), exception);
        validationException.setErrorCode(ERROR_PROCESSING_NAMED_QUERY);
        return validationException;
    }
    
    /**
    * PUBLIC:
    * The session named "name" could not be found in the Sessions.XML
    */
    public static ValidationException errorInSessionsXML(String translatedExceptions) {
        ValidationException validationException = new ValidationException(translatedExceptions);
        validationException.setErrorCode(ERROR_IN_SESSION_XML);
        return validationException;
    }

    public static ValidationException errorInstantiatingClass(Object cls, Exception exception) {
        Object[] args = { cls };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ERROR_INSTANTIATING_CLASS, args), exception);
        validationException.setErrorCode(ERROR_INSTANTIATING_CLASS);
        return validationException;
    }
    
    public static ValidationException errorInstantiatingConversionValueData(String converterName, String value, Object type, Exception exception) {
        Object[] args = { converterName, value, type };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ERROR_INSTANTIATING_CONVERSION_VALUE_DATA, args), exception);
        validationException.setErrorCode(ERROR_INSTANTIATING_CONVERSION_VALUE_DATA);
        return validationException;
    }
    
    public static ValidationException errorInstantiatingConversionValueObject(String converterName, String value, Object type, Exception exception) {
        Object[] args = { converterName, value, type };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ERROR_INSTANTIATING_CONVERSION_VALUE_OBJECT, args), exception);
        validationException.setErrorCode(ERROR_INSTANTIATING_CONVERSION_VALUE_OBJECT);
        return validationException;
    }

    public static ValidationException noPropertiesFileFound(Exception exception) {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_PROPERTIES_FILE_FOUND, args), exception);
        validationException.setErrorCode(NO_PROPERTIES_FILE_FOUND);
        return validationException;
    }

    public static ValidationException noSessionsXMLFound(String resourceName) {
        Object[] args = { resourceName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_SESSIONS_XML_FOUND, args));
        validationException.setErrorCode(NO_SESSIONS_XML_FOUND);
        return validationException;
    }

    public static ValidationException errorEncryptingPassword(Exception exception) {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ERROR_ENCRYPTING_PASSWORD, args), exception);
        validationException.setErrorCode(ERROR_ENCRYPTING_PASSWORD);
        return validationException;
    }

    public static ValidationException embeddedIdAndIdAnnotationFound(Object entityClass, String embeddedIdAttributeName, String idAttributeName) {
        Object[] args = { entityClass, embeddedIdAttributeName, idAttributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EMBEDDED_ID_AND_ID_ANNOTATIONS_FOUND, args));
        validationException.setErrorCode(EMBEDDED_ID_AND_ID_ANNOTATIONS_FOUND);
        return validationException;
    }

    public static ValidationException errorDecryptingPassword(Exception exception) {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ERROR_DECRYPTING_PASSWORD, args), exception);
        validationException.setErrorCode(ERROR_DECRYPTING_PASSWORD);
        return validationException;
    }

    public static ValidationException invalidEncryptionClass(String className, Throwable throwableError) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_ENCRYPTION_CLASS, args));
        validationException.setErrorCode(INVALID_ENCRYPTION_CLASS);
        validationException.setInternalException(throwableError);
        return validationException;
    }

    public static ValidationException invalidEntityCallbackMethodArguments(Object entityClass, String methodName) {
        Object[] args = { entityClass, methodName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_ENTITY_CALLBACK_METHOD_ARGUMENTS, args));
        validationException.setErrorCode(INVALID_ENTITY_CALLBACK_METHOD_ARGUMENTS);
        return validationException;
    }
    
    public static ValidationException invalidEntityListenerCallbackMethodArguments(Object entityClass, Object parameterClass, Object entityListener, String methodName) {
        Object[] args = { entityClass, parameterClass, entityListener, methodName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_ENTITY_LISTENER_CALLBACK_METHOD_ARGUMENTS, args));
        validationException.setErrorCode(INVALID_ENTITY_LISTENER_CALLBACK_METHOD_ARGUMENTS);
        return validationException;
    }

    public static ValidationException noTopLinkEjbJarXMLFound() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_TOPLINK_EJB_JAR_XML_FOUND, args));
        validationException.setErrorCode(NO_TOPLINK_EJB_JAR_XML_FOUND);
        return validationException;
    }

    public static ValidationException excusiveConnectionIsNoLongerAvailable(DatabaseQuery query, AbstractSession session) {
        Object[] args = { query.getReferenceClass().getName() };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EXCLUSIVE_CONNECTION_NO_LONGER_AVAILABLE, args));
        validationException.setErrorCode(EXCLUSIVE_CONNECTION_NO_LONGER_AVAILABLE);
        validationException.setSession(session);
        return validationException;
    }

    public static ValidationException existingQueryTypeConflict(DatabaseQuery newQuery, DatabaseQuery existingQuery) {
        Object[] args = { newQuery, newQuery.getName(), newQuery.getArgumentTypes(), existingQuery, existingQuery.getName(), existingQuery.getArgumentTypes() };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EXISTING_QUERY_TYPE_CONFLICT, args));
        validationException.setErrorCode(EXISTING_QUERY_TYPE_CONFLICT);
        return validationException;
    }

    public static ValidationException fatalErrorOccurred(Exception exception) {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, FATAL_ERROR_OCCURRED, args), exception);
        validationException.setErrorCode(FATAL_ERROR_OCCURRED);
        return validationException;
    }

    public static ValidationException featureIsNotAvailableInRunningJDKVersion(String feature) {
        Object[] args = { feature, System.getProperty("java.version") };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, FEATURE_NOT_SUPPORTED_IN_JDK_VERSION, args));
        validationException.setErrorCode(FEATURE_NOT_SUPPORTED_IN_JDK_VERSION);
        return validationException;
    }

    public static ValidationException fieldLevelLockingNotSupportedWithoutUnitOfWork() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, FIELD_LEVEL_LOCKING_NOTSUPPORTED_OUTSIDE_A_UNIT_OF_WORK, args));
        validationException.setErrorCode(FIELD_LEVEL_LOCKING_NOTSUPPORTED_OUTSIDE_A_UNIT_OF_WORK);
        return validationException;
    }

    /**
     * PUBLIC:
     * Possible cause:  The order-by value provided does not correspond to an attribute on the target entity.
     * Action: Ensure that an attribute with the same name as the order-by value exists on the target entity.
     */
    public static ValidationException invalidOrderByValue(String referenceAttribute, Object referenceClass, String entityAttribute, Object entityClass) {
        Object[] args = { referenceAttribute, referenceClass, entityAttribute, entityClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_ORDER_BY_VALUE, args));
        validationException.setErrorCode(INVALID_ORDER_BY_VALUE);
        return validationException;
    }

    public static ValidationException fileError(java.io.IOException exception) {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, FILE_ERROR, args), exception);
        validationException.setErrorCode(FILE_ERROR);
        return validationException;
    }
    
    public static ValidationException idRelationshipCircularReference(HashSet processing){
        Object[] args = { processing };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, DERIVED_ID_CIRCULAR_REFERENCE, args));
        validationException.setErrorCode(DERIVED_ID_CIRCULAR_REFERENCE);
        return validationException;
    }

    public static ValidationException illegalContainerClass(Object aClass) {
        Object[] args = { aClass.toString() };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ILLEGAL_CONTAINER_CLASS, args));
        validationException.setErrorCode(ILLEGAL_CONTAINER_CLASS);
        return validationException;
    }

    public static ValidationException illegalUseOfMapInDirectCollection(org.eclipse.persistence.mappings.DirectCollectionMapping directCollectionMapping, Object aMapClass, String keyMethodName) {
        Object[] args = { directCollectionMapping, keyMethodName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ILLEGAL_USE_OF_MAP_IN_DIRECTCOLLECTION, args));
        validationException.setErrorCode(ILLEGAL_USE_OF_MAP_IN_DIRECTCOLLECTION);
        return validationException;
    }

    public static ValidationException incorrectLoginInstanceProvided() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INCORRECT_LOGIN_INSTANCE_PROVIDED, args));
        validationException.setErrorCode(INCORRECT_LOGIN_INSTANCE_PROVIDED);
        return validationException;
    }
    
    public static ValidationException instantiatingValueholderWithNullSession() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INSTANTIATING_VALUEHOLDER_WITH_NULL_SESSION, args));
        validationException.setErrorCode(INSTANTIATING_VALUEHOLDER_WITH_NULL_SESSION);
        return validationException;
    }

    public static ValidationException invalidAssociationOverrideReferenceColumnName(String referenceColumnName, String associationOverrideName, String attributeName, String className) {
        Object[] args = { referenceColumnName, associationOverrideName, attributeName, className };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_ASSOCIATION_OVERRIDE_REFERENCE_COLUMN_NAME, args));
        validationException.setErrorCode(INVALID_ASSOCIATION_OVERRIDE_REFERENCE_COLUMN_NAME);
        return validationException;
    }

    /**
     * PUBLIC:
     * Possible cause:  A mapping for the attribute name specified in the attribute-override cannot be found 
     * in the descriptor for the embeddable class.
     * Action:  Ensure that there is an attribute on the embeddable class matching the attribute name in the 
     * attribute-override declaration.
     */
    public static ValidationException invalidAttributeOverrideName(String columnName, Object embeddableClass) {
        Object[] args = { columnName, embeddableClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_ATTRIBUTE_OVERRIDE_NAME, args));
        validationException.setErrorCode(INVALID_ATTRIBUTE_OVERRIDE_NAME);
        return validationException;
    }

    /**
     * PUBLIC:
     * An order column can only be applied to an attribute of type List.
     */
    public static ValidationException invalidAttributeTypeForOrderColumn(String attributeName, Object cls) {
        Object[] args = { attributeName, cls };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_ATTRIBUTE_TYPE_FOR_ORDER_COLUMN, args));
        validationException.setErrorCode(INVALID_ATTRIBUTE_TYPE_FOR_ORDER_COLUMN);
        return validationException;
    }
    
    public static ValidationException invalidCallbackMethod(Object listenerClass, String methodName) {
        Object[] args = { listenerClass, methodName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_CALLBACK_METHOD, args));
        validationException.setErrorCode(INVALID_CALLBACK_METHOD);
        return validationException;
    }
    
    public static ValidationException invalidCallbackMethodModifier(Object listenerClass, String methodName) {
        Object[] args = { listenerClass, methodName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_CALLBACK_METHOD_MODIFIER, args));
        validationException.setErrorCode(INVALID_CALLBACK_METHOD_MODIFIER);
        return validationException;
    }
    
    public static ValidationException invalidCallbackMethodName(Object listenerClass, String methodName) {
        Object[] args = { listenerClass, methodName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_CALLBACK_METHOD_NAME, args));
        validationException.setErrorCode(INVALID_CALLBACK_METHOD_NAME);
        return validationException;
    }

    public static ValidationException invalidClassLoaderForDynamicPersistence() {
        Object[] args = { };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_CLASS_LOADER_FOR_DYNAMIC_PERSISTENCE, args));
        validationException.setErrorCode(INVALID_CLASS_LOADER_FOR_DYNAMIC_PERSISTENCE);
        return validationException;
    }
    
    public static ValidationException invalidClassTypeForBLOBAttribute(Object entityClass, String attributeName) {
        Object[] args = { entityClass, attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_CLASS_TYPE_FOR_BLOB_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_CLASS_TYPE_FOR_BLOB_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException invalidClassTypeForCLOBAttribute(Object entityClass, String attributeName) {
        Object[] args = { entityClass, attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_CLASS_TYPE_FOR_CLOB_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_CLASS_TYPE_FOR_CLOB_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException invalidTypeForBasicCollectionAttribute(String attributeName, Object targetClass, Object entityClass) {
        Object[] args = { attributeName, targetClass, entityClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_TYPE_FOR_BASIC_COLLECTION_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_TYPE_FOR_BASIC_COLLECTION_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException invalidTypeForBasicMapAttribute(String attributeName, Object targetClass, Object entityClass) {
        Object[] args = { attributeName, targetClass, entityClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_TYPE_FOR_BASIC_MAP_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_TYPE_FOR_BASIC_MAP_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException invalidTypeForEnumeratedAttribute(String attributeName, Object targetClass, Object entityClass) {
        Object[] args = { attributeName, targetClass, entityClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_TYPE_FOR_ENUMERATED_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_TYPE_FOR_ENUMERATED_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException invalidTypeForLOBAttribute(String attributeName, Object targetClass, Object entityClass) {
        Object[] args = { attributeName, targetClass, entityClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_TYPE_FOR_LOB_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_TYPE_FOR_LOB_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException invalidTypeForSerializedAttribute(String attributeName, Object targetClass, Object entityClass) {
        Object[] args = { attributeName, targetClass, entityClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_TYPE_FOR_SERIALIZED_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_TYPE_FOR_SERIALIZED_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException invalidTypeForTemporalAttribute(String attributeName, Object targetClass, Object entityClass) {
        Object[] args = { attributeName, targetClass, entityClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_TYPE_FOR_TEMPORAL_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_TYPE_FOR_TEMPORAL_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException invalidTypeForVersionAttribute(String attributeName, Object lockingType, Object entityClass) {
        Object[] args = { attributeName, lockingType, entityClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_TYPE_FOR_VERSION_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_TYPE_FOR_VERSION_ATTRIBUTE);
        return validationException;
    }

    public static ValidationException invalidCollectionTypeForRelationship(Object cls, Object rawClass, Object element) {
        Object[] args = { rawClass, element, cls };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_COLLECTION_TYPE_FOR_RELATIONSHIP, args));
        validationException.setErrorCode(INVALID_COLLECTION_TYPE_FOR_RELATIONSHIP);
        return validationException;
    }
    
    public static ValidationException invalidColumnAnnotationOnRelationship(Object entityClass, String attributeName) {
        Object[] args = { entityClass, attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_COLUMN_ANNOTATION_ON_RELATIONSHIP, args));
        validationException.setErrorCode(INVALID_COLUMN_ANNOTATION_ON_RELATIONSHIP);
        return validationException;
    }

    public static ValidationException invalidCompositePKAttribute(String entityClassName, String pkClassName, String attributeName, Object expectedType, Object actualType) {
        Object[] args = { entityClassName, pkClassName, attributeName, expectedType, actualType };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_COMPOSITE_PK_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_COMPOSITE_PK_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException invalidDerivedCompositePKAttribute(Object entityClass, String pkClassName, String attributeName, Object expectedType, Object actualType) {
        Object[] args = { entityClass, pkClassName, attributeName, expectedType, actualType };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_DERIVED_COMPOSITE_PK_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_DERIVED_COMPOSITE_PK_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException invalidCompositePKSpecification(Object entityClass, String pkClassName) {
        Object[] args = { entityClass, pkClassName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_COMPOSITE_PK_SPECIFICATION, args));
        validationException.setErrorCode(INVALID_COMPOSITE_PK_SPECIFICATION);
        return validationException;
    }
    
    public static ValidationException invalidConnector(org.eclipse.persistence.sessions.Connector connector) {
        Object[] args = { connector };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_CONNECTOR, args));
        validationException.setErrorCode(INVALID_CONNECTOR);
        return validationException;
    }

    public static ValidationException invalidDataSourceName(String name, Exception exception) {
        Object[] args = { name };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_DATA_SOURCE_NAME, args), exception);
        validationException.setErrorCode(INVALID_DATA_SOURCE_NAME);
        return validationException;
    }

    public static ValidationException invalidDerivedIdPrimaryKeyField(String referenceClassName, String primaryKeyFieldName, String attributeName, String cls) {
        Object[] args = { referenceClassName, primaryKeyFieldName, attributeName, cls };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_DERIVED_ID_PRIMARY_KEY_FIELD, args));
        validationException.setErrorCode(INVALID_DERIVED_ID_PRIMARY_KEY_FIELD);
        return validationException;
    }

    public static ValidationException invalidEmbeddableAttributeForAssociationOverride(Object aggregateClass, String aggregateAttributeName, String associationOverrideName, Object location) {
        Object[] args = { aggregateClass, aggregateAttributeName, associationOverrideName, location};

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_EMBEDDABLE_ATTRIBUTE_FOR_ASSOCIATION_OVERRIDE, args));
        validationException.setErrorCode(INVALID_EMBEDDABLE_ATTRIBUTE_FOR_ASSOCIATION_OVERRIDE);
        return validationException;
    }

    
    public static ValidationException invalidEmbeddableAttributeForAttributeOverride(Object aggregateClass, String aggregateAttributeName, Object owningClass, String owningAttributeName) {
        Object[] args = { aggregateClass, aggregateAttributeName,  owningClass, owningAttributeName};

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_EMBEDDABLE_ATTRIBUTE_FOR_ATTRIBUTE_OVERRIDE, args));
        validationException.setErrorCode(INVALID_EMBEDDABLE_ATTRIBUTE_FOR_ATTRIBUTE_OVERRIDE);
        return validationException;
    }
    
    public static ValidationException invalidEmbeddableClassForElementCollection(Object embeddableClass, String attributeName, Object owningClass, String embeddableClassAttributeName) {
        Object[] args = { embeddableClass, attributeName, owningClass, embeddableClassAttributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_EMBEDDABLE_CLASS_FOR_ELEMENT_COLLECTION, args));
        validationException.setErrorCode(INVALID_EMBEDDABLE_CLASS_FOR_ELEMENT_COLLECTION);
        return validationException;
    }
    
    public static ValidationException embeddableAssociationOverrideNotFound(Object aggregateClass, String aggregateAttributeName, Object owningClass, String owningAttributeName) {
        Object[] args = { aggregateClass, aggregateAttributeName,  owningClass, owningAttributeName};

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EMBEDDABLE_ASSOCIATION_OVERRIDE_NOT_FOUND, args));
        validationException.setErrorCode(EMBEDDABLE_ASSOCIATION_OVERRIDE_NOT_FOUND);
        return validationException;
    }
    
    public static ValidationException embeddableAttributeOverrideNotFound(Object aggregateClass, String aggregateAttributeName, Object owningClass, String owningAttributeName) {
        Object[] args = { aggregateClass, aggregateAttributeName,  owningClass, owningAttributeName};

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EMBEDDABLE_ATTRIBUTE_OVERRIDE_NOT_FOUND, args));
        validationException.setErrorCode(EMBEDDABLE_ATTRIBUTE_OVERRIDE_NOT_FOUND);
        return validationException;
    }
    
    /**
     * PUBLIC:
     * Possible cause:  Either the URL for the entity-mappings document is invalid, or there is an error in the document.
     * Action: Verify that the URL is correct.  If so, analyze the exception message for an indication of what is wrong withthe document.
     */
    public static ValidationException invalidEntityMappingsDocument(String fileName, Exception exception) {
        Object[] args = { fileName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_ENTITY_MAPPINGS_DOCUMENT, args), exception);
        validationException.setErrorCode(INVALID_ENTITY_MAPPINGS_DOCUMENT);
        return validationException;
    }
    
    public static ValidationException invalidFileName(String fileName) {
        Object[] args = { fileName };

        ValidationException exception = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_FILE_TYPE, args));
        exception.setErrorCode(INVALID_FILE_TYPE);
        return exception;
    }
    
    public static ValidationException invalidMappedByIdValue(String mappedByIdValue, String attribute, Object idClass) {
        Object[] args = { mappedByIdValue, attribute, idClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_MAPPED_BY_ID_VALUE, args));
        validationException.setErrorCode(INVALID_MAPPED_BY_ID_VALUE);
        return validationException;
    }
    
    public static ValidationException invalidMapping(Object entityClass, Object targetClass) {
        Object[] args = { entityClass, targetClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_MAPPING, args));
        validationException.setErrorCode(INVALID_MAPPING);
        return validationException;
    }
    
    public static ValidationException invalidMappingForConverter(Object entityClass, Object annotatedElement) {
        Object[] args = { entityClass, annotatedElement };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_MAPPING_FOR_CONVERTER, args));
        validationException.setErrorCode(INVALID_MAPPING_FOR_CONVERTER);
        return validationException;
    }

    public static ValidationException invalidMappingForEmbeddedId(String sourceAttributeName, Object sourceClass, String embeddedAttributeName, Object embeddedIdClass) {
        Object[] args = { sourceAttributeName, sourceClass, embeddedAttributeName, embeddedIdClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_MAPPING_FOR_EMBEDDED_ID, args));
        validationException.setErrorCode(INVALID_MAPPING_FOR_EMBEDDED_ID);
        return validationException;
    }
    
    public static ValidationException invalidMergePolicy() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_MERGE_POLICY, args));
        validationException.setErrorCode(INVALID_MERGE_POLICY);
        return validationException;
    }

    public static ValidationException javaTypeIsNotAValidDatabaseType(Object javaClass) {
        Object[] args = { javaClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, JAVA_TYPE_IS_NOT_A_VALID_DATABASE_TYPE, args));
        validationException.setErrorCode(JAVA_TYPE_IS_NOT_A_VALID_DATABASE_TYPE);
        return validationException;
    }

    public static ValidationException jtsExceptionRaised(Exception exception) {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, JTS_EXCEPTION_RAISED, args), exception);
        validationException.setErrorCode(JTS_EXCEPTION_RAISED);
        return validationException;
    }

    public static ValidationException loginBeforeAllocatingClientSessions() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, LOGIN_BEFORE_ALLOCATING_CLIENT_SESSIONS, args));
        validationException.setErrorCode(LOGIN_BEFORE_ALLOCATING_CLIENT_SESSIONS);
        return validationException;
    }

    public static ValidationException logIOError(java.io.IOException exception) {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, LOG_IO_ERROR, args), exception);
        validationException.setErrorCode(LOG_IO_ERROR);
        return validationException;
    }
    
    public static ValidationException mapKeyNotDeclaredInItemClass(String keyName, Object aClass) {
        Object[] args = { keyName, aClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MAP_KEY_NOT_DECLARED_IN_ITEM_CLASS, args));
        validationException.setErrorCode(MAP_KEY_NOT_DECLARED_IN_ITEM_CLASS);
        return validationException;
    }

    public static ValidationException maxSizeLessThanMinSize() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MAX_SIZE_LESS_THAN_MIN_SIZE, args));
        validationException.setErrorCode(MAX_SIZE_LESS_THAN_MIN_SIZE);
        return validationException;
    }

    public static ValidationException noMappedByAttributeFound(Object ownerClass, String mappedByAttributeName, Object entityClass, String attributeName) {
        Object[] args = { ownerClass, mappedByAttributeName, entityClass, attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_MAPPED_BY_ATTRIBUTE_FOUND, args));
        validationException.setErrorCode(NO_MAPPED_BY_ATTRIBUTE_FOUND);
        return validationException;
    }
    
    public static ValidationException nonEntityTargetInRelationship(Object javaClass, Object targetEntity, Object annotatedElement) {
        Object[] args = {javaClass, targetEntity, annotatedElement};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NON_ENTITY_AS_TARGET_IN_RELATIONSHIP, args));
        validationException.setErrorCode(NON_ENTITY_AS_TARGET_IN_RELATIONSHIP);
        return validationException;
    }
    
    public static ValidationException nonUniqueEntityName(String clsName1, String clsName2, String name) {
        Object[] args = { name, clsName1, clsName2, CR };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NON_UNIQUE_ENTITY_NAME, args));
        validationException.setErrorCode(NON_UNIQUE_ENTITY_NAME);
        return validationException;
    }

    public static ValidationException nonUniqueMappingFileName(String puName, String mf) {
        Object[] args = {puName, mf};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NON_UNIQUE_MAPPING_FILE_NAME, args));
        validationException.setErrorCode(NON_UNIQUE_MAPPING_FILE_NAME);
        return validationException;
    }

    public static ValidationException nonUniqueRepositoryFileName(String fileName) {
        Object[] args = {fileName};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NON_UNIQUE_REPOSITORY_FILE_NAME, args));
        validationException.setErrorCode(NON_UNIQUE_REPOSITORY_FILE_NAME);
        return validationException;
    }
    
    public static ValidationException missingXMLMetadataRepositoryConfig(){
        Object[] args = {};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MISSING_XML_FILE_FOR_METADATA_SOURCE, args));
        validationException.setErrorCode(MISSING_XML_FILE_FOR_METADATA_SOURCE);
        return validationException;
    }
    
    public static ValidationException missingPropertiesFileForMetadataRepositoryConfig(String fileName){
        Object[] args = {fileName};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MISSING_PROPERTIES_FILE_FOR_METADATA_SOURCE, args));
        validationException.setErrorCode(MISSING_PROPERTIES_FILE_FOR_METADATA_SOURCE);
        return validationException;
    }
    
    public static ValidationException noPrimaryKeyAnnotationsFound(Object entityClass) {
        Object[] args = { entityClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_PK_ANNOTATIONS_FOUND, args));
        validationException.setErrorCode(NO_PK_ANNOTATIONS_FOUND);
        return validationException;
    }

    public static ValidationException noSessionFound(String sessionName, String resourceName) {
        Object[] args = { sessionName, resourceName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_SESSION_FOUND, args));
        validationException.setErrorCode(NO_SESSION_FOUND);
        return validationException;
    }

    public static ValidationException noSessionRegisteredForClass(Object domainClass) {
        Object[] args = { domainClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_SESSION_REGISTERED_FOR_CLASS, args));
        validationException.setErrorCode(NO_SESSION_REGISTERED_FOR_CLASS);
        return validationException;
    }

    public static ValidationException noSessionRegisteredForName(String sessionName) {
        Object[] args = { sessionName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_SESSION_REGISTERED_FOR_NAME, args));
        validationException.setErrorCode(NO_SESSION_REGISTERED_FOR_NAME);
        return validationException;
    }

    public static ValidationException noTablesToCreate(org.eclipse.persistence.sessions.Project project) {
        Object[] args = { project };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_TABLES_TO_CREATE, args));
        validationException.setErrorCode(NO_TABLES_TO_CREATE);
        return validationException;
    }
    
    public static ValidationException noTemporalTypeSpecified(String attributeName, Object entityClass) {
        Object[] args = { attributeName, entityClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_TEMPORAL_TYPE_SPECIFIED, args));
        validationException.setErrorCode(NO_TEMPORAL_TYPE_SPECIFIED);
        return validationException;
    }


    public static ValidationException nullPrimaryKeyInUnitOfWorkClone(Object clone, Object id) {
        Object[] args = { clone, id };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NULL_PK_IN_UOW_CLONE, args));
        validationException.setErrorCode(NULL_PK_IN_UOW_CLONE);
        return validationException;
    }

    public static ValidationException uniDirectionalOneToManyHasJoinColumnAnnotations(String attributeName, Object entityClass) {
        Object[] args = { entityClass, attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNI_DIRECTIONAL_ONE_TO_MANY_HAS_JOINCOLUMN_ANNOTATIONS, args));
        validationException.setErrorCode(UNI_DIRECTIONAL_ONE_TO_MANY_HAS_JOINCOLUMN_ANNOTATIONS);
        return validationException;
    }
    
    public static ValidationException onlyFieldsAreValidKeysForDatabaseRows() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ONLY_FIELDS_ARE_VALID_KEYS_FOR_DATABASE_ROWS, args));
        validationException.setErrorCode(ONLY_FIELDS_ARE_VALID_KEYS_FOR_DATABASE_ROWS);
        return validationException;
    }

    public static ValidationException operationNotSupported(String methodName) {
        Object[] args = { methodName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, OPERATION_NOT_SUPPORTED, args));
        validationException.setErrorCode(OPERATION_NOT_SUPPORTED);
        return validationException;
    }

    public static ValidationException optimisticLockingNotSupportedWithStoredProcedureGeneration() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, OPTIMISTIC_LOCKING_NOT_SUPPORTED, args));
        validationException.setErrorCode(OPTIMISTIC_LOCKING_NOT_SUPPORTED);
        return validationException;
    }
    
    public static ValidationException optimisticLockingSelectedColumnNamesNotSpecified(Object entityClass) {
        Object[] args = { entityClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, OPTIMISTIC_LOCKING_SELECTED_COLUMN_NAMES_NOT_SPECIFIED, args));
        validationException.setErrorCode(OPTIMISTIC_LOCKING_SELECTED_COLUMN_NAMES_NOT_SPECIFIED);
        return validationException;
    }

    public static ValidationException optimisticLockingVersionElementNotSpecified(Object entityClass) {
        Object[] args = { entityClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, OPTIMISTIC_LOCKING_VERSION_ELEMENT_NOT_SPECIFIED, args));
        validationException.setErrorCode(OPTIMISTIC_LOCKING_VERSION_ELEMENT_NOT_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException oracleObjectTypeIsNotDefined(String typeName) {
        Object[] args = { typeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ORACLE_OBJECT_TYPE_NOT_DEFINED, args));
        validationException.setErrorCode(ORACLE_OBJECT_TYPE_NOT_DEFINED);
        return validationException;
    }

    public static ValidationException oracleObjectTypeNameIsNotDefined(Object type) {
        Object[] args = { type };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ORACLE_OBJECT_TYPE_NAME_NOT_DEFINED, args));
        validationException.setErrorCode(ORACLE_OBJECT_TYPE_NAME_NOT_DEFINED);
        return validationException;
    }

    public static ValidationException oracleVarrayMaximumSizeNotDefined(String typeName) {
        Object[] args = { typeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ORACLE_VARRAY_MAXIMIM_SIZE_NOT_DEFINED, args));
        validationException.setErrorCode(ORACLE_VARRAY_MAXIMIM_SIZE_NOT_DEFINED);
        return validationException;
    }

    public static ValidationException platformClassNotFound(Throwable exception, String className) {
        Object[] args = { className };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, PLATFORM_CLASS_NOT_FOUND, args), exception);
        validationException.setErrorCode(PLATFORM_CLASS_NOT_FOUND);
        return validationException;
    }

    public static ValidationException poolNameDoesNotExist(String poolName) {
        Object[] args = { poolName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, POOL_NAME_DOES_NOT_EXIST, args));
        validationException.setErrorCode(POOL_NAME_DOES_NOT_EXIST);
        return validationException;
    }

    public static ValidationException poolsMustBeConfiguredBeforeLogin() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, POOLS_MUST_BE_CONFIGURED_BEFORE_LOGIN, args));
        validationException.setErrorCode(POOLS_MUST_BE_CONFIGURED_BEFORE_LOGIN);
        return validationException;
    }

    /**
    * PUBLIC:
    * Possible cause:  Instance document is incomplete - primary tables must be 
    * defined for both sides of a relationhip.
    * Action:  Make sure that each entity of a relationship has a primary table defined.
    */
    public static ValidationException primaryTableNotDefined(Object javaClass, String instanceDocName) {
        Object[] args = { javaClass, instanceDocName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, PRIMARY_TABLE_NOT_DEFINED_FOR_RELATIONSHIP, args));
        validationException.setErrorCode(PRIMARY_TABLE_NOT_DEFINED_FOR_RELATIONSHIP);
        return validationException;
    }

    public static ValidationException projectXMLNotFound(String projectXMLFile, Exception exception) {
        Object[] args = { projectXMLFile };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, PROJECT_XML_NOT_FOUND, args));
        validationException.setInternalException(exception);
        validationException.setErrorCode(PROJECT_XML_NOT_FOUND);
        return validationException;
    }

    public static ValidationException queryArgumentTypeNotFound(DatabaseQuery query, String argumentName, String typeAsString, Exception exception) {
        Object[] args = { query.getName(), argumentName, typeAsString };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, QUERY_ARGUMENT_TYPE_NOT_FOUND, args));
        validationException.setInternalException(exception);
        validationException.setErrorCode(QUERY_ARGUMENT_TYPE_NOT_FOUND);
        return validationException;
    }

    public static ValidationException sequenceSetupIncorrectly(String sequenceName) {
        Object[] args = { sequenceName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, SEQUENCE_SETUP_INCORRECTLY, args));
        validationException.setErrorCode(SEQUENCE_SETUP_INCORRECTLY);
        return validationException;
    }

    /**
    * PUBLIC:
    * Possible cause:  An attempt was made to modify the ServerPlatform after login.
    * Action:  All changes to the ServerPlatform must be made before login.
    */
    public static ValidationException serverPlatformIsReadOnlyAfterLogin(String serverPlatformClassName) {
        Object[] args = { serverPlatformClassName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, SERVER_PLATFORM_IS_READ_ONLY_AFTER_LOGIN, args));
        validationException.setErrorCode(SERVER_PLATFORM_IS_READ_ONLY_AFTER_LOGIN);
        return validationException;
    }

    /**
    * PUBLIC:
    * Possible cause:  An amendment method was called but can not be found.
    * Action:  Check that the required amendment method exists on the class specified.
    */
    public static ValidationException sessionAmendmentExceptionOccured(Exception exception, String amendmentMethod, String amendmentClass, Class[] parameters) {
        StringBuffer buf = new StringBuffer(30);
        for (int i = 0; i < (parameters.length - 1); i++) {
            buf.append(parameters[i].getName());
            if (i != (parameters.length - 1)) {
                buf.append(", ");
            }
        }
        buf.append(parameters[parameters.length - 1].getName());

        Object[] args = { amendmentClass, amendmentMethod, buf.toString() };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, SESSION_AMENDMENT_EXCEPTION_OCCURED, args));
        validationException.setInternalException(exception);
        validationException.setErrorCode(SESSION_AMENDMENT_EXCEPTION_OCCURED);
        return validationException;
    }

    public static ValidationException startIndexOutOfRange() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, START_INDEX_OUT_OF_RANGE, args));
        validationException.setErrorCode(START_INDEX_OUT_OF_RANGE);
        return validationException;
    }

    public static ValidationException stopIndexOutOfRange() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, STOP_INDEX_OUT_OF_RANGE, args));
        validationException.setErrorCode(STOP_INDEX_OUT_OF_RANGE);
        return validationException;
    }

    public static ValidationException writeObjectNotAllowedInUnitOfWork() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRITE_OBJECT_NOT_ALLOWED_IN_UNIT_OF_WORK, args));
        validationException.setErrorCode(WRITE_OBJECT_NOT_ALLOWED_IN_UNIT_OF_WORK);
        return validationException;
    }

    public static ValidationException wrongObjectRegistered(Object registered, Object parent) {
        Object[] args = { registered, parent };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRONG_OBJECT_REGISTERED, args));
        validationException.setErrorCode(WRONG_OBJECT_REGISTERED);
        return validationException;
    }
    
    public static ValidationException cannotIssueModifyAllQueryWithOtherWritesWithinUOW() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MODIFY_ALL_QUERIES_NOT_SUPPORTED_WITH_OTHER_WRITES, args));
        validationException.setErrorCode(MODIFY_ALL_QUERIES_NOT_SUPPORTED_WITH_OTHER_WRITES);
        return validationException;
    }

    public static ValidationException nullCacheKeyFoundOnRemoval(IdentityMap map, Object clazz) {
        Object[] args = { map, clazz, CR };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NULL_CACHE_KEY_FOUND_ON_REMOVAL, args));
        validationException.setErrorCode(NULL_CACHE_KEY_FOUND_ON_REMOVAL);
        return validationException;
    }

    public static ValidationException nullUnderlyingValueHolderValue(String methodName) {
        Object[] args = { methodName, CR };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NULL_UNDERLYING_VALUEHOLDER_VALUE, args));
        validationException.setErrorCode(NULL_UNDERLYING_VALUEHOLDER_VALUE);
        return validationException;
    }

    public static ValidationException invalidSequencingLogin() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_SEQUENCING_LOGIN, args));
        validationException.setErrorCode(INVALID_SEQUENCING_LOGIN);
        return validationException;
    }

    public static ValidationException isolatedDataNotSupportedInSessionBroker(String sessionName) {
        Object[] args = { sessionName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ISOLATED_DATA_NOT_SUPPORTED_IN_CLIENTSESSIONBROKER, args));
        validationException.setErrorCode(ISOLATED_DATA_NOT_SUPPORTED_IN_CLIENTSESSIONBROKER);
        return validationException;
    }

    public static ValidationException projectLoginIsNull(AbstractSession session) {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, PROJECT_LOGIN_IS_NULL, args));
        validationException.setErrorCode(PROJECT_LOGIN_IS_NULL);
        return validationException;
    }

    public static ValidationException historicalSessionOnlySupportedOnOracle() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, HISTORICAL_SESSION_ONLY_SUPPORTED_ON_ORACLE, args));
        validationException.setErrorCode(HISTORICAL_SESSION_ONLY_SUPPORTED_ON_ORACLE);
        return validationException;
    }

    public static ValidationException platformDoesNotSupportCallWithReturning(String platformTypeName) {
        Object[] args = { platformTypeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, PLATFORM_DOES_NOT_SUPPORT_CALL_WITH_RETURNING, args));
        validationException.setErrorCode(PLATFORM_DOES_NOT_SUPPORT_CALL_WITH_RETURNING);
        return validationException;
    }

    public static ValidationException invalidNullMethodArguments() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_NULL_METHOD_ARGUMENTS, args));
        validationException.setErrorCode(INVALID_NULL_METHOD_ARGUMENTS);
        return validationException;
    }

    public static ValidationException invalidMethodArguments() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_METHOD_ARGUMENTS, args));
        validationException.setErrorCode(INVALID_METHOD_ARGUMENTS);
        return validationException;
    }

    public static ValidationException wrongUsageOfSetCustomArgumentTypeMethod(String callString) {
        Object[] args = { callString };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRONG_USAGE_OF_SET_CUSTOM_SQL_ARGUMENT_TYPE_METOD, args));
        validationException.setErrorCode(WRONG_USAGE_OF_SET_CUSTOM_SQL_ARGUMENT_TYPE_METOD);
        return validationException;
    }

    public static ValidationException cannotTranslateUnpreparedCall(String callString) {
        Object[] args = { callString };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_TRANSLATE_UNPREPARED_CALL, args));
        validationException.setErrorCode(CANNOT_TRANSLATE_UNPREPARED_CALL);
        return validationException;
    }

    public static ValidationException cannotSetCursorForParameterTypeOtherThanOut(String fieldName, String callString) {
        Object[] args = { fieldName, callString };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_SET_CURSOR_FOR_PARAMETER_TYPE_OTHER_THAN_OUT, args));
        validationException.setErrorCode(CANNOT_SET_CURSOR_FOR_PARAMETER_TYPE_OTHER_THAN_OUT);
        return validationException;
    }

    public static ValidationException platformDoesNotSupportStoredFunctions(String platformTypeName) {
        Object[] args = { platformTypeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, PLATFORM_DOES_NOT_SUPPORT_STORED_FUNCTIONS, args));
        validationException.setErrorCode(PLATFORM_DOES_NOT_SUPPORT_STORED_FUNCTIONS);
        return validationException;
    }

    public static ValidationException illegalOperationForUnitOfWorkLifecycle(int lifecycle, String operation) {
        switch (lifecycle) {
        case UnitOfWorkImpl.CommitTransactionPending:
            return unitOfWorkInTransactionCommitPending(operation);
        case UnitOfWorkImpl.WriteChangesFailed:
            return unitOfWorkAfterWriteChangesFailed(operation);
        case UnitOfWorkImpl.Death:default:
            return inActiveUnitOfWork(operation);
        }
    }

    public static ValidationException unitOfWorkInTransactionCommitPending(String operation) {
        Object[] args = { operation };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNIT_OF_WORK_IN_TRANSACTION_COMMIT_PENDING, args));
        validationException.setErrorCode(UNIT_OF_WORK_IN_TRANSACTION_COMMIT_PENDING);
        return validationException;
    }
    
    public static ValidationException unspecifiedCompositePKNotSupported(Object entityClass) {
        Object[] args = { entityClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNSPECIFIED_COMPOSITE_PK_NOT_SUPPORTED, args));
        validationException.setErrorCode(UNSPECIFIED_COMPOSITE_PK_NOT_SUPPORTED);
        return validationException;
    }
    
    public static ValidationException unsupportedCascadeLockingDescriptor(ClassDescriptor descriptor) {
        Object[] args = { descriptor };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNSUPPORTED_CASCADE_LOCKING_DESCRIPTOR, args));
        validationException.setErrorCode(UNSUPPORTED_CASCADE_LOCKING_DESCRIPTOR);
        return validationException;
    }

    public static ValidationException unsupportedCascadeLockingMapping(DatabaseMapping mapping) {
        Object[] args = { mapping };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNSUPPORTED_CASCADE_LOCKING_MAPPING, args));
        validationException.setErrorCode(UNSUPPORTED_CASCADE_LOCKING_MAPPING);
        return validationException;
    }

    public static ValidationException unsupportedCascadeLockingMappingWithCustomQuery(DatabaseMapping mapping) {
        Object[] args = { mapping };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNSUPPORTED_CASCADE_LOCKING_MAPPING_WITH_CUSTOM_QUERY, args));
        validationException.setErrorCode(UNSUPPORTED_CASCADE_LOCKING_MAPPING_WITH_CUSTOM_QUERY);
        return validationException;
    }

    public static ValidationException unitOfWorkAfterWriteChangesFailed(String operation) {
        Object[] args = { operation };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNIT_OF_WORK_AFTER_WRITE_CHANGES_FAILED, args));
        validationException.setErrorCode(UNIT_OF_WORK_AFTER_WRITE_CHANGES_FAILED);
        return validationException;
    }

    public static ValidationException inActiveUnitOfWork(String operation) {
        Object[] args = { operation };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INACTIVE_UNIT_OF_WORK, args));
        validationException.setErrorCode(INACTIVE_UNIT_OF_WORK);
        return validationException;
    }
    
    public static ValidationException incompleteJoinColumnsSpecified(Object annotatedElement, Object javaClass) {
        Object[] args = { annotatedElement, javaClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INCOMPLETE_JOIN_COLUMNS_SPECIFIED, args));
        validationException.setErrorCode(INCOMPLETE_JOIN_COLUMNS_SPECIFIED);
        return validationException;
    }

    public static ValidationException incompletePrimaryKeyJoinColumnsSpecified(Object annotatedElement) {
        Object[] args = { annotatedElement };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INCOMPLETE_PRIMARY_KEY_JOIN_COLUMNS_SPECIFIED, args));
        validationException.setErrorCode(INCOMPLETE_PRIMARY_KEY_JOIN_COLUMNS_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException unitOfWorkInTransactionCommitPending() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNIT_OF_WORK_IN_TRANSACTION_COMMIT_PENDING, args));
        validationException.setErrorCode(UNIT_OF_WORK_IN_TRANSACTION_COMMIT_PENDING);
        return validationException;
    }

    public static ValidationException writeChangesOnNestedUnitOfWork() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_WRITE_CHANGES_ON_NESTED_UNIT_OF_WORK, args));
        validationException.setErrorCode(CANNOT_WRITE_CHANGES_ON_NESTED_UNIT_OF_WORK);
        return validationException;
    }

    public static ValidationException cannotWriteChangesTwice() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_WRITE_CHANGES_TWICE, args));
        validationException.setErrorCode(CANNOT_WRITE_CHANGES_TWICE);
        return validationException;
    }
    
    public static ValidationException multipleUniqueConstraintsWithSameNameSpecified(String name, String tableName, Object location) {
        Object[] args = { name, tableName, location };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MULTIPLE_UNIQUE_CONSTRAINTS_WITH_SAME_NAME_SPECIFIED, args));
        validationException.setErrorCode(MULTIPLE_UNIQUE_CONSTRAINTS_WITH_SAME_NAME_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException multipleVPDIdentifiersSpecified(String identifier1, String entityClassName1, String identifier2, String entityClassName2) {
        Object[] args = { identifier1, entityClassName1, identifier2, entityClassName2 };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, VPD_MULTIPLE_IDENTIFIERS_SPECIFIED, args));
        validationException.setErrorCode(VPD_MULTIPLE_IDENTIFIERS_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException multitenantContextPropertyForNonSharedEMFNotSpecified(String contextProperty) {
        Object[] args = { contextProperty };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MULTITENANT_PROPERTY_FOR_NON_SHARED_EMF_NOT_SPECIFIED, args));
        validationException.setErrorCode(MULTITENANT_PROPERTY_FOR_NON_SHARED_EMF_NOT_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException vpdNotSupported(String platformName) {
        Object[] args = { platformName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, VPD_NOT_SUPPORTED, args));
        validationException.setErrorCode(VPD_NOT_SUPPORTED);
        return validationException;
    }
    
    public static ValidationException nestedUOWNotSupportedForAttributeTracking() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NESTED_UOW_NOT_SUPPORTED_FOR_ATTRIBUTE_TRACKING, args));
        validationException.setErrorCode(NESTED_UOW_NOT_SUPPORTED_FOR_ATTRIBUTE_TRACKING);
        return validationException;
    }

    public static ValidationException nestedUOWNotSupportedForModifyAllQuery() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NESTED_UOW_NOT_SUPPORTED_FOR_MODIFY_ALL_QUERY, args));
        validationException.setErrorCode(NESTED_UOW_NOT_SUPPORTED_FOR_MODIFY_ALL_QUERY);
        return validationException;
    }
    
    public static ValidationException noAttributeTypeSpecification(String attributeName, String entityClassName, Object mappingFile) {
        Object[] args = { attributeName, entityClassName, mappingFile };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_ATTRIBUTE_TYPE_SPECIFICATION, args));
        validationException.setErrorCode(NO_ATTRIBUTE_TYPE_SPECIFICATION);
        return validationException;
    }    
    
    public static ValidationException noConverterDataTypeSpecified(Object entityClass, String attributeName, String converterName) {
        Object[] args = { entityClass, attributeName, converterName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_CONVERTER_DATA_TYPE_SPECIFIED, args));
        validationException.setErrorCode(NO_CONVERTER_DATA_TYPE_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException noConverterObjectTypeSpecified(Object entityClass, String attributeName, String converterName) {
        Object[] args = { entityClass, attributeName, converterName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_CONVERTER_OBJECT_TYPE_SPECIFIED, args));
        validationException.setErrorCode(NO_CONVERTER_OBJECT_TYPE_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException noCorrespondingSetterMethodDefined(Object entityClass, Object method) {
        Object[] args = { entityClass, method };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NO_CORRESPONDING_SETTER_METHOD_DEFINED, args));
        validationException.setErrorCode(NO_CORRESPONDING_SETTER_METHOD_DEFINED);
        return validationException;
    }

    public static ValidationException wrongCollectionChangeEventType(int eveType) {
        Object[] args = { Integer.valueOf(eveType) };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRONG_COLLECTION_CHANGE_EVENT_TYPE, args));
        validationException.setErrorCode(WRONG_COLLECTION_CHANGE_EVENT_TYPE);
        return validationException;
    }

    public static ValidationException wrongChangeEvent(Object eveClass) {
        Object[] args = { eveClass };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRONG_CHANGE_EVENT, args));
        validationException.setErrorCode(WRONG_CHANGE_EVENT);
        return validationException;
    }

    public static ValidationException oldCommitNotSupportedForAttributeTracking() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, OLD_COMMIT_NOT_SUPPORTED_FOR_ATTRIBUTE_TRACKING, args));
        validationException.setErrorCode(OLD_COMMIT_NOT_SUPPORTED_FOR_ATTRIBUTE_TRACKING);
        return validationException;
    }

    public static ValidationException unableToDetermineTargetClass(String attributeName, Object cls) {
        Object[] args = { attributeName, cls };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNABLE_TO_DETERMINE_TARGET_CLASS, args));
        validationException.setErrorCode(UNABLE_TO_DETERMINE_TARGET_CLASS);
        return validationException;
    }

    public static ValidationException unableToDetermineMapKeyClass(String attributeName, Object cls) {
        Object[] args = { attributeName, cls };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNABLE_TO_DETERMINE_MAP_KEY_CLASS, args));
        validationException.setErrorCode(UNABLE_TO_DETERMINE_MAP_KEY_CLASS);
        return validationException;
    }
    
    /**
     * PUBLIC:
     * Possible cause: the type of the attribute is Map, Set, List or Collection, and no target-entity is defined.
     * Action: ensure that the target-entity is defined in the instance doc. for the relationship mapping.
     */
    public static ValidationException unableToDetermineTargetEntity(String attributeName, Object entityClass) {
        Object[] args = { attributeName, entityClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNABLE_TO_DETERMINE_TARGET_ENTITY, args));
        validationException.setErrorCode(UNABLE_TO_DETERMINE_TARGET_ENTITY);
        return validationException;
    }
    
    public static ValidationException unableToLoadClass(String classname, Exception exception) {
        Object[] args = { classname };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNABLE_TO_LOAD_CLASS, args), exception);
        validationException.setErrorCode(UNABLE_TO_LOAD_CLASS);
        return validationException;
    }

    public static ValidationException unfetchedAttributeNotEditable(String attributeName) {
        Object[] args = { attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNFETCHED_ATTRIBUTE_NOT_EDITABLE, args));
        validationException.setErrorCode(UNFETCHED_ATTRIBUTE_NOT_EDITABLE);
        return validationException;
    }

    public static ValidationException objectNeedImplTrackerForFetchGroupUsage(String className) {
        Object[] args = { className };

        QueryException queryException = new QueryException(ExceptionMessageGenerator.buildMessage(QueryException.class, OBJECT_NEED_IMPL_TRACKER_FOR_FETCH_GROUP_USAGE, args));
        queryException.setErrorCode(OBJECT_NEED_IMPL_TRACKER_FOR_FETCH_GROUP_USAGE);
        return queryException;
    }

    public static ValidationException wrongSequenceType(String typeName, String methodName) {
        Object[] args = { typeName, methodName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRONG_SEQUENCE_TYPE, args));
        validationException.setErrorCode(WRONG_SEQUENCE_TYPE);
        return validationException;
    }

    public static ValidationException cannotSetDefaultSequenceAsDefault(String seqName) {
        Object[] args = { seqName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_SET_DEFAULT_SEQUENCE_AS_DEFAULT, args));
        validationException.setErrorCode(CANNOT_SET_DEFAULT_SEQUENCE_AS_DEFAULT);
        return validationException;
    }

    public static ValidationException defaultSequenceNameAlreadyUsedBySequence(String seqName) {
        Object[] args = { seqName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, DEFAULT_SEQUENCE_NAME_ALREADY_USED_BY_SEQUENCE, args));
        validationException.setErrorCode(DEFAULT_SEQUENCE_NAME_ALREADY_USED_BY_SEQUENCE);
        return validationException;
    }

    public static ValidationException sequenceNameAlreadyUsedByDefaultSequence(String seqName) {
        Object[] args = { seqName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, SEQUENCE_NAME_ALREADY_USED_BY_DEFAULT_SEQUENCE, args));
        validationException.setErrorCode(SEQUENCE_NAME_ALREADY_USED_BY_DEFAULT_SEQUENCE);
        return validationException;
    }

    public static ValidationException platformDoesNotSupportSequence(String seqName, String platformTypeName, String sequenceTypeName) {
        Object[] args = { seqName, platformTypeName, sequenceTypeName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, PLATFORM_DOES_NOT_SUPPORT_SEQUENCE, args));
        validationException.setErrorCode(PLATFORM_DOES_NOT_SUPPORT_SEQUENCE);
        return validationException;
    }

    public static ValidationException sequenceCannotBeConnectedToTwoPlatforms(String seqName, String ownerPlatformName, String otherPlatformName) {
        Object[] args = { seqName, ownerPlatformName, otherPlatformName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, SEQUENCE_CANNOT_BE_CONNECTED_TO_TWO_PLATFORMS, args));
        validationException.setErrorCode(SEQUENCE_CANNOT_BE_CONNECTED_TO_TWO_PLATFORMS);
        return validationException;
    }

    public static ValidationException querySequenceDoesNotHaveSelectQuery(String seqName) {
        Object[] args = { seqName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, QUERY_SEQUENCE_DOES_NOT_HAVE_SELECT_QUERY, args));
        validationException.setErrorCode(QUERY_SEQUENCE_DOES_NOT_HAVE_SELECT_QUERY);
        return validationException;
    }

    public static ValidationException createPlatformDefaultSequenceUndefined(String platformTypeName) {
        Object[] args = { platformTypeName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CREATE_PLATFORM_DEFAULT_SEQUENCE_UNDEFINED, args));
        validationException.setErrorCode(CREATE_PLATFORM_DEFAULT_SEQUENCE_UNDEFINED);
        return validationException;
    }

    public static ValidationException sequenceGeneratorUsingAReservedName(String reservedName, Object location) {
        Object[] args = { reservedName, location };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, SEQUENCE_GENERATOR_RESERVED_NAME, args));
        validationException.setErrorCode(SEQUENCE_GENERATOR_RESERVED_NAME);
        return validationException;
    }
    
    public static ValidationException tableGeneratorUsingAReservedName(String reservedName, Object location) {
        Object[] args = { reservedName, location };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, TABLE_GENERATOR_RESERVED_NAME, args));
        validationException.setErrorCode(TABLE_GENERATOR_RESERVED_NAME);
        return validationException;
    }

    public static ValidationException onlyOneGeneratedValueIsAllowed(Object cls, String field1, String field2) {
        Object[] args = { cls, field1, field2 };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ONLY_ONE_GENERATED_VALURE_IS_ALLOWED, args));
        validationException.setErrorCode(ONLY_ONE_GENERATED_VALURE_IS_ALLOWED);
        return validationException;
    }

    public static ValidationException wrongPropertyNameInChangeEvent(Object objectClass, String propertyName) {
        Object[] args = { objectClass, propertyName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRONG_PROPERTY_NAME_IN_CHANGE_EVENT, args));
        validationException.setErrorCode(WRONG_PROPERTY_NAME_IN_CHANGE_EVENT);
        return validationException;
    }

    public static ValidationException oracleOCIProxyConnectorRequiresOracleOCIConnectionPool() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ORACLEOCIPROXYCONNECTOR_REQUIRES_ORACLEOCICONNECTIONPOOL, args));
        validationException.setErrorCode(ORACLEOCIPROXYCONNECTOR_REQUIRES_ORACLEOCICONNECTIONPOOL);
        return validationException;
    }

    public static ValidationException oracleJDBC10_1_0_2ProxyConnectorRequiresOracleConnection() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ORACLEJDBC10_1_0_2PROXYCONNECTOR_REQUIRES_ORACLECONNECTION, args));
        validationException.setErrorCode(ORACLEJDBC10_1_0_2PROXYCONNECTOR_REQUIRES_ORACLECONNECTION);
        return validationException;
    }

    public static ValidationException oracleJDBC10_1_0_2ProxyConnectorRequiresOracleConnectionVersion() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ORACLEJDBC10_1_0_2PROXYCONNECTOR_REQUIRES_ORACLECONNECTION_VERSION, args));
        validationException.setErrorCode(ORACLEJDBC10_1_0_2PROXYCONNECTOR_REQUIRES_ORACLECONNECTION_VERSION);
        return validationException;
    }

    public static ValidationException oracleJDBC10_1_0_2ProxyConnectorRequiresIntProxytype() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ORACLEJDBC10_1_0_2PROXYCONNECTOR_REQUIRES_INT_PROXYTYPE, args));
        validationException.setErrorCode(ORACLEJDBC10_1_0_2PROXYCONNECTOR_REQUIRES_INT_PROXYTYPE);
        return validationException;
    }

    public static ValidationException couldNotFindDriverClass(Object driver, Exception ex) {
        Object[] args = { driver };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, COULD_NOT_FIND_DRIVER_CLASS, args), ex);
        validationException.setErrorCode(COULD_NOT_FIND_DRIVER_CLASS);
        return validationException;
    }
    
    public static ValidationException couldNotFindMapKey(String attributeName, Object entityClass, DatabaseMapping mapping) {
        Object[] args = { attributeName, entityClass, mapping };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, COULD_NOT_FIND_MAP_KEY, args));
        validationException.setErrorCode(COULD_NOT_FIND_MAP_KEY);
        return validationException;
    }

    public static ValidationException errorClosingPersistenceXML(Exception ex) {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ERROR_CLOSING_PERSISTENCE_XML, args), ex);
        validationException.setErrorCode(ERROR_CLOSING_PERSISTENCE_XML);
        return validationException;
    }

    public static ValidationException configFactoryNamePropertyNotSpecified(String configFactory) {
        Object[] args = { configFactory };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFIG_FACTORY_NAME_PROPERTY_NOT_SPECIFIED, args));
        validationException.setErrorCode(CONFIG_FACTORY_NAME_PROPERTY_NOT_SPECIFIED);
        return validationException;
    }

    public static ValidationException configFactoryNamePropertyNotFound(String configClass, String configFactory, Exception ex) {
        Object[] args = { configClass, configFactory };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFIG_FACTORY_NAME_PROPERTY_NOT_FOUND, args), ex);
        validationException.setErrorCode(CONFIG_FACTORY_NAME_PROPERTY_NOT_FOUND);
        return validationException;
    }

    public static ValidationException cannotInvokeMethodOnConfigClass(String configMethod, String configClass, String configFactory, Exception ex) {
        Object[] args = { configMethod, configClass, configFactory };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_INVOKE_METHOD_ON_CONFIG_CLASS, args), ex);
        validationException.setErrorCode(CANNOT_INVOKE_METHOD_ON_CONFIG_CLASS);
        return validationException;
    }

    public static ValidationException configMethodNotDefined(String configClass, String configMethod) {
        Object[] args = { configClass, configMethod };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFIG_METHOD_NOT_DEFINED, args));
        validationException.setErrorCode(CONFIG_METHOD_NOT_DEFINED);
        return validationException;
    }

    public static ValidationException conflictingNamedAnnotations(String name, Object annotation1, Object location1, Object annotation2, Object location2) {
        Object[] args = { name, annotation1, location1, annotation2, location2 };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFLICTING_NAMED_ANNOTATIONS, args));
        validationException.setErrorCode(CONFLICTING_NAMED_ANNOTATIONS);
        return validationException;
    }
    
    public static ValidationException conflictingNamedXMLElements(String name, String xmlElement, Object location1, Object location2) {
        Object[] args = { name, xmlElement, location1, location2 };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFLICTING_NAMED_XML_ELEMENTS, args));
        validationException.setErrorCode(CONFLICTING_NAMED_XML_ELEMENTS);
        return validationException;
    }
    
    public static ValidationException conflictingAnnotations(Object annotation1, Object location1, Object annotation2, Object location2) {
        Object[] args = { annotation1, location1, annotation2, location2 };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFLICTING_ANNOTATIONS, args));
        validationException.setErrorCode(CONFLICTING_ANNOTATIONS);
        return validationException;
    }
    
    public static ValidationException conflictingXMLElements(String xmlElement, Object element, Object location1, Object location2) {
        Object[] args = { xmlElement, element, location1, location2 };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFLICTING_XML_ELEMENTS, args));
        validationException.setErrorCode(CONFLICTING_XML_ELEMENTS);
        return validationException;
    }    
    
    
    public static ValidationException conflictingSequenceAndTableGeneratorsSpecified(String name, Object sequenceGeneratorLocation, Object tableGeneratorLocation) {
        Object[] args = { name, sequenceGeneratorLocation, tableGeneratorLocation };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFLICTING_SEQUENCE_AND_TABLE_GENERATORS_SPECIFIED, args));
        validationException.setErrorCode(CONFLICTING_SEQUENCE_AND_TABLE_GENERATORS_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException conflictingSequenceNameAndTablePkColumnValueSpecified(String name, Object sequenceGeneratorLocation, Object tableGeneratorLocation) {
        Object[] args = { name, sequenceGeneratorLocation, tableGeneratorLocation };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFLICTING_SEQUENCE_NAME_AND_TABLE_PK_COLUMN_VALUE_SPECIFIED, args));
        validationException.setErrorCode(CONFLICTING_SEQUENCE_NAME_AND_TABLE_PK_COLUMN_VALUE_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException classListMustNotBeNull() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CLASS_LIST_MUST_NOT_BE_NULL, args));
        validationException.setErrorCode(CLASS_LIST_MUST_NOT_BE_NULL);
        return validationException;
    }

    public static ValidationException currentLoaderNotValid(ClassLoader loader) {
        Object[] args = { loader };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CURRENT_LOADER_NOT_VALID, args));
        validationException.setErrorCode(CURRENT_LOADER_NOT_VALID);
        return validationException;
    }

    public static ValidationException methodFailed(String methodName, Exception ex) {
        Object[] args = { methodName };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, METHOD_FAILED, args), ex);
        validationException.setErrorCode(METHOD_FAILED);
        return validationException;
    }
    
    public static ValidationException missingDescriptor(String className) {
        Object[] args = { className };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MISSING_DESCRIPTOR, args));
        validationException.setErrorCode(MISSING_DESCRIPTOR);
        return validationException;
    }
    
    public static ValidationException missingContextStringForContext(String context) {
        Object[] args = { context };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MISSING_CONTEXT_STRING_FOR_CONTEXT, args));
        validationException.setErrorCode(MISSING_CONTEXT_STRING_FOR_CONTEXT);
        return validationException;
    }

    public static ValidationException missingConvertAttributeName(String className) {
        Object[] args = { className };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MISSING_CONVERT_ATTRIBUTE_NAME, args));
        validationException.setErrorCode(MISSING_CONVERT_ATTRIBUTE_NAME);
        return validationException;
    }
    
    public static ValidationException missingMappingConvertAttributeName(String className, String attributeName) {        
        Object[] args = { className, attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MISSING_MAPPING_CONVERT_ATTRIBUTE_NAME, args));
        validationException.setErrorCode(MISSING_MAPPING_CONVERT_ATTRIBUTE_NAME);
        return validationException;
    }

    public static ValidationException invalidMappingForConvert(String className, String attributeName) {        
        Object[] args = { className, attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_MAPPING_FOR_CONVERT, args));
        validationException.setErrorCode(INVALID_MAPPING_FOR_CONVERT);
        return validationException;
    }
    
    public static ValidationException invalidMappingForConvertWithAttributeName(String className, String attributeName) {        
        Object[] args = { className, attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_MAPPING_FOR_CONVERT_WITH_ATTRIBUTE_NAME, args));
        validationException.setErrorCode(INVALID_MAPPING_FOR_CONVERT_WITH_ATTRIBUTE_NAME);
        return validationException;
    }
    
    public static ValidationException invalidMappingForMapKeyConvert(String className, String attributeName) {        
        Object[] args = { className, attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_MAPPING_FOR_MAP_KEY_CONVERT, args));
        validationException.setErrorCode(INVALID_MAPPING_FOR_MAP_KEY_CONVERT);
        return validationException;
    }
    
    public static ValidationException embeddableAttributeNameForConvertNotFound(String className, String attributeName, String embeddableClassName, String embeddableAttributeName) {
        Object[] args = { className, attributeName, embeddableClassName, embeddableAttributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EMBEDDABLE_ATTRIBUTE_NAME_FOR_CONVERT_NOT_FOUND, args));
        validationException.setErrorCode(EMBEDDABLE_ATTRIBUTE_NAME_FOR_CONVERT_NOT_FOUND);
        return validationException;
    }
    
    public static ValidationException converterClassNotFound(String className, String attributeName, String converterClass) {
        Object[] args = { className, attributeName, converterClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONVERTER_CLASS_NOT_FOUND, args));
        validationException.setErrorCode(CONVERTER_CLASS_NOT_FOUND);
        return validationException;
    }

    public static ValidationException converterClassMustImplementAttributeConverterInterface(String converterClass) {
        Object[] args = { converterClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONVERTER_CLASS_MUST_IMPLEMENT_ATTRIBUTE_CONVERTER_INTERFACE, args));
        validationException.setErrorCode(CONVERTER_CLASS_MUST_IMPLEMENT_ATTRIBUTE_CONVERTER_INTERFACE);
        return validationException;
    }
    
    /**
     * Create a validation exception for the look up of a mapping on a descriptor for an unknown attribute name. The source
     * is a string describing where the lookup was called from.
     */
    public static ValidationException missingMappingForAttribute(ClassDescriptor descriptor, String attributeName, String source) {
        Object[] args = { descriptor, attributeName, source };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MISSING_MAPPING, args));
        validationException.setErrorCode(MISSING_MAPPING);
        return validationException;
    }
    
    public static ValidationException missingFieldTypeForDDLGenerationOfClassTransformation(ClassDescriptor descriptor, String attributeName, String methodName) {
        Object[] args = { descriptor, attributeName, methodName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MISSING_FIELD_TYPE_FOR_DDL_GENERATION_OF_CLASS_TRANSFORMATION_, args));
        validationException.setErrorCode(MISSING_FIELD_TYPE_FOR_DDL_GENERATION_OF_CLASS_TRANSFORMATION_);
        return validationException;
    }
    
    public static ValidationException missingTransformerMethodForDDLGenerationOfClassTransformation(ClassDescriptor descriptor, String attributeName, String methodName) {
        Object[] args = { descriptor, attributeName, methodName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MISSING_TRANSFORMER_METHOD_FOR_DDL_GENERATION_OF_CLASS_TRANSFORMATION, args));
        validationException.setErrorCode(MISSING_TRANSFORMER_METHOD_FOR_DDL_GENERATION_OF_CLASS_TRANSFORMATION);
        return validationException;
    }
    
    public static ValidationException multipleOutParamsNotSupported(String platformTypeName, String procedureName) {
        Object[] args = { platformTypeName, procedureName};

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MULTIPLE_OUT_PARAMS_NOT_SUPPORTED, args));
        validationException.setErrorCode(MULTIPLE_OUT_PARAMS_NOT_SUPPORTED);
        return validationException;
    }

    public static ValidationException multipleCursorsNotSupported(String callString) {
        Object[] args = { callString };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MULTIPLE_CURSORS_NOT_SUPPORTED, args));
        validationException.setErrorCode(MULTIPLE_CURSORS_NOT_SUPPORTED);
        return validationException;
    }
    
    public static ValidationException multipleEmbeddedIdAnnotationsFound(Object entityClass, String attributeName1, String attributeName2) {
        Object[] args = { entityClass, attributeName1, attributeName2 };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MULTIPLE_EMBEDDED_ID_ANNOTATIONS_FOUND, args));
        validationException.setErrorCode(MULTIPLE_EMBEDDED_ID_ANNOTATIONS_FOUND);
        return validationException;
    }
    
    public static ValidationException multipleLifecycleCallbackMethodsForSameLifecycleEvent(Object listenerClass, Method method1, Method method2) {
        Object[] args = { listenerClass, method1, method2 };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MULTIPLE_CALLBACK_METHODS_DEFINED, args));
        validationException.setErrorCode(MULTIPLE_CALLBACK_METHODS_DEFINED);
        return validationException;
    }
    
    public static ValidationException multipleObjectValuesForDataValue(Object javaClass, String converterName, String dataValue) {
        Object[] args = { javaClass, converterName, dataValue };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MULTIPLE_OBJECT_VALUES_FOR_DATA_VALUE, args));
        validationException.setErrorCode(MULTIPLE_OBJECT_VALUES_FOR_DATA_VALUE);
        return validationException;
    }

    public static ValidationException entityClassNotFound(String entityClass, ClassLoader loader, Exception ex) {
        Object[] args = { entityClass, loader };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, ENTITY_CLASS_NOT_FOUND, args), ex);
        validationException.setErrorCode(ENTITY_CLASS_NOT_FOUND);
        return validationException;
    }

    public static ValidationException classExtractorCanNotBeSpecifiedWithDiscriminatorMetadata(String className) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CLASS_EXTRACTOR_CAN_NOT_BE_SPECIFIED_WITH_DISCRIMINATOR, args));
        validationException.setErrorCode(CLASS_EXTRACTOR_CAN_NOT_BE_SPECIFIED_WITH_DISCRIMINATOR);
        return validationException;
    }
    
    public static ValidationException classFileTransformerThrowsException(Object transformer, String className, Exception ex) {
        Object[] args = { transformer, className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CLASS_FILE_TRANSFORMER_THROWS_EXCEPTION, args), ex);
        validationException.setErrorCode(CLASS_FILE_TRANSFORMER_THROWS_EXCEPTION);
        return validationException;
    }

    public static ValidationException jarFilesInPersistenceXmlNotSupported() {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, JAR_FILES_IN_PERSISTENCE_XML_NOT_SUPPORTED, args));
        validationException.setErrorCode(JAR_FILES_IN_PERSISTENCE_XML_NOT_SUPPORTED);
        return validationException;
    }

    public static ValidationException couldNotBindJndi(String bindName, Object bindValue, Exception ex) {
        Object[] args = { bindName, bindValue };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, COULD_NOT_BIND_JNDI, args), ex);
        validationException.setErrorCode(COULD_NOT_BIND_JNDI);
        return validationException;
    }

    public static ValidationException exceptionConfiguringEMFactory(Exception ex) {
        Object[] args = {  };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EXCEPTION_CONFIGURING_EM_FACTORY, args), ex);
        validationException.setErrorCode(EXCEPTION_CONFIGURING_EM_FACTORY);
        return validationException;
    }

    public static ValidationException excessivePrimaryKeyJoinColumnsSpecified(Object annotatedElement) {
        Object[] args = { annotatedElement };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EXCESSIVE_PRIMARY_KEY_JOIN_COLUMNS_SPECIFIED, args));
        validationException.setErrorCode(EXCESSIVE_PRIMARY_KEY_JOIN_COLUMNS_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException classNotFoundWhileConvertingClassNames(String className, Exception exception) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CLASS_NOT_FOUND_WHILE_CONVERTING_CLASSNAMES, args), exception);
        validationException.setErrorCode(CLASS_NOT_FOUND_WHILE_CONVERTING_CLASSNAMES);
        return validationException;
    }
    
    public static ValidationException platformDoesNotOverrideGetCreateTempTableSqlPrefix(String className) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, PLATFORM_DOES_NOT_OVERRIDE_GETCREATETEMPTABLESQLPREFIX, args));
        validationException.setErrorCode(PLATFORM_DOES_NOT_OVERRIDE_GETCREATETEMPTABLESQLPREFIX);
        return validationException;
    }

    public static ValidationException mappingAnnotationsAppliedToTransientAttribute(Object annotatedElement) {
        Object[] args = { annotatedElement };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MAPPING_ANNOTATIONS_APPLIED_TO_TRANSIENT_ATTRIBUTE, args));
        validationException.setErrorCode(MAPPING_ANNOTATIONS_APPLIED_TO_TRANSIENT_ATTRIBUTE);
        return validationException;
    }
    
    public static ValidationException mappingDoesNotOverrideValueFromRowInternalWithJoin(String className) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MAPPING_DOES_NOT_OVERRIDE_VALUEFROMROWINTERNALWITHJOIN, args));
        validationException.setErrorCode(MAPPING_DOES_NOT_OVERRIDE_VALUEFROMROWINTERNALWITHJOIN);
        return validationException;
    }
    
    public static ValidationException mappingFileNotFound(String puName, String mf) {
        Object[] args = {puName, mf};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MAPPING_FILE_NOT_FOUND, args));
        validationException.setErrorCode(MAPPING_FILE_NOT_FOUND);
        return validationException;
    }
    
    public static ValidationException mappingMetadataAppliedToMethodWithArguments(Object element, Object cls) {
        Object[] args = { element, cls };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MAPPING_METADATA_APPLIED_TO_METHOD_WITH_ARGUMENTS, args));
        validationException.setErrorCode(MAPPING_METADATA_APPLIED_TO_METHOD_WITH_ARGUMENTS);
        return validationException;
    }
    
    public static ValidationException cannotPersistExistingObject(Object registeredObject, AbstractSession session) {
        Object key = null;
        if (session != null) {
            key = session.getId(registeredObject);
        }
        Object[] args = { registeredObject, registeredObject.getClass().getName(), key, CR };
        
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_PERSIST_MANAGED_OBJECT, args));     
        validationException.setErrorCode(CANNOT_PERSIST_MANAGED_OBJECT);
        return validationException;
    }

    public static ValidationException classNotListedInPersistenceUnit(String className) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CLASS_NOT_LISTED_IN_PERSISTENCE_UNIT, args));
        validationException.setErrorCode(CLASS_NOT_LISTED_IN_PERSISTENCE_UNIT);
        return validationException;
    }
    
    public static ValidationException conflictingAccessTypeForEmbeddable(String embeddableClassName, String embeddingClassName1, String accessType1, String embeddingClassName2, String accessType2) {
        Object[] args = {embeddableClassName, embeddingClassName1, accessType1, embeddingClassName2, accessType2};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFLICTNG_ACCESS_TYPE_FOR_EMBEDDABLE, args));
        validationException.setErrorCode(CONFLICTNG_ACCESS_TYPE_FOR_EMBEDDABLE);
        return validationException;
    }
    
    public static ValidationException conflictingAccessMethodsForEmbeddable(String embeddableClassName, String embeddingClassName1, Object accessMethods1, String embeddingClassName2, Object accessMethods2) {
        Object[] args = {embeddableClassName, embeddingClassName1, accessMethods1, embeddingClassName2, accessMethods2};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CONFLICTNG_ACCESS_METHODS_FOR_EMBEDDABLE, args));
        validationException.setErrorCode(CONFLICTNG_ACCESS_METHODS_FOR_EMBEDDABLE);
        return validationException;
    }

    public static ValidationException invalidEmbeddedAttribute(
        Object javaClass, String attributeName, Object embeddableClass) {
        Object[] args = {javaClass, attributeName, embeddableClass};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_EMBEDDED_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_EMBEDDED_ATTRIBUTE);
        return validationException;
    }

    public static ValidationException embeddedIdHasNoAttributes(Object entityClass, Object embeddableClass, String accessType) {
        Object[] args = {entityClass, embeddableClass, accessType};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EMBEDDED_ID_CLASS_HAS_NO_ATTRIBUTES, args));
        validationException.setErrorCode(EMBEDDED_ID_CLASS_HAS_NO_ATTRIBUTES);
        return validationException;
    }
    
    public static ValidationException primaryKeyColumnNameNotSpecified(Object entityClass) {
        Object[] args = { entityClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, PRIMARY_KEY_COLUMN_NAME_NOT_SPECIFIED, args));
        validationException.setErrorCode(PRIMARY_KEY_COLUMN_NAME_NOT_SPECIFIED);
        return validationException;
    }
    
    public static ValidationException primaryKeyUpdateDisallowed(String className, String attributeName) {
        Object[] args = {className, attributeName};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, PRIMARY_KEY_UPDATE_DISALLOWED, args));
        validationException.setErrorCode(PRIMARY_KEY_UPDATE_DISALLOWED);
        return validationException;
    }
    
    public static ValidationException cannotInstantiateExceptionHandlerClass(String className,Exception e) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_EXCEPTIONHANDLER_CLASS, args),e);
        validationException.setErrorCode(CANNOT_INSTANTIATE_EXCEPTIONHANDLER_CLASS);
        return validationException;
    }
    
    public static ValidationException cannotInstantiateProfilerClass(String className,Exception e) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_INSTANTIATE_PROFILER_CLASS, args),e);
        validationException.setErrorCode(CANNOT_INSTANTIATE_PROFILER_CLASS);
        return validationException;
    }
    
    
    public static ValidationException cannotInstantiateSessionEventListenerClass(String className,Exception e) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_INSTANTIATE_SESSIONEVENTLISTENER_CLASS, args),e);
        validationException.setErrorCode(CANNOT_INSTANTIATE_SESSIONEVENTLISTENER_CLASS);
        return validationException;
    }

    public static ValidationException invalidExceptionHandlerClass(String className) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_EXCEPTIONHANDLER_CLASS, args));
        validationException.setErrorCode(INVALID_EXCEPTIONHANDLER_CLASS);
        return validationException;
    }

    public static ValidationException invalidExplicitAccessTypeSpecified(Object annotatedElement, Object javaClass, String expectedAccessType) {
        Object[] args = { annotatedElement, javaClass, expectedAccessType };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_EXPLICIT_ACCESS_TYPE, args));
        validationException.setErrorCode(INVALID_EXPLICIT_ACCESS_TYPE);
        return validationException;
    }
    
    public static ValidationException invalidSessionEventListenerClass(String className) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_SESSIONEVENTLISTENER_CLASS, args));
        validationException.setErrorCode(INVALID_SESSIONEVENTLISTENER_CLASS);
        return validationException;
    }

    public static ValidationException invalidSQLResultSetMapping(String sqlResultSetMapping, String queryName, Object location) {
        Object[] args = { sqlResultSetMapping, queryName, location };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_SQL_RESULT_SET_MAPPING_NAME, args));
        validationException.setErrorCode(INVALID_SQL_RESULT_SET_MAPPING_NAME);
        return validationException;
    }
    
    public static ValidationException invalidTargetClass(String attributeName, Object cls) {
        Object[] args = { attributeName, cls };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_TARGET_CLASS, args));
        validationException.setErrorCode(INVALID_TARGET_CLASS);
        return validationException;
    }
    
    public static ValidationException invalidCacheStatementsSize(String cacheStatementsSize,String errorMessage) {
        Object[] args = { cacheStatementsSize,errorMessage};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_CACHESTATEMENTS_SIZE_VALUE, args));
        validationException.setErrorCode(INVALID_CACHESTATEMENTS_SIZE_VALUE);
        return validationException;
    }

    public static ValidationException invalidBooleanValueForSettingNativeSQL(String specifiedBooleanValue) {
        Object[] args = { specifiedBooleanValue };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_BOOLEAN_VALUE_FOR_SETTING_NATIVESQL, args));
        validationException.setErrorCode(INVALID_BOOLEAN_VALUE_FOR_SETTING_NATIVESQL);
        return validationException;
    }
    
    public static ValidationException invalidBooleanValueForSettingAllowNativeSQLQueries(String specifiedBooleanValue) {
        Object[] args = { specifiedBooleanValue };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_BOOLEAN_VALUE_FOR_SETTING_ALLOW_NATIVESQL_QUERIES, args));
        validationException.setErrorCode(INVALID_BOOLEAN_VALUE_FOR_SETTING_ALLOW_NATIVESQL_QUERIES);
        return validationException;
    }
    
    public static ValidationException invalidBooleanValueForEnableStatmentsCached(String specifiedBooleanValue) {
        Object[] args = { specifiedBooleanValue };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_BOOLEAN_VALUE_FOR_ENABLESTATMENTSCACHED, args));
        validationException.setErrorCode(INVALID_BOOLEAN_VALUE_FOR_ENABLESTATMENTSCACHED);
        return validationException;
    }
    
    public static ValidationException invalidBooleanValueForProperty(String specifiedBooleanValue, String property) {
        Object[] args = { specifiedBooleanValue, property };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_BOOLEAN_VALUE_FOR_PROPERTY, args));
        validationException.setErrorCode(INVALID_BOOLEAN_VALUE_FOR_PROPERTY);
        return validationException;
    }
    
    public static ValidationException invalidValueForProperty(Object specifiedValue, String property, Exception error) {
        Object[] args = { specifiedValue, property, String.valueOf(error) };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_VALUE_FOR_PROPERTY, args), error);
        validationException.setErrorCode(INVALID_VALUE_FOR_PROPERTY);
        return validationException;
    }
    
    public static ValidationException invalidBooleanValueForAddingNamedQueries(String specifiedBooleanValue) {
        Object[] args = { specifiedBooleanValue };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_BOOLEAN_VALUE_FOR_ADDINGNAMEDQUERIES, args));
        validationException.setErrorCode(INVALID_BOOLEAN_VALUE_FOR_ADDINGNAMEDQUERIES);
        return validationException;
    }
    
    public static ValidationException invalidLoggingFile() {
        Object[] args = { };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, LOGGING_FILE_NAME_CANNOT_BE_EMPTY,args));
        validationException.setErrorCode(LOGGING_FILE_NAME_CANNOT_BE_EMPTY);
        return validationException;
    }

    public static ValidationException invalidLoggingFile(String loggingFile, Exception e ) {
        Object[] args = { loggingFile,e};
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_LOGGING_FILE,args),e);
        validationException.setErrorCode(INVALID_LOGGING_FILE);
        return validationException;
    }
            
    public static ValidationException multipleClassesForTheSameDiscriminator(String discriminator, String attributeName) {
        Object[] args = {discriminator, attributeName};

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MULTIPLE_CLASSES_FOR_THE_SAME_DISCRIMINATOR, args));
        validationException.setErrorCode(MULTIPLE_CLASSES_FOR_THE_SAME_DISCRIMINATOR);
        return validationException;
    }
        
    public static ValidationException invalidMappingForStructConverter(String name, DatabaseMapping mapping) {
        Object[] args = {name, mapping};

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_MAPPING_FOR_STRUCT_CONVERTER, args));
        validationException.setErrorCode(INVALID_MAPPING_FOR_STRUCT_CONVERTER);
        return validationException;
    }
    
    public static ValidationException twoStructConvertersAddedForSameClass(String className) {
        Object[] args = {className};

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, TWO_STRUCT_CONVERTERS_ADDED_FOR_SAME_CLASS, args));
        validationException.setErrorCode(TWO_STRUCT_CONVERTERS_ADDED_FOR_SAME_CLASS);
        return validationException;
    }
    
    public static ValidationException invalidComparatorClass(String className) {
        Object[] args = {className};

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_COMPARATOR_CLASS, args));
        validationException.setErrorCode(INVALID_COMPARATOR_CLASS);
        return validationException;
    }
    
    
    public static ValidationException invalidProfilerClass(String className) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_PROFILER_CLASS, args));
        validationException.setErrorCode(INVALID_PROFILER_CLASS);
        return validationException;
    }
    
    /**
     * PUBLIC:
     * Possible cause: An field name has been encountered that does not exist on 
     * the associated entity.
     * Action: Ensure that a field with a matching name exists on the associated 
     * entity.
     */
    public static ValidationException invalidFieldForClass(String fieldName, Object entityClass) {
        Object[] args = { fieldName, entityClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_FIELD_FOR_CLASS, args));
        validationException.setErrorCode(INVALID_FIELD_FOR_CLASS);
        return validationException;
    }
    
    /**
     * PUBLIC:
     * Possible cause: An property name has been encountered that does not exist 
     * on the associated entity.
     * Action: Ensure that a property with a matching name exists on the 
     * associated entity.
     */
    public static ValidationException invalidPropertyForClass(String propertyName, Object entityClass) {
        Object[] args = { propertyName, entityClass };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, INVALID_PROPERTY_FOR_CLASS, args));
        validationException.setErrorCode(INVALID_PROPERTY_FOR_CLASS);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: ReadTransformer for the specified attribute of the specified class
     * doesn't implement the required interface AttributeTransforer. 
     */
    public static ValidationException readTransformerClassDoesntImplementAttributeTransformer(String annotatedElement) {
        Object[] args = { annotatedElement };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, READ_TRANSFORMER_CLASS_DOESNT_IMPLEMENT_ATTRIBUTE_TRANSFORMER, args));
        validationException.setErrorCode(READ_TRANSFORMER_CLASS_DOESNT_IMPLEMENT_ATTRIBUTE_TRANSFORMER);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: ReadTransformer for the specified attribute of the specified class
     * specifies both class and method. 
     */
    public static ValidationException readTransformerHasBothClassAndMethod(String annotatedElement) {
        Object[] args = { annotatedElement };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, READ_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD, args));
        validationException.setErrorCode(READ_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: ReadTransformer for the specified attribute of the specified class
     * specifies neither class nor method. 
     */
    public static ValidationException readTransformerHasNeitherClassNorMethod(String annotatedElement) {
        Object[] args = { annotatedElement };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, READ_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD, args));
        validationException.setErrorCode(READ_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: WriteTransformer for the specified attribute of the specified class and specified column
     * doesn't implement the required interface FieldTransforer. 
     */
    public static ValidationException writeTransformerClassDoesntImplementFieldTransformer(String annotatedElement, String columnName) {
        Object[] args = { annotatedElement, columnName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRITE_TRANSFORMER_CLASS_DOESNT_IMPLEMENT_FIELD_TRANSFORMER, args));
        validationException.setErrorCode(WRITE_TRANSFORMER_CLASS_DOESNT_IMPLEMENT_FIELD_TRANSFORMER);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: WriteTransformer for the specified attribute of the specified class and specified column
     * specifies both class and method. 
     */
    public static ValidationException writeTransformerHasBothClassAndMethod(String annotatedElement, String columnName) {
        Object[] args = { annotatedElement, columnName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRITE_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD, args));
        validationException.setErrorCode(WRITE_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: WriteTransformer for the specified attribute of the specified class and specified column
     * specifies neither class nor method. 
     */
    public static ValidationException writeTransformerHasNeitherClassNorMethod(String annotatedElement, String columnName) {
        Object[] args = { annotatedElement, columnName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRITE_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD, args));
        validationException.setErrorCode(WRITE_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: WriteTransformer for the specified attribute of the specified class
     * has no column specified, of the specified column doesn't have name.
     */
    public static ValidationException writeTransformerHasNoColumnName(String annotatedElement) {
        Object[] args = { annotatedElement };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, WRITE_TRANSFORMER_HAS_NO_COLUMN_NAME, args));
        validationException.setErrorCode(WRITE_TRANSFORMER_HAS_NO_COLUMN_NAME);
        return validationException;
    }
     
    /**
     * PUBLIC:
     * Cause: An annotation or XML document specifies a CloneCopyPolicy, bug does not supply either a method or
     * working copy method
     */
    public static ValidationException copyPolicyMustSpecifyEitherMethodOrWorkingCopyMethod(Object location) {
        Object[] args = { location };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, COPY_POLICY_MUST_SPECIFY_METHOD_OR_WORKING_COPY_METHOD, args));
        validationException.setErrorCode(COPY_POLICY_MUST_SPECIFY_METHOD_OR_WORKING_COPY_METHOD);
        return validationException;
    }
    
    public static ValidationException multipleContextPropertiesForSameTenantDiscriminatorFieldSpecified(String className, String fieldName, String property1, String property2) {
        Object[] args = { className, fieldName, property1, property2 };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MULTIPLE_CONTEXT_PROPERTY_FOR_TENANT_DISCRIMINATOR_FIELD, args));
        validationException.setErrorCode(MULTIPLE_CONTEXT_PROPERTY_FOR_TENANT_DISCRIMINATOR_FIELD);
        return validationException;
    }

    public static ValidationException nonReadOnlyMappedTenantDiscriminatorField(String className, String fieldName) {
        Object[] args = { className, fieldName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, NON_READ_ONLY_MAPPED_TENANT_DISCRIMINATOR_FIELD, args));
        validationException.setErrorCode(NON_READ_ONLY_MAPPED_TENANT_DISCRIMINATOR_FIELD);
        return validationException;
    }
    
    /**
     * PUBLIC:
     * Cause: An annotation or XML document specifies a CloneCopyPolicy, bug does not supply either a method or
     * working copy method
     */
    public static ValidationException multipleCopyPolicyAnnotationsOnSameClass(String className) {
        Object[] args = { className };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MULTIPLE_COPY_POLICY_ANNOTATIONS_ON_SAME_CLASS, args));
        validationException.setErrorCode(MULTIPLE_COPY_POLICY_ANNOTATIONS_ON_SAME_CLASS);
        return validationException;
    }
    
    /**
     * PUBLIC:
     * Cause: When deploying a JPA application, an Exception was thrown while reflectively instantiating a
     * class that was listed in the metadata.  See the chained exception chain for more information.
     */
    public static ValidationException reflectiveExceptionWhileCreatingClassInstance(String className, Exception exception) {
        Object[] args = { className };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, REFLECTIVE_EXCEPTION_WHILE_CREATING_CLASS_INSTANCE, args), exception);
        validationException.setErrorCode(REFLECTIVE_EXCEPTION_WHILE_CREATING_CLASS_INSTANCE);
        return validationException;
    }   

    /**
     * PUBLIC:
     * Proxy property corresponding to the specified proxy type was not found.
     */
    public static ValidationException expectedProxyPropertyNotFound(String proxyType, String proxyPropertyNotFound) {
        Object[] args = { proxyType, proxyPropertyNotFound };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, EXPECTED_PROXY_PROPERTY_NOT_FOUND, args));
        validationException.setErrorCode(EXPECTED_PROXY_PROPERTY_NOT_FOUND);
        return validationException;
    }    

    /**
     * PUBLIC:
     * Proxy property corresponding to the specified proxy type was not found.
     */
    public static ValidationException unknownProxyType(int unknownProxyType, String knownProxyType1, String knownProxyType2, String knownProxyType3) {
        Object[] args = { unknownProxyType, knownProxyType1, knownProxyType2, knownProxyType3 };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, UNKNOWN_PROXY_TYPE, args));
        validationException.setErrorCode(UNKNOWN_PROXY_TYPE);
        return validationException;
    }    
    
    /**
     * PUBLIC:
     * Proxy property corresponding to the specified proxy type was not found.
     */
    public static ValidationException  mapKeyCannotUseIndirection(DatabaseMapping mapping){
        Object[] args = { mapping };
        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, MAP_KEY_CANNOT_USE_INDIRECTION, args));
        validationException.setErrorCode(MAP_KEY_CANNOT_USE_INDIRECTION);
        return validationException;
    }    

    /**
     * PUBLIC:
     * Attempt to call setListOrderField method on a mapping that doesn't support listOrderField.
     */
    public static ValidationException listOrderFieldNotSupported(DatabaseMapping mapping) {
        Object[] args = { mapping };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, LIST_ORDER_FIELD_NOT_SUPPORTED, args));
        validationException.setErrorCode(LIST_ORDER_FIELD_NOT_SUPPORTED);
        return validationException;
    }

    /**
     * PUBLIC:
     * Attempt to call setListOrderField method on a mapping that doesn't support listOrderField.
     */
    public static ValidationException collectionRemoveEventWithNoIndex(DatabaseMapping mapping) {
        Object[] args = { mapping };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, COLLECTION_REMOVE_EVENT_WITH_NO_INDEX, args));
        validationException.setErrorCode(COLLECTION_REMOVE_EVENT_WITH_NO_INDEX);
        return validationException;
    }
    
    /**
     * PUBLIC:
     * Fetch group has an attribute that doesn't have corresponding mapping.
     */
    public static ValidationException fetchGroupHasUnmappedAttribute(AttributeGroup fetchGroup, String attributeName) {
        Object[] args = { fetchGroup,  attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, FETCH_GROUP_HAS_UNMAPPED_ATTRIBUTE, args));
        validationException.setErrorCode(FETCH_GROUP_HAS_UNMAPPED_ATTRIBUTE);
        return validationException;
    }

    /**
     * PUBLIC:
     * Fetch group has an attribute that references nested fetch group but the corresponding mapping is either not ForeignReferenceMapping or does not have reference descriptor.
     */
    public static ValidationException fetchGroupHasWrongReferenceAttribute(FetchGroup fetchGroup, String attributeName) {
        Object[] args = { fetchGroup,  attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, FETCH_GROUP_HAS_WRONG_REFERENCE_ATTRIBUTE, args));
        validationException.setErrorCode(FETCH_GROUP_HAS_WRONG_REFERENCE_ATTRIBUTE);
        return validationException;
    }

    /**
     * PUBLIC:
     * Fetch group has an attribute that references nested fetch group but target class does not support fetch groups..
     */
    public static ValidationException fetchGroupHasWrongReferenceClass(FetchGroup fetchGroup, String attributeName) {
        Object[] args = { fetchGroup,  attributeName };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, FETCH_GROUP_HAS_WRONG_REFERENCE_CLASS, args));
        validationException.setErrorCode(FETCH_GROUP_HAS_WRONG_REFERENCE_CLASS);
        return validationException;
    }

    /**
     * ValuePartitioning using the same value twice.
     */
    public static ValidationException duplicatePartitionValue(String policyName, Object value) {
        Object[] args = { policyName,  value };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, DUPLICATE_PARTITION_VALUE, args));
        validationException.setErrorCode(DUPLICATE_PARTITION_VALUE);
        return validationException;
    }

    public static ValidationException cannotAddSequencesToSessionBroker() {
        Object[] args = {  };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, CANNOT_ADD_SEQUENCES_TO_SESSION_BROKER, args));
        validationException.setErrorCode(CANNOT_ADD_SEQUENCES_TO_SESSION_BROKER);
        return validationException;
    }

    public static ValidationException sharedDescriptorAlias(String alias, String className1, String className2) {
        Object[] args = { alias, className1, className2 };

        ValidationException validationException = new ValidationException(ExceptionMessageGenerator.buildMessage(ValidationException.class, SHARED_DESCRIPTOR_ALIAS, args));
        validationException.setErrorCode(SHARED_DESCRIPTOR_ALIAS);
        return validationException;
    }
    
}
