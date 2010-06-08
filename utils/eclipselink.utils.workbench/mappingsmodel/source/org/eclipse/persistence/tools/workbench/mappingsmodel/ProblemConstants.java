/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel;

public final class ProblemConstants 
{
	// ********************************************************************
	// 		Rule Indices
	//	====================
	//	0100-0199 - Mappings Project Rules
	//  0200-0399 - Descriptor Rules
	//	0400-0699 - Mapping Rules
	//	0700-0799 - Table Rules
	//	0800-0899 - XML Schema Rules
	//	0900-.... - Sessions Rules
	// 
	// ********************************************************************
	
	
	// ********************************************************************
	//		Mappings Project Rules
	// ********************************************************************
	
	// ***	MWRelationalProject  ***
	public static final String PROJECT_CACHES_QUERY_STATEMENTS_WITHOUT_BINDING_PARAMETERS 	= "0100";
	public static final String PROJECT_NO_SEQUENCE_COUNTER_FIELD_SPECIFIED					= "0101";
	public static final String PROJECT_NO_SEQUENCE_NAME_FIELD_SPECIFIED						= "0102";
	
	
	// ********************************************************************
	//		Descriptor Rules 
	// ********************************************************************
	
	// ***	MWDescriptor  ***
	public static final String DESCRIPTOR_CLASS_NOT_PUBLIC									= "0200";
	public static final String DESCRIPTOR_CLASS_SUBCLASSES_FINAL_CLASS						= "0201";
	
	// ***	MWMappingDescriptor  ***
	public static final String DESCRIPTOR_CLASS_MULTIPLE_METHODS_WITH_SAME_SIGNATURE		= "0210";
	
	// ***	MWAggregateDescriptor ***
	public static final String DESCRIPTOR_SHARED_AGGREGATE_HAS_1_TO_M_OR_M_TO_M_MAPPINGS	= "0220";
	public static final String DESCRIPTOR_CLASSES_REFERENCE_AN_AGGREGATE_TARGET				= "0221";

	
	// ***	MWInterfacesDescriptor ***
	public static final String INTERFACE_DESCRIPTOR_IMPLEMENTOR_DOES_NOT_IMPLEMENT_INTERFACE	
																							= "0225";
	
	// ***	MWRelationalDescriptor  ***
	public static final String DESCRIPTOR_NO_PRIMARY_TABLE_SPECIFIED						= "0230";
	public static final String DESCRIPTOR_NO_PRIMARY_KEYS_SPECIFIED							= "0231";
	public static final String DESCRIPTOR_PRIMARY_KEY_FIELD_UNMAPPED						= "0232";
	public static final String DESCRIPTOR_PK_SIZE_DONT_MATCH								= "0233";
	public static final String DESCRIPTOR_PKS_DONT_MATCH_PARENT								= "0234";
	public static final String DESCRIPTOR_PRIMARY_KEY_MAPPING_READ_ONLY						= "0235";
	public static final String DESCRIPTOR_PRIMARY_KEY_MAPPING_DUPLICATED_IN_HIERARCHY		= "0226";
	
	public static final String DESCRIPTOR_NO_SEQUENCE_NUMBER_FIELD_SPECIFIED				= "0236";
	public static final String DESCRIPTOR_NO_SEQUENCE_NAME_SPECIFIED						= "0237";
	public static final String DESCRIPTOR_SEQUENCE_TABLE_NOT_SPECIFIED						= "0238";
	public static final String DESCRIPTOR_SEQUENCE_TABLE_NOT_VALID							= "0239";
			
	public static final String DESCRIPTOR_MULTIPLE_QUERIES_WITH_SAME_SIGNATURE				= "0240";
	public static final String DESCRIPTOR_QUERY_CACHES_STATEMENT_WITHOUT_BINDING_PARAMETERS	= "0241";
	public static final String DESCRIPTOR_QUERY_REFRESHES_REMOTE_IDENTITY_MAP_WITHOUT_MAINTAINING_CACHE
																							= "0242";
	public static final String DESCRIPTOR_QUERY_REFRESHES_IDENTITY_MAP_WITHOUT_MAINTAINING_CACHE
																							= "0243";
	public static final String DESCRIPTOR_QUERY_REFRESHES_IDENTITY_MAP_WITHOUT_REFRESHING_REMOTE_IDENTITY_MAP
																							= "0245";
	public static final String DESCRIPTOR_QUERY_KEY_NO_COLUMN_SPECIFIED				= "0246";
	public static final String DESCRIPTOR_QUERY_KEY_INVALID_COLUMN				= "0247";
	public static final String DESCRIPTOR_QUERY_EXPRESSION_NO_PARAMETER_SPECIFIED			= "0248";
    public static final String DESCRIPTOR_QUERY_EXPRESSION_NO_QUERY_KEY_SPECIFIED           = "0249";
    public static final String DESCRIPTOR_QUERY_EXPRESSION_QUERY_KEY_NOT_VALID           = "0250";
	public static final String DESCRIPTOR_QUERY_EXPRESSION_NON_UNARY_OPERATOR				= "0251";

