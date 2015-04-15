/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.rivetlogic.mobilepeopledirectory.transport;

import com.liferay.mobile.android.service.BaseService;
import com.liferay.mobile.android.service.JSONObjectWrapper;
import com.liferay.mobile.android.service.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Bruno Farache
 */
public class PeopleDirectoryService extends BaseService {

	public PeopleDirectoryService(Session session) {
		super(session);
	}

	public JSONObject fetchAll() throws Exception {
		JSONObject _command = new JSONObject();

		try {
			JSONObject _params = new JSONObject();

			_command.put("/people-directory-services-portlet/peopledirectory/fetch-all", _params);
		}
		catch (JSONException _je) {
			throw new Exception(_je);
		}

		JSONArray _result = session.invoke(_command);

		if (_result == null) {
			return null;
		}

		return _result.getJSONObject(0);
	}

	public Integer getActiveUsersCount() throws Exception {
		JSONObject _command = new JSONObject();

		try {
			JSONObject _params = new JSONObject();

			_command.put("/people-directory-services-portlet/peopledirectory/get-active-users-count", _params);
		}
		catch (JSONException _je) {
			throw new Exception(_je);
		}

		JSONArray _result = session.invoke(_command);

		if (_result == null) {
			return null;
		}

		return _result.getInt(0);
	}

	public JSONObject search(String keywords, int start, int end) throws Exception {
		JSONObject _command = new JSONObject();

		try {
			JSONObject _params = new JSONObject();

			_params.put("keywords", keywords);
			_params.put("start", start);
			_params.put("end", end);

			_command.put("/people-directory-services-portlet/peopledirectory/search", _params);
		}
		catch (JSONException _je) {
			throw new Exception(_je);
		}

		JSONArray _result = session.invoke(_command);

		if (_result == null) {
			return null;
		}

		return _result.getJSONObject(0);
	}

	public JSONObject usersFetchByDate(long modifiedDate) throws Exception {
		JSONObject _command = new JSONObject();

		try {
			JSONObject _params = new JSONObject();

			_params.put("modifiedDate", modifiedDate);

			_command.put("/people-directory-services-portlet/peopledirectory/users-fetch-by-date", _params);
		}
		catch (JSONException _je) {
			throw new Exception(_je);
		}

		JSONArray _result = session.invoke(_command);

		if (_result == null) {
			return null;
		}

		return _result.getJSONObject(0);
	}

}