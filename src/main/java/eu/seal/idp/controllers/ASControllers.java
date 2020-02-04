///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package eu.seal.idp.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//
//import eu.seal.idp.model.pojo.SessionMngrResponse;
//import eu.seal.idp.service.EsmoMetadataService;
//import eu.seal.idp.service.HttpSignatureService;
//import eu.seal.idp.service.KeyStoreService;
//import eu.seal.idp.service.NetworkService;
//import eu.seal.idp.service.impl.HttpSignatureServiceImpl;
//import eu.seal.idp.service.impl.NetworkServiceImpl;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.security.Key;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.UnrecoverableKeyException;
//import java.security.spec.InvalidKeySpecException;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import org.apache.commons.httpclient.NameValuePair;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;	
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
///**
// * Controllers managing Seal Authentication Source, used in the log in and SSO Callback
// */
//@Controller
//public class ASControllers {
//
//	private final static Logger LOG = LoggerFactory.getLogger(ASControllers.class);
//
//	private final NetworkService netServ;
//	private final EsmoMetadataService metadataServ;
//	private final KeyStoreService keyServ;
//
////	@Autowired
////	public ASControllers(KeyStoreService keyServ,
////			EsmoMetadataService metadataServ) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, UnsupportedEncodingException, InvalidKeySpecException, IOException {
////		this.metadataServ = metadataServ;
////		this.keyServ = keyServ;
////		Key signingKey = this.keyServ.getSigningKey();
////		String fingerPrint = this.keyServ.getFingerPrint();
////		HttpSignatureService httpSigServ = new HttpSignatureServiceImpl(fingerPrint, signingKey);
////		this.netServ = new NetworkServiceImpl(httpSigServ);
////	}
////	
////	/**
////	 * Redirects an existing IDP request to the IDP 
////	 * @param msToken
////	 * @param model
////	 * @param redirectAttrs
////	 * @return
////	 * @throws KeyStoreException
////	 */
////
////	@RequestMapping(value = "/as/authenticate", method = {RequestMethod.POST, RequestMethod.GET})
////	public String authenticate(@RequestParam(value = "msToken", required = false) String msToken, Model model, RedirectAttributes redirectAttrs) throws KeyStoreException {
////		String sessionMngrUrl = System.getenv("SESSION_MANAGER_URL");
////
////		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
////		requestParams.add(new NameValuePair("token", msToken));
////		ObjectMapper mapper = new ObjectMapper();    
////
////		try {
////			SessionMngrResponse resp = mapper.readValue(netServ.sendGet(sessionMngrUrl, "/sm/validateToken", requestParams, 1), SessionMngrResponse.class);
////			if (resp.getCode().toString().equals("OK") && StringUtils.isEmpty(resp.getError())) {
////				String sealSessionId = resp.getSessionData().getSessionId();
////				requestParams.clear();
////				requestParams.add(new NameValuePair("sessionId", sealSessionId));
////				Gson gson = new Gson();
////				LinkedHashMap<?, ?> apRequest = (LinkedHashMap<?, ?>) resp.getSessionData().getSessionVariables().get("apRequest");
////				String jsonApRequest = gson.toJson(apRequest, LinkedHashMap.class);
////
////				LOG.info("* Session:" + apRequest + "\n" + "* Correct data:" + jsonApRequest);
////
////				if (apRequest == null) {
////					LOG.error("no apRequest found in session" + sealSessionId);
////					model.addAttribute("error", "No AP request attributes found in the Session! Please restart the process");
////					redirectAttrs.addFlashAttribute("errorMsg", "No AP request attributes found in the Session! Please restart the process");
////					return "redirect:/authfail";
////				} else {
////					return "redirect:/saml/login?session=" + sealSessionId;
////				}
////			} else {
////				model.addAttribute("error", "Error validating token! " + resp.getError());
////				LOG.error("something wring with the SM session!" + resp.getError());
////				redirectAttrs.addFlashAttribute("errorMsg", "Error validating token! " + resp.getError());
////			}
////		} catch (NoSuchAlgorithmException e) {
////			e.printStackTrace();
////			return "redirect:/authfail";
////		} catch (IOException e) {
////			e.printStackTrace();
////		} 
////		return "redirect:/saml/login";
////	}
//}

