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
 *     02/18/2010-2.0.2 Guy Pelletier 
 *       - 294803: @Column(updatable=false) has no effect on @Basic mappings
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

public class Official {
    private int id;
    private String name;
    private Integer age;
    private ServiceTime serviceTime;
    private Integer salary;
    private Integer bonus;
    
    public Official() {}
    
    public Integer getAge() {
        return age;
    }
    
    public Integer getBonus() {
        return bonus;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Integer getSalary() { 
        return salary; 
    }
    
    public ServiceTime getServiceTime() {
        return serviceTime;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSalary(Integer salary) { 
        this.salary = salary; 
    }
    
    public void setServiceTime(ServiceTime serviceTime) {
        this.serviceTime = serviceTime;
    }
    
    public String toString() {
        return this.name;
    }
}
