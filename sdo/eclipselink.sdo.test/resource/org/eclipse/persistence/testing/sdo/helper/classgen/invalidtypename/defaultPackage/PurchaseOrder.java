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

   public java.lang.String getPoID();

   public void setPoID(java.lang.String value);

   public defaultPackage.USAddress getShipTo();

   public void setShipTo(defaultPackage.USAddress value);

   public defaultPackage.USAddress getBillTo();

   public void setBillTo(defaultPackage.USAddress value);

   public java.util.List getComment();

   public void setComment(java.util.List value);

   public java.util.List getItem();

   public void setItem(java.util.List value);

   public java.lang.String getOrderDate();

   public void setOrderDate(java.lang.String value);


}

