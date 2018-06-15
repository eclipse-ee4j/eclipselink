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

public class CdnAddressTypeImpl extends org.example.AddressTypeImpl implements CdnAddressType {

   public static final int START_PROPERTY_INDEX = org.example.AddressTypeImpl.END_PROPERTY_INDEX + 1;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public CdnAddressTypeImpl() {}

   public java.lang.String getPostalcode() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setPostalcode(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

