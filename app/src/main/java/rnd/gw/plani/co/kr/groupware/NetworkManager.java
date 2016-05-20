package rnd.gw.plani.co.kr.groupware;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;

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
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            CookieSyncManager.createInstance(MyApplication.getContext());
//        }
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



    public HttpGet getAllNotify(Context context, String url, final OnResultListener<Element> listener){
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
                    Element element = doc.select("div.gw_feed.feeds_page").get(0);

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
    public HttpGet getCustomer(Context context, String url, final OnResultListener<Element> listener){
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
                    Element element = doc.select("table.bbs_table.table-hover").get(0);
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

    public HttpGet getContactSection(Context context, String url, final OnResultListener<Element> listener){
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
                    Element element = doc.select("section#content").get(0);
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


    private static final String DO_LOGIN_URL = "http://gw.plani.co.kr/login/accounts/do_login/redirect/eNortjK0UtJXsgZcMAkSAcc.";

    public HttpPost login(Context context, String url , String id, String password, final OnResultListener<Element> listener) {
        final CallbackObject<Element> callbackObject = new CallbackObject<>();
        final ArrayList<NameValuePair> nameValuePairs =
                new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("userid", id));
        nameValuePairs.add(new BasicNameValuePair("passwd", password));
        final HttpPost httpPost = new HttpPost(url+"/login/accounts/do_login/redirect/eNortjK0UtJXsgZcMAkSAcc.");
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
                        Thread.sleep(500);
                    }

                    HttpEntity resEntity = responsePost.getEntity();
                    String result = EntityUtils.toString(resEntity, "UTF-8");
                    Document doc = Jsoup.parse(result);
                    Element element = doc.select("section#content").get(0);
                    if (responsePost != null) {
                        callbackObject.result = element;
                        Message msg = mHandler.obtainMessage(MESSAGE_SUCCESS, callbackObject);
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
//                e.printStackTrace();
                    callbackObject.exception = e;
                    Message msg = mHandler.obtainMessage(MESSAGE_FAILURE, callbackObject);
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
    public HttpPost sendReply(Context context, String url , String reply, final OnResultListener<Boolean> listener) {
        final CallbackObject<Boolean> callbackObject = new CallbackObject<>();
        final ArrayList<NameValuePair> nameValuePairs =
                new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("contents", reply));
        final HttpPost httpPost = new HttpPost(url+"/reception/reception/comment_save/tableid/liaison/id/2859882");
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

                    if (responsePost != null) {
                        if(responsePost.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                            callbackObject.result = true;
                        }else{
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
