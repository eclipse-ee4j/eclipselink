/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package com.example.myPackage;

public interface ItemSDO extends java.io.Serializable {

   public java.lang.String getPorder();

   public void setPorder(java.lang.String value);

   public java.lang.String getProductName();

   public void setProductName(java.lang.String value);

   public java.math.BigInteger getQuantity();

   public void setQuantity(java.math.BigInteger value);

   public com.example.myPackage.SKU getPartNumSDO();

   public void setPartNumSDO(com.example.myPackage.SKU value);

   public java.math.BigDecimal getUSPrice();

   public void setUSPrice(java.math.BigDecimal value);

   public java.lang.String getComment();

   public void setComment(java.lang.String value);

   public java.lang.String getShipDate();

   public void setShipDate(java.lang.String value);


}

