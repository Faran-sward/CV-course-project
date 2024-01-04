package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private LinearLayout tv_photo;
    private ImageView iv_bg;
    private Button btn_add;
    private Button btn_check;
    private static final int RC_CAMERA_PERM = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_photo = findViewById(R.id.tv_photo);
        iv_bg = findViewById(R.id.iv_bg);
        btn_add = findViewById(R.id.btn_add);
        btn_check = findViewById(R.id.btn_check);
        tv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Have permission, do the thing

                    testTakePhoto();
                } else {//没有相应权限，获取相机权限
                    // Ask for one permission
                    EasyPermissions.requestPermissions(MainActivity.this, "获取照相机权限",
                            RC_CAMERA_PERM, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkGo.<String>post("http://47.117.145.92:8021/upload")
                        .tag(this)
                        .isMultipart(true)
                        .params("id", 1)
                        .params("image", mPhotoFile)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                Log.e("上传照片成功!",response.body());
                                showSuccessDialog();
                            }

                            @Override
                            public void onError(Response<String> response) {

                            }
                        });
            }
            private void showSuccessDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("");
                builder.setMessage("upload successfully!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户点击确定后的操作
                        dialog.dismiss(); // 关闭对话框
                    }
                });
                builder.create().show();
            }

        });
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkGo.<String>post("http://47.117.145.92:8021/check")
                        .tag(this)
                        .params("id", 1)
                        .params("image", mPhotoFile)
                        .isMultipart(true)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                Log.e("校验照片成功!",response.body());
                                showSuccessDialog(response.body());
                            }

                            @Override
                            public void onError(Response<String> response) {

                            }
                        });
            }

            private void showSuccessDialog(String message) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("");
                builder.setMessage(message);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户点击确定后的操作
                        dialog.dismiss(); // 关闭对话框
                    }
                });
                builder.create().show();
            }
        });
    }


    private Uri fileUri1;
    File mPhotoFile;
    //打开系统相机
    private void testTakePhoto() {
        //指定相机拍照的存储路径
        mPhotoFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        try {
            //判断文件是否存在，存在删除，不存在创建
            if (mPhotoFile.exists()){
                mPhotoFile.delete();
            }
            mPhotoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //指定intent跳转到系统相机
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(Build.VERSION.SDK_INT>=24){
            fileUri1= FileProvider.getUriForFile(MainActivity.this,"com.example.myapplication.provider",mPhotoFile);
            Log.e("获取到的url为",fileUri1.toString());
        }else {
            fileUri1 = Uri.fromFile(mPhotoFile);
        }


        //设置存储路径
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri1);
        try {
            //开启相机
            startActivityForResult(captureIntent, 5);
        } catch (Exception e) {
            Log.e("相机异常",e.getMessage());
            Toast.makeText(this, "跳转系统相机异常", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {//拍照返回


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String extension = mPhotoFile.getAbsolutePath();
                        extension = extension.substring(extension.lastIndexOf("."));
                        extension = extension.replace(".","");
                        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                        iv_bg.setImageBitmap(bitmap);
                    }
                }).run();
            }
        }
    }
}
