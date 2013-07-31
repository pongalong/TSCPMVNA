package com.tscp.mvne.config;

import com.tscp.mvna.ws.InitializationException;

/**
 * ESN and MEID lenghts are static final because of switch statments in
 * TruConnect.class
 * 
 * @author Tachikoma
 * 
 */
public final class DEVICE extends CONFIG {
  public static String ACTIVE;
  public static String CANCELED;
  public static String SUSPENDED;
  public static String HOTLINED;
  public static String RESERVED;
  public static String SWAPPED;
  public static final int ESN_DEC_LENGTH = 11;
  public static final int ESN_HEX_LENGTH = 8;
  public static final int MEID_DEC_LENGTH = 18;
  public static final int MEID_HEX_LENGTH = 14;

  public static final void init() throws InitializationException {
    CONFIG.loadAll();
    try {
      ACTIVE = props.getProperty("device.status.active");
      CANCELED = props.getProperty("device.status.canceled");
      SUSPENDED = props.getProperty("device.status.suspended");
      HOTLINED = props.getProperty("device.status.hotlined");
      RESERVED = props.getProperty("device.status.reserved");
      SWAPPED = props.getProperty("device.status.swapped");
      // ESN_DEC = Integer.parseInt(props.getProperty("device.esn.dec.length"));
      // ESN_HEX = Integer.parseInt(props.getProperty("device.esn.hex.length"));
      // MEID_DEC =
      // Integer.parseInt(props.getProperty("device.meid.dec.length"));
      // MEID_HEX =
      // Integer.parseInt(props.getProperty("device.meid.hex.length"));
    } catch (NumberFormatException e) {
      e.printStackTrace();
      throw new InitializationException(e);
    }

  }

}
