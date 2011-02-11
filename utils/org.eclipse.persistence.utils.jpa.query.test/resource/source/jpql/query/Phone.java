package jpql.query;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries
({
  @NamedQuery(name="Phone.findAll", query="SELECT p FROM Phone p")
})
public class Phone implements Serializable
{
	@Id
	@Column(nullable = false)
	private Long id;
	@Column(name="PHONE_NUMBER")
	private String phoneNumber;
	private Long type;
	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID")
	private Customer customer;
	private String area;

	public Phone()
	{
		super();
	}

	public Phone(Customer customer,
	             Long id,
	             String phoneNumber,
	             Long type)
	{
		super();

		this.id = id;
		this.type = type;
		this.customer = customer;
		this.phoneNumber = phoneNumber;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getArea()
	{
		return area;
	}

	public void setArea(String area)
	{
		this.area = area;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public Long getType()
	{
		return type;
	}

	public void setType(Long type)
	{
		this.type = type;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}
}