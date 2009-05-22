/*******************************************************************************
 * Copyright (c) 1998-2009 Oracle. All rights reserved.
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
import static org.eclipse.persistence.internal.dynamicpersist.BaseEntityClassLoader.COLLECTION_WRAPPER_SUFFIX;

public class AdvancedJDBCORDescriptorBuilder extends PublisherDefaultListener {

    public static final String ITEMS_MAPPING_ATTRIBUTE_NAME = "items";
    public static final String ITEMS_MAPPING_FIELD_NAME = "ITEMS";
    
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
                        arrayMapping.setStructureName(sqlArrayTypeHelper.arrayTypename());
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
                DatabaseMapping dm = ordt.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                if (dm == null) {
                    ArrayMapping arrayMapping = new ArrayMapping();
                    arrayMapping.setFieldName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                    arrayMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                    arrayMapping.useCollectionClass(ArrayList.class);
                    arrayMapping.setStructureName(sqlArrayTypeHelper.arrayTypename());
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
                            ordt2.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                        if (dm == null) {
                            ObjectArrayMapping arrayMapping = new ObjectArrayMapping();
                            arrayMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                            arrayMapping.setFieldName(ITEMS_MAPPING_FIELD_NAME);
                            arrayMapping.setStructureName(sqlArrayTypeNameAlias.toUpperCase());
                            arrayMapping.setReferenceClassName(ordt.getJavaClassName());
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
    public void handleAttributeField(String attributeFieldName) {
        stac.push(new AttributeFieldHelper(attributeFieldName, null));
    }
}