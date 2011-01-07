/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/

package dbws.testing.veearray;

// javase imports
import java.util.Collection;

// EclipseLin imports
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;

public class Employee {

    public Integer id;
    public String firstName;
    public String lastName;
    @SuppressWarnings("unchecked")
    public Collection phones = new NonSynchronizedVector();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(id);
        sb.append("] ");
        sb.append(firstName);
        sb.append(" ");
        sb.append(lastName);
        return sb.toString();
    }
}
