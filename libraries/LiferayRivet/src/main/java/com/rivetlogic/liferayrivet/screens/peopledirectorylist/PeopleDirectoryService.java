package com.rivetlogic.liferayrivet.screens.peopledirectorylist;

import com.liferay.mobile.android.service.BaseService;
import com.liferay.mobile.android.service.Session;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lorenz on 1/12/15.
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

        return (JSONObject)session.invoke(_command);
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

        return (JSONObject)session.invoke(_command);
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

        return (JSONObject)session.invoke(_command);
    }

}