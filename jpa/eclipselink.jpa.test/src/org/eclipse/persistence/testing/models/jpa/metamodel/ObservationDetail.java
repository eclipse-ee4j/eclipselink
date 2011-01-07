/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/27/2010-2.2  mobrien - JPA 2.0 Metadata API test model
 *       - 300626: Nested embeddable test case coverage  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Embeddable;

@Embeddable
public class ObservationDetail {
    // This class is embedded inside a GalacticPosition via an embbedded Observation
    
    private String data;
    
    public ObservationDetail() {        
    }
    
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

