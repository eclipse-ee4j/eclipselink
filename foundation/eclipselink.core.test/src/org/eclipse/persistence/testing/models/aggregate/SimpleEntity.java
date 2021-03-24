/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.aggregate;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class SimpleEntity {
  private String id;
  private String field;
  private SimpleAggregate simpleAggregate;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public SimpleAggregate getSimpleAggregate() {
    return simpleAggregate;
  }

  public void setSimpleAggregate(SimpleAggregate simpleAggregate) {
    this.simpleAggregate = simpleAggregate;
  }

  public static TableDefinition tableDefinition() {
    TableDefinition definition = new TableDefinition();

    definition.setName("SIMPLEENTITY");

    definition.addIdentityField("ID", String.class, 15);
    definition.addField("FIELD", String.class, 15);
    definition.addField("CONTENT", String.class, 20);

    return definition;
  }
}
