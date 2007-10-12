/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyontarget;

public class LargeProject extends Project{
	private float budget;
    	
    public LargeProject()
    {
      super();          
    }
    public float getBudget(){
	    return budget;
    }

	public void setBudget(float newBudget){
	    budget = budget;
    }
    
    public String toString()
    {
      return "Large Project: " + this.hashCode() + " id:"+ this.getId()+" " +this.getName() + " " +this.getType() + " "+ this.getLeader() + " " + "Budget: " + getBudget();
    } 
    
    
    public boolean equals(Object object)
    {
      boolean sofar = super.equals(object);
      if(!sofar)
        return false;
      
      if(!(object instanceof LargeProject))
        return false;
        
	    LargeProject projectObject = (LargeProject)object;
      if(this.getBudget() == projectObject.getBudget())
          return true;
          
      return false;
      
   }
   
}
