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

import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
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

    public void attributeWithoutQName(String namespaceURI, String localName, String prefix, String value);

    public Node getDOM();

    public MARSHALLER getMarshaller();

    public NAMESPACE_RESOLVER getNamespaceResolver();

    public boolean hasCustomNamespaceMapper();

    public boolean hasEqualNamespaceResolvers();

    public void namespaceDeclaration(String prefix, String typeUri);

    public Object put(FIELD field, Object object);

}
