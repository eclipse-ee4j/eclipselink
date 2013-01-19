/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
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

    public List addExtraNamespacesToNamespaceResolver(Descriptor descriptor, CoreAbstractSession session, boolean allowOverride, boolean ignoreEqualResolvers);

    public boolean addXsiTypeAndClassIndicatorIfRequired(Descriptor descriptor, Descriptor referenceDescriptor, Field xmlField, boolean isRootElement);

    public boolean addXsiTypeAndClassIndicatorIfRequired(Descriptor descriptor, Descriptor referenceDescriptor, Field xmlField,Object originalObject, Object obj, boolean wasXMLRoot, boolean isRootElement);

    public void attribute(String namespaceURI, String localName, String qualifiedName, String value);

    public void attributeWithoutQName(String namespaceURI, String localName, String prefix, String value);

    public Node getDOM();

    public XPathQName getLeafElementType();

    public MARSHALLER getMarshaller();

    public NAMESPACE_RESOLVER getNamespaceResolver();

    public Object getOwningObject();

    public boolean hasCustomNamespaceMapper();

    public boolean hasEqualNamespaceResolvers();

    /**
     * Determine if namespaces will be considered during marshal/unmarshal operations.
     */
    public boolean isNamespaceAware();

    public boolean isXOPPackage();

    public void namespaceDeclaration(String prefix, String typeUri);

    public Object put(FIELD field, Object object);

    public void removeExtraNamespacesFromNamespaceResolver(List<Namespace> extraNamespaces, CoreAbstractSession session);

    public String resolveNamespacePrefix(String prefix);
 
    public void setCustomNamespaceMapper(boolean customNamespaceMapper);

    public void setEqualNamespaceResolvers(boolean equalNRs);

    public void setLeafElementType(QName leafElementType);

    public void setLeafElementType(XPathQName leafElementType);

    public void setMarshaller(MARSHALLER marshaller);

    public void setNamespaceResolver(NAMESPACE_RESOLVER namespaceResolver);

    public void setOwningObject(Object owningObject);

    public void setSession(ABSTRACT_SESSION session);

    public void setXOPPackage(boolean isXOPPackage);

    public void writeXsiTypeAttribute(Descriptor descriptor, String typeUri,  String  typeLocal, String typePrefix, boolean addToNamespaceResolver);

    public void writeXsiTypeAttribute(Descriptor xmlDescriptor, XMLSchemaReference xmlRef, boolean addToNamespaceResolver);

}