	public static final String QUERYABLE_NULL_FOR_ORDERING_ITEM							= "0252";
	public static final String QUERYABLE_NOT_VALID_FOR_READ_ALL_QUERY_ORDERING_ITEM		= "0253";
	public static final String QUERYABLE_NULL_FOR_JOINED_ITEM								= "0254";
	public static final String QUERYABLE_NOT_VALID_FOR_READ_QUERY_JOINED_READ_ITEM		= "0255";
	public static final String QUERYABLE_NULL_FOR_BATCH_READ_ITEM							= "0256";
	public static final String QUERYABLE_NOT_VALID_FOR_READ_ALL_QUERY_BATCH_READ_ITEM		= "0257";
	public static final String QUERYABLE_NULL_FOR_GROUPING_ITEM							= "0258";
	public static final String QUERYABLE_NULL_FOR_REPORT_ITEM								= "0259";
	public static final String QUERYABLE_NOT_VALID_FOR_REPORT_QUERY_ATTRIBUTE              = "0260";
	
	public static final String STORED_PROCEDURE_INOUTARGUMENT_REQUIREMENTS					= "0261";

	public static final String LITERAL_ARGUMENT_ILLEGAL_BYTE_FORMAT	 						= "0262";
	public static final String LITERAL_ARGUMENT_ILLEGAL_BOOLEAN_FORMAT	 					= "0263";
	public static final String LITERAL_ARGUMENT_ILLEGAL_CHARACTER_FORMAT	 				= "0264";
	public static final String LITERAL_ARGUMENT_ILLEGAL_NUMBER_FORMAT	 					= "0265";
	public static final String LITERAL_ARGUMENT_ILLEGAL_STRING_FORMAT	 					= "0266";
	public static final String LITERAL_ARGUMENT_ILLEGAL_BIGDECIMAL_FORMAT	 				= "0267";
	public static final String LITERAL_ARGUMENT_ILLEGAL_BIGINTEGER_FORMAT	 				= "0268";
	public static final String LITERAL_ARGUMENT_ILLEGAL_SQLDATE_FORMAT	 					= "0269";
	public static final String LITERAL_ARGUMENT_ILLEGAL_SQLTIME_FORMAT	 					= "0211";
	public static final String LITERAL_ARGUMENT_ILLEGAL_SQLTIMESTAMP_FORMAT	 				= "0212";
	public static final String LITERAL_ARGUMENT_ILLEGAL_UTILDATE_FORMAT	 					= "0213";
	public static final String LITERAL_ARGUMENT_ILLEGAL_UTILCALENDAR_FORMAT	 				= "0214";
	public static final String LITERAL_ARGUMENT_ILLEGAL_BYTEARRAY_FORMAT	 				= "0215";
	public static final String LITERAL_ARGUMENT_ILLEGAL_CHARARRAY_FORMAT	 				= "0216";
	public static final String LITERAL_ARGUMENT_ILLEGAL_GENERIC_FORMAT	 					= "0217";

	// ***	MWXmlDescriptor  ***
	public static final String DESCRIPTOR_NO_SCHEMA_CONTEXT_SPECIFIED						= "0270";
	public static final String DESCRIPTOR_NO_DEFAULT_ROOT_ELEMENT_SPECIFIED					= "0271";
	public static final String DESCRIPTOR_MULTIPLE_MAPPINGS_WRITE_TO_XPATH					= "0272";
	
	// *** MWOXDescriptor ***
	public static final String DESCRIPTOR_ANY_TYPE_WITH_INHERITANCE							= "0280";
	public static final String DESCRIPTOR_ANY_TYPE_WITH_NON_ANY_MAPPINGS					= "0281";
	public static final String DESCRIPTOR_DEFAULT_ROOT_ELEMENT_TYPE							= "0282";
	
	// ***	MWEisDescriptor  ***
	public static final String EIS_DESCRIPTOR_NO_PRIMARY_KEYS_SPECIFIED						= "0290";
	public static final String EIS_ROOT_DESCRIPTOR_NO_WRITABLE_MAPPINGS_FOR_PRIMARY_KEYS	= "0354";
	
	// *** Advanced Policies ***
	
