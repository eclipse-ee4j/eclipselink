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

public class Sub1Impl extends defaultPackage.RootImpl implements Sub1 {

   public static final int START_PROPERTY_INDEX = defaultPackage.RootImpl.END_PROPERTY_INDEX + 1;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public Sub1Impl() {}

   public java.lang.String getSub1Elem() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setSub1Elem(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public int getSub1Attr() {
      return getInt(START_PROPERTY_INDEX + 1);
   }

   public void setSub1Attr(int value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

