package com.example.myp2p;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myp2p.model.DeviceDTO;

import java.util.ArrayList;
import java.util.List;


public class PeerListFragment extends Fragment {

    public static final String ARG_DEVICE_LIST = "device_list";

    private OnListFragmentInteractionListener mListener;

    private List<DeviceDTO> devices = null;

    private RecyclerView recyclerView;

    public PeerListFragment() {
        devices = new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public static PeerListFragment newInstance(int columnCount) {
        PeerListFragment fragment = new PeerListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DEVICE_LIST, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devices = new ArrayList<>();

        if (getArguments() != null) {
            devices = (List<DeviceDTO>) getArguments().getSerializable(ARG_DEVICE_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new PeerListAdapter(devices, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(DeviceDTO deviceDTO);
    }
}
