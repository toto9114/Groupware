package rnd.gw.plani.co.kr.groupware;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

import java.net.URI;

/**
 * Created by RND on 2016-05-23.
 */
public class MyRedirectHandler extends DefaultRedirectHandler{public URI lastRedirectedUri;

    @Override
    public boolean isRedirectRequested(HttpResponse response, HttpContext context) {

        return super.isRedirectRequested(response, context);
    }

    @Override
    public URI getLocationURI(HttpResponse response, HttpContext context)
            throws ProtocolException {

        lastRedirectedUri = super.getLocationURI(response, context);

        return lastRedirectedUri;
    }

}
