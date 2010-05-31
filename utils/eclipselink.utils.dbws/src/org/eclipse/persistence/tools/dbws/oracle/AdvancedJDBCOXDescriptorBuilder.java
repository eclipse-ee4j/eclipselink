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
 *     Mike Norman - 090423
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

//java eXtension imports
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

//EclipseLink imports
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherDefaultListener;
import org.eclipse.persistence.tools.dbws.NamingConventionTransformer;
import static org.eclipse.persistence.internal.helper.ClassConstants.Object_Class;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_INSTANCE_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_PREFIX;
import static org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType.XSI_NIL;
import static org.eclipse.persistence.tools.dbws.oracle.PLSQLOXDescriptorBuilder.attributeClassFromDatabaseType;
import static org.eclipse.persistence.tools.dbws.oracle.PLSQLOXDescriptorBuilder.qnameFromDatabaseType;

public class AdvancedJDBCOXDescriptorBuilder extends PublisherDefaultListener {

    public static final String ITEMS_MAPPING_ATTRIBUTE_NAME = "items";
    public static final String ITEM_MAPPING_NAME = "item";

    protected String targetNamespace;
    protected NamingConventionTransformer nct;
    protected Stack<ListenerHelper> stac = new Stack<ListenerHelper>();
    protected Map<String, XMLDescriptor> descriptorMap =
        new HashMap<String, XMLDescriptor>();
    protected String packageName = null;

    public AdvancedJDBCOXDescriptorBuilder(String targetNamespace, NamingConventionTransformer nct) {
        super();
        this.targetNamespace = targetNamespace;
        this.nct = nct;
    }
    
    public AdvancedJDBCOXDescriptorBuilder(String targetNamespace, NamingConventionTransformer nct,
        String packageName) {
        this(targetNamespace, nct);
        this.packageName = packageName;
    }

    public List<XMLDescriptor> getDescriptors() {
        if (descriptorMap.isEmpty()) {
            return null;
        }
        ArrayList<XMLDescriptor> al = new ArrayList<XMLDescriptor>();
        al.addAll(descriptorMap.values());
        return al;
    }

    @Override
    public void beginPackage(String packageName) {
        if (this.packageName == null) {
            this.packageName = trimDotPrefix(packageName);
        }
    }

    @Override
    public void handleMethodReturn(String returnTypeName) {
        // trim-off dotted-prefix
        String returnType = trimDotPrefix(returnTypeName);
        stac.push(new ReturnArgHelper("", returnType));
    }

