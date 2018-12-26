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

import ro.sapientia.ms.sapiadvertiser.Adapters.RecyclerViewAdapter_AllAdvs;
import ro.sapientia.ms.sapiadvertiser.Adapters.RecyclerViewAdapter_MyAdvs;
import ro.sapientia.ms.sapiadvertiser.NewsModel;
import ro.sapientia.ms.sapiadvertiser.R;

import static java.lang.Integer.parseInt;

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

        mRecyclerView = mInflatedView.findViewById(R.id.recyclerView);
        initNewsData();

        return this.mInflatedView;
    }

    private void initNewsData()
    {
        mNewsList.clear();
        mDatabase.child("sapiAdvertisments").orderByChild("UserId").equalTo(mAuth.getCurrentUser().getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                for(final DataSnapshot data : dataSnapshot.getChildren()) {
                    final NewsModel newsModel = new NewsModel();
                    newsModel.setmNewsId(data.getKey());
                    newsModel.setmTitle(data.child("Title").getValue().toString());
                    newsModel.setmCounter(parseInt(data.child("ViewCounter").getValue().toString()));
                    //tob img is lehet fel kell bontani elsore
                    newsModel.setmImage(data.child("Image").getValue().toString());
                    newsModel.setmDescription(data.child("ShortDesc").getValue().toString());
                    newsModel.setmUserId(mAuth.getCurrentUser().getPhoneNumber());
                    mDatabase.child("users").child(mAuth.getCurrentUser().getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            newsModel.setmProfileImage(dataSnapshot.child("ProfileImage").getValue().toString());
                            mNewsList.add(newsModel);
                            mAdapter = new RecyclerViewAdapter_MyAdvs(mNewsList,mInflatedView.getContext());
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(mInflatedView.getContext()));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
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
