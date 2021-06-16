/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlrootelement;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="point3d")
public class Point3D extends Point2D
{
    public int z;

    public Point3D(){
        ;
    }
    public Point3D(int _x, int _y){
        super(_x,_y);
    }

    public Point3D(int _x, int _y, int _z){
        super(_x,_y);
        z = _z;
    }

    public boolean equals(Object object) {
        Point3D point3d = ((Point3D)object);
        return (point3d.x == this.x) && (point3d.y == this.y) && (point3d.z == this.z);
    }
}
