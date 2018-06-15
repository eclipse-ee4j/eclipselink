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

public interface PurchaseOrder extends java.io.Serializable {

   public java.lang.String getPoID();

   public void setPoID(java.lang.String value);

   public org.example.USAddress getShipTo();

   public void setShipTo(org.example.USAddress value);

   public org.example.USAddress getBillTo();

   public void setBillTo(org.example.USAddress value);

   public java.util.List getComment();

   public void setComment(java.util.List value);

   public java.util.List getItem();

   public void setItem(java.util.List value);

   public java.lang.String getOrderDate();

   public void setOrderDate(java.lang.String value);


}

