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

