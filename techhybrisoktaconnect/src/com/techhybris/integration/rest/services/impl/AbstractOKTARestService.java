package com.techhybris.integration.rest.services.impl;

import com.techhybris.integration.rest.services.OKTARESTServices;
import de.hybris.platform.util.Config;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
/**
 * The type Abstract okta rest service.
 */
public abstract class AbstractOKTARestService implements OKTARESTServices {

    private static final String OKTASSOAUTHERIZATIONKEY = "Authorization";
    private static final String OKTASSOAUTHERIZATIONVALUE = "SSWS " + Config.getParameter("okta.sso.token");
    private static final int OKTAREADTIMEOUT = Config.getInt("okta.sso.read.timeout", 3000);
    private static final int OKTACONEECTIONTIMEOUT = Config.getInt("okta.sso.connection.timeout", 3000);

    /**
     * Gets okta specific authorizations.
     *
     * @return the okta specific authorizations
     */
    protected HttpHeaders getOKTASpecificAuthorizations() {
        HttpHeaders oktaHttpHeaders = new HttpHeaders();
        oktaHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
        oktaHttpHeaders.set(OKTASSOAUTHERIZATIONKEY, OKTASSOAUTHERIZATIONVALUE);
        return oktaHttpHeaders;

    }

    /**
     * Create rest template object rest template.
     *
     * @return the rest template
     */
    protected RestTemplate createRestTemplateObject() {
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory =
                (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        simpleClientHttpRequestFactory.setReadTimeout(OKTAREADTIMEOUT);
        simpleClientHttpRequestFactory.setConnectTimeout(OKTACONEECTIONTIMEOUT);
        return restTemplate;
    }
}