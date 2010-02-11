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
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.*;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.sessions.Project;

/**
 * <p><b>Purpose</b> SDOSchemaGenerator generates an XSD (returned as a String)
 * from a list of SDO Type objects. Populates an org.eclipse.persistence.internal.oxm.schema.model.Schema
 * object and makes use of org.eclipse.persistence.internal.oxm.schema.SchemaModelProject to marshal
 * the Schema Object to XML.
 * @see commonj.sdo.XSDHelper
 */
public class SDOSchemaGenerator {
    private Map namespaceToSchemaLocation;
    private SchemaLocationResolver schemaLocationResolver;
    private List allTypes;
    private Schema generatedSchema;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOSchemaGenerator(HelperContext aContext) {
        aHelperContext = aContext;
    }

    /**
     * <p>Method to generate an XSD. Note the following:<ul>
     * <li> All types must have same URI
     * <li> Referenced types in same URI will also be generated in schema
     * <li> Includes will never be generated
     * <li> Imports will be generated for referenced types in other URIs
     * </ul>
     * @param types The list of commonj.sdo.Type objects to generate the XSD from
     * @param aSchemaLocationResolver implementation of the org.eclipse.persistence.sdo.helper.SchemaLocationResolver interface
     * used for getting the value of the schemaLocation attribute of generated imports and includes
     * @return String The generated XSD.
     */
    public String generate(List types, SchemaLocationResolver aSchemaLocationResolver) {
        schemaLocationResolver = aSchemaLocationResolver;
        if ((types == null) || (types.size() == 0)) {
            throw new IllegalArgumentException("No Schema was generated from null or empty list of types.");
        }

        String uri = null;
        Type firstType = (Type)types.get(0);
        uri = firstType.getURI();

        allTypes = types;
        generateSchema(uri, types);
        
        //Now we have a built schema model        
        Project p = new SchemaModelProject();
        Vector generatedNamespaces = generatedSchema.getNamespaceResolver().getNamespaces();
        XMLDescriptor desc = ((XMLDescriptor)p.getDescriptor(Schema.class));
        for (int i = 0; i < generatedNamespaces.size(); i++) {
            Namespace next = (Namespace)generatedNamespaces.get(i);
            desc.getNamespaceResolver().put(next.getPrefix(), next.getNamespaceURI());

            if (next.getNamespaceURI().equals(SDOConstants.SDO_URL) || next.getNamespaceURI().equals(SDOConstants.SDOXML_URL) || next.getNamespaceURI().equals(SDOConstants.SDOJAVA_URL)) {
                if (!importExists(generatedSchema.getImports(), next.getNamespaceURI())) {
                    Import theImport = new Import();
                    theImport.setNamespace(next.getNamespaceURI());
                    String schemaLocation = "classpath:/xml/";
                    if (next.getNamespaceURI().equals(SDOConstants.SDO_URL)) {
                        schemaLocation += "sdoModel.xsd";
                    } else if (next.getNamespaceURI().equals(SDOConstants.SDOXML_URL)) {
                        schemaLocation += "sdoXML.xsd";
                    } else if (next.getNamespaceURI().equals(SDOConstants.SDOJAVA_URL)) {
                        schemaLocation += "sdoJava.xsd";
                    }
                    try {
                        new URL(schemaLocation);
                        theImport.setSchemaLocation(schemaLocation);
                    } catch (MalformedURLException e) {
                        // DO NOTHING - fix for bug 6054754 to add custom schemalocation if possible
                    }                    
                    generatedSchema.getImports().add(theImport);
                }
            }
        }

        XMLLogin login = new XMLLogin();
        login.setDatasourcePlatform(new DOMPlatform());
        p.setDatasourceLogin(login);

        XMLContext context = new XMLContext(p);

        XMLMarshaller marshaller = context.createMarshaller();

        StringWriter generatedSchemaWriter = new StringWriter();
        marshaller.marshal(generatedSchema, generatedSchemaWriter);
        return generatedSchemaWriter.toString();
    }

