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
  @NamedQuery(name="phone.findAll", query="SELECT p FROM Phone p")
})
public class Phone implements Serializable
{
	private String area;
	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID")
	private Customer customer;
	@ManyToOne
	@JoinColumn(name = "EMPLOYEE_ID")
	private Employee employee;
	@Id
	@Column(nullable = false)
	private Long id;
	@Column(name="PHONE_NUMBER")
	private String phoneNumber;
	private Long type;

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

	public String getArea()
	{
		return area;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public Employee getEmployee()
	{
		return employee;
	}

	public Long getId()
	{
		return id;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public Long getType()
	{
		return type;
	}

	public void setArea(String area)
	{
		this.area = area;
	}

	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}

	public void setEmployee(Employee employee)
	{
		this.employee = employee;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public void setType(Long type)
	{
		this.type = type;
	}
}