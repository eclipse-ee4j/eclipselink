package org.eclipse.persistence.jpa.test.query.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

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
