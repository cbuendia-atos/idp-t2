package eu.seal.idp.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.opensaml.saml2.core.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import eu.seal.idp.model.pojo.AttributeType;
import eu.seal.idp.model.pojo.DataSet;
import eu.seal.idp.model.pojo.DataStore;

@Service
public class SAMLUserDetailsServiceImpl implements SAMLUserDetailsService {
	
	// Logger
	private static final Logger LOG = LoggerFactory.getLogger(SAMLUserDetailsServiceImpl.class);
	
	public Object loadUserBySAML(SAMLCredential credential)
			throws UsernameNotFoundException {
		
		DataStore datastore = new DataStore();
		DataSet dataset = new DataSet();
		String userID = credential.getNameID().getValue();
		dataset.setId(userID);
		List<Attribute> attributesList = credential.getAttributes();
		
		for (Attribute att: attributesList) {
			AttributeType attributeType = new AttributeType();
			attributeType.setName(att.getName());
			attributeType.setFriendlyName(att.getFriendlyName());
			dataset.addAttributesItem(attributeType);
		}
		
		LOG.info(dataset.toString());
		LOG.info(userID + " is logged in");
		
		datastore.addClearDataItem(dataset);
		
		 
		 List<NameValuePair> requestParams = new ArrayList<>();
		 requestParams.clear();
		 requestParams.add(new NameValuePair("dataObject", "{ 'object': 'hello' }") );
		 requestParams.add(new NameValuePair("sessionId", ""));
		 UpdateDataRequest updateReq = new UpdateDataRequest(esmoSessionId, "dsResponse", attributSetString);
		 resp = mapper.readValue(netServ.sendPostBody(sessionMngrUrl, "/sm/updateSessionData", updateReq, "application/json", 1), SessionMngrResponse.class);
		 
		 if (!resp.getCode().toString().equals("OK")) {
			 LOG.error("ERROR: " + resp.getError());
			 redirectAttrs.addFlashAttribute("errorMsg", "Error communicating with the ESMO Network");
			 return "redirect:/authfail";
		 }

		 // store the ap metadata
		 requestParams.clear();
		 if (metadataServ != null && metadataServ.getMetadata() != null) {
			 updateReq = new UpdateDataRequest(esmoSessionId, "dsMetadata", mapper.writeValueAsString(metadataServ.getMetadata()));
			 resp = mapper.readValue(netServ.sendPostBody(sessionMngrUrl, "/sm/updateSessionData", updateReq, "application/json", 1), SessionMngrResponse.class);
			 if (!resp.getCode().toString().equals("OK")) {
				 LOG.error("ERROR: " + resp.getError());
				 redirectAttrs.addFlashAttribute("errorMsg", "Error communicating with the ESMO Network");
				 return "redirect:/authfail";
			 }
		 }
		
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
		authorities.add(authority);

		return new User(userID, "", true, true, true, true, authorities);
	}

	
}
