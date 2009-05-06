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
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.StructureMapping;
import org.eclipse.persistence.platform.database.oracle.publisher.visit.PublisherDefaultListener;

public class AdvancedJDBCORDescriptorBuilder extends PublisherDefaultListener {

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
            // trim-off dotted-prefix
            int dotIdx = packageName.indexOf('.');
            if (dotIdx > -1) {
                this.packageName = packageName.substring(dotIdx+1);
            }
        }
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
            }  
        }
    }

    @Override
    public void handleObjectType(String objectTypeName, String targetTypeName, int numAttributes) {
        // JDBC Advanced type?
        if (numAttributes > 0) {
            // trim-off dotted-prefix, toLowerCase
            String objectTypeNameAlias = objectTypeName;
            int dotIdx = objectTypeName.indexOf('.');
            if (dotIdx > -1) {
                objectTypeNameAlias = objectTypeName.substring(dotIdx+1).toLowerCase();
            }
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
            }
            stac.push(new ObjectTypeHelper(objectTypeNameAlias, targetTypeName, numAttributes));
        }
    }

    @Override
    public void handleSqlArrayType(String arrayTypename, String targetTypeName) {
        // TODO
    }

    @Override
    public void handleSqlTableType(String tableTypeName, String targetTypeName) {
        // TODO
    }

    @Override
    public void handleAttributeField(String attributeFieldName) {
        stac.push(new AttributeFieldHelper(attributeFieldName, null));
    }
}