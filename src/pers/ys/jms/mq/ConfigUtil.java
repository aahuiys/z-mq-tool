package pers.ys.jms.mq;

import org.apache.log4j.Logger;

import com.xinhuanet.api.XinhuanetAPI;

public class ConfigUtil {
	public static final String URL_HEAD = "http://106.14.92.38/webswap/";
	public static final String isvEntId ="17c4656c-d0ea-11e6-95e5-28f10e23505a";
	public static final int isTest=0;
	public static final String entId ="3307010001";
	public static final String keyCertNo="Wh9oPkRdwt5ot0tu5jrXBg==";
	public static final String key="v3WEPVitbKydb5lnVOddCQ==";

	public static void printLog(Logger log, String flag, String printLog) {

		if ("e".toLowerCase().equals(flag)) {
			log.error(printLog);
		} else if ("w".toLowerCase().equals(flag)) {
			log.warn(printLog);
		} else if ("d".toLowerCase().equals(flag)) {
			log.debug(printLog);
		} else {
			log.info(printLog);
		}
	}
}
