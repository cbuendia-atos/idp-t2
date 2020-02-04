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
public class SAMLDatasetDetailsServiceImpl {
	
	// Logger
	private static final Logger LOG = LoggerFactory.getLogger(SAMLDatasetDetailsServiceImpl.class);
	
	public DataSet loadDatasetBySAML(String dsId, SAMLCredential credential)
			throws UsernameNotFoundException {
		
		DataSet dataset = new DataSet();
		dataset.setId(dsId);
		List<Attribute> attributesList = credential.getAttributes();
		
		for (Attribute att: attributesList) {
			AttributeType attributeType = new AttributeType();
			attributeType.setName(att.getName());
			attributeType.setFriendlyName(att.getFriendlyName());
			dataset.addAttributesItem(attributeType);
		}
		
		LOG.info(dataset.toString());
		return dataset;
	}
}
