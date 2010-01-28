/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

@Cacheable(true)
@Entity
@Table(name = "TMP_DEP")
@NamedQueries( {
        @NamedQuery(name = "getAllDepartmentsCached", query = "select d from Department d", hints = { @QueryHint(name = QueryHints.QUERY_RESULTS_CACHE, value = HintValues.TRUE) }) ,
        @NamedQuery(name = "getDepartmentByName", query = "select d from Department d where d.name = ?1"),
        @NamedQuery(name = "getDepartmentById", query = "select d from Department d where d.id = ?1"),
        @NamedQuery(name = "getDepartmentUnCached", query = "select d from Department d where d.name = ?1", hints = { }),
        @NamedQuery(name = "getDepartmentCached", query = "select d from Department d where d.name = ?1", hints = { @QueryHint(name = QueryHints.QUERY_RESULTS_CACHE, value = HintValues.TRUE) }) })
@NamedNativeQueries( {
        @NamedNativeQuery(name = "getDepartmentWithId10SQL_class", query = "select * from TMP_DEP D where D.ID = 10", resultClass = Department.class),
        @NamedNativeQuery(name = "getDepartmentWithId10SQL_mapping", query = "select * from TMP_DEP D where D.ID = 10", resultSetMapping = "departmentByClass"),
        @NamedNativeQuery(name = "getDepartmentName", query = "select name as \"HUTZLIPUTZ\" from TMP_DEP D where D.ID = 10", resultSetMapping = "departmentNameOnly"),
        @NamedNativeQuery(name = "getDepartmentFieldByField", query = "select id as \"D_ID\", name as \"D_NAME\", version as \"D_VERSION\" from TMP_DEP D where D.ID = 10", resultSetMapping = "departmentByFields"),
        @NamedNativeQuery(name = "getDepartmentFieldByField1", query = "select id as \"ID\", name as \"NAME\" from TMP_DEP D where D.ID = 1", resultSetMapping = "departmentByFields1") })
@SqlResultSetMappings( {
        @SqlResultSetMapping(name = "departmentByClass", entities = { @EntityResult(entityClass = Department.class) }),
        @SqlResultSetMapping(name = "departmentNameOnly", columns = { @ColumnResult(name = "HUTZLIPUTZ") }),
        @SqlResultSetMapping(name = "departmentByFields", entities = { @EntityResult(entityClass = Department.class, fields = {
                @FieldResult(name = "id", column = "D_ID"), @FieldResult(name = "name", column = "D_NAME"),
                @FieldResult(name = "version", column = "D_VERSION") }) }),
        @SqlResultSetMapping(name = "departmentByFields1", columns = { @ColumnResult(name = "ID"), @ColumnResult(name = "NAME") }) })
public class Department implements Serializable {

    public static final String HUGO = "hugo";

    private static final long serialVersionUID = 1L;

    public void setId(int id) {
        this._id = id;
    }

    private int _id;

    private String _name;

    private short version;

    // this constructor should be protected
    protected Department() {
    }

    public Department(int aId, String aName) {
        _id = aId;
        _name = aName;
    }

    @Id
    public int getId() {
        return _id;
    }

    public void setName(String aName) {
        _name = aName;
    }

    @Basic
    public String getName() {
        return _name;
    }

    @Version
    public short getVersion() {
        return version;
    }

    // called by EntityManager
    protected void setVersion(short newVersion) {
        version = newVersion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Department) {
            Department other = (Department) obj;
            if (_id != other._id) {
                return false;
            }
            if (_name == null) {
                return other._name == null;
            } else {
                return _name.equals(other._name);
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _id;
    }

    public Object writeReplace() {
        Department copy = new Department();
        copy._id = _id;
        copy._name = HUGO;
        copy.version = version;
        return copy;
    }

    public static class KrassDep extends Department {
        private static final long serialVersionUID = 1L;
        private final String gnubbelWurst;

        public KrassDep(String gnubbelWurst) {
            super();
            this.gnubbelWurst = gnubbelWurst;
        }

        public String getGnubbelWurst() {
            return gnubbelWurst;
        }
    }
}
