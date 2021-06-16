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

//this is the preInterfacePackage event
package org.example;

//this is the preInterfaceImports event
//this is the preInterfaceClass event
public interface LineItemType extends java.io.Serializable {

   public java.lang.String getProductName();

   public void setProductName(java.lang.String value);

   public java.lang.String getProductSize();

   public void setProductSize(java.lang.String value);

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

