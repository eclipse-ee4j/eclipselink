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

public interface Item extends java.io.Serializable {

   public java.lang.String getItemID();

   public void setItemID(java.lang.String value);

   public java.lang.String getName();

   public void setName(java.lang.String value);


}

