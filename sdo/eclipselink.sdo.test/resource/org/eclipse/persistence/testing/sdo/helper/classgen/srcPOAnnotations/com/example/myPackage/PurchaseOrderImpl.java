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

import org.eclipse.persistence.sdo.SDODataObject;

public class PurchaseOrderImpl extends SDODataObject implements PurchaseOrder {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 5;

   public PurchaseOrderImpl() {}

   public java.util.List getShipTo() {
      return getList(START_PROPERTY_INDEX + 0);
   }

   public void setShipTo(java.util.List value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public com.example.myPackage.USAddress getBillToSDO() {
      return (com.example.myPackage.USAddress)get(START_PROPERTY_INDEX + 1);
   }

   public void setBillToSDO(com.example.myPackage.USAddress value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public com.example.myPackage.Items getItems() {
      return (com.example.myPackage.Items)get(START_PROPERTY_INDEX + 2);
   }

   public void setItems(com.example.myPackage.Items value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public com.example.myPackage.ItemSDO getTopPriorityItems() {
      return (com.example.myPackage.ItemSDO)get(START_PROPERTY_INDEX + 3);
   }

   public void setTopPriorityItems(com.example.myPackage.ItemSDO value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.lang.String getComment() {
      return getString(START_PROPERTY_INDEX + 4);
   }

   public void setComment(java.lang.String value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.sql.Time getOrderDate() {
      return (java.sql.Time)get(START_PROPERTY_INDEX + 5);
   }

   public void setOrderDate(java.sql.Time value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }


}

