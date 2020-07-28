/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.record;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.ConversionManager;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.w3c.dom.Node;

/**
 * This class represents marshal record behaviour that is specific to the SAX
 * platform.
 */
public interface MarshalRecord<
    ABSTRACT_SESSION extends CoreAbstractSession,
    FIELD extends CoreField,
    MARSHALLER extends Marshaller,
    NAMESPACE_RESOLVER extends NamespaceResolver> extends AbstractMarshalRecord<ABSTRACT_SESSION, FIELD, MARSHALLER, NAMESPACE_RESOLVER> {

    public void add(FIELD field, Object value);

    public void addGroupingElement(XPathNode holderXPathNode);

    public void afterContainmentMarshal(Object object, Object value);

    public void attribute(String namespaceURI, String localPart,
            String qualifiedName, String value);

    public void attribute(XPathFragment nextFragment,
            NAMESPACE_RESOLVER namespaceResolver, Object fieldValue,
            QName schemaType);

    public void attribute(XPathFragment xPathFragment,
            NAMESPACE_RESOLVER namespaceResolver, String value);

    public void attributeWithoutQName(String namespaceURI,
            String localName, String prefix, String value);

    public void beforeContainmentMarshal(Object value);

    public void cdata(String string);

    public void characters(QName schemaType, Object objectValue,
            String mimeType, boolean b);

    public void characters(String c_id);

    public void closeStartElement();

    public void closeStartGroupingElements(XPathFragment groupingFragment);

    public void emptyAttribute(XPathFragment xPathFragment,
            NAMESPACE_RESOLVER namespaceResolver);

    public boolean emptyCollection(XPathFragment xPathFragment,
            NAMESPACE_RESOLVER namespaceResolver, boolean b);

    public void emptyComplex(XPathFragment xPathFragment,
            NAMESPACE_RESOLVER namespaceResolver);

    public void emptySimple(NAMESPACE_RESOLVER namespaceResolver);

    public void endCollection();

    public void endElement(XPathFragment xPathFragment,
            NAMESPACE_RESOLVER namespaceResolver);

    public void endPrefixMapping(String prefix);

    public void flush();

    public void forceValueWrapper();

    public CoreAttributeGroup getCurrentAttributeGroup();

    public CycleDetectionStack<Object>  getCycleDetectionStack();

    public ArrayList<XPathNode> getGroupingElements();

    public XPathFragment getTextWrapperFragment();

    public String getValueToWrite(QName schemaType, Object fieldValue,
            ConversionManager conversionManager);

    public boolean hasCustomNamespaceMapper();

    public boolean isWrapperAsCollectionName();

    public boolean isXOPPackage();

    public void namespaceDeclaration(String generatedPrefix, String namespaceURI);

    public void nilComplex(XPathFragment xPathFragment,
            NAMESPACE_RESOLVER namespaceResolver);

    public void nilSimple(NAMESPACE_RESOLVER namespaceResolver);

    public void node(Node item, NAMESPACE_RESOLVER namespaceResolver);

    public void openStartElement(XPathFragment xPathFragment,
            NAMESPACE_RESOLVER namespaceResolver);

    public XPathFragment openStartGroupingElements(
            NAMESPACE_RESOLVER namespaceResolver);

    public void popAttributeGroup();

    public void predicateAttribute(XPathFragment anXPathFragment,
            NAMESPACE_RESOLVER namespaceResolver);

    public void pushAttributeGroup(CoreAttributeGroup group);

    public void removeExtraNamespacesFromNamespaceResolver(List<Namespace> extraNamespaces, CoreAbstractSession session);

    public void removeGroupingElement(XPathNode holderXPathNode);

    public void setGroupingElement(ArrayList<XPathNode> object);

    public void setLeafElementType(QName leafElementType);

    public void setMarshaller(MARSHALLER marshaller);

    public void startCollection();

    public void startPrefixMapping(String prefix, String uri);

    /**
     * A Stack-like List, used to detect object cycles during marshal operations.
     */
    public static class CycleDetectionStack<E> extends AbstractList<Object> {

        private Object[] data = new Object[8];

        int currentIndex = 0;

        public void push(E item) {
            if (currentIndex == data.length) {
                growArray();
            }
            data[currentIndex] = item;
            currentIndex++;
        }

        private void growArray() {
            Object[] newArray = new Object[data.length * 2];
            System.arraycopy(data, 0, newArray, 0, data.length);
            data = newArray;
        }

        public Object pop() {
            Object o = data[currentIndex - 1];
            data[currentIndex - 1] = null;
            currentIndex--;
            return o;
        }

        public boolean contains(Object item, boolean equalsUsingIdentity) {
            if (equalsUsingIdentity) {
                for (int i = 0; i < currentIndex; i++) {
                    if (data[i] == item) {
                        return true;
                    }
                }
            } else {
                for (int i = 0; i < currentIndex; i++) {
                    if (data[i] != null && data[i].equals(item)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public String getCycleString() {
            StringBuilder sb = new StringBuilder();
            int i = size() - 1;
            Object obj = get(i);
            sb.append(obj);
            Object x;
            do {
                sb.append(" -> ");
                x = get(--i);
                sb.append(x);
            } while (obj != x);

            return sb.toString();
        }

        @Override
        public Object get(int index) {
            return data[index];
        }

        @Override
        public int size() {
            return currentIndex;
        }

    }

}
