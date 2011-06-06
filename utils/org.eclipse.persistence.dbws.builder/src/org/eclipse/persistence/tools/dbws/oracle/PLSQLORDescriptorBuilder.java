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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

//EclipseLink imports
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.structures.ArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.StructureMapping;
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherDefaultListener;

import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;

public class PLSQLORDescriptorBuilder extends PublisherDefaultListener {

    public static final String ITEMS_MAPPING_ATTRIBUTE_NAME = "items";
    public static final String ITEMS_MAPPING_FIELD_NAME = "ITEMS";

    protected Stack<ListenerHelper> stac = new Stack<ListenerHelper>();
    protected Map<String, ObjectRelationalDataTypeDescriptor> descriptorMap =
        new HashMap<String, ObjectRelationalDataTypeDescriptor>();
    protected String packageName = null;

    public PLSQLORDescriptorBuilder() {
        super();
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
        TableHelper tableHelper = (TableHelper)stac.peek();
        String tableAlias = tableHelper.targetTypeName().toLowerCase();
        ObjectRelationalDataTypeDescriptor ordt = descriptorMap.get(tableAlias);
        if (ordt == null) {
            ordt = new ObjectRelationalDataTypeDescriptor();
            ordt.descriptorIsAggregate();
            ordt.setAlias(tableHelper.tableAlias());
            ordt.setJavaClassName(tableName.toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
            ordt.getQueryManager();
        }
        boolean itemsMappingFound =
            ordt.getMappingForAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME) == null ? false : true;
        if (top.isRecord()) {
            if (!itemsMappingFound) {
                ObjectArrayMapping itemsMapping = new ObjectArrayMapping();
                itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                itemsMapping.setFieldName(ITEMS_MAPPING_FIELD_NAME);
                itemsMapping.setStructureName(tableHelper.targetTypeName());
                itemsMapping.setReferenceClassName(((RecordHelper)top).recordName().toLowerCase());
                ordt.addMapping(itemsMapping);
            }
            tableHelper.nestedIsComplex();
        }
        else  {
            if (!itemsMappingFound) {
                ArrayMapping itemsMapping = new ArrayMapping();
                itemsMapping.setAttributeName(ITEMS_MAPPING_ATTRIBUTE_NAME);
                itemsMapping.setFieldName(ITEMS_MAPPING_FIELD_NAME);
                itemsMapping.useCollectionClass(ArrayList.class);
                itemsMapping.setStructureName(tableHelper.targetTypeName());
                ordt.addMapping(itemsMapping);
            }
            if (top.isTable()) {
                tableHelper.nestedIsComplex();
            }
        }
        if (!itemsMappingFound) {
            descriptorMap.put(tableAlias, ordt);
        }
    }
    @Override
    public void beginPlsqlRecord(String plsqlRecordName, String targetTypeName, int numFields) {
        RecordHelper recordHelper = new RecordHelper(plsqlRecordName, targetTypeName, numFields);
        stac.push(recordHelper);
        String recordAlias = targetTypeName.toLowerCase();
        ObjectRelationalDataTypeDescriptor ordt = descriptorMap.get(recordAlias);
        if (ordt == null) {
            ordt = new ObjectRelationalDataTypeDescriptor();
            ordt.descriptorIsAggregate();
            ordt.setAlias(recordAlias);
            ordt.setJavaClassName(plsqlRecordName.toLowerCase());
            ordt.getQueryManager();
            ordt.setStructureName(targetTypeName);
            descriptorMap.put(recordAlias, ordt);
        }
    }
    @SuppressWarnings({"unchecked"/*, "rawtypes"*/})
    @Override
    public void beginPlsqlRecordField(String fieldName, int idx) {
        RecordHelper recordHelper = (RecordHelper)stac.peek();
        ObjectRelationalDataTypeDescriptor ordt =
            descriptorMap.get(recordHelper.targetTypeName().toLowerCase());
        String lFieldName = fieldName.toLowerCase();
        boolean found = false;
        Vector orderedFields = ordt.getOrderedFields();
        for (Iterator i = orderedFields.iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof DatabaseField) {
                DatabaseField field = (DatabaseField)o;
                if (field.getName().equals(lFieldName)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            ordt.addFieldOrdering(lFieldName);
        }
    }
    @Override
    public void endPlsqlRecordField(String fieldName, int idx) {
        ListenerHelper top = stac.pop();
        ListenerHelper listenerHelper = stac.peek();
        if (listenerHelper.isRecord()) {
            RecordHelper recordHelper = (RecordHelper)stac.peek();
            ObjectRelationalDataTypeDescriptor ordt =
                descriptorMap.get(recordHelper.targetTypeName().toLowerCase());
            String lFieldName = fieldName.toLowerCase();
            if (ordt.getMappingForAttributeName(lFieldName) == null) {
                if (top.isComplex()) {
                    if (top.isTable()) {
                        if (((TableHelper)top).isNestedComplex()) {
                            ObjectArrayMapping objectArrayMapping = new ObjectArrayMapping();
                            objectArrayMapping.setAttributeName(lFieldName);
                            objectArrayMapping.setFieldName(lFieldName);
                            objectArrayMapping.setStructureName(top.targetTypeName());
                            objectArrayMapping.setReferenceClassName(((TableHelper)top).tableName()
                                .toLowerCase() + COLLECTION_WRAPPER_SUFFIX);
                            ordt.addMapping(objectArrayMapping);
                        }
                        else {
                            ArrayMapping arrayMapping = new ArrayMapping();
                            arrayMapping.setAttributeName(lFieldName);
                            arrayMapping.setFieldName(lFieldName);
                            arrayMapping.setStructureName(top.targetTypeName());
                            ordt.addMapping(arrayMapping);
                        }
                    }
                    else if (top.isRecord()) {
                        StructureMapping structureMapping = new StructureMapping();
                        structureMapping.setAttributeName(lFieldName);
                        structureMapping.setFieldName(lFieldName);
                        structureMapping.setReferenceClassName(((RecordHelper)top).recordName().toLowerCase());
                        ordt.addMapping(structureMapping);
                    }
                }
                else {
                    ordt.addDirectMapping(lFieldName, lFieldName);
                }
            }
        }
        else {
            System.identityHashCode(listenerHelper);
        }
    }
    @Override
    public void endPlsqlRecord(String recordName, String typeDDL, String typeDropDDL) {
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
}