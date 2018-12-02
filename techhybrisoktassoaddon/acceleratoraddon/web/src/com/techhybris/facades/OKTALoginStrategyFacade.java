package com.techhybris.facades;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The interface Okta login strategy facade.
 */
@FunctionalInterface
public interface OKTALoginStrategyFacade {
    /**
     * This method is responsible for Login into Hybris System using Spring Security. Don't call this method without
     * proper authentication otherwise user will be logged into hybris system without password.
     *
     * @param emailID  the email id
     * @param request  the request
     * @param response the response
     */
    void oktaAutoLoginStrategy(final String emailID, final HttpServletRequest request,
                               final HttpServletResponse response);
}