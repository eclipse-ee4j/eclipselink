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
//     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
//               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//
// This code is being developed under INCUBATION and is not currently included
// in the automated EclipseLink build. The API in this code may change, or
// may never be included in the product. Please provide feedback through mailing
// lists or the bug database.
package org.eclipse.persistence.jpa.dynamic;

//java eXtension imports
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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
        super(JpaHelper.getEntityManager(em).getDatabaseSession());
    }

    /**
     * Add one or more EntityType instances to a session and optionally generate
     * needed tables with or without FK constraints.
     */
    @Override
    public void addTypes(boolean createMissingTables, boolean generateFKConstraints, DynamicType... types) {
        super.addTypes(createMissingTables, generateFKConstraints, types);
        for (DynamicType type : types) {
            ClassDescriptor descriptor = type.getDescriptor();
            descriptor.getQueryManager().checkDatabaseForDoesExist();
        }
    }
}
