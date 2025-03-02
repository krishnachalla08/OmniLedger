package com.example.omniledger;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    StorageReference storageReference;

    FusedLocationProviderClient fLPC;

    FirebaseDatabase db;
    DatabaseReference reference;
    Button capture,upload;
    ImageView imageView;

    EditText bank,item,amount;

    String[] required_permissions = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
    };

    boolean is_storage_image_permitted = false;
    boolean is_camera_access_permitted = false;

    String TAG = "permission";

    String address,Imageurl;
    String bnk,itm,amnt,dbcd;

    Uri uri_for_cam;
    RadioGroup radioGroup;
    RadioButton TransradioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        capture = findViewById(R.id.capture);
        imageView = findViewById(R.id.iview);


        if (!is_storage_image_permitted){
            requestPermissionStorageImages();
        }
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_camera_access_permitted){
                    openCamera();

                }else{
                    requestPermissionCameraAccess();
                }
            }
        });
        bank = findViewById(R.id.editTextBank);
        item = findViewById(R.id.editTextItem);
        amount = findViewById(R.id.editTextAmount);
        upload = findViewById(R.id.upload);
        radioGroup = findViewById(R.id.groupradio);




        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveImage(uri_for_cam);
                getCurrLocation();


            }
        });

    }

    public void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"OmniLedger");
        values.put(MediaStore.Images.Media.DESCRIPTION,"by OmniLedger");

        uri_for_cam = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri_for_cam);
        launcher_for_camera.launch(cameraIntent);
    }

    private ActivityResultLauncher<Intent> launcher_for_camera =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            if (result.getResultCode()==RESULT_OK){
                                imageView.setImageURI(uri_for_cam);
                            }
                        }
                    });

    private void saveImage(Uri uriForCam) {

        String email = getIntent().getStringExtra("Email");
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                .format(new Date());

        storageReference =FirebaseStorage.getInstance().getReference();
        StorageReference reference = storageReference.child(email).child(uriForCam.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        reference.putFile(uriForCam).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri Imgurl = uriTask.getResult();
                Imageurl = Imgurl.toString();
                saveData();
                dialog.dismiss();
                Toast.makeText(CameraActivity.this,Imageurl,Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(CameraActivity.this,"upload error",Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void saveData() {


        String email = getIntent().getStringExtra("Email");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());

        String em = email.replace("@gmail.com","");
        String file = "Trans_"+timeStamp;

        bnk = bank.getText().toString();
        itm = item.getText().toString();
        amnt = amount.getText().toString();
        int id = radioGroup.getCheckedRadioButtonId();
        TransradioButton = findViewById(id);
        dbcd = TransradioButton.getText().toString();


        if (!bnk.isEmpty() && !itm.isEmpty() && !amnt.isEmpty() && !dbcd.isEmpty()){
            Users users = new Users(bnk,itm,dbcd,timeStamp,address,amnt,Imageurl);
            db  =FirebaseDatabase.getInstance();
            reference = db.getReference("Transactions");
            reference.child(em).child(file).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    bank.setText("");
                    item.setText("");
                    amount.setText("");
                    radioGroup.clearCheck();
                    imageView.setImageResource(R.drawable.round_image_24);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CameraActivity.this,"Enter the Complete Details",Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            Toast.makeText(CameraActivity.this,"Enter the Complete Details",Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrLocation() {

        fLPC = LocationServices.getFusedLocationProviderClient(this);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            fLPC.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null){
                        Geocoder geocoder = new Geocoder(CameraActivity.this,Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            address = addresses.get(0).getAddressLine(0);
                            //Toast.makeText(CameraActivity.this,address,Toast.LENGTH_SHORT).show();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                         }
                }
            });
        }else{
                askPermissions();
        }


    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(CameraActivity.this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==100){
            if (grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrLocation();
            }else{
                Toast.makeText(this,"need access to location",Toast.LENGTH_SHORT).show();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void requestPermissionStorageImages(){
        if(ContextCompat.checkSelfPermission(CameraActivity.this,required_permissions[0])==PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,required_permissions[0]+"Granted");
            is_storage_image_permitted=true;
            requestPermissionCameraAccess();
        }else{
            request_permission_launcher_storage_images.launch(required_permissions[0]);
        }
    }

    private ActivityResultLauncher<String> request_permission_launcher_storage_images = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            isGranted->{
                if (isGranted){
                    Log.d(TAG,required_permissions[0]+"Granted");
                    is_storage_image_permitted = true;
                }else{
                    Log.d(TAG,required_permissions[0]+"Not Granted");
                    is_storage_image_permitted = false;
                }
                requestPermissionCameraAccess();
            });



    private void requestPermissionCameraAccess() {
        if(ContextCompat.checkSelfPermission(CameraActivity.this,required_permissions[1])==PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,required_permissions[1]+"Granted");
            is_camera_access_permitted=true;

        }else{
            request_permission_launcher_camera_access.launch(required_permissions[1]);
        }
    }

    private ActivityResultLauncher<String> request_permission_launcher_camera_access = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            isGranted->{
                if (isGranted){
                    Log.d(TAG,required_permissions[1]+"Granted");
                    is_camera_access_permitted = true;
                }else{
                    Log.d(TAG,required_permissions[1]+"Not Granted");
                    is_camera_access_permitted = false;
                }

            });


}


/*String getFileName(Uri uri, Context context){
        String res = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
            try{
                if (cursor != null && cursor.moveToFirst()){
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }

            }finally {
                cursor.close();
            }
            if (res == null){
                res = uri.getPath();
                int cutt = res.lastIndexOf('/');
                if (cutt != -1){
                    res = res.substring(cutt + 1);
                }
            }
        }
        return res;
    }*/