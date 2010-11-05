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
 *     Mike Norman - from Proof-of-concept, become production code
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

import static org.eclipse.persistence.internal.helper.ClassConstants.BIGDECIMAL;
import static org.eclipse.persistence.internal.helper.ClassConstants.BOOLEAN;
import static org.eclipse.persistence.internal.helper.ClassConstants.INTEGER;
import static org.eclipse.persistence.internal.helper.ClassConstants.JavaSqlDate_Class;
import static org.eclipse.persistence.internal.helper.ClassConstants.Object_Class;
import static org.eclipse.persistence.internal.helper.ClassConstants.STRING;
import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;
import static org.eclipse.persistence.oxm.XMLConstants.ANY_SIMPLE_TYPE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.BOOLEAN_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DECIMAL_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.INTEGER_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_INSTANCE_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.STRING_QNAME;
import static org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType.XSI_NIL;

public class PLSQLOXDescriptorBuilder extends PublisherDefaultListener {

    public static final String ITEMS_MAPPING_ATTRIBUTE_NAME = "items";

    protected String targetNamespace;
    protected Stack<ListenerHelper> stac = new Stack<ListenerHelper>();
    protected Map<String, XMLDescriptor> descriptorMap = new HashMap<String, XMLDescriptor>();
    protected String packageName = null;

