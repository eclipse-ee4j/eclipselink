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
package org.eclipse.persistence.testing.tests.proxyindirection;


/*
 * Phone implementation.
 *
 * An Implementation of the Contact interface.
 *
 * @author        Rick Barkhouse
 * @since        08/25/2000 16:36:20
 */
public class PhoneImpl implements Contact {
    public int id;
    public boolean isPublic;
    public String number;
    public String type;
    public static String HOME = "Home";
    public static String WORK = "Work";
    public static String CELL = "Mobile";

    public int getID() {
        return this.id;
    }

    public String getNumber() {
        return this.number;
    }

    public String getType() {
        return this.type;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public void setID(int value) {
        this.id = value;
    }

    public void setIsPublic(boolean value) {
        this.isPublic = value;
    }

    public void setNumber(String value) {
        this.number = value;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String toString() {
        return "[Phone #" + getID() + "] " + getNumber() + " (" + getType() + ")";
    }
}
