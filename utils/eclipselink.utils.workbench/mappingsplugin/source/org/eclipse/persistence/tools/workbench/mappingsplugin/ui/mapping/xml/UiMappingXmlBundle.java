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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.util.ListResourceBundle;

public class UiMappingXmlBundle 
	extends ListResourceBundle
{
	/**
	 * The string contents of the bundle
	 */
	static final Object[][] contents = 
	{
		// *** MapAsXXXAction ***
		{"MAP_AS_XML_DIRECT_ACTION", "&Direct"},
		{"MAP_AS_XML_DIRECT_ACTION.toolTipText", "Map Attribute as Direct Mapping"},
		
		{"MAP_AS_XML_DIRECT_COLLECTION_ACTION", "Di&rect Collection"},
		{"MAP_AS_XML_DIRECT_COLLECTION_ACTION.toolTipText", "Map Attribute as Direct Collection Mapping"},
		
		{"MAP_AS_COMPOSITE_OBJECT_ACTION", "&Composite Object"},
		{"MAP_AS_COMPOSITE_OBJECT_ACTION.toolTipText", "Map Attribute as Composite Object Mapping"},
		
		{"MAP_AS_COMPOSITE_COLLECTION_ACTION", "Co&mposite Collection"},
		{"MAP_AS_COMPOSITE_COLLECTION_ACTION.toolTipText", "Map Attribute as Composite Collection Mapping"},
		
		{"MAP_AS_ANY_OBJECT_ACTION", "&Any Object"},
		{"MAP_AS_ANY_OBJECT_ACTION.toolTipText", "Map Attribute as Any Object Mapping"},
		
		{"MAP_AS_ANY_COLLECTION_ACTION", "A&ny Collection"},
		{"MAP_AS_ANY_COLLECTION_ACTION.toolTipText", "Map Attribute as Any Collection Mapping"},
		
		{"MAP_AS_ANY_ATTRIBUTE_ACTION", "An&y Attribute"},
		{"MAP_AS_ANY_ATTRIBUTE_ACTION.toolTipText", "Map Attribute as Any Attribute Mapping"},

		{"MAP_AS_EIS_ONE_TO_ONE_ACTION",             "O&ne to One"},
		{"MAP_AS_EIS_ONE_TO_ONE_ACTION.toolTipText", "Map as One to One"},
		
		{"MAP_AS_EIS_ONE_TO_MANY_ACTION",             "One to Man&y"},
		{"MAP_AS_EIS_ONE_TO_MANY_ACTION.toolTipText", "Map as One to Many"},

		{"MAP_AS_XML_FRAGMENT_ACTION", 				"XML Fragme&nt"},
		{"MAP_AS_XML_FRAGMENT_ACTION.toolTipText",  "Map Attribute as XML Fragment Mapping"},
		
		{"MAP_AS_XML_FRAGMENT_COLLECTION_ACTION", 				"XML Fra&gment Collection"},
		{"MAP_AS_XML_FRAGMENT_COLLECTION_ACTION.toolTipText",  "Map Attribute as XML Fragment Collection Mapping"},
		
		{"MAP_AS_MIXED_XML_CONTENT_ACTION", 				"Mi&xed XML Content"},
		{"MAP_AS_MIXED_XML_CONTENT_ACTION.toolTipText",  "Map Attribute as Mixed XML Content Mapping"},

		{"MAP_AS_XML_OBJECT_REFERENCE_ACTION", 				"XML &Object Reference"},
		{"MAP_AS_XML_OBJECT_REFERENCE_ACTION.toolTipText",  "Map Attribute as XML Object Reference Mapping"},

		{"MAP_AS_XML_COLLECTION_REFERENCE_ACTION", 				"XML Co&llection Reference"},
		{"MAP_AS_XML_COLLECTION_REFERENCE_ACTION.toolTipText",  "Map Attribute as XML Collection Reference Mapping"},

		// *** XmlFieldPanel (and varieties) ***
		{"XML_FIELD_PANEL_TITLE", "XML Field"},
		{"XML_FIELD_XPATH_CHOOSER_LABEL", "&XPath:"},
		{"XML_FIELD_XPATH_RADIO_BUTTON", "Specify &XPath:"},
		{"XML_FIELD_AGGREGATE_RADIO_BUTTON", "A&ggregate into parent element"},
		{"XML_FIELD_TYPED_CHECK_BOX", "&Use XML Schema \"type\" attribute"},
		{"XML_FIELD_USE_SINGLE_NODE_CHECK_BOX", "Use single node"},
		{"ELEMENT_TYPE_LABEL", "Element &Type:"},
		
		// *** Wildcard checkbox (used on "any" mappings) ***
		{"WILDCARD_CHECK_BOX", "Maps to &Wildcard  (Uses \"any\" tag)"},
		
		// *** Is CDATA used on direct ox mappings ***
		{"MAPPING_IS_CDATA_CHECK_BOX", "Maps to CDA&TA field."},
		
		// *** CompositeObjectMappingPropertiesPage ***
		{"COMPOSITE_OBJECT_MAPPING_REFERENCE_DESCRIPTOR_CHOOSER", "&Reference Descriptor:"},
		
		// *** CompositeCollectionMappingPropertiesPage ***
		{"COMPOSITE_COLLECTION_MAPPING_REFERENCE_DESCRIPTOR_CHOOSER", "&Reference Descriptor:"},
		
		// *** XmlFieldTransformerAssociationEditingPanel ***
		{"XML_FIELD_TRANSFORMER_ASSOCIATION_PANEL_FIELD_CHOOSER", "&XPath: "},
		
		// EIS 1-1 and 1-M foreign keys panel
		{"FOREIGN_KEYS_TITLE",                    "Foreign Keys"},
		
		// EisOneToManyGeneralPropertiesPage
		{"FOREIGN_KEYS_ON_TARGET_BUTTON", "Foreign &Keys Located On Target"},
		{"FOREIGN_KEYS_ON_SOURCE_BUTTON", "Foreign Keys &Located On Source"},
		{"FOREIGN_KEYS_GROUPING_ELEMENT_CHOOSER", "&Grouping Element:"},

		// EisOneToOneTabbedPropertiesPage
		{"DELETE_ALL_INTERACTION",          "Delete All Interaction"},
		{"GENERAL_TAB_TITLE",               "General"},
		{"SELECTION_INTERACTION_TAB_TITLE", "Selection Interaction"},

		// EisOneToOneSelectionInteractionPropertiesPage
		{"USE_REFERENCE_DESCRIPTOR_READ_OBJECT_INTERACTION", "&Use Reference Descriptor's Read Object Interaction"},

		// MaintainsBidirectionalRelationshipPanel
		{"MAINTAINS_BIDI_RELATIONSHIP_CHECK_BOX",                    "&Maintain Bidirectional Relationship"},
		{"MAINTAINS_BIDI_RELATIONSHIP_RELATIONSHIP_PARTNER_CHOOSER", "Relat&ionship Partner:"},
		
		// XmlTransformationMappingPanel
		{"TRANSFORMATION_MAPPING_IS_MUTABLE", "&Mutable"},
		
		// XmlFieldTransformationAssociationsPanel
		{"TRANSFORMATION_MAPPING_XPATH_COLUMN_LABEL", "XPath"},
		
		// *** XmlFieldTransformerAssociationEditingDialog ***
		{"FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.NULL_XPATH_ERROR", "XPath is not specified."},
		{"FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.DUPLICATE_XPATH_WARNING", "XPath is a duplicate within transformer associations."},
		
		// FieldPairsPanel
		{"FIELD_PAIR_ADD_BUTTON",                  "&Add"},
		{"FIELD_PAIR_DOWN_BUTTON",                 "&Down"},
		{"FIELD_PAIR_EDIT_BUTTON",                 "&Edit"},
		{"FIELD_PAIR_REMOVE_BUTTON",               "&Remove"},
		{"FIELD_PAIR_SOURCE_FIELD_COLUMN",         "Source XPath"},
		{"FIELD_PAIR_TABLE",                       "Field &Pairs:"},
		{"FIELD_PAIR_TARGET_FIELD_COLUMN",         "Target XPath"},
		{"FIELD_PAIR_TITLE",                       "Field Pairs"},
		{"FIELD_PAIR_UP_BUTTON",                   "&Up"},
		
		// *** EisReferenceMappingFieldPairEditingDialog ***
		{"EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.TITLE", "Specify Field Pair"},
		{"EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.DESCRIPTION", "Specify the source and target XPath of the field pair."},
		{"EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.NULL_SOURCE_FIELD_ERROR", "Source XPath is not specified."},
		{"EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.NULL_TARGET_FIELD_ERROR", "Target XPath is not specified."},
		{"EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.DUPLICATE_SOURCE_FIELD_WARNING", "Source XPath is a duplicate within field pairs."},
		{"EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.DUPLICATE_TARGET_FIELD_WARNING", "Target XPath is a duplicate within field pairs."},
		
		// *** EisReferenceMappingFieldPairEditingPanel ***
		{"EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_PANEL.SOURCE_FIELD_CHOOSER", "&Source XPath:"},
		{"EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_PANEL.TARGET_FIELD_CHOOSER", "&Target XPath:"},
		
		// *** MWAnyAttributeMappingPropertiesPage ***
		{"MW_ANY_ATTRIBUTE_MAPPING_MAP_CLASS_SELECTION", "&Map Class:"},
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
