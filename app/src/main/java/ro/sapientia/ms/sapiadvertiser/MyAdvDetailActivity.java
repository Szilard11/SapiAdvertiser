package ro.sapientia.ms.sapiadvertiser;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ro.sapientia.ms.sapiadvertiser.Adapters.ViewPagerAdapter;

public class MyAdvDetailActivity extends AppCompatActivity {
    private ViewPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private ArrayList<String> mImageURLs = new ArrayList<>();
    private Button mShareButton;
    private Button mDeleteButton;
    private Button mEditButton;
    private TextView mTitle;
    private TextView mLongDesc;
    private TextView mPhone;
    private TextView mEmail;
    private TextView mLocation;
    private DatabaseReference mdatabase;
    private String mNewsId;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mUserId = mAuth.getCurrentUser().getPhoneNumber();
    private Context mContext = this;

    public void setViews(String pTitle,String pLongdesc,String pPhone,String pEmail,String pLocation){
        this.mTitle.setText(pTitle);
        this.mLongDesc.setText(pLongdesc);
        this.mPhone.setText(pPhone);
        this.mEmail.setText(pEmail);
        this.mLocation.setText(pLocation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_adv_detail);
        //mImageURLs.add("https://www.gettyimages.ie/gi-resources/images/Homepage/Hero/UK/CMS_Creative_164657191_Kingfisher.jpg");
        //mImageURLs.add("https://wallpaperbrowse.com/media/images/3848765-wallpaper-images-download.jpg");
        mViewPager = findViewById(R.id.view_pager2);
        mShareButton = findViewById(R.id.my_adv_det_share_butt);
        mDeleteButton= findViewById(R.id.my_adv_det_delete_butt);
        mEditButton=findViewById(R.id.my_adv_det_edit_butt);
        mTitle = findViewById(R.id.my_adv_det_title);
        mLongDesc = findViewById(R.id.my_adv_det_longdesc);
        mPhone = findViewById(R.id.my_adv_det_phone);
        mEmail = findViewById(R.id.my_adv_det_email);
        mLocation = findViewById(R.id.my_adv_det_location);

        getIncomingExtras();
        final Intent sharingIntent=new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Here is the share content body";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mdatabase = FirebaseDatabase.getInstance().getReference();

        mdatabase.child("users").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                final String email = dataSnapshot.child("Email").getValue().toString();
                final String loc = dataSnapshot.child("Address").getValue().toString();
                mdatabase.child("sapiAdvertisments").child(mNewsId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mImageURLs.add(dataSnapshot.child("Image").getValue().toString());
                        String longDesc = dataSnapshot.child("LongDesc").getValue().toString();
                        String title = dataSnapshot.child("Title").getValue().toString();
                        setViews(title,longDesc,mUserId,email,loc);
                        mAdapter = new ViewPagerAdapter(mContext,mImageURLs);
                        mViewPager.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getIncomingExtras()
    {
        if(getIntent().hasExtra("news_id"))
        {
            mNewsId = getIntent().getStringExtra("news_id");
        }
    }
}
