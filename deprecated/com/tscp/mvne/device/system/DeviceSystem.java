package com.tscp.mvne.device.system;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.tscp.mvna.dao.hibernate.HibernateUtil;
import com.tscp.mvne.device.OldDevice;

public class DeviceSystem {

  public List<OldDevice> getDevices(int custId, int deviceId, int accountNo) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    Query q = session.getNamedQuery("fetch_device_info");
    q.setParameter("in_cust_id", custId);
    q.setParameter("in_device_id", deviceId);
    q.setParameter("in_account_no", accountNo);
    List<OldDevice> devices = q.list();
    session.getTransaction().rollback();
    return devices;
  }

}