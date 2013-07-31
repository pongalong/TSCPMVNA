package com.tscp.mvna.account.device.network.service;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.account.device.network.ConnectException;
import com.tscp.mvna.account.device.network.NetworkException;
import com.tscp.mvna.account.device.network.NetworkStatus;
import com.tscp.mvna.account.device.network.OldNetworkInfo;
import com.tscp.mvna.account.device.network.exception.DisconnectException;
import com.tscp.mvna.account.device.network.exception.NetworkQueryException;
import com.tscp.mvna.account.device.network.exception.ReserveException;
import com.tscp.mvna.account.device.network.exception.RestoreException;
import com.tscp.mvna.account.device.network.exception.SuspendException;
import com.tscp.mvna.account.device.network.exception.SwapException;
import com.tscp.mvne.config.DEVICE;
import com.tscp.mvne.network.service.NetworkGatewayProvider;
import com.tscp.mvno.webservices.API3;
import com.tscp.mvno.webservices.AccessEqpAsgmInfo;
import com.tscp.mvno.webservices.ActivateReserveSubscription;
import com.tscp.mvno.webservices.ApiActivateReserveSubscriptionResponseHolder;
import com.tscp.mvno.webservices.ApiGeneralResponseHolder;
import com.tscp.mvno.webservices.ApiPendingSubscriptionNPAResponseHolder;
import com.tscp.mvno.webservices.ApiResellerSubInquiryResponseHolder;
import com.tscp.mvno.webservices.ApiSwapESNResponseHolder;
import com.tscp.mvno.webservices.PendingSubscriptionNPA;
import com.tscp.mvno.webservices.Sali2;

@Deprecated
public class OldNetworkService {
	private static Logger logger = LoggerFactory.getLogger("TSCPMVNA");
	protected static final API3 port = NetworkGatewayProvider.getInstance();

	public OldNetworkInfo reserveAndConnect(
			OldNetworkInfo networkInfo) throws DisconnectException {

		if (networkInfo.getStatus() != null && (networkInfo.getStatus() != NetworkStatus.R || networkInfo.getStatus() != NetworkStatus.C))
			throw new ConnectException("Device is already active with MDN " + networkInfo.getMdn());

		try {
			OldNetworkInfo newNetworkInfo = reserveMdn();
			newNetworkInfo.setEsnmeiddec(networkInfo.getEsnmeiddec());
			try {
				newNetworkInfo = connect(newNetworkInfo);
				return newNetworkInfo;
			} catch (ConnectException e) {
				logger.error(e.getMessage());
				disconnect(newNetworkInfo);
				throw new ConnectException("Could not activate " + newNetworkInfo.toString() + ". " + e.getMessage());
			}

		} catch (ReserveException e) {
			throw new ConnectException("Could not reserve MDN. " + e.getMessage());
		}
	}

	public OldNetworkInfo reserveMdn() throws ReserveException {
		return reserveMdn(null, null, null);
	}

	public OldNetworkInfo reserveMdn(
			String csa, String pricePlan, List<String> socList) throws ReserveException {

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

		OldNetworkInfo networkInfo = new OldNetworkInfo();
		networkInfo.setMdn(response.getSubNPA().getMDN());
		networkInfo.setMsid(response.getSubNPA().getMSID());
		networkInfo.setStatus(NetworkStatus.R);

		logger.info("Reserved {}", networkInfo);

		return networkInfo;
	}

