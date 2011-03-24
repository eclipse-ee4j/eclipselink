/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/23/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

import java.util.Collection;
import java.util.Vector;

public class Underboss extends Mafioso {
    private Boss boss;
    private Collection<Capo> capos;

    public Underboss() {
        this.capos = new Vector<Capo>();
    }

    public void addCapo(Capo capo) {
        this.capos.add(capo);
        capo.setUnderboss(this);
    }
    
    public Boss getBoss() {
        return boss;
    }
    
    public Collection<Capo> getCapos() { 
        return capos; 
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }
    
    public void setCapos(Collection<Capo> capos) {
        this.capos = capos;
    }
}
