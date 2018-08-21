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

public interface PurchaseOrderType extends java.io.Serializable {

   public java.lang.String getTheID();

   public void setTheID(java.lang.String value);

   public org.example.AddressType getShipTo();

   public void setShipTo(org.example.AddressType value);

   public org.example.AddressType getBillTo();

   public void setBillTo(org.example.AddressType value);

   public java.lang.String getComment();

   public void setComment(java.lang.String value);

   public org.example.Items getItems();

   public void setItems(org.example.Items value);

   public java.lang.String getOrderDate();

   public void setOrderDate(java.lang.String value);


}