	public OldNetworkInfo connect(
			OldNetworkInfo networkInfo) throws ConnectException {

		if (networkInfo == null)
			throw new ConnectException("NetworkInfo is null and cannot be connected");
		if (!networkInfo.hasEsn())
			throw new ConnectException(networkInfo + " has no ESN assigned and cannot be activated");
		if (!networkInfo.hasMdn())
			throw new ConnectException(networkInfo + " has no MDN assigned and cannot be activated");
		if (!networkInfo.hsMsid())
			throw new ConnectException(networkInfo + " has no MSID assigned and cannot be activated");
		if (networkInfo.getStatus() == null || (networkInfo.getStatus() != NetworkStatus.R && networkInfo.getStatus() != NetworkStatus.P))
			throw new ConnectException(networkInfo + " is not in connectable status");

		logger.debug("Activating {}", networkInfo);

		ActivateReserveSubscription activateReserveSubscription = new ActivateReserveSubscription();
		activateReserveSubscription.setESN(networkInfo.getEsnmeiddec() != null && !networkInfo.getEsnmeiddec().isEmpty() ? networkInfo.getEsnmeiddec() : networkInfo.getEsnmeidhex());
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

	public OldNetworkInfo disconnect(
			OldNetworkInfo networkInfo) throws DisconnectException {

		if (networkInfo == null)
			throw new DisconnectException("NetworkInfo is null and cannot be disconnected");
		if (networkInfo.getStatus() == null || networkInfo.getStatus() == NetworkStatus.C || networkInfo.getExpirationdate() != null)
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

	public OldNetworkInfo restoreService(
			OldNetworkInfo networkInfo) throws RestoreException {

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

	public OldNetworkInfo suspendService(
			OldNetworkInfo networkInfo) throws SuspendException {

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

	public OldNetworkInfo getNetworkInfo(
			String esn, String mdn) throws NetworkQueryException {

		esn = esn == null ? esn : esn.trim();
		mdn = mdn == null ? mdn : mdn.trim();

		boolean hasEsn = esn != null && !esn.isEmpty();
		boolean hasMdn = mdn != null && !mdn.isEmpty();

		if (!hasEsn && !hasMdn)
			throw new NetworkQueryException("ESN or MDN required");
		if (hasEsn && hasMdn)
			throw new NetworkQueryException("Method require an ESN or MDN, not both");

		OldNetworkInfo networkInfo = new OldNetworkInfo();
		networkInfo.setEsnmeiddec(esn);
		networkInfo.setMdn(mdn);

		ApiResellerSubInquiryResponseHolder sub = port.apIresellerV2SubInquiry(esn, mdn);

		if (sub == null)
			throw new NetworkQueryException("NetworkInfo not found for " + networkInfo);

		boolean hasAccessNumbers = sub.getAccessNbrAsgmList() != null && sub.getAccessNbrAsgmList().getValue() != null && !sub.getAccessNbrAsgmList().getValue().isEmpty();
		if (hasAccessNumbers) {
			NetworkStatus status = NetworkStatus.valueOf(sub.getAccessNbrAsgmList().getValue().get(0).getSwitchStatusCd());
			networkInfo.setMdn(sub.getAccessNbrAsgmList().getValue().get(0).getAccessNbr());
			networkInfo.setMsid(sub.getAccessNbrAsgmList().getValue().get(0).getMSID());
			networkInfo.setStatus(status);
			if (status == NetworkStatus.C) {
				networkInfo.setExpirationdate(sub.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmEffDt());
				networkInfo.setExpirationtime(sub.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmEffTm());
			}
		}

		boolean hasEquipment = sub.getAccessEqpAsgmList() != null && sub.getAccessEqpAsgmList().getValue() != null && !sub.getAccessEqpAsgmList().getValue().isEmpty();
		if (hasEquipment) {

			if (hasEsn) {
				AccessEqpAsgmInfo equipment = null;
				String equipmentEsn = null;

				for (int i = 0; i < sub.getAccessEqpAsgmList().getValue().size(); i++) {
					equipment = (AccessEqpAsgmInfo) sub.getAccessEqpAsgmList().getValue().get(i);
					equipmentEsn = equipment.getESNMEIDDcmlId();

					if (equipmentEsn != null && equipmentEsn.equals(esn)) {
						if (i > 0 && equipment.getEqpExprDt() != null && !equipment.getEqpExprDt().trim().isEmpty())
							networkInfo.setStatus(NetworkStatus.P);
						networkInfo.setEffectivedate(equipment.getEqpEffDt());
						networkInfo.setEffectivetime(equipment.getEqpEffTm());
						networkInfo.setEsnmeiddec(equipment.getESNMEIDDcmlId());
						networkInfo.setEsnmeidhex(equipment.getESNMEIDHexId());
						break;
					}

				}

			} else {
				networkInfo.setEffectivedate(sub.getAccessEqpAsgmList().getValue().get(0).getEqpEffDt());
				networkInfo.setEffectivetime(sub.getAccessEqpAsgmList().getValue().get(0).getEqpEffTm());
				networkInfo.setEsnmeiddec(sub.getAccessEqpAsgmList().getValue().get(0).getESNMEIDDcmlId());
				networkInfo.setEsnmeidhex(sub.getAccessEqpAsgmList().getValue().get(0).getESNMEIDHexId());
			}

		}

		return hasAccessNumbers || hasEquipment ? networkInfo : null;
	}

	@Deprecated
	public OldNetworkInfo getNetworkInfo_old(
			String esn, String mdn) throws NetworkException {
		if ((esn == null || esn.length() == 0) && (mdn == null || mdn.length() == 0)) {
			throw new NetworkException("getNetworkInfo", "ESN or an MDN required");
		} else if (esn != null && esn.length() > 0 && mdn != null && mdn.length() > 0) {
			throw new NetworkException("getNetworkInfo", "Only an ESN or an MDN may be used.");
		} else {
			logger.trace("BEGIN => Network informational query for " + (esn == null ? "MDN :: " + mdn : "ESN :: " + esn));
			OldNetworkInfo networkinfo = new OldNetworkInfo();
			networkinfo.setEsnmeiddec(esn.trim());
			networkinfo.setMdn(mdn.trim());
			ApiResellerSubInquiryResponseHolder subscription = port.apIresellerV2SubInquiry(esn.trim(), mdn.trim());
			if (subscription != null) {
				if (subscription.getAccessNbrAsgmList() != null && subscription.getAccessNbrAsgmList().getValue() != null && subscription.getAccessNbrAsgmList().getValue().size() >= 1) {
					if (subscription.getAccessNbrAsgmList().getValue().get(0).getSwitchStatusCd().equals("C")) {
						networkinfo.setExpirationdate(subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmEffDt());
						networkinfo.setExpirationtime(subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmEffTm());
					}
					networkinfo.setMdn(subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbr());
					networkinfo.setMsid(subscription.getAccessNbrAsgmList().getValue().get(0).getMSID());
					networkinfo.setStatus(NetworkStatus.valueOf(subscription.getAccessNbrAsgmList().getValue().get(0).getSwitchStatusCd()));
				}

				if (subscription.getAccessEqpAsgmList() != null && subscription.getAccessEqpAsgmList().getValue() != null && subscription.getAccessEqpAsgmList().getValue().size() >= 1) {
					networkinfo.setEffectivedate(subscription.getAccessEqpAsgmList().getValue().get(0).getEqpEffDt());
					networkinfo.setEffectivetime(subscription.getAccessEqpAsgmList().getValue().get(0).getEqpEffTm());
					networkinfo.setEsnmeiddec(subscription.getAccessEqpAsgmList().getValue().get(0).getESNMEIDDcmlId());
					networkinfo.setEsnmeidhex(subscription.getAccessEqpAsgmList().getValue().get(0).getESNMEIDHexId());
				}
				logger.debug("MDN      :: " + networkinfo.getMdn());
				logger.debug("MSID     :: " + networkinfo.getMsid());
				logger.debug("EffDate  :: " + networkinfo.getEffectivedate());
				logger.debug("EffTime  :: " + networkinfo.getEffectivetime());
				logger.debug("ExpDate  :: " + networkinfo.getExpirationdate());
				logger.debug("ExpTime  :: " + networkinfo.getExpirationtime());
				logger.debug("ESNDec   :: " + networkinfo.getEsnmeiddec());
				logger.debug("ESNHex   :: " + networkinfo.getEsnmeidhex());
				logger.debug("Status   :: " + networkinfo.getStatus());
				logger.trace("DONE => Network informational query for " + (esn == null ? "MDN :: " + mdn : "ESN :: " + esn));
				return networkinfo;
			} else {
				NetworkException networkexception = new NetworkException("getNetworkInfo", "Subscriber not found for " + (networkinfo.getEsnmeiddec() == null || networkinfo.getEsnmeiddec().length() == 0 ? " ESN " + networkinfo.getEsnmeiddec() : " MDN " + networkinfo.getMdn()));
				networkexception.setNetworkinfo(networkinfo);
				logger.warn("Subscriber not found for " + (networkinfo.getEsnmeiddec() == null || networkinfo.getEsnmeiddec().length() == 0 ? " ESN " + networkinfo.getEsnmeiddec() : " MDN " + networkinfo.getMdn()));
				logger.trace("DONE => Network informational query for " + (esn == null ? "MDN :: " + mdn : "ESN :: " + esn));
				throw networkexception;
			}
		}
	}

	public OldNetworkInfo getSwapNetworkInfo(
			String esn, String mdn) throws NetworkException {
		if ((esn == null || esn.length() == 0)) {
			throw new NetworkException("getSwapNetworkInfo", "ESN required");
		} else {
			logger.trace("BEGIN => Network informational query for swapping " + "ESN :: " + esn);
			esn = esn.trim();
			OldNetworkInfo networkinfo = new OldNetworkInfo();
			switch (esn.length()) {
				case DEVICE.ESN_HEX_LENGTH:
				case DEVICE.MEID_HEX_LENGTH:
					networkinfo.setEsnmeidhex(esn);
					break;
				case DEVICE.ESN_DEC_LENGTH:
				case DEVICE.MEID_DEC_LENGTH:
					networkinfo.setEsnmeiddec(esn);
					break;
				default:
					throw new NetworkException("Invalid ESN length");
			}

			ApiResellerSubInquiryResponseHolder subscription = port.apIresellerV2SubInquiry(esn, null);
			if (subscription != null) {
				if (subscription.getAccessNbrAsgmList() != null && subscription.getAccessNbrAsgmList().getValue() != null && subscription.getAccessNbrAsgmList().getValue().size() >= 1) {
					if (subscription.getAccessNbrAsgmList().getValue().get(0).getSwitchStatusCd().equals("C")) {
						networkinfo.setExpirationdate(subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmEffDt());
						networkinfo.setExpirationtime(subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmEffTm());
					}
					networkinfo.setMdn(subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbr());
					networkinfo.setMsid(subscription.getAccessNbrAsgmList().getValue().get(0).getMSID());
					networkinfo.setStatus(NetworkStatus.valueOf(subscription.getAccessNbrAsgmList().getValue().get(0).getSwitchStatusCd()));
				}

				if (subscription.getAccessEqpAsgmList() != null && subscription.getAccessEqpAsgmList().getValue() != null && subscription.getAccessEqpAsgmList().getValue().size() >= 1) {
					Iterator<AccessEqpAsgmInfo> iter = subscription.getAccessEqpAsgmList().getValue().iterator();
					int iteration = 0;
					while (iter.hasNext()) {
						AccessEqpAsgmInfo accessEqp = (AccessEqpAsgmInfo) iter.next();
						String getESN = accessEqp.getESNMEIDDcmlId();
						if ((getESN != null) && (getESN.equals(esn))) {
							if ((iteration > 0) && (accessEqp.getEqpExprDt() != null) && (!accessEqp.getEqpExprDt().trim().isEmpty())) {
								networkinfo.setStatus(NetworkStatus.P);
							}
							networkinfo.setEffectivedate(accessEqp.getEqpEffDt());
							networkinfo.setEffectivetime(accessEqp.getEqpEffTm());
							networkinfo.setEsnmeiddec(accessEqp.getESNMEIDDcmlId());
							networkinfo.setEsnmeidhex(accessEqp.getESNMEIDHexId());
							break;
						}
						iteration++;
					}
				}
				logger.trace("DONE => Network informational query for swapping ESN :: " + esn);
				return networkinfo;
			} else {
				NetworkException networkexception = new NetworkException("getSwapNetworkInfo", "Subscriber not found for " + " ESN " + esn);
				networkexception.setNetworkinfo(networkinfo);
				logger.warn("Subscriber not found for ESN " + esn);
				logger.trace("DONE => Network informational query for swapping ESN :: " + esn);
				throw networkexception;
			}
		}
	}

	public void swapESN(
			OldNetworkInfo fromNetworkInfo, OldNetworkInfo targetNetworkInfo) throws SwapException {

		if (fromNetworkInfo == null)
			throw new SwapException("oldNetworkInfo is null and cannot be swapped");
		if (targetNetworkInfo == null)
			throw new SwapException("newNetworkInfo is null and cannot be swapped");
		if (!fromNetworkInfo.hasMdn())
			throw new SwapException("oldNetworkInfo has no MDN and cannot be swapped");
		if (!targetNetworkInfo.hasEsn())
			throw new SwapException("newNetworkInfo has no ESN and cannot be swapped");
		if (fromNetworkInfo.getStatus() != NetworkStatus.A)
			throw new SwapException(fromNetworkInfo + " is not active and cannot be swapped to " + targetNetworkInfo);

		String targetEsn = "";
		if (targetNetworkInfo.getEsnmeiddec() != null) {
			targetEsn = targetNetworkInfo.getEsnmeiddec();
			if (targetEsn.length() != DEVICE.ESN_DEC_LENGTH && targetEsn.length() != DEVICE.MEID_DEC_LENGTH)
				throw new NetworkException("Dec ESN is not of a valid length");
		} else if (targetNetworkInfo.getEsnmeidhex() != null) {
			targetEsn = targetNetworkInfo.getEsnmeidhex();
			if (targetEsn.length() != DEVICE.ESN_HEX_LENGTH && targetEsn.length() != DEVICE.MEID_HEX_LENGTH)
				throw new NetworkException("Hex ESN is not of a valid length");
		}

		ApiSwapESNResponseHolder response = port.apIswapESN(fromNetworkInfo.getMdn(), targetEsn);

		if (response == null)
			throw new SwapException("No response recieved");
		if (!response.getStatusMessage().equals("SUCCEED"))
			throw new SwapException("Error swapping from " + fromNetworkInfo + " to " + targetNetworkInfo + ". " + response.getResponseMessage());
		if (!response.getMSID().equals(fromNetworkInfo.getMsid()))
			throw new SwapException("Error swapping from " + fromNetworkInfo + " to " + targetNetworkInfo + ". MSID did not match.");

		targetNetworkInfo.setMdn(fromNetworkInfo.getMdn());
		targetNetworkInfo.setMsid(response.getMSID());
		targetNetworkInfo.setStatus(NetworkStatus.A);
	}
}