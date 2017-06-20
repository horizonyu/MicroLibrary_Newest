package cn.fanrunqi.materiallogin.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.droidbyme.dialoglib.AnimUtils;
import com.droidbyme.dialoglib.DroidDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.fanrunqi.materiallogin.R;
import cn.fanrunqi.materiallogin.Utils.HttpUtils;
import cn.fanrunqi.materiallogin.bean.BookDetailInfo;


public class BorrowActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private static final String TAG = BorrowActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private static final int CHOOSE_PHOTO = 3;

    public static String QR_INFO = "";
    public static String BOOK_ID = "";

    private QRCodeView mQRCodeView;
    private Context context;
    private String payQRUrl;
    private String userId;
    private HashMap<String, String> map = new HashMap<>();
    private List<BookDetailInfo> bookList = new ArrayList<BookDetailInfo>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    map = (HashMap<String, String>) msg.obj;
                    //2. 使用二维码信息（bookIds）获取加密的缓存值，并以此值作为访问路径的最后一部分，进行重定向请求，
                    //   获得借书单的详细信息以及用户id userId，通过对话框显示出来
                    String encryptedCacheKey = map.get("encryptedCacheKey");
                    MainActivity.ENCRYPTEDCACHEKEY = encryptedCacheKey;
                    Toast.makeText(BorrowActivity.this, "encryptedCacheKey: " + encryptedCacheKey, Toast.LENGTH_SHORT).show();
                    HttpUtils.okhttp_get_book_details(getApplicationContext(), MainActivity.ENCRYPTEDCACHEKEY, MainActivity.ACCESS_TOKEN, mHandler);
                    break;

                case 3:
                    //将书籍信息以对话框的形式表示出来
                    bookList = (List<BookDetailInfo>) msg.obj;
                    showBookDetails(bookList, context);
                    break;
                case 4:
                    payQRUrl = (String) msg.obj;
                    Intent intent = new Intent(BorrowActivity.this, PayActivity.class);
                    intent.putExtra("payQRUrl", payQRUrl);
                    //读者id
                    intent.putExtra("userId", userId);
                    startActivity(intent);

                    BorrowActivity.this.finish();
                    break;
                default:
                    break;

            }
        }
    };

    /**
     * 将用户借书信息显示出来
     *  @param bookList 书籍对象列表
     * @param context
     */
    private void showBookDetails(List<BookDetailInfo> bookList, final Context context) {
//        String state = map.get("state");

//        //需要
//        String bookId = map.get("bookId");
//        String author = map.get("author");
//        String title = map.get("title");
////        String book_id = map.get("id");
//        String price = map.get("price");
//        String deposit = map.get("deposit");
//        //需要
//        userId = map.get("userId");
//
//        BOOK_ID = bookId;

        String content = "";
        //逐个获取书籍对象，并得到指定的属性值
        for (int i = 0; i < bookList.size(); i++){
            BookDetailInfo bookDetailInfo = bookList.get(i);
            int state = bookDetailInfo.getState();
            String bookId = bookDetailInfo.getId();
            String author = bookDetailInfo.getAuthor();
            String title = bookDetailInfo.getTitle();
            double price = bookDetailInfo.getPrice();
            int deposit = bookDetailInfo.getDeposit();
            String userId = bookDetailInfo.getUserId();
            content = content + "state: " + state + "\n" +
                    "bookId: " + bookId + "\n" +
                    "author: " + author + "\n" +
                    "title: " + title + "\n" +
                    "price: " + price + "\n" +
                    "deposit: " + deposit + "\n" +
                    "userId: " + userId + "\n";
        }



        //将书籍信息以对话框的形式表示出来
        new DroidDialog.Builder(context)
                .icon(R.drawable.ic_action_tick)  //添加图标
                .title("All Well!")                //添加标题
                .content(content)   //添加内容
                .cancelable(true, false)                         //触摸对话框边缘可以取消对话框(boolean isCancelable, boolean isCancelableTouchOutside)
                .positiveButton("Borrow", new DroidDialog.onPositiveListener() {

                    @Override
                    public void onPositive(Dialog dialog) {
                        //获取并显示支付二维码
                        Toast.makeText(context, "获取支付二维码", Toast.LENGTH_SHORT).show();
                        HttpUtils.okhttp_get_payQRUrl(userId, QR_INFO, MainActivity.ACCESS_TOKEN, context, mHandler);

                    }
                })
                .negativeButton("Cancel", new DroidDialog.onNegativeListener() {

                    @Override
                    public void onNegative(Dialog dialog) {
                        Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                })
                .neutralButton("GetQR", new DroidDialog.onNeutralListener() {

                    @Override
                    public void onNeutral(Dialog dialog) {


                    }
                })
                .typeface("regular.ttf")                                        //修改字体
                .animation(AnimUtils.AnimZoomInOut)                             //添加对话框弹出与消失的动画
                .color(ContextCompat.getColor(context, R.color.color1),         //添加字体的颜色
                        ContextCompat.getColor(context, R.color.indigo),
                        ContextCompat.getColor(context, R.color.orange))
                .divider(true, ContextCompat.getColor(context, R.color.orange)) //添加分隔线
                .show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);

        mQRCodeView = (QRCodeView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);

        context = this;

    }

    /**
     * 此处是从本地相册打开二维码进行识别，
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mQRCodeView.showScanRect();

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String imagePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);

            AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... params) {
                    return QRCodeDecoder.syncDecodeQRCode(imagePath);
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if (TextUtils.isEmpty(result)) {
                        Toast.makeText(BorrowActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();

                    } else {
                        //识别二维码的信息
                        Toast.makeText(BorrowActivity.this, "图片二维码信息是：" + result, Toast.LENGTH_SHORT).show();

                        QR_INFO = result;
//                        encryptedCacheKey
//                        ENCRYPTED_CACHEKEY
                        //根据获取的二维码信息以及登录时获取的access_token获取借书信息，并通过对话框显示出来，确认之后调出支付二维码
                        //1. 使用access_token进行授权  MainActivity.ACCESS_TOKEN
//                        HttpUtils.okhttp_authorization(getApplicationContext(), MainActivity.ACCESS_TOKEN, result, mHandler);

                        //根据获取到的二维码信息（encryptedCacheKey），以及access_token，获取书籍的详细信息
                        HttpUtils.okhttp_get_book_details(context, result, MainActivity.ACCESS_TOKEN, mHandler);

                        Log.i(TAG, "onPostExecute: okhttp_get_book_details");
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

        Log.i(TAG, "onStart: ");
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

    public void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {

        QR_INFO = result;
        //成功扫描二维码/条形码
        Log.i(TAG, "扫描结果： " + result);
        Toast.makeText(this, "扫描结果：" + result, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onScanQRCodeSuccess: " + result);

        vibrate();
        mQRCodeView.startSpot();

        HttpUtils.okhttp_get_book_details(context, result, MainActivity.ACCESS_TOKEN, mHandler);





    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");

    }

    public void OnClick(View v) {
        switch (v.getId()) {
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
                Log.i(TAG, "OnClick: startActivityForResult");
                break;
            default:
                break;
        }
    }


}
