/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package com.example.myPackage;

public interface PurchaseOrder extends java.io.Serializable {

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