    /**
     * <p>Method to generate an XSD. Note the following:<ul>
     * <li> All types must have same URI
     * <li> Referenced types in same URI will also be generated in schema
     * <li> Includes will never be generated
     * <li> Imports will be generated for referenced types in other URIs
     * </ul>
     * @param types The list of commonj.sdo.Type objects to generate the XSD from
     * @param aNamespaceToSchemaLocation map of namespaces to schemaLocations
     * used for getting the value of the schemaLocation attribute of generated imports and includes
     * @return String The generated XSD.
    */
    public String generate(List types, Map aNamespaceToSchemaLocation) {
        if ((types == null) || (types.size() == 0)) {
            throw new IllegalArgumentException("No Schema was generated from null or empty list of types.");
        }

        String uri = null;
        namespaceToSchemaLocation = aNamespaceToSchemaLocation;
 
        Type firstType = (Type)types.get(0);
        if (firstType == null) {
            throw new IllegalArgumentException("No Schema was generated from a list of types containing null elements");
        } else {
            uri = firstType.getURI();
        }
        allTypes = types;
        generateSchema(uri, types);

        //Now we have a built schema model						      
        Project p = new SchemaModelProject();
        Vector namespaces = generatedSchema.getNamespaceResolver().getNamespaces();
        for (int i = 0; i < namespaces.size(); i++) {
            Namespace next = (Namespace)namespaces.get(i);
            ((XMLDescriptor)p.getDescriptor(Schema.class)).getNamespaceResolver().put(next.getPrefix(), next.getNamespaceURI());
        }

        XMLLogin login = new XMLLogin();
        login.setDatasourcePlatform(new DOMPlatform());
        p.setDatasourceLogin(login);
        XMLContext context = new XMLContext(p);
        XMLMarshaller marshaller = context.createMarshaller();

        StringWriter generatedSchemaWriter = new StringWriter();
        marshaller.marshal(generatedSchema, generatedSchemaWriter);
        return generatedSchemaWriter.toString();
    }

    private void generateSchema(String uri, List typesWithSameUri) { 
        generatedSchema = new Schema();
        generatedSchema.setTargetNamespace(uri);
        generatedSchema.setDefaultNamespace(uri);

        generatedSchema.setAttributeFormDefault(false);
        generatedSchema.setElementFormDefault(true);
        String javaPackage = null;

        for (int i = 0; i < typesWithSameUri.size(); i++) {
            SDOType nextType = (SDOType)typesWithSameUri.get(i);
            if ((nextType.getBaseTypes() != null) && (nextType.getBaseTypes().size() > 1)) {
                //A schema can not be generated because the following type has more than 1 base type + type
            }
            String nextUri = nextType.getURI();
            if (nextUri != uri) {
                //all types must have same uri
            }

            if (!nextType.isDataType()) {
                String fullName = nextType.getInstanceClassName();
                if (fullName != null) {
                    String nextPackage = null;

                    int lastDot = fullName.lastIndexOf('.');
                    if (lastDot != -1) {
                        nextPackage = fullName.substring(0, lastDot);
                    }

                    if (nextPackage != null) {
                        javaPackage = nextPackage;
                    }
                }
            }            

            if (nextType.isDataType()) {
                //generate simple type
                SimpleType generatedType = generateSimpleType(nextType);
                generatedSchema.addTopLevelSimpleTypes(generatedType);
            } else {
                //generate complex type
                ComplexType generatedType = generateComplexType(nextType);
                generatedSchema.addTopLevelComplexTypes(generatedType);

                //generate global element for the complex type generated above
                Element element = buildElementForComplexType(generatedSchema, generatedType);
                if (element != null) {
                    generatedSchema.addTopLevelElement(element);
                }
            }
        }
        if (javaPackage != null) {
            getPrefixForURI(SDOConstants.SDOJAVA_URL);
            generatedSchema.getAttributesMap().put(SDOConstants.SDOJAVA_PACKAGE_QNAME, javaPackage);
        }
    }

