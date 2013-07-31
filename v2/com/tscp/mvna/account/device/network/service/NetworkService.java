package com.tscp.mvna.account.device.network.service;

import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.device.network.NetworkInfo;
import com.tscp.mvna.account.device.network.NetworkStatus;
import com.tscp.mvna.account.device.network.exception.ConnectException;
import com.tscp.mvna.account.device.network.exception.DisconnectException;
import com.tscp.mvna.account.device.network.exception.InvalidEsnException;
import com.tscp.mvna.account.device.network.exception.NetworkQueryException;
import com.tscp.mvna.account.device.network.exception.ReserveException;
import com.tscp.mvna.account.device.network.exception.RestoreException;
import com.tscp.mvna.account.device.network.exception.SuspendException;
import com.tscp.mvno.webservices.AccessEqpAsgmInfo;
import com.tscp.mvno.webservices.AccessNbrAsgmInfo;
import com.tscp.mvno.webservices.ActivateReserveSubscription;
import com.tscp.mvno.webservices.ApiActivateReserveSubscriptionResponseHolder;
import com.tscp.mvno.webservices.ApiGeneralResponseHolder;
import com.tscp.mvno.webservices.ApiPendingSubscriptionNPAResponseHolder;
import com.tscp.mvno.webservices.ApiResellerSubInquiryResponseHolder;
import com.tscp.mvno.webservices.PendingSubscriptionNPA;
import com.tscp.mvno.webservices.Sali2;

public class NetworkService extends NetworkGateway {
	protected static final Logger logger = LoggerFactory.getLogger(NetworkService.class);
	protected static final DateTimeFormatter serviceDateFormat = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss.SSSZZ");

	protected void parseAccessNumbers(
			List<AccessNbrAsgmInfo> accessNumbers, NetworkInfo networkInfo) {

		AccessNbrAsgmInfo firstNumber = accessNumbers.get(0);

		networkInfo.setMdn(firstNumber.getAccessNbr());
		networkInfo.setMsid(firstNumber.getMSID());
		networkInfo.setStatus(NetworkStatus.valueOf(firstNumber.getSwitchStatusCd()));

		if (networkInfo.getStatus() == NetworkStatus.C)
			networkInfo.setExpirationDate(serviceDateFormat.parseDateTime(firstNumber.getAccessNbrAsgmEffDt() + " " + firstNumber.getAccessNbrAsgmEffTm()));
	}

	protected void parseAccessEquipment(
			List<AccessEqpAsgmInfo> accessEquipment, NetworkInfo networkInfo) {

		AccessEqpAsgmInfo firstEquipment = accessEquipment.remove(0);

		// matches first equipment in list
		if (networkInfo.getEsn().equals(firstEquipment.getESNMEIDDcmlId())) {
			networkInfo.setEffectiveDate(serviceDateFormat.parseDateTime(firstEquipment.getEqpEffDt() + " " + firstEquipment.getEqpEffTm()));
			networkInfo.getEsn().setDec(firstEquipment.getESNMEIDDcmlId());
			networkInfo.getEsn().setHex(firstEquipment.getESNMEIDHexId());
			return;
		}

		// matches historical equipment
		for (AccessEqpAsgmInfo equipment : accessEquipment) {
			if (networkInfo.getEsn().equals(equipment.getESNMEIDDcmlId()) && equipment.getEqpExprDt() != null && !equipment.getEqpExprDt().trim().isEmpty()) {
				networkInfo.setStatus(NetworkStatus.P);
				networkInfo.setEffectiveDate(serviceDateFormat.parseDateTime(equipment.getEqpEffDt() + " " + equipment.getEqpEffTm()));
				networkInfo.getEsn().setDec(equipment.getESNMEIDDcmlId());
				networkInfo.getEsn().setHex(equipment.getESNMEIDHexId());
				return;
			}
		}

	}

