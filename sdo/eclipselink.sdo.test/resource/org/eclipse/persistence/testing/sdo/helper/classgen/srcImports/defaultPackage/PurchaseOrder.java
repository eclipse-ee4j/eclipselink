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

public interface PurchaseOrder {

   public defaultPackage.USAddress getShipTo();

   public void setShipTo(defaultPackage.USAddress value);

   public defaultPackage.USAddress getBillTo();

   public void setBillTo(defaultPackage.USAddress value);

   public int getQuantity();

   public void setQuantity(int value);

   public java.lang.String getPartNum();

   public void setPartNum(java.lang.String value);


}

