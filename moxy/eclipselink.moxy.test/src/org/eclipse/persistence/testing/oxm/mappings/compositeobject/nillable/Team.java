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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;

public class Team {
    public static final int DEFAULT_ID = 123;
    
    // Factory method
    public static Team getInstance() {
        return new Team(DEFAULT_ID, new Employee(), "Eng");
    }
    
    private int id;
    private Employee manager;
    private String name;

    private boolean isSetManager = false;

    public Team() {
        super();        
    }

    public Team(int id) {
        super();
        this.id = id;
    }

    public Team(int id, Employee manager, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    // override default equals
    public boolean equals(Object object) {
      try {
          Team team = (Team) object;
          if(this.getId() != team.getId()) {
              return false;
          }
          if(this.isSetManager() != team.isSetManager()) {
              return false;
          }
          if(this.getManager() != team.getManager()) {
            if(this.getManager() == null) {
                return false;
            }
            if(!this.getManager().equals(team.getManager())) {
                return false;
            }
          }
          if(this.getName() != team.getName()) {
            if(this.getName() == null) {
                return false;
            }
            if(!this.getName().equals(team.getName())) {
                return false;
            }
          }
          return true;
      } catch(ClassCastException e) {
          return false;
      }
    }

    public int getId() {
        return id;
    }

    public Employee getManager() {
		return manager;
	}

    public String getName() {
        return name;
    }

    public boolean isSetManager() {
        return isSetManager;
    }

    public void setId(int id) {
        this.id = id;
    } 

    public void setManager(Employee manager) {
	    // no unset for now
	    isSetManager = true;
		this.manager = manager;
	}

	public void setName(String name) {
        this.name = name;
    }

    public String toString() {
    	StringBuffer aBuffer = new StringBuffer();
    	aBuffer.append("Team(id=");
    	aBuffer.append(getId());
    	aBuffer.append(", manager=");
        aBuffer.append(getManager());
        aBuffer.append(", isSetManager=");
        aBuffer.append(isSetManager());
        aBuffer.append(", name=");
        aBuffer.append(getName());
        aBuffer.append(")");
        return aBuffer.toString();
    }

}

