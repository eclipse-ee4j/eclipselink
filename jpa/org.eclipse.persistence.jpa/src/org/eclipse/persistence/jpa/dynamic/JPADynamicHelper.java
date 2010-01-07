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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicHelper;
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

}
