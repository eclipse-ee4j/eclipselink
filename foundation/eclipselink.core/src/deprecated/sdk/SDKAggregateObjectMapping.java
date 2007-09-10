/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.sdk;

import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.queries.*;

/**
 * Chunks of data from non-relational data sources can have an
 * embedded component objects. These can be
 * mapped using this mapping. The format of the embedded
 * data is determined by the reference descriptor.
 *
 * @see SDKDescriptor
 * @see SDKFieldValue
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public class SDKAggregateObjectMapping extends AbstractCompositeObjectMapping {

    /**
     * Default constructor.
     */
    public SDKAggregateObjectMapping() {
        super();
    }

    /**
    * PUBLIC:
    * Return the name of the field mapped by the mapping.
    */
    public String getFieldName() {
        return this.getField().getName();
    }

    /**
     * PUBLIC:
     * Set the name of the field mapped by the mapping.
     */
    public void setFieldName(String fieldName) {
        this.setField(new DatabaseField(fieldName));
    }

    protected Object buildCompositeRow(Object attributeValue, AbstractSession session, AbstractRecord Record) {
    	AbstractRecord nestedRow = this.getObjectBuilder(attributeValue, session).buildRow(attributeValue, session);
        return this.getReferenceDescriptor(attributeValue, session).buildFieldValueFromNestedRow(nestedRow, session);
    }

    protected Object buildCompositeObject(ObjectBuilder objectBuilder, AbstractRecord nestedRow, ObjectBuildingQuery query, JoinedAttributeManager joinManager) {
        Object aggregateObject = objectBuilder.buildNewInstance();
        objectBuilder.buildAttributesIntoObject(aggregateObject, nestedRow, query, joinManager, false);
        return aggregateObject;
    }
}