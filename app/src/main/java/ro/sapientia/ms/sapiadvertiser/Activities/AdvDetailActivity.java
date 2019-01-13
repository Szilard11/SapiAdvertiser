package ro.sapientia.ms.sapiadvertiser.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import ro.sapientia.ms.sapiadvertiser.Adapters.ViewPagerAdapter;
import ro.sapientia.ms.sapiadvertiser.R;

import static java.lang.Integer.parseInt;

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
    private DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
    private String mUserId;
    private String mNewsId;
    private Context mContext = this;

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


        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = mTitle.getText().toString() + "\n\n" + mLongDesc.getText().toString() + "\n\n" + "Images:" + "\n";
                for(int i = 0;i < mImageURLs.size();i++){
                    shareBody += mImageURLs.get(i) + "\n";
                }
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sapi Advertisment");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportDialog();
            }
        });

        mdatabase.child("users").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                final String email = dataSnapshot.child("Email").getValue().toString();
                final String loc = dataSnapshot.child("Address").getValue().toString();
                final String lName = dataSnapshot.child("LastName").getValue().toString();
                final String profileImg = dataSnapshot.child("ProfileImage").getValue().toString();
                mdatabase.child("sapiAdvertisments").child(mNewsId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.child("Image").getChildren())
                        {
                            mImageURLs.add(data.getValue().toString());
                        }
                        String longDesc = dataSnapshot.child("LongDesc").getValue().toString();
                        String title = dataSnapshot.child("Title").getValue().toString();
                        setViews(title,longDesc,mUserId,email,loc,lName,profileImg);
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
        if(getIntent().hasExtra("news_id") &&
                getIntent().hasExtra("user_id"))
        {
            mUserId = getIntent().getStringExtra("user_id");
            mNewsId = getIntent().getStringExtra("news_id");
        }
    }

    public void ReportDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sapi Advertiser");
        builder.setMessage("Are you sure you want report this advertisment?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                mdatabase.child("sapiAdvertisments").child(mNewsId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer value = parseInt(dataSnapshot.child("WarningCount").getValue().toString());
                        mdatabase.child("sapiAdvertisments").child(mNewsId).child("WarningCount").setValue(value+1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