    @Override
    public void handleSqlType(String sqlTypeName, int typecode, String targetTypeName) {
        if (!stac.isEmpty()) {
            ListenerHelper listenerHelper = stac.pop();
            if (listenerHelper.isAttribute()) {
                AttributeFieldHelper attributeFieldHelper = (AttributeFieldHelper)listenerHelper;
                attributeFieldHelper.setSqlTypeName(sqlTypeName);
                String fieldName = attributeFieldHelper.attributeFieldName();
                String attributeName = fieldName.toLowerCase();
                ListenerHelper listenerHelper2 = stac.peek();
                if (listenerHelper2.isObject()) {
                    ObjectTypeHelper objectTypeHelper = (ObjectTypeHelper)listenerHelper2;
                    XMLDescriptor xdesc = descriptorMap.get(objectTypeHelper.objectTypename());
                    DatabaseMapping dm = xdesc.getMappingForAttributeName(attributeName);
                    if (dm == null) {
                        XMLDirectMapping fieldMapping = new XMLDirectMapping();
                        fieldMapping.setAttributeName(attributeName);
                        XMLField xField = new XMLField(attributeName + "/text()");
                        xField.setRequired(true);
                        QName qnameFromDatabaseType = qnameFromDatabaseType(listenerHelper);
                        xField.setSchemaType(qnameFromDatabaseType);
                        // special case to avoid Calendar problems
                        if (qnameFromDatabaseType == DATE_QNAME) {
                            fieldMapping.setAttributeClassification(java.sql.Date.class);
                            xField.addXMLConversion(DATE_QNAME, java.sql.Date.class);
                            xField.addJavaConversion(java.sql.Date.class, DATE_QNAME);
                            xdesc.getNamespaceResolver().put(SCHEMA_PREFIX, W3C_XML_SCHEMA_NS_URI);
                        }
                        else {
                            Class<?> attributeClass = (Class<?>)XMLConversionManager.getDefaultXMLTypes().
                                get(qnameFromDatabaseType);
                            if (attributeClass == null) {
                                attributeClass =  Object_Class;
                            }
                            fieldMapping.setAttributeClassification(attributeClass);
                        }
                        fieldMapping.setField(xField);
                        AbstractNullPolicy nullPolicy = fieldMapping.getNullPolicy();
                        nullPolicy.setNullRepresentedByEmptyNode(false);
                        nullPolicy.setMarshalNullRepresentation(XSI_NIL);
                        nullPolicy.setNullRepresentedByXsiNil(true);
                        fieldMapping.setNullPolicy(nullPolicy);
                        xdesc.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX,
                            W3C_XML_SCHEMA_INSTANCE_NS_URI); // to support xsi:nil policy
                        xdesc.addMapping(fieldMapping);
                    }
                    // last attribute, pop ObjectTypeHelper off stack
                    int numAttributes = objectTypeHelper.decrNumAttributes();
                    if (numAttributes == 0) {
                        stac.pop();
                    }
                }
                else if (listenerHelper2.isArray()) {
                    SqlArrayTypeHelper sqlArrayTypeHelper = (SqlArrayTypeHelper)listenerHelper2;
                    XMLDescriptor xdesc = descriptorMap.get(sqlArrayTypeHelper.arrayTypename());
                    DatabaseMapping dm = xdesc.getMappingForAttributeName(attributeName);
                    if (dm == null) {
                        XMLCompositeDirectCollectionMapping dirCollectMapping = 
                            new XMLCompositeDirectCollectionMapping();
                        SqltypeHelper componentType = new SqltypeHelper(sqlTypeName);
                        dirCollectMapping.setAttributeElementClass(
                            attributeClassFromDatabaseType((DefaultListenerHelper)componentType));
                        dirCollectMapping.setAttributeName(attributeName);
                        dirCollectMapping.setUsesSingleNode(true);
                        dirCollectMapping.setXPath(attributeName + "/text()");
                        XMLField xField = (XMLField)dirCollectMapping.getField();
                        xField.setRequired(true);
                        xField.setSchemaType(qnameFromDatabaseType(componentType));
                        dirCollectMapping.useCollectionClassName("java.util.ArrayList");
                        AbstractNullPolicy nullPolicy = dirCollectMapping.getNullPolicy();
                        nullPolicy.setNullRepresentedByEmptyNode(false);
                        nullPolicy.setMarshalNullRepresentation(XSI_NIL);
                        nullPolicy.setNullRepresentedByXsiNil(true);
                        dirCollectMapping.setNullPolicy(nullPolicy);
                        xdesc.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX,
                            W3C_XML_SCHEMA_INSTANCE_NS_URI); // to support xsi:nil policy
                        xdesc.addMapping(dirCollectMapping);
                    }
                }
            }
            else if (listenerHelper.isArray()) {
                SqlArrayTypeHelper sqlArrayTypeHelper2 = (SqlArrayTypeHelper)listenerHelper;
                XMLDescriptor xdesc = descriptorMap.get(sqlArrayTypeHelper2.arrayTypename());
                DatabaseMapping dm = xdesc.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                if (dm == null) {
                    XMLCompositeDirectCollectionMapping itemsMapping = new XMLCompositeDirectCollectionMapping();
                    SqltypeHelper componentType = new SqltypeHelper(sqlTypeName);
                    itemsMapping.setAttributeElementClass(
                        attributeClassFromDatabaseType((DefaultListenerHelper)componentType));
                    itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                    itemsMapping.setUsesSingleNode(true);
                    itemsMapping.setXPath(ITEM_MAPPING_NAME + "/text()");
                    XMLField xField = (XMLField)itemsMapping.getField();
                    xField.setRequired(true);
                    xField.setSchemaType(qnameFromDatabaseType(componentType));
                    itemsMapping.useCollectionClassName("java.util.ArrayList");
                    AbstractNullPolicy nullPolicy = itemsMapping.getNullPolicy();
                    nullPolicy.setNullRepresentedByEmptyNode(false);
                    nullPolicy.setMarshalNullRepresentation(XSI_NIL);
                    nullPolicy.setNullRepresentedByXsiNil(true);
                    itemsMapping.setNullPolicy(nullPolicy);
                    xdesc.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX,
                        W3C_XML_SCHEMA_INSTANCE_NS_URI); // to support xsi:nil policy
                    xdesc.addMapping(itemsMapping);
                }
                ListenerHelper listenerHelper2 = stac.peek();
                if (listenerHelper2.isAttribute()) {
                    // type built above used in field definition of object further up stack
                    stac.pop();
                    AttributeFieldHelper fieldHelper = (AttributeFieldHelper)listenerHelper2;
                    ListenerHelper listenerHelper3 = stac.peek();
                    if (listenerHelper3.isObject()) {
                        ObjectTypeHelper objectTypeHelper = (ObjectTypeHelper)listenerHelper3;
                        XMLDescriptor xdesc2 = descriptorMap.get(objectTypeHelper.objectTypename());
                        String fieldName = fieldHelper.attributeFieldName();
                        DatabaseMapping dm2 = xdesc2.getMappingForAttributeName(fieldName.toLowerCase());
                        if (dm2 == null) {
                            XMLCompositeDirectCollectionMapping fieldMapping = new XMLCompositeDirectCollectionMapping();
                            SqltypeHelper componentType = new SqltypeHelper(sqlTypeName);
                            fieldMapping.setAttributeElementClass(
                                attributeClassFromDatabaseType((DefaultListenerHelper)componentType));
                            fieldMapping.setAttributeName(fieldName.toLowerCase());
                            XMLField field = new XMLField(fieldName.toLowerCase() + "/" + 
                                ITEM_MAPPING_NAME + "/text()");
                            field.setRequired(true);
                            fieldMapping.setField(field);
                            field.setSchemaType(qnameFromDatabaseType(componentType));
                            fieldMapping.useCollectionClassName("java.util.ArrayList");
                            AbstractNullPolicy nullPolicy = fieldMapping.getNullPolicy();
                            nullPolicy.setNullRepresentedByEmptyNode(false);
                            nullPolicy.setMarshalNullRepresentation(XSI_NIL);
                            nullPolicy.setNullRepresentedByXsiNil(true);
                            fieldMapping.setNullPolicy(nullPolicy);
                            xdesc2.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX,
                                W3C_XML_SCHEMA_INSTANCE_NS_URI); // to support xsi:nil policy
                            xdesc2.addMapping(fieldMapping);
                        }
                        // last attribute, pop ObjectTypeHelper off stack
                        int numAttributes = objectTypeHelper.decrNumAttributes();
                        if (numAttributes == 0) {
                            stac.pop();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handleObjectType(String objectTypeName, String targetTypeName, int numAttributes) {
        // JDBC Advanced type?
        if (numAttributes > 0) {
            // trim-off dotted-prefix, toLowerCase
            String objectTypeNameAlias = trimDotPrefix(objectTypeName).toLowerCase();
            XMLDescriptor xdesc = descriptorMap.get(objectTypeNameAlias);
            String userType = nct.generateSchemaAlias(objectTypeNameAlias);
            if (xdesc == null) {
                xdesc = new XMLDescriptor();
                xdesc.setAlias(objectTypeNameAlias);
                xdesc.setJavaClassName(packageName.toLowerCase() + "." + objectTypeNameAlias);
                xdesc.getQueryManager();
                XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
                schemaReference.setSchemaContext("/" + userType);
                schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
                xdesc.setSchemaReference(schemaReference);
                NamespaceResolver nr = new NamespaceResolver();
                nr.setDefaultNamespaceURI(targetNamespace);
                xdesc.setNamespaceResolver(nr);
                descriptorMap.put(objectTypeNameAlias, xdesc);
            }
            // before we push the new ObjectTypeHelper, check stac to see if we are part
            // of nested chain of object types
            if (!stac.isEmpty()) {
                ListenerHelper listenerHelper = stac.peek();
                if (listenerHelper.isAttribute()) {
                    AttributeFieldHelper fieldHelper = (AttributeFieldHelper)stac.pop();
                    fieldHelper.setSqlTypeName(objectTypeNameAlias);
                    String fieldName = fieldHelper.attributeFieldName();
                    String attributeName = fieldName.toLowerCase();
                    ListenerHelper listenerHelper2 = stac.peek();
                    if (listenerHelper2.isObject()) {
                        ObjectTypeHelper objectTypeHelper2 = (ObjectTypeHelper)listenerHelper2;
                        String objectTypeNameAlias2 = objectTypeHelper2.objectTypename();
                        XMLDescriptor xdesc2 = 
                            descriptorMap.get(objectTypeNameAlias2);
                        if (xdesc2 != null) {
                            DatabaseMapping dm = xdesc2.getMappingForAttributeName(attributeName);
                            if (dm == null) {
                                XMLCompositeObjectMapping compMapping =
                                    new XMLCompositeObjectMapping();
                                compMapping.setAttributeName(attributeName);
                                compMapping.setReferenceClassName(xdesc.getJavaClassName());
                                compMapping.setXPath(attributeName);
                                XMLField xField = (XMLField)compMapping.getField();
                                xField.setRequired(true);
                                xdesc2.addMapping(compMapping);
                            }
                            // last attribute, pop ObjectTypeHelper off stack
                            int numAttributes2 = objectTypeHelper2.decrNumAttributes();
                            if (numAttributes2 == 0) {
                                stac.pop();
                            }
                        }
                    }
                }
                else if (listenerHelper.isArray()) {
                    SqlArrayTypeHelper sqlArrayTypeHelper = (SqlArrayTypeHelper)stac.pop();
                    String sqlArrayTypeAlias = sqlArrayTypeHelper.arrayTypename();
                    XMLDescriptor xdesc2 = descriptorMap.get(sqlArrayTypeAlias);
                    if (xdesc2 != null) {
                        boolean itemsMappingFound = 
                            xdesc2.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
                        if (!itemsMappingFound) {
                            XMLCompositeCollectionMapping itemsMapping = new XMLCompositeCollectionMapping();
                            itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                            itemsMapping.setXPath(ITEM_MAPPING_NAME);
                            itemsMapping.useCollectionClassName("java.util.ArrayList");
                            itemsMapping.setReferenceClassName(xdesc.getJavaClassName());
                            xdesc2.addMapping(itemsMapping);
                        }
                    }
                }
                else if (listenerHelper.isReturnArg()) {
                    //ReturnArgHelper returnArgHelper = (ReturnArgHelper)stac.pop();
                    stac.pop();
                    xdesc.setDefaultRootElement(userType);
                }
            }
            stac.push(new ObjectTypeHelper(objectTypeNameAlias, targetTypeName, numAttributes));
        }
    }

    @Override
    public void handleSqlArrayType(String arrayTypename, String targetTypeName) {
        // trim-off dotted-prefix, toLowerCase
        String arrayTypenameAlias = trimDotPrefix(arrayTypename).toLowerCase();
        String userType = nct.generateSchemaAlias(arrayTypenameAlias);
        XMLDescriptor xdesc = descriptorMap.get(arrayTypenameAlias);
        if (xdesc == null) {
            xdesc = new XMLDescriptor();
            xdesc.setAlias(arrayTypenameAlias);
            xdesc.setJavaClassName(packageName.toLowerCase() + "." + 
                arrayTypenameAlias + COLLECTION_WRAPPER_SUFFIX);
            xdesc.getQueryManager();
            XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
            schemaReference.setSchemaContext("/" + userType);
            schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
            xdesc.setSchemaReference(schemaReference);
            NamespaceResolver nr = new NamespaceResolver();
            nr.setDefaultNamespaceURI(targetNamespace);
            xdesc.setNamespaceResolver(nr);
            descriptorMap.put(arrayTypenameAlias, xdesc);
        }
        // before we push the new SqlArrayTypeHelper, check stac to see if we are part
        // of nested chain of object types
        if (!stac.isEmpty()) {
            ListenerHelper listenerHelper = stac.peek();
            if (listenerHelper.isReturnArg()) {
                xdesc.setDefaultRootElement(userType);
            }
        }
        stac.push(new SqlArrayTypeHelper(arrayTypenameAlias, targetTypeName));
    }

    @Override
    public void handleSqlTableType(String tableTypeName, String targetTypeName) {
        // trim-off dotted-prefix, toLowerCase
        String tableTypeNameAlias = trimDotPrefix(tableTypeName).toLowerCase();
        String userType = nct.generateSchemaAlias(tableTypeNameAlias);
        XMLDescriptor xdesc = descriptorMap.get(tableTypeNameAlias);
        if (xdesc == null) {
            xdesc = new XMLDescriptor();
            xdesc.setAlias(tableTypeNameAlias);
            xdesc.setJavaClassName(packageName.toLowerCase() + "." + 
                tableTypeNameAlias + COLLECTION_WRAPPER_SUFFIX);
            xdesc.getQueryManager();
            XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
            schemaReference.setSchemaContext("/" + userType);
            schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
            xdesc.setSchemaReference(schemaReference);
            NamespaceResolver nr = new NamespaceResolver();
            nr.setDefaultNamespaceURI(targetNamespace);
            xdesc.setNamespaceResolver(nr);
            descriptorMap.put(tableTypeNameAlias, xdesc);
        }
        // before we push the new SqlArrayTypeHelper, check stac to see if we are part
        // of nested chain of object types
        if (!stac.isEmpty()) {
            ListenerHelper listenerHelper = stac.peek();
            if (listenerHelper.isReturnArg()) {
                xdesc.setDefaultRootElement(userType);
            }
        }
        stac.push(new SqlArrayTypeHelper(tableTypeNameAlias, targetTypeName));
    }

    @Override
    public void handleAttributeField(String attributeFieldName) {
        stac.push(new AttributeFieldHelper(attributeFieldName, null));
    }
}