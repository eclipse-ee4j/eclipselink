/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 01 November 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="TestBean")
public class TestBean {

    // Direct
    @XmlPath("info/desc/text()")
    public String description;

    // Composite
    @XmlPath("companion")
    public TestBean companion;

    // Composite Collection Primitive Array
    @XmlPath("info/name/text()")
    public String[] name;

    // Composite Collection Primitive List
    @XmlPath("info/roles/text()")
    public ArrayList<String> roles;

    // Composite Collection Array
    @XmlPath("sub-bean")
    public TestBean[] subBean;

    // Composite Collection List
    @XmlPath("rejected")
    public ArrayList<TestBean> rejected;

    // Direct Positional
    @XmlPath("info/coords[1]/text()")
    public String lat;
    @XmlPath("info/coords[2]/text()")
    public String lon;

    // Attribute
    @XmlPath("@global")
    public boolean global = true;

    public static TestBean example() {
        TestBean bean = new TestBean();
        bean.description = "Root Bean";
        bean.name = new String[3]; bean.name[0] = "root"; bean.name[1] = "ROOT"; bean.name[2] = "ROOT-00";
        bean.roles = new ArrayList<String>(2); bean.roles.add("ROLE-A1"); bean.roles.add("ROLE-A7");
        bean.lat = "1.111";
        bean.lat = "11.111";

        TestBean companion1 = new TestBean();
        companion1.description = "Companion 1";
        companion1.name = new String[3]; companion1.name[0] = "root-c"; companion1.name[1] = "ROOT-C"; companion1.name[2] = "ROOT-00C";
        companion1.roles = new ArrayList<String>(2); companion1.roles.add("ROLE-A1"); companion1.roles.add("ROLE-A7");
        companion1.lat = "1.111";
        companion1.lat = "11.111";
        TestBean companion2 = new TestBean();
        companion2.description = "Companion 2";
        companion2.name = new String[3]; companion2.name[0] = "bbb2-c"; companion2.name[1] = "BBB2-C"; companion2.name[2] = "BEAN-BBB-02C";
        companion2.roles = new ArrayList<String>(2); companion2.roles.add("ROLE-B2"); companion2.roles.add("ROLE-B7");
        companion2.lat = "2.222";
        companion2.lat = "22.222";
        companion2.global = false;

        TestBean sub1 = new TestBean();
        sub1.description = "Sub Bean 1";
        sub1.name = new String[3]; sub1.name[0] = "aaa1"; sub1.name[1] = "AAA1"; sub1.name[2] = "BEAN-AAA-01";
        sub1.roles = new ArrayList<String>(2); sub1.roles.add("ROLE-A1"); sub1.roles.add("ROLE-A7");
        sub1.lat = "1.111";
        sub1.lat = "11.111";
        TestBean sub2 = new TestBean();
        sub2.description = "Sub Bean 2 (non-global)";
        sub2.name = new String[3]; sub2.name[0] = "bbb2"; sub2.name[1] = "BBB2"; sub2.name[2] = "BEAN-BBB-02";
        sub2.roles = new ArrayList<String>(2); sub2.roles.add("ROLE-B2"); sub2.roles.add("ROLE-B7");
        sub2.lat = "2.222";
        sub2.lat = "22.222";
        sub2.global = false;
        TestBean sub3 = new TestBean();
        sub3.description = "Sub Bean 3";
        sub3.name = new String[3]; sub3.name[0] = "ccc3"; sub3.name[1] = "CCC3"; sub3.name[2] = "BEAN-CCC-03";
        sub3.roles = new ArrayList<String>(2); sub3.roles.add("ROLE-C3"); sub3.roles.add("ROLE-C7");
        sub3.lat = "3.333";
        sub3.lat = "33.333";

        TestBean rej1 = new TestBean();
        rej1.description = "Rejected Bean 1";
        rej1.name = new String[3]; rej1.name[0] = "fff1"; rej1.name[1] = "FFF1"; rej1.name[2] = "BEAN-FFF-01";
        rej1.roles = new ArrayList<String>(2); rej1.roles.add("ROLE-F1"); rej1.roles.add("ROLE-F7");
        rej1.lat = "4.444";
        rej1.lat = "44.444";
        TestBean rej2 = new TestBean();
        rej2.description = "Rejected Bean 2";
        rej2.name = new String[3]; rej2.name[0] = "fff2"; rej2.name[1] = "FFF2"; rej2.name[2] = "BEAN-FFF-02";
        rej2.roles = new ArrayList<String>(2); rej2.roles.add("ROLE-F1"); rej2.roles.add("ROLE-F7");
        rej2.lat = "5.555";
        rej2.lat = "55.555";

        bean.subBean = new TestBean[3]; bean.rejected = new ArrayList<TestBean>();
        sub1.subBean = new TestBean[3]; sub1.rejected = new ArrayList<TestBean>();
        sub2.subBean = new TestBean[3]; sub2.rejected = new ArrayList<TestBean>();
        sub3.subBean = new TestBean[3]; sub3.rejected = new ArrayList<TestBean>();
        rej1.subBean = new TestBean[3]; rej1.rejected = new ArrayList<TestBean>();
        rej2.subBean = new TestBean[3]; rej2.rejected = new ArrayList<TestBean>();

        bean.companion = companion1;

        sub2.companion = companion2;

        bean.subBean[0] = sub1;
        bean.subBean[1] = sub2;

        sub2.subBean[0] = sub3;

        sub3.rejected.add(rej1);
        sub3.rejected.add(rej1);

        bean.rejected.add(rej1);

        return bean;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((companion == null) ? 0 : companion.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + (global ? 1231 : 1237);
        result = prime * result + ((lat == null) ? 0 : lat.hashCode());
        result = prime * result + ((lon == null) ? 0 : lon.hashCode());
        result = prime * result + Arrays.hashCode(name);
        result = prime * result + ((rejected == null) ? 0 : rejected.hashCode());
        result = prime * result + ((roles == null) ? 0 : roles.hashCode());
        result = prime * result + Arrays.hashCode(subBean);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestBean other = (TestBean) obj;
        if (companion == null) {
            if (other.companion != null)
                return false;
        } else if (!companion.equals(other.companion))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (global != other.global)
            return false;
        if (lat == null) {
            if (other.lat != null)
                return false;
        } else if (!lat.equals(other.lat))
            return false;
        if (lon == null) {
            if (other.lon != null)
                return false;
        } else if (!lon.equals(other.lon))
            return false;
        if (!Arrays.equals(name, other.name))
            return false;
        if (rejected == null) {
            if (other.rejected != null)
                return false;
        } else if (!rejected.equals(other.rejected))
            return false;
        if (roles == null) {
            if (other.roles != null)
                return false;
        } else if (!roles.equals(other.roles))
            return false;
        if (!Arrays.equals(subBean, other.subBean))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TestBean [name=" + Arrays.toString(name) + "]";
    }

}
