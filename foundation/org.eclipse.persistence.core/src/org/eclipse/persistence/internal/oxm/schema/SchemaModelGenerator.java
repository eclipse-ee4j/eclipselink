/*******************************************************************************
* Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.schema.model.Attribute;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexContent;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Extension;
import org.eclipse.persistence.internal.oxm.schema.model.Import;
import org.eclipse.persistence.internal.oxm.schema.model.Occurs;
import org.eclipse.persistence.internal.oxm.schema.model.Restriction;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.Sequence;
import org.eclipse.persistence.internal.oxm.schema.model.SimpleType;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;

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
    protected static final String COLON = ":";
    protected static final String EMPTY_STRING = "";

    /**
     * The default constructor.
     */
    public SchemaModelGenerator() {
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
    public Map<String, Schema> generateSchemas(List<XMLDescriptor> descriptorsToProcess, SchemaModelGeneratorProperties properties) throws DescriptorException {
        HashMap<String, Schema> schemaForNamespace = new HashMap<String, Schema>();
        Schema workingSchema = null;
        if (properties == null) {
            properties = new SchemaModelGeneratorProperties();
        }
        
        for (XMLDescriptor desc : descriptorsToProcess) {
            String namespace;
            XMLSchemaReference schemaRef = desc.getSchemaReference();
            if (schemaRef != null) {
                namespace = schemaRef.getSchemaContextAsQName(desc.getNamespaceResolver()).getNamespaceURI();
                workingSchema = getSchema(namespace, desc.getNamespaceResolver(), schemaForNamespace, properties);
                addNamespacesToWorkingSchema(desc.getNamespaceResolver(), workingSchema);
            } else {
                // at this point there is no schema reference set, but if a descriptor has a
                // default root element set we will need to generate a global element for it
                for (DatabaseTable table : desc.getTables()) {
                    namespace = getDefaultRootElementAsQName(desc, table.getName()).getNamespaceURI();
                    workingSchema = getSchema(namespace, desc.getNamespaceResolver(), schemaForNamespace, properties);
                    addNamespacesToWorkingSchema(desc.getNamespaceResolver(), workingSchema);
                }
            }
        }
        
        // process the descriptors
        for (XMLDescriptor xdesc : descriptorsToProcess) {
            processDescriptor(xdesc, schemaForNamespace, workingSchema, properties, descriptorsToProcess);
        }
        
        // return the generated schema(s)
        return schemaForNamespace;
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
    protected void processDescriptor(XMLDescriptor desc, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<XMLDescriptor> descriptors) {
        XMLSchemaReference schemaRef = desc.getSchemaReference();
        if (schemaRef != null) {
            if (schemaRef.getType() == org.eclipse.persistence.platform.xml.XMLSchemaReference.COMPLEX_TYPE) {
                workingSchema.addTopLevelComplexTypes(buildComplexType(false, desc, schemaForNamespace, workingSchema, properties, descriptors));
            } else if (schemaRef.getType() == org.eclipse.persistence.platform.xml.XMLSchemaReference.SIMPLE_TYPE) {
                workingSchema.addTopLevelSimpleTypes(buildSimpleType(desc, workingSchema));
            }

            for (DatabaseTable table : desc.getTables()) {
                String localName = getDefaultRootElementAsQName(desc, table.getName()).getLocalPart();
    
                Element topLevelElement = new Element();
                topLevelElement.setName(localName);
    
                QName qname = schemaRef.getSchemaContextAsQName(workingSchema.getNamespaceResolver());
                String elementType = qname.getLocalPart();
                String elementTypeUri = qname.getNamespaceURI();
                String elementTypePrefix = workingSchema.getNamespaceResolver().resolveNamespaceURI(elementTypeUri);
                if (elementTypePrefix != null) {
                    elementType = elementTypePrefix + COLON + elementType;
                }
                
                topLevelElement.setType(elementType);
                workingSchema.addTopLevelElement(topLevelElement);
            }
        } else {
            // here we have a descriptor that does not have a schema reference set, but since 
            // there is a default root element set we need to generate a global element
            for (DatabaseTable table : desc.getTables()) {
                String localName = getDefaultRootElementAsQName(desc, table.getName()).getLocalPart();
                // a global element may have been created while generating an element ref
                if (workingSchema.getTopLevelElements().get(localName) == null) {
                    Element topLevelElement = new Element();
                    topLevelElement.setName(localName);
                    topLevelElement.setComplexType(buildComplexType(true, desc, schemaForNamespace, workingSchema, properties, descriptors));
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
    private Schema getSchema(String uri, NamespaceResolver nr, HashMap<String, Schema> schemaForNamespace, SchemaModelGeneratorProperties properties) {
        Schema schema = schemaForNamespace.get(uri);
        if (schema == null) {
            schema = buildNewSchema(uri, nr, schemaForNamespace.size(), properties);
            schemaForNamespace.put(uri, schema);
        }
        return schema;
    }

    /**
     * Create and return a SimpleType for a given XMLDescriptor.
     * 
     * @param desc
     * @param workingSchema
     * @return
     */
    protected SimpleType buildSimpleType(XMLDescriptor desc, Schema workingSchema) {
        SimpleType st = buildNewSimpleType(desc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()).getLocalPart());

        DatabaseMapping mapping = desc.getMappings().get(0);
        QName qname = (QName) XMLConversionManager.getDefaultJavaTypes().get(mapping.getAttributeClassification());
        String baseType = qname.getLocalPart();

        if (qname.getNamespaceURI() != null) {
            String prefix = workingSchema.getNamespaceResolver().resolveNamespaceURI(qname.getNamespaceURI());
            if (prefix == null) {
                prefix = workingSchema.getNamespaceResolver().generatePrefix();
                workingSchema.getNamespaceResolver().put(prefix, qname.getNamespaceURI());
            }
            baseType = prefix + COLON + baseType;
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
     * Create and return a ComplexType for a given XMLDescriptor.
     * 
     * @param anonymous
     * @param desc
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     * @return
     */
    private ComplexType buildComplexType(boolean anonymous, XMLDescriptor desc,  HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<XMLDescriptor> descriptors) {
        ComplexType ct = new ComplexType();
        if (!anonymous) {
            ct.setName(desc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()).getLocalPart());
        }

        InheritancePolicy inheritancePolicy = desc.getInheritancePolicyOrNull();
        Extension extension = null;
        if (inheritancePolicy != null && inheritancePolicy.getParentClass() != null) {
            extension = new Extension();
            extension.setBaseType(desc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()).getLocalPart());
            ComplexContent complexContent = new ComplexContent();
            complexContent.setExtension(extension);
            ct.setComplexContent(complexContent);
        }
        Sequence seq = new Sequence();
        for (DatabaseMapping mapping : desc.getMappings()) {
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
     * Return the schema type for a given mapping's xmlfield.  If the field does not have a schema type
     * set, the attribute classification will be used if non-null.  Otherwise, ClassConstants.STRING
     * will be returned.
     * 
     * @param mapping
     * @param workingSchema
     * @return
     */
    protected String getSchemaTypeForDirectMapping(XMLDirectMapping mapping, Schema workingSchema) {
        return getSchemaTypeForElement((XMLField) mapping.getField(), mapping.getAttributeClassification(), workingSchema);
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
    protected String getSchemaTypeForElement(XMLField xmlField, Class attrClass, Schema workingSchema) {
        String schemaTypeString = null;
        QName schemaType = xmlField.getSchemaType();
        if (schemaType != null) {
            schemaTypeString = getSchemaTypeString(schemaType, workingSchema);
        } else {
            if (attrClass != null && !attrClass.equals(ClassConstants.STRING)) {
                schemaTypeString = getSchemaTypeString((QName) XMLConversionManager.getDefaultJavaTypes().get(attrClass), workingSchema);
            } else {
                // default to string
                schemaTypeString = getSchemaTypeString(XMLConstants.STRING_QNAME, workingSchema);
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
    private XMLDescriptor getDescriptorByName(String javaClassName, List<XMLDescriptor> descriptors) {
        for (XMLDescriptor xDesc : descriptors) {
            if (xDesc.getJavaClassName().equals(javaClassName)) {
                return xDesc;
            }
        }
        return null;
    }
    
    /**
     * Process a given XMLDirectMapping.
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param workingSchema
     */
    private void processXMLDirectMapping(XMLDirectMapping mapping, Sequence seq, ComplexType ct, Schema workingSchema) {
        XPathFragment frag = ((XMLField) mapping.getField()).getXPathFragment();
        if (frag.isSelfFragment()) {
            // do nothing;
            return;
        }

        String schemaTypeString = getSchemaTypeForDirectMapping(mapping, workingSchema);
        if (frag.isAttribute()) {
            Attribute attr = buildAttribute(mapping, schemaTypeString);
            ct.getOrderedAttributes().add(attr);
        } else {
            Element elem = buildElement(frag, schemaTypeString, Occurs.ZERO, null);
            seq.addElement(elem);
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
    private void processXMLCompositeDirectCollectionMapping(XMLCompositeDirectCollectionMapping mapping, Sequence seq, ComplexType ct, Schema workingSchema) {
        XMLField field = ((XMLField) (mapping).getField());

        String schemaTypeString = getSchemaTypeForElement(field, mapping.getAttributeElementClass(), workingSchema);
        Element element;
        if (field.usesSingleNode()) {
            SimpleType st = new SimpleType();
            org.eclipse.persistence.internal.oxm.schema.model.List list = new org.eclipse.persistence.internal.oxm.schema.model.List();

            if (schemaTypeString == null) {
                schemaTypeString = getSchemaTypeString(XMLConstants.ANY_SIMPLE_TYPE_QNAME, workingSchema);
            }
            list.setItemType(schemaTypeString);
            st.setList(list);

            element = buildElement(field.getXPathFragment(), null, Occurs.ZERO, null);
            element.setSimpleType(st);
        } else {
            element = buildElement(field.getXPathFragment(), schemaTypeString, Occurs.ZERO, null);
            element.setMaxOccurs(Occurs.UNBOUNDED);
        }
        seq.addElement(element);
    }

    /**
     * Process a given XMLCompositeObjectMapping.
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     */
    private void processXMLCompositeObjectMapping(XMLCompositeObjectMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<XMLDescriptor> descriptors) {
        String refClassName = mapping.getReferenceClassName();
        XMLDescriptor refDesc = getDescriptorByName(refClassName, descriptors);
        if (refDesc == null) {
            throw DescriptorException.descriptorIsMissing(refClassName, mapping);
        }
        
        Element element = buildElement(((XMLField) mapping.getField()).getXPathFragment(), null, Occurs.ZERO, null);
        ComplexType ctype = null;
        
        if (refDesc.getSchemaReference() == null) {
            ctype = buildComplexType(true, refDesc, schemaForNamespace, workingSchema, properties, descriptors);
        } else {
            element.setType(getSchemaTypeString(refDesc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()), workingSchema));
        }
       
        XPathFragment frag = ((XMLField) mapping.getField()).getXPathFragment();
        String fragUri = frag.getNamespaceURI();
        if (fragUri != null) {
            // may need to add a global element
            Schema s = getSchema(fragUri, null, schemaForNamespace, properties);
            String targetNS = workingSchema.getTargetNamespace();
            if ((s.isElementFormDefault() && !fragUri.equals(targetNS)) || (!s.isElementFormDefault() && !fragUri.equals(""))) {
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
                element.setRef(frag.getShortName());
            } else {
                element.setComplexType(ctype);
            }
        } else if (ctype != null) {
            element.setComplexType(ctype);
        }
        seq.addElement(element);
    }

    /**
     * Process a given XMLCompositeCollectionMapping.
     * 
     * @param mapping
     * @param seq
     * @param ct
     * @param schemaForNamespace
     * @param workingSchema
     * @param properties
     * @param descriptors
     */
    private void processXMLCompositeCollectionMapping(XMLCompositeCollectionMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<XMLDescriptor> descriptors) {
        String refClassName = mapping.getReferenceClassName();
        XMLDescriptor refDesc = getDescriptorByName(refClassName, descriptors);
        if (refDesc == null) {
            throw DescriptorException.descriptorIsMissing(refClassName, mapping);
        }
        
        Element element = buildElement(((XMLField) mapping.getField()).getXPathFragment(), null, Occurs.ZERO, Occurs.UNBOUNDED);
        ComplexType ctype = null;
        
        if (refDesc.getSchemaReference() == null) {
            ctype = buildComplexType(true, refDesc, schemaForNamespace, workingSchema, properties, descriptors);
        } else {
            element.setType(getSchemaTypeString(refDesc.getSchemaReference().getSchemaContextAsQName(workingSchema.getNamespaceResolver()), workingSchema));
        }

        XPathFragment frag = ((XMLField) mapping.getField()).getXPathFragment();
        String fragUri = frag.getNamespaceURI();
        if (fragUri != null) {
            // may need to add a global element
            Schema s = getSchema(fragUri, null, schemaForNamespace, properties);
            String targetNS = workingSchema.getTargetNamespace();
            if ((s.isElementFormDefault() && !fragUri.equals(targetNS)) || (!s.isElementFormDefault() && !fragUri.equals(""))) {
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
                element.setMaxOccurs(Occurs.UNBOUNDED);
                element.setRef(frag.getShortName());
            } else {
                element.setComplexType(ctype);
            }
        } else if (ctype != null) {
            element.setComplexType(ctype);
        }
        seq.addElement(element);
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
    private void processMapping(DatabaseMapping mapping, Sequence seq, ComplexType ct, HashMap<String, Schema> schemaForNamespace, Schema workingSchema, SchemaModelGeneratorProperties properties, List<XMLDescriptor> descriptors) {
        if (mapping instanceof XMLDirectMapping) {
            processXMLDirectMapping((XMLDirectMapping) mapping, seq, ct, workingSchema);
        } else if (mapping instanceof XMLCompositeDirectCollectionMapping) {
            processXMLCompositeDirectCollectionMapping((XMLCompositeDirectCollectionMapping) mapping, seq, ct, workingSchema);
        } else if (mapping instanceof XMLCompositeObjectMapping) {
            processXMLCompositeObjectMapping((XMLCompositeObjectMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties, descriptors);
        } else if (mapping instanceof XMLCompositeCollectionMapping) {
            processXMLCompositeCollectionMapping((XMLCompositeCollectionMapping) mapping, seq, ct, schemaForNamespace, workingSchema, properties, descriptors);
        }
    }

    /**
     * Build and return an Attribute for a given XMLDirectMapping.
     * 
     * @param mapping
     * @param schemaType
     * @return
     */
    protected Attribute buildAttribute(XMLDirectMapping mapping, String schemaType) {
        XPathFragment frag = ((XMLField) mapping.getField()).getXPathFragment();
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
    private Element buildElement(XPathFragment frag, String schemaType, String minOccurs, String maxOccurs) {
        Element element = new Element();
        element.setName(frag.getLocalName());
        element.setMinOccurs(minOccurs);
        element.setMaxOccurs(maxOccurs);
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
    private Element buildElement(String name, String minOccurs, String maxOccurs) {
        Element element = new Element();
        element.setName(name);
        element.setMinOccurs(minOccurs);
        element.setMaxOccurs(maxOccurs);
        return element;
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
        if (prefix == null && !uri.equals(workingSchema.getDefaultNamespace())) {
            if (uri.equals(XMLConstants.SCHEMA_URL)) {
                prefix = workingSchema.getNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_PREFIX);
            } else if (uri.equals(XMLConstants.SCHEMA_INSTANCE_URL)) {
                prefix = workingSchema.getNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_INSTANCE_PREFIX);
            } else {
                prefix = workingSchema.getNamespaceResolver().generatePrefix();
            }
            workingSchema.getNamespaceResolver().put(prefix, uri);
        }
        if (prefix != null) {
            schemaTypeString = prefix + COLON + schemaTypeString;
        }
        return schemaTypeString;
    }

    /**
     * Adds each namespace in the given resolver to the schema.
     * 
     * @param nr
     * @param workingSchema
     */
    private void addNamespacesToWorkingSchema(NamespaceResolver nr, Schema workingSchema) {
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
    private Schema buildNewSchema(String uri, NamespaceResolver nr, int schemaCount, SchemaModelGeneratorProperties properties) {
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

        if (!uri.equals(EMPTY_STRING)) {
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
        return schema;
    }

    /**
     * Determines if a given schema contains an import for a given schema name.
     * 
     * @param schema
     * @param schemaName
     * @return
     */
    private boolean importExists(Schema schema, String schemaName) {
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
    protected QName getDefaultRootElementAsQName(XMLDescriptor desc, String qualifiedTableName) {
        QName qName = null;
        NamespaceResolver nsResolver = desc.getNamespaceResolver();
        int idx = qualifiedTableName.indexOf(COLON);
        String localName = qualifiedTableName.substring(idx + 1);
        if (idx > -1) {
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
}