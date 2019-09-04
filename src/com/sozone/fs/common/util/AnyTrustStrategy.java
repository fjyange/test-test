package com.sozone.fs.common.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.TrustStrategy;


public class AnyTrustStrategy implements TrustStrategy
{

	@Override
	public boolean isTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException
	{
		return true;
	}

}
