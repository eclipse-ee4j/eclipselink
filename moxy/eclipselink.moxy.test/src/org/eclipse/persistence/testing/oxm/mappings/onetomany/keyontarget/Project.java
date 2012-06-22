/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyontarget;

public class Project {
	private long id;
	private String name;
	private String type;
  private Employee leader;
	    	
    public Project()
    {
	    this.name = "";
      this.type ="";
	
    }
    
    public String getType(){
	    return type;
    }
   
    public long getId()
    {
	    return id;
    }
    public String getName()
    {
	    return name;
    }
 
    public Employee getLeader()
    {
	    return leader;
    }
 
    public void setType(String type)
    {
	    this.type = type;
    }
    
    public void setName(String name)
    {
	    this.name = name;
    }
    public void setLeader(Employee leader)
    {
	    this.leader = leader;
    }
    
    public void setId(long id)
    {
	    this.id = id;
    }
    
    public String toString()
    {
      return "Project: " + this.hashCode() + " id:"+ this.getId()+" " +this.getName() + " " +this.getType();// +  " " + this.getLeader();
    } 
    
    
    public boolean equals(Object object)
    {
     if(!(object instanceof Project))
      return false;
      
     Project projectObject = (Project)object;
		 		 
     if( (this.getId() == projectObject.getId()) &&(this.getName().equals(projectObject.getName()))){ 
         if( ((this.getLeader() == null) && projectObject.getLeader() ==null) || (this.getLeader().equals(projectObject.getLeader())) ){						
					  if( (this.getType() == null && projectObject.getType() == null) ||(this.getType().equals(projectObject.getType()))	){
							return true;	
						}	
				 }
		 }
      return false;
      
   }
   
}
