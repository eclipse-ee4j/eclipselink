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

public class TestTypeImpl extends SDODataObject implements TestType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 1;

   public TestTypeImpl() {}

   public java.lang.String getValue() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setValue(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public int getTester() {
      return getInt(START_PROPERTY_INDEX + 1);
   }

   public void setTester(int value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }


}

