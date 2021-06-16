/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

