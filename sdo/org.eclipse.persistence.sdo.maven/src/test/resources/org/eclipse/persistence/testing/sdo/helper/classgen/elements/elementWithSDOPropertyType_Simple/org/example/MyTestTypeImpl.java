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

public class MyTestTypeImpl extends SDODataObject implements MyTestType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public MyTestTypeImpl() {}

   public org.example.P_TYPE getMyTest1() {
      return (org.example.P_TYPE)get(START_PROPERTY_INDEX + 0);
   }

   public void setMyTest1(org.example.P_TYPE value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

