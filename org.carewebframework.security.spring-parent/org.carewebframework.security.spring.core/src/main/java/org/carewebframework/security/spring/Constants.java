/*
 * #%L
 * carewebframework
 * %%
 * Copyright (C) 2008 - 2016 Regenstrief Institute, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.carewebframework.org/licensing/disclaimer.
 *
 * #L%
 */
package org.carewebframework.security.spring;

import org.carewebframework.ui.zk.ZKUtil;

public class Constants {
    
    public static final String RESOURCE_PREFIX = ZKUtil.getResourcePath(Constants.class);
    
    // Miscellaneous security constants
    
    public static final String ROLE_PREFIX = "ROLE_";
    
    public static final String PRIV_PREFIX = "PRIV_";
    
    public static final String ROLE_USER = ROLE_PREFIX + "USER";
    
    public static final String PRIV_DEBUG = PRIV_PREFIX + "DEBUG"; // Privilege that enables debug modes.
    
    public static final String ANONYMOUS_USER = "ANONYMOUS_USER";
    
    public static final String DEFAULT_TARGET_PARAMETER = "spring-security-redirect";
    
    public static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";
    
    public static final String SAVED_USER = "SPRING_SECURITY_SAVED_USER";
    
    public static final String DEFAULT_SECURITY_DOMAIN = "cwf_defaultDomain";
    
    public static final String DEFAULT_USERNAME = "cwf_defaultUsername";
    
    public static final String DIALOG_ACCESS_DENIED = RESOURCE_PREFIX + "accessDenied.zul";
    
    // Logout constants
    
    public static final String LOGOUT_URI = "/logout?" + DEFAULT_TARGET_PARAMETER + "=";
    
    public static final String LOGOUT_WARNING_ATTR = "logoutWarningMessage";
    
    public static final String LOGOUT_TARGET_ATTR = "logoutTargetURI";
    
    public static final String LBL_LOGOUT_MESSAGE_DEFAULT = "logout.message.default";
    
    // Login constants
    
    public static final String PROP_LOGIN_LOGO = "LOGIN.LOGO";
    
    public static final String PROP_LOGIN_HEADER = "LOGIN.HEADER";
    
    public static final String PROP_LOGIN_FOOTER = "LOGIN.FOOTER";
    
    public static final String PROP_LOGIN_INFO = "LOGIN.INFO";
    
    public static final String LBL_LOGIN_PAGE_TITLE = "login.form.panel.title";
    
    public static final String LBL_LOGIN_ERROR = "login.error";
    
    public static final String LBL_LOGIN_FORM_TIMEOUT_MESSAGE = "login.form.timeout.message";
    
    public static final String LBL_LOGIN_PROGRESS = "login.progress";
    
    public static final String LBL_LOGIN_REQUIRED_FIELDS = "login.required.fields";
    
    public static final String LBL_LOGIN_NO_VALID_DOMAINS = "login.error.no.valid.domains";
    
    public static final String LBL_LOGIN_ERROR_INVALID = "login.error.invalid";
    
    public static final String LBL_LOGIN_ERROR_EXPIRED_PASSWORD = "login.error.expired.password";
    
    public static final String LBL_LOGIN_ERROR_EXPIRED_USER = "login.error.expired.user";
    
    public static final String LBL_LOGIN_ERROR_UNEXPECTED = "login.error.unexpected";
    
    // Password constants
    
    public static final String PASSWORD_EXPIRED_EXCEPTION = "expiredPasswordException";
    
    public static final String LBL_PASSWORD_RANDOM_LENGTH = "password.random.length";
    
    public static final String LBL_PASSWORD_RANDOM_CONSTRAINTS = "password.random.constraints";
    
    public static final String LBL_PASSWORD_CHANGE_UNAVAILABLE = "password.change.unavailable";
    
    public static final String LBL_PASSWORD_CHANGE_PAGE_TITLE = "password.change.dialog.panel.title";
    
    /**
     * Enforce static class.
     */
    private Constants() {
    };
}
