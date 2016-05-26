package rnd.gw.plani.co.kr.groupware;


import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import net.htmlparser.jericho.HTMLElementName;

import org.apache.http.HttpRequest;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebContentFragment extends Fragment {


    public WebContentFragment() {
        // Required empty public constructor
    }

    MyWebView webView;
    Button replyBtn;

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_CATEGORY = "category";

    String url = "";
    String category = "";
    DownloadManager mDownloadManager = null;
    String DIRETORY = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(EXTRA_URL, "");
            category = getArguments().getString(EXTRA_CATEGORY, "");
        }
        mDownloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        DIRETORY = Environment.getExternalStorageDirectory() + File.separator + "groupware";
        final File destinationDir = new File(DIRETORY, getActivity().getPackageName());
        if (!destinationDir.exists()) {
            destinationDir.mkdir();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_content, container, false);

        webView = (MyWebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("attachment")) {
                    Uri source = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(source);
                    request.setTitle(url.toString());

                    File destinationFile = new File(DIRETORY, source.getLastPathSegment());
                    request.setDestinationUri(Uri.fromFile(destinationFile));
                    mDownloadManager.enqueue(request);
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                return true;
            }
        });

        replyBtn = (Button) view.findViewById(R.id.btn_reply);
        ((WebViewActivity) getActivity()).toolbar.setVisibility(View.GONE);

        if (category.equals("업무연락") || category.equals("댓글알림") || category.contains("커뮤니티")) {
            setContact();
        } else {
            setNote();
        }


        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WebViewActivity) getActivity()).changeReply();
            }
        });
        return view;
    }

    ProgressDialog dialog = null;

    public void setNote() {
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("");
        dialog.setMessage("loading...");
        dialog.setCancelable(false);
        dialog.show();
        NetworkManager.getInstance().getNote(getContext(), url, new NetworkManager.OnResultListener<Element>() {
            @Override
            public void onSuccess(HttpRequest request, Element result) {
                loadWebView(result.toString());
                dialog.dismiss();
            }

            @Override
            public void onFailure(HttpRequest request, int code, Throwable cause) {
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.alert_title)
                        .setMessage("네트워크 연결 상태를 확인해주세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .show();
            }
        });
    }

    public void setContact() {
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("");
        dialog.setMessage("loading...");
        dialog.setCancelable(false);
        dialog.show();
        NetworkManager.getInstance().getContactSection(getContext(), url, new NetworkManager.OnResultListener<ContentData>() {
            @Override
            public void onSuccess(HttpRequest request, ContentData result) {
                Element content = result.element.getElementsByTag(HTMLElementName.TABLE).get(0);
                Log.i("WebContent", content.getElementsByClass("btn-group").toString());
                content.getElementsByClass("btn-group").remove();
                if(content.hasClass("bbs_writer")) {
                    content.getElementsByClass("bbs_writer").get(0).getElementsByTag(HTMLElementName.SPAN).get(1).remove();
                }
                Element reply = result.element.getElementsByClass("comment_entries").get(0);
                List<Element> btn = reply.getElementsByClass("comment_btn");
                for (int i = 0; i < btn.size(); i++) {
                    btn.get(i).remove();
                }
                String tableId = result.tableId;
                ((WebViewActivity) getActivity()).setTableid(tableId);
                String realSection = content.toString() + reply.toString();
                if (!TextUtils.isEmpty(realSection)) {
                    loadWebView(realSection);
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(HttpRequest request, int code, Throwable cause) {
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.alert_title)
                        .setMessage("네트워크 연결 상태를 확인해주세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .show();
            }
        });


    }


    public void loadWebView(String data) {
        webView.loadData(getString(R.string.html_header) + data, "text/html; charset=UTF-8", null);
    }
}
