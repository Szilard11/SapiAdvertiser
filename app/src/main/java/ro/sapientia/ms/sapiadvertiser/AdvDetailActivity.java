package ro.sapientia.ms.sapiadvertiser;

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

public class AdvDetailActivity extends AppCompatActivity {
    private ViewPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private ArrayList<String> mImageURLs = new ArrayList<>();
    private Button mShareButton;
    private Button mReportButton;
    private TextView mTitle;
    private TextView mLongDesc;
    private TextView mPhone;
    private TextView mEmail;
    private TextView mLocation;
    private TextView mUserFname;
    private CircleImageView mProfileImage;
    private DatabaseReference database;
    private String mUserId;
    private String mNewsId;

    public void setViews(String pTitle,String pLongdesc,String pPhone,String pEmail,String pLocation,String pUserFname, String pProfileImage ){
        this.mTitle.setText(pTitle);
        this.mLongDesc.setText(pLongdesc);
        this.mPhone.setText(pPhone);
        this.mEmail.setText(pEmail);
        this.mLocation.setText(pLocation);
        this.mUserFname.setText(pUserFname);
        Glide.with(this)
                .load(pProfileImage)
                .into(mProfileImage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_detail);
        mImageURLs.add("https://www.gettyimages.ie/gi-resources/images/Homepage/Hero/UK/CMS_Creative_164657191_Kingfisher.jpg");
        mImageURLs.add("https://wallpaperbrowse.com/media/images/3848765-wallpaper-images-download.jpg");
        mViewPager = findViewById(R.id.view_pager);
        mShareButton = findViewById(R.id.adv_det_share_butt);
        mReportButton= findViewById(R.id.adv_det_report_butt);
        mTitle = findViewById(R.id.adv_det_title);
        mLongDesc = findViewById(R.id.adv_det_longdesc);
        mPhone = findViewById(R.id.adv_det_phone);
        mEmail = findViewById(R.id.adv_det_email);
        mUserFname = findViewById(R.id.adv_det_name);
        mLocation = findViewById(R.id.adv_det_location);
        mProfileImage = findViewById(R.id.circleImageView2);


        getIncomingExtras();
        final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
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

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        database = FirebaseDatabase.getInstance().getReference("users");

        database.child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                String email = dataSnapshot.child("Email").getValue().toString();
                setViews("Alma","Ez egy almarol szol","+40123456789",email,"On da tree","Almacska","https://www.apple.com/ac/structured-data/images/knowledge_graph_logo.png?201606271147");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mAdapter = new ViewPagerAdapter(this,mImageURLs);
        mViewPager.setAdapter(mAdapter);
    }

    private void getIncomingExtras()
    {
        if(getIntent().hasExtra("news_id") &&
                getIntent().hasExtra("user_id"))
        {
            mUserId = getIntent().getStringExtra("user_id");
            mNewsId = getIntent().getStringExtra("news_id");

            //https://www.youtube.com/watch?v=ZXoGG2XTjzU&t=449s itt volt a tutorial
        }
    }
}
