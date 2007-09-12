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

public class PurchaseOrderImpl extends SDODataObject implements PurchaseOrder {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 3;

   public PurchaseOrderImpl() {}

   public defaultPackage.USAddress getShipTo() {
      return (defaultPackage.USAddress)get(START_PROPERTY_INDEX + 0);
   }

   public void setShipTo(defaultPackage.USAddress value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public defaultPackage.USAddress getBillTo() {
      return (defaultPackage.USAddress)get(START_PROPERTY_INDEX + 1);
   }

   public void setBillTo(defaultPackage.USAddress value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public int getQuantity() {
      return getInt(START_PROPERTY_INDEX + 2);
   }

   public void setQuantity(int value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.lang.String getPartNum() {
      return getString(START_PROPERTY_INDEX + 3);
   }

   public void setPartNum(java.lang.String value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }


}

