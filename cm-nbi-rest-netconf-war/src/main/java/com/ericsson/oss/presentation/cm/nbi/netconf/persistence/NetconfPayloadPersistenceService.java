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

package com.ericsson.oss.presentation.cm.nbi.netconf.persistence;

import java.util.*;

import javax.ejb.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.*;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.presentation.cm.nbi.netconf.common.NetconfPayloadConstants;
import com.ericsson.oss.services.cm.nbi.netconf.NetconfPayloadRequestStatus;

/**
 * @author 552715
 *
 */
@Stateless
public class NetconfPayloadPersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetconfPayloadPersistenceService.class);

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    public static final String NETWORK_ELEMENT_MO = "NetworkElement=%s";
    public static final String STATUS = "status";
    public static final String CM_NBI_NETCONF_REQUEST = "CmNbiNetconfPayloadRequest";
    public static final String OSS_CM_NBI = "OSS_CM_NBI";

    public Long createCmNBiNetconfPayloadRequest(final String networkElementName, final String requestId) {

	Map<String, Object> poAttributes = new HashMap<>();
	poAttributes.put("nodeAddress", networkElementName);
	poAttributes.put("requestId", requestId);
	poAttributes.put(STATUS, NetconfPayloadRequestStatus.INITIATED.toString());

	PersistenceObject persistenceObject = dataPersistenceService.getLiveBucket().getPersistenceObjectBuilder().namespace(OSS_CM_NBI).version("1.0.0").type(CM_NBI_NETCONF_REQUEST)
		.addAttributes(poAttributes).create();
	LOGGER.debug("persistenceObject for networkElement {} created with attributes : {}, PO ID : {}", networkElementName, persistenceObject.getAllAttributes(), persistenceObject.getPoId());
	return persistenceObject.getPoId();
    }

    public Map<String, Object> getNetconfPayloadRequestAttributes(final String requestId) {
	QueryExecutor queryExecutor = dataPersistenceService.getLiveBucket().getQueryExecutor();
	final QueryBuilder queryBuilder = dataPersistenceService.getQueryBuilder();

	final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_CM_NBI, CM_NBI_NETCONF_REQUEST);
	Restriction restriction = typeQuery.getRestrictionBuilder().equalTo(NetconfPayloadConstants.REQUEST_ID, requestId);
	typeQuery.setRestriction(restriction);

	List<PersistenceObject> poList = queryExecutor.getResultList(typeQuery);
	LOGGER.debug("Persistent object list received : {} for query : {}", poList, typeQuery);
	return (poList != null && !poList.isEmpty()) ? poList.get(0).getAllAttributes() : null;
    }

    public Long getInprogressNetconfPayloadRequests() {

	QueryExecutor queryExecutor = dataPersistenceService.getLiveBucket().getQueryExecutor();
	final QueryBuilder queryBuilder = dataPersistenceService.getQueryBuilder();

	final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_CM_NBI, CM_NBI_NETCONF_REQUEST);
	Restriction completedStatusRestriction = typeQuery.getRestrictionBuilder().equalTo(NetconfPayloadConstants.STATUS, NetconfPayloadRequestStatus.COMPLETED.toString());
	Restriction inprogressStatusRestriction = typeQuery.getRestrictionBuilder().not(completedStatusRestriction);
	typeQuery.setRestriction(inprogressStatusRestriction);

	Long noOfInprogressRequests = queryExecutor.executeCount(typeQuery);
	LOGGER.debug("Number of inprogress requests received are : {} for query : {}", noOfInprogressRequests, typeQuery);
	return noOfInprogressRequests;
    }

    public ManagedObject getNetworkElementMo(final String networkElementName) {
	final String networkElementFdn = String.format(NETWORK_ELEMENT_MO, networkElementName);
        LOGGER.debug("Fetching Network Element MO details from DPS for fdn : {}", networkElementFdn);
	return dataPersistenceService.getLiveBucket().findMoByFdn(networkElementFdn);
    }

    public Iterator<PersistenceObject> getAllNetconfPayloadRequestPOs() {
        final QueryExecutor queryExecutor = dataPersistenceService.getLiveBucket().getQueryExecutor();
        final QueryBuilder queryBuilder = dataPersistenceService.getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_CM_NBI, CM_NBI_NETCONF_REQUEST);
        return queryExecutor.execute(typeQuery);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deletePO(final PersistenceObject netconfRequestPO) {
        dataPersistenceService.getLiveBucket().deletePo(netconfRequestPO);
    }

}
