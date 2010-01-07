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
package org.eclipse.persistence.testing.models.jpa.inheritance;

import static javax.persistence.InheritanceType.JOINED;
import javax.persistence.Inheritance;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.EmbeddedId;
import java.io.Serializable;

@Entity
@Table(name="CMP3_COMPUTER")
@Inheritance(strategy=JOINED)
public class Computer implements Serializable {
    private ComputerPK computerPK;

    public Computer() {
    }

    public Computer(ComputerPK computerPK) {
        this.computerPK = computerPK;
    }

    @EmbeddedId
    public ComputerPK getComputerPK() {
        return computerPK;
    }

    public void setComputerPK(ComputerPK computerPK) {
        this.computerPK = computerPK;
    }
}
