/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package com.example.myPackage;

public interface PurchaseOrder {

   public java.util.List getShipTo();

   public void setShipTo(java.util.List value);

   public com.example.myPackage.USAddress getBillToSDO();

   public void setBillToSDO(com.example.myPackage.USAddress value);

   public com.example.myPackage.Items getItems();

   public void setItems(com.example.myPackage.Items value);

   public com.example.myPackage.ItemSDO getTopPriorityItems();

   public void setTopPriorityItems(com.example.myPackage.ItemSDO value);

   public java.lang.String getComment();

   public void setComment(java.lang.String value);

   public java.sql.Time getOrderDate();

   public void setOrderDate(java.sql.Time value);


}

