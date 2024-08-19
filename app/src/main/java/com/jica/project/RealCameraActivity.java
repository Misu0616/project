package com.jica.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
/*import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;*/
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class RealCameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    FirebaseAuth firebaseAuth;
    File file;

    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private boolean isCameraInitialized = false;
    ImageView imageView;

    private final ActivityResultLauncher<Intent> cameraResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (file != null && file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                            imageView.setVisibility(View.VISIBLE);
                            uploadImageToFirebase(bitmap);
                        } else {
                            Log.e("ImageError", "Bitmap is null");
                        }
                    } else {
                        Log.e("FileError", "File does not exist");
                    }
                } else {
                    Log.e("CameraResult", "Result not OK");
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_camera);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        PreviewView previewView = findViewById(R.id.previewView);
        imageView = findViewById(R.id.imageView);
        Button btnCamera = findViewById(R.id.btnCamera);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // 권한 요청
        int permissionCheck = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "카메라 권한 수락됨", Toast.LENGTH_LONG).show();
            startCamera(previewView);
        } else {
            Toast.makeText(this, "카메라 권한을 허용해주세요", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.CAMERA")) {
                Toast.makeText(this, "카메라 권한을 허용해주세요", Toast.LENGTH_LONG).show();
            } else {
                // 사용자 권한 사용에 대한 대화 상자를 띄워서 확인
                ActivityCompat.requestPermissions(this, new String[] {"android.permission.CAMERA"}, 1);
            }
        }

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCameraInitialized) {
                    takePicture();
                } else {
                    Toast.makeText(getApplicationContext(), "카메라 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startCamera(PreviewView previewView) {
        if (isCameraInitialized) return; // 이미 초기화되면 실행 x
        previewView.setVisibility(View.VISIBLE);

        // CameraProvider 인스턴스를 가져옴
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview와 ImageCapture 설정
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();

                // 카메라 선택 (후면 카메라 선택)
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // 카메라와 Preview, ImageCapture를 Lifecycle에 바인딩
                cameraProvider.unbindAll(); // 이전 바인딩 해제
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                // PreviewView에 SurfaceProvider 설정
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                isCameraInitialized = true;

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "카메라 초기화 실패", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePicture() {
        file = createFile();
        Uri uri;

        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, "com.jica.project.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        cameraResultLauncher.launch(intent);
    }

    private File createFile(){

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "photo_" + timeStamp + ".jpg";
        return new File(getExternalFilesDir(null), fileName);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == RESULT_OK){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            imageView.setImageBitmap(bitmap);
            uploadImageToFirebase(bitmap);
        }
    }*/

    private void uploadImageToFirebase(Bitmap bitmap) {
        // Bitmap을 바이트 배열로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // 환경 보호 종류 position으로 나누기
        int position = getIntent().getIntExtra(ActivityAdapter.ViewHolder.POSITION_KEY, -1); // 기본값 -1
        if (position != -1) {
            Toast.makeText(this, "받은 포지션: " + position, Toast.LENGTH_SHORT).show();
            Log.d("noAnswer : ", "받은 포지션: " + position);
        }

        // 사진 이름 현재 날짜로 구별하기
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "photo_" + timeStamp + ".jpg"; // 예: photo_20230818_123456.jpg

        // UID로 user 구분하기
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // UID 가져오기

            // Firebase Storage에 업로드
            StorageReference imagesRef = storageRef.child(userId + "/" + position + "/" + fileName); // 경로 설정
            imagesRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(RealCameraActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MyGalleryActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(RealCameraActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                    });
        }

    }

}