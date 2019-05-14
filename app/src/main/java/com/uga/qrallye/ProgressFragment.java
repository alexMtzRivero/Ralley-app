package com.uga.qrallye;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.support.v7.widget.RecyclerView.VERTICAL;


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
        RecyclerView recyclerView = view.findViewById(R.id.progressList);
        try{
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
            recyclerView.addItemDecoration(itemDecor);
        }catch(Exception e){
            Log.e(TAG, "onCreateView: ", e);
        }


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
            View itemView;

            MyViewHolder(View v) {
                super(v);
                teamView = v.findViewById(R.id.team);
                chrono = v.findViewById(R.id.chrono);
                quizzesCount = v.findViewById(R.id.quizzesCount);
                itemView = v;
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
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            ProgressItem pi = mDataset.get(position);
            holder.teamView.setText(pi.getTeam());
            holder.quizzesCount.setText("x"+pi.getQuizzesCount());

            Long timeInMillis = pi.getChrono();
            int hrs = (int) TimeUnit.MILLISECONDS.toHours(timeInMillis) % 24;
            int min = (int) TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60;
            String dateFormatted = String.format(Locale.FRANCE, "%02d:%02d", hrs, min);

            holder.chrono.setText(dateFormatted);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && pi.getTeam().equals(SessionMGR.getInstance().getLogedTeam().getName())) {
                try{
                    holder.itemView.setBackground(getResources().getDrawable(R.drawable.gradient));
                }catch(Exception e){
                    Log.e(TAG, "onBindViewHolder: ", e);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    }

}
