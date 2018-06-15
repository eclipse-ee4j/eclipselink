/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     07/07/2017-2.7 Vikram Bhatia
//       - 441546: Foreign Key attribute when used in JoinColumn generates wrong DDL statement
package org.eclipse.persistence.testing.models.jpa21.advanced.ddl;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="JPA21_DDL_VEHICLE")
public class Vehicle {
  
  @Id
  private String vnum;
  
  @ManyToOne
  @JoinColumn(name="LAST",foreignKey=@ForeignKey(name="FKv2d"))
  private Driver driver;
  
  public String getVnum() {
    return vnum;
  }
  public void setVnum(String vnum) {
    this.vnum = vnum;
  }
  public Driver getDriver() {
    return driver;
  }
  public void setDriver(Driver driver) {
    this.driver = driver;
  }
}
