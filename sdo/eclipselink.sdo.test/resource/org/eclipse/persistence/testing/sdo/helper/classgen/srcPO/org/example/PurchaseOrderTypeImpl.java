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

public class PurchaseOrderTypeImpl extends SDODataObject implements PurchaseOrderType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 5;

   public PurchaseOrderTypeImpl() {}

   public java.lang.String getPoId() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setPoId(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public org.example.AddressType getShipTo() {
      return (org.example.AddressType)get(START_PROPERTY_INDEX + 1);
   }

   public void setShipTo(org.example.AddressType value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public org.example.AddressType getBillTo() {
      return (org.example.AddressType)get(START_PROPERTY_INDEX + 2);
   }

   public void setBillTo(org.example.AddressType value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.lang.String getComment() {
      return getString(START_PROPERTY_INDEX + 3);
   }

   public void setComment(java.lang.String value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public org.example.Items getItems() {
      return (org.example.Items)get(START_PROPERTY_INDEX + 4);
   }

   public void setItems(org.example.Items value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.lang.String getOrderDate() {
      return getString(START_PROPERTY_INDEX + 5);
   }

   public void setOrderDate(java.lang.String value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }


}

