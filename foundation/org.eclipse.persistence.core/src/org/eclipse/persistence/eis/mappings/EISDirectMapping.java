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
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/  
package org.eclipse.persistence.eis.mappings;

import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

/**
 * <p>EIS Direct Mappings map a simple Java attribute to and from an EIS Record according to 
 * its descriptor's record type.  
 * 
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">Record Type</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">Indexed</td>
 * <td headers="c2">Ordered collection of record elements.  The indexed record EIS format 
 * enables Java class attribute values to be retreived by position or index.</td>
 * </tr>
 * <tr>
 * <td headers="c1">Mapped</td>
 * <td headers="c2">Key-value map based representation of record elements.  The mapped record
 * EIS format enables Java class attribute values to be retreived by an object key.</td>
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
public class EISDirectMapping extends AbstractDirectMapping implements EISMapping {
    public EISDirectMapping() {
        super();
    }

    /**
     * INTERNAL:
     */
    public boolean isEISMapping() {
        return true;
    }

    /**
     * Set the Mapping field name attribute to the given XPath String.
     * @param xpathString String
     */
    public void setXPath(String xpathString) {
        setField(new XMLField(xpathString));
    }

    /**
     * Get the XPath String associated with this Mapping
     * @return String the XPath String associated with this Mapping
     */
    public String getXPath() {
        return getFieldName();
    }

    /**
     * PUBLIC:
     * Set the field name in the mapping.
     */
    public void setFieldName(String fieldName) {
        setField(new DatabaseField(fieldName));
    }

    protected void writeValueIntoRow(AbstractRecord row, DatabaseField field, Object fieldValue) {
        if (((EISDescriptor)this.getDescriptor()).isXMLFormat()) {
            row.put(field, fieldValue);
        } else {
            row.add(field, fieldValue);
        }
    }

    /**
     * INTERNAL:
     * We override this method in order to set the session on the record if the data
     * format is XML.
     * 
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        if (((EISDescriptor) this.getDescriptor()).isXMLFormat()) {
            ((XMLRecord) row).setSession(session);
        }
        super.writeFromObjectIntoRow(object, row, session, writeType);
    }
}
