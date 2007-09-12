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

public interface PurchaseOrderType {

   public java.lang.String getTheID();

   public void setTheID(java.lang.String value);

   public defaultPackage.AddressType getShipTo();

   public void setShipTo(defaultPackage.AddressType value);

   public defaultPackage.AddressType getBillTo();

   public void setBillTo(defaultPackage.AddressType value);

   public java.lang.String getComment();

   public void setComment(java.lang.String value);

   public defaultPackage.Items getItems();

   public void setItems(defaultPackage.Items value);

   public java.lang.String getOrderDate();

   public void setOrderDate(java.lang.String value);


}

