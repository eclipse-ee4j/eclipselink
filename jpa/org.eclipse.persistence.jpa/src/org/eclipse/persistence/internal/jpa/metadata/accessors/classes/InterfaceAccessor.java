/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/26/2008-1.0M6 Guy Pelletier 
 *       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     05/04/2010-2.1 Guy Pelletier 
 *       - 309373: Add parent class attribute to EclipseLink-ORM
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.util.HashSet;

import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VariableOneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * An interface accessor. This is kinda forward thinking. I assume once we
 * get into full interface support etc. this class will handle much more and
 * will map directly to an interface schema element from the eclipselink orm
 * schema.
 * 
 * Things that should or could be mapped on this interface are:
 *  - alias
 *  - query keys
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject  method.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class InterfaceAccessor extends ClassAccessor {
    private HashSet<VariableOneToOneAccessor> m_variableOneToOneAccessors;
    
    /**
     * INTERNAL:
     */
    public InterfaceAccessor(MetadataAnnotation annotation, MetadataClass cls, MetadataProject project) {
        super(annotation, cls, project);
        m_variableOneToOneAccessors = new HashSet<VariableOneToOneAccessor>();
    }

    /**
     * INTERNAL:
     * Add the given entity accessor to this interface's list of variable one
     * to one accessors.
     */
    public void addEntityAccessor(EntityAccessor accessor) {
        for (VariableOneToOneAccessor variableOneToOne : m_variableOneToOneAccessors) {
            variableOneToOne.addDiscriminatorClassFor(accessor);
        } 
    }
    
    /**
     * INTERNAL:
     * Query keys are stored internally in a map (keyed on the query key name).
     * Therefore, adding the same query key name multiple times (for each
     * variable one to one accessor to this interface) will not cause a problem. 
     */
    public void addQueryKey(String queryKeyName) {
        getDescriptor().getClassDescriptor().addAbstractQueryKey(queryKeyName);
    }
    
    /**
     * INTERNAL:
     * Add a variable one to one accessor for this interface. Those entities 
     * that implement the interface on the accessor will need to make sure they
     * add themselves to the class indicator list. See the process method below
     * which is called from MetadataProject processing.
     */
    public void addVariableOneToOneAccessor(VariableOneToOneAccessor accessor) {
        m_variableOneToOneAccessors.add(accessor);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void process() {
        // Does nothing at this point ... perhaps it will in the future ...
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void processAccessType() {
        // Does nothing at this point ... perhaps it will in the future ...
    }
}
