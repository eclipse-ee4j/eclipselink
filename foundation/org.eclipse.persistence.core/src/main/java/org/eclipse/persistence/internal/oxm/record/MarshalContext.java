/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;

/**
 * The MarshalContext allows mappings to be marshalled differently depending on
 * the type of object.  For example POJOs are marshalled based on the order in
 * which mappings were added to the descriptor, while sequenced objects are based
 * on the order of their Setting objects.
 */
public interface MarshalContext {

    /**
     * @return Return the MarshalContext at the specified position.
     */
    MarshalContext getMarshalContext(int index);

    /**
     * @return The number of non-attribute children.  For POJOs this is
     * based on the number of non-attribute mappings, and for sequenced
     * objects this is based on the number of Setting objects.
     */
    int getNonAttributeChildrenSize(XPathNode xPathNode);

    /**
     * @return The non-attribute child at the specified index for the
     * specified xPathNode.
     */
    Object getNonAttributeChild(int index, XPathNode xPathNode);

    /**
     * @return the attribute value corresponding to the object parameter.
     */
    Object getAttributeValue(Object object, Mapping mapping);

    /**
     * Perform a marshal using the NodeValue parameter.
     * @return If anything as marshalled as a result of this call.
     */
    boolean marshal(NodeValue nodeValue, XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver);

    boolean marshal(NodeValue nodeValue, XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, XPathFragment rootFragment);

}
