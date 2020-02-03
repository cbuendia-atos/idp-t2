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

import java.util.List;

import org.opensaml.saml2.core.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.seal.idp.model.pojo.AttributeType;
import eu.seal.idp.model.pojo.CurrentUser;
import eu.seal.idp.model.pojo.DataSet;
import eu.seal.idp.model.pojo.DataStore;

@Controller
public class CallbackController {
	
	// Logger
	private static final Logger LOG = LoggerFactory
			.getLogger(LandingController.class);

	@RequestMapping("/callback")
	public String landing(@CurrentUser User user, Model model, Authentication authentication) {
		LOG.info("I'm in the callback");
		authentication.getDetails();
		DataStore datastore = new DataStore();
		DataSet dataset = new DataSet();
		
		SAMLCredential credentials = (SAMLCredential) authentication.getCredentials();
		
		List<Attribute> attributesList = credentials.getAttributes();
		
		for (Attribute att: attributesList) {
			AttributeType attributeType = new AttributeType();
			attributeType.setName(att.getName());
			attributeType.setFriendlyName(att.getFriendlyName());
			dataset.addAttributesItem(attributeType);
		}
		datastore.addClearDataItem(dataset);
		LOG.info(datastore.toString());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		// Si
		return "pages/landing"; // Change with the callback 
	}

}
