package org.geogebra.web.html5.util;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Implements HTTP requests and
 *         responses for web.
 */
public class HttpRequestW extends HttpRequest {

	/*
	 * The following code has been copied mostly from
	 * http://code.google.com/intl
	 * /hu-HU/webtoolkit/doc/latest/DevGuideServerCommunication
	 * .html#DevGuideHttpRequests
	 */
	@Override
	public void sendRequest(String url) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
		        URL.encode(url));

		try {
			builder.setTimeoutMillis(getTimeout() * 1000);
			Log.debug("Sending request " + url + " until timeout " + getTimeout());
			builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(Request request, Throwable exception) {
					// Couldn't connect to server (could be timeout, SOP
					// violation, etc.)
					setResponseText(exception.getMessage());
					processed = true;
				}

				@Override
				public void onResponseReceived(Request request,
				        Response response) {
					if (200 == response.getStatusCode()) {
						// Process the response in response.getText()
						setResponseText(response.getText());
						processed = true;
					} else {
						// Handle the error. Can get the status text from
						// response.getStatusText()
						setResponseText(response.getStatusText());
						processed = true;
					}
				}
			});
		} catch (RequestException e) {
			// Couldn't connect to server
			processed = true;
		}
	}

	@Override
	public void sendRequestPost(String url, String post, AjaxCallback callback) {
		XHR2 request = XHR2.create();
		if (callback == null) {
			request.openSync("POST", url);
		} else {
			request.open("POST", url);
		}
		// needed for SMART, hopefully no problem for others
		request.setRequestHeader("Content-type", "text/plain");
		// request.setTimeOut(timeout * 1000);
		request.onLoad(callback);
		request.send(post);
	}
}