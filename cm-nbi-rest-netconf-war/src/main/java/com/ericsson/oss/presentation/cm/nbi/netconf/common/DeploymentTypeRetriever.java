/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.presentation.cm.nbi.netconf.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.itpf.sdk.resources.Resources;

@ApplicationScoped
public class DeploymentTypeRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentTypeRetriever.class);

    private String enmDeploymentType = null;

    public String getEnmDeploymentType() {

	if (enmDeploymentType == null) {
	    loadProperties();
	}
	return enmDeploymentType;
    }

    private void loadProperties() {

	final Properties properties = new Properties();
	InputStream inputStream = null;
	LOGGER.info("Reading Properties from {}.", NetconfPayloadConstants.GLOBAL_PROPERTIES_FILE_PATH);
	final Resource resource = Resources.getFileSystemResource(NetconfPayloadConstants.GLOBAL_PROPERTIES_FILE_PATH);
	if (resource.exists()) {
	    inputStream = resource.getInputStream();
	}
	if (inputStream != null) {
	    try {
		properties.load(inputStream);
	    } catch (final IOException e) {
		LOGGER.error("Exception in loading parameters from input stream", e);
	    }
	    Resources.safeClose(inputStream);
	}

	enmDeploymentType = getPropertyValueAsString(properties, NetconfPayloadConstants.ENM_DEPLOYMENT_TYPE);
    }

    private String getPropertyValueAsString(final Properties properties, final String propertyKey) {
	final String property = properties.getProperty(propertyKey);
	if (property != null) {
	    LOGGER.info("Loading property {} with value {}", propertyKey, property);
	    return property;
	}
	LOGGER.error("Property {} was not found!!", propertyKey);
	return "";
    }

}
