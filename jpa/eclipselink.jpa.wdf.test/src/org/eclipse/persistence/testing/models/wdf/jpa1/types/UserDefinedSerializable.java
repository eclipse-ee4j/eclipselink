/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.types;

import java.io.IOException;
import java.io.Serializable;

/**
 * This class is a user-defined serializable class. The class basically contains a single string. To make things a bit
 * complicated, the string is writen in reverse character sequence.
 */
public class UserDefinedSerializable implements Serializable {

    private static final long serialVersionUID = 1L;
    private String txt;

    public UserDefinedSerializable(String aTxt) {
        txt = aTxt;
    }

    public String getTxt() {
        return txt;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(new StringBuffer(txt).reverse().toString());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        String read = (String) in.readObject();
        txt = new StringBuffer(read).reverse().toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserDefinedSerializable) {
            UserDefinedSerializable other = (UserDefinedSerializable) obj;
            if (txt == null) {
                return other.txt == null;
            } else {
                return txt.equals(other.txt);
            }

        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (txt == null) {
            return 0;
        } else {
            return txt.hashCode();
        }
    }

    /**
     * @param txt
     *            The txt to set.
     */
    public void setTxt(String txt) {
        this.txt = txt;
    }
}
