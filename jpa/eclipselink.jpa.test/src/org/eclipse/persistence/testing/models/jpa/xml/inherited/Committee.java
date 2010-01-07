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
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import java.util.ArrayList;
import java.util.List;

public class Committee {
    private Integer id;
    private String description;
    private List<ExpertBeerConsumer> expertBeerConsumers;
    private List<NoviceBeerConsumer> noviceBeerConsumers;
    
    public Committee() {
        expertBeerConsumers = new ArrayList<ExpertBeerConsumer>();
        noviceBeerConsumers = new ArrayList<NoviceBeerConsumer>();
    }
    
    protected void addExpertBeerConsumer(ExpertBeerConsumer expertBeerConsumer) {
        expertBeerConsumers.add(expertBeerConsumer);
    }
    
    protected void addNoviceBeerConsumer(NoviceBeerConsumer noviceBeerConsumer) {
        noviceBeerConsumers.add(noviceBeerConsumer);
    }

    public List<ExpertBeerConsumer> getExpertBeerConsumers() {
        return expertBeerConsumers;
    }

    public List<NoviceBeerConsumer> getNoviceBeerConsumers() {
        return noviceBeerConsumers;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }
    
    public void setExpertBeerConsumers(List<ExpertBeerConsumer> expertBeerConsumers) {
        this.expertBeerConsumers = expertBeerConsumers;
    }
    
    public void setNoviceBeerConsumers(List<NoviceBeerConsumer> noviceBeerConsumers) {
        this.noviceBeerConsumers = noviceBeerConsumers;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
}
