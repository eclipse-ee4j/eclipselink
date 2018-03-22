/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.collection.model;

import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="MAIN_ENTITY")
public class MainEntity {

  @Id
  @Column(name = "ID")
  public long id;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
          name = "PARAMS",
          joinColumns = {
              @JoinColumn(name = "MAIN_ENTITY_ID", referencedColumnName = "ID")
          }
  )
  @Column(name = "VALUE")
  public Map<Param, String> params;

}
