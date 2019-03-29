/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: If an implementation of NodeValue is capable of returning
 * a collection value then it must implement this interface to be handled
 * correctly by the TreeObjectBuilder.</p>
 */

public interface ContainerValue {
    Object getContainerInstance();

    void setContainerInstance(Object object, Object containerInstance);

    CoreContainerPolicy getContainerPolicy();

    /**
     * Marshal only one of the values from the collection.
     * @param xPathFragment
     * @param marshalRecord
     * @param object
     * @param value
     * @param session
     * @param namespaceResolver
     * @param marshalContext
     */
    boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext);

    Mapping getMapping();

    /**
     * Return true if the original container on the object should be used if
     * present.  If it is not present then the container policy will be used to
     * create the container.
     */
    boolean getReuseContainer();

    /**
     *  INTERNAL:
     *  Used to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord
     */
    int getIndex();

    /**
     * INTERNAL
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     * @since EclipseLink 2.3.3
     */
    boolean isDefaultEmptyContainer();

    /**
     * For media types that provide a native representation of collections (such
     * as JSON arrays), can the representation be simplified so that the
     * grouping element can be used as the collection name.
     */
    boolean isWrapperAllowedAsCollectionName();

    /**
     * INTERNAL:
     * Set to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord
     * Set during TreeObjectBuilder initialization
     */
    void setIndex(int index);



}
