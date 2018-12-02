package com.techhybris.filter;

import com.techhybris.facades.OKTALoginStrategyFacade;
import com.techhybris.facades.UserAssociationFacade;
import de.hybris.platform.jalo.user.CookieBasedLoginToken;
import de.hybris.platform.jalo.user.LoginToken;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The type Customer login service filter.
 *
 * @author anshul.
 */
public class CustomerLoginServiceFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(CustomerLoginServiceFilter.class);
    public static final String ANONYMOUS = "anonymous";
    public static final String SAML_COOKIE_NAME = "samlPassThroughToken";
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "oktaLoginStrategyFacade")
    private OKTALoginStrategyFacade oktaLoginStrategyFacade;
    @Resource(name = "userAssociationFacade")
    private UserAssociationFacade userAssociationFacade;

    public CustomerLoginServiceFilter() {
        super();
    }

    /**
     * Get SAML sso cookie.
     *
     * @param request the request
     * @return saml SSO cookie if persist
     */
    public static Cookie getSamlCookie(final HttpServletRequest request) {
        final String cookieName = SAML_COOKIE_NAME;
        if (null != cookieName) {
            return WebUtils.getCookie(request, cookieName);
        } else {
            return null;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse, final FilterChain filterChain)
            throws ServletException, IOException {

        if (getSamlCookie(httpServletRequest) != null && null != userService.getCurrentUser() && userService
                .getCurrentUser().getUid().equalsIgnoreCase(ANONYMOUS)) {
            final LoginToken loginToken = new CookieBasedLoginToken(getSamlCookie(httpServletRequest));
            if (null != loginToken.getUser()) {
                LOGGER.debug("LOG IN THE OKTA AUTHENTICATED USER " + loginToken.getUser().getUid() + " INTO HYBRIS");
                oktaLoginStrategyFacade
                        .oktaAutoLoginStrategy(loginToken.getUser().getUid(), httpServletRequest, httpServletResponse);
            }

        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}