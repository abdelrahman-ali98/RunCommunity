package com.abdelrahman.runcommunity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdalrahman on 1/31/2018.
 */

public class MyRunsFragment extends Fragment implements RunsAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<List<RunKeys>>{


    private RunsAdapter mRunsAdapter;
    private RecyclerView mRecyclerView;
    private RunningFragment.OnFragmentInteractionListener mListener;
    List<RunKeys> myList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      //  return super.onCreateView(inflater, container, savedInstanceState);
        if (mListener != null) {mListener.onFragmentInteraction("My Runs");}

        View view= inflater.inflate(R.layout.recycler_container, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRunsAdapter = new RunsAdapter(loadData(), this);
        mRecyclerView.setAdapter(mRunsAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RunningFragment.OnFragmentInteractionListener) {
            mListener = (RunningFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onListItemClick(View view, int clickedID) {
    }

    @Override
    public Loader<List<RunKeys>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<RunKeys>>(getActivity()) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(myList != null){
                    deliverResult(myList);
                }
                else forceLoad();
            }

            @Override
            public List<RunKeys> loadInBackground() {
                return loadData();
            }

            @Override
            public void deliverResult(List<RunKeys> data) {
                myList = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<RunKeys>> loader, List<RunKeys> data) {
    }

    @Override
    public void onLoaderReset(Loader<List<RunKeys>> loader) {
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
    }

    public List<RunKeys> loadData(){
        Cursor cursor = null;
        myList = new ArrayList<RunKeys>();
        try {

            Uri uri = RunsContract.RunsEntry.CONTENT_URI;
            String[] projection = new String[]{
                    RunsContract.RunsEntry.COLUMN_DISTANCE,
                    RunsContract.RunsEntry.COLUMN_DURATION,
                    RunsContract.RunsEntry.COLUMN_DATE,
                    RunsContract.RunsEntry._ID
            };
            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = null;

            cursor = getActivity().getContentResolver().query(uri, projection, selection, selectionArgs,
                    sortOrder);
            if (cursor != null) {
                cursor.moveToFirst();

                for(int i = cursor.getCount(); i>0;i--){
                    RunKeys runKeys = new RunKeys();
                    runKeys.setDistance(cursor.getInt(cursor.getColumnIndex(RunsContract.RunsEntry.COLUMN_DISTANCE)));
                    runKeys.setDate(cursor.getString(cursor.getColumnIndex(RunsContract.RunsEntry.COLUMN_DATE)));
                    runKeys.setDuration(cursor.getInt(cursor.getColumnIndex(RunsContract.RunsEntry.COLUMN_DURATION)));
                    runKeys.setMyId(cursor.getInt(cursor.getColumnIndex(RunsContract.RunsEntry._ID)));
                    myList.add(runKeys);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return myList;
    }
}