	// Events
	
	public static final String DESCRIPTOR_EVENTS_ABOUT_TO_INSERT							= "0291";
	public static final String DESCRIPTOR_EVENTS_ABOUT_TO_UPDATE							= "0292";
	public static final String DESCRIPTOR_EVENTS_PRE_DELETING								= "0293";
	public static final String DESCRIPTOR_EVENTS_PRE_INSERT									= "0294";
	public static final String DESCRIPTOR_EVENTS_PRE_UPDATE									= "0295";
	public static final String DESCRIPTOR_EVENTS_PRE_WRITING								= "0296";
	public static final String DESCRIPTOR_EVENTS_POST_BUILD									= "0297";
	public static final String DESCRIPTOR_EVENTS_POST_CLONE									= "0298";
	public static final String DESCRIPTOR_EVENTS_POST_DELETING								= "0299";
	public static final String DESCRIPTOR_EVENTS_POST_INSERT								= "0300";
	public static final String DESCRIPTOR_EVENTS_POST_MERGE									= "0301";
	public static final String DESCRIPTOR_EVENTS_POST_REFRESH								= "0302";
	public static final String DESCRIPTOR_EVENTS_POST_UPDATE								= "0303";
	public static final String DESCRIPTOR_EVENTS_POST_WRITING								= "0304";
	public static final String DESCRIPTOR_EVENTS_ABOUT_TO_INSERT_VALID						= "0358";
	public static final String DESCRIPTOR_EVENTS_ABOUT_TO_UPDATE_VALID						= "0359";
	public static final String DESCRIPTOR_EVENTS_PRE_DELETING_VALID							= "0360";
	public static final String DESCRIPTOR_EVENTS_PRE_INSERT_VALID							= "0361";
	public static final String DESCRIPTOR_EVENTS_PRE_UPDATE_VALID							= "0362";
	public static final String DESCRIPTOR_EVENTS_PRE_WRITING_VALID							= "0363";
	public static final String DESCRIPTOR_EVENTS_POST_BUILD_VALID							= "0364";
	public static final String DESCRIPTOR_EVENTS_POST_CLONE_VALID							= "0365";
	public static final String DESCRIPTOR_EVENTS_POST_DELETING_VALID						= "0366";
	public static final String DESCRIPTOR_EVENTS_POST_INSERT_VALID							= "0367";
	public static final String DESCRIPTOR_EVENTS_POST_MERGE_VALID							= "0368";
	public static final String DESCRIPTOR_EVENTS_POST_REFRESH_VALID							= "0369";
	public static final String DESCRIPTOR_EVENTS_POST_UPDATE_VALID							= "0370";
	public static final String DESCRIPTOR_EVENTS_POST_WRITING_VALID							= "0371";
	
	// Locking
	
	public static final String DESCRIPTOR_LOCKING_FIELD_WRITEABLE							= "0305";
	public static final String DESCRIPTOR_LOCKING_SELECTED_FIELDS_NOT_MAPPED				= "0306";
	public static final String DESCRIPTOR_LOCKING_SELECTED_FIELDS_ARE_PKS					= "0307";
	public static final String DESCRIPTOR_LOCKING_VERSION_LOCK_FIELD_NOT_SPECIFIED			= "0308";
	public static final String DESCRIPTOR_LOCKING_VERSION_LOCK_FIELD_NOT_VALID				= "0309";
	public static final String DESCRIPTOR_LOCKING_SELECTED_FIELDS_NOT_VALID					= "0310";
		
	// Instantiation
	
    public static final String DESCRIPTOR_INSTANTIATION_INSTANTIATION_METHOD_NOT_VISIBLE    = "0311";
    public static final String DESCRIPTOR_INSTANTIATION_INSTANTIATION_METHOD_NOT_VALID      = "0343";
    public static final String DESCRIPTOR_INSTANTIATION_FACTORY_INSTANTIATION_METHOD_NOT_VISIBLE
                                                                                            = "0312";
    public static final String DESCRIPTOR_INSTANTIATION_FACTORY_INSTANTIATION_METHOD_NOT_VALID
                                                                                            = "0344";
    public static final String DESCRIPTOR_INSTANTIATION_FACTORY_METHOD_NOT_VISIBLE          = "0313";
    public static final String DESCRIPTOR_INSTANTIATION_FACTORY_METHOD_NOT_VALID            = "0345";
	public static final String DESCRIPTOR_INSTANTIATION_USE_FACTORY							= "0314";
	public static final String DESCRIPTOR_INSTANTIATION_USE_METHOD							= "0315";
	public static final String DESCRIPTOR_INSTANTIATION_NO_ZERO_ARG_CONSTRUCTOR				= "0316";
	
