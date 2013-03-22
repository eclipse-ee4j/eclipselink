/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractRecord;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;

/**
 * INTERNAL
 * A abstraction of inheritance policy capturing behavior common to all 
 * persistence types.
 */
public abstract class CoreInheritancePolicy<
    ABSTRACT_RECORD extends CoreAbstractRecord,
    ABSTRACT_SESSION extends CoreAbstractSession,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField> {

    /**
     * INTERNAL:
     * Add abstract class indicator information to the database row.  This is
     * required when building a row for an insert or an update of a concrete child
     * descriptor.
     */
    public abstract void addClassIndicatorFieldToRow(ABSTRACT_RECORD databaseRow);

    /**
     * INTERNAL:
     * Add the class name reference by class name, used by the MW.
     */
    public abstract void addClassNameIndicator(String childClassName, Object typeValue);

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
     * PUBLIC:
     * Return the class indicator field name.
     * This is the name of the field in the table that stores what type of object this is.
     */
    public abstract String getClassIndicatorFieldName();

    /**
     * INTERNAL:
     * Return the association of indicators and classes using specified ConversionManager
     */
    public abstract Map getClassIndicatorMapping();

    /**
     * INTERNAL:
     * Return the mapping from class name to indicator, used by MW.
     */
    public abstract Map getClassNameIndicatorMapping();
    
    /**
     * INTERNAL:
     * Returns the descriptor which the policy belongs to.
     */
    public abstract DESCRIPTOR getDescriptor();
    
    /**
     * PUBLIC:
     * Return the parent class.
     */
    public abstract Class getParentClass();
    
    /**
     * INTERNAL:
     * Return the parent descriptor.
     */
    public abstract DESCRIPTOR getParentDescriptor();

    /**
     * INTERNAL:
     */
    public abstract boolean hasClassExtractor();
    
    /**
     * INTERNAL:
     * Return whether or not is root parent descriptor
     */
    public abstract boolean isRootParentDescriptor();
    
    /**
     * ADVANCED:
     * Set the class extractor class name. At descriptor initialize time this
     * class will be converted to a Class and set as the ClassExtractor. This
     * method is called from JPA.
     * 
     * @see setClassExtractor for more information on the ClassExtractor class.
     */
    public abstract void setClassExtractorName(String classExtractorName);
    
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
    public abstract void setDescriptor(DESCRIPTOR descriptor);
    

    /**
     * INTERNAL:
     * Set the parent class name, used by MW to avoid referencing the real class for
     * deployment XML generation.
     */
    public abstract void setParentClassName(String parentClassName);
    
    /**
     * INTERNAL:
     * Set the descriptor to read instance of itself and its subclasses when queried.
     * This is used with inheritance to configure the result of queries.
     * By default this is true for root inheritance descriptors, and false for all others.
     */
    public abstract void setShouldReadSubclasses(Boolean shouldReadSubclasses);
}