    private SimpleType generateSimpleType(Type type) {
        SDOType sdoType = (SDOType) type;
        SimpleType simpleType = new SimpleType();

        String xsdLocalName = sdoType.getXsdLocalName();
        if (xsdLocalName != null) {
            simpleType.setName(xsdLocalName);
        } else {
            simpleType.setName(sdoType.getName());
        }

        if ((sdoType.getAppInfoElements() != null) && (sdoType.getAppInfoElements().size() > 0)) {
            Annotation annotation = new Annotation();

            annotation.setAppInfo(sdoType.getAppInfoElements());
            simpleType.setAnnotation(annotation);
        }
        
        if ((xsdLocalName != null) && !(xsdLocalName.equals(sdoType.getName()))) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_NAME, sdoXmlPrefix);
            simpleType.getAttributesMap().put(qname, sdoType.getName());
        }

        if ((sdoType.getAliasNames() != null) && (sdoType.getAliasNames().size() > 0)) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            String aliasNamesString = buildAliasNameString(sdoType.getAliasNames());
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_ALIASNAME, sdoXmlPrefix);
            simpleType.getAttributesMap().put(qname, aliasNamesString);
        }

        Object value = sdoType.get(SDOConstants.JAVA_CLASS_PROPERTY);
        if ((value != null) && value instanceof String) {
            String sdoJavaPrefix = getPrefixForURI(SDOConstants.SDOJAVA_URL);
            QName qname = new QName(SDOConstants.SDOJAVA_URL, SDOConstants.SDOJAVA_INSTANCECLASS, sdoJavaPrefix);
            simpleType.getAttributesMap().put(qname, value);
        }

        SDOType baseType = null;
        if ((sdoType.getBaseTypes() != null) && (sdoType.getBaseTypes().size() > 0) && ((SDOType) sdoType.getBaseTypes().get(0) != null)) {
            baseType = (SDOType) sdoType.getBaseTypes().get(0);
        } 

        if (baseType != null) {
            Restriction restriction = new Restriction();
            addTypeToListIfNeeded(sdoType, baseType);
            QName schemaType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getXSDTypeFromSDOType(baseType);

            if (schemaType != null) {
                String prefix = getPrefixStringForURI(schemaType.getNamespaceURI());
                restriction.setBaseType(prefix + schemaType.getLocalPart());
            } else {
                String prefix = getPrefixStringForURI(baseType.getURI());
                restriction.setBaseType(prefix + baseType.getName());
            }
            simpleType.setRestriction(restriction);
        }

        return simpleType;
    }

    private ComplexType generateComplexType(Type type) {
        SDOType sdoType = (SDOType) type;
        ComplexType complexType = new ComplexType();
        String xsdLocalName = sdoType.getXsdLocalName();
        if (xsdLocalName != null) {
            complexType.setName(xsdLocalName);
        } else {
            complexType.setName(sdoType.getName());
        }

        if ((xsdLocalName != null) && !(xsdLocalName.equals(sdoType.getName()))) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_NAME, sdoXmlPrefix);
            complexType.getAttributesMap().put(qname, sdoType.getName());
        }

        complexType.setAbstractValue(sdoType.isAbstract());
        if ((sdoType.getAppInfoElements() != null) && (sdoType.getAppInfoElements().size() > 0)) {
            Annotation annotation = new Annotation();
            annotation.setAppInfo(sdoType.getAppInfoElements());
            complexType.setAnnotation(annotation);
        }

        if ((sdoType.getAliasNames() != null) && (sdoType.getAliasNames().size() > 0)) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            String aliasNamesString = buildAliasNameString(sdoType.getAliasNames());

            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_ALIASNAME, sdoXmlPrefix);
            complexType.getAttributesMap().put(qname, aliasNamesString);
        }

        complexType.setMixed(sdoType.isSequenced());
        Type baseType = null;
        if ((sdoType.getBaseTypes() != null) && (sdoType.getBaseTypes().size() > 0) && ((Type)sdoType.getBaseTypes().get(0) != null)) {
            baseType = (Type)sdoType.getBaseTypes().get(0);

            //baseName = base.getName();
            //String baseURI = ((Type)type.getBaseTypes().get(0)).getURI();
            //if (baseURI != type.getURI()) {
            //need to add something  to track referenced uris for includes/imports
            //}
        }

        if (baseType != null) {
            addTypeToListIfNeeded(sdoType, baseType);
            Extension extension = new Extension();
            QName schemaType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getXSDTypeFromSDOType(baseType);

            //fixed below for bug5893546
            if (schemaType != null) {
                extension.setBaseType(getPrefixStringForURI(schemaType.getNamespaceURI()) + schemaType.getLocalPart());
            } else if ((baseType.getURI() == null) || (baseType.getURI().equalsIgnoreCase(generatedSchema.getTargetNamespace()))) {
                extension.setBaseType(baseType.getName());
            } else {
                extension.setBaseType(getPrefixStringForURI(baseType.getURI()) + baseType.getName());
            }

            buildElementsAndAttributes(extension, sdoType);
            ComplexContent complexContent = new ComplexContent();
            complexContent.setExtension(extension);
            complexType.setComplexContent(complexContent);
            return complexType;
        }

        buildElementsAndAttributes(complexType, sdoType);

        return complexType;
    }

    private void buildElementsAndAttributes(Object owner, Type type) {
        List properties = ((SDOType) type).getDeclaredProperties();
        NestedParticle nestedParticle = null;

        if ((properties == null) || (properties.size() == 0)) {
            if (type.isOpen()) {
                nestedParticle = new Sequence();
            } else {
                return;
            }
        } else {
            if (type.isSequenced()) {
                nestedParticle = new Choice();
                nestedParticle.setMaxOccurs(Occurs.UNBOUNDED);
            } else {
                nestedParticle = new Sequence();
            }
        }
        for (int i = 0; i < properties.size(); i++) {
            Property nextProperty = (Property)properties.get(i);

            if (aHelperContext.getXSDHelper().isElement(nextProperty)) {
                Element elem = buildElement(nextProperty, nestedParticle);
                nestedParticle.addElement(elem);
            } else if (aHelperContext.getXSDHelper().isAttribute(nextProperty)) {
                Attribute attr = buildAttribute(nextProperty);
                if (owner instanceof ComplexType) {
                    ((ComplexType)owner).getOrderedAttributes().add(attr);
                } else if (owner instanceof Extension) {
                    ((Extension)owner).getOrderedAttributes().add(attr);
                }
            }
        }
        if (type.isOpen()) {
            Any any = new Any();
            any.setProcessContents(AnyAttribute.LAX);
            any.setMaxOccurs(Occurs.UNBOUNDED);
            nestedParticle.addAny(any);

            AnyAttribute anyAttribute = new AnyAttribute();
            anyAttribute.setProcessContents(AnyAttribute.LAX);
            if (owner instanceof ComplexType) {
                ((ComplexType)owner).setAnyAttribute(anyAttribute);
            }
        }

        if (!nestedParticle.isEmpty()) {
            if (owner instanceof ComplexType) {
                ((ComplexType)owner).setTypeDefParticle((TypeDefParticle)nestedParticle);
                //baseType.getAttributes().add(attr);
            } else if (owner instanceof Extension) {
                ((Extension)owner).setTypeDefParticle((TypeDefParticle)nestedParticle);
            }
        }
    }

    private void addSimpleComponentAnnotations(SimpleComponent sc, Property property, boolean element) {
        SDOProperty sdoProperty = (SDOProperty) property;
        if (sdoProperty.isReadOnly()) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_READONLY, sdoXmlPrefix);
            sc.getAttributesMap().put(qname, "true");
        }
        if ((sdoProperty.getAliasNames() != null) && (sdoProperty.getAliasNames().size() > 0)) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            String aliasNamesString = buildAliasNameString(sdoProperty.getAliasNames());
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_ALIASNAME, sdoXmlPrefix);
            sc.getAttributesMap().put(qname, aliasNamesString);
        }

        String xsdLocalName = sdoProperty.getXsdLocalName();

        if ((xsdLocalName != null) && !(xsdLocalName.equals(sdoProperty.getName()))) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_NAME, sdoXmlPrefix);
            sc.getAttributesMap().put(qname, sdoProperty.getName());
        }

        if ((element && !sdoProperty.isContainment() && !sdoProperty.getType().isDataType()) || (!element && !sdoProperty.getType().isDataType())) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            String uri = sdoProperty.getType().getURI();
            String value = sdoProperty.getType().getName();
            if (uri != null) {
                String typePrefix = getPrefixForURI(uri);
                if (typePrefix != null) {
                    value = typePrefix + ":" + value;
                }
            }
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_PROPERTYTYPE, sdoXmlPrefix);
            sc.getAttributesMap().put(qname, value);
        }

        if (sdoProperty.getOpposite() != null) {
            String value = sdoProperty.getOpposite().getName();
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_OPPOSITEPROPERTY, sdoXmlPrefix);
            sc.getAttributesMap().put(qname, value);
        }

        Property xmlDataTypeProperty = aHelperContext.getTypeHelper().getOpenContentProperty(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_DATATYPE);
        Type dataType = (Type) sdoProperty.get(xmlDataTypeProperty);
        if (dataType == null) {
            dataType = getAutomaticDataTypeForType(sdoProperty.getType());
        }
        if (dataType != null && !shouldSuppressDataType(sdoProperty, dataType)) {        	
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_DATATYPE, sdoXmlPrefix);
            String dataTypeString = dataType.getName();
            if (dataType.getURI() != null) {
                String dataTypePrefix = getPrefixForURI(dataType.getURI());
                if (dataTypePrefix != null) {
                    dataTypeString = dataTypePrefix + ":" + dataTypeString;
                }
            }
            sc.getAttributesMap().put(qname, dataTypeString);
        }

        if (element) {
            String mimeType = (String) sdoProperty.get(SDOConstants.MIME_TYPE_PROPERTY);
            if (mimeType != null) {
                String prefix = getPrefixForURI(SDOConstants.MIMETYPE_URL);
                QName qname = new QName(SDOConstants.XML_MIME_TYPE_QNAME.getNamespaceURI(), SDOConstants.XML_MIME_TYPE_QNAME.getLocalPart(), prefix);
                sc.getAttributesMap().put(qname, mimeType);
            } else {
                mimeType = (String) sdoProperty.get(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY);
                if (mimeType != null) {
                    String prefix = getPrefixForURI(SDOConstants.ORACLE_SDO_URL);
                    QName qname = new QName(SDOConstants.XML_MIME_TYPE_PROPERTY_QNAME.getNamespaceURI(), SDOConstants.XML_MIME_TYPE_PROPERTY_QNAME.getLocalPart(), prefix);
                    sc.getAttributesMap().put(qname, mimeType);
                }
            }
        }
    }

    private boolean shouldSuppressDataType(SDOProperty prop, Type dataType){
    	if(prop.isNullable()){
    		SDOType type = prop.getType();    		
    		if(dataType == SDOConstants.SDO_BOOLEANOBJECT && (type == SDOConstants.SDO_BOOLEAN || type == SDOConstants.SDO_BOOLEANOBJECT )){
    			return true;
    		}
    		if(dataType == SDOConstants.SDO_BYTEOBJECT && (type == SDOConstants.SDO_BYTE || type == SDOConstants.SDO_BYTEOBJECT )){
    			return true;
    		}
    		if(dataType == SDOConstants.SDO_CHARACTEROBJECT && (type == SDOConstants.SDO_CHARACTER || type == SDOConstants.SDO_CHARACTEROBJECT )){
    			return true;
    		}
    		if(dataType == SDOConstants.SDO_DOUBLEOBJECT && (type == SDOConstants.SDO_DOUBLE || type == SDOConstants.SDO_DOUBLEOBJECT )){
    			return true;
    		}
    		if(dataType == SDOConstants.SDO_FLOATOBJECT && (type == SDOConstants.SDO_FLOAT || type == SDOConstants.SDO_FLOATOBJECT )){
    			return true;
    		}
    		if(dataType == SDOConstants.SDO_INTOBJECT && (type == SDOConstants.SDO_INT || type == SDOConstants.SDO_INTOBJECT )){
    			return true;
    		}    		
    		if(dataType == SDOConstants.SDO_LONGOBJECT && (type == SDOConstants.SDO_LONG || type == SDOConstants.SDO_LONGOBJECT )){
    			return true;
    		}
    		if(dataType == SDOConstants.SDO_SHORTOBJECT && (type == SDOConstants.SDO_SHORT || type == SDOConstants.SDO_SHORTOBJECT )){
    			return true;
    		}
        }
    	return false;
    }
    
    private String buildAliasNameString(List aliasNames) {
        String aliasNamesString = "";
        int size = aliasNames.size();
        for (int i = 0; i < size; i++) {
            String nextName = (String)aliasNames.get(i);
            aliasNamesString += nextName;
            if (i < (size - 1)) {
                aliasNamesString += " ";
            }
        }
        return aliasNamesString;
    }

    private Element buildElement(Property property, NestedParticle nestedParticle) {
        SDOProperty sdoProperty = (SDOProperty) property;
        Element elem = new Element();
        String xsdLocalName = sdoProperty.getXsdLocalName();
        if (xsdLocalName != null) {
            elem.setName(xsdLocalName);
        } else {
            elem.setName(sdoProperty.getName());
        }
        elem.setMinOccurs(Occurs.ZERO);
        elem.setNillable(sdoProperty.isNullable());
        if ((sdoProperty.getAppInfoElements() != null) && (sdoProperty.getAppInfoElements().size() > 0)) {
            Annotation annotation = new Annotation();
            annotation.setAppInfo(sdoProperty.getAppInfoElements());
            elem.setAnnotation(annotation);
        }

        // process default values that are defined in the schema (not via primitive numeric Object wrapped pseudo defaults)
        if (sdoProperty.isDefaultSet()) {
            if (!sdoProperty.isMany() && sdoProperty.getType().isDataType()) {
                XMLConversionManager xmlConversionManager = ((SDOXMLHelper)aHelperContext.getXMLHelper()).getXmlConversionManager();
                elem.setDefaultValue((String)xmlConversionManager.convertObject(sdoProperty.getDefault(), ClassConstants.STRING, sdoProperty.getXsdType()));
            }

        }

        addSimpleComponentAnnotations(elem, sdoProperty, true);

        /*
         When containment is true, then DataObjects of that Type will appear as nested elements in an XML document.
        When containment is false and the property's type is a DataObject, a URI reference
        to the element containing the DataObject is used and an sdo:propertyType
        declaration records the target type. Values in XML documents will be of the form
        "#xpath" where the xpath is an SDO DataObject XPath subset. It is typical to
        customize the declaration to IDREF if the target element has an attribute with type
        customized to ID.

        [TYPE.NAME] is the type of the element. If property.type.dataType is true,
        [TYPE.NAME] is the name of the XSD built in SimpleType corresponding to
        property.type, where the prefix is for the xsd namespace. Otherwise,
        [TYPE.NAME] is property.type.name where the tns: prefix is determined by the
        namespace declaration for the Type's URI.
         */
        Type schemaSDOType = null;
        QName schemaType = sdoProperty.getXsdType();
        if (schemaType != null) {
            schemaSDOType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getType(schemaType.getNamespaceURI(), schemaType.getLocalPart());

            if ((sdoProperty.getType() == SDOConstants.SDO_STRING) && (schemaSDOType != SDOConstants.SDO_STRING)) {
                String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
                QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_STRING_NAME, sdoXmlPrefix);
                elem.getAttributesMap().put(qname, "true");
            }
        }
        if (!sdoProperty.isContainment() && !sdoProperty.getType().isDataType()) {
            schemaType = SDOConstants.ANY_URI_QNAME;
        }

        Type propertyType = sdoProperty.getType();

        if (propertyType != null) {
            if (sdoProperty.getContainingType() != null) {
                addTypeToListIfNeeded(sdoProperty.getContainingType(), propertyType);
            }

            if (schemaType == null) {
                schemaType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getXSDTypeFromSDOType(propertyType);
            }

            //get url for prefix in namespace resolver and map sure it is added to the schema if necessary
            if (schemaType != null) {
                elem.setType(getPrefixStringForURI(schemaType.getNamespaceURI()) + schemaType.getLocalPart());
                if (schemaSDOType != null) {
                    addTypeToListIfNeeded(sdoProperty.getContainingType(), schemaSDOType);
                }
            } else if ((propertyType.getURI() == null) || (propertyType.getURI().equalsIgnoreCase(generatedSchema.getTargetNamespace()))) {
                String xsdTypeLocalName = ((SDOType)propertyType).getXsdLocalName();
                if (xsdTypeLocalName != null) {
                    elem.setType(xsdTypeLocalName);
                } else {
                    elem.setType(propertyType.getName());
                }
            } else {
                String nameString = null;
                String xsdTypeLocalName = ((SDOType)propertyType).getXsdLocalName();
                if (xsdTypeLocalName != null) {
                    nameString = xsdTypeLocalName;
                } else {
                    nameString = propertyType.getName();
                }
                
                elem.setType(getPrefixStringForURI(propertyType.getURI()) + nameString);
            }
        } else {
            elem.setType("anyURI");
        }
        if (sdoProperty.isMany()) {
            elem.setMaxOccurs(Occurs.UNBOUNDED);
        } else if (nestedParticle.getMaxOccurs() == Occurs.UNBOUNDED) {
            //this means property.isMany==false and the owning sequence of choice is unbounded Jira SDO-3
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_MANY, sdoXmlPrefix);
            elem.getAttributesMap().put(qname, "false");
        }

        return elem;
    }

    private Attribute buildAttribute(Property property) {
        Attribute attr = new Attribute();
        String xsdLocalName = ((SDOProperty)property).getXsdLocalName();
        if (xsdLocalName != null) {
            attr.setName(xsdLocalName);
        } else {
            attr.setName(property.getName());
        }

        if ((((SDOProperty)property).getAppInfoElements() != null) && (((SDOProperty)property).getAppInfoElements().size() > 0)) {
            Annotation annotation = new Annotation();
            annotation.setAppInfo(((SDOProperty)property).getAppInfoElements());
            attr.setAnnotation(annotation);
        }

        // process default values that are defined in the schema (not via primitive numeric Object wrapped pseudo defaults)
        if (((SDOProperty)property).isDefaultSet()) {
            if (!property.isMany() && ((SDOType)property.getType()).isDataType()) {
                XMLConversionManager xmlConversionManager = ((SDOXMLHelper)aHelperContext.getXMLHelper()).getXmlConversionManager();
                attr.setDefaultValue((String)xmlConversionManager.convertObject(property.getDefault(), ClassConstants.STRING, ((SDOProperty)property).getXsdType()));
            }            
        }
        addSimpleComponentAnnotations(attr, property, false);

        Type propertyType = property.getType();
        QName schemaType = ((SDOProperty)property).getXsdType();

        if (schemaType != null) {
            Type schemaSDOType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getType(schemaType.getNamespaceURI(), schemaType.getLocalPart());

            if ((property.getType() == SDOConstants.SDO_STRING) && (schemaSDOType != SDOConstants.SDO_STRING)) {
                String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
                QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_STRING_NAME, sdoXmlPrefix);
                attr.getAttributesMap().put(qname, "true");
            }
        }

        if (!((SDOType)property.getType()).isDataType()) {
            schemaType = SDOConstants.ANY_URI_QNAME;
        }

        if (propertyType != null) {
            if (property.getContainingType() != null) {
                addTypeToListIfNeeded(property.getContainingType(), propertyType);
            }
            if (schemaType == null) {
                schemaType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getXSDTypeFromSDOType(propertyType);
            }
            if (schemaType != null) {
                attr.setType(getPrefixStringForURI(schemaType.getNamespaceURI()) + schemaType.getLocalPart());
            } else if ((propertyType.getURI() == null) || (propertyType.getURI().equalsIgnoreCase(generatedSchema.getTargetNamespace()))) {
                String xsdTypeLocalName = ((SDOType)propertyType).getXsdLocalName();
                if (xsdTypeLocalName != null) {
                    attr.setType(xsdTypeLocalName);
                } else {
                    attr.setType(propertyType.getName());
                }
            } else {
                String nameString = null;
                String xsdTypeLocalName = ((SDOType)propertyType).getXsdLocalName();

                if (xsdTypeLocalName != null) {
                    nameString = xsdTypeLocalName;
                } else {
                    nameString = propertyType.getName();
                }
             
                attr.setType(getPrefixStringForURI(propertyType.getURI()) + nameString);
            }

            //get url for prefix in namespace resolver and map sure it is added to the schema if necessary

            /*
            if (schemaType != null) {
            attr.setType(getPrefixStringForURI(schemaType.getNamespaceURI()) + schemaType.getLocalPart());
            } else {
            attr.setType(propertyType.getName());
            }*/
        }

        return attr;
    }

    private void addTypeToListIfNeeded(Type sourceType, Type targetType) {
        if ((targetType.getURI() != null) && !targetType.getURI().equals(SDOConstants.SDO_URL) && !targetType.getURI().equals(SDOConstants.SDOJAVA_URL) && !targetType.getURI().equals(SDOConstants.SDOXML_URL)) {
            boolean alreadyGenerated = allTypes.contains(targetType);
            String schemaLocation = null;
            if (namespaceToSchemaLocation != null) {
                schemaLocation = (String)namespaceToSchemaLocation.get(targetType.getURI());

                if (targetType.getURI().equals(generatedSchema.getTargetNamespace())) {
                    if (!alreadyGenerated) {
                        allTypes.add(targetType);
                    }
                } else {
                    if(!importExists(generatedSchema.getImports(), schemaLocation)){                    
                        Import theImport = new Import();
                        theImport.setSchemaLocation(schemaLocation);
                        theImport.setNamespace(targetType.getURI());                        
                        generatedSchema.getImports().add(theImport);
                    }
                }
            } else if (schemaLocationResolver != null) {
                schemaLocation = schemaLocationResolver.resolveSchemaLocation(sourceType, targetType);
                if (schemaLocation != null) {
                    if (targetType.getURI().equals(generatedSchema.getTargetNamespace())) {                        
                        if(!importExists(generatedSchema.getIncludes(), schemaLocation)){                        
                            Include include = new Include();
                            include.setSchemaLocation(schemaLocation);
                            generatedSchema.getIncludes().add(include);                            
                            // 20060713 remove type from List of types when adding an include
                            allTypes.remove(targetType);
                        }
                    } else {                        
                        if(!importExists(generatedSchema.getImports(), schemaLocation)){
                            Import theImport = new Import();
                            theImport.setSchemaLocation(schemaLocation);
                            theImport.setNamespace(targetType.getURI());
                            generatedSchema.getImports().add(theImport);                            
                        }
                    }
                } else {
                    if (!alreadyGenerated) {
                        //we can #1 add to list of allTypes or #2 make an appropriate include
                        if (targetType.getURI().equals(generatedSchema.getTargetNamespace())) {
                            allTypes.add(targetType);
                        }
                    }
                }
            } else {
                if (!alreadyGenerated) {
                    //we can #1 add to list of allTypes or #2 make an appropriate include
                    if (targetType.getURI().equals(generatedSchema.getTargetNamespace())) {
                        allTypes.add(targetType);
                    }
                }
            }
        }
    }

    private Element buildElementForComplexType(Schema schema, ComplexType type) {
        Element elem = new Element();
        String name = type.getName();
        if (name == null) {
            return null;
        }
        String lowerName = Character.toLowerCase(name.charAt(0)) + name.substring(1, name.length());

        Object exists = schema.getTopLevelElements().get(lowerName);
        if (exists != null) {
            elem.setName(name);
        } else {
            elem.setName(lowerName);
        }

        elem.setType(type.getName());

        return elem;
    }

    private String getPrefixStringForURI(String uri) {
        if(null == uri || SDOConstants.EMPTY_STRING.equals(uri)) {
            return SDOConstants.EMPTY_STRING;
        }
        String prefix = getPrefixForURI(uri);
        if (prefix == null) {
            return SDOConstants.EMPTY_STRING;
        } else {
            return prefix + ":";
        }
    }

    private String getPrefixForURI(String uri) {
        String prefix = null;
        if (uri.equals(generatedSchema.getTargetNamespace())) {
            return null;
        } else if (uri.equals(XMLConstants.SCHEMA_URL)) {
            return XMLConstants.SCHEMA_PREFIX;
        } else if (uri.equals(SDOConstants.SDO_URL)) {
            prefix = generatedSchema.getNamespaceResolver().resolveNamespaceURI(uri);
            if (prefix == null) {
                prefix = generatedSchema.getNamespaceResolver().generatePrefix(SDOConstants.SDO_PREFIX);
                generatedSchema.getNamespaceResolver().put(prefix, uri);
            }
        } else if (uri.equals(SDOConstants.SDOJAVA_URL)) {
            prefix = generatedSchema.getNamespaceResolver().resolveNamespaceURI(uri);
            if (prefix == null) {
                prefix = generatedSchema.getNamespaceResolver().generatePrefix(SDOConstants.SDOJAVA_PREFIX);
                generatedSchema.getNamespaceResolver().put(prefix, uri);
            }
        } else if (uri.equals(SDOConstants.SDOXML_URL)) {
            prefix = generatedSchema.getNamespaceResolver().resolveNamespaceURI(uri);
            if (prefix == null) {
                prefix = generatedSchema.getNamespaceResolver().generatePrefix(SDOConstants.SDOXML_PREFIX);
                generatedSchema.getNamespaceResolver().put(prefix, uri);
            }
        }
        if (prefix == null) {
            prefix = generatedSchema.getNamespaceResolver().resolveNamespaceURI(uri);
        }
        if (prefix != null) {
            return prefix;
        } else {
            String generatedPrefix = generatedSchema.getNamespaceResolver().generatePrefix();
            generatedSchema.getNamespaceResolver().put(generatedPrefix, uri);
            return generatedPrefix;
        }
    }

    private Type getAutomaticDataTypeForType(Type theType) {
        // Section 10.1 of the spec 
        //For the SDO Java Types, the corresponding base SDO Type is used. For the SDO Java
        // Types, and for SDO Date, an sdo:dataType annotation is generated on the XML attribute
        // or element referring to the SDO Type.
        if (theType == SDOConstants.SDO_BOOLEANOBJECT) {
            return SDOConstants.SDO_BOOLEANOBJECT;
        } else if (theType == SDOConstants.SDO_BYTEOBJECT) {
            return SDOConstants.SDO_BYTEOBJECT;
        } else if (theType == SDOConstants.SDO_CHARACTEROBJECT) {
            return SDOConstants.SDO_CHARACTEROBJECT;
        } else if (theType == SDOConstants.SDO_DOUBLEOBJECT) {
            return SDOConstants.SDO_DOUBLEOBJECT;
        } else if (theType == SDOConstants.SDO_INTOBJECT) {
            return SDOConstants.SDO_INTOBJECT;
        } else if (theType == SDOConstants.SDO_FLOATOBJECT) {
            return SDOConstants.SDO_FLOATOBJECT;
        } else if (theType == SDOConstants.SDO_LONGOBJECT) {
            return SDOConstants.SDO_LONGOBJECT;
        } else if (theType == SDOConstants.SDO_SHORTOBJECT) {
            return SDOConstants.SDO_SHORTOBJECT;
        } else if (theType == SDOConstants.SDO_DATE) {
            return SDOConstants.SDO_DATE;
        } else if (theType == SDOConstants.SDO_DATETIME) {
            return SDOConstants.SDO_DATETIME;
        }
        return null;
    }
    
     private boolean importExists(java.util.List imports, String schemaName){                
        for(int i=0;i < imports.size();i++){
            //Cast to the parent class, since this could be an Include or an Import
            Include nextImport = (Include)imports.get(i);
            if(nextImport.getSchemaLocation() != null && nextImport.getSchemaLocation().equals(schemaName)){
                return true;
            }
        }
        return false;                
    }
}
