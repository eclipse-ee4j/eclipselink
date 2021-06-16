/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.sdo.externalizable;

import commonj.sdo.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class InsecureDataObject implements DataObject {
    @Override
    public Object get(String path) {
        return null;
    }

    @Override
    public void set(String path, Object value) {

    }

    @Override
    public boolean isSet(String path) {
        return false;
    }

    @Override
    public void unset(String path) {

    }

    @Override
    public boolean getBoolean(String path) {
        return false;
    }

    @Override
    public byte getByte(String path) {
        return 0;
    }

    @Override
    public char getChar(String path) {
        return 0;
    }

    @Override
    public double getDouble(String path) {
        return 0;
    }

    @Override
    public float getFloat(String path) {
        return 0;
    }

    @Override
    public int getInt(String path) {
        return 0;
    }

    @Override
    public long getLong(String path) {
        return 0;
    }

    @Override
    public short getShort(String path) {
        return 0;
    }

    @Override
    public byte[] getBytes(String path) {
        return new byte[0];
    }

    @Override
    public BigDecimal getBigDecimal(String path) {
        return null;
    }

    @Override
    public BigInteger getBigInteger(String path) {
        return null;
    }

    @Override
    public DataObject getDataObject(String path) {
        return null;
    }

    @Override
    public Date getDate(String path) {
        return null;
    }

    @Override
    public String getString(String path) {
        return null;
    }

    @Override
    public List getList(String path) {
        return null;
    }

    @Override
    public Sequence getSequence(String path) {
        return null;
    }

    @Override
    public void setBoolean(String path, boolean value) {

    }

    @Override
    public void setByte(String path, byte value) {

    }

    @Override
    public void setChar(String path, char value) {

    }

    @Override
    public void setDouble(String path, double value) {

    }

    @Override
    public void setFloat(String path, float value) {

    }

    @Override
    public void setInt(String path, int value) {

    }

    @Override
    public void setLong(String path, long value) {

    }

    @Override
    public void setShort(String path, short value) {

    }

    @Override
    public void setBytes(String path, byte[] value) {

    }

    @Override
    public void setBigDecimal(String path, BigDecimal value) {

    }

    @Override
    public void setBigInteger(String path, BigInteger value) {

    }

    @Override
    public void setDataObject(String path, DataObject value) {

    }

    @Override
    public void setDate(String path, Date value) {

    }

    @Override
    public void setString(String path, String value) {

    }

    @Override
    public void setList(String path, List value) {

    }

    @Override
    public Object get(int propertyIndex) {
        return null;
    }

    @Override
    public void set(int propertyIndex, Object value) {

    }

    @Override
    public boolean isSet(int propertyIndex) {
        return false;
    }

    @Override
    public void unset(int propertyIndex) {

    }

    @Override
    public boolean getBoolean(int propertyIndex) {
        return false;
    }

    @Override
    public byte getByte(int propertyIndex) {
        return 0;
    }

    @Override
    public char getChar(int propertyIndex) {
        return 0;
    }

    @Override
    public double getDouble(int propertyIndex) {
        return 0;
    }

    @Override
    public float getFloat(int propertyIndex) {
        return 0;
    }

    @Override
    public int getInt(int propertyIndex) {
        return 0;
    }

    @Override
    public long getLong(int propertyIndex) {
        return 0;
    }

    @Override
    public short getShort(int propertyIndex) {
        return 0;
    }

    @Override
    public byte[] getBytes(int propertyIndex) {
        return new byte[0];
    }

    @Override
    public BigDecimal getBigDecimal(int propertyIndex) {
        return null;
    }

    @Override
    public BigInteger getBigInteger(int propertyIndex) {
        return null;
    }

    @Override
    public DataObject getDataObject(int propertyIndex) {
        return null;
    }

    @Override
    public Date getDate(int propertyIndex) {
        return null;
    }

    @Override
    public String getString(int propertyIndex) {
        return null;
    }

    @Override
    public List getList(int propertyIndex) {
        return null;
    }

    @Override
    public Sequence getSequence(int propertyIndex) {
        return null;
    }

    @Override
    public void setBoolean(int propertyIndex, boolean value) {

    }

    @Override
    public void setByte(int propertyIndex, byte value) {

    }

    @Override
    public void setChar(int propertyIndex, char value) {

    }

    @Override
    public void setDouble(int propertyIndex, double value) {

    }

    @Override
    public void setFloat(int propertyIndex, float value) {

    }

    @Override
    public void setInt(int propertyIndex, int value) {

    }

    @Override
    public void setLong(int propertyIndex, long value) {

    }

    @Override
    public void setShort(int propertyIndex, short value) {

    }

    @Override
    public void setBytes(int propertyIndex, byte[] value) {

    }

    @Override
    public void setBigDecimal(int propertyIndex, BigDecimal value) {

    }

    @Override
    public void setBigInteger(int propertyIndex, BigInteger value) {

    }

    @Override
    public void setDataObject(int propertyIndex, DataObject value) {

    }

    @Override
    public void setDate(int propertyIndex, Date value) {

    }

    @Override
    public void setString(int propertyIndex, String value) {

    }

    @Override
    public void setList(int propertyIndex, List value) {

    }

    @Override
    public Object get(Property property) {
        return null;
    }

    @Override
    public void set(Property property, Object value) {

    }

    @Override
    public boolean isSet(Property property) {
        return false;
    }

    @Override
    public void unset(Property property) {

    }

    @Override
    public boolean getBoolean(Property property) {
        return false;
    }

    @Override
    public byte getByte(Property property) {
        return 0;
    }

    @Override
    public char getChar(Property property) {
        return 0;
    }

    @Override
    public double getDouble(Property property) {
        return 0;
    }

    @Override
    public float getFloat(Property property) {
        return 0;
    }

    @Override
    public int getInt(Property property) {
        return 0;
    }

    @Override
    public long getLong(Property property) {
        return 0;
    }

    @Override
    public short getShort(Property property) {
        return 0;
    }

    @Override
    public byte[] getBytes(Property property) {
        return new byte[0];
    }

    @Override
    public BigDecimal getBigDecimal(Property property) {
        return null;
    }

    @Override
    public BigInteger getBigInteger(Property property) {
        return null;
    }

    @Override
    public DataObject getDataObject(Property property) {
        return null;
    }

    @Override
    public Date getDate(Property property) {
        return null;
    }

    @Override
    public String getString(Property property) {
        return null;
    }

    @Override
    public List getList(Property property) {
        return null;
    }

    @Override
    public Sequence getSequence(Property property) {
        return null;
    }

    @Override
    public void setBoolean(Property property, boolean value) {

    }

    @Override
    public void setByte(Property property, byte value) {

    }

    @Override
    public void setChar(Property property, char value) {

    }

    @Override
    public void setDouble(Property property, double value) {

    }

    @Override
    public void setFloat(Property property, float value) {

    }

    @Override
    public void setInt(Property property, int value) {

    }

    @Override
    public void setLong(Property property, long value) {

    }

    @Override
    public void setShort(Property property, short value) {

    }

    @Override
    public void setBytes(Property property, byte[] value) {

    }

    @Override
    public void setBigDecimal(Property property, BigDecimal value) {

    }

    @Override
    public void setBigInteger(Property property, BigInteger value) {

    }

    @Override
    public void setDataObject(Property property, DataObject value) {

    }

    @Override
    public void setDate(Property property, Date value) {

    }

    @Override
    public void setString(Property property, String value) {

    }

    @Override
    public void setList(Property property, List value) {

    }

    @Override
    public DataObject createDataObject(String propertyName) {
        return null;
    }

    @Override
    public DataObject createDataObject(int propertyIndex) {
        return null;
    }

    @Override
    public DataObject createDataObject(Property property) {
        return null;
    }

    @Override
    public DataObject createDataObject(String propertyName, String namespaceURI, String typeName) {
        return null;
    }

    @Override
    public DataObject createDataObject(int propertyIndex, String namespaceURI, String typeName) {
        return null;
    }

    @Override
    public DataObject createDataObject(Property property, Type type) {
        return null;
    }

    @Override
    public void delete() {

    }

    @Override
    public DataObject getContainer() {
        return null;
    }

    @Override
    public Property getContainmentProperty() {
        return null;
    }

    @Override
    public DataGraph getDataGraph() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Sequence getSequence() {
        return null;
    }

    @Override
    public List getInstanceProperties() {
        return null;
    }

    @Override
    public Property getInstanceProperty(String propertyName) {
        return null;
    }

    @Override
    public Property getProperty(String propertyName) {
        return null;
    }

    @Override
    public DataObject getRootObject() {
        return null;
    }

    @Override
    public ChangeSummary getChangeSummary() {
        return null;
    }

    @Override
    public void detach() {

    }
}
