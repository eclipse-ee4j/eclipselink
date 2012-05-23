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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.*;

@Embeddable
public class GolferPK implements java.io.Serializable {
	private int id;

    public GolferPK() {}

    public GolferPK(int id) { 
        this.id = id;
    }
    
    @Column(name="WORLDRANK_ID", nullable=false, insertable=false, updatable=false)
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String toString() { 
        return "GolferPK id(" + id + ")";
    }
}
