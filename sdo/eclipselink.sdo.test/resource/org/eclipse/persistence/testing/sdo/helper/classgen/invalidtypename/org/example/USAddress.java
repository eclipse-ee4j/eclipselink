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

public interface USAddress extends java.io.Serializable {

   public java.lang.String getName();

   public void setName(java.lang.String value);

   public java.lang.String getStreet();

   public void setStreet(java.lang.String value);

   public java.lang.String getCity();

   public void setCity(java.lang.String value);

   public java.lang.String getState();

   public void setState(java.lang.String value);

   public java.math.BigDecimal getZip();

   public void setZip(java.math.BigDecimal value);

   public java.lang.String getCountry();

   public void setCountry(java.lang.String value);


}

