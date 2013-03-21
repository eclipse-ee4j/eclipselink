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
* dmccann - Mar 2/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.schema;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.descriptors.CoreInheritancePolicy;
import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XMLChoiceFieldToClassAssociation;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.AnyAttributeMapping;
import org.eclipse.persistence.internal.oxm.mappings.AnyCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.AnyObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataMapping;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.CollectionReferenceMapping;
import org.eclipse.persistence.internal.oxm.mappings.CompositeCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.CompositeObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.DirectCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.DirectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.internal.oxm.schema.model.Any;
import org.eclipse.persistence.internal.oxm.schema.model.AnyAttribute;
import org.eclipse.persistence.internal.oxm.schema.model.Attribute;
import org.eclipse.persistence.internal.oxm.schema.model.Choice;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexContent;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Extension;
import org.eclipse.persistence.internal.oxm.schema.model.Import;
import org.eclipse.persistence.internal.oxm.schema.model.Occurs;
import org.eclipse.persistence.internal.oxm.schema.model.Restriction;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.Sequence;
import org.eclipse.persistence.internal.oxm.schema.model.SimpleContent;
import org.eclipse.persistence.internal.oxm.schema.model.SimpleType;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.sessions.Project;

/**
 * INTERNAL:
 *  <p><b>Purpose:</b>Generate one or more EclipseLink Schema model objects based 
 *  on a given list of XMLDescriptors. 
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Return a Map of generated EclipseLink Schema objects based on a given list of XMLDescriptors</li>
 *  </ul>
 *  <p> This class will create and populate one or more EclipseLink schema model Schema objects for a 
 *  given list of EclipseLink XMLDescriptors.  
 *  
 *  @see Schema
 *  @see XMLDescriptor
 */
public class SchemaModelGenerator {
    protected static final String SCHEMA_FILE_NAME = "schema";
    protected static final String SCHEMA_FILE_EXT = ".xsd";     
    protected static final String TEXT = "text()";
    protected static final String ID = "ID";
    protected static final String IDREF = "IDREF";
    protected static String SWAREF_LOCATION;

    /**
     * The default constructor.
     */
    public SchemaModelGenerator() {
    	this(false);
    }
    
    /**
     * This constructor should be used with a value of 'true' when the schemaLocation
     * attribute of the import for swaRef should be swaref.xsd.  This is useful when
     * the user has a local copy of the swaRef schema and wants to use it when access
     * to external URLs is not available.  The default value is:
     * "http://ws-i.org/profiles/basic/1.1/swaref.xsd".
     */
    public SchemaModelGenerator(boolean customSwaRefSchema) {
    	if (customSwaRefSchema) {
    		SWAREF_LOCATION = Constants.SWA_REF.toLowerCase() + SCHEMA_FILE_EXT;
    	} else {
    		SWAREF_LOCATION = Constants.SWAREF_XSD;
    	}
    }
    
    /**
     * Generates a Map of EclipseLink schema model Schema objects for a given list of XMLDescriptors.
     * The descriptors are assumed to have been initialized.  One Schema  object will be generated 
     * per namespace.
     * 
     * @param descriptorsToProcess list of XMLDescriptors which will be used to generate Schema objects
     * @param properties holds a namespace to Properties map containing schema settings, such as elementFormDefault
     * @param additionalGlobalElements a map of QName-Type entries identifying additional global elements to be added  
     * @return a map of namespaces to EclipseLink schema model Schema objects
     * @throws DescriptorException if the reference descriptor for a composite mapping is not in the list of descriptors
     * @see Schema
     */
    public Map<String, Schema> generateSchemas(List<Descriptor> descriptorsToProcess, SchemaModelGeneratorProperties properties, Map<QName, Type> additionalGlobalElements) throws DescriptorException {
        Map<String, Schema> schemaForNamespace = generateSchemas(descriptorsToProcess, properties);
        // process any additional global elements
        if (additionalGlobalElements != null) {
            for(Entry<QName, Type> entry : additionalGlobalElements.entrySet()) {
                QName qname = entry.getKey();
                Type type = entry.getValue();
                if (type instanceof Class) {
                    Class tClass = (Class) type;
                    String nsKey = qname.getNamespaceURI();
                    Schema schema = schemaForNamespace.get(nsKey);
                    
                    QName typeAsQName = (QName) XMLConversionManager.getDefaultJavaTypes().get(tClass);
                    if (typeAsQName == null) {
                        // not a built in type - need to get the type via schema reference
                    	Descriptor desc = getDescriptorByClass(tClass, descriptorsToProcess);
                        if (desc == null) {
                            // at this point we can't determine the element type, so don't add anything
                            continue;
                        }
                        // if the schema is null generate a new one and create an import 
                        if (schema == null) {
                            schema = buildNewSchema(nsKey, new org.eclipse.persistence.oxm.NamespaceResolver(), schemaForNamespace.size(), properties);
                            schemaForNamespace.put(nsKey, schema);
                            typeAsQName = desc.getSchemaReference().getSchemaContextAsQName();
                            
                            Schema schemaForUri = schemaForNamespace.get(typeAsQName.getNamespaceURI());
                            
                            if (!importExists(schema, schemaForUri.getTargetNamespace())) {
                                Import newImport = new Import();
                                newImport.setNamespace(schemaForUri.getTargetNamespace());
                                newImport.setSchemaLocation(schemaForUri.getName());
                                schema.getImports().add(newImport);
                            }
                        } else {
                            typeAsQName = desc.getSchemaReference().getSchemaContextAsQName(schema.getNamespaceResolver());
                        }
                    }
                    if (schema == null) {
                        schema = buildNewSchema(nsKey, new org.eclipse.persistence.oxm.NamespaceResolver(), schemaForNamespace.size(), properties);
                        schemaForNamespace.put(nsKey, schema);
                    }
                    Element element = new Element();
                    element.setName(qname.getLocalPart());
                    element.setType(getSchemaTypeString(typeAsQName, schema));
                    schema.addTopLevelElement(element);
                }
            }
        }
        return schemaForNamespace;
    }
    
    /**
     * Generates a Map of EclipseLink schema model Schema objects for a given list of XMLDescriptors.
     * The descriptors are assumed to have been initialized.  One Schema  object will be generated 
     * per namespace.
     * 
     * @param descriptorsToProcess list of XMLDescriptors which will be used to generate Schema objects
     * @param properties holds a namespace to Properties map containing schema settings, such as elementFormDefault 
     * @return a map of namespaces to EclipseLink schema model Schema objects
     * @throws DescriptorException if the reference descriptor for a composite mapping is not in the list of descriptors
     * @see Schema
     */
    public Map<String, Schema> generateSchemas(List<Descriptor> descriptorsToProcess, SchemaModelGeneratorProperties properties) throws DescriptorException {
        HashMap<String, Schema> schemaForNamespace = new HashMap<String, Schema>();
        Schema workingSchema = null;
        if (properties == null) {
            properties = new SchemaModelGeneratorProperties();
        }
        // set up schemas for the descriptors
        for (Descriptor desc : descriptorsToProcess) {
            String namespace;
            XMLSchemaReference schemaRef = desc.getSchemaReference();
            if (schemaRef != null) {
                namespace = schemaRef.getSchemaContextAsQName(desc.getNamespaceResolver()).getNamespaceURI();
                workingSchema = getSchema(namespace, desc.getNamespaceResolver(), schemaForNamespace, properties);
                addNamespacesToWorkingSchema(desc.getNamespaceResolver(), workingSchema);
            } else {
                // at this point there is no schema reference set, but if a descriptor has a
                // default root element set we will need to generate a global element for it
                for (DatabaseTable table : (Vector<DatabaseTable>)desc.getTables()) {
                    namespace = getDefaultRootElementAsQName(desc, table.getName()).getNamespaceURI();
                    workingSchema = getSchema(namespace, desc.getNamespaceResolver(), schemaForNamespace, properties);
                    addNamespacesToWorkingSchema(desc.getNamespaceResolver(), workingSchema);
                }
            }
        }
        // process the descriptors
        for (Descriptor xdesc : descriptorsToProcess) {
            processDescriptor(xdesc, schemaForNamespace, workingSchema, properties, descriptorsToProcess);
        }
        // return the generated schema(s)
        return schemaForNamespace;
    }
    
