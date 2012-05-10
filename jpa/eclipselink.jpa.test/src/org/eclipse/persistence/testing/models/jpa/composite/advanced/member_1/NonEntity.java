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
 *     tware - Initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1;

/**
 * Used to test ProviderUtil
 * Intentionally not persistent
 * @author tware
 *
 */
public class NonEntity {

    private int id = 0;
    private String name = null;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        throw new RuntimeException("Called getName");
    }
    public void setName(String name) {
        throw new RuntimeException("Called setName");
    }

}
