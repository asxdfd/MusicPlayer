package com.example.lenovo.musicplayer.fragment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.lenovo.musicplayer.R;
import com.example.lenovo.musicplayer.model.Music;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements BottomNavigationBar.OnTabSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private BottomNavigationBar bottomNavigationBar;
    private String TAG = HomeFragment.class.getSimpleName();
    int lastSelectedPosition = 0;
    private MusicFragment mMusicFragment;
    private VideoFragment mVideoFragment;
    private LocationFragment mLocationFragment;

    private static HomeFragment homeFragment;

    @SuppressLint("ValidFragment")
    private HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment getHomeFragment() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        return homeFragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        bottomNavigationBar = (BottomNavigationBar) view.findViewById(R.id.nav_bottom);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_music, "音乐").setActiveColor(R.color.orange))
                .addItem(new BottomNavigationItem(R.drawable.ic_video, "视频").setActiveColor(R.color.blue))
                .addItem(new BottomNavigationItem(R.drawable.ic_location, "附近").setActiveColor(R.color.green))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mMusicFragment = MusicFragment.newInstance(1);
        transaction.replace(R.id.app_content, mMusicFragment);
        transaction.commit();
    }

    @Override
    public void onTabSelected(int position) {
        Log.d(TAG, "onTabSelected() called with: " + "position = [" + position + "]");
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (mMusicFragment == null) {
                    mMusicFragment = MusicFragment.newInstance(1);
                }
                transaction.replace(R.id.app_content, mMusicFragment);
                break;
            case 1:
                if (mVideoFragment == null) {
                    mVideoFragment = VideoFragment.newInstance(1);
                }
                transaction.replace(R.id.app_content, mVideoFragment);
                break;
            case 2:
                if (mLocationFragment == null) {
                    mLocationFragment = LocationFragment.newInstance("附近");
                }
                transaction.replace(R.id.app_content, mLocationFragment);
                break;
            default:
                break;
        }
        // 事务提交
        transaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {
        Log.d(TAG, "onTabUnselected() called with: " + "position = [" + position + "]");
    }

    @Override
    public void onTabReselected(int position) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public MusicFragment getmMusicFragment() {
        return mMusicFragment;
    }
}
