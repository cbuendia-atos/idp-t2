/*
 * Copyright 2019 Vincenzo De Notaris
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package eu.seal.idp.controllers;

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
import org.opensaml.saml2.core.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.seal.idp.model.factories.AttributeTypeFactory;
import eu.seal.idp.model.pojo.AttributeType;
import eu.seal.idp.model.pojo.CurrentUser;
import eu.seal.idp.model.pojo.DataSet;
import eu.seal.idp.model.pojo.DataStore;
import eu.seal.idp.model.pojo.SessionMngrResponse;
import eu.seal.idp.service.EsmoMetadataService;
import eu.seal.idp.service.HttpSignatureService;
import eu.seal.idp.service.KeyStoreService;
import eu.seal.idp.service.NetworkService;
import eu.seal.idp.service.impl.HttpSignatureServiceImpl;
import eu.seal.idp.service.impl.NetworkServiceImpl;
import eu.seal.idp.service.impl.SAMLDatasetDetailsServiceImpl;

@Controller
public class CallbackController {
	
	private final NetworkService netServ;
	private final KeyStoreService keyServ;
	// Logger
	private static final Logger LOG = LoggerFactory
			.getLogger(CallbackController.class);
	
	@Autowired
	public CallbackController(KeyStoreService keyServ,
			EsmoMetadataService metadataServ) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException, InvalidKeySpecException, IOException {
		this.keyServ = keyServ;
		Key signingKey = this.keyServ.getSigningKey();
		String fingerPrint = this.keyServ.getFingerPrint();
		HttpSignatureService httpSigServ = new HttpSignatureServiceImpl(fingerPrint, signingKey);
		this.netServ = new NetworkServiceImpl(httpSigServ);
	}
	
	/**
	 * Manages SAML success callback (mapped from /saml/SSO callback) and writes to the DataStore
	 * @param session 
	 * @param authentication
	 * @param model
	 * @param redirectAttrs
	 * @return 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	
	@RequestMapping("/callback")
	@ResponseBody
	public String callback(@RequestParam(value = "session", required = true) String sessionId, Authentication authentication) throws NoSuchAlgorithmException, IOException {
		authentication.getDetails();
		SAMLCredential credentials = (SAMLCredential) authentication.getCredentials();		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String sessionMngrUrl = System.getenv("SESSION_MANAGER_URL");
		
		// Request Session Data
		List<NameValuePair> requestParams = new ArrayList<>();
		requestParams.add(new NameValuePair("sessionId", sessionId));
		String clearResp = netServ.sendGet(sessionMngrUrl, "/sm/getSessionData",requestParams, 1);
		
		// Recover Session ID
		SessionMngrResponse resp = (new ObjectMapper()).readValue(clearResp, SessionMngrResponse.class);
		String recoveredSessionID = resp.getSessionData().getSessionId(); 
		
		// Generate Datastore
		DataStore datastore = new DataStore();
		DataSet dataset = (new SAMLDatasetDetailsServiceImpl()).loadDatasetBySAML(recoveredSessionID, credentials);;
		LOG.info("This is the dataset after callback" + dataset);
		
		return "pages/landing"; // Change with the callback 
	}

}
