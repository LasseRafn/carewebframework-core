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
 * 
 * @param <T> Type of proxied object.
 */
public interface IProxiedDomainObject<T> extends IDomainObject {
    
    /**
     * Returns the native domain object. If this implementation wraps another domain object, the
     * wrapped object is returned; otherwise, the method should simply return a reference to the
     * domain object itself.
     *
     * @return The native domain object.
     */
    T getProxiedObject();
    
}