	// Copying
	
	public static final String DESCRIPTOR_COPYING_NO_METHOD_SPECIFIED						= "0317";
    public static final String DESCRIPTOR_COPYING_METHOD_NOT_VISIBLE                        = "0318";
    public static final String DESCRIPTOR_COPYING_METHOD_NOT_VALID                          = "0346";
	
	// Multi-Table
	public static final String DESCRIPTOR_MULTI_TABLE_PKS_DONT_MATCH						= "0319";
	
	// After Loading
	public static final String DESCRIPTOR_AFTER_LOADING_CLASS_MUST_BE_SPECIFIED				= "0322";
	public static final String DESCRIPTOR_AFTER_LOADING_METHOD_MUST_BE_SPECIFIED			= "0323";
	public static final String DESCRIPTOR_AFTER_LOADING_METHOD_NOT_IN_SPECIFIED_CLASS		= "0342";
	public static final String DESCRIPTOR_AFTER_LOADING_METHOD_NOT_VALID					= "0372";
	
	// Interface Alias
	
	public static final String DESCRIPTOR_INTERFACE_ALIAS_INTERFACE_SPECIFIED				= "0324";
	
	
	// Inheritance
	
	public static final String DESCRIPTOR_TABLE_INHERITANCE_DESCRIPTOR_TYPES_DONT_MATCH		= "0325";
	public static final String DESCRIPTOR_EIS_INHERITANCE_DESCRIPTOR_TYPES_DONT_MATCH		= "0326";
	public static final String DESCRIPTOR_INHERITANCE_CLASS_EXTRACTION_METHOD_NOT_SPECIFIED	= "0327";
	public static final String DESCRIPTOR_INHERITANCE_CLASS_EXTRACTION_METHOD_NOT_VISIBLE	= "0328";
	public static final String DESCRIPTOR_INHERITANCE_CLASS_EXTRACTION_METHOD_NOT_VALID		= "0329";
	public static final String DESCRIPTOR_INHERITANCE_MAPPED_CHILD_DESCRIPTOR_INACTIVE		= "0355";
	public static final String DESCRIPTOR_INHERITANCE_PARENT_DESCRIPTOR_INACTIVE			= "0356";
	public static final String DESCRIPTOR_INHERITANCE_NO_PARENT								= "0357";
	
	// Returning Policy
	
	public static final String DESCRIPTOR_RETURNING_POLICY_INSERT_FIELD_NOT_VALID			= "0330";
	public static final String DESCRIPTOR_RETURNING_POLICY_UPDATE_FIELDS_NOT_VALID			= "0331";
	public static final String DESCRIPTOR_RETURNING_POLICY_INSERT_SEQUENCING_FIELD			= "0332";
	public static final String DESCRIPTOR_RETURNING_POLICY_UPDATE_SEQUENCING_FIELD			= "0333";
	public static final String DESCRIPTOR_RETURNING_POLICY_UPDATE_CLASS_INDICATOR_FIELD		= "0334";
	public static final String DESCRIPTOR_RETURNING_POLICY_INSERT_CLASS_INDICATOR_FIELD		= "0335";
	public static final String DESCRIPTOR_RETURNING_POLICY_INSERT_LOCKING_FIELD				= "0336";
	public static final String DESCRIPTOR_RETURNING_POLICY_UPDATE_LOCKING_FIELD				= "0337";			
	public static final String DESCRIPTOR_RETURNING_POLICY_INSERT_ONE_TO_ONE_FORIEGN_KEY	= "0338";
	public static final String DESCRIPTOR_RETURNING_POLICY_UPDATE_ONE_TO_ONE_FORIEGN_KEY	= "0339";
	public static final String DESCRIPTOR_RETURNING_POLICY_UPDATE_UNMAPPED					= "0340";
	public static final String DESCRIPTOR_RETURNING_POLICY_INSERT_UNMAPPED					= "0341";
	public static final String DESCRIPTOR_RETURNING_POLICY_NATIVE_RETURNING_NOT_SUPPORTED   = "0347";
	//don't use 0342 here, it is used above under "After Loading"
	
	// EJB Info
	public static final String DESCRIPTOR_EJB_PERSISTENCE_TYPE_INCOMPATIBLE					= "0350";
	public static final String DESCRIPTOR_UNKNOWN_PK_CLASS_INCONSISTENT_WITH_PARENT_DESCRIPTOR
																							= "0351";
	public static final String DESCRIPTOR_UNKNOWN_PK_CLASS_NOT_OBJECT 						= "0352";
	public static final String DESCRIPTOR_UNKNOWN_PK_DOESNT_USE_SEQUENCING 					= "0353";
	

