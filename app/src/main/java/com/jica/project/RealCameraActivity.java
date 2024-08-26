package com.jica.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
/*import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;*/
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class RealCameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    FirebaseAuth firebaseAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    File file;
    private Uri imageUri;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private boolean isCameraInitialized = false;
    ImageView imageView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String userId = FirebaseAuth.getInstance().getUid();

    public static final String FILENUM_KEY = "FILENUM_KEY";

    private final ActivityResultLauncher<Intent> cameraResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (file != null && file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        if (bitmap != null) {
                            // ExifInterface를 사용하여 이미지 회전
                            int orientation = getExifOrientation(file.getAbsolutePath());
                            Bitmap rotatedBitmap = rotateBitmap(bitmap, orientation);
                            imageView.setImageBitmap(rotatedBitmap);
                            uploadImageToFirebase(rotatedBitmap);

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

        imageView = findViewById(R.id.imageView4);
        Button btnCamera = findViewById(R.id.btnCamera);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // 권한 요청
        int permissionCheck = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "카메라 권한 수락됨", Toast.LENGTH_LONG).show();
            startCamera();
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

    private void startCamera() {
        if (isCameraInitialized) return; // 이미 초기화되면 실행 x

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

                isCameraInitialized = true;

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "카메라 초기화 실패", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePicture() {
        file = createFile();
        imageUri = FileProvider.getUriForFile(this, "com.jica.project.fileprovider", file);
        Uri uri;

        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, "com.jica.project.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        if (uri != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            cameraResultLauncher.launch(intent);
        } else {
            Toast.makeText(this, "사진을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    private File createFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "photo_" + timeStamp + ".jpg";
        return new File(getExternalFilesDir(null), fileName);
    }

    private void uploadImageToFirebase(Bitmap bitmap) {
        // Bitmap을 바이트 배열로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // 환경 보호 종류 position으로 나누기
        int position = getIntent().getIntExtra(ActivityAdapter.ViewHolder.POSITION_KEY, -1); // 기본값 -1

        // 사진 이름 현재 날짜로 구별하기
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "photo_" + timeStamp + ".jpg";
        // UID로 user 구분하기
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // UID 가져오기

            // Firebase Storage에 업로드
            StorageReference imagesRef = storageRef.child(userId).child(fileName); // 경로 설정
            imagesRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(RealCameraActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                        Intent list = new Intent(this, MyGalleryActivity.class);
                        list.putExtra(ActivityAdapter.ViewHolder.POSITION_KEY, position); // position 값 전달
                        list.putExtra(RealCameraActivity.FILENUM_KEY, fileName);
                        startActivity(list);
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(RealCameraActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                    });
            StorageReference imageRef = storageRef.child(userId).child(fileName);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            saveImageInfoToFirestore(String.valueOf(position), timeStamp, Boolean.valueOf("false"), fileName, downloadUrl, firebaseAuth.getCurrentUser().getUid());
                        }).addOnFailureListener(exception -> {
                            Log.e("FirebaseStorage", "Error getting download URL", exception);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Log.e("FirebaseStorage", "Upload failed", exception);
                    });
        }
    }

    private void saveImageInfoToFirestore(String title, String date, Boolean admin_check, String fileName, String downloadUrl, String userId) {

        CollectionReference imagesRef = firestore.collection(userId);

        Map<String, Object> imageInfo = new HashMap<>();
        imageInfo.put("title", title);
        imageInfo.put("date", date);
        imageInfo.put("admin_check", admin_check);
        imageInfo.put("fileName", fileName);
        imageInfo.put("downloadUrl", downloadUrl);
        imageInfo.put("userId", firebaseAuth.getCurrentUser().getUid());

        imagesRef.add(imageInfo)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    String newDocumentId = documentReference.getId();
                    saveDocumentIdToCollection(newDocumentId);
                })
                .addOnFailureListener(exception -> {
                    Log.w("Firestore", "Error adding document", exception);
                });
    }
    private void saveDocumentIdToCollection(String documentId) {
        // documentIds 컬렉션에 문서 ID 저장
        Map<String, Object> idData = new HashMap<>();
        idData.put("documentId", documentId);

        firestore.collection("documentId").add(idData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FirestoreHelper", "Document ID saved to documentIds collection: " + documentId);
                })
                .addOnFailureListener(e -> {
                    Log.w("FirestoreHelper", "Error saving document ID", e);
                });
    }

    private int getExifOrientation(String path) {
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;
        try {
            ExifInterface exif = new ExifInterface(path);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orientation;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}