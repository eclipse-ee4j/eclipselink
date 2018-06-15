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

public class UsAddressTypeImpl extends org.example.AddressTypeImpl implements UsAddressType {

   public static final int START_PROPERTY_INDEX = org.example.AddressTypeImpl.END_PROPERTY_INDEX + 1;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public UsAddressTypeImpl() {}

   public int getZip() {
      return getInt(START_PROPERTY_INDEX + 0);
   }

   public void setZip(int value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

