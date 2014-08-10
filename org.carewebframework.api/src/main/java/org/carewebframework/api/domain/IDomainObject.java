/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.api.domain;

/**
 * Base interface for domain objects.
 */
public interface IDomainObject {
    
    /**
     * Returns an id that is assumed to uniquely identify this object within its domain.
     *
     * @return The unique id.
     */
    String getLogicalId();
    
    /**
     * Returns an id that is unique across domains. The logical id is typically a subset of the
     * universal id.
     *
     * @return The universal id.
     */
    String getUniversalId();
    
}