	// ********************************************************************
	//		Mapping Rules 
	// ********************************************************************
	
	// *** MWMapping ***
	public static final String MAPPING_METHOD_ACCESSORS_NOT_SPECIFIED						= "0400";
	public static final String MAPPING_INVALID_GET_METHOD									= "0401";
	public static final String MAPPING_INVALID_SET_METHOD									= "0402";
	public static final String MAPPING_EJB_CMP_FIELD_USES_METHOD_ACCESSING					= "0403";
	public static final String MAPPING_REFERENCE_WRITE_LOCK_FIELD_NOT_READ_ONLY				= "0404";
	public static final String MAPPING_ATTRIBUTE_NO_LONGER_MAPPABLE							= "0405";
	
	// *** MWRelationalDirectContainerMapping ***
	/** for common errors, see also
	 		MWTableReferenceMapping,
	 		MWCollectionMapping,
	 		MWIndirectableMapping, and
	 		MWIndirectableCollectionMapping */
	public static final String MAPPING_DIRECT_VALUE_FIELD_NOT_SPECIFIED						= "0410";
	
	// *** MWRelationalDirectMapMapping ***
	public static final String MAPPING_DIRECT_KEY_FIELD_NOT_SPECIFIED						= "0415";
	
	// *** MWDirectMapping ***
	public static final String MAPPING_FIELD_NOT_SPECIFIED									= "0420";
	public static final String MAPPING_FIELD_NOT_VALID										= "0421";
	
	// *** MWDirectToXmlTypeMapping ***
	public static final String MAPPING_XML_TYPE_ON_NON_ORACLE_9i_PLATFORM					= "0440";
	public static final String MAPPING_XML_TYPE_WITH_INCORRECT_ATTRIBUTE_TYPE				= "0441";
	public static final String MAPPING_XML_TYPE_WITH_INCORRECT_DATABASE_TYPE				= "0442";
	
	// *** MWAbstractReferenceMapping ***
	public static final String MAPPING_REFERENCE_DESCRIPTOR_NOT_SPECIFIED					= "0450";
	public static final String MAPPING_REFERENCE_DESCRIPTOR_IS_INACTIVE						= "0451";
	public static final String MAPPING_CANNOT_ACCESS_ISOLATED_DESCRIPTOR						= "0452";
	
	// *** MWTableReferenceMapping ***
	public static final String MAPPING_TABLE_REFERENCE_NOT_SPECIFIED						= "0460";
	public static final String MAPPING_TABLE_REFERENCE_INVALID								= "0461";
	public static final String MAPPING_RELATIONSHIP_PARTNER_NOT_SPECIFIED					= "0464";
	public static final String MAPPING_RELATIONSHIP_PARTNER_INVALID							= "0465";
	public static final String MAPPING_RELATIONSHIP_PARTNER_NOT_MUTUAL						= "0466";
	public static final String MAPPING_REFERENCE_DESCRIPTOR_NOT_RELATIONAL_DESCRIPTOR		= "0467";
	public static final String MAPPING_REFERENCE_MAINTAINS_BIDI_BUT_NO_INDIRECTION		= "0468";
	 
	// *** MWCollectionMapping ***
	public static final String MAPPING_CONTAINER_CLASS_NOT_SPECIFIED = "0470";
	public static final String MAPPING_CONTAINER_CLASS_NOT_COLLECTION = "0471";
	public static final String MAPPING_CONTAINER_CLASS_NOT_MAP	 = "0472";
	public static final String MAPPING_CONTAINER_CLASS_NOT_INSTANTIABLE = "0473";
	public static final String MAPPING_CONTAINER_CLASS_DISAGREES_WITH_ATTRIBUTE = "0474";
	public static final String MAPPING_KEY_METHOD_NOT_SPECIFIED = "0475";
	public static final String MAPPING_KEY_METHOD_NOT_VISIBLE = "0476";
	public static final String MAPPING_KEY_METHOD_NOT_VALID = "0477";
	public static final String MAPPING_CANNOT_USE_VALUE_HOLDER_INDIRECTION = "0478";
	public static final String MAPPING_ORDERING_QUERY_KEY_NOT_SPECIFIED = "0479";
	public static final String MAPPING_COLLECTION_MAINTTAINSBIDIRECTIONAL_NO_TRANSPARENT_INDIRECTION = "0469";
	
