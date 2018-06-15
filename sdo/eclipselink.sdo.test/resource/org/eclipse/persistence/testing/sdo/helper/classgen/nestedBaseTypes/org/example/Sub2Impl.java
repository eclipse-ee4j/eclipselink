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

public class Sub2Impl extends org.example.Sub1Impl implements Sub2 {

   public static final int START_PROPERTY_INDEX = org.example.Sub1Impl.END_PROPERTY_INDEX + 1;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public Sub2Impl() {}

   public java.lang.String getSub2Elem() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setSub2Elem(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public int getSub2Attr() {
      return getInt(START_PROPERTY_INDEX + 1);
   }

   public void setSub2Attr(int value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

