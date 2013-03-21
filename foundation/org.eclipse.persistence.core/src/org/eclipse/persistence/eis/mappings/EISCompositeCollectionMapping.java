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
package org.eclipse.persistence.eis.mappings;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;

/**
 * <p>EIS Composite Collection Mappings map a java.util.Map or java.util.Collection of Java objects 
 * to an EIS record in a privately owned, one-to-many relationship according to its descriptor's
 * record type.
 * 
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">Record Type</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">Indexed</td>
 * <td headers="c2">Ordered collection of record elements.  The indexed record EIS format 
 * enables Java class attribute values to be retrieved by position or index.</td>
 * </tr>
 * <tr>
 * <td headers="c1">Mapped</td>
 * <td headers="c2">Key-value map based representation of record elements.  The mapped record
 * EIS format enables Java class attribute values to be retrieved by an object key.</td>
 * </tr>
 * <tr>
 * <td headers="c1">XML</td>
 * <td headers="c2">Record/Map representation of an XML DOM element.</td>
 * </tr>
 * </table>
 * 
 * @see org.eclipse.persistence.eis.EISDescriptor#useIndexedRecordFormat
 * @see org.eclipse.persistence.eis.EISDescriptor#useMappedRecordFormat
 * @see org.eclipse.persistence.eis.EISDescriptor#useXMLRecordFormat
 * 
 * @since Oracle TopLink 10<i>g</i> Release 2 (10.1.3)
 */
public class EISCompositeCollectionMapping extends AbstractCompositeCollectionMapping implements EISMapping {
    public EISCompositeCollectionMapping() {
        super();
    }

    /**
     * INTERNAL:
     */
    public boolean isEISMapping() {
        return true;
    }

    /**
     * Fix field names for XML data descriptors.
     * Since fields are fixed to use text() by default in descriptor, ensure the correct non text field is used here.
     */
    @Override
    public void preInitialize(AbstractSession session) {
        super.preInitialize(session);
        if (((EISDescriptor)this.descriptor).isXMLFormat()) {
            if (!(this.field instanceof XMLField)) {
                XMLField newField = new XMLField(this.field.getName());
                this.field = newField;
            }
        }
    }
    
    /**
     * INTERNAL:
     * The mapping is initialized with the given session. This mapping is fully initialized
     * after this.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (this.getContainerPolicy() instanceof MapContainerPolicy) {
            ((MapContainerPolicy)getContainerPolicy()).setElementClass(this.getReferenceClass());
        }
    }

    /**
     * Get the XPath String
     *
     * @return String the XPath String associated with this Mapping
     *
     */
    public String getXPath() {
        return getField().getName();
    }

    /**
     * Set the Mapping field name attribute to the given XPath String
     *
     * @param xpathString String
     *
     */
    public void setXPath(String xpathString) {
        this.setField(new XMLField(xpathString));
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

    @Override
    protected Object buildCompositeObject(ClassDescriptor descriptor, AbstractRecord nestedRow, ObjectBuildingQuery query, CacheKey parentsCacheKey, JoinedAttributeManager joinManager, AbstractSession targetSession) {
        if (((EISDescriptor)descriptor).isXMLFormat()) {
            return descriptor.getObjectBuilder().buildObject(query, nestedRow, joinManager);
        } else {
            Object element = descriptor.getObjectBuilder().buildNewInstance();
            descriptor.getObjectBuilder().buildAttributesIntoObject(element, parentsCacheKey, nestedRow, query, joinManager, query.getExecutionFetchGroup(descriptor), false, targetSession);
            return element;
        }
    }

    @Override
    protected AbstractRecord buildCompositeRow(Object attributeValue, AbstractSession session, AbstractRecord parentRow, WriteType writeType) {
        if (((EISDescriptor)getDescriptor()).isXMLFormat()) {
            XMLObjectBuilder objectBuilder = (XMLObjectBuilder)getReferenceDescriptor(attributeValue, session).getObjectBuilder();
            return objectBuilder.buildRow(attributeValue, session, getField(), (XMLRecord)parentRow);
        } else {
            return this.getObjectBuilder(attributeValue, session).buildRow(attributeValue, session, writeType);
        }
    }
}
