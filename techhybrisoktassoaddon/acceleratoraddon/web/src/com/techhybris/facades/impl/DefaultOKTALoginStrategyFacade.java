package com.techhybris.facades.impl;

import com.techhybris.facades.OKTALoginStrategyFacade;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * The type Default okta login strategy facade.
 */
public class DefaultOKTALoginStrategyFacade implements OKTALoginStrategyFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOKTALoginStrategyFacade.class);
    @Resource(name = "guidCookieStrategy")
    private GUIDCookieStrategy guidCookieStrategy;
    @Resource(name = "rememberMeServices")
    private RememberMeServices rememberMeServices;
    @Resource(name = "customerFacade")
    private CustomerFacade customerFacade;
    @Resource(name = "oktaLoginAuthenticationManager")
    private AuthenticationManager authenticationManager;

    public DefaultOKTALoginStrategyFacade() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void oktaAutoLoginStrategy(final String emailID, final HttpServletRequest request,
                                      final HttpServletResponse response) {
        final UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(StringUtils.lowerCase(emailID), null, Collections.emptyList());
        token.setDetails(new WebAuthenticationDetails(request));

        try {
            final Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            customerFacade.loginSuccess();
            guidCookieStrategy.setCookie(request, response);
            rememberMeServices.loginSuccess(request, response, authentication);
        } catch (final Exception e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            LOGGER.error("Failure during autoLogin", e);
        }
    }
}