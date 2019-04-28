package com.example.qrallye;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCallback} interface
 * to handle interaction events.
 * Use the {@link ProgressFragment#} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

    private static final String TAG = "ProgressFragment";
    private FragmentCallback mListener;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener.progressFragmentInitialisation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        changeDisplay(DisplayState.LOADING, view);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentCallback) {
            mListener = (FragmentCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private enum DisplayState{
        LOADING, LIST
    }

    private void changeDisplay(DisplayState d, View view){
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        RecyclerView recyclerView = view.findViewById(R.id.progressList);
        switch (d){
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                break;
            case LIST:
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
        }
    }


    public void progressListRetrieved() {
        try{
            ArrayList list = QuizMGR.getInstance().getProgressList();
            Collections.sort(list, Collections.reverseOrder(new Comparator<ProgressItem>() {
                @Override
                public int compare(ProgressItem item1, ProgressItem item2)
                {
                    return  item1.getQuizzesCount() - item2.getQuizzesCount();
                }
            }));
            RecyclerView recyclerView = getView().findViewById(R.id.progressList);
            recyclerView.setAdapter(new ProgressListAdapter(list));
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            changeDisplay(DisplayState.LIST, getView());
        }catch (Exception e){
            Log.e(TAG, "progressListRetrieved: ", e);
        }

    }

    private class ProgressListAdapter extends RecyclerView.Adapter<ProgressListAdapter.MyViewHolder> {
        private ArrayList<ProgressItem> mDataset;

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView teamView;
            TextView chrono;
            TextView quizzesCount;

            MyViewHolder(View v) {
                super(v);
                teamView = v.findViewById(R.id.team);
                chrono = v.findViewById(R.id.chrono);
                quizzesCount = v.findViewById(R.id.quizzesCount);
            }
        }


        ProgressListAdapter(ArrayList<ProgressItem> myDataset) {
            mDataset= new ArrayList<>();
            mDataset.addAll(myDataset);
        }

        @Override
        public ProgressListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_list_item, parent, false);

            ProgressListAdapter.MyViewHolder vh = new ProgressListAdapter.MyViewHolder(v);

            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ProgressListAdapter.MyViewHolder holder, final int position) {
            holder.teamView.setText(mDataset.get(position).getTeam());
            holder.quizzesCount.setText("x"+mDataset.get(position).getQuizzesCount());

            Long timeInMillis = mDataset.get(position).getChrono();
            Date date = new Date(timeInMillis);
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            String dateFormatted = formatter.format(date);

            holder.chrono.setText(dateFormatted);

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    }

}
