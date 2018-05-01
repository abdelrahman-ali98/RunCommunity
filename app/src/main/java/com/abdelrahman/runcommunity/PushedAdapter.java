package com.abdelrahman.runcommunity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdalrahman on 2/1/2018.
 */

 class PushedAdapter extends RecyclerView.Adapter<PushedAdapter.NumberViewHolderPushed> {

    private List<EeddKeys> mList;
    final private PushedAdapterClickListener mOnClickListener;
    private final int BTN_OFF =17301515;
    private final int BTN_ON =17301516;
    private final String finalUser = FirebaseAuth.getInstance().getCurrentUser().getUid();



    PushedAdapter(List<EeddKeys> list,PushedAdapterClickListener k){
        this.mOnClickListener =k;
        this.mList = list;
    }

    @Override
    public NumberViewHolderPushed onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdforListItem = R.layout.pushed_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdforListItem,parent,false);
        NumberViewHolderPushed viewHolderPushed = new NumberViewHolderPushed(view);

        return viewHolderPushed;
    }

    @Override
    public void onBindViewHolder(NumberViewHolderPushed holder, int position) {

        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mList== null? 5:mList.size();
    }

    class NumberViewHolderPushed extends RecyclerView.ViewHolder  implements View.OnClickListener{


        private TextView mDetails;
        private TextView mDate;
        private TextView mUsernameV;
        private ImageButton mImageButton;
        private TextView mCounterV;

        public NumberViewHolderPushed(View itemView) {
            super(itemView);

            mDetails = (TextView) itemView.findViewById(R.id.pushedDetails);
            mDate = (TextView) itemView.findViewById(R.id.pushedDate);
            mUsernameV = (TextView) itemView.findViewById(R.id.pushedUsername);
            mImageButton = (ImageButton) itemView.findViewById(R.id.mImageButton);
            mCounterV = (TextView) itemView.findViewById(R.id.mCounterId);
            mImageButton.setOnClickListener(this);

        }
        void bind(int listIndex){
            EeddKeys eeddKeysObject =mList.get(listIndex);
            mDate.setText(eeddKeysObject.getPushedDate());
            mUsernameV.setText(eeddKeysObject.getPushedusername());
            mDetails.setText(eeddKeysObject.getPushedDetails());
            mCounterV.setText(String.valueOf(eeddKeysObject.getCounter()));
            List<String> kk = eeddKeysObject.getNamed();
            for(int i=0; i<kk.size();i++) {
                if (kk.get(i).equals(finalUser)){
                    mImageButton.setImageResource(BTN_ON);
                    break;
                }
            }
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            String o = mList.get(clickedPosition).getNodeKey();
            int oldCounter = mList.get(clickedPosition).getCounter();
            boolean isAlreadyExist= false;
            int imageInt= BTN_ON;
            List<String> mListObject = mList.get(clickedPosition).getNamed();
            for(int i=0; i<mListObject.size();i++) {
                if (mListObject.get(i).equals(finalUser)){
                    isAlreadyExist = true;
                     imageInt = BTN_OFF;
                    break;
                }
            }
            if(isAlreadyExist && mList.get(clickedPosition)
                    .getPushedusername().equals(
                            FirebaseAuth.getInstance()
                                    .getCurrentUser().getDisplayName())){
                return;
            }
            mOnClickListener.onImageButtonClick(clickedPosition, o, oldCounter,isAlreadyExist);
            ((ImageButton) view).setImageResource(imageInt);
        }
    }
    public interface PushedAdapterClickListener{void onImageButtonClick(int clickedID
            ,String nodeKey, int oldCounter,boolean alreadyExist);}
}
