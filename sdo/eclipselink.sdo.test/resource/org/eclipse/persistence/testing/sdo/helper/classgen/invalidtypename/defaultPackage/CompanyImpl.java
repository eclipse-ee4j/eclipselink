/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package defaultPackage;

import org.eclipse.persistence.sdo.SDODataObject;

public class CompanyImpl extends SDODataObject implements Company {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 4;

   public CompanyImpl() {}

   public defaultPackage.InvalidClassname getInvalidClassname() {
      return (defaultPackage.InvalidClassname)get(START_PROPERTY_INDEX + 0);
   }

   public void setInvalidClassname(defaultPackage.InvalidClassname value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public defaultPackage.InvalidClassname1 getInvalidClassname1() {
      return (defaultPackage.InvalidClassname1)get(START_PROPERTY_INDEX + 1);
   }

   public void setInvalidClassname1(defaultPackage.InvalidClassname1 value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public java.util.List getPorder() {
      return getList(START_PROPERTY_INDEX + 2);
   }

   public void setPorder(java.util.List value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.util.List getItem() {
      return getList(START_PROPERTY_INDEX + 3);
   }

   public void setItem(java.util.List value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.lang.String getName() {
      return getString(START_PROPERTY_INDEX + 4);
   }

   public void setName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }


}

