package cn.fanrunqi.materiallogin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.fanrunqi.materiallogin.R;
import cn.fanrunqi.materiallogin.Utils.HttpUtils;

public class BorrowActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private static final String TAG = BorrowActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private static final int CHOOSE_PHOTO = 3;
    private QRCodeView mQRCodeView;

//    private Button bt_choose_qrcde_from_gallery;
//    private Button bt_open_flashlight;
//    private Button bt_close_flashlight;
private HashMap<String,String> map = new HashMap<>();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2:
                    map = (HashMap<String, String>) msg.obj;
                    //2. 使用二维码信息（bookIds）获取加密的缓存值，并以此值作为访问路径的最后一部分，进行重定向请求，
                    //   获得借书单的详细信息以及用户id userId，通过对话框显示出来

                    String encryptedCacheKey  = map.get("encryptedCacheKey");
                     MainActivity.ENCRYPTEDCACHEKEY = encryptedCacheKey;
                    Toast.makeText(BorrowActivity.this, encryptedCacheKey, Toast.LENGTH_SHORT).show();
                    HttpUtils.okhttp_get_book_details(getApplicationContext(),  MainActivity.ENCRYPTEDCACHEKEY, MainActivity.ACCESS_TOKEN, mHandler);
                    break;

                case 3:
                    //将书籍信息以对话框的形式表示出来
                   map = (HashMap<String, String>) msg.obj;
                    showBookDetails();
                default:
                    break;

            }
        }
    };

    /**
     * 将用户借书信息显示出来
     */
    private void showBookDetails() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
//        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mQRCodeView = (QRCodeView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);

//        bt_choose_qrcde_from_gallery = (Button) findViewById(R.id.tv_photo_picker_preview_choose);
//        bt_open_flashlight = (Button) findViewById(R.id.open_flashlight);
//        bt_close_flashlight = (Button) findViewById(R.id.close_flashlight);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mQRCodeView.showScanRect();

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY){
            final String imagePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);

            AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... params) {
                    return QRCodeDecoder.syncDecodeQRCode(imagePath);
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if (TextUtils.isEmpty(result)){
                        Toast.makeText(BorrowActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();

                    }else {
                        //识别二维码的信息
                        Toast.makeText(BorrowActivity.this, "二维码信息是：" + result, Toast.LENGTH_SHORT).show();

                        //根据获取的二维码信息以及登录时获取的access_token获取借书信息，并通过对话框显示出来，确认之后调出支付二维码
                        //1. 使用access_token进行授权  MainActivity.ACCESS_TOKEN
                        HttpUtils.okhttp_authorization(getApplicationContext(), MainActivity.ACCESS_TOKEN, result, mHandler);

                        //3. 使用识别二维码获得的bookIds以及用户id userId 获得支付二维码链接，并将图片显示出来供借书者进行付款
                    }
                }
            };
            asyncTask.execute();


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();

        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    public void vibrate(){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        //成功扫描二维码/条形码
        Log.i(TAG, "result : " + result);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

        vibrate();
        mQRCodeView.startSpot();
        
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");

    }

    public void OnClick(View v){
        switch (v.getId()){
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;
            case R.id.choose_qrcde_from_gallery:
//                Intent intent = new Intent("android.intent.action.GET_CONTENT");
//                intent.setType("image/*");
//                startActivityForResult(intent, CHOOSE_PHOTO);

                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
                break;
            default:
                break;
        }
    }



}
