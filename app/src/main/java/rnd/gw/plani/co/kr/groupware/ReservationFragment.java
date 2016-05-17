package rnd.gw.plani.co.kr.groupware;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationFragment extends Fragment {


    public ReservationFragment() {
        // Required empty public constructor
    }

    MyWebView webView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        webView = (MyWebView)view.findViewById(R.id.webView);

        Toast.makeText(getContext(), "예약현황", Toast.LENGTH_SHORT).show();
        return view;
    }

}
