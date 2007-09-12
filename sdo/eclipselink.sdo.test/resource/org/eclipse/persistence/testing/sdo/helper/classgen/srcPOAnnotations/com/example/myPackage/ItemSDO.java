/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package com.example.myPackage;

public interface ItemSDO {

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

