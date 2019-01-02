package ro.sapientia.ms.sapiadvertiser.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.List;

import ro.sapientia.ms.sapiadvertiser.R;

public class RecyclerViewAdapter_LoadImages extends RecyclerView.Adapter<RecyclerViewAdapter_LoadImages.ViewHolder> {

    private List<Uri> mImageList;
    private Context mContext;

    public RecyclerViewAdapter_LoadImages(List<Uri> imageList, Context context) {
        this.mImageList = imageList;
        this.mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_imagemodel, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mImageList.get(position))
                .into(holder.mImage);
    }
    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImage;

        public ViewHolder(View view) {
            super(view);
            mImage = view.findViewById(R.id.viewImage);
        }
    }
}
