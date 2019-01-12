package ro.sapientia.ms.sapiadvertiser.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import ro.sapientia.ms.sapiadvertiser.Adapters.RecyclerViewAdapter_AllAdvs;
import ro.sapientia.ms.sapiadvertiser.NewsModel;
import ro.sapientia.ms.sapiadvertiser.R;

import static java.lang.Integer.parseInt;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ArrayList<NewsModel> mNewsList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter_AllAdvs mAdapter;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
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
        //initNewsData();

        mNewsList.clear();
        mDatabase.child("sapiAdvertisments").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    mNewsList.clear();
                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        if (parseInt(data.child("WarningCount").getValue().toString()) < 10
                                && parseInt(data.child("isDeleted").getValue().toString()) != 1) {
                            final NewsModel newsModel = new NewsModel();
                            newsModel.setmNewsId(data.getKey());
                            newsModel.setmTitle(data.child("Title").getValue().toString());
                            newsModel.setmCounter(parseInt(data.child("ViewCounter").getValue().toString()));
                            newsModel.setmImage(data.child("Image").child("0").getValue().toString());
                            newsModel.setmDescription(data.child("ShortDesc").getValue().toString());
                            newsModel.setmUserId(data.child("UserId").getValue().toString());
                            mDatabase.child("users").child(data.child("UserId").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    newsModel.setmProfileImage(dataSnapshot.child("ProfileImage").getValue().toString());
                                    mNewsList.add(newsModel);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAdapter = new RecyclerViewAdapter_AllAdvs(mNewsList, mInflatedView.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mInflatedView.getContext()));
        return this.mInflatedView;
    }
}
