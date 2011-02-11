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
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;

@Entity
@NamedQueries
({
	@NamedQuery(name="address.query1",     query="SELECT e FROM Employee e WHERE e.empId = 2"),
	@NamedQuery(name="address.query2",     query="SELECT "),
	@NamedQuery(name="address.query3",     query="SELECT  FROM Address a JOIN a.customerList c LEFT OUTER JOIN a.phoneList phone, IN(c.phoneList) p"),
	@NamedQuery(name="address.query4",     query="SELECT p. FROM Address a JOIN a.customerList c, IN(c.phoneList) p"),
	@NamedQuery(name="address.query5",     query="SELECT c.lastName FROM Address a JOIN a.customerList AS c"),
	@NamedQuery(name="address.concat",     query="SELECT CONCAT(a.street, a.city) FROM Address a"),
	@NamedQuery(name="address.length",     query="SELECT LENGTH(a.street) FROM Address a"),
	@NamedQuery(name="address.locate",     query="SELECT LOCATE(a.street, 'Arco Drive') FROM Address a"),
	@NamedQuery(name="address.size",       query="SELECT SIZE(c) FROM Address a JOIN a.customerList c"),
	@NamedQuery(name="address.substring",  query="SELECT SUBSTRING(a.state, 0, 1) FROM Address a"),
	@NamedQuery(name="address.collection", query="SELECT c FROM Address a JOIN a.customerList c")
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
	@OrderBy
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