    /**
     * Generates a Map of EclipseLink schema model Schema objects for a given list of XMLDescriptors.
     * The descriptors are assumed to have been initialized.  One Schema  object will be generated 
     * per namespace.
     * 
     * @param descriptorsToProcess list of XMLDescriptors which will be used to generate Schema objects
     * @param properties holds a namespace to Properties map containing schema settings, such as elementFormDefault 
     * @param additionalGlobalElements a map of QName-Type entries identifying additional global elements to be added  
     * @return a map of namespaces to EclipseLink schema model Schema objects
     * @throws DescriptorException if the reference descriptor for a composite mapping is not in the list of descriptors
     * @see Schema
     */
    public Map<String, Schema> generateSchemas(List<Descriptor> descriptorsToProcess, SchemaModelGeneratorProperties properties, SchemaModelOutputResolver outputResolver, Map<QName, Type> additionalGlobalElements) throws DescriptorException {
        Map<String, Schema> schemas = generateSchemas(descriptorsToProcess, properties, additionalGlobalElements);
        // write out the generates schema(s) via the given output resolver
        Project proj = new SchemaModelProject();
        XMLContext context = new XMLContext(proj);
        XMLMarshaller marshaller = context.createMarshaller();
        Descriptor schemaDescriptor = (Descriptor)proj.getDescriptor(Schema.class);
        int schemaCount = 0;
        for (Entry<String, Schema> entry : schemas.entrySet()) {
            Schema schema = entry.getValue();
            try {
                NamespaceResolver schemaNamespaces = schema.getNamespaceResolver();
                schemaNamespaces.put(Constants.SCHEMA_PREFIX, "http://www.w3.org/2001/XMLSchema");
                schemaDescriptor.setNamespaceResolver(schemaNamespaces);
                javax.xml.transform.Result target = outputResolver.createOutput(schema.getTargetNamespace(), schema.getName());
                marshaller.marshal(schema, target);
                schemaCount++;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return schemas;
    }
    
    /**
     * Generates a Map of EclipseLink schema model Schema objects for a given list of XMLDescriptors.
     * The descriptors are assumed to have been initialized.  One Schema  object will be generated 
     * per namespace.
     * 
     * @param descriptorsToProcess list of XMLDescriptors which will be used to generate Schema objects
     * @param properties holds a namespace to Properties map containing schema settings, such as elementFormDefault 
     * @return a map of namespaces to EclipseLink schema model Schema objects
     * @throws DescriptorException if the reference descriptor for a composite mapping is not in the list of descriptors
     * @see Schema
     */
    public Map<String, Schema> generateSchemas(List<Descriptor> descriptorsToProcess, SchemaModelGeneratorProperties properties, SchemaModelOutputResolver outputResolver) throws DescriptorException {
        return generateSchemas(descriptorsToProcess, properties, outputResolver, null);
    }
    
    /**
     * Process a given descriptor.  Global complex types will be generated for based on 
     * schema context, and global elements based on default root element. 
     * 
     * @param desc
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     */
    protected void processDescriptor(Descriptor desc, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors) {
    	// determine if a simple type (or complex type with simple content) or complex type is required
    	boolean simple = isSimple(desc);
    	
        XMLSchemaReference schemaRef = desc.getSchemaReference();
        if (schemaRef != null) {
            if (schemaRef.getType() == org.eclipse.persistence.platform.xml.XMLSchemaReference.COMPLEX_TYPE) {
                workingSchema.addTopLevelComplexTypes(buildComplexType(false, desc, schemaForNamespace, workingSchema, properties, descriptors));
            } else if (schemaRef.getType() == org.eclipse.persistence.platform.xml.XMLSchemaReference.SIMPLE_TYPE) {
                workingSchema.addTopLevelSimpleTypes(buildSimpleType(desc, workingSchema, true));
            } else if (schemaRef.getType() == org.eclipse.persistence.platform.xml.XMLSchemaReference.ELEMENT) {
                workingSchema.addTopLevelElement(buildElement(desc, schemaForNamespace, workingSchema, properties, descriptors, simple));
            }

            for (DatabaseTable table :  (Vector<DatabaseTable>)desc.getTables()) {
                String localName = getDefaultRootElementAsQName(desc, table.getName()).getLocalPart();
                // don't overwrite existing top level elements
                if (workingSchema.getTopLevelElements().get(localName) != null) {
                	continue;
                }
                Element topLevelElement = new Element();
                topLevelElement.setName(localName);
    
                QName qname = schemaRef.getSchemaContextAsQName(workingSchema.getNamespaceResolver());
                String elementType = qname.getLocalPart();
                String elementTypeUri = qname.getNamespaceURI();
                String elementTypePrefix = workingSchema.getNamespaceResolver().resolveNamespaceURI(elementTypeUri);
                if (elementTypePrefix != null) {
                    elementType = elementTypePrefix + Constants.COLON + elementType;
                }
                
                topLevelElement.setType(elementType);
                workingSchema.addTopLevelElement(topLevelElement);
            }
        } else {
            // here we have a descriptor that does not have a schema reference set, but since 
            // there is a default root element set we need to generate a global element
            for (DatabaseTable table :  (Vector<DatabaseTable>)desc.getTables()) {
                String localName = getDefaultRootElementAsQName(desc, table.getName()).getLocalPart();
                // a global element may have been created while generating an element ref
                if (workingSchema.getTopLevelElements().get(localName) == null) {
                    Element topLevelElement = new Element();
                    topLevelElement.setName(localName);
                    if (simple) {
                    	if (isComplexTypeWithSimpleContentRequired(desc)) {
                    		topLevelElement.setComplexType(buildComplexTypeWithSimpleContent(desc, schemaForNamespace, workingSchema, properties, descriptors));
                    	} else {
                    		topLevelElement.setSimpleType(buildSimpleType(desc, workingSchema, false));
                    	}
                    } else {
                    	topLevelElement.setComplexType(buildComplexType(true, desc, schemaForNamespace, workingSchema, properties, descriptors));
                    }
                    workingSchema.addTopLevelElement(topLevelElement);
                }
            }
        }
    }

    /**
     * Return the Schema for a given namespace.  If one doesn't exist, a new one will 
     * be created and returned.
     * 
     * @param uri
     * @param nr
     * @param schemaForNamespace
     * @param properties
     * @return
     * @see Schema
     */
    protected Schema getSchema(String uri, NamespaceResolver nr, HashMap<String, Schema> schemaForNamespace, SchemaModelGeneratorProperties properties) {
        Schema schema = schemaForNamespace.get(uri);
        if (schema == null) {
            schema = buildNewSchema(uri, nr, schemaForNamespace.size(), properties);
            schemaForNamespace.put(uri, schema);
        }
        return schema;
    }
    
    /**
     * Create and return an Element for a given XMLDescriptor.
     * 
     * @param desc
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     * @param simple
     * @return
     */
    protected Element buildElement(Descriptor desc,  HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors, boolean simple) {
        Element element = new Element();
        element.setName(desc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()).getLocalPart());
        if (simple) {
        	if (isComplexTypeWithSimpleContentRequired(desc)) {
        		element.setComplexType(buildComplexTypeWithSimpleContent(desc, schemaForNamespace, workingSchema, properties, descriptors));
        	} else {
        		element.setSimpleType(buildSimpleType(desc, workingSchema, false));
        	}
        } else {
        	element.setComplexType(buildComplexType(true, desc, schemaForNamespace, workingSchema, properties, descriptors));
        }
        
        return element;
    }
    
    /**
     * Create and return a SimpleType for a given XMLDescriptor.
     * 
     * @param desc
     * @param workingSchema
     * @return
     */
    protected SimpleType buildSimpleType(Descriptor desc, Schema workingSchema, boolean global) {
        SimpleType st;
    	if (global) {
    		st = buildNewSimpleType(desc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()).getLocalPart());
    	} else {
    		st = new SimpleType();
    	}

        CoreMapping mapping = (CoreMapping)desc.getMappings().get(0);
        QName qname = (QName) XMLConversionManager.getDefaultJavaTypes().get(mapping.getAttributeClassification());
        String baseType = qname.getLocalPart();

        if (qname.getNamespaceURI() != null) {
            String prefix = workingSchema.getNamespaceResolver().resolveNamespaceURI(qname.getNamespaceURI());
            if (prefix == null) {
                prefix = workingSchema.getNamespaceResolver().generatePrefix();
                workingSchema.getNamespaceResolver().put(prefix, qname.getNamespaceURI());
            }
            baseType = prefix + Constants.COLON + baseType;
        }

        Restriction restriction = new Restriction();
        restriction.setBaseType(baseType);
        st.setRestriction(restriction);
        return st;
    }

