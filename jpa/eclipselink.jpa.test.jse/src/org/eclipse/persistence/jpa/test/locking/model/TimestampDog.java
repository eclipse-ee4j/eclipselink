/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/28/2015 - Will Dazey
 *       - 478331 : Added support for defining local or server as the default locale for obtaining timestamps
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.locking.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class TimestampDog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @Version
    Timestamp version;

    String name;

    public int getId() {
        return id;
    }
}
