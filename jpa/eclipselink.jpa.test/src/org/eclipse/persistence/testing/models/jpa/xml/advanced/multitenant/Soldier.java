/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

public class Soldier extends Mafioso {
    private Capo capo;
    private Collection<Contract> contracts;

    public Soldier() {
        this.contracts = new Vector<Contract>();
    }

    public void addContract(Contract contract) {
        contracts.add(contract);
    }
    
    public Capo getCapo(){
        return capo;
    }
    
    public Collection<Contract> getContracts() { 
        return contracts; 
    }

    public void setCapo(Capo capo){
        this.capo = capo;
    }
    
    public void setContracts(Collection<Contract> contracts) {
        this.contracts = contracts;
    }
}
