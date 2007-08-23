/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
/*
   DESCRIPTION

   PRIVATE CLASSES

   NOTES

   MODIFIED    (MM/DD/YY)
    mfobrien    07/28/06 - Creation
 */

/**
 *  @version $Header: Team.java 22-aug-2006.16:51:28 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.0
 */
package org.eclipse.persistence.testing.sdo.helper.pluggable.model;


/**
 * This class has a 1-1 loose aggregation relationship with Employee.<br>
 * @author mfobrien
 */
public class Team extends POJO {
    // instance variables
    private Employee manager;

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    // overloaded function to handle setName(null)
    public void setManager() {
        manager = null;
    }
}