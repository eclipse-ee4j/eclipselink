package org.eclipse.persistence.jpa.test.query.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="QUERY_EMPLOYEE")
@NamedQuery(name="QueryEmployee.findAll", query="SELECT e FROM QueryEmployee e")
public class QueryEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Version
    int version;

    public QueryEmployee() { }

    public int getId() {
        return id;
    }
}
