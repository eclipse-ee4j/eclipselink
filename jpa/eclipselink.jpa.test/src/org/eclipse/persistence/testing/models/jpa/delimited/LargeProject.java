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
 *     tware - add for testing JPA 2.0 delimited identifiers
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.delimited;

import javax.persistence.*;

import org.eclipse.persistence.annotations.ExistenceChecking;

import static org.eclipse.persistence.annotations.ExistenceType.ASSUME_NON_EXISTENCE;

/**
 * LargeProject subclass of Project.
 * This class in used to test inheritance.
 * The field names intentionally do not match the property names to test method weaving.
 */
@Entity
@Table(name="CMP3_DEL_LPROJECT")
@DiscriminatorValue("L")
@NamedQueries({
@NamedQuery(
	name="findWithBudgetLargerThan",
	query="SELECT OBJECT(project) FROM LargeProject project WHERE project.budget >= :amount"
),
@NamedQuery(
	name="constructLProject",
	query="SELECT new org.eclipse.persistence.testing.models.jpa.delimited.LargeProject(project.name) FROM LargeProject project")
}
)
@ExistenceChecking(ASSUME_NON_EXISTENCE)
public class LargeProject extends Project {
    private double m_budget;
    
    public LargeProject() {
        super();
    }
    
    public LargeProject(String name) {
        this();
        this.setName(name);
    }

    public double getBudget() { 
        return m_budget; 
    }
    
    public void setBudget(double budget) { 
        this.m_budget = budget; 
    }
    
    /**
     * This tests over-writing a get method.
     */
    public Employee getTeamLeader() {
        return m_teamLeader; 
    }
}
