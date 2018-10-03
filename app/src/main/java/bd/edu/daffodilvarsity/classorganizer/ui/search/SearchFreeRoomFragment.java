package bd.edu.daffodilvarsity.classorganizer.ui.search;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFreeRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFreeRoomFragment extends Fragment {

    private static final String TAG = "SearchFreeRoomFragment";

    @BindView(R.id.search_fragment_options_card) CardView mOptionsContainer;
    @BindView(R.id.search_fragment_no_result_text) TextView mNoResultText;
    @BindView(R.id.search_fragment_result_list) RecyclerView mResultView;
    @BindView(R.id.search_fragment_progress)
    MaterialProgressBar mProgress;

    private View mView;
    private SearchViewModel mViewModel;
    private SearchResultAdapter mAdapter;



    public SearchFreeRoomFragment() {
        // Required empty public constructor
    }

    public static SearchFreeRoomFragment newInstance() {
        return new SearchFreeRoomFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, mView);
        // Inflate the layout for this fragment
        setupView(inflater);
        return mView;
    }

    private void setupView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.layout_search_free_room, mOptionsContainer, true);
        mViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        mViewModel.getProgressListener().observe(getActivity(), aBoolean -> {
            if (aBoolean == null || !aBoolean) {
                mProgress.setVisibility(View.GONE);
            } else {
                mProgress.setVisibility(View.VISIBLE);
                mProgress.setIndeterminate(true);
            }
        });
        RoomOptionsViewHolder holder = new RoomOptionsViewHolder(view);
        mAdapter = new SearchResultAdapter(new ArrayList<>(), SearchResultAdapter.RESULT_TYPE_SECTION_CLASS);
        mResultView.setAdapter(mAdapter);
        mResultView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mViewModel.getFreeRoomListListener().observe(getActivity(), listResource -> {
            if (listResource != null) {
                switch (listResource.getStatus()) {
                    case ERROR:
                        mResultView.setVisibility(View.GONE);
                        mNoResultText.setVisibility(View.VISIBLE);
                        mNoResultText.setText("No free rooms found! ¯\\_(ツ)_/¯");
                        break;
                    case LOADING:
                        mNoResultText.setVisibility(View.GONE);
                        mResultView.setVisibility(View.GONE);
                        break;
                    case SUCCESSFUL:
                        if (listResource.getData().size() > 0) {
                            mNoResultText.setVisibility(View.GONE);
                            mResultView.setVisibility(View.VISIBLE);
                            mAdapter.replaceData(listResource.getData());
                        } else {
                            mResultView.setVisibility(View.GONE);
                            mNoResultText.setVisibility(View.VISIBLE);
                            mNoResultText.setText("No free rooms found! ¯\\_(ツ)_/¯");
                        }

                        break;
                }
            }
        });

    }

    class RoomOptionsViewHolder {

        private View mOptionViewLayout;
        @BindView(R.id.lsfr_day_spinner)
        AppCompatSpinner mDaySpinner;

        @BindView(R.id.lsfr_time_spinner)
        AppCompatSpinner mTimeSpinner;

        @BindView(R.id.lsfr_search_button)
        MaterialButton mSearchButton;

        RoomOptionsViewHolder(View view) {
            this.mOptionViewLayout = view;
            ButterKnife.bind(this, mOptionViewLayout);
            setupView();
        }

        private void setupView() {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.weekdays, R.layout.spinner_row);
            mDaySpinner.setAdapter(adapter);
            mViewModel.getTimeListListener().observe(getActivity(), listResource -> {
                if (listResource != null) {
                    switch (listResource.getStatus()) {
                        case LOADING:
                            disableTimeSpinner();
                            break;
                        case ERROR:
                            disableTimeSpinner();
                            break;
                        case SUCCESSFUL:
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row, listResource.getData());
                            enableTimeSpinner();
                            mTimeSpinner.setAdapter(arrayAdapter);
                            break;
                    }
                }
            });

            mSearchButton.setOnClickListener(v ->
                    mViewModel.searchFreeRooms(mTimeSpinner.getSelectedItem().toString(), mDaySpinner.getSelectedItem().toString()));

        }

        private void disableTimeSpinner() {
            mTimeSpinner.setEnabled(false);
            mSearchButton.setEnabled(false);

        }

        private void enableTimeSpinner() {
            mTimeSpinner.setEnabled(true);
            mSearchButton.setEnabled(true);
        }

    }


}