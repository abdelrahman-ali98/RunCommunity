package com.abdelrahman.runcommunity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abdalrahman on 2/1/2018.
 */

public class PushedFragment extends Fragment implements PushedAdapter.PushedAdapterClickListener {

    private FirebaseDatabase mFirebaseDatabase;
    private Query mDatabaseReference;
    private ValueEventListener mValueEventListener;
    private ValueEventListener mValueEventListenerNode;
    private RecyclerView mRecyclerView;
    private PushedAdapter mPushedAdapter;
    private List<EeddKeys> myList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("eedd").limitToLast(20);

        View view = inflater.inflate(R.layout.recycler_container,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        myList = new ArrayList<>();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPushedAdapter = null;
                try {
                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                        EeddKeys mEeddKeys = mDataSnapshot.getValue(EeddKeys.class);
                        mEeddKeys.setNodeKey(mDataSnapshot.getKey());
                        myList.add(mEeddKeys);
                        FirebaseDatabase.getInstance().getReference()
                                .child("eedd")
                                .child(mDataSnapshot.getKey())
                                .addValueEventListener(mValueEventListenerNode);
                    }
                        mPushedAdapter = new PushedAdapter(myList,PushedFragment.this);
                        mRecyclerView.setAdapter(mPushedAdapter);

                }catch (Exception e){
                }finally {
                    mDatabaseReference.removeEventListener(mValueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        mValueEventListenerNode = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(int i= 0;i<myList.size();i++){
                    if(myList.get(i).getNodeKey().equals(dataSnapshot.getKey())){

                        myList.get(i).setPushedDate(dataSnapshot
                                .child("pushedDate").getValue().toString());
                        myList.get(i).setPushedusername(dataSnapshot
                                .child("pushedusername").getValue().toString());
                        myList.get(i).setPushedDetails(dataSnapshot
                                .child("pushedDetails").getValue().toString());
                        myList.get(i).setCounter(Integer.parseInt(
                                dataSnapshot.child("counter").getValue().toString()));
                        mPushedAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatabaseReference.removeEventListener(mValueEventListener);
        FirebaseDatabase.getInstance().getReference().removeEventListener(mValueEventListenerNode);

    }

    @Override
    public void onResume() {
        super.onResume();
        mDatabaseReference.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onImageButtonClick(final int clickedID, String nodeId,  int oldCounter,boolean isAlreadyExist) {

        Map<String, Object> map= new HashMap<>();
        List<String> mListObject = myList.get(clickedID).getNamed();

        if (!isAlreadyExist){
            final int newValue = ++oldCounter;
            mListObject.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
         map = new HashMap<String, Object>() {{
            put("counter", new Long(newValue));
            put("named", myList.get(clickedID).getNamed());
        }};
        } else {
            final int newValue = --oldCounter;
            for (int i=0; i<myList.size();i++) {
                if (mListObject.get(i).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    mListObject.remove(i);
                    map = new HashMap<String, Object>() {{
                        put("counter", new Long(newValue));
                        put("named", myList.get(clickedID).getNamed());
                    }};
                    break;
                }
            }
        }

        DatabaseReference updateCounter = mFirebaseDatabase.getReference()
                .child("eedd")
                .child(nodeId);
        updateCounter.updateChildren(map);
    }
}
