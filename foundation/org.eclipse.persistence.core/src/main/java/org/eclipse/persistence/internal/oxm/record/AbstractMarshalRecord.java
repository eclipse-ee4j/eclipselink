/*
 * Copyright (c) 2013, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathQName;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.w3c.dom.Node;

/**
 * This class represents marshal record behaviour that is common to all XML
 * platforms.
 */
public interface AbstractMarshalRecord<
    ABSTRACT_SESSION extends CoreAbstractSession,
    FIELD extends CoreField,
    MARSHALLER extends Marshaller,
    NAMESPACE_RESOLVER extends NamespaceResolver> extends XMLRecord<ABSTRACT_SESSION> {

    List addExtraNamespacesToNamespaceResolver(Descriptor descriptor, CoreAbstractSession session, boolean allowOverride, boolean ignoreEqualResolvers);

    boolean addXsiTypeAndClassIndicatorIfRequired(Descriptor descriptor, Descriptor referenceDescriptor, Field xmlField, boolean isRootElement);

    boolean addXsiTypeAndClassIndicatorIfRequired(Descriptor descriptor, Descriptor referenceDescriptor, Field xmlField,Object originalObject, Object obj, boolean wasXMLRoot, boolean isRootElement);

    void attribute(String namespaceURI, String localName, String qualifiedName, String value);

    void attributeWithoutQName(String namespaceURI, String localName, String prefix, String value);

    Node getDOM();

    XPathQName getLeafElementType();

    MARSHALLER getMarshaller();

    NAMESPACE_RESOLVER getNamespaceResolver();

    Object getOwningObject();

    boolean hasCustomNamespaceMapper();

    boolean hasEqualNamespaceResolvers();

    /**
     * Determine if namespaces will be considered during marshal/unmarshal operations.
     */
    @Override
    boolean isNamespaceAware();

    boolean isXOPPackage();

    void namespaceDeclaration(String prefix, String typeUri);

    Object put(FIELD field, Object object);

    void removeExtraNamespacesFromNamespaceResolver(List<Namespace> extraNamespaces, CoreAbstractSession session);

    String resolveNamespacePrefix(String prefix);

    void setCustomNamespaceMapper(boolean customNamespaceMapper);

    void setEqualNamespaceResolvers(boolean equalNRs);

    void setLeafElementType(QName leafElementType);

    void setLeafElementType(XPathQName leafElementType);

    void setMarshaller(MARSHALLER marshaller);

    void setNamespaceResolver(NAMESPACE_RESOLVER namespaceResolver);

    void setOwningObject(Object owningObject);

    void setSession(ABSTRACT_SESSION session);

    void setXOPPackage(boolean isXOPPackage);

    void writeXsiTypeAttribute(Descriptor descriptor, String typeUri,  String  typeLocal, String typePrefix, boolean addToNamespaceResolver);

    void writeXsiTypeAttribute(Descriptor xmlDescriptor, XMLSchemaReference xmlRef, boolean addToNamespaceResolver);

}
