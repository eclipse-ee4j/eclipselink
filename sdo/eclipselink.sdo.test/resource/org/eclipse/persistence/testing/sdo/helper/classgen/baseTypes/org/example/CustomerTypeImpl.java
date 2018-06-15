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

public class CustomerTypeImpl extends SDODataObject implements CustomerType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 4;

   public CustomerTypeImpl() {}

   public java.lang.String getFirstName() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setFirstName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getLastName() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setLastName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public org.example.AddressType getAddress() {
      return (org.example.AddressType)get(START_PROPERTY_INDEX + 2);
   }

   public void setAddress(org.example.AddressType value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public int getCustomerID() {
      return getInt(START_PROPERTY_INDEX + 3);
   }

   public void setCustomerID(int value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.lang.String getSin() {
      return getString(START_PROPERTY_INDEX + 4);
   }

   public void setSin(java.lang.String value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }


}

