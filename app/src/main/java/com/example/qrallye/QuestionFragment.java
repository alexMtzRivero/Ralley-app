package com.example.qrallye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrallye.databinding.ResponseListItemBinding;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCallback} interface
 * to handle interaction events.
 * Use the {@link QuestionFragment#} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {


    private FragmentCallback mListener;

    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.responsesList);

        String[] list = {"", "", ""};
        ResponsesListAdapter responsesListAdapter = new ResponsesListAdapter(list);
        recyclerView.setAdapter(responsesListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                1);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.response_item_decoration));
        recyclerView.addItemDecoration(dividerItemDecoration);
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

    class ResponsesListAdapter extends RecyclerView.Adapter<ResponsesListAdapter.MyViewHolder> {
        private String[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class MyViewHolder extends RecyclerView.ViewHolder {
            private final ResponseListItemBinding binding;
            // each data item is just a string in this case
            MyViewHolder(ResponseListItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(boolean bool) {
                binding.setSelected(bool);
                binding.executePendingBindings();
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       binding.setSelected(true);
                       new Handler().postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               binding.setSelected(false);
                           }
                        },500);
                    }
                });
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        ResponsesListAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ResponsesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater  layoutInflater = LayoutInflater.from(parent.getContext());
            ResponseListItemBinding responseListItemBinding = ResponseListItemBinding.inflate(layoutInflater, parent, false);

            MyViewHolder vh = new MyViewHolder(responseListItemBinding);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.bind(false);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return 1;
            else return 2;
        }
    }
}
