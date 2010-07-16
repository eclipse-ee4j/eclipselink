/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.dynamic;

//java eXtension imports
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.jpa.JpaHelper;

/**
 * 
 * @author dclarke
 * @since EclipseLink 1.2
 */
public class JPADynamicHelper extends DynamicHelper {

    public JPADynamicHelper(EntityManagerFactory emf) {
        super(JpaHelper.getServerSession(emf));
    }
    
    public JPADynamicHelper(EntityManager em) {
        super(JpaHelper.getEntityManager(em).getServerSession());
    }
    
    /**
     * Add one or more EntityType instances to a session and optionally generate
     * needed tables with or without FK constraints.
     */
    public void addTypes(boolean createMissingTables, boolean generateFKConstraints, DynamicType... types) {
        super.addTypes(createMissingTables, generateFKConstraints, types);
        // bugs 316996 - support Sparse Merge via FetchGroups
        // JPA Dynamic Entities require 'checkDatabase' in order for sparse merge to work
        for (DynamicType type : types) {
            ClassDescriptor descriptor = type.getDescriptor();
            descriptor.getQueryManager().checkDatabaseForDoesExist();
        }
    }
}
