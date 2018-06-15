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

public class MyTestTypeImpl extends SDODataObject implements MyTestType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public MyTestTypeImpl() {}

   public java.lang.String getMyTest1() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setMyTest1(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.math.BigInteger getMyTest2() {
      return getBigInteger(START_PROPERTY_INDEX + 1);
   }

   public void setMyTest2(java.math.BigInteger value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

