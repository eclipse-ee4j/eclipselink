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

public class InvalidClassnameImpl extends SDODataObject implements InvalidClassname {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 2;

   public InvalidClassnameImpl() {}

   public java.util.List getEmail() {
      return getList(START_PROPERTY_INDEX + 0);
   }

   public void setEmail(java.util.List value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.util.List getPhone() {
      return getList(START_PROPERTY_INDEX + 1);
   }

   public void setPhone(java.util.List value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public java.lang.String getCustID() {
      return getString(START_PROPERTY_INDEX + 2);
   }

   public void setCustID(java.lang.String value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }


}

