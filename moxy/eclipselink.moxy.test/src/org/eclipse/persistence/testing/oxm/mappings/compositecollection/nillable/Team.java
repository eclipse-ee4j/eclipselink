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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.nillable;

import java.util.Vector;

public class Team {
    public static final int DEFAULT_ID = 123;

    // Factory method
    public static Team getInstance() {
        return new Team(DEFAULT_ID, new Employee(), "Eng");
    }

    private int id;
    private Vector developers;// type Employee
    private String name;

    // we need to handle vector=null and vector=empty
    private boolean isSetDevelopers = false;

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
        if (!(object instanceof Team)) {
            return false;
        }
        Team teamObject = (Team)object;

        //      if((this.getId() == teamObject.getId()) && //
        //        ((this.getDevelopers()==null && teamObject.getDevelopers()==null) || //
        //        		((this.getDevelopers()!=null && teamObject.getDevelopers()!=null) && //
        //        				this.getDevelopers().equals(teamObject.getDevelopers())))) {
        //            return true;
        //      }
        if ((teamObject.getDevelopers() == null) && (getDevelopers() != null)) {
            return false;
        }
        if ((teamObject.getDevelopers() != null) && (getDevelopers() == null)) {
            return false;
        }
        if ((teamObject.getDevelopers() != null) && (getDevelopers() != null) &&//
                (!(teamObject.getDevelopers() instanceof Vector) || !(getDevelopers() instanceof Vector))) {
            return false;
        }

        if ((this.getId() == teamObject.getId()) &&//
                (((this.getDevelopers() == null) && (teamObject.getDevelopers() == null)) ||//
                (this.getDevelopers().isEmpty() && teamObject.getDevelopers().isEmpty()) ||//
                (this.getDevelopers().containsAll(teamObject.getDevelopers()))) &&//
                (this.getDevelopers().size() == teamObject.getDevelopers().size())) {
            return true;
        }

        // todo handle :

        /*Expected:
            Employee(123,[null, write code, null],Doe)
            Actual:
            Employee(123,[write code],Doe)
        */
        return false;
    }

    public int getId() {
        return id;
    }

    public Vector getDevelopers() {
        return developers;
    }

    public String getName() {
        return name;
    }

    public boolean isSetDevelopers() {
        return isSetDevelopers;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDevelopers(Vector developers) {
        this.developers = developers;
        isSetDevelopers = true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append("Team(");
        aBuffer.append(getId());
        aBuffer.append(",");
        if (getDevelopers() != null) {
            aBuffer.append(getDevelopers().toString());
            aBuffer.append(",");
        }
        aBuffer.append(name);
        aBuffer.append(")");
        return aBuffer.toString();
    }
}
