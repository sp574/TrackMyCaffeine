package sleeping_vityaz.trackmycaffeine.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import sleeping_vityaz.trackmycaffeine.R;

/**
 * Created by naja-ox on 1/29/15.
 */
public class AboutFragment extends Fragment {

    TextView tv_version, tv_about, tv_license, tv_about_message, tv_license_message;

    private FragmentActivity myContext;
    private Context mContext = this.getActivity();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        findViewsById(rootView);

        return rootView;
    }

    private void findViewsById(View rootView) {

        tv_version = (TextView) rootView.findViewById(R.id.tv_version);
        tv_about = (TextView) rootView.findViewById(R.id.tv_about);
        tv_about_message = (TextView) rootView.findViewById(R.id.tv_about_message);
        tv_license= (TextView) rootView.findViewById(R.id.tv_license);
        tv_license_message = (TextView) rootView.findViewById(R.id.tv_license_message);


    }


}
