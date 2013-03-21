/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
 package org.eclipse.persistence.mappings.structures;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.mappings.ContainerMapping;

/**
 * Interface used by the <code>ArrayCollectionMappingHelper</code> to interact
 * with the assorted array collection mappings.
 * @see ArrayCollectionMappingHelper
 */

public interface ArrayCollectionMapping extends ContainerMapping{
    /**
     * Build and return a newly-added element based on the change set.
     */
    Object buildAddedElementFromChangeSet(Object changeSet, MergeManager mergeManager, AbstractSession targetSession);

    /**
     * Build and return a change set for the specified element.
     */
    Object buildChangeSet(Object element, ObjectChangeSet owner, AbstractSession session);

    /**
     * Build and return a new element based on the specified element.
     */
    Object buildElementFromElement(Object element, MergeManager mergeManager, AbstractSession targetSession);

    /**
     * Build and return a recently-removed element based on the change set.
     */
    Object buildRemovedElementFromChangeSet(Object changeSet, MergeManager mergeManager, AbstractSession targetSession);

    /**
     * Compare the non-null elements and return true if they are alike.
     */
    boolean compareElements(Object element1, Object element2, AbstractSession session);

    /**
     * Compare the non-null elements and return true if they are alike.
     * This is used to build a change record.
     */
    boolean compareElementsForChange(Object element1, Object element2, AbstractSession session);

    /**
     * Return the mapping's attribute name.
     */
    String getAttributeName();

    /**
     * Return the attribute value from the specified object,
     * unwrapping the value holder if necessary.
     * If the value is null, build a new container.
     */
    Object getRealCollectionAttributeValueFromObject(Object object, AbstractSession session);

    /**
     * Return whether the element's user-defined Map key has changed
     * since it was cloned from the original version.
     */
    boolean mapKeyHasChanged(Object element, AbstractSession session);

    /**
     * Set the attribute value for the specified object,
     * wrapping it in a value holder if necessary.
     */
    void setRealAttributeValueInObject(Object object, Object attributeValue);
}
