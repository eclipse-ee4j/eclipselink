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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import javax.persistence.*;

/**
 * Local interface for the large project bean.
 * This is the bean's public/local interface for the clients usage.
 * All locals must extend the javax.ejb.EJBLocalObject.
 * The bean itself does not have to implement the local interface, but must implement all of the methods.
 */
@Entity(name="LargeProject")
@Table(name="CMP3_FA_LPROJECT")
@DiscriminatorValue("L")
@NamedQueries({
@NamedQuery(
	name="findFieldAccessWithBudgetLargerThan",
	query="SELECT OBJECT(project) FROM LargeProject project WHERE project.budget >= :amount"
),
@NamedQuery(
	name="constructFieldAccessLProject",
	query="SELECT new org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.LargeProject(project.name) FROM LargeProject project")
}
)
public class LargeProject extends Project {
    private double budget;
    
    public LargeProject () {
        super();
        fieldOnlySetThroughConstructor = 2;
    }
    
    public LargeProject (String name) {
        this();
        this.setName(name);
    }

    public double getBudget() { 
        return budget; 
    }
    
    public void setBudget(double budget) { 
            this.budget = budget; 
    }
    
    /**
     * This tests accessing a superclass field.
     */
    public Employee getTeamLeader() {
        return teamLeader; 
    }
}
