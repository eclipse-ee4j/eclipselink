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
package org.eclipse.persistence.testing.tests.returning.model;

import java.math.BigDecimal;

import org.eclipse.persistence.sessions.*;

public abstract class BaseClass implements Cloneable {
    public BigDecimal[] a_plus_minus_b_BigDecimal;
    //	public Integer[] a_plus_minus_b_Integer;
    public Integer a_Integer;
    public Integer b_Integer;

    public BigDecimal c_BigDecimal;
    public Integer c_Integer;

    public BaseClass() {
        a_plus_minus_b_BigDecimal = new BigDecimal[2];
        //		a_plus_minus_b_Integer = new Integer[2];
    }

    public BaseClass(double a, double b) {
        this();
        setAB(new BigDecimal(a), new BigDecimal(b));
    }

    public BaseClass(double a, double b, double c) {
        this(a, b);
        setC(new BigDecimal(c));
    }

    public BaseClass(double c) {
        this();
        setC(new BigDecimal(c));
    }

    protected void setAB(BigDecimal a, BigDecimal b) {
        if (a != null && b != null) {
            a_plus_minus_b_BigDecimal[0] = a.add(b);
            a_plus_minus_b_BigDecimal[1] = a.subtract(b);
        } else {
            a_plus_minus_b_BigDecimal[0] = null;
            a_plus_minus_b_BigDecimal[1] = null;
        }
        if (a != null) {
            a_Integer = new Integer(a.intValue());
        } else {
            a_Integer = null;
        }
        if (b != null) {
            b_Integer = new Integer(b.intValue());
        } else {
            b_Integer = null;
        }
    }

    protected void setC(BigDecimal c) {
        if (c != null) {
            c_BigDecimal = c;
            c_Integer = new Integer(c_BigDecimal.intValue());
        } else {
            c_BigDecimal = null;
            c_Integer = null;
        }
    }

    public BigDecimal[] build_a_plus_minus_b_BigDecimal(Record row, Session session) {
        BigDecimal[] a_plus_minus_b_BigDecimal = new BigDecimal[2];
        BigDecimal a = (BigDecimal)session.getDatasourcePlatform().convertObject(row.get(getFieldAName()), BigDecimal.class);
        BigDecimal b = (BigDecimal)session.getDatasourcePlatform().convertObject(row.get(getFieldBName()), BigDecimal.class);
        if (a == null || b == null) {
            a_plus_minus_b_BigDecimal[0] = null;
            a_plus_minus_b_BigDecimal[1] = null;
        } else {
            a_plus_minus_b_BigDecimal[0] = a.add(b);
            a_plus_minus_b_BigDecimal[1] = a.subtract(b);
        }
        return a_plus_minus_b_BigDecimal;
    }

    /*	public Integer[] build_a_plus_minus_b_Integer(DatabaseRecord row, Session session) {
//		Integer a = (Integer) session.getDatasourcePlatform().convertObject(row.get(getFieldAName()), Integer.class);
//		Integer b = (Integer) session.getDatasourcePlatform().convertObject(row.get(getFieldBName()), Integer.class);	
		BigDecimal a = (BigDecimal) session.getDatasourcePlatform().convertObject(row.get(getFieldAName()), BigDecimal.class);
		BigDecimal b = (BigDecimal) session.getDatasourcePlatform().convertObject(row.get(getFieldBName()), BigDecimal.class);
		if(a==null || b==null) {
			a_plus_minus_b_Integer[0] = null;
			a_plus_minus_b_Integer[1] = null;
		} else {
			BigDecimal a_plus_b = a.add(b);
			BigDecimal a_minus_b = a.subtract(b);
			a_plus_minus_b_Integer[0] = new Integer(a_plus_b.intValue());
			a_plus_minus_b_Integer[1] = new Integer(a_minus_b.intValue());
		}
		return a_plus_minus_b_Integer;
	}*/

    public BigDecimal getA() {
        if (a_plus_minus_b_BigDecimal[0] == null || a_plus_minus_b_BigDecimal[1] == null) {
            return null;
        }
        BigDecimal a2 = a_plus_minus_b_BigDecimal[0].add(a_plus_minus_b_BigDecimal[1]);
        BigDecimal a = a2.divide(new BigDecimal(2), BigDecimal.ROUND_UNNECESSARY);
        return a;
    }

    public BigDecimal getB() {
        if (a_plus_minus_b_BigDecimal[0] == null || a_plus_minus_b_BigDecimal[1] == null) {
            return null;
        }
        BigDecimal b2 = a_plus_minus_b_BigDecimal[0].subtract(a_plus_minus_b_BigDecimal[1]);
        BigDecimal b = b2.divide(new BigDecimal(2), BigDecimal.ROUND_UNNECESSARY);
        return b;
    }

