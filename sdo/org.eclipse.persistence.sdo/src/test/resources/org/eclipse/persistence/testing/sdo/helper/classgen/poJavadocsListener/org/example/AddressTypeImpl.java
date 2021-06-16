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

//this is the preImplPackage event
package org.example;

//this is the preImplImports event
import org.eclipse.persistence.sdo.SDODataObject;

/**
 * This is documentation for the complextype called AddressType
 */
//this is the preImplClass event
public class AddressTypeImpl extends SDODataObject implements AddressType {

//this is the preImplAttributes event
   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 5;

   public AddressTypeImpl() {}

   /**
    * Gets name.
    * return This is documentation for the name element inside the complextype called AddressType
    */
   public java.lang.String getName() {
      return getString(START_PROPERTY_INDEX + 0);
   }

   /**
    * Sets name.
    * param value This is documentation for the name element inside the complextype called AddressType
    */
   public void setName(java.lang.String value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getStreet() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setStreet(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public java.lang.String getCity() {
      return getString(START_PROPERTY_INDEX + 2);
   }

   public void setCity(java.lang.String value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.lang.String getState() {
      return getString(START_PROPERTY_INDEX + 3);
   }

   public void setState(java.lang.String value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.lang.String getZip() {
      return getString(START_PROPERTY_INDEX + 4);
   }

   public void setZip(java.lang.String value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.lang.String getCountry() {
      return getString(START_PROPERTY_INDEX + 5);
   }

   public void setCountry(java.lang.String value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }


}

