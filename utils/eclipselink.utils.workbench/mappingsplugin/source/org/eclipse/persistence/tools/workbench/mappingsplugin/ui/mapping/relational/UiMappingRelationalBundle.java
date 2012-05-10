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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

public class UiMappingRelationalBundle extends java.util.ListResourceBundle {
	
	/**
	 * The string contents of the bundle
	 */
	static final Object[][] contents = {
	
		//MapAsXXXAction 
		{"MAP_AS_DIRECT_TO_FIELD_ACTION", "&Direct to Field"},
		{"MAP_AS_DIRECT_TO_FIELD_ACTION.toolTipText", "Map Attribute as Direct To Field"},
		
		{"MAP_AS_TYPE_CONVERSION_ACTION", "&Type Conversion"},
		{"MAP_AS_TYPE_CONVERSION_ACTION.toolTipText", "Map Attribute as Type Conversion"},
		
		{"MAP_AS_SERIALIZED_OBJECT_ACTION", "&Serialized Object"},
		{"MAP_AS_SERIALIZED_OBJECT_ACTION.toolTipText", "Map Attribute as Serialized Object"},
		
		{"MAP_AS_OBJECT_TYPE_ACTION", "&Object Type"},
		{"MAP_AS_OBJECT_TYPE_ACTION.toolTipText", "Map Attribute as Object Type"},
		
		{"MAP_AS_DIRECT_TO_XML_TYPE_ACTION", "Direct to &XML Type"},
		{"MAP_AS_DIRECT_TO_XML_TYPE_ACTION.toolTipText", "Map Attribute as Direct To XML Type"},
		
		{"MAP_AS_DIRECT_COLLECTION_ACTION", "Direct &Collection"},
		{"MAP_AS_DIRECT_COLLECTION_ACTION.toolTipText", "Map Attribute as Direct Collection"},
		
		{"MAP_AS_DIRECT_MAP_ACTION", "Direct &Map"},
		{"MAP_AS_DIRECT_MAP_ACTION.toolTipText", "Map Attribute as Direct Map"},
		
		{"MAP_AS_AGGREGATE_ACTION", "&Aggregate"},
		{"MAP_AS_AGGREGATE_ACTION.toolTipText", "Map Attribute as Aggregate"},

		{"MAP_AS_ONE_TO_ONE_ACTION", "&One To One"},
		{"MAP_AS_ONE_TO_ONE_ACTION.toolTipText", "Map Attribute as One to One"},

		{"MAP_AS_VARIABLE_ONE_TO_ONE_ACTION", "&Variable One to One"},
		{"MAP_AS_VARIABLE_ONE_TO_ONE_ACTION.toolTipText", "Map Attribute as Variable One to One"},
				
		{"MAP_AS_ONE_TO_MANY_ACTION", "O&ne To Many"},
		{"MAP_AS_ONE_TO_MANY_ACTION.toolTipText", "Map Attribute as One to Many"},

		{"MAP_AS_MANY_TO_MANY_ACTION", "&Many To Many"},
		{"MAP_AS_MANY_TO_MANY_ACTION.toolTipText", "Map Attribute as Many to Many"},

		//DirectToFieldMappingPanel
		{"DATABASE_FIELD_CHOOSER_LABEL", "&Database Field:"},
		{"DATABASE_FIELD_LIST_BROWSER_DIALOG.title", "Select a Database Field"},
		{"DATABASE_FIELD_LIST_BROWSER_DIALOG.listLabel", "&Database Fields:"},

		//DirectToXmlTypeMappingPropertiesPage
		{"READ_WHOLE_DOCUMENT_CHECK_BOX", "Read Wh&ole Document"},
		
		// *** RelationalFieldTransformerAssociationEditingPanel ***
		{"FIELD_TRANSFORMER_ASSOCIATION_PANEL_FIELD_CHOOSER", "&Field: "},
		
		// *** RelationalFieldTransformerAssociationDialog ***
		{"FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.NULL_FIELD_ERROR", "Field is not specified."},
		{"FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.DUPLICATE_FIELD_WARNING", "Field has a transformer already associated."},
		
		
		//VariableOneToOneMappingTabbedPropertiesPage
		{"VARIABLE_ONE_TO_ONE_GENERAL_TAB", "General"},	
		{"VARIABLE_ONE_TO_ONE_QUERY_KEY_ASSOCIATIONS_TAB", "Query Key Associations"},	
		{"VARIABLE_ONE_TO_ONE_CLASS_INDICATOR_INFO_TAB", "Class Indicator Info"},	
			
		//VariableOneToOneClassIndicatorsPanel
		{"VARIABLE_ONE_TO_ONE_CLASS_INDICATOR_FIELD_LABEL", "Class &Indicator Field:"},
		
		
		//VariableOneToOneQueryKeyAssociationsPanel
		{"FIELD_COLUMN_HEADER", "Foreign Key"},		
		{"QUERY_KEY_NAME_COLUMN_HEADER", "Query Key Name"},
		{"ADD_ASSOCIATION_BUTTON_TEXT", "&Add"},
		{"REMOVE_ASSOCIATION_BUTTON_TEXT", "&Remove"},
		{"REMOVE_FIELD_ASSOCIATIONS_WARNING_DIALOG.title", "Remove Selected Query Key Associations"},
		{"REMOVE_FIELD_ASSOCIATIONS_WARNING_DIALOG.message", "Are you sure you want to delete the selected query key asociations?"},
						
		
		//AggregateMappingTabbedPropertiesPage		
		{"AGGREGATE_MAPPING_GENERAL_TAB", "General"},
		{"AGGREGATE_MAPPING_FIELDS_TAB", "Fields"},
	

		//AggregateMappingPanel
		{"AGGREGATE_MAPPING_ALLOWS_NULL_CHECK_BOX", "&Allows Null"},
				
		//AggregateMappingFieldsPanel
		{"FIELD_DESCRIPTION_COLUMN_HEADER", "Field Description"},
		{"FIELDS_COLUMN_HEADER", "Fields"},
		{"AGGREGATE_FIELD_DESCRIPTION_FOR_USER_DEFINED_QUERY_KEY", ", field used for query key \"{0}\""},
		{"AGGREGATE_FIELD_DESCRIPTION_FOR_CLASS_BASED_TRANSFORMER", ", field set by transformer class {0}"},
		{"AGGREGATE_FIELD_DESCRIPTION_FOR_METHOD_BASED_TRANSFORMER", ", field set by method {0}"},
		{"AGGREGATE_FIELD_DESCRIPTION_FOR_DIRECT_MAPPING", " direct field"},
		{"AGGREGATE_FIELD_DESCRIPTION_FOR_CLASS_INDICATOR_FIELD_FOR_INHERITANCE", " class indicator field for inheritance policy"},
		{"AGGREGATE_FIELD_DESCRIPTION_FOR_CLASS_INDICATOR_FIELD", " Class indicator field"},
		{"AGGREGATE_FIELD_DESCRIPTION_FOR_FIELD_QUERY_KEY_ASSOCIATION", ", field used as an association to query key \"{0}\""},
		{"AGGREGATE_FIELD_DESCRIPTION_FOR_REFERENCE", " {0} in reference {1}"},
				
					
				
		// DirectCollectionMappingTabbedPropertiesPage
		{"DIRECT_COLLECTION_GENERAL_TAB_TITLE",         "General"},
		{"DIRECT_COLLECTION_TABLE_REFERENCE_TAB_TITLE", "Table Reference"},

		// OneToOneMappingTabbedPropertiesPage
		{"ONE_TO_ONE_GENERAL_TAB_TITLE",         "General"},
		{"ONE_TO_ONE_TABLE_REFERENCE_TAB_TITLE", "Table Reference"},

		// OneToManyMappingTabbedPropertiesPage
		{"ONE_TO_MANY_GENERAL_TAB_TITLE",         "General"},
		{"ONE_TO_MANY_TABLE_REFERENCE_TAB_TITLE", "Table Reference"},
		{"ONE_TO_MANY_ORDERING_TAB_TITLE",        "Ordering"},

		// ManyToManyMappingTabbedPropertiesPage
		{"MANY_TO_MANY_GENERAL_TAB_TITLE",               "General"},
		{"MANY_TO_MANY_TABLE_REFERENCE_TAB_TITLE",       "Table Reference"},
		{"MANY_TO_MANY_ORDERING_TAB_TITLE",              "Ordering"},
		{"MANY_TO_MANY_TABLE_REFERENCE_SOURCE_TITLE",    "Source Reference"},
		{"MANY_TO_MANY_TABLE_REFERENCE_TARGET_TITLE",    "Target Reference"},
		{"RELATION_TABLE_LIST_BROWSER_DIALOG.title",     "Select Relation Table"},
		{"RELATION_TABLE_LIST_BROWSER_DIALOG.listLabel", "&Tables:"},

		// MaintainsBidirectionalRelationshipPanel
		{"MAINTAINS_BIDI_RELATIONSHIP_CHECK_BOX",                    "Mai&ntains Bidirectional Relationship"},
		{"MAINTAINS_BIDI_RELATIONSHIP_RELATIONSHIP_PARTNER_CHOOSER", "Relat&ionship Partner:"},

		// OneToOneGeneralPropertiesPage
		{"ONE_TO_ONE_USE_JOINING_CHECK_BOX",                  "Use &Joining"},
		{"ONE_TO_ONE_USE_VALUE_HOLDER_INDIRECTION_CHECK_BOX", "Use &ValueHolder Indirection"},

		// OneToOneTableReferencePropertiesPage
		{"ONE_TO_ONE_SOURCE_FIELD_COLUMN", "Source Field"},
		{"ONE_TO_ONE_TARGET_FIELD_COLUMN", "Target Field"},
		{"ONE_TO_ONE_TARGET_FOREIGN_KEY",  "Target Foreign Key"},


		// OneToManyGeneralPropertiesPage
		{"MANY_TO_MANY_RELATION_TABLE_CHOOSER",       "Re&lation Table:"},

		// CollectionOrderingPanel
		{"ASCENDING_OPTION",  "Ascending"},
		{"DESCENDING_OPTION", "Descending"},
		{"QUERY_KEY_COLUMN_HEADER" , "Query Key"},
        {"ORDER_COLUMN_HEADER", "Order"},
        
		// DirectCollectionGeneralProperties
		{"DIRECT_COLLECTION_DIRECT_FIELD_CHOOSER",         "&Direct Value Field:"},
		{"DIRECT_COLLECTION_TARGET_TABLE_CHOOSER",         "T&arget Table:"},

		// RelationalDirectMapGeneralPropertiesPage
		{"DIRECT_MAP_DIRECT_KEY_FIELD_CHOOSER", "Direct &Key Field:"},
		{"DIRECT_MAP_CONTAINER_OPTIONS_TAB_TITLE", "Container Options"},
		
		
		// TableReferencePropertiesPage
		{"TABLE_REFERENCE_CHOOSER",    "T&able Reference:"},
		{"TABLE_REFERENCE_NEW_BUTTON", "&New..."},
		{"TABLE_REFERENCE_LIST_BROWSER_DIALOG.title", "Select a Table Reference"},
		{"TABLE_REFERENCE_LIST_BROWSER_DIALOG.listLabel", "&References:"},

		//RelationalMappingComponentFactory
		{"MAPPING_BATCH_READING_CHECK_BOX", "B&atch Reading"},
		{"DIRECT_CONTAINER_MAPPING_TARGET_TABLE_LIST_BROWSER_DIALOG.title", "Select Target Table"},
		{"DIRECT_CONTAINER_MAPPING_TARGET_TABLE_LIST_BROWSER_DIALOG.listLabel", "&Tables:"},
		{"DIRECT_KEY_FIELD_LIST_BROWSER_DIALOG.title", "Select Direct Key Field"},
		{"DIRECT_KEY_FIELD_LIST_BROWSER_DIALOG.listLabel", "&Database Fields:"},
		{"DIRECT_VALUE_FIELD_LIST_BROWSER_DIALOG.title", "Select Direct Value Field"},
		{"DIRECT_VALUE_FIELD_LIST_BROWSER_DIALOG.listLabel", "&Database Fields:"},

		//RelationalDirectMapConverterPropertiesPage
		{"DIRECT_MAP_CONVERTER_TAB_TITLE", "Converter"},
		{"KEY_CONVERTER_TAB_TITLE",        "Key Converter"},
		{"VALUE_CONVERTER_TAB_TITLE",      "Value Converter"},
		
		// TransformationMappingPanel
		{"TRANSFORMATION_MAPPING_IS_MUTABLE", "&Mutable"},
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
