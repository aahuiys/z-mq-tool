package pers.ys.jms.mq;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;



/**
 * ApacheHTTPSimulate apache httpcomponents 项目，解决 http协议模拟器
 * 
 * @author heJun
 * @sine 2015年5月5日 上午6:13:30
 */
public final class ApacheHTTPSimulate {
	public static Logger webLogger = Logger.getLogger("webserviceLogger");

	/**
	 * 发送Http请求。默认UTF-8字符集
	 * 
	 * @param type 发送请求类型 POST & GET
	 * @param url 请求地址
	 * @param params 参数
	 * @return
	 * @throws RedInfoSecurityException
	 */
	public static String sendHttp(String type, String url, Map<String, String> params) throws Exception {
		String ret = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			URI uri = null;
			CloseableHttpResponse response = null;
			List<NameValuePair> paramsLs = new ArrayList<NameValuePair>();
			URIBuilder u = new URIBuilder().setPath(url).setCharset(CharsetUtils.get("UTF-8"));
            String meg="";
			/** 参数组合 */
			for (Iterator<String> key = params.keySet().iterator(); key.hasNext();) {
				String k = key.next();
				paramsLs.add(new BasicNameValuePair(k, params.get(k)));
				meg+=k+":"+params.get(k);
			}
			

			/** 创建连接 */
			if (type.equals("GET")) {
				u.setParameters(paramsLs);
				uri = u.build();
				HttpGet httpget = new HttpGet(uri);
				response = httpclient.execute(httpget);
			} else if (type.equals("POST")) {
				StringEntity entity = new UrlEncodedFormEntity(paramsLs, "utf-8");
				uri = u.build();
				HttpPost httppost = new HttpPost(uri);
				httppost.setEntity(entity);
				response = httpclient.execute(httppost);
			}

			/** 获取返回参数 */
			try {
				HttpEntity entity1 = response.getEntity();
				ret = EntityUtils.toString(entity1);
				ConfigUtil.printLog(webLogger, "w", "调用url"+url+"参数"+meg+"调用返回"+ret);
			} finally {
				response.close();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
			}
		}
		return ret;
	}

	/**
	 * 发送Http请求。
	 * 
	 * @param type 发送请求类型 POST & GET
	 * @param url 请求地址
	 * @param params 参数
	 * @param charset 参数传递字符集
	 * @return
	 * @throws RedInfoSecurityException
	 */
	public static String sendHttp(String type, String url, Map<String, String> params, String charset) throws Exception {
		String ret = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			URI uri = null;
			CloseableHttpResponse response = null;
			List<NameValuePair> paramsLs = new ArrayList<NameValuePair>();
			URIBuilder u = new URIBuilder().setPath(url).setCharset(CharsetUtils.get(charset));

			/** 参数组合 */
			for (Iterator<String> key = params.keySet().iterator(); key.hasNext();) {
				String k = key.next();
				paramsLs.add(new BasicNameValuePair(k, params.get(k)));
			}

			/** 创建连接 */
			if (type == "get") {
				u.setParameters(paramsLs);
				uri = u.build();
				HttpGet httpget = new HttpGet(uri);
				response = httpclient.execute(httpget);
			} else if (type == "post") {
				StringEntity entity = new UrlEncodedFormEntity(paramsLs, charset);
				uri = u.build();
				HttpPost httppost = new HttpPost(uri);
				httppost.setEntity(entity);
				response = httpclient.execute(httppost);
			}

			/** 获取返回参数 */
			try {
				HttpEntity entity1 = response.getEntity();
				ret = EntityUtils.toString(entity1);
			} finally {
				response.close();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
			}
		}
		return ret;
	}
}