	protected NetworkInfo parseNetworkInfoResponse(
			ApiResellerSubInquiryResponseHolder sub, NetworkInfo networkInfo) throws NetworkQueryException {
		if (sub == null)
			throw new NetworkQueryException("NetworkInfo not found for " + networkInfo);

		boolean hasAccessNumbers = sub.getAccessNbrAsgmList() != null && sub.getAccessNbrAsgmList().getValue() != null && !sub.getAccessNbrAsgmList().getValue().isEmpty();
		boolean hasAccessEquipment = sub.getAccessEqpAsgmList() != null && sub.getAccessEqpAsgmList().getValue() != null && !sub.getAccessEqpAsgmList().getValue().isEmpty();

		if (hasAccessNumbers)
			parseAccessNumbers(sub.getAccessNbrAsgmList().getValue(), networkInfo);
		if (hasAccessEquipment)
			parseAccessEquipment(sub.getAccessEqpAsgmList().getValue(), networkInfo);

		return hasAccessNumbers || hasAccessEquipment ? networkInfo : null;
	}

	public NetworkInfo getNetworkInfoByMdn(
			String mdn) throws NetworkQueryException {
		NetworkInfo networkInfo = new NetworkInfo();
		networkInfo.setMdn(mdn = mdn == null ? mdn : mdn.trim());
		return parseNetworkInfoResponse(port.apIresellerV2SubInquiry(null, mdn), networkInfo);
	}

	public NetworkInfo getNetworkInfoByEsn(
			String esn) throws NetworkQueryException {
		try {
			NetworkInfo networkInfo = new NetworkInfo(esn = esn == null ? esn : esn.trim());
			return parseNetworkInfoResponse(port.apIresellerV2SubInquiry(esn, null), networkInfo);
		} catch (InvalidEsnException e) {
			throw new NetworkQueryException("ESN " + esn + " is not valid");
		}
	}

	public NetworkInfo reserveMdn() throws ReserveException {
		return reserveMdn(null, null, null);
	}

	public NetworkInfo reserveMdn(
			String csa, String pricePlan, List<String> socList) throws ReserveException {

		// TODO fix queryCsa or add addition CSA to a randomized picker
		pricePlan = pricePlan == null ? "PRSCARD5" : pricePlan;
		csa = csa == null ? "LAXLAX213" : csa;

		Sali2 sali2 = new Sali2();
		sali2.setSvcCode("PRSCARD5");

		PendingSubscriptionNPA pendingsubscription = new PendingSubscriptionNPA();
		pendingsubscription.setPricePlans(sali2);
		pendingsubscription.setCSA(csa);

		ApiPendingSubscriptionNPAResponseHolder response = port.apIreserveSubscriptionNPA(pendingsubscription);

		if (response == null || response.getSubNPA() == null || response.getSubNPA().getMDN() == null || response.getSubNPA().getMSID() == null)
			throw new ReserveException("No or incomplete response received");

		NetworkInfo networkInfo = new NetworkInfo();
		networkInfo.setMdn(response.getSubNPA().getMDN());
		networkInfo.setMsid(response.getSubNPA().getMSID());
		networkInfo.setStatus(NetworkStatus.R);

		logger.info("Reserved {}", networkInfo);

		return networkInfo;
	}

	public NetworkInfo connect(
			NetworkInfo networkInfo) throws ConnectException {

		if (networkInfo == null)
			throw new ConnectException("NetworkInfo is null and cannot be connected");
		if (!networkInfo.hasEsn())
			throw new ConnectException(networkInfo + " has no ESN assigned and cannot be activated");
		if (!networkInfo.hasMdn())
			throw new ConnectException(networkInfo + " has no MDN assigned and cannot be activated");
		if (!networkInfo.hasMsid())
			throw new ConnectException(networkInfo + " has no MSID assigned and cannot be activated");
		if (networkInfo.getStatus() == null || (networkInfo.getStatus() != NetworkStatus.R && networkInfo.getStatus() != NetworkStatus.P))
			throw new ConnectException(networkInfo + " is not in connectable status");

		logger.debug("Activating {}", networkInfo);

		ActivateReserveSubscription activateReserveSubscription = new ActivateReserveSubscription();
		activateReserveSubscription.setESN(networkInfo.getEsn().getValue());
		activateReserveSubscription.setMDN(networkInfo.getMdn());
		activateReserveSubscription.setMSID(networkInfo.getMsid());

		ApiActivateReserveSubscriptionResponseHolder response = port.apIactivatePendingSubscription(activateReserveSubscription);

		if (response == null)
			throw new ConnectException("No response received");
		if (!response.getStatusMessage().equals("SUCCEED"))
			throw new ConnectException("Error connecting " + networkInfo + ". responseMessage: " + response.getResponseMessage());

		networkInfo.setStatus(NetworkStatus.A);
		networkInfo.setStale(true);
		return networkInfo;
	}

