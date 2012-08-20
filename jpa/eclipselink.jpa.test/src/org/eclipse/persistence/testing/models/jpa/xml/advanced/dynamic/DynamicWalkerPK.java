/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/20/2012-2.4 Guy Pelletier 
 *       - 381079: EclipseLink dynamic entity does not support embedded-id
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic;

public class DynamicWalkerPK {
    protected Integer id;
    protected String style;
    
    public DynamicWalkerPK() {}
    
    public DynamicWalkerPK(Integer id, String style) {
        this.id = id;
        this.style = style;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
