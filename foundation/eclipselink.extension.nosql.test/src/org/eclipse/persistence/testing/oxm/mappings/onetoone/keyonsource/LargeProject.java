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
package org.eclipse.persistence.testing.oxm.mappings.onetoone.keyonsource;

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
        budget = newBudget;
    }

    public String toString()
    {
      return "Large Project id:"+ this.getId()+" " +this.getName() + " " +this.getType() + " "+ this.getDescription() + " " + "Budget: " + getBudget();
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
