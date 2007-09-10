/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.interfaces;

import java.io.*;

/**
 * Commercial and show are programs.
 */
public interface Program extends Serializable {
    String getDescription();

    Number getDuration();

    String getName();

    void setDescription(String description);

    void setDuration(Number duration);

    void setName(String name);
}
