/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="JPA_TPC_SUBCLASS")
public class SubclassEntityTablePerClass extends MappedSuperclassTablePerClass {
    
    @Column(name="SUBCLASS_ATTRIBUTE")
    protected String subclassAttribute;
    
    public SubclassEntityTablePerClass() {
        super();
    }

    public String getSubclassAttribute() {
        return subclassAttribute;
    }

    public void setSubclassAttribute(String subclassAttribute) {
        this.subclassAttribute = subclassAttribute;
    }

}
