package ro.sapientia.ms.sapiadvertiser.Fragments;

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

import ro.sapientia.ms.sapiadvertiser.Adapters.RecyclerViewAdapter_MyAdvs;
import ro.sapientia.ms.sapiadvertiser.NewsModel;
import ro.sapientia.ms.sapiadvertiser.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAdvsFragment extends Fragment {

    private ArrayList<NewsModel> mNewsList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter_MyAdvs mAdapter;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private View mInflatedView;

    public MyAdvsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mInflatedView = inflater.inflate(R.layout.fragment_my_advs, container, false);

        initNewsData();

        mRecyclerView = mInflatedView.findViewById(R.id.recyclerView);
        mAdapter = new RecyclerViewAdapter_MyAdvs(mNewsList,mInflatedView.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mInflatedView.getContext()));

        return this.mInflatedView;
    }

    private void initNewsData()
    {
        String newsId, title, shortDesc, image, userId;
        Integer counter;
        NewsModel newsModel = new NewsModel();
        //TODO: lekerni az osszes news-t
        mDatabase.child("sapiAdvertisments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                //newsId = setmNewsId(dataSnapshot.child("newsId").getValue().toString());
                //setmDescription(dataSnapshot.child(newsId).child("ShortDesc").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*NewsModel news = new NewsModel("title","short description",10,"url",
                "url2", "userid","newsid");
        mNewsList.add(news);*/
    }
}
