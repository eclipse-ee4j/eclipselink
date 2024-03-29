/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.onetoone.keyontarget;

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
      return "Large Project id:" + this.getId()+" " +this.getName() + " " +this.getType() + " "+ this.getLeader() + " " + "Budget: " + getBudget();
    }


    public boolean equals(Object object)
    {
      boolean sofar = super.equals(object);
      if(!sofar)
        return false;

      if(!(object instanceof LargeProject projectObject))
        return false;

        if(this.getBudget() == projectObject.getBudget())
          return true;

      return false;

   }

}
