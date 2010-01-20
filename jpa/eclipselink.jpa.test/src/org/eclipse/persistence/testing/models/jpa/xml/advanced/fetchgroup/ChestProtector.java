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
 *     01/19/2010-2.1 Guy Pelletier 
 *       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Table;

@Table(name="XML_CHEST_PROTECT")
@DiscriminatorValue("CHP")
public class ChestProtector extends GoalieGear {
    @Column(name="C_SIZE")
    public String size;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
