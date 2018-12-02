package com.techhybris.controllers;

import com.techhybris.OKTALoginStatus;
import com.techhybris.facades.OKTALoginStrategyFacade;
import com.techhybris.facades.UserAssociationFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The type Okta login controller.
 */
@Controller
@RequestMapping("/oktasso")
@Produces(MediaType.APPLICATION_JSON)
public class OKTALoginController {

    @Resource(name = "oktaLoginStrategyFacade")
    private OKTALoginStrategyFacade oktaLoginStrategyFacade;
    @Resource(name = "userAssociationFacade")
    private UserAssociationFacade userAssociationFacade;

    public OKTALoginController() {
        super();
    }

    /**
     * Login using okta login status.
     *
     * @param userid              the userid
     * @param sessionToken        the session token
     * @param referer             the referer
     * @param contextURL          the context url
     * @param httpServletRequest  the http servlet request
     * @param httpServletResponse the http servlet response
     * @param model               the model
     * @return the okta login status
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public OKTALoginStatus loginUsingOKTA(
            @RequestParam("uid") final String userid,
            @RequestParam("sToken") final String sessionToken,
            @RequestHeader(value = "referer", required = false) final String referer,
            @RequestHeader(value = "origin", required = false) final String contextURL, final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse, final Model model) {
        OKTALoginStatus oktaLoginStatus = new OKTALoginStatus();
        if (!userAssociationFacade.validateUserExistence(userid)) {
            oktaLoginStatus.setSuccess(Boolean.FALSE);
            model.addAttribute("loginError", Boolean.valueOf(true));
            GlobalMessages.addErrorMessage(model, "login.error.account.not.found.title");
            oktaLoginStatus.setRedirectionURL(referer);
            return oktaLoginStatus;
        }
        oktaLoginStrategyFacade.oktaAutoLoginStrategy(userid, httpServletRequest, httpServletResponse);
        oktaLoginStatus.setSuccess(Boolean.TRUE);
        oktaLoginStatus.setRedirectionURL(getRedirectURL(contextURL, referer));
        return oktaLoginStatus;
    }

    private static String getRedirectURL(final String contextURL, final String referer) {
        if (referer.contains("/checkout")) {
            return contextURL + "/cart/checkout";
        } else {
            return referer;
        }

    }

}
