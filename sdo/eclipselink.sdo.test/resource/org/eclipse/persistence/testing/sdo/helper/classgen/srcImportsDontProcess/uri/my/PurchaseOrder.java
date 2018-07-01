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

package uri.my;

public interface PurchaseOrder extends java.io.Serializable {

   public uri2.my.USAddress getShipTo();

   public void setShipTo(uri2.my.USAddress value);

   public uri2.my.USAddress getBillTo();

   public void setBillTo(uri2.my.USAddress value);

   public int getQuantity();

   public void setQuantity(int value);

   public java.lang.String getPartNum();

   public void setPartNum(java.lang.String value);


}

