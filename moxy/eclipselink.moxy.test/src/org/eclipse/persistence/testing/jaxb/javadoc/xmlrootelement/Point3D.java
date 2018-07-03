/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlrootelement;

import javax.xml.bind.annotation.*;

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