    /**
     * Create and return a SimpleType with name set to the given name.
     * 
     * @param name
     * @return
     */
    protected SimpleType buildNewSimpleType(String name) {
        SimpleType st = new SimpleType();
        st.setName(name);
        return st;
    }

    /**
     * Create and return a ComplexType for a given XMLDescriptor.  Assumes that the descriptor has a schema context
     * set.
     * 
     * @param anonymous
     * @param desc
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     * @return
     */
    protected ComplexType buildComplexType(boolean anonymous, Descriptor desc,  HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors) {
        ComplexType ct = new ComplexType();
        if (!anonymous) {
            ct.setName(desc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()).getLocalPart());
        }

        CoreInheritancePolicy inheritancePolicy = desc.getInheritancePolicyOrNull();
        Extension extension = null;
        if (inheritancePolicy != null && inheritancePolicy.getParentClass() != null) {
            extension = new Extension();
            extension.setBaseType(desc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()).getLocalPart());
            ComplexContent complexContent = new ComplexContent();
            complexContent.setExtension(extension);
            ct.setComplexContent(complexContent);
        }
        Sequence seq = new Sequence();
        for (CoreMapping mapping : (Vector<CoreMapping>)desc.getMappings()) {
            processMapping(mapping, seq, ct, schemaForNamespace, workingSchema, properties, descriptors);
        }
        if (extension != null) {
            extension.setSequence(seq);
        } else {
            ct.setSequence(seq);
        }
        return ct;
    }

    /**
     * Create and return a ComplexType containing simple content for a given XMLDescriptor.  Assumes 
     * that the descriptor has a schema context set.
     * 
     * @param desc
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     * @return
     */
    private ComplexType buildComplexTypeWithSimpleContent(Descriptor desc,  HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors) {
        ComplexType ct = new ComplexType();
        SimpleContent sc = new SimpleContent();
        Extension extension = new Extension();
        sc.setExtension(extension);
    	ct.setSimpleContent(sc);
        for (CoreMapping mapping : (Vector<CoreMapping>)desc.getMappings()) {
        	Field xFld = (Field) mapping.getField();
        	if (xFld.getXPath().equals(TEXT)) {
        		extension.setBaseType(getSchemaTypeForDirectMapping((DirectMapping) mapping, workingSchema));
        	} else if (xFld.getXPathFragment().isAttribute()) {
                String schemaTypeString = getSchemaTypeForDirectMapping((DirectMapping) mapping, workingSchema);
                Attribute attr = buildAttribute((DirectMapping) mapping, schemaTypeString);
                extension.getOrderedAttributes().add(attr);
        	}
        }
        return ct;
    }
    
    /**
     * Return the schema type for a given mapping's xmlfield.  If the field does not have a schema type
     * set, the attribute classification will be used if non-null.  Otherwise, ClassConstants.STRING
     * will be returned.
     * 
     * @param mapping
     * @param workingSchema
     * @return
     */
    protected String getSchemaTypeForDirectMapping(DirectMapping mapping, Schema workingSchema) {
        return getSchemaTypeForElement((Field) mapping.getField(), mapping.getAttributeClassification(), workingSchema);
    }

    /**
     * Return the schema type for a given xmlfield.  If the field does not have a schema type set,
     * the attribute classification will be used if non-null.  Otherwise, ClassConstants.STRING
     * will be returned.
     * 
     * @param xmlField
     * @param attrClass
     * @param workingSchema
     * @return
     */
    protected String getSchemaTypeForElement(Field xmlField, Class attrClass, Schema workingSchema) {
        String schemaTypeString = null;
        QName schemaType = xmlField.getSchemaType();
        if (schemaType != null) {
            schemaTypeString = getSchemaTypeString(schemaType, workingSchema);
        } else {
            if (attrClass != null && !attrClass.equals(CoreClassConstants.STRING)) {
                QName qName = (QName) XMLConversionManager.getDefaultJavaTypes().get(attrClass);
                if (qName != null) {
                    schemaTypeString = getSchemaTypeString(qName, workingSchema);
                }
            } else {
                // default to string
                schemaTypeString = getSchemaTypeString(Constants.STRING_QNAME, workingSchema);
            }
        }
        return schemaTypeString;
    }

    /**
     * Return the descriptor from the list whose java class name matches
     * javaClassName.  If none exists null will be returned.
     * 
     * @param javaClassName
     * @param descriptors
     * @return
     */
    protected Descriptor getDescriptorByName(String javaClassName, List<Descriptor> descriptors) {
        for (Descriptor xDesc : descriptors) {
            if (xDesc.getJavaClassName().equals(javaClassName)) {
                return xDesc;
            }
        }
        return null;
    }
    
    /**
     * Return the descriptor from the list whose java class matches
     * javaClass.  If none exists null will be returned.
     * 
     * @param javaClass
     * @param descriptors
     * @return
     */
    protected Descriptor getDescriptorByClass(Class javaClass, List<Descriptor> descriptors) {
        for (Descriptor xDesc : descriptors) {
            if (xDesc.getJavaClass() != null && xDesc.getJavaClass() == javaClass) {
                return xDesc;
            }
        }
        return null;
    }

    /**
     * Adds an Any to a given sequence.  If isCollection is true, maxOccurs will
     * be set to unbounded.
     * 
     * @param seq
     * @param isCollection
     * @see Any
     * @see Occurs.UNBOUNDED
     */
    protected void processAnyMapping(Sequence seq, boolean isCollection) {
        Any any = new Any();
        any.setProcessContents(Any.LAX);
        any.setMinOccurs(Occurs.ZERO);
        if (isCollection) {
            any.setMaxOccurs(Occurs.UNBOUNDED);
        }
        seq.addAny(any);
    }    
    
    /**
     * Process a given XMLBinaryDataMapping.
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param workingSchema
     */
    protected void processXMLBinaryDataMapping(BinaryDataMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties) {
    	Field xmlField = (Field) mapping.getField();
        XPathFragment frag = xmlField.getXPathFragment();
        
        String schemaTypeString;
        if (mapping.isSwaRef()) {
            schemaTypeString = getSchemaTypeString(Constants.SWA_REF_QNAME, workingSchema);
            Import newImport = new Import();
            newImport.setNamespace(Constants.REF_URL);
            newImport.setSchemaLocation(SWAREF_LOCATION);
            workingSchema.getImports().add(newImport);
        } else {
            schemaTypeString = getSchemaTypeString(Constants.BASE_64_BINARY_QNAME, workingSchema);
        }
        
        seq = buildSchemaComponentsForXPath(frag, seq, schemaForNamespace, workingSchema, properties);
        frag = getTargetXPathFragment(frag);

        Element elem = elementExistsInSequence(frag.getLocalName(), frag.getShortName(), seq);
        if (elem == null) {
            if (frag.getNamespaceURI() != null) {
                elem = handleFragNamespace(frag, schemaForNamespace, workingSchema, properties, elem, schemaTypeString);
            } else {
                elem = buildElement(frag, schemaTypeString, Occurs.ZERO, null);
            }
            if (mapping.getNullPolicy().isNullRepresentedByXsiNil()) {
                elem.setNillable(true);
            }
            if (xmlField.isRequired()) {
                elem.setMinOccurs("1");
            }
            if (mapping.getMimeType() != null) {
                elem.getAttributesMap().put(Constants.EXPECTED_CONTENT_TYPES_QNAME, mapping.getMimeType());
            }
            seq.addElement(elem);
        }
    }

    /**
     * Process a given XMLBinaryDataCollectionMapping.
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param workingSchema
     */
    protected void processXMLBinaryDataCollectionMapping(BinaryDataCollectionMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties) {
    	Field xmlField = (Field) mapping.getField();
        XPathFragment frag = xmlField.getXPathFragment();
        
        String schemaTypeString;
        if (mapping.isSwaRef()) {
            schemaTypeString = getSchemaTypeString(Constants.SWA_REF_QNAME, workingSchema);
        } else {
            schemaTypeString = getSchemaTypeString(Constants.BASE_64_BINARY_QNAME, workingSchema);
        }
        
        seq = buildSchemaComponentsForXPath(frag, seq, schemaForNamespace, workingSchema, properties);
        frag = getTargetXPathFragment(frag);

        Element elem = elementExistsInSequence(frag.getLocalName(), frag.getShortName(), seq);
        if (elem == null) {
            if (frag.getNamespaceURI() != null) {
                elem = handleFragNamespace(frag, schemaForNamespace, workingSchema, properties, elem, schemaTypeString);
                elem.setMaxOccurs(Occurs.UNBOUNDED);
            } else {
                elem = buildElement(frag, schemaTypeString, Occurs.ZERO, Occurs.UNBOUNDED);
            }
            if (mapping.getNullPolicy().isNullRepresentedByXsiNil()) {
                elem.setNillable(true);
            }
            if (xmlField.isRequired()) {
                elem.setMinOccurs("1");
            }
            if (mapping.getMimeType() != null) {
                elem.getAttributesMap().put(Constants.EXPECTED_CONTENT_TYPES_QNAME, mapping.getMimeType());
            }
            seq.addElement(elem);
        }
    }

    /**
     * Process a given XMLDirectMapping.
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param workingSchema
     */
    protected void processXMLDirectMapping(DirectMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties) {
    	Field xmlField = (Field) mapping.getField();
        
        XPathFragment frag = xmlField.getXPathFragment();
        if (frag.isSelfFragment()) {
            // do nothing;
            return;
        }

        // Handle ID
        boolean isPk = isFragPrimaryKey(frag, mapping);
        String schemaTypeString = null;
        if (isPk) {
            schemaTypeString = Constants.SCHEMA_PREFIX + Constants.COLON + ID;
        } else {
            schemaTypeString = getSchemaTypeForDirectMapping(mapping, workingSchema);
        }

        // Handle enumerations
        Class attributeClassification = mapping.getAttributeClassification();
        if (attributeClassification != null && Enum.class.isAssignableFrom(attributeClassification)) {
            CoreConverter converter = mapping.getConverter();
            if (converter != null && converter instanceof EnumTypeConverter) {
                processEnumeration(schemaTypeString, frag, mapping, seq, ct, workingSchema, converter);
                return;
            }
        }

        if (frag.isAttribute()) {
            Attribute attr = buildAttribute(mapping, schemaTypeString);
            if (xmlField.isRequired()) {
                attr.setUse(Attribute.REQUIRED);
            }
            ct.getOrderedAttributes().add(attr);
        } else {
            seq = buildSchemaComponentsForXPath(frag, seq, schemaForNamespace, workingSchema, properties);
            frag = getTargetXPathFragment(frag);

            Element elem = elementExistsInSequence(frag.getLocalName(), frag.getShortName(), seq);
            if (elem == null) {
                if (frag.getNamespaceURI() != null) {
                    elem = handleFragNamespace(frag, schemaForNamespace, workingSchema, properties, elem, schemaTypeString);
                } else {
                    elem = buildElement(frag, schemaTypeString, Occurs.ZERO, null);
                }
                if (mapping.getNullPolicy().isNullRepresentedByXsiNil()) {
                    elem.setNillable(true);
                }
                if (xmlField.isRequired()) {
                    elem.setMinOccurs("1");
                }
                seq.addElement(elem);
            }
        }
    }

    /**
     * Process a given XMLCompositeDirectCollectionMapping.
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param workingSchema
     */
    protected void processXMLCompositeDirectCollectionMapping(DirectCollectionMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties) {
    	Field xmlField = ((Field) (mapping).getField());

        XPathFragment frag = xmlField.getXPathFragment();
        seq = buildSchemaComponentsForXPath(frag, seq, schemaForNamespace, workingSchema, properties);
    	frag = getTargetXPathFragment(frag);

        String schemaTypeString = getSchemaTypeForElement(xmlField, mapping.getAttributeElementClass(), workingSchema);
        Element element = null;
        if (xmlField.usesSingleNode()) {
            SimpleType st = new SimpleType();
            org.eclipse.persistence.internal.oxm.schema.model.List list = new org.eclipse.persistence.internal.oxm.schema.model.List();

            if (schemaTypeString == null) {
                schemaTypeString = getSchemaTypeString(Constants.ANY_SIMPLE_TYPE_QNAME, workingSchema);
            }
            list.setItemType(schemaTypeString);
            st.setList(list);

            element = buildElement(xmlField.getXPathFragment(), null, Occurs.ZERO, null);
            element.setSimpleType(st);
        } else {
            if (frag.getNamespaceURI() != null) {
                element = handleFragNamespace(frag, schemaForNamespace, workingSchema, properties, element, schemaTypeString);
                element.setMaxOccurs(Occurs.UNBOUNDED);
            } else {
                element = buildElement(frag, schemaTypeString, Occurs.ZERO, Occurs.UNBOUNDED);
            }
        }
        
        if (mapping.getNullPolicy().isNullRepresentedByXsiNil()) {
            element.setNillable(true);
        }

        if (xmlField.isRequired()) {
            element.setMinOccurs("1");
        }

        seq.addElement(element);
    }

    /**
     * Process a given XML composite mapping - either an XMLCompositeObjectMapping, or an 
     * XMLCompositeCollectionMapping.  For XMLCompositeDirectCollectionMappings the 
     * processXMLCompositeDirectCollectionMapping method should be used.
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     * @param collection
     */
    protected void processXMLCompositeMapping(CompositeObjectMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors, boolean collection) {
    	Field xmlField = (Field) mapping.getField();
        
        String refClassName = mapping.getReferenceClassName();
        Descriptor refDesc = getDescriptorByName(refClassName, descriptors);
        if (refDesc == null) {
            throw DescriptorException.descriptorIsMissing(refClassName, (DatabaseMapping)mapping);
        }
        
        XPathFragment frag = xmlField.getXPathFragment();
    	seq = buildSchemaComponentsForXPath(frag, seq, schemaForNamespace, workingSchema, properties);
    	frag = getTargetXPathFragment(frag);
    	
        Element element = buildElement(frag, null, Occurs.ZERO, (collection ? Occurs.UNBOUNDED : null));
        ComplexType ctype = null;
        
        // if the reference descriptor's schema context is null we need to generate an anonymous complex type
        if (refDesc.getSchemaReference() == null) {
            ctype = buildComplexType(true, refDesc, schemaForNamespace, workingSchema, properties, descriptors);
        } else {
            element.setType(getSchemaTypeString(refDesc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()), workingSchema));
        }

        if (frag.getNamespaceURI() != null) {
            // may need to add a global element
        	element = handleFragNamespace(frag, schemaForNamespace, workingSchema, properties, element, ctype, refDesc);
        } else if (ctype != null) {
        	// set an anonymous complex type
            element.setComplexType(ctype);
        }
        
        boolean isNillable = false;
        if (!collection) {
            isNillable = ((CompositeObjectMapping) mapping).getNullPolicy().isNullRepresentedByXsiNil();
        } else {
            isNillable = ((CompositeCollectionMapping) mapping).getNullPolicy().isNullRepresentedByXsiNil();            
        }
        element.setNillable(isNillable);

        if (xmlField.isRequired()) {
            element.setMinOccurs("1");
        }

        seq.addElement(element);
    }

    /**
     * Process a given XMLChoiceCollectionMapping.
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     */
    protected void processXMLChoiceCollectionMapping(ChoiceCollectionMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors) {
        Map<Field, Class> fieldToClassMap = mapping.getFieldToClassMappings(); 
        List<XMLChoiceFieldToClassAssociation> choiceFieldToClassList = mapping.getChoiceFieldToClassAssociations();
        processChoiceMapping(fieldToClassMap, choiceFieldToClassList, seq, ct, schemaForNamespace, workingSchema, properties, descriptors, true);
    }
    
    /**
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     */
    protected void processXMLChoiceObjectMapping(ChoiceObjectMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors) {
        Map<Field, Class> fieldToClassMap =mapping.getFieldToClassMappings(); 
        List<XMLChoiceFieldToClassAssociation> choiceFieldToClassList = mapping.getChoiceFieldToClassAssociations();
        processChoiceMapping(fieldToClassMap, choiceFieldToClassList, seq, ct, schemaForNamespace, workingSchema, properties, descriptors, false);
    }

    /**
     * Process a given XMLChoiceMapping.
     * 
     * @param fieldToClassMap
     * @param choiceFieldToClassList
     * @param seq
     * @param ct
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     * @param isCollection
     */
    protected void processChoiceMapping(Map<Field, Class> fieldToClassMap, List<XMLChoiceFieldToClassAssociation> choiceFieldToClassList, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors, boolean isCollection) {
        Choice theChoice = new Choice();
        if (isCollection) {
            theChoice.setMaxOccurs(Occurs.UNBOUNDED);
        }
        for (XMLChoiceFieldToClassAssociation next : choiceFieldToClassList) {
        	Field field = next.getXmlField();
            Element element = buildElement(field.getXPathFragment().getShortName(), Occurs.ZERO, null);
            
            QName schemaTypeQName = field.getSchemaType();
            if (schemaTypeQName != null) { 
                element.setType(getSchemaTypeString(schemaTypeQName, workingSchema));
            } else {
                element = processReferenceDescriptor(element, getDescriptorByClass(fieldToClassMap.get(field), descriptors), schemaForNamespace, workingSchema, properties, descriptors, field, false);
            }
            theChoice.addElement(element);
        }
        seq.addChoice(theChoice);
    }

    /**
     * Process a given XMLObjectReferenceMapping.  In the case of an XMLCollectionReferenceMapping, 
     * i.e. the isCollection flag is set to true, maxOccurs will be set to 'unbounded' on any 
     * source elements 
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     * @param isCollection
     */
    protected void processXMLObjectReferenceMapping(ObjectReferenceMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors, boolean isCollection) {
        String tgtClassName = mapping.getReferenceClassName();
        Descriptor tgtDesc = getDescriptorByName(tgtClassName, descriptors);
        if (tgtDesc == null) {        	
            throw DescriptorException.descriptorIsMissing(tgtClassName, (DatabaseMapping)mapping);
        }

        // get the target mapping(s) to determine the appropriate type(s)
        String schemaTypeString = null;
        Map<Field, Field> associations = mapping.getSourceToTargetKeyFieldAssociations();
        for (Entry<Field, Field> entry : associations.entrySet()) {
        	Field tgtField = entry.getValue();
            Vector mappings = tgtDesc.getMappings();
            // Until IDREF support is added, we want the source type to be that of the target
            //schemaTypeString = Constants.SCHEMA_PREFIX + COLON + IDREF;
            for (Enumeration mappingsNum = mappings.elements(); mappingsNum.hasMoreElements();) {
                Mapping nextMapping = (Mapping)mappingsNum.nextElement();
                if (nextMapping.getField() != null && nextMapping.getField() instanceof Field) {
                	Field xFld = (Field) nextMapping.getField();
                    if (xFld == tgtField) {
                        schemaTypeString = getSchemaTypeForElement(tgtField, nextMapping.getAttributeClassification(), workingSchema);
                    }
                }
            }
            if (schemaTypeString == null) {
                schemaTypeString = getSchemaTypeString(Constants.STRING_QNAME, workingSchema);
            }
            
            XPathFragment frag = entry.getKey().getXPathFragment();
            if (frag.isAttribute()) {
                Attribute attr = buildAttribute(frag, schemaTypeString);
                ct.getOrderedAttributes().add(attr);
            } else {
                Element elem = buildElement(frag, schemaTypeString, Occurs.ZERO, null);
                if (isCollection) {
                    elem.setMaxOccurs(Occurs.UNBOUNDED);
                }
                seq.addElement(elem);
            }
        }
    }
    
    /**
     * Process a given mapping.
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     */
    protected void processMapping(CoreMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors) {
        if (mapping instanceof BinaryDataMapping) {
            processXMLBinaryDataMapping((BinaryDataMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties);
        } else if (mapping instanceof BinaryDataCollectionMapping) {
            processXMLBinaryDataCollectionMapping((BinaryDataCollectionMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties);
        } else if (mapping instanceof DirectMapping) {
            processXMLDirectMapping((DirectMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties);
        } else if (mapping instanceof DirectCollectionMapping) {
            processXMLCompositeDirectCollectionMapping((DirectCollectionMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties);
        } else if (mapping instanceof CompositeCollectionMapping) {
            processXMLCompositeMapping((CompositeCollectionMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties, descriptors, true);
        } else if (mapping instanceof CompositeObjectMapping) {
            processXMLCompositeMapping((CompositeObjectMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties, descriptors, false);
        } else if (mapping instanceof AnyAttributeMapping) {
            AnyAttribute anyAttribute = new AnyAttribute();
            anyAttribute.setProcessContents(AnyAttribute.LAX);
            ct.setAnyAttribute(anyAttribute);
        } else if (mapping instanceof AnyObjectMapping) {
            processAnyMapping(seq, false);
        } else if (mapping instanceof AnyCollectionMapping) {
            processAnyMapping(seq, true);
        } else if (mapping instanceof ChoiceObjectMapping) {
            processXMLChoiceObjectMapping((ChoiceObjectMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties, descriptors);
        } else if (mapping instanceof ChoiceCollectionMapping) {
            processXMLChoiceCollectionMapping((ChoiceCollectionMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties, descriptors);
        } else if (mapping instanceof CollectionReferenceMapping) {
            processXMLObjectReferenceMapping((CollectionReferenceMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties, descriptors, true);
        } else if (mapping instanceof ObjectReferenceMapping) {
            processXMLObjectReferenceMapping((ObjectReferenceMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties, descriptors, false);
        }
    }

    /**
     * This method will generate a global element if required (based in URI and elementFormDefault) and
     * set a reference to it on a given element accordingly.  This method will typically be used for
     * direct mappings.
     * 
     * @param frag
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param element
     * @param schemaTypeString
     * @return
     */
    protected Element handleFragNamespace(XPathFragment frag, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, Element element, String schemaTypeString) {
    	String fragUri = frag.getNamespaceURI();
        // may need to add a global element
        Schema s = getSchema(fragUri, null, schemaForNamespace, properties);
        String targetNS = workingSchema.getTargetNamespace();
        if ((s.isElementFormDefault() && !fragUri.equals(targetNS)) || (!s.isElementFormDefault() && fragUri.length() > 0)) {
            if (s.getTopLevelElements().get(frag.getLocalName()) == null) {
                Element globalElement = new Element();
                globalElement.setName(frag.getLocalName());
                globalElement.setType(schemaTypeString);
                s.addTopLevelElement(globalElement);
            }
            element = new Element();
            element.setRef(frag.getShortName());
        } else {
        	element = buildElement(frag, schemaTypeString, Occurs.ZERO, null);
        }
        return element;
    }
    
    /**
     * This method will generate a global element if required (based in URI and elementFormDefault) and
     * set a reference to it on a given element accordingly, or set an anonymous complex type on a given 
     * element.  This method will typically be used by composite mappings.
     * 
     * @param frag
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param element
     * @param ctype
     * @param refDesc
     */
    protected Element handleFragNamespace(XPathFragment frag, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, Element element, ComplexType ctype, Descriptor refDesc) {
    	String fragUri = frag.getNamespaceURI();
        // may need to add a global element
    	Element globalElement = null; 
    	Schema s = getSchema(fragUri, null, schemaForNamespace, properties);
        String targetNS = workingSchema.getTargetNamespace();
        if ((s.isElementFormDefault() && !fragUri.equals(targetNS)) || (!s.isElementFormDefault() && fragUri.length() > 0)) {
        	globalElement = (Element) s.getTopLevelElements().get(frag.getLocalName());
            if (globalElement == null) {
                globalElement = new Element();
                globalElement.setName(frag.getLocalName());
                if (ctype != null) {
                    globalElement.setComplexType(ctype);
                } else {
                    globalElement.setType(getSchemaTypeString(refDesc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()), workingSchema));
                }
                s.addTopLevelElement(globalElement);
            }
            element = new Element();
            element.setMaxOccurs(Occurs.UNBOUNDED);
            element.setRef(frag.getShortName());
        }
        
        if (globalElement == null && ctype != null) {
            element.setComplexType(ctype);
        }        
        return element;
    }
    
    /**
     * Return the last fragment before text() in the XPath that a given XPathFragment 
     * is part of.
     * 
     * @param frag
     * @return
     */
    protected XPathFragment getTargetXPathFragment(XPathFragment frag) {
    	if (frag.isAttribute() || frag.isSelfFragment()) {
    		return frag;
    	}
    	while (frag.getNextFragment() != null && !frag.getNextFragment().nameIsText()) {
            frag = frag.getNextFragment();
            if (frag.getNextFragment() == null || frag.getNextFragment().nameIsText()) {
            	break;
    		}
    	}
    	return frag;
    }
    
    /**
     * This method will build element/complexType/sequence components for a given XPath, 
     * and return the sequence that the target element of the mapping should be added 
     * to. For example, if the XPath was "contact-info/address/street/text()", street 
     * would be the target.  This method defers processing of the target path element
     * to the calling method, allowing for differences in handling, such as direct 
     * mappings versus composite mappings, etc.
     * 
     * @param frag
     * @param seq
     * @return
     */
    protected Sequence buildSchemaComponentsForXPath(XPathFragment frag, Sequence seq, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties) {
		// the mapping will handle processing of the target fragment; return the sequence it will be added to 
    	if (frag.getNextFragment() == null || frag.getNextFragment().nameIsText()) {
			return seq;
		}

		Sequence currentSequence = seq;
		
		// if the current element exists, use it; otherwise create a new one
		Element currentElement = elementExistsInSequence(frag.getLocalName(), frag.getShortName(), currentSequence);
		boolean currentElementExists = (currentElement != null);
		if (currentElement == null) {
            currentElement = new Element();
        	// don't set the element name yet, as it may end up being a ref
            ComplexType cType = new ComplexType();
            Sequence sequence = new Sequence();
            cType.setSequence(sequence);
            currentElement.setComplexType(cType);
		}
		
    	Element globalElement = null;
    	String fragUri = frag.getNamespaceURI();
    	if (fragUri != null) {
            Schema s = getSchema(fragUri, null, schemaForNamespace, properties);
            String targetNS = workingSchema.getTargetNamespace();
            if ((s.isElementFormDefault() && !fragUri.equals(targetNS)) || (!s.isElementFormDefault() && fragUri.length() > 0)) {
                // must generate a global element are create a reference to it
        		// if the global element exists, use it; otherwise create a new one
            	globalElement = (Element) s.getTopLevelElements().get(frag.getLocalName());
                if (globalElement == null) {
                    globalElement = new Element();
                    globalElement.setName(frag.getLocalName());
                    ComplexType gCType = new ComplexType();
                    Sequence gSequence = new Sequence();
                    gCType.setSequence(gSequence);
                    globalElement.setComplexType(gCType);
                    s.addTopLevelElement(globalElement);
                }
                // if the current element doesn't exist set a ref and add it to the sequence
                if (!currentElementExists) {
                	// ref won't have a complex type                	
	                currentElement.setComplexType(null);
	                currentElement.setRef(frag.getShortName());
	                currentSequence.addElement(currentElement);
	                currentElementExists = true;
                }
                // make the global element current 
            	currentElement = globalElement;
            }
    	}
    	// if we didn't process a global element, and the current element isn't already in the sequence, add it 
    	if (!currentElementExists && globalElement == null) {
    		currentElement.setName(frag.getLocalName());
            currentSequence.addElement(currentElement);
    	}
    	// set the correct sequence to use/return
    	currentSequence = currentElement.getComplexType().getSequence();

        // don't process the last element in the path - let the calling mapping process it
        frag = frag.getNextFragment();
        if (frag.getNextFragment() != null && !frag.getNextFragment().nameIsText()) {
        	Element childElt = buildElement(frag, null, Occurs.ZERO, null);             
        	currentSequence.addElement(childElt);
        }
        // call back into this method to process the next path element
    	return buildSchemaComponentsForXPath(frag, currentSequence, schemaForNamespace, workingSchema, properties);
    }
    
    /**
     * Build and return an Attribute for a given XMLDirectMapping.
     * 
     * @param mapping
     * @param schemaType
     * @return
     */
    protected Attribute buildAttribute(DirectMapping mapping, String schemaType) {
        XPathFragment frag = ((Field) mapping.getField()).getXPathFragment();
        Attribute attr = new Attribute();
        attr.setName(frag.getShortName());
        attr.setType(schemaType);
        return attr;
    }

    /**
     * Build and return an Attribute for a given XPathFragment.
     * 
     * @param mapping
     * @param schemaType
     * @return
     */
    protected Attribute buildAttribute(XPathFragment frag, String schemaType) {
        Attribute attr = new Attribute();
        attr.setName(frag.getShortName());
        attr.setType(schemaType);
        return attr;
    }

    /**
     * Build and return an Element for a given XPathFragment.
     * 
     * @param frag
     * @param schemaType
     * @param minOccurs
     * @param maxOccurs
     * @return
     */
    protected Element buildElement(XPathFragment frag, String schemaType, String minOccurs, String maxOccurs) {
        Element element = new Element();
        element.setName(frag.getLocalName());
        element.setMinOccurs(minOccurs);
        element.setMaxOccurs(frag.containsIndex() ? Occurs.UNBOUNDED : maxOccurs);
        if (schemaType != null) {
            element.setType(schemaType);
        }
        return element;
    }

    /**
     * Build and return an Element based on a given name, minOccurs and
     * maxOccurs.
     * 
     * @param name
     * @param minOccurs
     * @param maxOccurs
     * @return
     */
    protected Element buildElement(String name, String minOccurs, String maxOccurs) {
        Element element = new Element();
        element.setName(name);
        element.setMinOccurs(minOccurs);
        element.setMaxOccurs(maxOccurs);
        return element;
    }

    /**
     * Indicates if two namespaces are equal.  The result is TRUE if the two URI strings are
     * equal according to the equals() method, if one is null and the other is "", or
     * both are null.
     *  
     * @param uri
     * @param defaultNS
     * @return
     */
    public boolean areNamespacesEqual(String ns1, String ns2) {
        if (ns1 == null) {
            return (ns2 == null || ns2.equals(Constants.EMPTY_STRING));
        }
        return ((ns1.equals(ns2)) || (ns1.equals(Constants.EMPTY_STRING) && ns2 == null));
    }
    
    /**
     * Return the schema type as a string for a given QName and Schema.  The schema's 
     * namespace resolver will be used to determine the prefix (if any) to use. 
     * 
     * @param schemaType
     * @param workingSchema
     * @return
     */
    protected String getSchemaTypeString(QName schemaType, Schema workingSchema) {
        String schemaTypeString = schemaType.getLocalPart();
        String uri = schemaType.getNamespaceURI();
        String prefix = workingSchema.getNamespaceResolver().resolveNamespaceURI(uri);
        if (prefix == null && !areNamespacesEqual(uri, workingSchema.getDefaultNamespace())) {
            if (uri.equals(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
                prefix = workingSchema.getNamespaceResolver().generatePrefix(Constants.SCHEMA_PREFIX);
            } else if (uri.equals(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
                prefix = workingSchema.getNamespaceResolver().generatePrefix(Constants.SCHEMA_INSTANCE_PREFIX);
            } else if (uri.equals(Constants.REF_URL)) {
                prefix = workingSchema.getNamespaceResolver().generatePrefix(Constants.REF_PREFIX);
            } else {
                prefix = workingSchema.getNamespaceResolver().generatePrefix();
            }
            workingSchema.getNamespaceResolver().put(prefix, uri);
        }
        if (prefix != null) {
            schemaTypeString = prefix + Constants.COLON + schemaTypeString;
        }
        return schemaTypeString;
    }

    /**
     * Adds each namespace in the given resolver to the schema.
     * 
     * @param nr
     * @param workingSchema
     */
    protected void addNamespacesToWorkingSchema(NamespaceResolver nr, Schema workingSchema) {
        if (nr != null) {
            Vector<Namespace> descNamespaces = nr.getNamespaces();
            for (Namespace nextNamespace : descNamespaces) {
                workingSchema.getNamespaceResolver().put(nextNamespace.getPrefix(), nextNamespace.getNamespaceURI());
            }
        }
    }

    /**
     * Create and return a new schema for the given namespace.  ElementFormDefault and
     * AttributeFormDefault can be set via SchemaModelGeneratorProperties object.  The
     * namespace resolver's default namespace will be set if non-null.
     * 
     * @param uri
     * @param nr
     * @param schemaCount
     * @param properties
     * @return
     */
    protected Schema buildNewSchema(String uri, NamespaceResolver nr, int schemaCount, SchemaModelGeneratorProperties properties) {
        Schema schema = new Schema();
        schema.setName(SCHEMA_FILE_NAME + schemaCount + SCHEMA_FILE_EXT);
        schemaCount++;

        String defaultNamespace = null;
        if (nr != null) {
            defaultNamespace = nr.getDefaultNamespaceURI();
            if (defaultNamespace != null) {
                schema.setDefaultNamespace(defaultNamespace);
                schema.getNamespaceResolver().setDefaultNamespaceURI(defaultNamespace);
            }
        }

        if (!uri.equals(Constants.EMPTY_STRING)) {
            schema.setTargetNamespace(uri);
            String prefix = null;
            if (nr != null) {
                prefix = nr.resolveNamespaceURI(uri);
            }

            if (prefix == null && !uri.equals(defaultNamespace)) {
                prefix = schema.getNamespaceResolver().generatePrefix();
                schema.getNamespaceResolver().put(prefix, uri);
            }
        }

        if (properties != null) {
            // set elementFormDefault and attributeFormDefault to qualified if necessary
            Properties props = properties.getProperties(uri);
            if (props != null) {
                if (props.containsKey(SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY)) {
                    schema.setElementFormDefault((Boolean) props.get(SchemaModelGeneratorProperties.ELEMENT_FORM_QUALIFIED_KEY));
                }
                if (props.containsKey(SchemaModelGeneratorProperties.ATTRIBUTE_FORM_QUALIFIED_KEY)) {
                    schema.setAttributeFormDefault((Boolean) props.get(SchemaModelGeneratorProperties.ATTRIBUTE_FORM_QUALIFIED_KEY));
                }
            }
        }
        return schema;
    }

    /**
     * Determines if a given schema contains an import for a given schema name.
     * 
     * @param schema
     * @param schemaName
     * @return
     */
    protected boolean importExists(Schema schema, String schemaName) {
        java.util.List<Import> imports = schema.getImports();
        for (Import nextImport : imports) {
            if (nextImport.getSchemaLocation() != null && nextImport.getSchemaLocation().equals(schemaName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a QName representation of a qualified table name (aka default root element).  The
     * given descriptor's namespace resolver will be used to determine the correct prefix - if
     * any - to be used.
     * 
     * @param desc
     * @param qualifiedTableName
     * @return
     */
    protected QName getDefaultRootElementAsQName(Descriptor desc, String qualifiedTableName) {
        QName qName = null;
        int idx = qualifiedTableName.indexOf(Constants.COLON);
        String localName = qualifiedTableName.substring(idx + 1);
        NamespaceResolver nsResolver = desc.getNamespaceResolver();
        if (nsResolver == null) {
            qName = new QName(localName);
        } else if (idx > -1) {
            String prefix = qualifiedTableName.substring(0, idx);
            String uri = nsResolver.resolveNamespacePrefix(prefix);
            qName = new QName(uri, localName);
        } else {
            if (nsResolver.getDefaultNamespaceURI() != null) {
                qName = new QName(nsResolver.getDefaultNamespaceURI(), localName);
            } else {
                qName = new QName(localName);
            }
        }
        return qName;
    }
    
    /**
     *  
     * @param element
     * @param refDesc
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     * @param field
     * @param isCollection
     * @return
     */
    protected Element processReferenceDescriptor(Element element, Descriptor refDesc, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<Descriptor> descriptors, Field field, boolean isCollection) {
        ComplexType ctype = null;
        
        if (refDesc.getSchemaReference() == null) {
            ctype = buildComplexType(true, refDesc, schemaForNamespace, workingSchema, properties, descriptors);
        } else {
            element.setType(getSchemaTypeString(refDesc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()), workingSchema));
        }
       
        XPathFragment frag = field.getXPathFragment();
        String fragUri = frag.getNamespaceURI();
        if (fragUri != null) {
            // may need to add a global element
            Schema s = getSchema(fragUri, null, schemaForNamespace, properties);
            String targetNS = workingSchema.getTargetNamespace();
            if ((s.isElementFormDefault() && !fragUri.equals(targetNS)) || (!s.isElementFormDefault() && fragUri.length() > 0)) {
                if (s.getTopLevelElements().get(frag.getShortName()) == null) {
                    Element globalElement = new Element();
                    globalElement.setName(frag.getLocalName());
                    if (ctype != null) {
                        globalElement.setComplexType(ctype);
                    } else {
                        globalElement.setType(getSchemaTypeString(refDesc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()), workingSchema));
                    }
                    s.getTopLevelElements().put(frag.getShortName(), globalElement);
                }
                element = new Element();
                element.setMinOccurs(Occurs.ZERO);
                if (isCollection) {
                    element.setMaxOccurs(Occurs.UNBOUNDED);
                }
                element.setRef(frag.getShortName());
            } else {
                element.setComplexType(ctype);
            }
        } else if (ctype != null) {
            element.setComplexType(ctype);
        }
        return element;
    }

    /**
     * Convenience method for determining if an element already exists in a given
     * sequence.  If an element exists whose name is equal to 'elementName' true
     * is returned.  False otherwise.
     * 
     * @param elementName
     * @param seq
     * @return
     */
    protected Element elementExistsInSequence(String elementName, String refString, Sequence seq) {
        if (seq.isEmpty()) {
            return null;
        }
        List existingElements = seq.getOrderedElements();
        if (existingElements != null) {
            Iterator elementIt = existingElements.iterator();
            while (elementIt.hasNext()) {
                Element element;
                // could be other components in the list, like Any, but we only care about Element
                try {
                    element = (Element) elementIt.next();
                } catch (ClassCastException cce) {
                    continue;
                }
                if ((element.getRef() != null && element.getRef().equals(refString)) || (element.getName() != null && element.getName().equals(elementName))) {
                    return element;
                }
            }
        }
        return null;
    }
    
    /**
     * Determines if a given descriptor contains a direct mapping to "text()" indicating a
     * simple mapping.  In this case, a simple type or complex type with simple content 
     * will be generated
     * 
     * @param desc
     * @return
     */
    protected boolean isSimple(Descriptor desc) {
    	boolean isSimple = false;
        for (CoreMapping mapping : (Vector<CoreMapping>)desc.getMappings()) {
        	if (mapping.isDirectToFieldMapping()) {
        		Field xFld = (Field) mapping.getField();
            	if (xFld.getXPath().equals(TEXT)) {
            		isSimple = true;
            		break;
            	}
        	} else {
        		break;
        	}
        }
        return isSimple;
    }
    
    /**
     * Indicates if a complex type with simple content is to be generated. This is true when
     * the given descriptor has more than one mapping.  It is assumed that isSimple(desc) 
     * will return true for the given descriptor, and that the descriptor will contain at most 
     * one direct with a 'text()' xpath, and any additional mappings are attribute mappings.   
     * 
     * @param desc
     * @return
     */
    protected boolean isComplexTypeWithSimpleContentRequired(Descriptor desc) {
    	return desc.getMappings().size() > 1 ? true : false;  
    }

    /**
     * Process information contained within an EnumTypeConverter.  This will generate a simple
     * type containing enumeration info.
     * 
     * @param schemaTypeString
     * @param frag
     * @param mapping
     * @param seq
     * @param ct
     * @param workingSchema
     * @param converter
     */
    protected void processEnumeration(String schemaTypeString, XPathFragment frag, DirectMapping mapping, Sequence seq, ComplexType ct, Schema workingSchema, CoreConverter converter) {
        Element elem = null;
        Attribute attr = null;
        if (frag.isAttribute()) {
            attr = buildAttribute(mapping, schemaTypeString);
        } else {
            elem = buildElement(frag, schemaTypeString, Occurs.ZERO, null);
        }
        
        Collection<String> fieldValues = ((EnumTypeConverter) converter).getAttributeToFieldValues().values();
        ArrayList facets = new ArrayList();
        for (String field : fieldValues) {
            facets.add(field);
        }

        Restriction restriction = new Restriction();
        restriction.setEnumerationFacets(facets);
        SimpleType st = new SimpleType();
        st.setRestriction(restriction);

        if (frag.isAttribute()) {
            attr.setSimpleType(st);
            ct.getOrderedAttributes().add(attr);
        } else {
            elem.setSimpleType(st);
            seq.addElement(elem);
        }
    }
    
    /**
     * Indicates if a given fragment is a primary key.
     *  
     * @param frag
     * @param mapping
     * @return
     */
    protected boolean isFragPrimaryKey(XPathFragment frag, DirectMapping mapping) {
        /* Uncomment the following when ID support is needed
        Vector<String> pkFieldNames = mapping.getDescriptor().getPrimaryKeyFieldNames();
        if (pkFieldNames != null) {
            if (frag.isAttribute()) {
                return pkFieldNames.contains(XMLConstants.ATTRIBUTE + frag.getLocalName());
            }
            return pkFieldNames.contains(frag.getLocalName() + '/' + TEXT);
        }
        */
        return false;
    }

}