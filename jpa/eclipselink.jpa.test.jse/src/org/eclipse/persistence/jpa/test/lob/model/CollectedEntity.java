/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/14/2018-2.7 Will Dazey
//       - 529602: Added support for CLOBs in DELETE statements for Oracle
package org.eclipse.persistence.jpa.test.lob.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class CollectedEntity {

    @Column(name = "label", nullable = false, unique = true)
    private String label;

    @Lob
    @Column(name = "content", columnDefinition = "CLOB", nullable = false)
    private String content;

    public CollectedEntity() {}

    public CollectedEntity(final String label, final String content) {
      this.label = label;
      this.content = content;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(final String label) {
      this.label = label;
    }

    public String getContent() {
      return content;
    }

    public void setContent(final String content) {
      this.content = content;
    }

    @Override
    public int hashCode() {
      return label.hashCode() + content.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
      if (object instanceof CollectedEntity) {
        final CollectedEntity that = (CollectedEntity) object;
        return this.label.equals(that.label)
            && this.content.equals(that.content);
      }
      return false;
    }
}
