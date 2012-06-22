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
 *     Mike Norman - 090423
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

//EclipseLink imports
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.structures.ArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.mappings.structures.StructureMapping;
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherDefaultListener;

import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;

public class AdvancedJDBCORDescriptorBuilder extends PublisherDefaultListener {

    public static final String ITEMS_ATTRIBUTE_NAME = "items";

    protected Stack<ListenerHelper> stac = new Stack<ListenerHelper>();
    protected Map<String, ObjectRelationalDataTypeDescriptor> descriptorMap =
        new HashMap<String, ObjectRelationalDataTypeDescriptor>();
    protected String packageName = null;

    public AdvancedJDBCORDescriptorBuilder() {
        super();
    }
    public AdvancedJDBCORDescriptorBuilder(String packageName) {
        this();
        this.packageName = packageName;
    }

    public List<ObjectRelationalDataTypeDescriptor> getDescriptors() {
        if (descriptorMap.isEmpty()) {
            return null;
        }
        ArrayList<ObjectRelationalDataTypeDescriptor> al =
            new ArrayList<ObjectRelationalDataTypeDescriptor>();
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
                    ObjectRelationalDataTypeDescriptor ordt =
                        descriptorMap.get(objectTypeHelper.objectTypename());
                    DatabaseMapping dm = ordt.getMappingForAttributeName(attributeName);
                    if (dm == null) {
                        ordt.addFieldOrdering(fieldName);
                        DirectToFieldMapping dfm = new DirectToFieldMapping();
                        dfm.setFieldName(fieldName);
                        dfm.setAttributeName(attributeName);
                        ordt.addMapping(dfm);
                    }
                    // last attribute, pop ObjectTypeHelper off stack
                    int numAttributes = objectTypeHelper.decrNumAttributes();
                    if (numAttributes == 0) {
                        stac.pop();
                    }
                }
                else if (listenerHelper2.isArray()) {
                    SqlArrayTypeHelper sqlArrayTypeHelper = (SqlArrayTypeHelper)listenerHelper2;
                    ObjectRelationalDataTypeDescriptor ordt =
                        descriptorMap.get(sqlArrayTypeHelper.arrayTypename());
                    DatabaseMapping dm = ordt.getMappingForAttributeName(attributeName);
                    if (dm == null) {
                        ordt.addFieldOrdering(fieldName);
                        ArrayMapping arrayMapping = new ArrayMapping();
                        arrayMapping.setFieldName(fieldName);
                        arrayMapping.setAttributeName(attributeName);
                        arrayMapping.useCollectionClass(ArrayList.class);
                        arrayMapping.setStructureName(sqlArrayTypeHelper.arrayTypename().toUpperCase());
                        DatabaseField nestedField = new DatabaseField("");
                        nestedField.setSqlType(typecode);
                        nestedField.setColumnDefinition(sqlTypeName);
                        ObjectRelationalDatabaseField field =
                            (ObjectRelationalDatabaseField)arrayMapping.getField();
                        field.setNestedTypeField(nestedField);
                        ordt.addMapping(arrayMapping);
                    }
                }
            }
            else if (listenerHelper.isArray()) {
                SqlArrayTypeHelper sqlArrayTypeHelper = (SqlArrayTypeHelper)listenerHelper;
                ObjectRelationalDataTypeDescriptor ordt =
                    descriptorMap.get(sqlArrayTypeHelper.arrayTypename());
                DatabaseMapping dm = ordt.getMappingForAttributeName(ITEMS_ATTRIBUTE_NAME);
                if (dm == null) {
                    ArrayMapping arrayMapping = new ArrayMapping();
                    arrayMapping.setFieldName(ITEMS_ATTRIBUTE_NAME);
                    arrayMapping.setAttributeName(ITEMS_ATTRIBUTE_NAME);
                    arrayMapping.useCollectionClass(ArrayList.class);
                    arrayMapping.setStructureName(sqlArrayTypeHelper.arrayTypename().toUpperCase());
                    DatabaseField nestedField = new DatabaseField("");
                    nestedField.setSqlType(typecode);
                    nestedField.setColumnDefinition(sqlTypeName);
                    ObjectRelationalDatabaseField field =
                        (ObjectRelationalDatabaseField)arrayMapping.getField();
                    field.setNestedTypeField(nestedField);
                    ordt.addMapping(arrayMapping);
                }
                ListenerHelper listenerHelper3 = stac.peek();
                ListenerHelper listenerHelper4 = null;
                if (listenerHelper3.isArray()) {
                    /*
                     * table of table scenario:
                    CREATE TYPE SOMEPACKAGE_TBL2 AS TABLE OF NUMBER;
                    CREATE TYPE SOMEPACKAGE_TBL4 AS TABLE OF SOMEPACKAGE_TBL2;
                     */
                    SqlArrayTypeHelper sqlArrayTypeHelper2 = (SqlArrayTypeHelper)listenerHelper3;
                    stac.pop();
                    ObjectRelationalDataTypeDescriptor ordt3 =
                        descriptorMap.get(sqlArrayTypeHelper2.arrayTypename());
                    DatabaseMapping dm3 = ordt3.getMappingForAttributeName(ITEMS_ATTRIBUTE_NAME);
                    if (dm3 == null) {
                        ObjectArrayMapping objArrayMapping = new ObjectArrayMapping();
                        objArrayMapping.setAttributeName(ITEMS_ATTRIBUTE_NAME);
                        objArrayMapping.setFieldName(ITEMS_ATTRIBUTE_NAME);
                        objArrayMapping.setStructureName(sqlArrayTypeHelper2.arrayTypename().toUpperCase());
                        objArrayMapping.setReferenceClassName(ordt.getJavaClassName());
                        objArrayMapping.useCollectionClass(ArrayList.class);
                        ordt3.addMapping(objArrayMapping);
                    }
                    // more stack peeking
                    if (stac.peek().isAttribute()) {
                        listenerHelper4 = stac.peek();
                    }
                }
                else {
                    listenerHelper4 = listenerHelper3;
                }
                if (listenerHelper4.isAttribute()) {
                    // type built above used in field definition of object further up stack
                    AttributeFieldHelper fieldHelper = (AttributeFieldHelper)listenerHelper4;
                    stac.pop();
                    ListenerHelper listenerHelper5 = stac.peek();
                    if (listenerHelper5.isObject()) {
                        ObjectTypeHelper objectTypeHelper = (ObjectTypeHelper)listenerHelper5;
                        ObjectRelationalDataTypeDescriptor ordt2 =
                            descriptorMap.get(objectTypeHelper.objectTypename());
                        String fieldName = fieldHelper.attributeFieldName();
                        DatabaseMapping dm2 =
                            ordt2.getMappingForAttributeName(fieldName.toLowerCase());
                        if (dm2 == null) {
                            ordt2.addFieldOrdering(fieldName);
                            // simple or complex array?
                            if (listenerHelper4 == listenerHelper3) {
                                ArrayMapping arrayMapping2 = new ArrayMapping();
                                arrayMapping2.setAttributeName(fieldName.toLowerCase());
                                arrayMapping2.setFieldName(fieldName);
                                arrayMapping2.setStructureName(sqlArrayTypeHelper.arrayTypename().toUpperCase());
                                arrayMapping2.useCollectionClass(ArrayList.class);
                                DatabaseField nestedField = new DatabaseField("");
                                nestedField.setSqlType(typecode);
                                nestedField.setColumnDefinition(sqlTypeName);
                                ObjectRelationalDatabaseField field =
                                    (ObjectRelationalDatabaseField)arrayMapping2.getField();
                                field.setNestedTypeField(nestedField);
                                ordt2.addMapping(arrayMapping2);
                            }
                            else {
                                ObjectArrayMapping objArrayMapping2 = new ObjectArrayMapping();
                                objArrayMapping2.setAttributeName(fieldName.toLowerCase());
                                objArrayMapping2.setFieldName(fieldName);
                                String structureName = "";
                                String referenceClassName = "";
                                if (listenerHelper3.isObject()) {
                                    ObjectTypeHelper objectTypeHelper2 =
                                        (ObjectTypeHelper)listenerHelper3;
                                    structureName =
                                        objectTypeHelper2.objectTypename().toUpperCase();
                                    ObjectRelationalDataTypeDescriptor ordt3 =
                                        descriptorMap.get(objectTypeHelper2.objectTypename());
                                    referenceClassName = ordt3.getJavaClassName();
                                }
                                else if (listenerHelper3.isArray()) {
                                    SqlArrayTypeHelper sqlArrayTypeHelper3 =
                                        (SqlArrayTypeHelper)listenerHelper3;
                                    structureName =
                                        sqlArrayTypeHelper3.arrayTypename().toUpperCase();
                                    ObjectRelationalDataTypeDescriptor ordt3 =
                                        descriptorMap.get(sqlArrayTypeHelper3.arrayTypename());
                                    referenceClassName = ordt3.getJavaClassName();
                                }
                                else {
                                    // ??
                                }
                                objArrayMapping2.setStructureName(structureName);
                                objArrayMapping2.setReferenceClassName(referenceClassName);
                                objArrayMapping2.useCollectionClass(ArrayList.class);
                                ordt2.addMapping(objArrayMapping2);
                            }
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
            ObjectRelationalDataTypeDescriptor ordt = descriptorMap.get(objectTypeNameAlias);
            if (ordt == null) {
                ordt = new ObjectRelationalDataTypeDescriptor();
                ordt.setStructureName(objectTypeNameAlias.toUpperCase());
                ordt.descriptorIsAggregate();
                ordt.setAlias(objectTypeNameAlias);
                ordt.setJavaClassName(packageName.toLowerCase() + "." + objectTypeNameAlias);
                ordt.getQueryManager();
                descriptorMap.put(objectTypeNameAlias, ordt);
            }
            // before we push the new ObjectTypeHelper, check stac to see if we are part
            // of nested chain of object types
            if (!stac.isEmpty()) {
                ListenerHelper listenerHelper = stac.peek();
                if (listenerHelper.isAttribute()) {
                    AttributeFieldHelper fieldHelper = (AttributeFieldHelper)stac.pop();
                    String fieldName = fieldHelper.attributeFieldName();
                    String attributeName = fieldName.toLowerCase();
                    ListenerHelper listenerHelper2 = stac.peek();
                    if (listenerHelper2.isObject()) {
                        ObjectTypeHelper objectTypeHelper2 = (ObjectTypeHelper)listenerHelper2;
                        String objectTypeNameAlias2 = objectTypeHelper2.objectTypename();
                        ObjectRelationalDataTypeDescriptor ordt2 =
                            descriptorMap.get(objectTypeNameAlias2);
                        if (ordt2 != null) {
                            DatabaseMapping dm = ordt2.getMappingForAttributeName(attributeName);
                            if (dm == null) {
                                ordt2.addFieldOrdering(fieldName);
                                StructureMapping structMapping = new StructureMapping();
                                structMapping.setFieldName(fieldName);
                                structMapping.setAttributeName(attributeName);
                                structMapping.setReferenceClassName(ordt.getJavaClassName());
                                ordt2.addMapping(structMapping);
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
                    String sqlArrayTypeNameAlias = sqlArrayTypeHelper.arrayTypename();
                    ObjectRelationalDataTypeDescriptor ordt2 =
                        descriptorMap.get(sqlArrayTypeNameAlias);
                    if (ordt2 != null) {
                        DatabaseMapping dm =
                            ordt2.getMappingForAttributeName(ITEMS_ATTRIBUTE_NAME);
                        if (dm == null) {
                            ObjectArrayMapping arrayMapping = new ObjectArrayMapping();
                            arrayMapping.setAttributeName(ITEMS_ATTRIBUTE_NAME);
                            arrayMapping.setFieldName(ITEMS_ATTRIBUTE_NAME);
                            arrayMapping.setStructureName(sqlArrayTypeNameAlias.toUpperCase());
                            arrayMapping.setReferenceClassName(ordt.getJavaClassName());
                            arrayMapping.useCollectionClass(ArrayList.class);
                            ordt2.addMapping(arrayMapping);
                        }
                    }
                }
            }
            stac.push(new ObjectTypeHelper(objectTypeNameAlias, targetTypeName, numAttributes));
        }
    }

    @Override
    public void handleSqlArrayType(String arrayTypename, String targetTypeName) {
        // trim-off dotted-prefix, toLowerCase
        String arrayTypenameAlias = trimDotPrefix(arrayTypename).toLowerCase();
        ObjectRelationalDataTypeDescriptor ordt = descriptorMap.get(arrayTypenameAlias);
        if (ordt == null) {
            ordt = new ObjectRelationalDataTypeDescriptor();
            ordt.descriptorIsAggregate();
            ordt.setAlias(arrayTypenameAlias);
            ordt.setJavaClassName(packageName.toLowerCase() + "." + arrayTypenameAlias
                + COLLECTION_WRAPPER_SUFFIX);
            ordt.getQueryManager();
            descriptorMap.put(arrayTypenameAlias, ordt);
        }
        // before we push the new ObjectTypeHelper, check stac to see if we are part
        // of nested chain of object types
        if (!stac.isEmpty()) {
            // TBD ListenerHelper listenerHelper = stac.peek();
        }
        stac.push(new SqlArrayTypeHelper(arrayTypenameAlias, targetTypeName));
    }

    @Override
    public void handleSqlTableType(String tableTypeName, String targetTypeName) {
        // trim-off dotted-prefix, toLowerCase
        String sqlTableTypeAlias = trimDotPrefix(tableTypeName).toLowerCase();
        ObjectRelationalDataTypeDescriptor ordt = descriptorMap.get(sqlTableTypeAlias);
        if (ordt == null) {
            ordt = new ObjectRelationalDataTypeDescriptor();
            ordt.descriptorIsAggregate();
            ordt.setAlias(sqlTableTypeAlias);
            ordt.setJavaClassName(packageName.toLowerCase() + "." + sqlTableTypeAlias
                + COLLECTION_WRAPPER_SUFFIX);
            ordt.getQueryManager();
            descriptorMap.put(sqlTableTypeAlias, ordt);
        }
        stac.push(new SqlArrayTypeHelper(sqlTableTypeAlias, targetTypeName));
    }

    @Override
    public void handleAttributeField(String attributeFieldName, int idx) {
        stac.push(new AttributeFieldHelper(attributeFieldName, null));
    }
}