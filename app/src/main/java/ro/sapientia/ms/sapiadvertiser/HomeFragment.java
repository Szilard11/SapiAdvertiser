package ro.sapientia.ms.sapiadvertiser;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    /*private NewsModel mNewsModel;
    private ImageView mImage;
    private TextView mCounterView;
    private TextView mTitle;
    private TextView mShortDescription;
    private CircleImageView mProfileImage;*/

    private ArrayList<NewsModel> mNewsList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private View mInflatedView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mInflatedView = inflater.inflate(R.layout.fragment_person, container, false);

        initNewsData();

        mRecyclerView = mInflatedView.findViewById(R.id.recyclerView);
        mAdapter = new RecyclerViewAdapter(mNewsList,mInflatedView.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mInflatedView.getContext()));

        return this.mInflatedView;
    }
    private void initNewsData()
    {
        //TODO: lekerni az osszes news-t
        NewsModel news = new NewsModel("title","short description",10,"url",
                "url2", "userid","newsid");
        mNewsList.add(news);
    }
}