	// *** MWManyToManyMapping ***
	/** for common errors, see also
	 		MWTableReferenceMapping */
	public static final String MAPPING_RELATION_TABLE_NOT_SPECIFIED							= "0480";
	public static final String MAPPING_RELATION_TABLE_NOT_DEDICATED							= "0481";
	public static final String MAPPING_SOURCE_TABLE_REFERENCE_NOT_SPECIFIED					= "0482";
	public static final String MAPPING_TARGET_TABLE_REFERENCE_NOT_SPECIFIED					= "0483";
	

	public static final String MAPPING_CONTAINER_CLASS_NOT_LIST 							= "0484";
	public static final String MAPPING_CONTAINER_CLASS_NOT_SET 								= "0485";
	public static final String MAPPING_CONTAINER_CLASS_NOT_SORTED_SET 						= "0486";
	public static final String USES_SORTING_NO_COMPARATOR_CLASS_SELECTED 					= "0487";
	public static final String COMPARATOR_CLASS_NOT_COMPARATOR								= "0488";

	// *** MWOneToManyMapping ***
	// none as of yet
	
	// *** MWOneToOneMapping ***
	public static final String MAPPING_BEAN_TO_BEAN_SHOULD_USE_VALUEHOLDER_INDIRECTION 		= "0500";
	
	// *** MWVariableOneToOneMapping ***
	public static final String MAPPING_QUERY_KEY_ASSOCIATIONS_NOT_SPECIFIED					= "0510";
	public static final String MAPPING_QUERY_KEY_ASSOCIATIONS_INCOMPLETE					= "0511";
	public static final String MAPPING_QUERY_KEY_ASSOCIATIONS_INVALID						= "0512";
	public static final String MAPPING_CLASS_INDICATOR_FIELD_NOT_SPECIFIED					= "0513";
	public static final String MAPPING_CLASS_INDICATOR_VALUES_INVALID						= "0515";
	public static final String MAPPING_REFERENCE_DESCRIPTOR_NOT_INTERFACE_DESCRIPTOR		= "0516";
	public static final String MAPPING_QUERY_KEY_ASSOCIATIONS_FIELD_INVALID             	= "0517";
	
	// *** MWTransformationMapping *** (including relational and xml)
	public static final String MAPPING_ATTRIBUTE_TRANSFORMER_NOT_SPECIFIED					= "0520";
	public static final String MAPPING_ATTRIBUTE_TRANSFORMER_CLASS_MISSING					= "0521";
	public static final String MAPPING_ATTRIBUTE_TRANSFORMER_CLASS_INVALID					= "0522";
	public static final String MAPPING_ATTRIBUTE_TRANSFORMER_METHOD_MISSING					= "0523";
	public static final String MAPPING_ATTRIBUTE_TRANSFORMER_METHOD_NOT_VISIBLE				= "0524";
	public static final String MAPPING_ATTRIBUTE_TRANSFORMER_METHOD_INVALID					= "0525";
	public static final String MAPPING_FIELD_TRANSFORMER_ASSOCIATIONS_NOT_SPECIFIED			= "0526";
	public static final String MAPPING_FIELD_TRANSFORMER_NOT_SPECIFIED						= "0527";
	public static final String MAPPING_FIELD_TRANSFORMER_FIELD_MISSING						= "0528";
	public static final String MAPPING_FIELD_TRANSFORMER_CLASS_MISSING						= "0529";
	public static final String MAPPING_FIELD_TRANSFORMER_CLASS_INVALID						= "0530";
	public static final String MAPPING_FIELD_TRANSFORMER_METHOD_MISSING						= "0531";
	public static final String MAPPING_FIELD_TRANSFORMER_METHOD_NOT_VISIBLE					= "0532";
	public static final String MAPPING_FIELD_TRANSFORMER_METHOD_INVALID						= "0533";
	public static final String MAPPING_FIELD_TRANSFORMER_NOT_VALID							= "0534";
	public static final String MAPPING_FIELD_TRANSFORMER_DUPLICATE_FIELD					= "0535";
	public static final String MAPPING_FIELD_TRANSFORMER_XPATH_MISSING						= "0536";
	public static final String MAPPING_FIELD_TRANSFORMER_XPATH_DUPLICATE					= "0537";
	
	
	// *** MWConverterMapping ***  Common to all mappings that use converters
	public static final String MAPPING_VALUE_PAIRS_NOT_SPECIFIED							= "0542";
	
	// *** MWRelationalTypeConversionConverter ***
	public static final String MAPPING_NTYPE_NOT_SUPPORTED_ON_PLATFORM						= "0545";
	public static final String MAPPING_ORACLE_SPECIFIC_TYPE_NOT_SUPPORTED_ON_PLATFORM		= "0546";
	
