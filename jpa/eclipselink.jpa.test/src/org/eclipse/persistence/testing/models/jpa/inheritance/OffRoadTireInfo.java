/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Table;

import static javax.persistence.InheritanceType.JOINED;

@Entity
@Table(name="CMP3_OFFROAD_TIRE")
@Inheritance(strategy=JOINED)
@DiscriminatorValue("Offroad")
public class OffRoadTireInfo extends TireInfo {
    protected String code;
    protected String name;

    public OffRoadTireInfo() {}

    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
