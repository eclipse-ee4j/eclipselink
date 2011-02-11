package jpql.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@NamedQueries
({
   @NamedQuery(name="Customer.findAll",   query="SELECT c FROM Customer c"),
   @NamedQuery(name="customer.name",      query="select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)"),
   @NamedQuery(name="customer.substring", query="select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)"),
   @NamedQuery(name="customer.area",      query="SELECT Distinct Object(c) From Customer c, IN(c.home.phones) p where p.area LIKE :area"),
   @NamedQuery(name="customer.city",      query="SELECT c from Customer c where c.home.city IN(:city)"),
   @NamedQuery(name="customer.new",       query="SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c")
})
public class Customer implements Serializable
{
	@Column(name="FIRST_NAME")
	private String firstName;
	@Column(name="HAS_GOOD_CREDIT")
	private Long hasGoodCredit;
	@Id
	@Column(nullable = false)
	private Long id;
	@Column(name="LAST_NAME")
	private String lastName;
	@ManyToOne
	@JoinColumn(name = "ADDRESS_ID")
	private Address address;
	@OneToMany(mappedBy = "customer")
	private List<Phone> phoneList;
	@OneToMany(mappedBy = "customer")
	private List<Alias> aliases;
	private Home home;
	@Transient
	private String title;

	public Customer()
	{
		super();
	}

	public Customer(Address address,
	                String firstName,
	                Long hasGoodCredit,
	                Long id,
						 String lastName)
	{
		super();

		this.id            = id;
		this.address       = address;
		this.lastName      = lastName;
		this.firstName     = firstName;
		this.hasGoodCredit = hasGoodCredit;
		this.phoneList     = new ArrayList<Phone>();
		this.aliases       = new ArrayList<Alias>();
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public Home getHome()
	{
		return home;
	}

	public void setHome(Home home)
	{
		this.home = home;
	}

	public Long getHasGoodCredit()
	{
		return hasGoodCredit;
	}

	public void setHasGoodCredit(Long hasGoodCredit)
	{
		this.hasGoodCredit = hasGoodCredit;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public Address getAddress()
	{
		return address;
	}

	public List<Alias> getAliases()
	{
		return aliases;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}

	public List<Phone> getPhoneList()
	{
		return phoneList;
	}

	public void setPhoneList(List<Phone> phoneList)
	{
		this.phoneList = phoneList;
	}

	public Phone addPhone(Phone phone)
	{
		getPhoneList().add(phone);
		phone.setCustomer(this);
		return phone;
	}

	public Phone removePhone(Phone phone)
	{
		getPhoneList().remove(phone);
		phone.setCustomer(null);
		return phone;
	}

	public Alias addAlias(Alias alias)
	{
		getAliases().add(alias);
		return alias;
	}

	public Alias removePhone(Alias alias)
	{
		getAliases().remove(alias);
		return alias;
	}

	public String getTitle()
	{
		return title;
	}
}