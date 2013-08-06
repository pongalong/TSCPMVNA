@XmlJavaTypeAdapters({ @XmlJavaTypeAdapter(type = DateTime.class, value = DateTimeAdapter.class) })
package com.tscp.mvna.account.payment;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.joda.time.DateTime;

import com.tscp.jaxb.xml.adapter.DateTimeAdapter;