	// *** MWIndirectableMapping *** Common to all mappings that can use value holder indirection
	public static final String MAPPING_VALUE_HOLDER_ATTRIBUTE_WITHOUT_VALUE_HOLDER_INDIRECTION		
																							= "0550";
	public static final String MAPPING_VALUE_HOLDER_INDIRECTION_WITHOUT_VALUE_HOLDER_ATTRIBUTE		
																							= "0551";
	public static final String MAPPING_VALUE_HOLDER_INDIRECTION_WITH_TL_VALUE_HOLDER_ATTRIBUTE
																							= "0552";
	
	// *** MWIndirectableCollectionMapping *** Common to all mappings that can use transparent indirection
	public static final String MAPPING_CONTAINER_CLASS_INVALID_FOR_TRANSPARENT_INDIRECTION	= "0560";
	
	
	
	// *** MWAggregateMapping ***
	public static final String MAPPING_REFERENCE_DESCRIPTOR_NOT_AGGREGATE_DESCRIPTOR		= "0570";
	public static final String MAPPING_AGGREGATE_COLUMNS_NOT_SPECIFIED						= "0571";
	public static final String MAPPING_AGGREGATE_COLUMNS_NOT_UNIQUE							= "0572";
	public static final String MAPPING_AGGREGATE_COLUMNS_NOT_VALID							= "0573";
	
	
	// *** MWEisReferenceMapping ***
	public static final String MAPPING_REFERENCE_DESCRIPTOR_NOT_ROOT						= "0590";
	public static final String MAPPING_EIS_RELATIONSHIP_PARTNER_NOT_SPECIFIED				= "0591";
	public static final String MAPPING_EIS_RELATIONSHIP_PARTNER_INVALID						= "0592";
	public static final String MAPPING_EIS_RELATIONSHIP_PARTNER_NOT_MUTUAL					= "0593";
	public static final String MAPPING_SOURCE_XPATH_MISSING									= "0594";
	public static final String MAPPING_TARGET_XPATH_MISSING									= "0595";
	public static final String MAPPING_SOURCE_XPATH_DUPLICATE								= "0596";
	public static final String MAPPING_TARGET_XPATH_DUPLICATE								= "0597";
	
	
	// *** MWEisOneToManyMapping ***
	public static final String MAPPING_1_TO_M_FIELD_PAIRS_NOT_SPECIFIED						= "0600";
	public static final String MAPPING_FOREIGN_KEY_GROUPING_ELEMENT_NOT_SPECIFIED			= "0601";
	public static final String MAPPING_FOREIGN_KEY_NOT_CONTAINED_BY_GROUPING_ELEMENT		= "0602";
	public static final String MAPPING_SELECTION_INTERACTION_NOT_SPECIFIED					= "0603";
	public static final String MAPPING_DELETE_ALL_INTERACTION_SPECIFIED_BUT_NOT_PRIVATE_OWNED 
																							= "0604";
	
	// *** MWEisOneToOneMapping ***
	public static final String MAPPING_1_TO_1_FIELD_PAIRS_NOT_SPECIFIED						= "0610";
	public static final String MAPPING_REFERENCE_DESCRIPTOR_READ_INTERACTION_NOT_SPECIFIED	= "0611";
	public static final String MAPPING_NONCORRESPONDING_TARGET_KEY							= "0612";
	
	// *** MWAbstractAnyMapping ***
	public static final String MAPPING_XPATH_SPECIFIED_IN_ANY_TYPE_DESCRIPTOR				= "0620";
	public static final String MAPPING_WILDCARD_SPECIFIED_IN_ANY_TYPE_DESCRIPTOR			= "0621";
	public static final String MAPPING_NO_WILDCARD_IN_SCHEMA_CONTEXT						= "0622";
	public static final String MAPPING_MAPS_TO_NON_ATTRIBUTES								= "0623";

	// *** MWAnyAttributeAMapping ***
	public static final String MAPPING_ATTRIBUTE_NOT_ASSIGNABLE_TO_MAP						= "0624";

	// *** MWMixedXmlContentMapping ***
	public static final String MAPPING_ATTRIBUTE_NOT_ASSIGNABLE_TO_COLLECTION				= "0625";
	
	// *** MWAbstractXmlReferenceMapping ***
	public static final String MAPPING_TARGET_NOT_PRIMARY_KEY_ON_REFERENCE_DESCRIPTOR		= "0626";
	public static final String MAPPING_NO_XML_FIELD_PAIRS_SPECIFIED							= "0627";
	
