/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package uri2.my;

import org.eclipse.persistence.sdo.SDODataObject;

public class USAddressImpl extends SDODataObject implements USAddress {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public USAddressImpl() {}

   public java.lang.String getStreet() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setStreet(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getCity() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setCity(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

