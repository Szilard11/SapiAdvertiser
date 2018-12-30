package ro.sapientia.ms.sapiadvertiser.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ro.sapientia.ms.sapiadvertiser.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {
    private EditText mTitle;
    private EditText mShortDesc;
    private EditText mLongDesc;
    private EditText mPhoneNum;
    private ImageButton mAddImage;
    private View mInflatedView;
    private Button mUpload;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();;
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final int RESULT_LOAD_IMAGE = 1;
    private List<Uri> filePath = new ArrayList<>();

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.mInflatedView = inflater.inflate(R.layout.fragment_add, container, false);
        mTitle = mInflatedView.findViewById(R.id.editText_Title);
        mShortDesc = mInflatedView.findViewById(R.id.editText_shortDesc);
        mLongDesc = mInflatedView.findViewById(R.id.editText_longDesc);
        mPhoneNum = mInflatedView.findViewById(R.id.editText_phone);
        mAddImage = mInflatedView.findViewById(R.id.imageButton_add);
        mUpload = mInflatedView.findViewById(R.id.button_Upload);

        mPhoneNum.setEnabled(false);
        mPhoneNum.setText(mAuth.getCurrentUser().getPhoneNumber());

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                filePath.clear();
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE);
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAdv();
            }
        });
        return this.mInflatedView;
    }

    private void uploadAdv() {
        String key = String.valueOf(System.currentTimeMillis());
        String longDesc = mLongDesc.getText().toString();
        String shortDesc = mShortDesc.getText().toString();
        String title = mTitle.getText().toString();

        if(shortDesc != "" && title != "" && filePath.size()>0) {
            mDatabaseRef.child("sapiAdvertisments").child(key).child("LongDesc").setValue(longDesc);
            mDatabaseRef.child("sapiAdvertisments").child(key).child("ShortDesc").setValue(shortDesc);
            mDatabaseRef.child("sapiAdvertisments").child(key).child("Title").setValue(title);
            mDatabaseRef.child("sapiAdvertisments").child(key).child("UserId").setValue(mAuth.getCurrentUser().getPhoneNumber());
            mDatabaseRef.child("sapiAdvertisments").child(key).child("ViewCounter").setValue(0);
            mDatabaseRef.child("sapiAdvertisments").child(key).child("WarningCount").setValue(0);
            mDatabaseRef.child("sapiAdvertisments").child(key).child("isDeleted").setValue(0);
            uploadImages(key);
        }
        else {
            Toast.makeText(getActivity(), "You must fill out all the fields!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){

            if(data.getClipData() != null){

                int totalItemsSelected = data.getClipData().getItemCount();
                for(int i = 0; i < totalItemsSelected; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    filePath.add(fileUri);

                }
            }
            else if (data.getData() != null){
                Uri fileUri = data.getData();
                filePath.add(fileUri);
            }
            //TODO: ide recview
        }
    }

    private Integer num = 0;
    private void uploadImages(final String childName){
        for(int i=0;i<filePath.size();i++) {
            final StorageReference fileToUpload = mStorageRef.child("Images").child(UUID.randomUUID().toString());
            fileToUpload.putFile(filePath.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mDatabaseRef.child("sapiAdvertisments").child(childName).child("Image").child(num.toString()).setValue(uri.toString());
                            num++;
                            /*if(num == filePath.size()-1)
                            {
                                filePath.clear();
                            }*/
                        }
                    });
                }
            });
        }
        Toast.makeText(getActivity(), "Advertisment uploaded", Toast.LENGTH_SHORT).show();
        mShortDesc.setText("");
        mLongDesc.setText("");
        mTitle.setText("");
    }
}