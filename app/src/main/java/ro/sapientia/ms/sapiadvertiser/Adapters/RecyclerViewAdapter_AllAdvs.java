package ro.sapientia.ms.sapiadvertiser.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import ro.sapientia.ms.sapiadvertiser.AdvDetailActivity;
import ro.sapientia.ms.sapiadvertiser.NewsModel;
import ro.sapientia.ms.sapiadvertiser.R;

import static java.lang.Integer.parseInt;

public class RecyclerViewAdapter_AllAdvs extends RecyclerView.Adapter<RecyclerViewAdapter_AllAdvs.ViewHolder> {

    private ArrayList<NewsModel> mNewsList;
    private Context mContext;

    public RecyclerViewAdapter_AllAdvs(ArrayList<NewsModel> newsList, Context context) {
        this.mNewsList = newsList;
        this.mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_newsmodel, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mNewsList.get(position).getmImage())
                .into(holder.mImage);
        Glide.with(mContext)
                .asBitmap()
                .load(mNewsList.get(position).getmProfileImage())
                .into(holder.mProfileImage);
        holder.mShortDescription.setText(mNewsList.get(position).getmDescription());
        holder.mTitle.setText(mNewsList.get(position).getmTitle());
        holder.mCounterView.setText(mNewsList.get(position).getmCounter().toString());
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateViewCounter(mNewsList.get(position).getmNewsId());
                Intent intent = new Intent(mContext,AdvDetailActivity.class);
                intent.putExtra("news_id",mNewsList.get(position).getmNewsId());
                intent.putExtra("user_id",mNewsList.get(position).getmUserId());
                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImage;
        public TextView mCounterView;
        public TextView mTitle;
        public TextView mShortDescription;
        public CircleImageView mProfileImage;
        public ConstraintLayout mLayout;

        public ViewHolder(View view) {
            super(view);
            mImage = view.findViewById(R.id.viewImage);
            mCounterView = view.findViewById(R.id.counter);
            mTitle = view.findViewById(R.id.viewTitle);
            mShortDescription = view.findViewById(R.id.shortDesc);
            mLayout = view.findViewById(R.id.parent_layout);
            mProfileImage = view.findViewById(R.id.profile_image);
        }
    }

    private void updateViewCounter(String pNewsId)
    {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("sapiAdvertisments/"+pNewsId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = parseInt(dataSnapshot.child("ViewCounter").getValue().toString());
                database.child("ViewCounter").setValue(value+1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
