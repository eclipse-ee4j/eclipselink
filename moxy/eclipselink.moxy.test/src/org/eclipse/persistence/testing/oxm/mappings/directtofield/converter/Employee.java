/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - September 22 /2009
package org.eclipse.persistence.testing.oxm.mappings.directtofield.converter;

public class Employee
{
  public String firstName;
  public String lastName;
  public String gender;

  public boolean equals(Object obj){
      Employee empObj = (Employee)obj;
      if(!firstName.equals(empObj.firstName)){
          return false;
      }
      if(!lastName.equals(empObj.lastName)){
          return false;
      }

      if(gender == null){
          if(empObj.gender != null){
              return false;
          }
      } else if(!gender.equals(empObj.gender)){
          return false;
      }
      return true;
  }
}
