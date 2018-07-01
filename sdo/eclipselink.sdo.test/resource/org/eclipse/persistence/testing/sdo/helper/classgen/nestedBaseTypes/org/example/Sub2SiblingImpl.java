/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class Sub2SiblingImpl extends org.example.Sub1Impl implements Sub2Sibling {

   public static final int START_PROPERTY_INDEX = org.example.Sub1Impl.END_PROPERTY_INDEX + 1;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 2;

   public Sub2SiblingImpl() {}

   public java.lang.String getSub2SiblingElem() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   public void setSub2SiblingElem(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getSub2SiblingElem2() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setSub2SiblingElem2(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public int getSub2SiblingAttr() {
      return getInt(START_PROPERTY_INDEX + 2);
   }

   public void setSub2SiblingAttr(int value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }


}

