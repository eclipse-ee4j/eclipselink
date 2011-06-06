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
 * E-mail implementation.
 *
 * An Implementation of the Contact interface.
 *
 * @author        Rick Barkhouse
 * @since        08/25/2000 16:36:20
 */
public class EmailImpl implements Contact {
    public int id;
    public boolean isPublic;
    public String username;
    public String domain;
    public boolean wantsHTMLMail;

    public String getDomain() {
        return this.domain;
    }

    public int getID() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public void setDomain(String value) {
        this.domain = value;
    }

    public void setID(int value) {
        this.id = value;
    }

    public void setIsPublic(boolean value) {
        this.isPublic = value;
    }

    public void setUsername(String value) {
        this.username = value;
    }

    public void setWantsHTMLMail(boolean value) {
        this.wantsHTMLMail = value;
    }

    public String toString() {
        return "[E-mail #" + getID() + "] <" + getUsername() + "@" + getDomain() + ">";
    }

    public boolean wantsHTMLMail() {
        return this.wantsHTMLMail;
    }
}
