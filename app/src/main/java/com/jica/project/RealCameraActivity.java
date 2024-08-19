package com.jica.project;

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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
/*import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;*/
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class RealCameraActivity extends AppCompatActivity {
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private boolean isCameraInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_camera);

       // FrameLayout previewFrame = findViewById(R.id.previewFrame);
        PreviewView previewView = findViewById(R.id.previewView);
        Button btnCamera = findViewById(R.id.btnCamera);


        cameraExecutor = Executors.newSingleThreadExecutor();

/*
        AndPermission.with(this)
                .runtime()
                .permission(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(getApplicationContext(), "허용된 권한 개수 : " + permissions.size(), Toast.LENGTH_SHORT).show();
                        startCamera(previewView);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(getApplicationContext(), "거부된 권한 개수 : " + permissions.size(), Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
*/
        // 권한 요청
        int permissionCheck = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "카메라 수신 권한 있음.", Toast.LENGTH_LONG).show();
            startCamera(previewView);
        } else {
            Toast.makeText(this, "카메라 수신 권한 없음.", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.CAMERA")) {
                Toast.makeText(this, "카메라 권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                //사용자에 권한 사용에 대한 대화상자를 띄워서 확인하도록 한다.
                ActivityCompat.requestPermissions(this, new String[] {"android.permission.CAMERA"}, 1);
            }
        }

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCameraInitialized) {
                    takePicture();
                } else {
                    Toast.makeText(getApplicationContext(), "카메라가 초기화되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startCamera(PreviewView previewView) {
        Toast.makeText(getApplicationContext(), " 카메라 되나요...?", Toast.LENGTH_SHORT).show();
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
        if (imageCapture != null) {
            File photoFile = new File(getExternalFilesDir(null), "photo.jpg");
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    Uri savedUri = Uri.fromFile(photoFile);
                    Log.d("CameraX", "Image saved successfully: " + savedUri);

                    // MediaScannerConnection을 사용하여 미디어 스캐너 호출
                    MediaScannerConnection.scanFile(
                            RealCameraActivity.this,
                            new String[]{photoFile.getAbsolutePath()},
                            null,
                            (path, uri) -> Log.d("CameraX", "MediaScanner connection scanned file: " + uri)
                    );
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Log.e("CameraX", "Photo capture failed: " + exception.getMessage());
                }
            });

            // 인증 내역으로 이동하기
            Intent intent = new Intent(getApplicationContext(), MyGalleryActivity.class);
            startActivity(intent);
        }

    }
}