    public BigDecimal getC() {
        return c_BigDecimal;
    }

    public void updateWith(BaseClass other) {
        setAB(other.getA(), other.getB());
        setC(other.getC());
    }

    protected boolean compareWithoutId(BaseClass other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        for (int i = 0; i < 2; i++) {
            if (a_plus_minus_b_BigDecimal[i] == null && other.a_plus_minus_b_BigDecimal[i] != null) {
                return false;
            } else if (a_plus_minus_b_BigDecimal[i] != null && other.a_plus_minus_b_BigDecimal[i] == null) {
                return false;
            } else if (a_plus_minus_b_BigDecimal[i] != null && other.a_plus_minus_b_BigDecimal[i] != null) {
                if (!a_plus_minus_b_BigDecimal[i].equals(other.a_plus_minus_b_BigDecimal[i])) {
                    return false;
                }
            }
        }
        if (a_Integer == null && other.a_Integer != null) {
            return false;
        } else if (a_Integer != null && other.a_Integer == null) {
            return false;
        } else if (a_Integer != null && other.a_Integer != null) {
            if (!a_Integer.equals(other.a_Integer)) {
                return false;
            }
        }
        if (b_Integer == null && other.b_Integer != null) {
            return false;
        } else if (b_Integer != null && other.b_Integer == null) {
            return false;
        } else if (b_Integer != null && other.b_Integer != null) {
            if (!b_Integer.equals(other.b_Integer)) {
                return false;
            }
        }
        if (c_BigDecimal == null && other.c_BigDecimal != null) {
            return false;
        } else if (c_BigDecimal != null && other.c_BigDecimal == null) {
            return false;
        } else if (c_BigDecimal != null && other.c_BigDecimal != null) {
            if (!c_BigDecimal.equals(other.c_BigDecimal)) {
                return false;
            }
        } else if (c_Integer == null && other.c_Integer != null) {
            return false;
        } else if (c_Integer != null && other.c_Integer == null) {
            return false;
        } else if (c_Integer != null && other.c_Integer != null) {
            if (!c_Integer.equals(other.c_Integer)) {
                return false;
            }
        }
        return true;
    }

    // should be overridden

    public abstract String getFieldAName();

    public abstract String getFieldBName();

    public String toString() {
        String str = new String();
        if (getA() != null && getB() != null) {
            str = str + "A=" + getA() + "; B=" + getB();
        }
        if (getC() != null) {
            str = str + "; C=" + getC();
        }
        if (str.length() > 0) {
            str = str + ".";
        }
        return str;
    }

    public boolean isValid() {
        if (a_plus_minus_b_BigDecimal[0] == null && a_plus_minus_b_BigDecimal[1] != null) {
            return false;
        } else if (a_plus_minus_b_BigDecimal[0] != null && a_plus_minus_b_BigDecimal[1] == null) {
            return false;
        } else if (a_plus_minus_b_BigDecimal[0] != null && a_plus_minus_b_BigDecimal[1] != null) {
            if (a_Integer == null) {
                return false;
            }
            if (b_Integer == null) {
                return false;
            }
            BigDecimal a2 = a_plus_minus_b_BigDecimal[0].add(a_plus_minus_b_BigDecimal[1]);
            BigDecimal a = a2.divide(new BigDecimal(2), BigDecimal.ROUND_UNNECESSARY);
            BigDecimal b2 = a_plus_minus_b_BigDecimal[0].subtract(a_plus_minus_b_BigDecimal[1]);
            BigDecimal b = b2.divide(new BigDecimal(2), BigDecimal.ROUND_UNNECESSARY);
            if (a_Integer.intValue() != a.intValue()) {
                return false;
            }
            if (b_Integer.intValue() != b.intValue()) {
                return false;
            }
        }
        if (c_BigDecimal == null) {
            if (c_Integer != null) {
                return false;
            }
        } else {
            if (c_Integer == null) {
                return false;
            }
            if (c_BigDecimal.intValue() != c_Integer.intValue()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(other == null) {
            return false;
        }
        if(getClass() == other.getClass()) {
            return this.compareWithoutId((BaseClass)other);
        } else {
            return false;
        }
    }

    public Object clone() {
        try {
            BaseClass clone = (BaseClass)super.clone();
            clone.a_plus_minus_b_BigDecimal = new BigDecimal[2];
            for(int i=0; i < a_plus_minus_b_BigDecimal.length; i++) {
                clone.a_plus_minus_b_BigDecimal[i] = a_plus_minus_b_BigDecimal[i]; 
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
}
