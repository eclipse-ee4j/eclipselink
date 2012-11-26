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
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.record.MarshalRecord.CycleDetectionStack;
import org.w3c.dom.Node;

public interface MarshalRecord<
    ABSTRACT_SESSION extends CoreAbstractSession,
    FIELD extends CoreField> extends XMLRecord<ABSTRACT_SESSION, FIELD> {

    public void add(FIELD field, Object value);

    public void addGroupingElement(XPathNode holderXPathNode);

    public void afterContainmentMarshal(Object object, Object value);

    public void attribute(String namespaceURI, String localPart,
            String qualifiedName, String value);

    public void attribute(XPathFragment nextFragment,
            NamespaceResolver namespaceResolver, Object fieldValue,
            QName schemaType);

    public void attribute(XPathFragment xPathFragment,
            NamespaceResolver namespaceResolver, String value);

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
            NamespaceResolver namespaceResolver);

    public boolean emptyCollection(XPathFragment xPathFragment,
            NamespaceResolver namespaceResolver, boolean b);

    public void emptyComplex(XPathFragment xPathFragment,
            NamespaceResolver namespaceResolver);

    public void emptySimple(NamespaceResolver namespaceResolver);

    public void endCollection();

    public void endElement(XPathFragment xPathFragment,
            NamespaceResolver namespaceResolver);

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
            NamespaceResolver namespaceResolver);

    public void nilSimple(NamespaceResolver namespaceResolver);

    public void node(Node item, NamespaceResolver namespaceResolver);

    public void openStartElement(XPathFragment xPathFragment,
            NamespaceResolver namespaceResolver);

    public XPathFragment openStartGroupingElements(
            NamespaceResolver namespaceResolver);

    public void predicateAttribute(XPathFragment anXPathFragment,
            NamespaceResolver namespaceResolver);

    public void removeGroupingElement(XPathNode holderXPathNode);

    public void setGroupingElement(ArrayList<XPathNode> object);

    public void setLeafElementType(QName defaultRootElementType);

    public void setMarshaller(XMLMarshaller marshaller);

    public void startCollection();

    public void startPrefixMapping(String prefix, String uri);

}