package com.abdelrahman.runcommunity;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdalrahman on 1/21/2018.
 */

class RunsAdapter extends RecyclerView.Adapter<RunsAdapter.NumberViewHolder> {

    final private ListItemClickListener mOnClickListener;
    private List<RunKeys> mList;
    private static final String userDisplayName =FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

    RunsAdapter(List<RunKeys> list, ListItemClickListener listener){
        this.mOnClickListener = listener;
        this.mList = list;
    }
    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdforListItem = R.layout.recycler;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachedToParentImmediately = false;

        View view = inflater.inflate(layoutIdforListItem,parent, shouldAttachedToParentImmediately);
        NumberViewHolder viewHolder = new NumberViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        // here I could set the values in the views
        // holder.textView.setText(my title from my source);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mList== null? 5:mList.size();
    }


    class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ConstraintLayout myConstraintLayout;

        TextView mDistance;
        TextView mDate;
        TextView mTime;
        TextView mAvgSpeed;
        TextView mUsernameV;

        public NumberViewHolder(View itemView) {
            super(itemView);

            mDistance = (TextView) itemView.findViewById(R.id.mDistance);
            mDate = (TextView) itemView.findViewById(R.id.mDate);
            mTime = (TextView) itemView.findViewById(R.id.mTime);
            mAvgSpeed = (TextView) itemView.findViewById(R.id.mAvgSpeed);
            mUsernameV = (TextView) itemView.findViewById(R.id.usernamE);


            myConstraintLayout = (ConstraintLayout) itemView.findViewById(R.id.myContainer);
            myConstraintLayout.setOnClickListener(this);
        }
        void bind(int listIndex){

            int getDistanceValueFromList = mList.get(listIndex).getDistance();
            int getDurationValueFromList =  mList.get(listIndex).getDuration();
            String getDateValueFromList = mList.get(listIndex).getDate();
            mDistance.setText(String.valueOf(getDistanceValueFromList));
            mDate.setText(getDateValueFromList);
            mUsernameV.setText(userDisplayName);
            {
               int timeHours = ((int)getDurationValueFromList/3600);
                int timeMins = (int )((getDurationValueFromList- (timeHours*3600))/60);
                int timeSecs = getDurationValueFromList- (timeHours*3600)-(timeMins*60);
                if(timeHours <=0) {
                    mTime.setText(String.valueOf(timeMins)+":"+String.valueOf(timeSecs));
                }else{
                    mTime.setText(String.valueOf(timeHours)+":"+String.valueOf(timeMins)+":"+String.valueOf(timeSecs));
                }
            }

                try{
                float myAvgSpeeed = (float) getDistanceValueFromList/getDurationValueFromList;
                float minPerKmSpeed = (float) (16.66666 / (myAvgSpeeed));

                int speedMins = (int)(minPerKmSpeed);
                int speedSecs= (int) (minPerKmSpeed-speedMins)*60;
                if(speedMins>59){
                mAvgSpeed.setText("layover");
                }else{
                    mAvgSpeed.setText(String.valueOf(speedMins)+":"+String.valueOf(speedSecs));
                }
            }catch (Exception e){
                }

        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            int o = mList.get(clickedPosition).getMyId();
            mOnClickListener.onListItemClick(view,o);
        }
    }

    public interface ListItemClickListener{void onListItemClick( View view, int clickedID);}
}

