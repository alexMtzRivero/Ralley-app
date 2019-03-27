package com.example.qrallye;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qrallye.databinding.ResponseListItemBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCallback} interface
 * to handle interaction events.
 * Use the {@link QuestionFragment#} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {

    public class Response{
        private int position;
        private String response;

        Response(int position, String response){
            this.position = position;
            this.response = response;
        }
    }

    private FragmentCallback mListener;
    private ArrayList<Response> responses;
    private ArrayList<Question> questions;
    private int index;

    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseMGR.getInstance().getQuestionsFromQuiz(getArguments().getString("key"));
        responses = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.responsesList);

        ResponsesListAdapter responsesListAdapter = new ResponsesListAdapter(new ArrayList<String>());
        recyclerView.setAdapter(responsesListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                1);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.response_item_decoration));
        recyclerView.addItemDecoration(dividerItemDecoration);

        new LongOperation().execute();

        return view;
    }


    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            while(!QuizMGR.getInstance().complete){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            questions = QuizMGR.getInstance().getQuestionList();
            index = 0;
            updateQuestion();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void updateQuestion() {
        RecyclerView recyclerView = getView().findViewById(R.id.responsesList);
        ArrayList responses = new ArrayList();
        TextView questionView = getActivity().findViewById(R.id.questionText);
        questionView.setText(questions.get(index).getQuestion());
        responses.addAll(questions.get(index).getChoices());
        ResponsesListAdapter responsesListAdapter = new ResponsesListAdapter(responses);
        recyclerView.setAdapter(responsesListAdapter);
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

    class ResponsesListAdapter extends RecyclerView.Adapter<ResponsesListAdapter.MyViewHolder> {
        private ArrayList<String> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            MyViewHolder(View v) {
                super(v);
            }

        }

        // Provide a suitable constructor (depends on the kind of dataset)
        ResponsesListAdapter(ArrayList<String> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ResponsesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.response_list_item, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            TextView responseView = holder.itemView.findViewById(R.id.responseItem);
            responseView.setText(mDataset.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setBackgroundColor(getResources().getColor(R.color.appGreen));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            validateQuestion(position, mDataset.get(position));
                        }
                    }, 500);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    private void validateQuestion(int position, String response){
        responses.add(new Response(position, response));
        index++;
        if(index < questions.size()){
            updateQuestion();
        }else{

        }
    }
}
