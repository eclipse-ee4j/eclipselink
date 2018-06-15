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

public class Sub4Impl extends org.example.Sub3Impl implements Sub4 {

   public static final int START_PROPERTY_INDEX = org.example.Sub3Impl.END_PROPERTY_INDEX + 1;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 3;

   public Sub4Impl() {}

   public java.lang.String getSub4Elem() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setSub4Elem(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getSub4Elem2() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setSub4Elem2(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public java.util.List getSub4Elem3() {
      return getList(START_PROPERTY_INDEX + 2);
   }

   public void setSub4Elem3(java.util.List value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public int getSub4Attr() {
      return getInt(START_PROPERTY_INDEX + 3);
   }

   public void setSub4Attr(int value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }


}