	public NetworkInfo disconnect(
			NetworkInfo networkInfo) throws DisconnectException {

		if (networkInfo == null)
			throw new DisconnectException("NetworkInfo is null and cannot be disconnected");
		if (networkInfo.getStatus() == null || networkInfo.getStatus() == NetworkStatus.C || networkInfo.getExpirationDate() != null)
			throw new DisconnectException(networkInfo.toString() + " is not connected and cannot be disconnected");

		logger.debug("Disconnecting {}", networkInfo);

		ApiGeneralResponseHolder response = port.apIexpireSubscription(networkInfo.getMdn(), null);

		if (response == null)
			throw new DisconnectException("No response resceived");
		if (!response.getStatusMessage().equals("SUCCEED"))
			throw new DisconnectException("Error disconnecting " + networkInfo + ". apiResponseMessage: " + response.getApiResponseMessage() + ". responseMessage: " + response.getResponseMessage());

		networkInfo.setStatus(NetworkStatus.C);
		networkInfo.setStale(true);
		return networkInfo;
	}

	public NetworkInfo restoreService(
			NetworkInfo networkInfo) throws RestoreException {

		if (networkInfo == null)
			throw new RestoreException("NetworkInfo is null and cannot be restored");
		if (!networkInfo.hasMdn())
			throw new RestoreException(networkInfo + " has no MDN and cannot be restored");
		if (networkInfo.getStatus() == null || networkInfo.getStatus() != NetworkStatus.S)
			throw new RestoreException(networkInfo + " is not suspended and cannot be restored");

		logger.debug("Restoring {}", networkInfo);

		ApiGeneralResponseHolder response = port.apIrestoreSubscription(networkInfo.getMdn());

		if (response == null)
			throw new RestoreException("No response received");
		if (!response.getStatusMessage().equals("SUCCEED"))
			throw new RestoreException("Error restoring " + networkInfo + ". " + response.getResponseMessage());

		networkInfo.setStatus(NetworkStatus.A);
		networkInfo.setStale(true);
		return networkInfo;
	}

	public NetworkInfo suspendService(
			NetworkInfo networkInfo) throws SuspendException {

		if (networkInfo == null)
			throw new SuspendException("NetworkInfo is null and cannot be suspended");
		if (!networkInfo.hasMdn())
			throw new SuspendException(networkInfo + " has no MDN assigned and cannot be suspended");
		if (networkInfo.getStatus() == null || networkInfo.getStatus() != NetworkStatus.A)
			throw new SuspendException(networkInfo + " is not active and cannot be suspended");

		logger.debug("Suspending {}", networkInfo);

		// this value can be HTL to hotline but we're just going for full suspend.
		String suspendcode = null;
		ApiGeneralResponseHolder response = port.apIsuspendSubscription(networkInfo.getMdn(), suspendcode);

		if (response == null)
			throw new SuspendException("No response received");
		if (!response.getStatusMessage().equals("SUCCEED"))
			throw new SuspendException("Error suspending " + networkInfo + ". " + response.getResponseMessage());

		networkInfo.setStatus(NetworkStatus.S);
		networkInfo.setStale(true);
		return networkInfo;
	}
}
