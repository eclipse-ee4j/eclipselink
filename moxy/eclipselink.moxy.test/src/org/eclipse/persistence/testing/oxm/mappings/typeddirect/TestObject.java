/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.typeddirect;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class TestObject {

	private byte[] base64;
	private byte[] hex;

	private Calendar date;
	private Calendar time;
	private Calendar dateTime;

	private Vector base64Vector;
	private Vector hexVector;

	private Vector dateVector;
	private Vector timeVector;
	private Vector dateTimeVector;
  
  private java.util.Date untypedDate;
  private java.util.Date typedDate;
  
  private java.sql.Date untypedSqlDate;
  private java.sql.Date typedSqlDate;
  
  private java.sql.Timestamp untypedTimestamp;
  private java.sql.Timestamp typedTimestamp;

	public byte[] getBase64() {
		return base64;
	}

	public byte[] getHex() {
		return hex;
	}

	public Calendar getDate() {
		return date;
	}

	public Calendar getTime() {
		return time;
	}

	public Calendar getDateTime() {
		return dateTime;
	}

	public Vector getBase64Vector() {
		return base64Vector;
	}

	public Vector getHexVector() {
		return hexVector;
	}

	public Vector getDateVector() {
		return dateVector;
	}

	public Vector getTimeVector() {
		return timeVector;
	}

	public Vector getDateTimeVector() {
		return dateTimeVector;
	}

	public void setBase64(byte[] value) {
		base64 = value;
	}

	public void setHex(byte[] value) {
		hex = value;
	}

	public void setDate(Calendar value) {
		date = value;
	}

	public void setTime(Calendar value) {
		time = value;
	}

	public void setDateTime(Calendar value) {
		dateTime = value;
	}

	public void setBase64Vector(Vector value) {
		base64Vector = value;
	}

	public void setHexVector(Vector value) {
		hexVector = value;
	}

	public void setDateVector(Vector value) {
		dateVector = value;
	}

	public void setTimeVector(Vector value) {
		timeVector = value;
	}

	public void setDateTimeVector(Vector value) {
		dateTimeVector = value;
	}
  
  public Date getUntypedDate() {
    return untypedDate;
  }
  
  public void setUntypedDate(Date date) {
    untypedDate = date;
  }
  
  public Date getTypedDate() {
    return typedDate;
  }
  
  public void setTypedDate(Date date) {
    typedDate = date;
  }

    public java.sql.Date getTypedSqlDate() {
        return typedSqlDate;
    }
    public void setTypedSqlDate(java.sql.Date date) {
        typedSqlDate = date;
    }
    public java.sql.Date getUntypedSqlDate() {
        return untypedSqlDate;
    }
    public void setUntypedSqlDate(java.sql.Date date) {
        untypedSqlDate = date;
    }
    public Timestamp getTypedTimestamp() {
        return typedTimestamp;
    }
    public Timestamp getUntypedTimestamp() {
        return untypedTimestamp;
    }
    public void setTypedTimestamp(Timestamp timestamp) {
        typedTimestamp = timestamp;
    }
    public void setUntypedTimestamp(Timestamp timestamp) {
        untypedTimestamp = timestamp;
    }
	public String toString() {
		String toString = super.toString() + " :";
		toString += "\n    base64         = " + getBase64();
		toString += "\n    hex            = " + getHex();
		toString += "\n    date           = " + getDate();
		toString += "\n    time           = " + getTime();
		toString += "\n    dateTime       = " + getDateTime();
		toString += "\n    base64Vector   = " + getBase64Vector();
		toString += "\n    hexVector      = " + getHexVector();
		toString += "\n    dateVector     = " + getDateVector();
		toString += "\n    timeVector     = " + getTimeVector();
		toString += "\n    dateTimeVector = " + getDateTimeVector();

		return toString;
	}

	public boolean equals(Object anObject) {
		if (!(anObject instanceof TestObject)) {
			return false;
		}

		TestObject aTestObject = (TestObject) anObject;
		boolean equal = true;

		equal = equal && equalByteArrays(aTestObject.getBase64(), this.getBase64());
		equal = equal && equalByteArrays(aTestObject.getHex(), this.getHex());
		
		equal = equal && (aTestObject.getDate().equals(this.getDate()));
		equal = equal && (aTestObject.getTime().equals(this.getTime()));
		equal = equal && (aTestObject.getDateTime().equals(this.getDateTime()));

		equal = equal && equalByteArrayVectors(aTestObject.getBase64Vector(), this.getBase64Vector());
		equal = equal && equalByteArrayVectors(aTestObject.getHexVector(), this.getHexVector());
		
		equal = equal && (aTestObject.getDateVector().equals(this.getDateVector()));
		equal = equal && (aTestObject.getTimeVector().equals(this.getTimeVector()));
		equal = equal && (aTestObject.getDateTimeVector().equals(this.getDateTimeVector()));

        equal = equal && (aTestObject.getTypedDate().equals(this.getTypedDate()));
        equal = equal && (aTestObject.getUntypedDate().equals(this.getUntypedDate()));
        equal = equal && (aTestObject.getTypedSqlDate().equals(this.getTypedSqlDate()));
        equal = equal && (aTestObject.getUntypedSqlDate().equals(this.getUntypedSqlDate()));
        equal = equal && (aTestObject.getTypedTimestamp().equals(this.getTypedTimestamp()));
        equal = equal && (aTestObject.getUntypedTimestamp().equals(this.getUntypedTimestamp()));
		return equal;
	}

	private boolean equalByteArrays(byte[] array1, byte[] array2) {
		if (array1.length != array2.length) {
			return false;
		}

		for (int i = 0; i < array1.length; i++) {
			if (array1[i] != array2[i]) {
				return false;
			}
		}

		return true;
	}

	private boolean equalByteArrayVectors(Vector vector1, Vector vector2) {
		if (vector1.size() != vector2.size()) {
			return false;
		}

		for (int i = 0; i < vector1.size(); i++) {
			byte[] array1 = (byte[]) vector1.elementAt(i);
			byte[] array2 = (byte[]) vector2.elementAt(i);

			if (!equalByteArrays(array1, array2)) {
				return false;
			}
		}

		return true;
	}

}
