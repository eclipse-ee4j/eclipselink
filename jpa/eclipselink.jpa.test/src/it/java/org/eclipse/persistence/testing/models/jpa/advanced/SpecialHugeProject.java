/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     08/30/2013-2.5.1 Chris Delahunt
//       - 413765: DeleteAll query on subclass with a parent that has secondary
//          tables causes statements to ignore selection criteria
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/*
 * This class uses the same table as Project, and should not have its own table or its immediate parent.
 * This allows HugeProject's table to act like a secondary table in tests for bug 413765
 */

@Entity
@Table(name="CMP3_PROJECT")
@DiscriminatorValue("Z")
public class SpecialHugeProject extends HugeProject {

    public SpecialHugeProject() {
    }

    public SpecialHugeProject(String name) {
        super(name);
    }

}
