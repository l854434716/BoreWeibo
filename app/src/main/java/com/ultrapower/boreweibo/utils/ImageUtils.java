package com.ultrapower.boreweibo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.ultrapower.boreweibo.BaseActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2015/9/26.
 */
public class ImageUtils extends com.sina.weibo.sdk.utils.ImageUtils {

    public static final int GET_IMAGE_BY_CAMERA = 5001;
    public static final int GET_IMAGE_FROM_PHONE = 5002;
    public static final int CROP_IMAGE = 5003;
    public static Uri imageUriFromCamera;
    public static Uri cropImageUri;


    public static void openCameraImage(BaseActivity activity) {

        imageUriFromCamera= ImageUtils.createImagePathUri(activity);

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // MediaStore.EXTRA_OUTPUT参数不设置时,系统会自动生成一个uri,但是只会返回一个缩略图
        // 返回图片在onActivityResult中通过以下代码获取
        // Bitmap bitmap = (Bitmap) data.getExtras().get("data");

        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUriFromCamera);

        activity.startActivityForResult(intent,GET_IMAGE_BY_CAMERA);



    }

    /*判断是否存在sd 卡 然后通过ContentResolver insert 生成换一个uri*/
    private static Uri createImagePathUri(Context context) {
        Uri imageFilePath = null;
        String status = Environment.getExternalStorageState();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        long time = System.currentTimeMillis();
        String imageName = timeFormatter.format(new Date(time));

        ContentValues  contentValues= new ContentValues(3);

        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,imageName);
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, time);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (status.equals(Environment.MEDIA_MOUNTED)){
            //判断是否有sd卡,有的话就将数据存放到Sd卡中
            imageFilePath=context.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        }else{
            imageFilePath=context.getContentResolver()
                    .insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI,contentValues);

        }
        Log.i("", "生成的照片输出路径：" + imageFilePath.toString());
        return  imageFilePath;

    }


    /**
     * 删除一条图片
     */
    public static void deleteImageUri(Context context, Uri uri) {
        context.getContentResolver().delete(uri, null, null);
    }

    public static void openLocalImage(BaseActivity activity) {
        Intent intent = new Intent();

        // 获取特定类型的数据，所以需继续设置type 表示要获取什么类型数据
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        //第二种获取本地图片的方法
        /*Intent intent1= new Intent();

        intent1.setAction(Intent.ACTION_PICK);// 会返回多种类型的数据，所以选取要指定目标路
        intent1.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);// 设置媒体储蓄的外部图片存储目录*/


        activity.startActivityForResult(intent,GET_IMAGE_FROM_PHONE);

    }

    public static void deleteImageUri(Activity activity, Uri imageUri) {

    }

    public static void cropImage(Activity activity, Uri imageUri) {

        ImageUtils.cropImageUri = ImageUtils.createImagePathUri(activity);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");

        ////////////////////////////////////////////////////////////////
        // 1.宽高和比例都不设置时,裁剪框可以自行调整(比例和大小都可以随意调整)
        ////////////////////////////////////////////////////////////////
        // 2.只设置裁剪框宽高比(aspect)后,裁剪框比例固定不可调整,只能调整大小
        ////////////////////////////////////////////////////////////////
        // 3.裁剪后生成图片宽高(output)的设置和裁剪框无关,只决定最终生成图片大小
        ////////////////////////////////////////////////////////////////
        // 4.裁剪框宽高比例(aspect)可以和裁剪后生成图片比例(output)不同,此时,
        //	会以裁剪框的宽为准,按照裁剪宽高比例生成一个图片,该图和框选部分可能不同,
        //  不同的情况可能是截取框选的一部分,也可能超出框选部分,向下延伸补足
        ////////////////////////////////////////////////////////////////

        // aspectX aspectY 是裁剪框宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪后生成图片的宽高
//		intent.putExtra("outputX", 300);
//		intent.putExtra("outputY", 100);

        // return-data为true时,会直接返回bitmap数据,但是大图裁剪时会出现问题,推荐下面为false时的方式
        // return-data为false时,不会返回bitmap,但需要指定一个MediaStore.EXTRA_OUTPUT保存图片uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.cropImageUri);
        intent.putExtra("return-data", false);

        activity.startActivityForResult(intent, CROP_IMAGE);

    }



    /**
     * 将图片保存到SD中
     */
    public static void saveFile(Context context, Bitmap bm, String fileName) throws IOException {
        // 未安装SD卡时不做保存
        String storageState = Environment.getExternalStorageState();
        if(!storageState.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showToast(context, "未检测到SD卡", Toast.LENGTH_SHORT);
            return;
        }

        // 图片文件保存路径
        File storageDirectory = Environment.getExternalStorageDirectory();
        File path = new File(storageDirectory, "/boreweibo/weiboimg");
        // 图片路径不存在创建之
        if (!path.exists()) {
            path.mkdirs();
        }
        // 图片文件如果不存在创建之
        File myCaptureFile = new File(path, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        // 将图片压缩至文件对应的流里,即保存图片至该文件中
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }



    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     * @param context
     * @param imageUri
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        // MediaStore (and general)
        if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }



    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



    public static Bitmap safeDecodeBimtapFile(String bmpFile, BitmapFactory.Options opts) {
        BitmapFactory.Options optsTmp = opts;
        if(opts == null) {
            optsTmp = new BitmapFactory.Options();
            optsTmp.inSampleSize = 1;
        }

        Bitmap bmp = null;
        FileInputStream input = null;
        boolean MAX_TRIAL = true;
        int i = 0;

        while(i < 5) {
            try {
                input = new FileInputStream(bmpFile);
                bmp = BitmapFactory.decodeStream(input, (Rect)null, opts);

                try {
                    input.close();
                } catch (IOException var9) {
                    var9.printStackTrace();
                }
                break;
            } catch (OutOfMemoryError var11) {
                var11.printStackTrace();
                optsTmp.inSampleSize *= 2;

                try {
                    input.close();
                } catch (IOException var10) {
                    var10.printStackTrace();
                }

                ++i;
            } catch (FileNotFoundException var12) {
                break;
            }
        }

        return bmp;
    }

    public  static  Bitmap  decodeBitmapFromInputStream(InputStream inputStream, BitmapFactory.Options opts){

        if (inputStream==null)
            return  null;


        BitmapFactory.Options optstmp=opts;


        if (optstmp==null){
            optstmp = new BitmapFactory.Options();
            optstmp.inSampleSize=8;
        }

        Bitmap bitmap=null;

        try{

            bitmap = BitmapFactory.decodeStream(inputStream, (Rect)null, optstmp);
        }catch (OutOfMemoryError error){


        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bitmap;

    }
}
