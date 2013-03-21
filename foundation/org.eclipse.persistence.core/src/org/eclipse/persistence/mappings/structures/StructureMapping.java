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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings.structures;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;


/**
 * <p><b>Purpose:</b>
 * A structure is an object-relational user-defined data-type or object-type. A structure is similar to a Java class
 * as it defines attributes or fields where each attribute is either a primitive data-type, another structure, an
 * array, or a reference to another structure.
 * The mapping is similar to an AggregateObjectMapping, as multiple objects are stored in a single table.
 */
public class StructureMapping extends AbstractCompositeObjectMapping {

    /**
     * Default constructor.
     */
    public StructureMapping() {
        super();
    }

    /**
     * INTERNAL:
     */
    public boolean isStructureMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Return the name of the structure.
     * This is the name of the user-defined data type as defined on the database.
     */
    public String getStructureName() {
        if (getReferenceDescriptor() instanceof ObjectRelationalDataTypeDescriptor) {
            return ((ObjectRelationalDataTypeDescriptor)getReferenceDescriptor()).getStructureName();
        } else {
            return "";
        }
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        // For bug 2730536 convert the field to be an ObjectRelationalDatabaseField.
        ObjectRelationalDatabaseField field = (ObjectRelationalDatabaseField)getField();
        field.setSqlType(java.sql.Types.STRUCT);
        field.setSqlTypeName(getStructureName());
    }

    public void setFieldName(String fieldName) {
        this.setField(new ObjectRelationalDatabaseField(fieldName));
    }
    
    @Override
    protected Object buildCompositeRow(Object attributeValue, AbstractSession session, AbstractRecord Record, WriteType writeType) {
        AbstractRecord nestedRow = this.getObjectBuilder(attributeValue, session).buildRow(attributeValue, session, writeType);
        return this.getReferenceDescriptor(attributeValue, session).buildFieldValueFromNestedRow(nestedRow, session);
    }
    
    @Override
    protected Object buildCompositeObject(ObjectBuilder objectBuilder, AbstractRecord nestedRow, ObjectBuildingQuery query, CacheKey parentCacheKey, JoinedAttributeManager joinManager, AbstractSession targetSession) {
        Object aggregateObject = objectBuilder.buildNewInstance();
        objectBuilder.buildAttributesIntoObject(aggregateObject, parentCacheKey, nestedRow, query, joinManager, query.getExecutionFetchGroup(objectBuilder.getDescriptor()), false, targetSession);
        return aggregateObject;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isRelationalMapping() {
        return true;
    }
}
