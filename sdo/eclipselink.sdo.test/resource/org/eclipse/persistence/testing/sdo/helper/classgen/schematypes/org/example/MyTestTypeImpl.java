package org.example;

import org.eclipse.persistence.sdo.SDODataObject;

public class MyTestTypeImpl extends SDODataObject implements MyTestType {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 51;

   public MyTestTypeImpl() {}

   public java.lang.Object getMyAnySimpleTypeTest() {
      return get(START_PROPERTY_INDEX + 0);
   }

   public void setMyAnySimpleTypeTest(java.lang.Object value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.Object getMyAnySimpleTypeTest2() {
      return get(START_PROPERTY_INDEX + 1);
   }

   public void setMyAnySimpleTypeTest2(java.lang.Object value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public commonj.sdo.DataObject getMyAnyTypeTest() {
      return (commonj.sdo.DataObject)get(START_PROPERTY_INDEX + 2);
   }

   public void setMyAnyTypeTest(commonj.sdo.DataObject value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public commonj.sdo.DataObject getMyAnyTypeTest2() {
      return (commonj.sdo.DataObject)get(START_PROPERTY_INDEX + 3);
   }

   public void setMyAnyTypeTest2(commonj.sdo.DataObject value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.util.List getMyAnyTypeTest3() {
      return getList(START_PROPERTY_INDEX + 4);
   }

   public void setMyAnyTypeTest3(java.util.List value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.util.List getMyAnyTypeTest4() {
      return getList(START_PROPERTY_INDEX + 5);
   }

   public void setMyAnyTypeTest4(java.util.List value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }

   public java.lang.String getMyAnyURITest() {
      return getString(START_PROPERTY_INDEX + 6);
   }

   public void setMyAnyURITest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 6 , value);
   }

   public byte[] getMyBase64BinaryTest() {
      return getBytes(START_PROPERTY_INDEX + 7);
   }

   public void setMyBase64BinaryTest(byte[] value) {
      set(START_PROPERTY_INDEX + 7 , value);
   }

   public boolean isMyBooleanTest() {
      return getBoolean(START_PROPERTY_INDEX + 8);
   }

   public void setMyBooleanTest(boolean value) {
      set(START_PROPERTY_INDEX + 8 , value);
   }

   public byte getMyByteTest() {
      return getByte(START_PROPERTY_INDEX + 9);
   }

   public void setMyByteTest(byte value) {
      set(START_PROPERTY_INDEX + 9 , value);
   }

   public java.lang.String getMyDateTest() {
      return getString(START_PROPERTY_INDEX + 10);
   }

   public void setMyDateTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 10 , value);
   }

   public java.lang.String getMyDateTimeTest() {
      return getString(START_PROPERTY_INDEX + 11);
   }

   public void setMyDateTimeTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 11 , value);
   }

   public java.math.BigDecimal getMyDecimalTest() {
      return getBigDecimal(START_PROPERTY_INDEX + 12);
   }

   public void setMyDecimalTest(java.math.BigDecimal value) {
      set(START_PROPERTY_INDEX + 12 , value);
   }

   public double getMyDoubleTest() {
      return getDouble(START_PROPERTY_INDEX + 13);
   }

   public void setMyDoubleTest(double value) {
      set(START_PROPERTY_INDEX + 13 , value);
   }

   public java.lang.String getMyDurationTest() {
      return getString(START_PROPERTY_INDEX + 14);
   }

   public void setMyDurationTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 14 , value);
   }

   public java.util.List getMyENTITIESTest() {
      return getList(START_PROPERTY_INDEX + 15);
   }

   public void setMyENTITIESTest(java.util.List value) {
      set(START_PROPERTY_INDEX + 15 , value);
   }

   public java.lang.String getMyENTITYTest() {
      return getString(START_PROPERTY_INDEX + 16);
   }

   public void setMyENTITYTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 16 , value);
   }

   public float getMyFloatTest() {
      return getFloat(START_PROPERTY_INDEX + 17);
   }

   public void setMyFloatTest(float value) {
      set(START_PROPERTY_INDEX + 17 , value);
   }

   public java.lang.String getMyGDayTest() {
      return getString(START_PROPERTY_INDEX + 18);
   }

   public void setMyGDayTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 18 , value);
   }

   public java.lang.String getMyGMonthTest() {
      return getString(START_PROPERTY_INDEX + 19);
   }

   public void setMyGMonthTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 19 , value);
   }

   public java.lang.String getMyGMonthDayTest() {
      return getString(START_PROPERTY_INDEX + 20);
   }

   public void setMyGMonthDayTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 20 , value);
   }

   public java.lang.String getMyGYearTest() {
      return getString(START_PROPERTY_INDEX + 21);
   }

   public void setMyGYearTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 21 , value);
   }

   public java.lang.String getMyGYearMonthTest() {
      return getString(START_PROPERTY_INDEX + 22);
   }

   public void setMyGYearMonthTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 22 , value);
   }

   public byte[] getMyHexBinaryTest() {
      return getBytes(START_PROPERTY_INDEX + 23);
   }

   public void setMyHexBinaryTest(byte[] value) {
      set(START_PROPERTY_INDEX + 23 , value);
   }

   public java.lang.String getMyIDTest() {
      return getString(START_PROPERTY_INDEX + 24);
   }

   public void setMyIDTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 24 , value);
   }

   public java.lang.String getMyIDREFTest() {
      return getString(START_PROPERTY_INDEX + 25);
   }

   public void setMyIDREFTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 25 , value);
   }

   public java.util.List getMyIDREFSTest() {
      return getList(START_PROPERTY_INDEX + 26);
   }

   public void setMyIDREFSTest(java.util.List value) {
      set(START_PROPERTY_INDEX + 26 , value);
   }

   public int getMyIntTest() {
      return getInt(START_PROPERTY_INDEX + 27);
   }

   public void setMyIntTest(int value) {
      set(START_PROPERTY_INDEX + 27 , value);
   }

   public java.math.BigInteger getMyIntegerTest() {
      return getBigInteger(START_PROPERTY_INDEX + 28);
   }

   public void setMyIntegerTest(java.math.BigInteger value) {
      set(START_PROPERTY_INDEX + 28 , value);
   }

   public java.lang.String getMyLanguageTest() {
      return getString(START_PROPERTY_INDEX + 29);
   }

   public void setMyLanguageTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 29 , value);
   }

   public long getMyLongTest() {
      return getLong(START_PROPERTY_INDEX + 30);
   }

   public void setMyLongTest(long value) {
      set(START_PROPERTY_INDEX + 30 , value);
   }

   public java.lang.String getMyNameTest() {
      return getString(START_PROPERTY_INDEX + 31);
   }

   public void setMyNameTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 31 , value);
   }

   public java.lang.String getMyNCNameTest() {
      return getString(START_PROPERTY_INDEX + 32);
   }

   public void setMyNCNameTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 32 , value);
   }

   public java.math.BigInteger getMyNegativeIntegerTest() {
      return getBigInteger(START_PROPERTY_INDEX + 33);
   }

   public void setMyNegativeIntegerTest(java.math.BigInteger value) {
      set(START_PROPERTY_INDEX + 33 , value);
   }

   public java.math.BigInteger getMyNonNegativeIntegerTest() {
      return getBigInteger(START_PROPERTY_INDEX + 34);
   }

   public void setMyNonNegativeIntegerTest(java.math.BigInteger value) {
      set(START_PROPERTY_INDEX + 34 , value);
   }

   public java.lang.String getMyNMTOKENTest() {
      return getString(START_PROPERTY_INDEX + 35);
   }

   public void setMyNMTOKENTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 35 , value);
   }

   public java.util.List getMyNMTOKENSTest() {
      return getList(START_PROPERTY_INDEX + 36);
   }

   public void setMyNMTOKENSTest(java.util.List value) {
      set(START_PROPERTY_INDEX + 36 , value);
   }

   public java.lang.String getMyNOTATIONTest() {
      return getString(START_PROPERTY_INDEX + 37);
   }

   public void setMyNOTATIONTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 37 , value);
   }

   public java.lang.String getMyNormalizedStringTest() {
      return getString(START_PROPERTY_INDEX + 38);
   }

   public void setMyNormalizedStringTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 38 , value);
   }

   public java.math.BigInteger getMyPositiveIntegerTest() {
      return getBigInteger(START_PROPERTY_INDEX + 39);
   }

   public void setMyPositiveIntegerTest(java.math.BigInteger value) {
      set(START_PROPERTY_INDEX + 39 , value);
   }

   public java.math.BigInteger getMyNonPositiveIntegerTest() {
      return getBigInteger(START_PROPERTY_INDEX + 40);
   }

   public void setMyNonPositiveIntegerTest(java.math.BigInteger value) {
      set(START_PROPERTY_INDEX + 40 , value);
   }

   public java.lang.String getMyQNameTest() {
      return getString(START_PROPERTY_INDEX + 41);
   }

   public void setMyQNameTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 41 , value);
   }

   public short getMyShort() {
      return getShort(START_PROPERTY_INDEX + 42);
   }

   public void setMyShort(short value) {
      set(START_PROPERTY_INDEX + 42 , value);
   }

   public java.lang.String getMyStringTest() {
      return getString(START_PROPERTY_INDEX + 43);
   }

   public void setMyStringTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 43 , value);
   }

   public java.lang.String getMyTimeTest() {
      return getString(START_PROPERTY_INDEX + 44);
   }

   public void setMyTimeTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 44 , value);
   }

   public java.lang.String getMyTokenTest() {
      return getString(START_PROPERTY_INDEX + 45);
   }

   public void setMyTokenTest(java.lang.String value) {
      set(START_PROPERTY_INDEX + 45 , value);
   }

   public short getMyUnsignedByteTest() {
      return getShort(START_PROPERTY_INDEX + 46);
   }

   public void setMyUnsignedByteTest(short value) {
      set(START_PROPERTY_INDEX + 46 , value);
   }

   public long getMyUnsignedIntTest() {
      return getLong(START_PROPERTY_INDEX + 47);
   }

   public void setMyUnsignedIntTest(long value) {
      set(START_PROPERTY_INDEX + 47 , value);
   }

   public java.math.BigInteger getMyUnsignedLongTest() {
      return getBigInteger(START_PROPERTY_INDEX + 48);
   }

   public void setMyUnsignedLongTest(java.math.BigInteger value) {
      set(START_PROPERTY_INDEX + 48 , value);
   }

   public int getMyUnsignedShortTest() {
      return getInt(START_PROPERTY_INDEX + 49);
   }

   public void setMyUnsignedShortTest(int value) {
      set(START_PROPERTY_INDEX + 49 , value);
   }

   public java.lang.Long getMyLongWrapperTest() {
      return getLong(START_PROPERTY_INDEX + 50);
   }

   public void setMyLongWrapperTest(java.lang.Long value) {
      set(START_PROPERTY_INDEX + 50 , value);
   }

   public java.lang.Boolean isMyBooleanWrapperTest() {
      return new Boolean(getBoolean(START_PROPERTY_INDEX + 51));
   }

   public void setMyBooleanWrapperTest(java.lang.Boolean value) {
      set(START_PROPERTY_INDEX + 51 , value);
   }


}

