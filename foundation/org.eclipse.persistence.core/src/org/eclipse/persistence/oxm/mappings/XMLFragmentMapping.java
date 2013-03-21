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
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.mappings.FragmentMapping;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
* <p><b>Purpose: </b>This mapping provides a means to keep a part of the xml tree as a DOM element. 
* 
* <p><b>Setting the XPath</b>: TopLink XML mappings make use of XPath statements to find the relevant
* data in an XML document.  The XPath statement is relative to the context node specified in the descriptor.
* The XPath may contain path and positional information;  the last node in the XPath forms the local
* root node for the fragment.  The XPath is specified on the mapping using the <code>setXPath</code>
* method.
* 
* <p><table border="1">
* <tr>
* <th id="c1" align="left">XPath</th>
* <th id="c2" align="left">Description</th>
* </tr>
* <tr>
* <td headers="c1">phone-number</td>
* <td headers="c2">The phone-number information is stored in the phone-number element.</td>
* </tr>
* <tr>
* <td headers="c1" nowrap="true">contact-info/phone-number</td>
* <td headers="c2">The XPath statement may be used to specify any valid path.</td>
* </tr>
* </table>
* <p><b>Sample Configuration:</b>
* <code><pre>
* XMLFragmentCollectionMapping mapping = new XMLFragmentCollectionMapping();
* mapping.setAttributeName("phoneNumber");
* mapping.setXPath("contact-info/phone-number");
* </pre>
* </code>
*/
public class XMLFragmentMapping extends XMLDirectMapping implements FragmentMapping<AbstractSession, AttributeAccessor, ContainerPolicy, ClassDescriptor, DatabaseField, Session, XMLRecord> {

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        if (isReadOnly()) {
            return;
        }
        Object attributeValue = getAttributeValueFromObject(object);

        // AttributeValue should be a node, try just putting it into the row
        writeSingleValue(attributeValue, object, (XMLRecord)row, session);
    }

    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) {
        DOMRecord domRecord = (DOMRecord) row;
        Object value = domRecord.getIndicatingNoEntry(this.getField(), true);
        if(value == domRecord) {
            value = domRecord.getDOM();
        }
        if (value instanceof Element) {
            XMLPlatformFactory.getInstance().getXMLPlatform().namespaceQualifyFragment((Element)value);
        }
        return value;
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

    @Override
    public boolean isAbstractColumnMapping() {
        return false;
    }

}
