/*
 * Copyright (c) 2017, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     07/07/2017-2.7 Vikram Bhatia
//       - 441546: Foreign Key attribute when used in JoinColumn generates wrong DDL statement
package org.eclipse.persistence.testing.models.jpa21.advanced.ddl;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
