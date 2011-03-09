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
public class Address implements Serializable
{
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

	public Address()
	{
		super();
	}

	public Address(String city,
	               Long id,
	               String street,
	               String state,
	               ZipCode zip)
	{
		super();

		this.id     = id;
		this.city   = city;
		this.state  = state;
		this.street = street;
		this.state  = state;
		this.zip    = zip;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public ZipCode getZip()
	{
		return zip;
	}

	public void setZip(ZipCode zip)
	{
		this.zip = zip;
	}

	public List<Customer> getCustomerList()
	{
		return customerList;
	}

	public void setCustomerList(List<Customer> customerList)
	{
		this.customerList = customerList;
	}

	public Customer addCustomer(Customer customer)
	{
		getCustomerList().add(customer);
		customer.setAddress(this);
		return customer;
	}

	public Customer removeCustomer(Customer customer)
	{
		getCustomerList().remove(customer);
		customer.setAddress(null);
		return customer;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}
}