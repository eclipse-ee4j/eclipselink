/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/11/2018 - Will Dazey
 *       - 533148 : Add the eclipselink.jpa.sql-call-deferral property
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.property.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ABSTRACTPARENT")
public abstract class AbstractParent {
  @Id @Column(name = "ABSTRACTPARENT_PK")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  public Long getId() {
      return this.id;
  }

  public void setId(Long id) {
      this.id = id;
  }
}
