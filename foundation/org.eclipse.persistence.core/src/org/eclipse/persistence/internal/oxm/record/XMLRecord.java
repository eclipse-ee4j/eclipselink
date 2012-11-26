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

import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.w3c.dom.Node;

public interface XMLRecord<
    ABSTRACT_SESSION extends CoreAbstractSession,
    FIELD extends CoreField> {

    /**
     * Nil: This is used to indicate that this field represents xsi:nil="true"
     */
    public static class Nil {
        private Nil() {
        }
    }

    /**
     * Nil: This is used to indicate that this field represents xsi:nil="true"
     */
    public static final XMLRecord.Nil NIL = new XMLRecord.Nil();

    public void attributeWithoutQName(String schemaInstanceUrl,
            String schemaTypeAttribute, String xsiPrefix, String typeValue);

    public Node getDOM();

    public XMLMarshaller getMarshaller();

    public NamespaceResolver getNamespaceResolver();

    public char getNamespaceSeparator();

    public ABSTRACT_SESSION getSession();

    public XMLUnmarshaller getUnmarshaller();

    public boolean hasCustomNamespaceMapper();

    public boolean hasEqualNamespaceResolvers();

    public boolean isNamespaceAware();

    public void namespaceDeclaration(String prefix, String typeUri);

    public Object put(FIELD field, Object object);

    public String resolveNamespacePrefix(String prefix);

}