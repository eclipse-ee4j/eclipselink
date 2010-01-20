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
package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.FetchAttribute;
import org.eclipse.persistence.annotations.FetchGroup;

@MappedSuperclass
@FetchGroup(name="AgeGroup", attributes={@FetchAttribute(name="ageGroup")})
public class GoalieGear extends HockeyGear {
    public enum AgeGroup { YOUTH, JUNIOR, INTERMEDIATE, SENIOR }
    
    public AgeGroup ageGroup;
    
    public GoalieGear() {}
    
    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }
}
