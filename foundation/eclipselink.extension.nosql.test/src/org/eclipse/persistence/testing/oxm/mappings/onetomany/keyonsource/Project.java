/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource;

public class Project {
    private long id;
    private String name;
    private String type;
    private String description;

    public Project()
    {
        this.name = "";
      this.type = "";
        this.description = "";
    }
    public String getDescription(){
        return description;
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

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public void setId(long id)
    {
        this.id = id;
    }

    public String toString()
    {
      return "Project: " + this.hashCode() + " id:"+ this.getId()+" " +this.getName() + " " +this.getType() + " "+ this.getDescription() + " ";
    }


    public boolean equals(Object object)
    {
     if(!(object instanceof Project))
      return false;

     Project projectObject = (Project)object;
     if( (this.getId() == projectObject.getId()) &&
         (this.getDescription().equals(projectObject.getDescription())) &&
         (this.getName().equals(projectObject.getName())) &&
         (this.getType().equals(projectObject.getType())) )
          return true;

      return false;

   }

}
