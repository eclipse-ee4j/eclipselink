/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.oxm.record.MarshalRecord.CycleDetectionStack;
import org.w3c.dom.Node;

public interface MarshalRecord<
    ABSTRACT_SESSION extends CoreAbstractSession,
    FIELD extends CoreField,
    MARSHALLER extends Marshaller,
    NAMESPACE_RESOLVER extends NamespaceResolver,
    UNMARSHALLER extends Unmarshaller> extends XMLRecord<ABSTRACT_SESSION, FIELD, MARSHALLER, NAMESPACE_RESOLVER, UNMARSHALLER> {

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

    public void attributeWithoutQName(String schemaInstanceUrl,
            String schemaTypeAttribute, String xsiPrefix, String typeValue);

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

    public CycleDetectionStack<Object>  getCycleDetectionStack();

    public ArrayList<XPathNode> getGroupingElements();

    public XPathFragment getTextWrapperFragment();

    public String getValueToWrite(QName schemaType, Object fieldValue,
            XMLConversionManager conversionManager);

    public boolean hasCustomNamespaceMapper();

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

    public void predicateAttribute(XPathFragment anXPathFragment,
            NAMESPACE_RESOLVER namespaceResolver);

    public void removeGroupingElement(XPathNode holderXPathNode);

    public void setGroupingElement(ArrayList<XPathNode> object);

    public void setLeafElementType(QName defaultRootElementType);

    public void setMarshaller(MARSHALLER marshaller);

    public void startCollection();

    public void startPrefixMapping(String prefix, String uri);

}