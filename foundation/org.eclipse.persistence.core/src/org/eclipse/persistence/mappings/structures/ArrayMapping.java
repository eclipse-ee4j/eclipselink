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
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;


/**
 * <p><b>Purpose:</b>
 * In an object-relational data model, structures can contain "Arrays" or collections of other data-types.
 * In Oracle 8i, a "VARRAY" is typically used to represent a collection of primitive data or aggregate structures.
 * These arrays are stored with their parent structure in the same table.
 *
 * @see StructureMapping
 * @see NestedTableMapping
 * @see ReferenceMapping
 */
public class ArrayMapping extends AbstractCompositeDirectCollectionMapping {

    /**
     * Default constructor.
     */
    public ArrayMapping() {
        super();
    }
    
    /**
     * PUBLIC:
     * Set the name of the field that holds the nested collection.
     */
    public void setFieldName(String fieldName) {
        this.setField(new ObjectRelationalDatabaseField(fieldName));
    }
    
    
    /**
     * PUBLIC:
     * Return the name of the structure.
     * This is the name of the user-defined data type as defined on the database.
     */
    public String getStructureName() {
        return this.getElementDataTypeName();
    }

    /**
     * PUBLIC:
     * Set the name of the structure.
     * This is the name of the user-defined data type as defined on the database.
     */
    public void setStructureName(String structureName) {
        this.setElementDataTypeName(structureName);
    }
    
    
    /**
     * PUBLIC:
     * Return the "data type" associated with each element
     * in the nested collection.
     * Depending on the data store, this could be optional.
     */
    public String getElementDataTypeName() {
        return elementDataTypeName;
    }

    /**
     * PUBLIC:
     * Set the "data type" associated with each element
     * in the nested collection.
     * Depending on the data store, this could be optional.
     */
    public void setElementDataTypeName(String elementDataTypeName) {
        this.elementDataTypeName = elementDataTypeName;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isRelationalMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (this.getStructureName().length() == 0) {
            throw DescriptorException.structureNameNotSetInMapping(this);
        }

        // For bug 2730536 convert the field to be an ObjectRelationalDatabaseField.
        ObjectRelationalDatabaseField field = (ObjectRelationalDatabaseField)getField();
        field.setSqlType(java.sql.Types.ARRAY);
        field.setSqlTypeName(getStructureName());
    }

}
