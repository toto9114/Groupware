package rnd.gw.plani.co.kr.groupware;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import net.htmlparser.jericho.HTMLElementName;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rnd.gw.plani.co.kr.groupware.GCM.PropertyManager;

/**
 * Created by RND on 2016-05-12.
 */
public class NetworkManager {
    private static NetworkManager instance;

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    DefaultHttpClient httpClient;
    CookieManager cookieManager;

    private NetworkManager() {
        httpClient = new DefaultHttpClient();

        HttpParams param = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(param, 5000);
        HttpConnectionParams.setSoTimeout(param, 5000);

        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
    }

    public interface OnResultListener<T> {
        public void onSuccess(HttpRequest request, T result);

        public void onFailure(HttpRequest request, int code, Throwable cause);
    }

    private static final int MESSAGE_SUCCESS = 0;
    private static final int MESSAGE_FAILURE = 1;

    static class NetworkHandler extends Handler {
        public NetworkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CallbackObject object = (CallbackObject) msg.obj;
            HttpRequest request = object.request;
            OnResultListener listener = object.listener;
            switch (msg.what) {
                case MESSAGE_SUCCESS:
                    listener.onSuccess(request, object.result);
                    break;
                case MESSAGE_FAILURE:
                    listener.onFailure(request, -1, object.exception);
                    break;
            }
        }
    }

    Handler mHandler = new NetworkHandler(Looper.getMainLooper());

    static class CallbackObject<T> {
        HttpRequest request;
        T result;
        IOException exception;
        OnResultListener<T> listener;
    }


    public HttpGet getAllNotify(Context context, String url, final OnResultListener<NewsFeedResult> listener) { //뉴스피드 정보 갖고오기
        final CallbackObject<NewsFeedResult> callbackObject = new CallbackObject<>();
        final HttpGet request = new HttpGet(url);
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    callbackObject.request = request;
                    callbackObject.listener = listener;
                    HttpResponse response = null;
                    response = httpClient.execute(request);
                    HttpEntity entity = response.getEntity();
                    String result = EntityUtils.toString(entity, "UTF-8");
                    Document doc = Jsoup.parse(result);
                    Element element = doc.select("div.gw_feed.feeds_page").get(0);
                    List<Element> list = doc.select("div.centered").get(0).getElementsByTag(HTMLElementName.LI);
                    NewsFeedResult data = new NewsFeedResult();
                    data.element = element;
                    data.page = list.size() - 1;

                    if (response != null) {
                        callbackObject.result = data;
                        Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    callbackObject.exception = e;
                    Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
                    mHandler.sendMessage(msg);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.flush();
                }
            }
        }.execute();
        return request;
    }

    public HttpGet getNote(Context context, String url, final OnResultListener<Element> listener) { //고객정보 갖고오기
        final CallbackObject<Element> callbackObject = new CallbackObject<>();
        final HttpGet request = new HttpGet(url);
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    callbackObject.request = request;
                    callbackObject.listener = listener;
                    HttpResponse response = null;
                    response = httpClient.execute(request);
                    HttpEntity entity = response.getEntity();
                    String result = EntityUtils.toString(entity, "UTF-8");
                    Document doc = Jsoup.parse(result);
                    Element element = doc.select("table.bbs_table").get(0);
                    if (response != null) {
                        callbackObject.result = element;
                        Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    callbackObject.exception = e;
                    Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
                    mHandler.sendMessage(msg);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.flush();
                }
            }
        }.execute();
        return request;
    }

    public HttpGet getContactSection(Context context, final String url, final OnResultListener<ContentData> listener) { //업무연락 상세보기 페이지 갖고오기
        final CallbackObject<ContentData> callbackObject = new CallbackObject<>();
        final HttpGet request = new HttpGet(url);
        final MyRedirectHandler handler = new MyRedirectHandler();
        httpClient.setRedirectHandler(handler);
        new AsyncTask<String, Integer, Boolean>() {
            ContentData data = new ContentData();

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    callbackObject.request = request;
                    callbackObject.listener = listener;
                    HttpResponse response = null;
                    response = httpClient.execute(request);
                    String lastUrl = url;
                    if (handler.lastRedirectedUri != null) {
                        lastUrl = handler.lastRedirectedUri.toString();
                        String id = lastUrl.replace("http://" + PropertyManager.getInstance().getDomain() + "/reception/reception/view/tableid/liaison/id/", "");
                        data.tableId = id;
                    }
                    HttpEntity entity = response.getEntity();
                    String result = EntityUtils.toString(entity, "UTF-8");
                    Document doc = Jsoup.parse(result);
                    Element element = doc.select("section#content").get(0);
                    data.element = element;
                    if (response != null) {
                        callbackObject.result = data;
                        Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    callbackObject.exception = e;
                    Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
                    mHandler.sendMessage(msg);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.flush();
                }
            }
        }.execute();
        return request;
    }

    public HttpPost login(Context context, String url, String id, String password, final OnResultListener<Element> listener) { //로그인
        httpClient.getCookieStore().clear();
        final CallbackObject<Element> callbackObject = new CallbackObject<>();
        final ArrayList<NameValuePair> nameValuePairs =
                new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("userid", id));
        nameValuePairs.add(new BasicNameValuePair("passwd", password));
        final HttpPost httpPost = new HttpPost(url + "/login/accounts/do_login/redirect/eNortjK0UtJXsgZcMAkSAcc.");
        new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {

                    callbackObject.request = httpPost;
                    callbackObject.listener = listener;
                    UrlEncodedFormEntity entityRequest =
                            new UrlEncodedFormEntity(nameValuePairs, "UTF-8");

                    httpPost.setEntity(entityRequest);

                    HttpResponse responsePost = httpClient.execute(httpPost);
                    List<Cookie> cookies = httpClient.getCookieStore().getCookies();
                    if (cookies.isEmpty()) {
                        Log.i("MainActivity", "None");
                    } else {
                        for (int i = 0; i < cookies.size(); i++) {
                            if (cookies.get(i).getName().equals("app_id")) {
                                PropertyManager.getInstance().setAppId(cookies.get(i).getValue());
                            }
                            String cookie = cookies.get(i).getName() + "=" + cookies.get(i).getValue();
                            cookieManager.setCookie(cookies.get(i).getDomain(), cookie);
                        }

                        if (TextUtils.isEmpty(PropertyManager.getInstance().getAppId())) {
                            return false;
                        } else {
                            HttpEntity resEntity = responsePost.getEntity();
                            String result = EntityUtils.toString(resEntity, "UTF-8");
                            Document doc = Jsoup.parse(result);
                            Element element = doc.select("section#content").get(0);
                            if (responsePost != null) {
                                callbackObject.result = element;
                                Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
                                mHandler.sendMessage(msg);
                            }
                        }
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                    callbackObject.exception = e;
                    Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
                    mHandler.sendMessage(msg);
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                if (!aBoolean) {
                    Toast.makeText(MyApplication.getContext(), "로그인 정보가 유효하지 않습니다", Toast.LENGTH_SHORT).show();
                    callbackObject.result = null;
                    Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
                    mHandler.sendMessage(msg);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.flush();
                }
            }
        }.execute();
        return httpPost;
    }

    public HttpPost sendReply(Context context, final String url, String pid, String reply, final OnResultListener<Boolean> listener) { //댓글달기
        final CallbackObject<Boolean> callbackObject = new CallbackObject<>();
        final MyRedirectHandler handler = new MyRedirectHandler();
        httpClient.setRedirectHandler(handler);
        final ArrayList<NameValuePair> nameValuePairs =
                new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("contents", reply));
        nameValuePairs.add(new BasicNameValuePair("pid", pid));
        nameValuePairs.add(new BasicNameValuePair("redirect", "eNortjK0UtJXsgZcMAkSAcc."));
        final HttpPost httpPost = new HttpPost(url);
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    callbackObject.request = httpPost;
                    callbackObject.listener = listener;
                    UrlEncodedFormEntity entityRequest =
                            new UrlEncodedFormEntity(nameValuePairs, "UTF-8");

                    httpPost.setEntity(entityRequest);
                    HttpResponse responsePost = httpClient.execute(httpPost);
                    String lastUrl = url;
                    if (handler.lastRedirectedUri != null) {
                        lastUrl = handler.lastRedirectedUri.toString();
                    }
                    if (responsePost != null) {
                        if (responsePost.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            callbackObject.result = true;
                        } else {
                            callbackObject.result = false;
                        }
                        Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
//                e.printStackTrace();
                    callbackObject.exception = e;
                    Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
                    mHandler.sendMessage(msg);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.flush();
                }
            }
        }.execute();
        return httpPost;
    }

}
