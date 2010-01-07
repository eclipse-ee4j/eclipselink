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
package org.eclipse.persistence.testing.tests.nchar;

public class CharNchar {
    private int id;
    private Character ch;
    private Character nCh;
    private String str;
    private String nStr;
    private char[] clob;
    private char[] nClob;
    private char[] clob2;
    private char[] nClob2;

    public CharNchar() {
    }

    public CharNchar(CharNchar obj) {
        copy(obj);
    }

    public CharNchar(char ch, char nCh, int sizeStr, int sizeClob, int sizeClob2) {
        setAll(ch, nCh, sizeStr, sizeClob, sizeClob2);
    }

    public void copy(CharNchar obj) {
        setId(obj.getId());
        copyAllButId(obj);
    }

    public void copyAllButId(CharNchar obj) {
        setChar(null);
        if (obj.getChar() != null) {
            setChar(new Character(obj.getChar().charValue()));
        }

        setNchar(null);
        if (obj.getNchar() != null) {
            setNchar(new Character(obj.getNchar().charValue()));
        }

        setStr(null);
        if (obj.getStr() != null) {
            setStr(new String(obj.getStr()));
        }

        setNstr(null);
        if (obj.getNstr() != null) {
            setNstr(new String(obj.getNstr()));
        }

        setClob(null);
        if (obj.getClob() != null) {
            setClob(new char[obj.getClob().length]);
            System.arraycopy(obj.getClob(), 0, getClob(), 0, getClob().length);
        }

        setNclob(null);
        if (obj.getNclob() != null) {
            setNclob(new char[obj.getNclob().length]);
            System.arraycopy(obj.getNclob(), 0, getNclob(), 0, getNclob().length);
        }

        setClob2(null);
        if (obj.getClob2() != null) {
            setClob2(new char[obj.getClob2().length]);
            System.arraycopy(obj.getClob2(), 0, getClob2(), 0, getClob2().length);
        }

        setNclob2(null);
        if (obj.getNclob2() != null) {
            setNclob2(new char[obj.getNclob2().length]);
            System.arraycopy(obj.getNclob2(), 0, getNclob2(), 0, getNclob2().length);
        }

    }

    public void setAll(char ch, char nCh, int sizeStr, int sizeClob, int sizeClob2) {
        this.ch = new Character(ch);
        this.nCh = new Character(nCh);

        char[] chArray = new char[sizeStr];
        char[] nchArray = new char[sizeStr];
        for (int i = 0; i < sizeStr; i++) {
            chArray[i] = ch;
            nchArray[i] = nCh;
        }
        str = new String(chArray);
        nStr = new String(nchArray);

        clob = new char[sizeClob];
        nClob = new char[sizeClob];
        for (int i = 0; i < sizeClob; i++) {
            clob[i] = ch;
            nClob[i] = nCh;
        }

        clob2 = new char[sizeClob2];
        nClob2 = new char[sizeClob2];
        for (int i = 0; i < sizeClob2; i++) {
            clob2[i] = ch;
            nClob2[i] = nCh;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Character getChar() {
        return ch;
    }

    public void setChar(Character ch) {
        this.ch = ch;
    }

    public Character getNchar() {
        return nCh;
    }

    public void setNchar(Character nCh) {
        this.nCh = nCh;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getNstr() {
        return nStr;
    }

    public void setNstr(String nStr) {
        this.nStr = nStr;
    }

    public char[] getClob() {
        return clob;
    }

    public void setClob(char[] clob) {
        this.clob = clob;
    }

    public char[] getNclob() {
        return nClob;
    }

    public void setNclob(char[] nClob) {
        this.nClob = nClob;
    }

    public char[] getClob2() {
        return clob2;
    }

    public void setClob2(char[] clob) {
        this.clob2 = clob;
    }

    public char[] getNclob2() {
        return nClob2;
    }

    public void setNclob2(char[] nClob) {
        this.nClob2 = nClob;
    }
}
