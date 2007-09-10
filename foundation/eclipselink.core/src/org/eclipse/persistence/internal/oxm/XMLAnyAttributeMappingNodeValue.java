/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;
import org.eclipse.persistence.internal.queries.DirectMapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Any Attribute Mapping is handled when
 * used with the TreeObjectBuilder.</p>
 */

public class XMLAnyAttributeMappingNodeValue extends XMLSimpleMappingNodeValue implements ContainerValue {
    private XMLAnyAttributeMapping xmlAnyAttributeMapping;

    public XMLAnyAttributeMappingNodeValue(XMLAnyAttributeMapping xmlAnyAttributeMapping) {
        super();
        this.xmlAnyAttributeMapping = xmlAnyAttributeMapping;
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return xPathFragment == null;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlAnyAttributeMapping.isReadOnly()) {
            return false;
        }
        DirectMapContainerPolicy cp = (DirectMapContainerPolicy)xmlAnyAttributeMapping.getContainerPolicy();
        Object collection = xmlAnyAttributeMapping.getAttributeValueFromObject(object);
        if (collection == null) {
            return false;
        }
        Object iter = cp.iteratorFor(collection);
        if (!cp.hasNext(iter)) {
            return false;
        }
        XPathFragment groupingElements = marshalRecord.openStartGroupingElements(namespaceResolver);
        while (cp.hasNext(iter)) {
            Object key = cp.next(iter, (org.eclipse.persistence.internal.sessions.AbstractSession)session);
            if (key instanceof QName) {
                QName name = (QName)key;
                String value = cp.valueFromKey(key, collection).toString();

                String qualifiedName = name.getLocalPart();
                NamespaceResolver nr = ((XMLDescriptor)xmlAnyAttributeMapping.getDescriptor()).getNamespaceResolver();
                if(nr != null){
                  String prefix = nr.resolveNamespaceURI(name.getNamespaceURI());
                  if ((prefix != null) && !prefix.equals("")) {
                      qualifiedName = prefix + ":" + qualifiedName;
                  }
                }
                marshalRecord.attribute(name.getNamespaceURI(), name.getLocalPart(), qualifiedName, value);
            }
        }
        marshalRecord.closeStartGroupingElements(groupingElements);
        return true;
    }

    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {
        try {
            DirectMapContainerPolicy cp = (DirectMapContainerPolicy)xmlAnyAttributeMapping.getContainerPolicy();
            Object containerInstance = unmarshalRecord.getContainerInstance(this);
            QName key = new QName(namespaceURI, localName);
            cp.addInto(key, value, containerInstance, (org.eclipse.persistence.internal.sessions.AbstractSession)unmarshalRecord.getSession());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object getContainerInstance() {
        return xmlAnyAttributeMapping.getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object container) {
        xmlAnyAttributeMapping.setAttributeValueInObject(object, container);
    }

    public boolean isContainerValue() {
        return true;
    }
}