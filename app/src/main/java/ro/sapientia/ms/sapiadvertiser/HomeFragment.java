package ro.sapientia.ms.sapiadvertiser;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ro.sapientia.ms.sapiadvertiser.Adapters.RecyclerViewAdapter_AllAdvs;


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
    private RecyclerViewAdapter_AllAdvs mAdapter;

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
        this.mInflatedView = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = mInflatedView.findViewById(R.id.recyclerView);
        initNewsData();

        mAdapter = new RecyclerViewAdapter_AllAdvs(mNewsList,mInflatedView.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mInflatedView.getContext()));

        return this.mInflatedView;
    }
    public void initNewsData()
    {
        String newsId, title, shortDesc, image, userId;
        Integer counter;
        final NewsModel newsModel = new NewsModel();
        //TODO: lekerni az osszes news-t
        mDatabase.child("sapiAdvertisments").child("201812201420").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                newsModel.setmTitle(dataSnapshot.child("Title").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mNewsList.add(newsModel);
        /*NewsModel news = new NewsModel("title","short description",10,"url",
                "url2", "userid","newsid");
        mNewsList.add(news);*/
    }
}
