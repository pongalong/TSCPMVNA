package com.tscp.mvna.account.device;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.Account;
import com.tscp.mvna.account.device.network.NetworkDevice;
import com.tscp.mvna.account.device.network.NetworkEsn;
import com.tscp.mvna.account.device.network.NetworkInfo;
import com.tscp.mvna.account.device.network.NetworkStatus;
import com.tscp.mvna.account.device.network.exception.ConnectException;
import com.tscp.mvna.account.device.network.exception.DisconnectException;
import com.tscp.mvna.account.device.network.exception.InvalidEsnException;
import com.tscp.mvna.account.device.network.exception.ReserveException;
import com.tscp.mvna.account.device.network.exception.RestoreException;
import com.tscp.mvna.account.device.network.exception.SuspendException;
import com.tscp.mvna.dao.hibernate.HibernateUtil;
import com.tscp.mvna.payment.method.CreditCard;
import com.tscp.mvna.user.customer.Customer;

@XmlSeeAlso({ DeviceAndService.class })
@MappedSuperclass
public abstract class Device extends NetworkDevice {
	private static final Logger logger = LoggerFactory.getLogger(Device.class);
	protected int id;
	protected String value;
	protected String name;
	protected Customer customer;
	protected Account account;
	protected CreditCard paymentMethod;
	protected List<DeviceHistory> history;

	/* **************************************************
	 * Network Methods
	 */

	@Override
	@Transient
	protected void reserve() throws ReserveException {
		if (networkInfo == null || networkInfo.isStale())
			refreshNetwork();
		super.reserve();

		try {
			networkInfo.setEsn(new NetworkEsn(value));
		} catch (InvalidEsnException e) {
			try {
				super.disconnect();
			} catch (DisconnectException e1) {
				throw new ReserveException("Unable to disconnect reserved MDN: " + e.getMessage());
			}
			throw new ReserveException("Unable to reserve MDN: " + e.getMessage());
		}

		updateHistory();

	}

	@Override
	@Transient
	public void connect() throws ConnectException {
		if (networkInfo == null || networkInfo.isStale() || networkInfo.getStatus() == NetworkStatus.C) {
			refreshNetwork();
			try {
				reserve();
			} catch (ReserveException e) {
				throw new ConnectException("Unable to connect device: " + e.getMessage());
			}
		}
		super.connect();
		updateHistory();
	}

	@Override
	@Transient
	public void disconnect() throws DisconnectException {
		super.disconnect();
		updateHistory();
	}

	@Override
	@Transient
	public void suspend() throws SuspendException {
		super.suspend();
		updateHistory();
	}

	@Override
	@Transient
	public void restore() throws RestoreException {
		super.restore();
		updateHistory();
	}

	@Override
	public void refreshNetwork() {
		logger.trace("Refreshing network {}", this);
		super.refreshByESN(value);
	}

	@Override
	@Transient
	public NetworkInfo getNetworkInfo() {
		if (!loadedNetworkInfo)
			refreshNetwork();
		return networkInfo;
	}

	/* **************************************************
	 * Status and Update Methods
	 */

	@Transient
	public void update() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.update(this);
		tx.commit();
	}

	@Transient
	protected void updateHistory() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(new DeviceHistory(this));
		tx.commit();
	}

	@Transient
	@XmlAttribute
	public NetworkStatus getStatus() {
		if (networkInfo == null)
			refreshNetwork();
		return networkInfo == null ? null : networkInfo.getStatus();
	}

	/* **************************************************
	 * Getters and Setters
	 */

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DEVICE_ID_GEN")
	@SequenceGenerator(name = "DEVICE_ID_GEN", sequenceName = "DEVICE_ID_SEQ")
	@XmlAttribute
	public int getId() {
		return id;
	}

	public void setId(
			int id) {
		this.id = id;
	}

	@Column(name = "VALUE")
	@XmlAttribute
	public String getValue() {
		return value;
	}

	public void setValue(
			String value) {
		this.value = value;
	}

	@Column(name = "NAME")
	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(
			String name) {
		this.name = name;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", referencedColumnName = "id")
	@XmlTransient
	public List<DeviceHistory> getHistory() {
		return history;
	}

	public void setHistory(
			List<DeviceHistory> history) {
		this.history = history;
	}

	@Transient
	@XmlTransient
	public Customer getCustomer() {
		if (customer == null && account != null)
			customer = getAccount().getCustomer();
		return customer;
	}

	public void setCustomer(
			Customer customer) {
		this.customer = customer;
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "DEVICE_MAP", joinColumns = @JoinColumn(name = "DEVICE_ID"), inverseJoinColumns = @JoinColumn(name = "ACCOUNT_NO"))
	@XmlTransient
	public Account getAccount() {
		return account;
	}

	public void setAccount(
			Account account) {
		this.account = account;
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "DEVICE_PMT_MAP", joinColumns = @JoinColumn(name = "DEVICE_ID"), inverseJoinColumns = @JoinColumn(name = "PMT_ID"))
	@XmlTransient
	public CreditCard getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(
			CreditCard paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/* **************************************************
	 * XML Helper Methods
	 */

	@Transient
	@XmlAttribute
	public int getCustomerId() {
		if (getCustomer() != null)
			return customer.getId();
		return 0;
	}

	@Transient
	@XmlAttribute
	public int getAccountNo() {
		if (getAccount() != null)
			return account.getAccountNo();
		return 0;
	}

	@Transient
	@XmlAttribute
	public int getPaymentMethodId() {
		if (getPaymentMethod() != null)
			return paymentMethod.getId();
		return 0;
	}

	/* **************************************************
	 * Debug Methods
	 */

	@Override
	public String toString() {
		return "NewDevice [id=" + id + ", value=" + value + ", name=" + name + "]";
	}
}