package com.zihangege.superweather.util;

public interface HttpCallbackListener {
	
	void onSuccess(String response);

	void onFailed(Exception e);
}
