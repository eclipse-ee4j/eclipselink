/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.w3c.dom.Text;

/**
 * PUBLIC:
 * <p><b>Purpose:</b>This mapping provides a means to keep a part of an XML tree as a Node.
 */
public class XMLFragmentMapping extends XMLDirectMapping {
    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session) {
        if (isReadOnly()) {
            return;
        }
        Object attributeValue = getAttributeValueFromObject(object);
        // AttributeValue should be a node, try just putting it into the row
        writeSingleValue(attributeValue, object, (XMLRecord)row, session);
    }
    
     public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, AbstractSession executionSession) {
        return ((DOMRecord)row).getIndicatingNoEntry(this.getField(), true);
     }
     
     public void writeSingleValue(Object attributeValue, Object parent, XMLRecord row, AbstractSession session) {     
         if (((XMLField)this.getField()).getLastXPathFragment().nameIsText()) {
             if (attributeValue instanceof Text) {
                 attributeValue = ((Text)attributeValue).getNodeValue();
             }
         }
         row.put(getField(), attributeValue);
     }
     
     public void setXPath(String xpathString) {
         setField(new XMLField(xpathString));
     }     
     
     public boolean isAbstractDirectMapping() {
         return false;
     }
}