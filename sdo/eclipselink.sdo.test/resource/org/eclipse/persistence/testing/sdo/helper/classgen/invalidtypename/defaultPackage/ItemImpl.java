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

public class ItemImpl extends SDODataObject implements Item {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public ItemImpl() {}

   public java.lang.String getItemID() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setItemID(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getName() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

