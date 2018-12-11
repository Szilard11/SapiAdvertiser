package ro.sapientia.ms.sapiadvertiser;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<NewsModel> mNewsList = new ArrayList<>();
    //private final ViewHolder.OnItemClickListener mListener;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<NewsModel> newsList, Context context) {
        this.mNewsList = newsList;
        //this.mListener = listener;
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
        holder.mCounterView.setText(mNewsList.get(position).getmCounter());
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,AdvDetailActivity.class);
                intent.putExtra("news_id",mNewsList.get(position).getmNewsId());
                intent.putExtra("user_id",mNewsList.get(position).getmUserId());
                mContext.startActivity(intent);
            }
        });
        //holder.bind(mNewsList.get(position), mListener); //lehet igy is megy
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
        /*public void bind(final NewsModel item, final OnItemClickListener listener) {
            //title.setText(item.getTitle());
            //date.setText(item.getDate());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
        //Interface to handle click event
        public interface OnItemClickListener {
            void onItemClick(NewsModel item);
        }*/
    }
}