    public PLSQLOXDescriptorBuilder(String targetNamespace) {
        this.targetNamespace = targetNamespace;
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
        // trim-off package name
        int dotIdx = packageName.indexOf('.');
        if (dotIdx > -1) {
            this.packageName = packageName.substring(dotIdx+1);
        }
    }
    @Override
    public void beginPlsqlTable(String tableName, String targetTypeName) {
        TableHelper tableHelper = new TableHelper(tableName, targetTypeName.toLowerCase(), targetTypeName);
        stac.push(tableHelper);
    }
    @Override
    public void endPlsqlTable(String tableName, String typeDDL, String typeDropDDL) {
        ListenerHelper top = stac.pop();
        ListenerHelper next  = stac.peek();
        TableHelper tableHelper = null;
        if (next.isTable()) {
            tableHelper = (TableHelper)next;
            next = top; // switch around stack order
        }
        else if (top.isTable()) {
            tableHelper = (TableHelper)top;
        }
        String tableAlias = tableHelper.targetTypeName().toLowerCase();
        XMLDescriptor xdesc = descriptorMap.get(tableAlias);
        if (xdesc == null) {
            xdesc = new XMLDescriptor();
            xdesc.setAlias(tableHelper.tableAlias());
            xdesc.setJavaClassName(tableName.toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
            xdesc.getQueryManager();
            XMLSchemaURLReference schemaReference = new XMLSchemaURLReference("");
            schemaReference.setSchemaContext("/" + tableHelper.targetTypeName());
            schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
            xdesc.setSchemaReference(schemaReference);
            NamespaceResolver nr = new NamespaceResolver();
            nr.setDefaultNamespaceURI(targetNamespace);
            xdesc.setNamespaceResolver(nr);
        }
        boolean itemsMappingFound =
            xdesc.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (next.isRecord()) {
            if (!itemsMappingFound) {
                XMLCompositeCollectionMapping itemsMapping = new XMLCompositeCollectionMapping();
                itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                itemsMapping.setXPath("item");
                XMLField xField = (XMLField)itemsMapping.getField();
                xField.setRequired(true);
                itemsMapping.useCollectionClassName("java.util.ArrayList");
                itemsMapping.setReferenceClassName(((RecordHelper)next).recordName().toLowerCase());
                xdesc.addMapping(itemsMapping);
            }
            tableHelper.nestedIsComplex();
        }
        else  {
            if (!itemsMappingFound) {
                if (!next.isComplex()) {
                    XMLCompositeDirectCollectionMapping itemsMapping = new XMLCompositeDirectCollectionMapping();
                    itemsMapping.setAttributeElementClass(
                        attributeClassFromDatabaseType((DefaultListenerHelper)next));
                    itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                    itemsMapping.setUsesSingleNode(true);
                    itemsMapping.setXPath("item/text()");
                    XMLField xField = (XMLField)itemsMapping.getField();
                    xField.setRequired(true);
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
                else {
                    XMLCompositeCollectionMapping itemsMapping = new XMLCompositeCollectionMapping();
                    itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                    itemsMapping.setXPath("item");
                    XMLField xField = (XMLField)itemsMapping.getField();
                    xField.setRequired(true);
                    itemsMapping.useCollectionClassName("java.util.ArrayList");
                    itemsMapping.setReferenceClassName(((TableHelper)next).tableName().toLowerCase() +
                        COLLECTION_WRAPPER_SUFFIX);
                    xdesc.addMapping(itemsMapping);
                }
            }
            if (next.isTable()) {
                ((TableHelper)next).nestedIsComplex();
            }
        }
        if (!itemsMappingFound) {
            descriptorMap.put(tableAlias, xdesc);
        }
    }
    @Override
    public void beginPlsqlRecord(String plsqlRecordName, String targetTypeName, int numFields) {
        RecordHelper recordHelper = new RecordHelper(plsqlRecordName, targetTypeName, numFields);
        stac.push(recordHelper);
        String recordAlias = targetTypeName.toLowerCase();
        XMLDescriptor xdesc = descriptorMap.get(recordAlias);
        if (xdesc == null) {
            xdesc = new XMLDescriptor();
            xdesc.setAlias(recordAlias);
            xdesc.setJavaClassName(plsqlRecordName.toLowerCase());
            xdesc.getQueryManager();
            XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
            schemaReference.setSchemaContext("/" + targetTypeName);
            schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
            xdesc.setSchemaReference(schemaReference);
            NamespaceResolver nr = new NamespaceResolver();
            nr.setDefaultNamespaceURI(targetNamespace);
            xdesc.setNamespaceResolver(nr);
            descriptorMap.put(recordAlias, xdesc);
        }
    }

    @Override
    public void endPlsqlRecordField(String fieldName, int idx) {
        ListenerHelper top = stac.pop();
        ListenerHelper listenerHelper = stac.peek();
        if (listenerHelper.isRecord()) {
            RecordHelper recordHelper = (RecordHelper)stac.peek();
            XMLDescriptor xdesc = descriptorMap.get(recordHelper.targetTypeName().toLowerCase());
            String lfieldName = fieldName.toLowerCase();
            if (xdesc.getMappingForAttributeName(lfieldName) == null) {
                if (top.isComplex()) {
                    if (top.isTable()) {
                        TableHelper tblHelper = (TableHelper)top;
                        if (tblHelper.isNestedComplex()) {
                            XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
                            fieldMapping.setAttributeName(lfieldName);
                            fieldMapping.setXPath(lfieldName);
                            XMLField xField = (XMLField)fieldMapping.getField();
                            xField.setRequired(true);
                            fieldMapping.setReferenceClassName(tblHelper.tableName().toLowerCase());
                            xdesc.addMapping(fieldMapping);
                        }
                        else {
                            XMLCompositeDirectCollectionMapping fieldMapping =
                                new XMLCompositeDirectCollectionMapping();
                            String foo = tblHelper.targetTypeName().toLowerCase();
                            XMLDescriptor xdesc2 = descriptorMap.get(foo);
                            if (xdesc2 != null) {
                                XMLCompositeDirectCollectionMapping itemsMapping =
                                    (XMLCompositeDirectCollectionMapping)xdesc2.getMappingForAttributeName(
                                    ITEMS_MAPPING_ATTRIBUTE_NAME);
                                Class<?> attributeElementClass = itemsMapping.getAttributeElementClass();
                                fieldMapping.setAttributeElementClass(attributeElementClass);
                            }
                            else {
                                fieldMapping.setAttributeElementClass(String.class); //TODO
                            }
                            fieldMapping.setAttributeName(lfieldName);
                            fieldMapping.setUsesSingleNode(true);
                            fieldMapping.setXPath(lfieldName + "/item/text()");
                            XMLField xField = (XMLField)fieldMapping.getField();
                            xField.setRequired(true);
                            fieldMapping.useCollectionClassName("java.util.ArrayList");
                            AbstractNullPolicy nullPolicy = fieldMapping.getNullPolicy();
                            nullPolicy.setNullRepresentedByEmptyNode(false);
                            nullPolicy.setMarshalNullRepresentation(XSI_NIL);
                            nullPolicy.setNullRepresentedByXsiNil(true);
                            fieldMapping.setNullPolicy(nullPolicy);
                            xdesc.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX,
                                W3C_XML_SCHEMA_INSTANCE_NS_URI); // to support xsi:nil policy
                            xdesc.addMapping(fieldMapping);
                        }
                    }
                    else if (top.isRecord()) {
                        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
                        fieldMapping.setAttributeName(lfieldName);
                        fieldMapping.setXPath(lfieldName);
                        XMLField xField = (XMLField)fieldMapping.getField();
                        xField.setRequired(true);
                        fieldMapping.setReferenceClassName(((RecordHelper)top).recordName().toLowerCase());
                        xdesc.addMapping(fieldMapping);
                    }
                }
                else {
                    XMLDirectMapping fieldMapping = new XMLDirectMapping();
                    fieldMapping.setAttributeName(lfieldName);
                    XMLField xField = new XMLField(lfieldName + "/text()");
                    xField.setRequired(true);
                    QName qnameFromDatabaseType = qnameFromDatabaseType(top);
                    xField.setSchemaType(qnameFromDatabaseType);
                    // special case to avoid Calendar problems
                    if (qnameFromDatabaseType == DATE_QNAME) {
                        fieldMapping.setAttributeClassification(java.sql.Date.class);
                        xField.addXMLConversion(DATE_QNAME, java.sql.Date.class);
                        xField.addJavaConversion(java.sql.Date.class, DATE_QNAME);
                        xdesc.getNamespaceResolver().put(SCHEMA_INSTANCE_PREFIX,
                            W3C_XML_SCHEMA_INSTANCE_NS_URI);
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
            }
        }
        else {
            System.identityHashCode(listenerHelper);
        }
    }
    @Override
    public void endMethodArg(String argName) {
        stac.pop();
    }
    @Override
    public void handleSqlType(String sqlTypeName, int typecode, String targetType) {
        stac.push(new SqltypeHelper(sqlTypeName));
    }
    @Override
    public void handleObjectType(String objectTypename, String targetTypeName, int numAttributes) {
        stac.push(new ObjectTypeHelper(objectTypename, targetTypeName, numAttributes));
    }

    public static Class<?> attributeClassFromDatabaseType(DefaultListenerHelper helper) {
        if (!helper.isComplex()) {
            String typeName = helper.targetTypeName();
            if (typeName == null) {
                if (helper instanceof SqltypeHelper) {
                    typeName = ((SqltypeHelper)helper).sqlTypeName();
                }
                else if (helper instanceof ObjectTypeHelper) {
                    typeName = ((ObjectTypeHelper)helper).objectTypename();
                }
                else if (helper instanceof SqlArrayTypeHelper) {
                    typeName = ((SqlArrayTypeHelper)helper).arrayTypename();
                }
            }
            if ("NUMBER".equals(typeName)) {
                return BIGDECIMAL;
            }
            if ("INTEGER".equals(typeName)) {
                return INTEGER;
            }
            else if ("BOOLEAN".equals(typeName)) {
                return BOOLEAN;
            }
            else if ("DATE".equals(typeName)) {
                return JavaSqlDate_Class;
            }
            // TODO - more conversions
        }
        // default is String
        return STRING;
    }

    public static <T extends ListenerHelper> QName qnameFromDatabaseType(T helper) {
        if (!helper.isComplex()) {
            String typeName = helper.targetTypeName();
            if (typeName == null) {
                if (helper instanceof SqltypeHelper) {
                    typeName = ((SqltypeHelper)helper).sqlTypeName();
                }
                if (typeName == null) {
                    if (helper instanceof ObjectTypeHelper) {
                        typeName = ((ObjectTypeHelper)helper).objectTypename();
                    }
                }
            }
            if ("VARCHAR2".equals(typeName) || "VARCHAR".equals(typeName)) {
                return STRING_QNAME;
            }
            else if ("NUMBER".equals(typeName)) {
                return DECIMAL_QNAME;
            }
            else if ("INTEGER".equals(typeName)) {
                return INTEGER_QNAME;
            }
            else if ("BOOLEAN".equals(typeName)) {
                return BOOLEAN_QNAME;
            }
            else if ("DATE".equals(typeName)) {
                return DATE_QNAME;
            }
            // TODO - more conversions
        }
        return ANY_SIMPLE_TYPE_QNAME;
    }
}