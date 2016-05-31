package com.gregashby.challenge.oauth;

import static com.gregashby.challenge.MyApp.logger;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.RequestParameters;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.SignatureBaseString;

public class MyHmacSha1MessageSigner extends HmacSha1MessageSigner {
	
	private static final String MAC_NAME = "HmacSHA1";

	/**
	 * 
	 */
	private static final long serialVersionUID = 3934949978734179791L;

	@Override
	public String sign(HttpRequest request, RequestParameters requestParams) throws OAuthMessageSignerException {
		 try {
	            String keyString = OAuth.percentEncode(getConsumerSecret()) + '&'
	                    + OAuth.percentEncode(getTokenSecret());
	            byte[] keyBytes = keyString.getBytes(OAuth.ENCODING);

	            logger.info(">>>>> keyString: {}", keyString);
	            logger.info(">>>>> keyBytes: {}", keyBytes);
	            
	            
	            SecretKey key = new SecretKeySpec(keyBytes, MAC_NAME);
	            Mac mac = Mac.getInstance(MAC_NAME);
	            mac.init(key);

	            String sbs = new SignatureBaseString(request, requestParams).generate();

	            OAuth.debugOut("SBS", sbs);
	            byte[] text = sbs.getBytes(OAuth.ENCODING);

	            logger.info(">>>>> sbsString: {}", sbs);
	            logger.info(">>>>> sbsBytes: {}", text);
	            
	            String result = base64Encode(mac.doFinal(text)).trim();
	            
	            logger.info(">>>>> result: {}", result);
	            return result;
	        } catch (GeneralSecurityException e) {
	            throw new OAuthMessageSignerException(e);
	        } catch (UnsupportedEncodingException e) {
	            throw new OAuthMessageSignerException(e);
	        }

	}

}
