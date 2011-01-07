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
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement;
/**
 *  @version $Header: Child.java 07-apr-2005.15:35:47 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class Child {
    private String content;
    
    public String getContent() {
        return content;
    }
    public void setContent(String c) {
        content = c;
    }
    
    public boolean equals(Object object) {
        if(object instanceof Child) {
            if ((content == null) && (((Child)object).getContent() == null)) {
                return true;
            }
            return this.content.equals(((Child)object).getContent());
        }
        return false;
    }
    public String toString() {
        return "Child: " + content + "\n";
    }
    
}
