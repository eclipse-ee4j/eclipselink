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
package org.eclipse.persistence.core.descriptors;

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractRecord;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;

public abstract class CoreInheritancePolicy<
    ABSTRACT_RECORD extends CoreAbstractRecord,
    ABSTRACT_SESSION extends CoreAbstractSession,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField> {

    /**
     * INTERNAL:
     * This method is invoked only for the abstract descriptors.
     */
    public abstract Class classFromRow(ABSTRACT_RECORD record, ABSTRACT_SESSION session);

    /**
     * INTERNAL:
     * Returns all the child descriptors, even descriptors for subclasses of
     * subclasses.
     * Required for bug 3019934.
     */
    public abstract List<DESCRIPTOR> getAllChildDescriptors();

    /**
     * INTERNAL:
     * Returns field that the class type indicator is store when using inheritance.
     */
    public abstract FIELD getClassIndicatorField();
    
    /**
     * INTERNAL:
     * Return the association of indicators and classes using specified ConversionManager
     */
    public abstract Map getClassIndicatorMapping();
    
    /**
     * INTERNAL:
     * Returns the descriptor which the policy belongs to.
     */
    public abstract DESCRIPTOR getDescriptor();

    /**
     * ADVANCED:
     * To set the class indicator field.
     * This can be used for advanced field types, such as XML nodes, or to set the field type.
     */
    public abstract void setClassIndicatorField(FIELD classIndicatorField);

    /**
     * PUBLIC:
     * Set the association of indicators and classes.
     * This may be desired to be used by clients in strange inheritance models.
     */
    public abstract void setClassIndicatorMapping(Map classIndicatorMapping);

    /**
     * INTERNAL:
     * Set the descriptor.
     */
    public abstract void setDescriptor(ClassDescriptor descriptor);

}