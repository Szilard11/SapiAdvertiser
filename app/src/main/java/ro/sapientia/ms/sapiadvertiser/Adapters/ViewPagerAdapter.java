package ro.sapientia.ms.sapiadvertiser.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import ro.sapientia.ms.sapiadvertiser.R;

public class ViewPagerAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<String> mImageURLs;

    public ViewPagerAdapter(Context mContext, ArrayList<String> mImageURLs) {
        this.mContext = mContext;
        this.mImageURLs = mImageURLs;
    }

    @Override
    public int getCount() {
        return mImageURLs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInflater.inflate(R.layout.viewpager_item,container,false);
        ImageView img = (ImageView)v.findViewById(R.id.viewPager_ImgView);
        Glide.with(v)
                .load(mImageURLs.get(position))
                .into(img);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }
}
