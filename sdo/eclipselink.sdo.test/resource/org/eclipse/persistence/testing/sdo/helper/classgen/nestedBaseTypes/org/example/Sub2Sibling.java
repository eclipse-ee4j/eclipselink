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

public interface Sub2Sibling extends org.example.Sub1 {

   public java.lang.String getSub2SiblingElem();

   public void setSub2SiblingElem(java.lang.String value);

   public java.lang.String getSub2SiblingElem2();

   public void setSub2SiblingElem2(java.lang.String value);

   public int getSub2SiblingAttr();

   public void setSub2SiblingAttr(int value);


}

