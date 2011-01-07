/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Embeddable;

@Embeddable
public class RedStripe {
    private Double alcoholContent;
    
    public RedStripe() {}
    
    public RedStripe(Double content) {
        this.alcoholContent = content;
    }

    public Double getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(Double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }
}
