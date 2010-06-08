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
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.jpa.dynamic;

//java eXtension imports
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.tools.schemaframework.DynamicSchemaManager;

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
    public void addTypes(boolean createMissingTables, boolean generateFKConstraints, Collection<DynamicType> types) {
        if (types == null || types.isEmpty()) {
            throw new IllegalArgumentException("No types provided");
        }

        Collection<ClassDescriptor> descriptors = new ArrayList<ClassDescriptor>(types.size());
        
        for (DynamicType type : types) {
            if (!type.getDescriptor().requiresInitialization()) {
                type.getDescriptor().getInstantiationPolicy().initialize((AbstractSession) session);
            }
            
            if (type.getDescriptor().getJavaClassName() != null) {
                fqClassnameToDescriptor.put(type.getDescriptor().getJavaClassName(), type.getDescriptor());
            }
            
            descriptors.add(type.getDescriptor());
        }

        session.addDescriptors(descriptors);
        
        if (createMissingTables) {
            if (!getSession().isConnected()) {
                getSession().login();
            }
            new DynamicSchemaManager(session).createTables(generateFKConstraints, types);
        }
    }
}
