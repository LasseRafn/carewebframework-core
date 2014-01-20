/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at 
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.api.event;

import static org.junit.Assert.assertEquals;

import org.carewebframework.common.JSONUtil;

import org.junit.Test;

public class SerializationTest {
    
    @Test
    public void testSerialization() {
        PingRequest pingRequest = new PingRequest("testApp", "testRequestor");
        String data = JSONUtil.serialize(pingRequest);
        pingRequest = (PingRequest) JSONUtil.deserialize(data);
        assertEquals("testApp", pingRequest.appName);
        assertEquals("testRequestor", pingRequest.requestor);
        
        PublisherInfo publisherInfo = new PublisherInfo();
        publisherInfo.setAppName("testApp");
        publisherInfo.setEndpointId("testEP");
        publisherInfo.setNodeId("testNode");
        publisherInfo.setUserId("testUserId");
        publisherInfo.setUserName("testUser");
        data = JSONUtil.serialize(publisherInfo);
        publisherInfo = (PublisherInfo) JSONUtil.deserialize(data);
        assertEquals("a-testApp", publisherInfo.getAppName());
        assertEquals("testEP", publisherInfo.getEndpointId());
        assertEquals("n-testNode", publisherInfo.getNodeId());
        assertEquals("u-testUserId", publisherInfo.getUserId());
        assertEquals("testUser", publisherInfo.getUserName());
    }
}
