/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     bdoughan - Mar 17/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.classgen.dynamicimpl;

public interface DataTypes {

   java.lang.Object getAnySimpleTypeProperty();

   void setAnySimpleTypeProperty(java.lang.Object value);

   commonj.sdo.DataObject getAnyTypeProperty();

   void setAnyTypeProperty(commonj.sdo.DataObject value);

   java.lang.String getAnyURIProperty();

   void setAnyURIProperty(java.lang.String value);

   byte[] getBase64BinaryProperty();

   void setBase64BinaryProperty(byte[] value);

   boolean isBooleanProperty();

   void setBooleanProperty(boolean value);

   byte getByteProperty();

   void setByteProperty(byte value);

   java.lang.String getDateProperty();

   void setDateProperty(java.lang.String value);

   java.lang.String getDateTimeProperty();

   void setDateTimeProperty(java.lang.String value);

   java.math.BigDecimal getDecimalProperty();

   void setDecimalProperty(java.math.BigDecimal value);

   double getDoubleProperty();

   void setDoubleProperty(double value);

   java.lang.String getDurationProperty();

   void setDurationProperty(java.lang.String value);

   java.util.List getENTITIESProperty();

   void setENTITIESProperty(java.util.List value);

   java.lang.String getENTITYProperty();

   void setENTITYProperty(java.lang.String value);

   float getFloatProperty();

   void setFloatProperty(float value);

   java.lang.String getGDayProperty();

   void setGDayProperty(java.lang.String value);

   java.lang.String getGMonthProperty();

   void setGMonthProperty(java.lang.String value);

   java.lang.String getGMonthDayProperty();

   void setGMonthDayProperty(java.lang.String value);

   java.lang.String getGYearProperty();

   void setGYearProperty(java.lang.String value);

   java.lang.String getGYearMonthProperty();

   void setGYearMonthProperty(java.lang.String value);

   byte[] getHexBinaryProperty();

   void setHexBinaryProperty(byte[] value);

   java.lang.String getIDProperty();

   void setIDProperty(java.lang.String value);

   java.lang.String getIDREFProperty();

   void setIDREFProperty(java.lang.String value);

   int getIntProperty();

   void setIntProperty(int value);

   java.math.BigInteger getIntegerProperty();

   void setIntegerProperty(java.math.BigInteger value);

   java.lang.String getLanguageProperty();

   void setLanguageProperty(java.lang.String value);

   long getLongProperty();

   void setLongProperty(long value);

   java.lang.String getNameProperty();

   void setNameProperty(java.lang.String value);

   java.lang.String getNCNameProperty();

   void setNCNameProperty(java.lang.String value);

   java.math.BigInteger getNegativeIntegerProperty();

   void setNegativeIntegerProperty(java.math.BigInteger value);

   java.lang.String getNMTOKENProperty();

   void setNMTOKENProperty(java.lang.String value);

   java.util.List getNMTOKENSProperty();

   void setNMTOKENSProperty(java.util.List value);

   java.math.BigInteger getNonNegativeIntegerProperty();

   void setNonNegativeIntegerProperty(java.math.BigInteger value);

   java.math.BigInteger getNonPositiveIntegerProperty();

   void setNonPositiveIntegerProperty(java.math.BigInteger value);

   java.lang.String getNormalizedStringProperty();

   void setNormalizedStringProperty(java.lang.String value);

   java.lang.String getNOTATION();

   void setNOTATION(java.lang.String value);

   java.math.BigInteger getPositiveIntegerProperty();

   void setPositiveIntegerProperty(java.math.BigInteger value);

   java.lang.String getQName();

   void setQName(java.lang.String value);

   short getShort();

   void setShort(short value);

   java.lang.String getString();

   void setString(java.lang.String value);

   java.lang.String getTime();

   void setTime(java.lang.String value);

   java.lang.String getToken();

   void setToken(java.lang.String value);

   short getUnsignedByte();

   void setUnsignedByte(short value);

   long getUnsignedInt();

   void setUnsignedInt(long value);

   java.math.BigInteger getUnsignedLong();

   void setUnsignedLong(java.math.BigInteger value);

   int getUnsignedShort();

   void setUnsignedShort(int value);


}
