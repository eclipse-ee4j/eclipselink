/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

public class UiMappingBundle extends java.util.ListResourceBundle {
	
	/**
	 * The string contents of the bundle
	 */
	static final Object[][] contents = {
		
		// *** MappingSelectionActionsPolicy ***
		{"CHANGE_MAPPING_TYPE_MENU", "Ma&p as"},
		{"REMOVE_MAPPING_ACTION_DIALOG.title", "Remove Mappings"},
		{"REMOVE_MAPPING_ACTION_DIALOG.message", "Are you sure you want to remove the selected mappings?{0}{0}(Note:  Unless the mapping is inherited, this will also remove the attribute from the associated class.  Use \"Map As...\" > \"Unmapped\" otherwise.)"},
	
		// *** MapAsUnmappedAction ***
		{"MAP_AS_UNMAPPED_ACTION", "&Unmapped"},
		{"MAP_AS_UNMAPPED_ACTION.toolTipText", "Unmap Attribute"},
		
		{"MAP_AS_TRANSFORMATION_ACTION", "Trans&formation"},
		{"MAP_AS_TRANSFORMATION_ACTION.toolTipText", "Map Attribute as Transformation"},
		
		// Accessibility
		{"ACCESSIBLE_AGGREGATE_MAPPING_NODE",       		"Aggregate Mapping {0}"},
		{"ACCESSIBLE_ANY_OBJECT_MAPPING_NODE", 				"Any Object Mapping {0}"},
		{"ACCESSIBLE_ANY_COLLECTION_MAPPING_NODE",     		"Any Collection Mapping {0}"},
		{"ACCESSIBLE_COMPOSITE_COLLECTION_MAPPING_NODE",	"Composite Collection Mapping {0}"},
		{"ACCESSIBLE_COMPOSITE_OBJECT_MAPPING_NODE",  		"Composite Object Mapping {0}"},
		{"ACCESSIBLE_DIRECT_COLLECTION_MAPPING_NODE", 		"Direct Collection Mapping {0}"},
		{"ACCESSIBLE_DIRECT_MAP_MAPPING_NODE",				"Direct Map Mapping {0}"},
		{"ACCESSIBLE_DIRECT_TO_FIELD_MAPPING_NODE",			"Direct To Field Mapping {0}"},
		{"ACCESSIBLE_DIRECT_TO_XML_MAPPING_NODE",			"Direct To XML Type Mapping {0}"},
		{"ACCESSIBLE_MANY_TO_MANY_MAPPING_NODE",			"Many To Many Mapping {0}"},
		{"ACCESSIBLE_OBJECT_TYPE_MAPPING_NODE",				"Object Type Mapping {0}"},
		{"ACCESSIBLE_ONE_TO_MANY_MAPPING_NODE",				"One To Many Mapping {0}"},
		{"ACCESSIBLE_ONE_TO_ONE_MAPPING_NODE",				"One To One Mapping {0}"},
		{"ACCESSIBLE_SERIALIZED_MAPPING_NODE",				"Serialized Mapping {0}"},
		{"ACCESSIBLE_TRANSFORMATION_MAPPING_NODE",			"Transformation Mapping {0}"},
		{"ACCESSIBLE_TYPE_CONVERSION_MAPPING_NODE",			"Type Conversion Mapping {0}"},
		{"ACCESSIBLE_VARIABLE_ONE_TO_ONE_MAPPING_NODE",		"Variable One To One Mapping {0}"},
		{"ACCESSIBLE_UNMAPPED_MAPPING_NODE",				"Unmapped {0}"},
		{"ACCESSIBLE_XML_DIRECT_COLLECTION_MAPPING_NODE",	"XML Direct Collection Mapping {0}"},
		{"ACCESSIBLE_XML_DIRECT_MAPPING_NODE",				"XML Direct Mapping {0}"},
		{"ACCESSIBLE_XML_TRANSFORMATION_MAPPING_NODE",		"XML Transformation Mapping {0}"},

		// *** DirectMappingPropertiesPage ***
		{"GENERAL_TAB", "General"},
		{"DIRECT_MAPPING_CONVERTER_TAB" , "Converter"},
		
		//MethodAccessingPanel
		{"USE_METHOD_ACCESSING_CHECK_BOX", "&Method Accessing"},
		{"SET_METHOD_LABEL", "S&et Method:"},
		{"GET_METHOD_LABEL", "&Get Method:"},
		{"GET_METHOD_LIST_BROWSER_DIALOG.title", "Select a Get Method"},
		{"GET_METHOD_LIST_BROWSER_DIALOG.listLabel", "&Methods:"},
		{"SET_METHOD_LIST_BROWSER_DIALOG.title", "Select a Set Method"},
		{"SET_METHOD_LIST_BROWSER_DIALOG.listLabel", "&Methods:"},
		
		// ContainerAccessorPanel
		{"USE_CONTAINER_ACCESSOR_CHECK_BOX", "&Use Container Accessor"},
		{"CONTAINER_ACCESSOR_LABEL", "Container &Accessor:"},
		{"CONTAINER_ACCESSOR_EDIT_BUTTON", "&Edit..."},
		{"ACCESSOR_PANEL_METHODS_BUTTON", "Use Accessor Get/Set &Methods"},
		{"ACCESSOR_PANEL_GET_METHOD_CHOOSER", "&Get Method:"},
		{"ACCESSOR_PANEL_SET_METHOD_CHOOSER", "&Set Method:"},
		{"ACCESSOR_PANEL_ATTRIBUTE_BUTTON", "Use &Attribute"},
		{"ACCESSOR_PANEL_ATTRIBUTE_CHOOSER", "At&tribute:"},
		{"CONTAINER_ACCESSOR_EDITING_DIALOG_TITLE", "Specify Accessor"},
		{"ACCESSOR_EDITING_DIALOG.NULL_ACCESSOR_ERROR", "Accessor type is not specified."},
		{"ACCESSOR_EDITING_DIALOG.NULL_ACCESSOR_ATTRIBUTE_ERROR", "Accessor attribute is not specified."},
		{"ACCESSOR_EDITING_DIALOG.NULL_ACCESSOR_GET_METHOD_ERROR", "Accessor get method is not specified"},
		{"ACCESSOR_EDITING_DIALOG.NULL_ACCESSOR_SET_METHOD_ERROR", "Accessor set method is not specified"},
		
		// NullValuePolicyPanel
		{"DEFAULT_NULL_VALUE_CHECK_BOX", "Def&ault Null Value"},
		{"DEFAULT_NULL_VALUE_TYPE_LABEL", "Ty&pe:"},
		{"DEFAULT_NULL_VALUE_VALUE_LABEL", "&Value:"},
		{"NULL_VALUE_TYPE_LIST_BROWSER_DIALOG.title", "Select a Null Value Type"},
		{"NULL_VALUE_TYPE_LIST_BROWSER_DIALOG.listLabel", "&Types:"},
		
		// ContainerPolicyPanel
		{"CONTAINER_POLICY_GROUP_BOX",              				"Container Type"},
		{"CONTAINER_POLICY_COLLECTION_CLASS_RADIO_BUTTON", 	"&Collection"},
		{"CONTAINER_POLICY_LIST_CLASS_RADIO_BUTTON",          "&List"},
		{"CONTAINER_POLICY_MAP_CLASS_RADIO_BUTTON",        	"&Map"},
		{"CONTAINER_POLICY_SET_CLASS_RADIO_BUTTON",           "&Set"},
		{"CONTAINER_POLICY_OVERRIDE_DEFAULT_CLASS_CHECK_BOX", "&Override Default Class:"},
		{"CONTAINER_POLICY_KEY_METHOD_CHOOSER",            	"&Key Method:"},
		{"CONTAINER_POLICY_USE_SORTING_CHECK_BOX",			"&Use Sorting (select Comparator class):"},
		
		// CollectionContainerPolicyPanel
		{"COLLECTION_CONTAINER_POLICY_GROUP_BOX",              				"Container Type"},
		{"COLLECTION_CONTAINER_POLICY_OVERRIDE_DEFAULT_CLASS_CHECK_BOX",	"&Override Default Class:"},
		
		// ConverterPanel
		{"NO_CONVERTER_RADIO_BUTTON", "N&o Converter"},
		{"SERIALIZED_OBJECT_CONVERTER_RADIO_BUTTON", "Seria&lized Object Converter"},
		{"TYPE_CONVERSION_CONVERTER_RADIO_BUTTON", "Type &Conversion Converter"},	
		{"OBJECT_TYPE_CONVERTER_RADIO_BUTTON", "Ob&ject Type Converter"},
		
		// ObjectTypeConverterPanel
		{"DATA_TYPE_LABEL", "&Data Type:"},
		{"ATTRIBUTE_TYPE_LABEL", "&Attribute Type:"},
		{"CONVERSION_VALUES_TABLE_LABEL", "Conversion Values:"},
		{"DATA_VALUE_HEADER", "Data Value"},
		{"ATTRIBUTE_VALUE_HEADER", "Attribute Value"},
		{"DEFAULT_ATTRIBUTE_VALUE_HEADER", "Default Attribute Value"},
		{"ADD_VALUE_PAIRS_BUTTON", "Add..."},
		{"REMOVE_VALUE_PAIRS_BUTTON", "Remove"},
		{"EDIT_VALUE_PAIRS_BUTTON", "Edit..."},
		{"DATA_TYPE_LIST_BROWSER_DIALOG.title", "Select a Data Type"},
		{"DATA_TYPE_LIST_BROWSER_DIALOG.listLabel", "&Types:"},
		{"ATTRIBUTE_TYPE_LIST_BROWSER_DIALOG.title", "Select an Attribute Type"},
		{"ATTRIBUTE_TYPE_LIST_BROWSER_DIALOG.listLabel", "&Types:"},
				
		
		// ObjectTypeMappingPanel.ConversionValueDialog
		{"CONVERSION_VALUE_DIALOG_ADD.title", "Enter Conversion Value Pair"},
		{"CONVERSION_VALUE_DIALOG_EDIT.title", "Edit Conversion Value Pair"},
		
		{"CONVERSION_VALUE_DIALOG.DATA_TYPE_LABEL", "Data Type:"},
		{"CONVERSION_VALUE_DIALOG.DATA_VALUE_LABEL", "Data Value:"},
		{"CONVERSION_VALUE_DIALOG.ATTRIBUTE_TYPE_LABEL", "Attribute Type:"},
		{"CONVERSION_VALUE_DIALOG.ATTRIBUTE_VALUE_LABEL", "Attribute Value:"},
		
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_FORMAT_DIALOG.title", "Illegal Entry"},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_BOOLEAN_FORMAT", "The entry for {0} must be either 'true' or 'false'."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_BYTE_FORMAT", "The entry for {0} must be between 0 and 127 inclusive."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_CHARACTER_FORMAT", "The entry for {0} must be a single character."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_NUMBER_FORMAT", "The entry for {0} must be between {1} and {2}."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_NUMBER_CLASS_FORMAT", "The entry for {0} must be a number."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_FLOAT_NUMBER_FORMAT", "The entry for {0} must be between {1,number,0.###E0} and {2,number,0.###E0}."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_STRING_FORMAT", "The entry for {0} must be a string."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_BIGDECIMAL_FORMAT", "The entry for {0} must contain only digits, '-', and '.'."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_BIGINTEGER_FORMAT", "The entry for {0} must contain only digits and '-'."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_SQLDATE_FORMAT", "The entry for {0} must be in the format YYYY/MM/DD or YYYY-MM-DD."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_SQLTIME_FORMAT", "The entry for {0} must be in the format HH-MM-SS or HH:MM:SS."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_SQLTIMESTAMP_FORMAT", "The entry for {0} must be in the format YYYY/MM/DD HH:MM:SS or YYYY-MM-DD HH:MM:SS."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_UTILDATE_FORMAT", "The entry for {0} must be in the format YYYY/MM/DD or YYYY-MM-DD."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_UTILCALENDAR_FORMAT", "The entry for {0} must be in the format YYYY/MM/DD HH:MM:SS, YYYY/MM/DD, or YYYY-MM-DD."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_BYTEARRAY_FORMAT", "The entry for {0} must be an even length HEX string."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_CHARARRAY_FORMAT", "The entry for {0} must be a string."},
		{"CONVERSION_VALUE_DIALOG.ILLEGAL_GENERIC_FORMAT", "Illegal entry"},
		
		{"CONVERSION_VALUE_DIALOG.REPEATED_DATA_VALUE_DIALOG.title", "Repeated Data Value"},
		{"CONVERSION_VALUE_DIALOG.REPEATED_DATA_VALUE_DIALOG.message", "The data value \"{0}\" already exists.  Enter another value."},
		{"CONVERSION_VALUE_DIALOG.REPEATED_OBJECT_VALUE_DIALOG.title", "Repeated Attribute Value"},
		{"CONVERSION_VALUE_DIALOG.REPEATED_OBJECT_VALUE_DIALOG.message", "The attribute value \"{0}\" already exists.  Enter another value."},			
		
		
		// *** AttributeTransformerPanel ***
		{"ATTRIBUTE_TRANSFORMER_LABEL", "&Attribute Transformer:"},
		{"ATTRIBUTE_TRANSFORMER_EDIT_BUTTON", "Edit..."},
		
		// *** FieldTransformerAssociationsPanel ***
		{"TRANSFORMATION_MAPPING_FIELD_TRANSFORMER_ASSOCIATIONS_PANEL", "&Field Transformer Associations"},
		{"TRANSFORMATION_MAPPING_FIELD_COLUMN_LABEL", "Field"},
		{"TRANSFORMATION_MAPPING_TRANSFORMER_COLUMN_LABEL", "Transformer"},
		{"TRANSFORMATION_MAPPING_FIELD_TRANSFORMER_ADD_BUTTON", "Add..."},
		{"TRANSFORMATION_MAPPING_FIELD_TRANSFORMER_EDIT_BUTTON", "Edit..."},
		{"TRANSFORMATION_MAPPING_FIELD_TRANSFORMERS_REMOVE_BUTTON", "Remove"},
		
		// *** TransformerCellRendererAdapter ***
		{"TRANSFORMER_METHOD_MISSING_TEXT", "<Transformation method missing ...>"},
		{"TRANSFORMER_CLASS_MISSING_TEXT", "<Transformer class missing ...>"},
		{"TRANSFORMER_METHOD_TOOLTIP", "Transformation method: {0}"},
		{"TRANSFORMER_CLASS_TOOLTIP", "Transformer class: {0}"},
		
		// *** ContainerAccessorRendererAdapter ***
		{"ACCESSOR_METHODS_MISSING_TEXT", "<Container Accessor methods missing ...>"},
		{"ACCESSOR_ATTRIBUTE_MISSING_TEXT", "<Container Accessor attribute missing ...>"},
		{"ACCESSOR_METHODS_TOOLTIP", "Container Accessor methods: {0}"},
		{"ACCESSOR_ATTRIBUTE_TOOLTIP", "Container Accessor attribute: {0}"},

		// *** TransformerEditingPanel ***
		{"TRANSFORMER_PANEL_METHOD_BUTTON", "&Use Transformation Method"},
		{"TRANSFORMER_PANEL_METHOD_CHOOSER", "&Method: "},
		{"TRANSFORMER_PANEL_CLASS_BUTTON", "Use &Transformer Class"},
		{"TRANSFORMER_PANEL_CLASS_CHOOSER", "&Class: "},
		
		// *** FieldTransformerAssociationEditingPanel ***
		{"FIELD_TRANSFORMER_ASSOCIATION_PANEL_TRANSFORMER_PANEL", "Transformer"},
		
		// *** TransformerEditingDialog ***
		{"TRANSFORMER_EDITING_DIALOG.TITLE", "Specify Transformer"},
		{"TRANSFORMER_EDITING_DIALOG.NULL_TRANSFORMER_ERROR", "Transformer type is not specified."},
		{"TRANSFORMER_EDITING_DIALOG.NULL_TRANSFORMER_CLASS_ERROR", "Transformer class is not specified."},
		{"TRANSFORMER_EDITING_DIALOG.NULL_TRANSFORMATION_METHOD_ERROR", "Transformation method is not specified."},
		{"TRANSFORMER_EDITING_DIALOG.INVALID_TRANSFORMATION_METHOD_WARNING", "Method is not a valid transformation method."},
		
		// *** FieldTransformerAssociationEditingDialog ***
		{"FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.TITLE", "Specify Field-Transformer Association"},
		{"FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.NULL_TRANSFORMER_ERROR", "Transformer type is not specified."},
		{"FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.NULL_TRANSFORMER_CLASS_ERROR", "Transformer class is not specified."},
		{"FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.NULL_TRANSFORMATION_METHOD_ERROR", "Transformation method is not specified."},
		{"FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.INVALID_TRANSFORMATION_METHOD_WARNING", "Method is not a valid transformation method."},
		
		// join fetching choices
		{ "JOIN_FETCH_OPTION_NONE",         "None" },
		{ "JOIN_FETCH_OPTION_INNER",       	"Inner" },
		{ "JOIN_FETCH_OPTION_OUTER",     	"Outer" },
		
		// IndirectableCollectionPanel
		{"INDIRECTABLE_COLLECTION_TRANSPARENT_RADIO_BUTTON",  "Tr&ansparent"},
		{"INDIRECTABLE_COLLECTION_VALUE_HOLDER_RADIO_BUTTON", "&ValueHolder"},
		{"INDIRECTABLE_COLLECTION_USE_INDIRECTION_CHECK_BOX", "Use &Indirection"},
		
        //ProxyIndirectionPanel
        {"PROXY_INDIRECTION_PANEL_VALUE_HOLDER_RADIO_BUTTON", "&ValueHolder"},
        {"PROXY_INDIRECTION_PANEL_PROXY_RADIO_BUTTON", "&Proxy"},
       
        
		//MappingComponentFactory
		{"REFERENCE_DESCRIPTOR_CHOOSER_LABEL", "Reference &Descriptor:"},
		{"REFERENCE_DESCRIPTOR_LIST_BROWSER_DIALOG.title", "Select a Reference Descriptor"},
		{"REFERENCE_DESCRIPTOR_LIST_BROWSER_DIALOG.listLabel", "Reference &Descriptors:"},
		{"MAPPING_PRIVATE_OWNED_CHECK_BOX", "&Private Owned"},
		{"MAPPING_READ_ONLY_CHECK_BOX", "&Read-Only"},
		{"MAPPING_USE_INDIRECTION_CHECK_BOX", "Use &Indirection"},
		{"COLLECTION_OPTIONS_ADVANCED_BUTTON", "Ad&vanced Container Options"},
		{"JOIN_FETCHING_CHOOSER_LABEL", "&Join Fetch: "},
	};

	/**
	 * Returns the initialized array of keys and values that
	 * represents the strings used by the classes in the descriptor
	 * package.
	 *
	 * @return An table where the first element is the key used to
	 * retrieve the second element, which is the value
	 */
	public Object[][] getContents() {
		return contents;
	}
}
