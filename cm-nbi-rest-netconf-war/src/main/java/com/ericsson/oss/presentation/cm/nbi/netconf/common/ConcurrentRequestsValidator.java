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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager;
import com.ericsson.oss.presentation.cm.nbi.netconf.exceptions.TooManyNetconfPayloadRequestsException;
import com.ericsson.oss.presentation.cm.nbi.netconf.persistence.NetconfPayloadPersistenceService;

@Singleton
public class ConcurrentRequestsValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentRequestsValidator.class);

    private static Map<String, Long> deploymentWiseAllowedRequests = new HashMap<>();

    @Inject
    private NetconfPayloadPersistenceService netconfPayloadPersistenceService;

    @Inject
    private DeploymentTypeRetriever deploymentTypeRetriever;

    @Inject
    private LockManager lockManager;

    @PostConstruct
    public void initialize() {
	deploymentWiseAllowedRequests.put("Extra_Large_ENM", 20l);
	deploymentWiseAllowedRequests.put("Medium_ENM", 20l);
	deploymentWiseAllowedRequests.put("Small_ENM_customer_cloud", 2l);
	deploymentWiseAllowedRequests.put("ENM_extra_small", 2l);
    }

    public synchronized void validateNumberOfInprogressRequests() throws TooManyNetconfPayloadRequestsException {

	Lock distributedLock = lockManager.getDistributedLock("NetconfPayloadLock", "CmNbiNetconfPayloadLock");

	try {
	    distributedLock.lock();
	    LOGGER.info("Obtained lock NetconfPayloadLock from CmNbiNetconfPayloadLock cluster");

	    Long inprogressNetconfPayloadRequests = netconfPayloadPersistenceService.getInprogressNetconfPayloadRequests();

	    if (inprogressNetconfPayloadRequests >= getDeploymentWiseAllowedCount()) {
		throw new TooManyNetconfPayloadRequestsException("Received more than max allowed Netconf Payload REST requests.");
	    }
	} finally {
	    distributedLock.unlock();
	    LOGGER.info("Released lock NetconfPayloadLock from CmNbiNetconfPayloadLock cluster");
	}

    }

    private Long getDeploymentWiseAllowedCount() {

	String enmDeploymentType = deploymentTypeRetriever.getEnmDeploymentType();
	Long allowedCount = deploymentWiseAllowedRequests.get(enmDeploymentType);

	LOGGER.debug("Allowed Netconf Payload Request count for ENM deployment type : {} is : {}", enmDeploymentType, allowedCount);

	return allowedCount != null ? allowedCount : NetconfPayloadConstants.DEFAULT_NETCONF_PAYLOAD_REQUESTS_ALLOWED;
    }
}
