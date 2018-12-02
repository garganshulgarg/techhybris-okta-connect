/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.techhybris.setup;

import static com.techhybris.constants.TechhybrisoktaconnectConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.techhybris.constants.TechhybrisoktaconnectConstants;
import com.techhybris.service.TechhybrisoktaconnectService;


@SystemSetup(extension = TechhybrisoktaconnectConstants.EXTENSIONNAME)
public class TechhybrisoktaconnectSystemSetup
{
	private final TechhybrisoktaconnectService techhybrisoktaconnectService;

	public TechhybrisoktaconnectSystemSetup(final TechhybrisoktaconnectService techhybrisoktaconnectService)
	{
		this.techhybrisoktaconnectService = techhybrisoktaconnectService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		techhybrisoktaconnectService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return TechhybrisoktaconnectSystemSetup.class.getResourceAsStream("/techhybrisoktaconnect/sap-hybris-platform.png");
	}
}
