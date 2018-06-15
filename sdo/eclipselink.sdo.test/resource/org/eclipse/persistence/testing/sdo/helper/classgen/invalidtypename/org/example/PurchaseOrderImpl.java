/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class PurchaseOrderImpl extends SDODataObject implements PurchaseOrder {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 5;

   public PurchaseOrderImpl() {}

   public java.lang.String getPoID() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setPoID(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public org.example.USAddress getShipTo() {
      return (org.example.USAddress)get(START_PROPERTY_INDEX + 1);
   }

   public void setShipTo(org.example.USAddress value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public org.example.USAddress getBillTo() {
      return (org.example.USAddress)get(START_PROPERTY_INDEX + 2);
   }

   public void setBillTo(org.example.USAddress value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.util.List getComment() {
      return getList(START_PROPERTY_INDEX + 3);
   }

   public void setComment(java.util.List value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.util.List getItem() {
      return getList(START_PROPERTY_INDEX + 4);
   }

   public void setItem(java.util.List value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.lang.String getOrderDate() {
      return getString(START_PROPERTY_INDEX + 5);
   }

   public void setOrderDate(java.lang.String value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }


}