	// *** MWAbstractCompositeMapping ***
	public static final String MAPPING_CONTAINER_ACCESSOR_NOT_CONFIGURED					= "0630";
	public static final String MAPPING_CONTAINER_ACCESSOR_ATTRIBUTE_NOT_SELECTED			= "0631";
	public static final String MAPPING_CONTAINER_ACCESSOR_GET_METHOD_NOT_SELECTED			= "0632";
	public static final String MAPPING_CONTAINER_ACCESSOR_SET_METHOD_NOT_SELECTED			= "0633";

	// ********************************************************************
	//		tables, fields, references, etc.
	// ********************************************************************

	public static final String TABLE_TOO_MANY_IDENTITY_COLUMNS								= "0701";
	public static final String COLUMN_SIZE_REQUIRED											= "0702";
	public static final String REFERENCE_NO_COLUMN_PAIRS									= "0703";
	public static final String INCOMPLETE_COLUMN_PAIR										= "0704";
	public static final String REFERENCE_NO_TARGET_TABLE									= "0707";

	public static final String LOGIN_DRIVER_CLASS_NOT_SPECIFIED								= "0720";
	public static final String LOGIN_URL_NOT_SPECIFIED										= "0721";
	
	// ********************************************************************
	//		Schema Rules 
	// ********************************************************************
	
	// *** MWXmlSchema ***
	public static final String SCHEMA_NAMESPACE_PREFIX_NOT_SPECIFIED						= "0800";
	public static final String SCHEMA_NAMESPACE_PREFIX_DUPLICATED							= "0801";
	public static final String SCHEMA_NAMESPACE_PREFIX_CONTAINS_SPACE						= "0802";
	
	// *** MWXpath ***
	// (Not sure where else to put this)
	public static final String XPATH_NOT_SPECIFIED											= "0810";
	public static final String XPATH_NOT_RESOLVED											= "0811";
	public static final String XPATH_NOT_VALID_TEXT											= "0812";
	public static final String XPATH_NOT_VALID_POSITION										= "0813";
	public static final String XPATH_NOT_DIRECT												= "0814";
	public static final String XPATH_NOT_SINGULAR											= "0815";

	public static final String GET_METHOD_VS_ATTRIBUTE_MISMATCH = "0901";
	public static final String GET_METHOD_PARMS_SIZE_INVALID = "0902";
	public static final String SET_METHOD_VS_ATTRIBUTE_MISMATCH = "0903";
	public static final String SET_METHOD_PARMS_SIZE_INVALID = "0904";
	public static final String VALUE_GET_METHOD_VS_ATTRIBUTE_MISMATCH = "0905";
	public static final String VALUE_GET_METHOD_PARMS_SIZE_INVALID = "0906";
	public static final String VALUE_SET_METHOD_VS_ATTRIBUTE_MISMATCH = "0907";
	public static final String VALUE_SET_METHOD_PARMS_SIZE_INVALID = "0908";
	public static final String ADD_METHOD_VS_ATTRIBUTE_MISMATCH = "0909";
	public static final String ADD_METHOD_PARMS_SIZE_INVALID = "0910";
	public static final String MAP_ADD_METHOD_VS_ATTRIBUTE_MISMATCH = "0911";
	public static final String MAP_ADD_METHOD_PARMS_SIZE_INVALID = "0912";
	public static final String REMOVE_METHOD_VS_ATTRIBUTE_MISMATCH = "0913";
	public static final String REMOVE_METHOD_PARMS_SIZE_INVALID = "0914";
	public static final String MAP_REMOVE_METHOD_VS_ATTRIBUTE_MISMATCH = "0915";
	public static final String MAP_REMOVE_METHOD_PARMS_SIZE_INVALID = "0916";


	public static final String CLASS_INDICATOR_FOR_ABSTRACT_CLASS = "0013";
	public static final String NO_CLASS_INDICATOR_FOR_ROOT_CLASS = "0054";
	public static final String NO_CLASS_INDICATOR_FOR_INCLUDED_CLASS = "0055";
	public static final String NO_ROOT_CLASS_INDICATOR_MAPPING_FOR_CLASS = "0089";

	public static final String MULTIPLE_MAPPINGS_WRITE_TO_COLUMN = "0106";

	public static final String MISSING_INHERITANCE_POLICY_IN_PARENT_DESCRIPTOR = "0118";

	public static final String NO_INDICATOR_MAPPINGS = "0123";
	public static final String WRITABLE_MAPPING_FOR_CLASS_INDICATOR_FIELD = "0126";

	public static final String IMPLEMENTED_INTERFACE_NOT_AN_INTERFACE = "0132";
	public static final String SUPERCLASS_IS_AN_INTERFACE = "0133";

}
