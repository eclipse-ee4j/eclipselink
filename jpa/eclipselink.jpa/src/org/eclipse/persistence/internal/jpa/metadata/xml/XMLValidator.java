/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.lang.reflect.AnnotatedElement;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataValidator;

/**
 * XML validator class.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLValidator extends MetadataValidator  { 
    /**
     * INTERNAL
     */
    public XMLValidator() {}
    
    /**
     * INTERNAL
     */
    public void throwEmbeddedIdAndIdFound(Class entityClass, String attributeName, String idAttributeName) {
        throw ValidationException.embeddedIdAndIdElementFound(entityClass, attributeName, idAttributeName);
    }
    
    /**
     * INTERNAL
     */
    public void throwErrorProcessingNamedQueryElement(String queryName, Exception exception) {
        throw ValidationException.errorProcessingNamedQueryElement(queryName, exception);
    }
    
    /**
     * INTERNAL
     */
    public void throwExcessiveJoinColumnsSpecified(Class entityClass, Object element) {
        throw ValidationException.excessiveJoinColumnElementsSpecified((String) element, entityClass);
    }
    
    /**
     * INTERNAL
     */
    public void throwExcessivePrimaryKeyJoinColumnsSpecified(Class entityClass, AnnotatedElement annotatedElement) {
        throw ValidationException.excessivePrimaryKeyJoinColumnElementsSpecified(entityClass);
    }
    
    /**
     * INTERNAL
     */
    public void throwIncompleteJoinColumnsSpecified(Class entityClass, Object element) {
        throw ValidationException.incompleteJoinColumnElementsSpecified(element, entityClass);
    }
    
    /**
     * INTERNAL
     */
    public void throwIncompletePrimaryKeyJoinColumnsSpecified(Class entityClass, AnnotatedElement annotatedElement) {
        throw ValidationException.incompletePrimaryKeyJoinColumnElementsSpecified(entityClass);
    }
    
    /**
     * INTERNAL
     */
    public void throwMultipleEmbeddedIdsFound(Class entityClass, String attributeName, String embeddedIdAttributeName) {
        throw ValidationException.multipleEmbeddedIdElementsFound(entityClass, attributeName, embeddedIdAttributeName);
    }
    
    /**
     * INTERNAL
     */
    public void throwNoMappedByAttributeFound(Class owningClass, String owningAttributeName, Class entityClass, String attributeName) {
        // ignore, not applicable.
    }
    
    /**
     * INTERNAL
     */
    public void throwNoTemporalTypeSpecified(Class entityClass, String attributeName) {
        // WIP - copied from AnnotationsValidator ... might need to have its own ...
        throw ValidationException.noTemporalTypeSpecified(attributeName, entityClass);
    }
    
    /**
     * INTERNAL
     */
    public void throwPersistenceUnitMetadataConflict(String element) {
        throw ValidationException.persistenceUnitMetadataConflict(element);
    }
    
    /**
     * INTERNAL
     */
    public void throwRelationshipHasColumnSpecified(Class entityClass, String attributeName) {
        throw ValidationException.invalidColumnElementOnRelationship(entityClass, attributeName);
    }
    
    /**
     * INTERNAL:
     */  
    public void throwSequenceGeneratorUsingAReservedName(String document, String reservedName) {
        throw ValidationException.sequenceGeneratorUsingAReservedName(reservedName, document);
    }
    
    /**
     * INTERNAL:
     */  
    public void throwTableGeneratorUsingAReservedName(String document, String reservedName) {
        throw ValidationException.tableGeneratorUsingAReservedName(reservedName, document);
    }
    
    /**
     * INTERNAL
     */
    public void throwUniDirectionalOneToManyHasJoinColumnSpecified(String attributeName, Class entityClass) {
        throw ValidationException.uniDirectionalOneToManyHasJoinColumnElements(attributeName, entityClass);
    }
}
