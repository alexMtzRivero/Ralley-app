package com.example.qrallye;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import static android.support.v7.widget.RecyclerView.VERTICAL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCallback} interface
 * to handle interaction events.
 * Use the {@link QuizzFragment#} factory method to
 * create an instance of this fragment.
 */
public class QuizzFragment extends Fragment {

    private static final String TAG = "Quizzfragment";
    private FragmentCallback mListener;
    ArrayList<Quiz> quizList = new ArrayList<>();
    LocationsListAdapter locationsListAdapter;
    RecyclerView recyclerView;
    ArrayList<Quiz> finishedQuizList;

    private enum DisplayState{
        LOADING, LIST
    }

    public QuizzFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quizz, container, false);
        recyclerView = view.findViewById(R.id.locationsList);

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void changeDisplay(DisplayState d, View view){
        ProgressBar progressBar = view.findViewById(R.id.quizzesProgressBar);
        RecyclerView recyclerView = view.findViewById(R.id.locationsList);
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

    class LocationsListAdapter extends RecyclerView.Adapter<LocationsListAdapter.MyViewHolder> {
        private ArrayList<Quiz> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public MyViewHolder(View v) {
                super(v);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public LocationsListAdapter(ArrayList<Quiz> myDataset) {
            mDataset= new ArrayList<>();
            mDataset.add(new Quiz());
            mDataset.addAll(myDataset);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public LocationsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            if(viewType == 1){
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_list_header, parent, false);
            }else{
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_list_item, parent, false);
            }
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            if(position != 0 ) {
                TextView name = holder.itemView.findViewById(R.id.quizName);
                TextView place = holder.itemView.findViewById(R.id.lieu);
                TextView timer = holder.itemView.findViewById(R.id.timer);
                ImageView img = holder.itemView.findViewById(R.id.img);
                name.setText(mDataset.get(position ).getId());
                place.setText(mDataset.get(position ).getNomQuiz());
                for (Quiz quiz : finishedQuizList) {
                    if(mDataset.get(position).getId().compareTo(quiz.getId()) == 0 && quiz.getEndQuiz() != null)
                    {
                        img.setImageResource(R.drawable.ic_valide);
                        long time = quiz.getEndQuiz().getTime() - quiz.getStartQuiz().getTime();
                        long minute = time / (60 *1000);
                        long sec = (time/1000)%60;
                        timer.setText(String.valueOf(String.format("%d:%02d",minute,sec)));
                    }
                }
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return 1;
            else return 2;
        }



    }

    public void quizzesRetrieved() {
        refreshAdapter();
    }

    public void finishedQuizzesRetrieved(){

    }

    private void refreshAdapter(){
        quizList = QuizMGR.getInstance().getQuizList();
        finishedQuizList = QuizMGR.getInstance().getFinishedQuizList();

        if(quizList != null && finishedQuizList != null){
            locationsListAdapter = new LocationsListAdapter(quizList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
            recyclerView.addItemDecoration(itemDecor);
            recyclerView.setAdapter(locationsListAdapter);
            changeDisplay(DisplayState.LIST, getView());
        }
    }
}
