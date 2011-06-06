/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.util.ListResourceBundle;

public class ProblemsBundle extends ListResourceBundle 
{
	final static Object[][] contents = {
		// ********************************************************************
		// 		Rule Indices
		//	====================
		//	0100-0199 - Mappings Project Rules
		//  0200-0399 - Descriptor Rules
		//	0400-0599 - Mapping Rules
		//	0600-0699 - Table Rules
		//	0700-0799 - XML Schema Rules
		//	0800-.... - Sessions Rules
		// 
		// ********************************************************************
		
		
		// ********************************************************************
		//		Mappings Project Rules
		// ********************************************************************
		
		// ***	MWRelationalProject  ***
		{"0100", "The project caches all statments by default for queries, but does not bind all parameters."},
		{"0101", "The project uses a custom sequence table, but the counter field is not specified."},
		{"0102", "The project uses a custom sequence table, but the name field is not specified."},
		
		
		
		// ********************************************************************
		//		Descriptor Rules 
		// ********************************************************************
		
		// ***	MWDescriptor  ***
		{"0200", "The descriptor's class is not public, this will not work when using generated Project Java source."},
		{"0201", "This class is a subclass of a final class."},
		
		// ***	MWMappingDescriptor  ***
		{"0210", "Two methods ({0}) cannot have the same signature."},
		
		// ***	MWAggregateDescriptor ***
		{"0220", "An aggregate shared by multiple source descriptors cannot have one-to-many or many-to-many mappings."},
		{"0221", "Classes cannot reference an aggregate target with one-to-one, one-to-many, or many-to-many mappings."},
		
		// ***	MWInterfaceDescriptor  ***
		{"0225", "The implementor \"{0}\" no longer implements this interface."},

		// ***	MWRelationalDescriptor  ***
		{"0230", "No primary table is specified."},
		{"0231", "No primary keys specified."},
		{"0232", "The following primary key field is unmapped - {0} "},
		{"0233", "Number of primary keys does not match the number of primary keys on the parent."},
		{"0234", "Primary keys do not match parent's primary keys."},
		{"0235", "The following primary key field has no writable mappings - {0}"},
		{"0226", "The following primary key mapping is duplicated in the hierachy - {0}"},
		
		{"0236", "No sequence field is selected."},
		{"0237", "No sequence name is selected."},
		{"0238", "No sequence table is selected."},
		{"0239", "The selected sequence table is not one of the descriptor's associated tables."},	
			
		{"0240", "Two queries ({0}) cannot have the same signature."},
		{"0241", "The query {0} has Cache Statement set to true, but does not bind parameters."},
		{"0242", "The query ({0}) does not maintain cache but does refresh the remote identity map results."},
		{"0243", "The query ({0}) does not maintain cache but does refresh the identity map results."},
		{"0245", "The query ({0}) refreshes identity map results but does not refresh remote identity map results."},
		{"0246", "The query key \"{0}\" does not have an associated database field"},
		{"0247", "The database field selected for query key \"{0}\" does not exist on this descriptor's associated tables."},
		{"0248", "The expression (line {0}) on query {1} is invalid because a parameter has not been specified."}, 
        {"0249", "The expression (line {0}) on query {1} is invalid because a query key has not been specified."},
        {"0250", "The expression (line {0}) on query {1} is invalid because the chosen query key is not a valid mapping type in an expression."},
		{"0251", "The expression (line {0})  on query {1} is invalid. When querying on a reference mapping, only unary operators (Is Null, Not Null) are supported."},
	
		{"0252", "The query {0} has no attribute chosen for the ordering attribute at index {1}."},
		{"0253", "The ordering attribute {0} for query {1} is not valid.  ReadAllQuery ordering items must be either query keys or direct mappings."},
		{"0254", "The query {0} has no attribute chosen for the joined attribute at index {1}."}, 
		{"0255", "The joined attribute {0} for query {1} is not valid.  Joined attributes must be 1-1, 1-many, Direct Collection, or Direct Map mappings."},
		{"0256", "The query {0} has no attribute chosen for the batch read attribute at index {1}."},
		{"0257", "The batch read attribute {0} for query {1} is not valid.  Batch read attributes must be 1-1, 1-m, m-m, direct collection, or direct map mappings."},
		{"0258", "The query {0} has no attribute chosen for the grouping attribute at index {1}."},
		{"0259", "The query {0} has no attribute chosen for the report attribute {1}."},
        {"0260", "The report attribute {0} for query {1} is not valid.  Report query attributes must be either query keys or direct mappings."},
		
		{"0261", "The in/out argument specifies pass by value yet has no out field named or java class name specified, both are required for pass by value arguments."},

		{"0262", "The format for {2} must be between 0 and 127 inclusive.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0263", "The format for {2} must be either 'true' or 'false'.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0264", "The format for {2} must be a single character.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0265", "The format for {2} must be between {3} and {4}.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0266", "The format for {2} must be a string.  Literal argument of expression (line {0}) on query {1} is invalid."}, 
        {"0267", "The format for {2} must contain only digits, '-', and '.'.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0268", "The format for {2} must contain only digits, '-', and '.'.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0269", "The format for {2} must be in the format YYYY/MM/DD or YYYY-MM-DD.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0211", "The format for {2} must be in the format HH-MM-SS or HH:MM:SS.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0212", "The format for {2} must be in the format YYYY/MM/DD HH:MM:SS or YYYY-MM-DD HH:MM:SS.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0213", "The format for {2} must be in the format YYYY/MM/DD or YYYY-MM-DD.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0214", "The format for {2} must be in the format YYYY/MM/DD HH:MM:SS, YYYY/MM/DD, or YYYY-MM-DD.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0215", "The format for {2} must be an even length HEX string.  Literal argument of expression (line {0}) on query {1} is invalid."},
        {"0216", "The format for {2} must be a string.  Literal argument of expression (line {0}) on query {1} is invalid. "},
        {"0217", "Literal argument of expression (line {0}) on query {1} is invalid. The format is illegal."},
 
        
		// ***	MWXmlDescriptor  ***
		{"0270", "No schema context is specified."},
		{"0271", "The descriptor represents a document root object, but no default root element is chosen."},
		{"0272", "Multiple mappings: {0} write to the XPath: \"{1}\"."},
		
		// *** MWOXDescriptor ***
		{"0280", "A descriptor that represents \"anyType\" cannot support inheritance."},
		{"0281", "A descriptor that represents \"anyType\" may contain only a single Any (Object or Collection) mapping."},
		{"0282", "A default root element type has been selected and the default root element is not.  Either select a default root element or clear the default root element type."},
		
		// ***	MWEisDescriptor  ***
		{"0290", "No primary keys specified."},
		{"0354", "No writable mappings for primary keys specified."},
		
		// *** Advanced Policies ***
		
		// Events
		{"0291", "The events policy's About-To-Insert method specified is no longer a visible member of this descriptor's associated class."},
		{"0292", "The events policy's About-To-Update method specified is no longer a visible member of this descriptor's associated class."},
		{"0293", "The events policy's Pre-Deleting method specified is no longer a visible member of this descriptor's associated class."},
		{"0294", "The events policy's Pre-Insert method specified is no longer a visible member of this descriptor's associated class."},
		{"0295", "The events policy's Pre-Update method specified is no longer a visible member of this descriptor's associated class."},
		{"0296", "The events policy's Pre-Writing method specified is no longer a visible member of this descriptor's associated class."},
		{"0297", "The events policy's Post-Build method specified is no longer a visible member of this descriptor's associated class."},
		{"0298", "The events policy's Post-Clone method specified is no longer a visible member of this descriptor's associated class."},
		{"0299", "The events policy's Post-Deleting method specified is no longer a visible member of this descriptor's associated class."},
		{"0300", "The events policy's Post-Insert method specified is no longer a visible member of this descriptor's associated class."},
		{"0301", "The events policy's Post-Merge method specified is no longer a visible member of this descriptor's associated class."},
		{"0302", "The events policy's Post-Refresh method specified is no longer a visible member of this descriptor's associated class."},
		{"0303", "The events policy's Post-Update method specified is no longer a visible member of this descriptor's associated class."},
		{"0304", "The events policy's Post-Writing method specified is no longer a visible member of this descriptor's associated class."},
		{"0358", "The events policy's About-To-Insert method specified is no longer valid descriptor event method."},
		{"0359", "The events policy's About-To-Update method specified is no longer a valid descriptor event method."},
		{"0360", "The events policy's Pre-Deleting method specified is no longer a valid descriptor event method."},
		{"0361", "The events policy's Pre-Insert method specified is no longer a valid descriptor event method."},
		{"0362", "The events policy's Pre-Update method specified is no longer a valid descriptor event method."},
		{"0363", "The events policy's Pre-Writing method specified is no longer a valid descriptor event method."},
		{"0364", "The events policy's Post-Build method specified is no longer a valid descriptor event method."},
		{"0365", "The events policy's Post-Clone method specified is no longer a valid descriptor event method."},
		{"0366", "The events policy's Post-Deleting method specified is no longer a valid descriptor event method."},
		{"0367", "The events policy's Post-Insert method specified is no longer a valid descriptor event method."},
		{"0368", "The events policy's Post-Merge method specified is no longer a valid descriptor event method."},
		{"0369", "The events policy's Post-Refresh method specified is no longer a valid descriptor event method."},
		{"0370", "The events policy's Post-Update method specified is no longer a valid descriptor event method."},
		{"0371", "The events policy's Post-Writing method specified is no longer a valid descriptor event method."},
		

		// Locking
		{"0305", "Write lock field is stored in object, but there is not a writable mapping to the field."},//CR#2734.  If the write lock field is stored in object, there must be a non-read-only mapping to it.
		{"0306", "Database fields specified for Selected Fields type Locking Policy must be mapped: {0}"},
		{"0307", "Database fields specified for Selected Fields type Locking Policy must not be primary key fields: {0} "},
		{"0308", "Version locking is chosen as the Locking Policy, but the field is not specified."},
		{"0309", "The Version Locking database field selected does not exist on this descriptor's associated tables."},
		{"0310", "Database fields specified for Selected Fields type Locking Policy do not exist on this descriptor's associated tables: {0}"},

		// Instantiation
		
        {"0311", "The method you have specified for the instantiation policy's method is no longer a visible member of this class."},
        {"0343", "The method you have specified for the instantiation policy's method is not valid. It must be a static method that returns an object of the descriptor's type."},
        {"0312", "The method you have specified for the instantiation policy's factory instantiation method is no longer a visible member of this class."},
        {"0344", "The method you have specified for the instantiation policy's factory instantiation method is not valid.  It must be a zero argument method ont he factory type that returns an object of the descriptor's type."},
        {"0313", "The method you have specified for the instantiation policy's factory method is no longer a visible member of this class."},
        {"0345", "The method you have specified for the instantiation policy's factory method is not valid.  It must be a static method that return an object of the the factory type."},
		{"0314", "'Use factory' is specified for the Instantiation policy, but all required info is not specified."},
		{"0315", "'Use method' is selected for the Instantiation policy, but no method is selected."},
		{"0316", "The class does not have an accessible zero argument constructor."},
		
		// Copying
		
		{"0317", "No method specified for copying policy."},
        {"0318", "The method specified for the copy policy is no longer a visible member of this class."},
        {"0346", "The method specified for the copy policy is not valid. It must be a zero argument instance method that returns an object of the descriptor's type."},
		
		// Multi-Table
		
		{"0319", "Primary keys do not match across associated tables and no references specified in multitable policy info."},
		
		// After Loading
		
		{"0322", "A class containing the desired after loading method should be specified."},
		{"0323", "The after loading method must be specified."},
		{"0342", "The method specified on the descriptor after loading policy is not a method in the specified class."},
		{"0372", "The method specified on the descriptor after loading policy is not a valid after load method.  It must be static and have one ClassDescriptor parameter."},
		
		// Interface Alias
		
		{"0324", "An interface class must be specified for the interface alias."},
		
		// Inheritance
		
		{"0325", "The inheritance hierarchy originating in this descriptor cannot contain both aggregate and non-aggregate child descriptors."},
		{"0326", "The inheritance hierarchy originating in this descriptor cannot contain both root and composite child descriptors."},
		{"0327", "Class extraction method has not been specified"},
		{"0328", "The method you have specified for the inheritance policy's class extraction method on this descriptor is no longer a visible member of this class."},
		{"0329", "The method specified for the inheritance policy's class extraction method must be static, take org.eclipse.persistence.sessions.Record as an argument, and return a Class."},
		{"0355", "The descriptor for {0} has a class indicator mapping for this root descriptor, yet the descriptor is inactive."},
		{"0356", "This descriptor's parent descriptor is inactive."},
		{"0357", "This child descriptor has no parent descriptor."},
		
		// Returning
		{"0330", "The returning policy insert field \"{0}\" does not exist on this descriptor's associated tables."},
		{"0331", "The returning policy update field \"{0}\" does not exist on this descriptor's associated tables."},
		{"0332", "The returning policy defines field \"{0}\" as an insert field yet this field is also selected as the sequencing field for the descriptor."},
		{"0333", "The returning policy defines field \"{0}\" as an update field yet this field is also selected as the sequencing field for the descriptor."},
		{"0334", "The returning policy defines field \"{0}\" as an update field yet this field is also selected as the class indicator field for the descriptor inheritance policy."},
		{"0335", "The returning policy defines field \"{0}\" as an insert field yet this field is also selected as the class indicator field for the descriptor inheritance policy."},		
		{"0336", "The returning policy defines field \"{0}\" as an update field yet this field is also selected as the version locking field for the descriptor locking policy."},
		{"0337", "The returning policy defines field \"{0}\" as an insert field yet this field is also selected as the version locking field for the descriptor locking policy."},		
		{"0338", "The returning policy defines field \"{0}\" as an update field yet this field is also selected as the foriegn key field for the descriptor one to one mapping \"{1}\"."},
		{"0339", "The returning policy defines field \"{0}\" as an insert field yet this field is also selected as the foriegn key field for the descriptor one to one mapping \"{1}\"."},		
		{"0340", "The following returning update field is unmapped - {0} "},
		{"0341", "The following returning insert field is unmapped - {0} "},
		{"0347", "The selected database platform does not natively support \"returning,\" stored procedures will need to be added for proper function."},
		//don't use 0342 here, it is used above under "After Loading"

		// EJB Info
		{"0350", "EJB Class information is not compatible with project persistence type." },
		{"0351", "Unknown Primary Key Class setting is inconsistent with parent descriptor." },
		{"0352", "Primary Key Class must be java.lang.Object when using Unknown Primary Keys."},
		{"0353", "Descriptors with Unknown Primary Keys must use sequencing."},
		

		// ********************************************************************
		//		Mapping Rules 
		// ********************************************************************
		
		// *** MWMapping ***
		{"0400", "Method accessors have not been selected."},
		{"0401", "The \"get\" method specified for this mapping's method accessing field is no longer visible to this descriptor."},
		{"0402", "The \"set\" method specified for this mapping's method accessing field is no longer visible to this descriptor."},
		{"0403", "Mappings for EJB 2.0 CMP fields should not use method accessing."},
		{"0404", "Mapping references write lock field stored in cache, but is not read-only."},
		{"0405", "The class attribute associated with this mapping is no longer a valid mappable attribute and should be unmapped.  Most likely it is static or final."},
		
		// *** MWRelationalDirectContainerMapping ***
		{"0410", "No direct value field is specified."},
		
		// *** MWRelationalDirectMapMapping ***
		{"0415", "No direct key field is specified."},
		
		// *** MWDirectMapping ***
		{"0420", "No database field is selected."},
		{"0421", "The selected database field does not exist on this descriptor's associated tables."},
				
		// *** MWDirectToXmlTypeMapping ***
		{"0440", "XML type mappings are only supported on the Oracle9i Platform."},
		{"0441", "Attribute must be assignable to java.lang.String, org.w3c.dom.Document, or org.w3c.Node."},
		{"0442", "Database type must be XMLTYPE to map as a Direct to XML Type mapping."},
		
		// *** MWAbstractReferenceMapping ***
		{"0450", "No reference descriptor is selected."},
		{"0451", "{0} references {1} which is not active."},
		{"0452", "The mapping {1} belongs to a shared descriptor and must not reference the isolated descriptor {0}"},
		
		// *** MWTableReferenceMapping ***
		{"0460", "No table reference is selected."},
		{"0461", "Table reference is invalid."},
		{"0464", "No relationship partner is specified."},
		{"0465", "The relationship partner must be a One-to-One, One-to-Many, or Many-to-Many mapping."},
		{"0466", "The specified relationship partner mapping does not specify this mapping as its own relationship partner."},
		{"0467", "The chosen reference descriptor is not a valid reference descriptor for this mapping."},
		{"0468", "\"Maintains BiDirectional Relationship\" is selected so indirection must be used."},
		
		// *** MWCollectionMapping ***
		{"0470", "No container class is selected."},
		{"0471", "The container policy uses a Collection class, but the container class is not a Collection."},
		{"0472", "The container policy uses a Map class, but the container class is not a Map."},
		{"0473", "The container class must be instantiable."},
		{"0474", "The container class does not agree with the instance variable."},
		{"0475", "The container class is a Map, but the key method is not selected."},
		{"0476", "The key method specified for this mapping is no longer visible to the reference descriptor's class."},
		{"0477", "The key method specified for this mapping is not valid."},
		{"0478", "One-to-Many and Many-to-Many mappings in EJB 2.0 CMP descriptors may not use ValueHolder indirection."},  
		{"0479", "The mapping uses ordering, but the query key is not selected."},
		{"0469", "\"Maintains BiDirectional Relationship\" is selected so transparent indirection must be used."},
		
		// *** MWManyToManyMapping ***
		{"0480", "No relation table is selected."},
		{"0481", "Relation table is not dedicated to single writeable many-to-many mapping."}, 
		{"0482", "No source reference is selected."},
		{"0483", "No target reference is selected."},
		
		{"0484", "The container policy uses a List class, but the container class is not a List."},
		{"0485", "The container policy uses a Set class, but the container class is not a Set."},
		{"0486", "Sorting is selected, but the container class is not a SortedSet."},
		{"0487", "Sorting is selected, but there is no comparator class selected."},
		{"0488", "Sorting is selected, but the selected comparator class is not a Comparator."},

		// *** MWOneToManyMapping ***
		// none as of yet
		
		// *** MWOneToOneMapping ***
		{"0500", "Beans mapped One-to-One should use ValueHolder Indirection."},
		
		// *** MWVariableOneToOneMapping ***
		{"0510", "No query key associations have been defined."},
		{"0511", "Not all query key associations have foreign key fields specified."},
		{"0512", "The following specified Query Key Names are no longer valid: {0} "},
		{"0513", "No indicator field is selected."},
		{"0515", "{0} is not an implementor of the {1} interface descriptor so it cannot have an indicator value."},
		{"0516", "The chosen reference descriptor is not an interface descriptor."},
		{"0517", "The association ''{0}->{1}'' is invalid because the field does not exist on this descriptor's associated tables'."},
		
		// *** MWTransformationMapping *** (including relational and xml)
		{"0520", "No attribute transformer is specified."},
		{"0521", "The attribute transformer class is missing."},
		{"0522", "The attribute transformer class \"{0}\" is not a valid transformer class."},
		{"0523", "The attribute transformer method is missing."},
		{"0524", "The attribute transformer method \"{0}\" is not visible to the parent descriptor's class."},
		{"0525", "The attribute transformer method \"{0}\" is not a valid transformer method."},
		{"0526", "No field transformer associations are specified."},
		{"0527", "No transformer is specified for the field \"{0}\"."},
		{"0528", "Missing field in field transformer association."},
		{"0529", "Missing transformer class for the field \"{0}\"."},
		{"0530", "The transformer class \"{0}\" for the field \"{1}\" is not a valid transformer class."},
		{"0531", "Missing transformer method for the field \"{0}\"."},
		{"0532", "The transformer method \"{0}\" for the field \"{1}\" is not visible to the parent descriptor's class."},
		{"0533", "The field transformer method \"{0}\" for the field \"{1}\" is not a valid transformer method."},
		{"0534", "The database field \"{0}\" does not exist on this descriptor\'s associated tables."},
		{"0535", "The database field \"{0}\" is associated with more than one transformer."},
		{"0536", "Missing transformer XPath."},
		{"0537", "Duplicate transformer XPath: \"{0}\"."},
		
		
		// *** MWConverterMapping ***
		{"0542", "No object-type mappings have been specified."},
		
		{"0545", "NCharacter, NString, and NClob database types are only supported on Oracle9 and above platforms."},
		{"0546", "oracle.sql.TIMESTAMP, oracle.sql.TIMESTAMPTZ, and oracle.sql.TIMESTAMPLTZ database types are only supported on Oracle9 and above platforms."},
		
		// *** MWIndirectableMapping ***
		{"0550", "Attribute is typed as a ValueHolderInterface but the mapping does not use Value Holder Indirection."},
		{"0551", "Mapping uses Value Holder Indirection but its associated attribute is not a ValueHolderInterface."},
		{"0552", "Mapping uses Value Holder Indirection, but its associated attribute is \"oracle.toplink.indirection.ValueHolderInterface\" the package renamer should be run on project classes before importation in EclipseLink Workbench"},
		
		// *** MWIndirectableCollectionMapping ***
		{"0560", "The container class for this mapping must implement org.eclipse.persistence.indirection.IndirectContainer."},
		
		
		// *** MWAggregateMapping ***
		{"0570", "The chosen reference descriptor is not an aggregate descriptor."},
		{"0571", "Aggregate fields are not specified."},
		{"0572", "Aggregate mapping fields must be unique."},
		{"0573", "The selected field does not exist on this descriptor's associated tables."},
	
		
		// *** MWEisReferenceMapping ***
		{"0590", "The chosen reference descriptor is not a root eis descriptor."},
		{"0591", "No relationship partner is specified."},
		{"0592", "The relationship partner must be an EIS One-to-One or EIS One-to-Many mapping."},
		{"0593", "The specified relationship partner mapping does not specify this mapping as its own relationship partner."},
		{"0594", "Missing source XPath."},
		{"0595", "Missing target XPath."},
		{"0596", "Duplicate source XPath: \"{0}\"."},
		{"0597", "Duplicate target XPath: \"{0}\"."},
				
		// *** MWEisOneToManyMapping ***
		{"0600", "At least one field pair must be specified, if the foreign keys are located on the source."},
		{"0601", "A foreign key grouping element is required if there are multiple field pairs."},
		{"0602", "The foreign key grouping element does not contain all foreign keys fields."},
		{"0603", "No selection interaction is specified."},
		{"0604", "A delete all interaction is specified, but the mapping is not private owned."},
		
		// *** MWEisOneToOneMapping ***
		{"0610", "At least one field pair must be specified, unless the mapping has no selection interaction and is read-only."},
		{"0611", "There is no Read Object interaction specified on the reference descriptor."},
		{"0612", "There is no target key corresponding to the interaction \"{0}\" from the reference descriptor's read interaction."},
		
		// *** MWAbstractAnyMapping ***
		{"0620", "An XPath may not be specified if enclosed in an \"anyType\" descriptor."},
		{"0621", "Wildcard Mapping may not be selected if enclosed in an \"anyType\" descriptor."},
		{"0622", "The schema does not specify a wildcard in this context."},
		{"0623", "Non-attribute XPaths are mapped within this schema context."},

		// *** MWAnyAttributeMapping ***
		{"0624", "The class attribute associated with this mapping must be assignable to java.util.Map"},
		
		// *** MWMixedXmlContentMapping ***
		{"0625", "The class selected for the container policy must be assigneable to type java.util.Collection"},
		
		// *** MWAbstractXmlReferenceMapping ***
		{"0626", "The selected target field \"{0}\" is not a primary key on the referenced descriptor."},
		{"0627", "There are no XML field pairs defined for this mapping."},
		
		// *** MWAbstractCompositeMapping ***
		{"0630", "The container accessor is not configured."},
		{"0631", "The attribute is not selected for the container accessor."},
		{"0632", "The get method is not selected for the container accessor."},
		{"0633", "The set method is not selected for the container accessor."},

		// ********************************************************************
		//		Database, Tables, Fields, etc. 
		// ********************************************************************

		{"0701", "A database table can only have one IDENTITY column defined."},
		{"0702", "A size is required for the column \"{0}\"."},
		{"0703", "The reference \"{0}\" does not have any column pairs."},
		{"0704", "A key pair has not been completely specified for the reference \"{0}\"."},
		{"0707", "The reference \"{0}\" does not have a target table."},

		{"0720", "The login \"{0}\" does not have the driver class specified." },
		{"0721", "The login \"{0}\" does not have the URL specified." },
		

		// ********************************************************************
		//		Schema Rules 
		// ********************************************************************
		
		// *** MWXmlSchema ***
		{"0800", "Prefix required for namespace \"{0}\"."},
		{"0801", "Duplicate prefix: \"{0}\"."},
		{"0802", "Namespace prefix for : \"{0}\" contains a space, this is invalid and can cause errors at runtime."},
		
		// *** MWXpath ***
		{"0810", "No XPath specified."},
		{"0811", "The XPath \"{0}\" cannot be resolved in this context."},
		{"0812", "The XPath \"{0}\" does not resolve to a valid text node."},
		{"0813", "The XPath \"{0}\" does not contain valid positional information."},
		{"0814", "The XPath \"{0}\" does not resolve to simple (text) data."},
		{"0815", "The XPath \"{0}\" does not resolve to a singular location."},
		
		
		// ********************************************************************
		//		class, class attribute, method problems
		// ********************************************************************
		
		//
		// MWClassAttribute
		//			
		{"0901", "The return type of the get method for the attribute \"{0}\" does not agree with the attribute's type."},
		{"0902", "The get method for the attribute \"{0}\" does not have zero parameters."},
		{"0903", "The parameter type of the set method for the attribute \"{0}\" does not agree with the attribute's type."},
		{"0904", "The set method for the attribute \"{0}\" does not have a single parameter."},
		{"0905", "The return type of the value get method for the attribute \"{0}\" does not agree with the attribute's value type."},
		{"0906", "The value get method for the attribute '{0}' does not have zero parameters."},
		{"0907", "The parameter type of the value set method for the attribute \"{0}\" does not agree with the attribute's value type."},
		{"0908", "The value set method for the attribute \"{0}\" does not have a single parameter."},
		{"0909", "The parameter type of the add method for the attribute \"{0}\" does not agree with the attribute's item type."},
		{"0910", "The add method for the attribute \"{0}\" does not have a single parameter."},
		{"0911", "The parameter types of the add method for the attribute \"{0}\" do not agree with the attribute's key and item types."},
		{"0912", "The add method for the attribute \"{0}\" does not have two parameters."},
		{"0913", "The parameter type of the remove method for the attribute \"{0}\" does not agree with the attribute's item type."},
		{"0914", "The remove method for the attribute \"{0}\" does not have a single parameter."},
		{"0915", "The parameter type of the remove method for the attribute \"{0}\" does not agree with the attribute's key type."},
		{"0916", "The remove method for the attribute \"{0}\" does not have a single parameter."},

		
		// ********************************************************************
		//		unorganized
		// ********************************************************************

		{"0013", "No class indicator value should be defined for the abstract class {0}."},
		{"0054", "No class indicator field is selected for this root class."},
		{"0055", "No class indicator value is defined for this included descriptor:  {0}"},
		{"0089", "Root class does not include an indicator mapping for this descriptor."},
		{"0106", "Multiple mappings: {0} write to the database field: \"{1}\"."},
		{"0118", "The selected parent descriptor for this descriptor's inheritance policy does not have an associated inheritance policy."},
		{"0123", "This root class has no class indicator mappings for its hierarchy."},
		{"0126", "Writable mappings defined for the class indicator field: \"{0}\"."},
		{"0132", "The implemented interface \"{0}\" is not an interface."},
		{"0133", "The superclass for \"{0}\" is an interface, classes cannot extend interfaces."},
						
	};
	
	public Object[][] getContents() {
		return contents;
	}
}
