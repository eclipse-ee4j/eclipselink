/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     etang - April 12/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.server;

import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

import java.net.URL;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless(name = "org.eclipse.persistence.testing.sdo.server.DeptServiceBean", mappedName = "DeptServiceBean")
@Remote
public class DeptServiceImpl implements DeptService {
    private static boolean _isInited = false;

    public DeptServiceImpl() {
        init();
    }

    protected void init() {
        if (_isInited) {
            return;
        }
        synchronized (DeptServiceImpl.class) {
            if (_isInited) {
                return;
            }
            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                URL url = cl.getResource("org/eclipse/persistence/testing/sdo/server/Dept.xsd");
                String path = url.toExternalForm();
                XSDHelper.INSTANCE.define(url.openStream(), path.substring(0, path.lastIndexOf('/') + 1));
                _isInited = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Dept getDept(Integer deptno) {
        System.out.println("\n********** getDept() Called **********\n");
        System.out.println(deptno);

        Dept dept = (Dept) DataFactory.INSTANCE.create(Dept.class);
        dept.setDeptno(deptno);
        dept.setDname("Dname" + deptno);
        dept.setLoc("Loc" + deptno);
        return dept;
    }

    public boolean updateDept(Dept dept) {
        System.out.println("\n********** updateDept() Called **********\n");
        DeptImpl deptImpl = (DeptImpl) dept;
        System.out.println(XMLHelper.INSTANCE.save(deptImpl, deptImpl.getType().getURI(), deptImpl.getType().getName()));

        return (dept != null);
    }
}
