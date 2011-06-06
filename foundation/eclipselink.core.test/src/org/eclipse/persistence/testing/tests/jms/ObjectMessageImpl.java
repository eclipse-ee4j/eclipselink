/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jms;

import javax.jms.*;

import java.io.Serializable;

/**  
 * Dummy implementation for testing purposes.
 */
public class ObjectMessageImpl implements ObjectMessage {
    Serializable sObj;

    public ObjectMessageImpl() {
        sObj = null;
    }

    public void setObject(Serializable sio) {
        sObj = sio;
    }

    public Serializable getObject() {
        return sObj;
    }

    // satisfy interface implementation

    public void setStringProperty(String str1, String str2) {
    }

    public void setShortProperty(String str1, short sh) {
    }

    public void setObjectProperty(String str1, Object obj) {
    }

    public void setLongProperty(String str1, long lg) {
    }

    public void setJMSType(String str) {
    }

    public void setJMSTimestamp(long ts) {
    }

    public void setJMSReplyTo(Destination des) {
    }

    public void setJMSRedelivered(boolean val) {
    }

    public void setJMSPriority(int i) {
    }

    public void setJMSMessageID(String id) {
    }

    public void setJMSExpiration(long exp) {
    }

    public void setJMSDestination(Destination dest) {
    }

    public void setJMSDeliveryMode(int mode) {
    }

    public void setJMSCorrelationIDAsBytes(byte[] bytes) {
    }

    public void setJMSCorrelationID(String id) {
    }

    public void setIntProperty(String str, int i) {
    }

    public void setFloatProperty(String str, float fl) {
    }

    public void setDoubleProperty(String str, double db) {
    }

    public void setByteProperty(String str, byte bt) {
    }

    public void setBooleanProperty(String str, boolean val) {
    }

    public boolean propertyExists(String str) {
        return false;
    }

    public String getStringProperty(String str) {
        return null;
    }

    public short getShortProperty(String str) {
        return 1;
    }

    public java.util.Enumeration getPropertyNames() {
        return null;
    }

    public Object getObjectProperty(String str) {
        return null;
    }

    public long getLongProperty(String str) {
        return 1;
    }

    public String getJMSType() {
        return null;
    }

    public long getJMSTimestamp() {
        return 1;
    }

    public Destination getJMSReplyTo() {
        return null;
    }

    public boolean getJMSRedelivered() {
        return false;
    }

    public int getJMSPriority() {
        return 1;
    }

    public String getJMSMessageID() {
        return null;
    }

    public long getJMSExpiration() {
        return 1;
    }

    public Destination getJMSDestination() {
        return null;
    }

    public int getJMSDeliveryMode() {
        return 1;
    }

    public byte[] getJMSCorrelationIDAsBytes() {
        return null;
    }

    public String getJMSCorrelationID() {
        return null;
    }

    public int getIntProperty(String str) {
        return 1;
    }

    public float getFloatProperty(String str) {
        return 1;
    }

    public double getDoubleProperty(String str) {
        return 1;
    }

    public byte getByteProperty(String str) {
        return 1;
    }

    public boolean getBooleanProperty(String str) {
        return false;
    }

    public void clearProperties() {
    }

    public void clearBody() {
    }

    public void acknowledge() {
    }
}
