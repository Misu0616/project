package com.jica.project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jica.project.ActivityAdapter.ViewHolder;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    FirebaseAuth firebaseAuth;

    ImageView imageView;
    Button takePics, savePics;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView = findViewById(R.id.tookPicture);
        takePics = findViewById(R.id.takeAPicture);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // 사진 촬영 버튼
        takePics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        // 사진 저장 버튼
       /* savePics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });*/
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == RESULT_OK){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            imageView.setImageBitmap(bitmap);
            uploadImageToFirebase(bitmap);
        }
    }

    private void uploadImageToFirebase(Bitmap bitmap) {
        // Bitmap을 바이트 배열로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // 환경 보호 종류 position으로 나누기
        int position = getIntent().getIntExtra(ViewHolder.POSITION_KEY, -1); // 기본값 -1
        if (position != -1) {
            Toast.makeText(this, "받은 포지션: " + position, Toast.LENGTH_SHORT).show();
        }

        // 사진 이름 현재 날짜로 구별하기
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "photo_" + timeStamp + ".jpg"; // 예: photo_20230818_123456.jpg

       // Toast.makeText(CameraActivity.this, "userID~~ : " + userId, Toast.LENGTH_SHORT).show();

        // UID로 user 구분하기
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // UID 가져오기

            // Firebase Storage에 업로드
            StorageReference imagesRef = storageRef.child(userId + "/" + position + "/" + fileName); // 경로 설정
            imagesRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(CameraActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MyGalleryActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(CameraActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                    });
        }

    }

    public void takePicture(){
        try{
            file = createFile();
            if(file.exists()){
                file.delete();
            }

            file.createNewFile();
        }   catch (Exception e){
            e.printStackTrace();
        }
        Uri uri;
        if(Build.VERSION.SDK_INT >= 24){
            uri = FileProvider.getUriForFile(this, "com.jica.project.fileprovider", file);
                                                            // authority에 원래 BuildConfig.APPLICATION_ID가 들어가야 함
        } else {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 101);
    }

    private File createFile(){
        String filename = "capture.jpg";
        File outFile = new File(getFilesDir(), filename);
        Log.d("cameraFileSave", "file path : " + outFile.getAbsolutePath());

        return outFile;
    }

/*
    //권한 확인
    public void checkPermission() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //권한이 없으면 권한 요청
        if (permissionCamera != PackageManager.PERMISSION_GRANTED
                || permissionRead != PackageManager.PERMISSION_GRANTED
                || permissionWrite != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "이 앱을 실행하기 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // 권한이 취소되면 result 배열은 비어있다.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "권한 확인", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();
                    finish(); //권한이 없으면 앱 종료
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission(); //권한체크
    }*/

}