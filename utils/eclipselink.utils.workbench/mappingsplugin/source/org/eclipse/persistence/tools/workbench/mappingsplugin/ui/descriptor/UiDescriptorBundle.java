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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

/**
 * The resource bundle for the package:
 * org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.
 * 
 * @version 4.5
 */
public class UiDescriptorBundle extends java.util.ListResourceBundle {
	/**
	 * The string contents of the bundle
	 */
	static final Object[][] contents = {

			//MappingDescriptorNode
			{ "MAP_INHERITED_ATTRIBUTES_MENU_ITEM", "&Map Inherited Attributes" },
			{ "MAP_INHERITED_CM_FIELDS_ATTRIBUTES_MENU_ITEM", "Map &Inherited CM Fields" },
			{ "DESCRIPTOR_INFO_TAB", "Descriptor Info" },
			{ "CLASS_INFO_TAB", "Class Info" },

			//Accessibility
			{ "ACCESSIBLE_AGGREGATE_DESCRIPTOR_NODE", "Aggregate Descriptor {0}" },
			{ "ACCESSIBLE_EIS_COMPOSITE_DESCRIPTOR_NODE", "EIS Composite Descriptor {0}" },
			{ "ACCESSIBLE_EIS_ROOT_DESCRIPTOR_NODE", "EIS Root Descriptor {0}" },
			{ "ACCESSIBLE_EJB_DESCRIPTOR_NODE", "EJB Descriptor {0}" },
			{ "ACCESSIBLE_INTERFACE_DESCRIPTOR_NODE", "Interface Descriptor {0}" },
			{ "ACCESSIBLE_PACKAGE_NODE", "Package {0}" },
			{ "ACCESSIBLE_TABLE_DESCRIPTOR_NODE", "Table Descriptor {0}" },
			{ "ACCESSIBLE_XML_DESCRIPTOR_NODE", "XML Descriptor {0}" },

			// Advanced Properties Menu Items
			{ "ADVANCED_PROPERTIES_MENU_ITEM", "Select Advanced &Properties..." },
			{ "ADVANCED_PROPERTIES_MENU_ITEM.tooltip", "Select advanced properties to add or remove from the selected descriptor(s)." },
			{ "COPY_POLICY_MAPPING_DESCRIPTOR_ACTION", "Copying" },
			{ "COPY_POLICY_MAPPING_DESCRIPTOR_ACTION.toolTipText", "Activate or deactivate Copy Policy on selected Descriptor(s)" },
			{ "AFTER_LOAD_POLICY_MAPPING_DESCRIPTOR_ACTION", "After Load" },
			{ "AFTER_LOAD_POLICY_MAPPING_DESCRIPTOR_ACTION.toolTipText", "Activate or deactivate After Load Policy on selected Descriptor(s)" },
			{ "EVENTS_POLICY_MAPPING_DESCRIPTOR_ACTION", "Events" },
			{ "EVENTS_POLICY_MAPPING_DESCRIPTOR_ACTION.toolTipText", "Activate or deactivate Events Policy on selected Descriptor(s)" },
			{ "INHERITANCE_POLICY_MAPPING_DESCRIPTOR_ACTION", "Inheritance" },
			{ "INHERITANCE_POLICY_MAPPING_DESCRIPTOR_ACTION.toolTipText", "Activate or deactivate Inheritance Policy on selected Descriptor(s)" },
			{ "INSTANTIATION_POLICY_MAPPING_DESCRIPTOR_ACTION", "Instantiation" },
			{ "INSTANTIATION_POLICY_MAPPING_DESCRIPTOR_ACTION.toolTipText", "Activate or deactivate Instantiation Policy on selected Descriptor(s)" },
			{ "RETURNING_ACTION", "Returning"},
			{"NATIVE_RETURNING_NOT_SUPPORTED_ON_PLATFORM", "Returning requires additional configuration on the database that is platform dependent to work correctly."},
			{"NATIVE_RETURNING_NOT_SUPPORTED_ON_PLATFORM_TITLE", "Additional Configuration Required"},
			
			{"INTERFACE_ALIAS_ACTION", "Interface Alias"},
			{"INTERFACE_ALIAS_ACTION.toolTipText", "Activate or deactivate Interface Alias Policy on selected Descriptor(s)."},

			// Advanced Properties Chooser
			{ "ADVANCED_PROPERTY_CHOOSER_PLEASE_SELECT_ALL", "Please select all of the advanced policies you would like applied to the selected Descriptor(s)." },
			{ "ADVANCED_PROPERTY_CHOOSER_TITLE", "Select Advanced Properties" },

			// Advanced Properties Tab Titles
			{ "COPY_POLICY_MAPPING_DESCRIPTOR_TAB", "Copying" },
			{ "AFTER_LOADING_POLICY_MAPPING_DESCRIPTOR_TAB", "After Load" },
			{ "INSTANTIATION_POLICY_MAPPING_DESCRIPTOR_TAB", "Instantiation" },
			{ "INHERITANCE_POLICY_MAPPING_DESCRIPTOR_TAB", "Inheritance" },
			{ "INTERFACE_ALIAS_POLICY_MAPPING_DESCRIPTOR_TAB", "Interface Alias"},

			// Copy Policy Editor Text
			{ "theCloneMethodMustBeNonStatic",
					"Note: The clone method must be non-static with no parameters and must return a logical shallow clone of the object." },
			{ "useCopyPolicy", "&Use Instantiation Policy" },
			{ "useCloneMethod", "Use &Clone Method" },

			// After Loading Editor Text
			{ "AFTER_LOAD_POLICY_DESCRIPTOR", "After loading the descriptor, execute the following static method:" },
			{ "AFTER_LOAD_POLICY_CLASS", "Class:" },
			{ "AFTER_LOAD_POLICY_STATIC_METHOD", "Static Method:" },
			{ "POST_LOAD_METHOD_LIST_BROWSER_DIALOG.title", "Select an After Load Method" },
			{ "POST_LOAD_METHOD_LIST_BROWSER_DIALOG.listLabel", "&Methods:" },
			{ "PROBLEM_LOADING_CLASS", "A problem was encountered loading the selected class, select another" },
			{ "PROBLEM_LOADING_CLASS_TITLE", "Problem loading class" },
	
			// Instantiation Policy Editor Text
			{ "INSTANTIATION_POLICY_FACTORY_CLASS", "Factory C&lass:" },
			{ "INSTANTIATION_POLICY_FACTORY_METHOD", "F&actory Method:" },
			{ "INSTANTIATION_POLICY_INSTANTIATION_METHOD", "&Instantiation Method:" },
			{ "INSTANTIATION_POLICY_USE_DEFAULT_CONSTRUCTOR", "Use &Default Constructor" },
			{ "INSTANTIATION_POLICY_USE_METHOD", "Use &Method" },
			{ "INSTANTIATION_POLICY_USE_FACTORY", "&Use Factory" },

			// CachingPolicyPropertiesPage
			{ "CACHING_POLICY_CACHE_TYPE_CHOOSER",                "T&ype:" },
			{ "CACHING_POLICY_CACHE_SIZE_SPINNER",                "Si&ze:" },
			{ "CACHING_POLICY_EXISTENCE_CHECKING_CHOOSER",        "&Existence Checking:" },
			{ "CACHING_POLICY_CACHE_SIZE_DEFAULT_CHECKBOX",       "Defa&ult" },
			{ "CACHING_POLICY_CACHE_COORDINATION_CHOOSER",        "C&oordination:" },
			{ "CACHING_POLICY_CACHE_ISOLATION_CHOOSER",           "&Isolation:" },
			{ "CACHING_POLICY_DEFAULT_VALUE",                     "Default ({0})"},
            
			// CacheExpiryPanel
			{ "CACHING_POLICY_CACHE_EXPIRY",                      "Expiry" },
			{ "CACHING_POLICY_NO_EXPIRY",                         "No E&xpiry" },
			{ "CACHING_POLICY_TIME_TO_LIVE_EXPIRY",               "Time to &Live Expiry" },
			{ "CACHING_POLICY_TIME_TO_LIVE_EXPIRY_PREFIX_LABEL",  "Expi&re after" },
			{ "CACHING_POLICY_TIME_TO_LIVE_EXPIRY_POSTFIX_LABEL", "milliseconds" },
			{ "CACHING_POLICY_DAILY_EXPIRY",                      "Daily Ex&piry" },
			{ "CACHING_POLICY_DAILY_EXPIRY_PREFIX_LABEL",         "Expire a&t" },
			{ "CACHING_POLICY_UPDATE_READ_TIME_ON_UPDATE",        "&Update Read Time on Update" },
			{ "CACHING_POLICY_PROJECT_DEFAULT_CHECK_BOX",         "Pro&ject Default"},

			// CachingPolicyPropertiesPage - Existence Checking choices
			{ "CACHING_POLICY_EXISTENCE_CHECKING_CHECK_CACHE",          "Check Cache" },
			{ "CACHING_POLICY_EXISTENCE_CHECKING_CHECK_DATABASE",       "Check Cache then Database" },
			{ "CACHING_POLICY_EXISTENCE_CHECKING_ASSUME_EXISTENCE",     "Assume Existence" },
			{ "CACHING_POLICY_EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE", "Assume Non-Existence" },

			// CachingPolicyPropertiesPage - Cache Coordination choices
			{ "CACHING_POLICY_CACHE_COORDINATION_NONE", "None" },
			{ "CACHING_POLICY_CACHE_COORDINATION_SYNCHRONIZE_CHANGES", "Synchronize Changes" },
			{ "CACHING_POLICY_CACHE_COORDINATION_SYNCHRONIZE_CHANGES_AND_NEW_OBJECTS", "Synchronize Changes and New Objects" },
			{ "CACHING_POLICY_CACHE_COORDINATION_INVALIDATE_CHANGED_OBJECTS", "Invalidate Changed Objects" },

			// CachingPolicyPropertiesPage - Cache Isolation choices
			{ "CACHING_POLICY_CACHE_ISOLATION_ISOLATED", "Isolated" },
			{ "CACHING_POLICY_CACHE_ISOLATION_SHARED",   "Shared" },

			// CachingPolicyPropertiesPage - Cache Type choices
			{ "CACHING_POLICY_CACHE_TYPE_FULL",          "Full" },
			{ "CACHING_POLICY_CACHE_TYPE_WEAK_WITH_HARD_SUBCACHE", "Weak with Hard Subcache" },
			{ "CACHING_POLICY_CACHE_TYPE_NONE",            "None" },
			{ "CACHING_POLICY_CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE", "Weak with Soft Subcache" },
			{ "CACHING_POLICY_CACHE_TYPE_WEAK",          "Weak" },
			{ "CACHING_POLICY_CACHE_TYPE_SOFT",			 "Soft"	},

			// Events Policy Editor Text
			{ "EVENTS_POLICY_POST_X_METHODS", "Post-X Methods" },
			{ "EVENTS_POLICY_UPDATING_METHODS", "Updating Methods" },
			{ "EVENTS_POLICY_INSERTING_METHODS", "Inserting Methods" },
			{ "EVENTS_POLICY_WRITING_METHODS", "Writing Methods" },
			{ "EVENTS_POLICY_DELETING_METHODS", "Deleting Methods" },
			{ "EVENTS_POLICY_BUILD", "&Build:" },
			{ "EVENTS_POLICY_CLONE", "Clo&ne:" },
			{ "EVENTS_POLICY_MERGE", "&Merge:" },
			{ "EVENTS_POLICY_REFRESH", "Refres&h:" },
			{ "EVENTS_POLICY_PRE", "&Pre:" },
			{ "EVENTS_POLICY_POST", "P&ost:" },
			{ "EVENTS_POLICY_ABOUT_TO", "&About to:" },

			// Locking Policy Editor Text         			
            { "LOCKING_POLICY_NO_LOCKING", "&No Locking" },
            { "LOCKING_POLICY_OPTIMISTIC_LOCKING", "&Optimistic Locking" },
			{ "LOCKING_POLICY_PESSIMISTIC_LOCKING", "&Pessimistic Locking" },
			{ "LOCKING_POLICY_OPTIMISTIC_VERSION", "By Ve&rsion" },
			{ "LOCKING_POLICY_OPTIMISTIC_FIELDS", "By Fie&lds" },
			{ "LOCKING_POLICY_VERSION_LOCKING", "&Version Locking" },
			{ "LOCKING_POLICY_TIMESTAMP_LOCKING", "Timest&amp Locking" },
			{ "LOCKING_POLICY_ALL_FIELDS_LOCKING", "A&ll Fields" },
			{ "LOCKING_POLICY_CHANGED_FIELDS_LOCKING", "&Changed Fields" },
			{ "LOCKING_POLICY_SELECTED_FIELDS_LOCKING", "S&elected Fields" },
			{ "LOCKING_POLICY_STORE_VERSION", "Store Ve&rsion in Cache" },
			{ "LOCKING_POLICY_WAIT_LOCK", "Wait for Loc&k" },
			{"LOCKING_POLICY_SERVER_TIME", "&Server Time"},
			{"LOCKING_POLICY_LOCAL_TIME", "Local &Time"},
			
			//Interface Alias Editor Text
			{"RELATIONAL_DESCRIPTOR_MAY_HAVE_ONE", "A relational descriptor may have one interface alias associated with it. This interface can then be used in queries and relationship mappings. If you use this mechanism, the interface should not have a descriptor associated with it."},
			{"INTERFACE_ALIAS", "&Interface Alias:"},
			{"INTERFACE_ALIAS_REMOVE_DESCRIPTOR_TITLE", "Remove Interface Descriptor"},
			{"INTERFACE_ALIAS_REMOVE_DESCRIPTOR_MESSAGE", "The interface you have chosen has a descriptor associated with it.  Would you like to remove it and continue with your selection?"},
			
			//DescriptorType
			{ "DESCRIPTOR_TYPE_MENU_ITEM", "&Descriptor Type" },

			//MapInheritedAttributesToRootMinusOneAction
			{ "MAP_INHERITED_ATTRIBUTES_TO_ROOT_MINUS_ONE_MENU_ITEM", "To &Root Minus One" },

			//MapInheritedAttributesToSelectedClassAction
			{ "MAP_INHERITED_ATTRIBUTES_TO_SELECTED_CLASS_MENU_ITEM", "T&o Selected Class..." },

			//MapInheritedAttributesToSuperclassAction
			{ "MAP_INHERITED_ATTRIBUTES_TO_SUPERCLASS_MENU_ITEM", "&To Superclass" },

			//MapInheritedCMFieldsAttributesToRootMinusOneAction
			{ "MAP_INHERITED_ATTRIBUTES_CM_FIELDS_TO_ROOT_MINUS_ONE_MENU_ITEM", "To &Root Minus One" },

			//MapInheritedCMFieldsAttributesToSelectedClassAction
			{ "MAP_INHERITED_ATTRIBUTES_CM_FIELDS_TO_SELECTED_CLASS_MENU_ITEM", "T&o Selected Class..." },

			//MapInheritedCMFieldsAttributesToSuperclassAction
			{ "MAP_INHERITED_ATTRIBUTES_CM_FIELDS_TO_SUPERCLASS_MENU_ITEM", "&To Superclass" },

			//HierarchyClassSelector
			{ "HIERARCHY_CLASS_SELECTOR_DIALOG.title", "Class Selector" },
			{ "HIERARCHY_CLASS_SELECTOR_DIALOG_LABEL", "&Select a Class:" },

			//AbstractMapInheritedAttributesAction
			{ "MAP_INHERITED_ATTRIBUTES_ERROR", "Could not map some inherited attributes. Could not find the class \"{0}\"." },
			{ "MAP_INHERITED_ATTRIBUTES_STATUS_DIALOG_TITLE", "Map Inherited Attributes Status" },

			//AbstractMapInheritedCMFieldsAction
			{ "MAP_INHERITED_CM_FIELDS_ERROR", "Could not map some inherited CM fields. Could not find the class \"{0}\"." },
			{ "MAP_INHERITED_CM_FIELDS_STATUS_DIALOG_TITLE", "Map Inherited CM Fields Status" },

			//RemoveInheritedAttributesAction
			{ "REMOVE_INHERITED_ATTRIBUTES_MENU_ITEM", "Remove &Inherited Mappings" },

			//RemoveInheritedCMFieldsAttributesAction
			{ "REMOVE_INHERITED_CM_FIELDS_ATTRIBUTES_MENU_ITEM", "Remove &Inherited Mappings" },

			//ActivateAction
			{ "ACTIVATE_DESCRIPTOR_ACTION", "&Activate Descriptor" },
			{ "ACTIVATE_DESCRIPTOR_ACTION.toolTipText", "Activate or Deactivate Descriptor" },

			//RenameDescriptorAction
			{ "RENAME_DESCRIPTOR_ACTION", "R&ename..." },
			{ "RENAME_DESCRIPTOR_ACTION.toolTipText", "Rename Descriptor and Associated Class" },

			//MoveDescriptorAction
			{ "MOVE_DESCRIPTOR_ACTION", "M&ove..." },
			{ "MOVE_DESCRIPTOR_ACTION.tooltip", "Move Descriptor to Another Package" },
            { "MOVE_DESCRIPTOR_DIALOG.title", "Move Descriptor" },
            { "MOVE_DESCRIPTOR_DIALOG_PACKAGE", "Package" },
            
			//RenamePackageAction
			{ "RENAME_PACKAGE_ACTION", "R&ename..." },
			{ "RENAME_PACKAGE_ACTION.tooltip", "&Rename Package" },
			{ "RENAME_PACKAGE_ACTION_DIALOG.title", "Rename Package" },
			{ "RENAME_PACKAGE_ACTION_DIALOG.textField", "&Enter New Package Name:" },

			//UnmapAction
			{ "UNMAP_MENU", "&Unmap" },
			{ "UNMAP_ACTION", "Unmap &Selected Descriptor" },
			{ "UNMAP_ACTION.toolTipText", "Unmap Selected Descriptor" },
			{ "UNMAP_ALL_DESCRIPTORS_IN_PACKAGE_ACTION", "Unmap &All Descriptors in Package" },
			{ "UNMAP_ALL_DESCRIPTORS_IN_PACKAGE_ACTION.toolTipText", "Unmap All Descriptors in Package" },

			//TransactionalDescriptorEjbPolicyAction
			{ "TRANSACTIONAL_DESCRIPTOR_EJB_POLICY", "EJB" },

			// *** TransactionalDescriptorComponentFactory ***
			{ "READ_ONLY_CHECKBOX_TEXT", "&Read-Only" },
			{ "CONFORM_RESULTS_CHECKBOX_TEXT", "C&onform Results in Unit of Work" },

			//RefreshCachePolicyPanel
			{ "REFRESH_CACHE_POLICY_PANEL_BORDER_TITLE", "Refreshing Cache Options (Advanced)" },
			{ "REFRESH_CACHE_POLICY_PANEL_ALWAYS_REFRESH_CACHE_CHECK_BOX", "A&lways Refresh" },
			{ "REFRESH_CACHE_POLICY_PANEL_SHOULD_DISABLE_CACHE_HITS_CHECK_BOX", "D&isable Cache Hits" },
			{ "REFRESH_CACHE_POLICY_PANEL_ONLY_REFRESH_IF_NEWER_VERSION_CHECK_BOX", "&Only Refresh If Newer Version" },

			//DescriptorClassInfoTabbedPropertiesPage
			{ "CLASS_TAB", "Class" },
			{ "ATTRIBUTES_TAB", "Attributes" },
			{ "METHODS_TAB", "Methods" },

			//ClassPropertiesPage
			{ "CLASS_NAME_LABEL", "Name:" },
			{ "SUPER_CLASS_LABEL", "S&uperclass:" },
			{ "INTERFACES_IMPLEMENTED_LIST", "Interfaces Implemented" },
			{ "INTERFACES_LIST_NAME", "Interfaces" },

			// NewCMFieldAction
			{ "NEW_CM_FIELD_ACTION_TEXT", "Add Container Managed &Field..." },
			{ "NEW_CM_FIELD_ACTION_TOOL_TIP", "Create a New Container Managed Field" },

			// NewCMFieldDialog
			{ "NEW_CM_FIELD_DIALOG_ATTRIBUTE_NAME_FIELD", "&Name:" },
			{ "NEW_CM_FIELD_DIALOG_ATTRIBUTE_TYPE_CHOOSER", "&Type:" },
			{ "NEW_CM_FIELD_DIALOG_TITLE", "Create New Container Managed Field" },
			{ "NEW_CM_FIELD_ACTION_CM_FIELD_NAME", "newName" },

			//
			//	For ClassAttributeAccessorsPanel
			//
			{ "GET_METHOD_LABEL", "Get Method:" },
			{ "VALUE_HOLDER_GET_METHOD_LABEL", "Value Holder Get Method:" },
			{ "SET_METHOD_LABEL", "Set Method:" },
			{ "VALUE_HOLDER_SET_METHOD_LABEL", "Value Holder Set Method:" },
			{ "VALUE_GET_METHOD_LABEL", "Value Get Method:" },
			{ "VALUE_SET_METHOD_LABEL", "Value Set Method:" },
			{ "ADD_METHOD_LABEL", "Add Method:" },
			{ "REMOVE_METHOD_LABEL", "Remove Method:" },
			{ "GENERATE_METHODS_BUTTON_TEXT", "Generate Methods" },
			{ "GET_METHOD_LIST_BROWSER_DIALOG.title", "Select a Get Method" },
			{ "GET_METHOD_LIST_BROWSER_DIALOG.listLabel", "&Methods:" },
			{ "SET_METHOD_LIST_BROWSER_DIALOG.title", "Select a Set Method" },
			{ "SET_METHOD_LIST_BROWSER_DIALOG.listLabel", "&Methods:" },
			{ "VALUE_GET_METHOD_LIST_BROWSER_DIALOG.title", "Select a Value Get Method" },
			{ "VALUE_GET_METHOD_LIST_BROWSER_DIALOG.listLabel", "&Methods:" },
			{ "VALUE_SET_METHOD_LIST_BROWSER_DIALOG.title", "Select a Value Set Method" },
			{ "VALUE_SET_METHOD_LIST_BROWSER_DIALOG.listLabel", "&Methods:" },
			{ "REMOVE_METHOD_LIST_BROWSER_DIALOG.title", "Select a Remove Method" },
			{ "REMOVE_METHOD_LIST_BROWSER_DIALOG.listLabel", "&Methods:" },
			{ "ADD_METHOD_LIST_BROWSER_DIALOG.title", "Select an Add Method" },
			{ "ADD_METHOD_LIST_BROWSER_DIALOG.listLabel", "&Methods:" },

			//
			//	For ClassAttributeModifiersPanel
			//
			{ "MODIFIER_PANEL_TITLE", "Modifiers" },
			{ "removeMapping?", "Remove Mapping?" },
			{
					"sTATICAndFINALAttributesCannot",
					"Attributes with \"static\" or \"final\" modifiers cannot be mapped to a database field. Therefore, the mapping for this attribute will be removed. Remove the mapping?" },

			//
			//	For ClassAttributePropertiesPanel
			//
			{ "ATTRIBUTE_GENERAL_PAGE", "General" },
			{ "ATTRIBUTE_ACCESSORS_PAGE", "Accessors" },

			//
			//	For ClassAttributeTypesPanel
			//
			{ "TYPE_PANEL_TITLE", "Type Declaration" },
			{ "TYPE_LABEL", "T&ype:" },
			{ "DIMENSIONALITY_LABEL", "Di&mensionality:" },
			{ "VALUE_TYPE_LABEL", "&Value Type:" },
			{ "MAP_KEY_TYPE_LABEL", "Map &Key Type:" },
			{ "ELEMENT_TYPE_LABEL", "Ele&ment Type:" },
			{ "MAP_VALUE_TYPE_LABEL", "&Map Value Type:" },
			{ "CLASS_ATTRIBUTE_TYPE_VOID_WARNING.title", "Invalid Class Attribute Type" },
			{ "CLASS_ATTRIBUTE_TYPE_VOID_WARNING.message", "The type of a class attribute can not be void." },

			//
			// For DescriptorPropertiesClassInfoMethodsPage
			//

			{
					"MAINTAIN_ZERO_ARGUMENT_CONSTRUCTOR_DIALOG.message",
					"This action will result in the class \"{0}\" having no zero-argument constructor, something which TopLink requires for most persisted classes.<p>Would you like to maintain a zero-argument constructor in this class?" },

			//
			//	For ClassAttributePanel
			//

			{ "addNewAttribute.title", "Add New Attribute" },
			{ "enterTheNameOfTheNewAttribute.message", "Enter the name of the new attribute:" },
			{ "renameAttribute.title", "Rename Attribute" },
			{ "renameAttribute.message", "Enter a new name for the attribute:" },
			{ "ATTRIBUTES_LIST", "Attributes" },

			//
			//	For InheritancePolicyPropertiesPage
			//
			{ "READ_SUBCLASSES_ON_QUERY", "Read Subclasses on &Query: " },
			{ "OUTER_JOIN_ALL_SUBCLASSES", "Outer &Join All Subclasses: "},
			{ "READ_SUBCLASSES_VIEW_OPTIONAL", "Read Subclasses &View (Optional): " },
			{ "IS_ROOT_DESC", "Is &Root Parent Descriptor:" },
			{ "IS_CHILD_DESC", "Is C&hild Descriptor" },
			{ "PARENT_DESC", "&Parent Descriptor: " },
			{"PARENT_DESCRIPTOR_LIST_BROWSER_DIALOG.title", "Select the Parent Descriptor"},
			{"PARENT_DESCRIPTOR_LIST_BROWSER_DIALOG.listLabel", "&Descriptors:"},

			//
			// For ClassIndicatorSubPanel
			//
			{ "USE_CLASS_EXTRACTION_METHOD", "Use Class Extraction &Method:" },
			{ "USE_CLASS_INDICATOR_DICTIONARY", "Use C&lass Indicator Dictionary:" },
			{ "USE_CLASS_INDICATOR_FIELD", "&Use Class Indicator Field:" },
			{ "USE_CLASS_NAME_AS_INDICATOR", "Use &Class Name as Indicator" },

			//
			// For XmlClassIndicatorSubPanel
			//
			{ "USE_XSITYPE_AS_INDICATOR", "Use XML &Schema \"Type\" Attribute" },
			{ "FIELD_SELECTION", "Field Selection: " },
			{ "INDICATOR_SELECTION", "Indicator Selection: " },
			{ "SPECIFY_FIELD", "Specify Field: " },

			//
			// For ClassIndicatorValueList
			//
			{ "BE_SINGLE_CHARACTER", "The format for {0} must be a single character." },
			{ "BE_A_STRING", "The format for {0} must be a string." },
			{ "BE_BETWEEN_DOUBLE", "The format for {0} must be between {1} and {2}." },
			{ "BE_BETWEEN_FLOAT", "The format for {0} must be between {1} and {2}." },
			{ "BE_BETWEEN_INT", "The format for {0} must be between {1} and {2}." },
			{ "BE_BETWEEN_LONG", "The format for {0} must be between {1} and {2}." },
			{ "BE_BETWEEN_0_AND_127", "The format for {0} must be between 0 and 127 inclusive." },
			{ "BE_BETWEEN_SHORT", "The format for {0} must be between {1} and {2}." },
			{ "BE_EITHER_TRUE_OR_FALSE", "The format for {0} must be either 'true' or 'false'." },
			{ "BE_IN_FORMAT1", "The format for {0} must be in the format YYYY/MM/DD or YYYY-MM-DD." },
			{ "BE_IN_FORMAT2", "The format for {0} must be in the format HH-MM-SS or HH:MM:SS." },
			{ "BE_IN_FORMAT3", "The format for {0} must be in the format YYYY/MM/DD HH:MM:SS or YYYY-MM-DD HH:MM:SS." },
			{ "BE_IN_FORMAT4", "The format for {0} must be in the format YYYY/MM/DD or YYYY-MM-DD." },
			{ "BE_IN_FORMAT5", "The format for {0} must be in the format YYYY/MM/DD HH:MM:SS, YYYY/MM/DD, or YYYY-MM-DD." },
			{ "BE_IN_FORMAT_BYTE_ARRAY", "The format for {0} must be an even length HEX string." },
			{ "CONTAINS_ONLY_DIGITS", "The format for {0} must contain only digits, '-', and '.'." },
			{ "CONTAINS_ONLY_DIGITS_AND", "The format for {0} must contain only digits and '-'." },
			{ "CLASS_COLUMN_HEADER", "&Class" },
			{ "EDIT", "E&dit" },
			{ "ENTER_INDICATOR_VALUE", "Enter an Indicator Value" },
			{ "ILLEGAL_FORMAT", "Illegal Format" },
			{ "INCLUDE_COLUMN_HEADER", "&Include" },
			{ "INDICATOR_TYPE", "&Indicator Type: " },
			{ "INDICATOR_VALUE", "Indicator &Value: " },
			{ "INDICATOR_VALUE_ALLREADY_EXISTS", "The indicator value {0} is already used by another descriptor." },
			{ "REPEATED_INDICATOR_VALUE", "Repeated Indicator Value" },
			{ "VALUE_COLUMN_HEADER", "Indicator &Value" },

            //ClassExtractionMethodPanel
            { "CLASS_EXTRACTION_METHOD_LIST_BROWSER_DIALOG.title", "Select a Class Extraction Method" },
            { "CLASS_EXTRACTION_METHOD_LIST_BROWSER_DIALOG.listLabel", "&Methods:" },

			//
			//	For ModifierComponentGroup
			//
			//TODO do we want the following items transalated?
			//If not, we need to remove them from the resource bundle
			{ "accessModifiers", "Access Modifiers" },
			{ "otherModifiers", "Other Modifiers" },
			{ "DEFAULT_ACCESS_MODIFIER", "(&Default)" },
			{ "ABSTRACT_MODIFIER", "ab&stract" },
			{ "FINAL_MODIFIER", "fina&l" },
			{ "NATIVE_MODIFIER", "nati&ve" },
			{ "PRIVATE_ACCESS_MODIFIER", "pr&ivate" },
			{ "PROTECTED_ACCESS_MODIFIER", "prot&ected" },
			{ "PUBLIC_ACCESS_MODIFIER", "&public" },
			{ "STATIC_MODIFIER", "stati&c" },
			{ "SYNCHRONIZED_MODIFIER", "synchroni&zed" },
			{ "TRANSIENT_MODIFIER", "&transient" },
			{ "VOLATILE_MODIFIER", "v&olatile" },

			// MethodPropertiesPanel
			{ "RETURN_TYPE_PANEL_TITLE", "Return Type" },
			{ "RETURN_TYPE_LABEL", "T&ype:" },

			// MethodParametersPanel
			{ "TYPE_COLUMN_HEADER", "Type" },
			{ "DIMENSIONALITY_COLUMN_HEADER", "Dimensionality" },
			{ "PARAMETERS_TABLE_LABEL", "Parameters" },
			{ "ADD_PARAMETER", "Add&..." },
			{ "REMOVE_PARAMETER", "Re&move" },
			{ "METHOD_PROPERTIES_PANEL_TITLE", "Properties" },
			{ "enterTheNameOfTheNewMethod.message", "Enter the name of the new method:" },
			{ "addNewMethod.title", "Add New Method" },
			{ "EDIT_METHOD_DIALOG_TITLE",   "Rename Method" },
			{ "EDIT_METHOD_DIALOG_MESSAGE", "Enter a new name for the method:" },
			{ "METHOD_PARAMETER_TYPE_VOID_WARNING.title", "Invalid Method Parameter Type" },
			{ "METHOD_PARAMETER_TYPE_VOID_WARNING.message", "A method parameter type can not be void." },
			{ "METHODS_LIST", "Methods" },

			// DescriptorCreationFailuresDialog
			{ "ERROR_CREATING_DESCRIPTORS_TITLE", "Descriptor Creation Error" },
			{ "XML_PROJECTS_DONT_SUPPORT_INTERFACE_DESCRIPTORS_MESSAGE",
					"Descriptors were not created for the following interfaces because interface descriptors are not currently supported for XML/EIS projects." },
			{ "DESCRIPTOR_REFRESH_ERROR_MESSAGE", "Type was refreshed as an interface, descriptor will be removed." },
			{ "DESCRIPTOR_CREATION_ERROR_MESSAGE", "Imported type is an interface, no descriptor created." },

			// Returning Properties Page
			{ "RETURNING_POLICY_PAGE_TITLE", "Returning Policy" }, { "RETURNING_POLICY_INSERT_FIELD_LABEL", "Insert" },
			{ "RETURNING_POLICY_UPDATE_FIELD_LABEL", "Update" }, { "RETURNING_POLICY_INSERT_COLUMN_HEADER", "Name" },
			{ "RETURNING_POLICY_UPDATE_COLUMN_HEADER", "Name" }, { "RETURNING_POLICY_RETURN_ONLY_COLUMN_HEADER", "Return-only" },

	};

	/**
	 * Returns the initialized array of keys and values that represents the
	 * strings used by the classes in the descriptor package.
	 * 
	 * @return An table where the first element is the key used to retrieve the
	 *         second element, which is the value
	 */
	public Object[][] getContents() {
		return contents;
	}
}
