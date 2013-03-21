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
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * <p><b>Purpose:</b>
 * In an object-relational data model, structures can contain "Arrays" or collections of other data-types.
 * In Oracle 8i, a <code>Varray</code> is typically used to represent a collection of primitive data or aggregate structures.
 * These arrays are stored with their parent structure in the same table.<p>
 *
 * ArrayMapping is used to map a collection of primitive data <p>
 * ObjectArrayMapping is used to map a collection of Oracle data-type
 *
 * <p>NOTE: Only Oracle8i supports Varray type.
 *
 * @author King (Yaoping) Wang
 * @since TOPLink/Java 3.0
 *
 * @see ArrayMapping
 */
public class ObjectArrayMapping extends AbstractCompositeCollectionMapping {

    /** Arrays require a structure name, this is the ADT defined for the VARRAY. */
    protected String structureName;

    /**
     * PUBLIC:
     * Return the name of the structure.
     * This is the name of the user defined data type as defined on the database.
     */
    public String getStructureName() {
        return structureName;
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if ((getStructureName() == null) || getStructureName().length() == 0) {
            throw DescriptorException.structureNameNotSetInMapping(this);
        }

        // For bug 2730536 convert the field to be an ObjectRelationalDatabaseField.
        ObjectRelationalDatabaseField field = (ObjectRelationalDatabaseField)getField();
        field.setSqlType(java.sql.Types.ARRAY);
        field.setSqlTypeName(getStructureName());

        // May require native connection in WLS to avoid wrapping wrapped.
        getDescriptor().setIsNativeConnectionRequired(true);
    }

    public void setFieldName(String fieldName) {
        this.setField(new ObjectRelationalDatabaseField(fieldName));
    }

    /**
     * PUBLIC:
     * Set the name of the structure.
     * This is the name of the user defined data type as defined on the database.
     */
    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }
    
    @Override
    protected Object buildCompositeObject(ClassDescriptor descriptor, AbstractRecord nestedRow, ObjectBuildingQuery query, CacheKey parentCacheKey, JoinedAttributeManager joinManager, AbstractSession targetSession) {
        Object element = descriptor.getObjectBuilder().buildNewInstance();
        descriptor.getObjectBuilder().buildAttributesIntoObject(element, parentCacheKey, nestedRow, query, joinManager, query.getExecutionFetchGroup(descriptor), false, targetSession);
        return element;
    }

    @Override
    protected AbstractRecord buildCompositeRow(Object attributeValue, AbstractSession session, AbstractRecord parentRow, WriteType writeType) {
        return this.getObjectBuilder(attributeValue, session).buildRow(attributeValue, session, writeType);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isRelationalMapping() {
        return true;
    }
}
