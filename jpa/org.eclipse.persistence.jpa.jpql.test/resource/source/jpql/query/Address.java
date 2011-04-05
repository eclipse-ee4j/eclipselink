package jpql.query;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries
({
	@NamedQuery(name="address.addition",        query="SELECT a.id + 2 FROM Address a"),
	@NamedQuery(name="address.collection",      query="SELECT c FROM Address a JOIN a.customerList c"),
	@NamedQuery(name="address.concat",          query="SELECT CONCAT(a.street, a.city) FROM Address a"),
	@NamedQuery(name="address.count",           query="SELECT DISTINCT COUNT(a) FROM Address a"),
	@NamedQuery(name="address.index",           query="SELECT INDEX(c) FROM Address a JOIN a.customerList c"),
	@NamedQuery(name="address.join",            query="SELECT a FROM Address a, Employee e JOIN FETCH a.customerList, Alias s JOIN FETCH s.addresses"),
	@NamedQuery(name="address.join.fetch",      query="SELECT MOD(a.id, 2) FROM Address a JOIN FETCH a.customerList"), // SHOULD BE "SELECT MOD(a.id, 2) AS m FROM Address a JOIN FETCH a.customerList ORDER BY m"
	@NamedQuery(name="address.join.inner",      query="SELECT c FROM Address a INNER JOIN a.customerList c"),
	@NamedQuery(name="address.join.left",       query="SELECT e FROM Address a LEFT JOIN a.customerList e"),
	@NamedQuery(name="address.join.left.outer", query="SELECT c FROM Address a LEFT OUTER JOIN a.customerList c"),
	@NamedQuery(name="address.relationship1",   query="SELECT c.dept FROM Address a LEFT OUTER JOIN a.customerList c"),
	@NamedQuery(name="address.relationship2",   query="SELECT c.lastName FROM Address a LEFT OUTER JOIN a.customerList c"),
	@NamedQuery(name="address.length",          query="SELECT LENGTH(a.street) FROM Address a"),
	@NamedQuery(name="address.locate",          query="SELECT LOCATE(a.street, 'Arco Drive') FROM Address a"),
	@NamedQuery(name="address.member",          query="SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.customerList"),
	@NamedQuery(name="address.member.not",      query="SELECT a FROM Address a, Customer c WHERE c NOT MEMBER OF a.customerList"),
	@NamedQuery(name="address.size",            query="SELECT SIZE(a.customerList) FROM Address a JOIN a.customerList c"),
	@NamedQuery(name="address.stateField",      query="SELECT c.lastName FROM Address a JOIN a.customerList AS c"),
	@NamedQuery(name="address.substring",       query="SELECT SUBSTRING(a.state, 0, 1) FROM Address a")
})
@SuppressWarnings("unused")
public class Address implements Serializable {

	private String city;
	@Id
	@Column(nullable = false)
	@GeneratedValue(generator="mySequenceGeneratorName")
	private Long id;
	private String state;
	private String street;
	@Embedded
	private ZipCode zip;
	@OneToMany(mappedBy = "address")
	private List<Customer> customerList;
}