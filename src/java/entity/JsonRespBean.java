package entity;

import org.json.JSONObject;

public class JsonRespBean {

	public static String success(Object data) {
		JSONObject req = new JSONObject();
		req.put("reason", "ok");
		req.put("data", data);
		return req.toString();

	}

	public static String success() {
		JSONObject req = new JSONObject();
		req.put("reason", "ok");
		return req.toString();

	}

	public static String erro(Exception e) {
		JSONObject req = new JSONObject();
		req.put("reason", e.getMessage());
		return req.toString();
	}


}
