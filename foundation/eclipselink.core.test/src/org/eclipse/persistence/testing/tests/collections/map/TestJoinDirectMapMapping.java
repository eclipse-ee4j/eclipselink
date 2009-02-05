/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.collections.map;

import java.util.Vector;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.models.collections.map.EntityDirectMapHolder;

public class TestJoinDirectMapMapping extends TestBatchReadEntityDirectMapMapping {

    public void test(){
        ReadAllQuery query = new ReadAllQuery(EntityDirectMapHolder.class);
        query.addJoinedAttribute("entityToDirectMap");
        holders = (Vector)getSession().executeQuery(query);
    }
    
}