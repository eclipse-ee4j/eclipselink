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
 *     tware - testing for DDL issue with embedded and MTM
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class PatentCollection {

    @ManyToMany 
    private List<Patent> patents = new ArrayList<Patent>();

    public List<Patent> getPatents() {
        return patents;
    }

    public void setPatents(List<Patent> patents) {
        this.patents = patents;
    }

}
