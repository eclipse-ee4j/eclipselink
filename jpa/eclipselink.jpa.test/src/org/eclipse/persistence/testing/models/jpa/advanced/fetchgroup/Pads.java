/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.FetchAttribute;
import org.eclipse.persistence.annotations.FetchGroup;
import org.eclipse.persistence.annotations.FetchGroups;

@Entity
@Table(name="JPA_PADS")
@DiscriminatorValue("PAD")
@FetchGroups({
    @FetchGroup(name="HeightAndWidth", 
        attributes={
            @FetchAttribute(name="height"), 
            @FetchAttribute(name="width")}),
    @FetchGroup(name="Weight", attributes={@FetchAttribute(name="weight")})
})
public class Pads extends GoalieGear {
    public Double weight;
    public Double height;
    public Double width;

    public Double getHeight() {
        return height;
    }

    public Double getWeight() {
        return weight;
    }
    
    public Double getWidth() {
        return width;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public void setWidth(Double width) {
        this.width = width;
    }
}
