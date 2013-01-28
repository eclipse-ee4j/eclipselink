/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/04/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa21.advanced.ddl.schema;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Health;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Level;

@Embeddable
public class RunnerInfo {
    @Column(name="R_LEVEL")
    protected Level level;
    
    @Column(name="R_HEALTH")
    protected Health health;
    
    @Embedded
    protected RunnerStatus status;

    public RunnerInfo() {}
    
    public Health getHealth() {
        return health;
    }
    
    public Level getLevel() {
        return level;
    }
    
    public RunnerStatus getStatus() {
        return status;
    }
    
    public void setLevel(Level level) {
        this.level = level;
    }
    
    public void setHealth(Health health) {
        this.health = health;
    }
    
    public void setStatus(RunnerStatus status) {
        this.status = status;
    }
}
