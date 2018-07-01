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

public interface LineItemType extends java.io.Serializable {

   public java.lang.String getProductName();

   public void setProductName(java.lang.String value);

   public int getQuantity();

   public void setQuantity(int value);

   public float getUSPrice();

   public void setUSPrice(float value);

   public java.lang.String getShipDate();

   public void setShipDate(java.lang.String value);

   public java.lang.String getComment();

   public void setComment(java.lang.String value);

   public java.lang.String getPartNum();

   public void setPartNum(java.lang.String value);


}

