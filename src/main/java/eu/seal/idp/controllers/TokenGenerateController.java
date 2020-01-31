package eu.seal.idp.controllers;

import eu.seal.idp.service.EsmoMetadataService;
import eu.seal.idp.service.HttpSignatureService;
import eu.seal.idp.service.KeyStoreService;
import eu.seal.idp.service.NetworkService;
import eu.seal.idp.service.impl.HttpSignatureServiceImpl;
import eu.seal.idp.service.impl.NetworkServiceImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;	
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controllers managing Seal Authentication Source, used in the log in and SSO Callback
 */
@Controller
public class TokenGenerateController {

	private final static Logger LOG = LoggerFactory.getLogger(TokenGenerateController.class);

	private final NetworkService netServ;
	private final EsmoMetadataService metadataServ;
	private final KeyStoreService keyServ;

	@Autowired
	public TokenGenerateController(KeyStoreService keyServ,
			EsmoMetadataService metadataServ) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException, InvalidKeySpecException, IOException {
		this.metadataServ = metadataServ;
		this.keyServ = keyServ;
		Key signingKey = this.keyServ.getSigningKey();
		String fingerPrint = this.keyServ.getFingerPrint();
		HttpSignatureService httpSigServ = new HttpSignatureServiceImpl(fingerPrint, signingKey);
		this.netServ = new NetworkServiceImpl(httpSigServ);
	}
	
	/**
	 * Redirects an existing IDP request to the IDP 
	 */
	@RequestMapping(value = "/start", method = {RequestMethod.POST, RequestMethod.GET}, produces = "text/plain")
	public String startSession() throws KeyStoreException, NoSuchAlgorithmException, IOException {
		String sessionMngrUrl = System.getenv("SESSION_MANAGER_URL");
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		String rsp = netServ.sendPostForm(sessionMngrUrl, "/sm/startSession", requestParams, 1);
		return rsp;	
	}
	
	/**
	 * Redirects an existing IDP request to the IDP 
	 */
	@RequestMapping(value = "/generateToken", method = {RequestMethod.POST, RequestMethod.GET}, produces = "text/plain")
	public String generateToken(@RequestParam(value = "session", required = true) String sessionID, @RequestParam(value = "sender", required = true) String sender, @RequestParam(value = "receiver", required = true) String receiver,RedirectAttributes redirectAttrs) throws KeyStoreException, NoSuchAlgorithmException, IOException {
		String sessionMngrUrl = System.getenv("SESSION_MANAGER_URL");
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		requestParams.add(new NameValuePair("sessionId", sessionID));
		requestParams.add(new NameValuePair("sender", sender));
		requestParams.add(new NameValuePair("receiver", receiver));
		String rsp = netServ.sendPostForm(sessionMngrUrl, "/sm/generateToken", requestParams, 1);
		return rsp;	
	}
}
