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

public interface Company extends java.io.Serializable {

   public org.example.InvalidClassname getInvalidClassname();

   public void setInvalidClassname(org.example.InvalidClassname value);

   public org.example.InvalidClassname1 getInvalidClassname1();

   public void setInvalidClassname1(org.example.InvalidClassname1 value);

   public java.util.List getPorder();

   public void setPorder(java.util.List value);

   public java.util.List getItem();

   public void setItem(java.util.List value);

   public java.lang.String getName();

   public void setName(java.lang.String value);


}

