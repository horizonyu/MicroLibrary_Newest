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
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.fanrunqi.materiallogin.R;
import cn.fanrunqi.materiallogin.Utils.HttpUtils;
import cn.fanrunqi.materiallogin.bean.BookDetailInfo;

public class ReturnActivity extends AppCompatActivity implements QRCodeView.Delegate{
    private static final String TAG = ReturnActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY_RETURN = 667;

    private QRCodeView mQRCodeView;
    private Context mContext;
    private Map<String, String> map;
    private List<BookDetailInfo> bookList = new ArrayList<BookDetailInfo>();
    //读者id
    private String userId = null;
    //书籍id
    private String bookBriefId = null;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
//                    map = new HashMap<String, String>();
                    bookList = (List<BookDetailInfo>) msg.obj;
                    showBorrowBooksDetail(bookList);
            }
        }
    };

    private void showBorrowBooksDetail(final List<BookDetailInfo> bookList) {
        String content = "";
        final String[] bookIds = new String[10];
        for (int i = 0; i < bookList.size(); i++){
            BookDetailInfo bookDetailInfo = bookList.get(i);
            String bookTitle = bookDetailInfo.getTitle();
            bookBriefId = bookDetailInfo.getId();
            String bookId = bookDetailInfo.getSearchId();
            int deposit = bookDetailInfo.getDeposit();
            String borrowTime = bookDetailInfo.getBorrowTime();
            userId = bookDetailInfo.getUserId();
            String nickName = bookDetailInfo.getNickName();
            content = content + "bookTitle: " + bookTitle + "\n" +
                    "bookBriefId: " + bookBriefId + "\n" +
                    "bookId: " + bookId + "\n" +
                    "deposit: " + deposit + "\n" +
                    "borrowTime: " + borrowTime + "\n" +
                    "userId: " + userId + "\n" +
                    "nickName: " + nickName + "\n";
            bookIds[i] = bookBriefId;
        }
        //将书籍信息以对话框的形式表示出来
        new DroidDialog.Builder(mContext)
                .icon(R.drawable.ic_action_tick)  //添加图标
                .title("All Well!")                //添加标题
                .content(content)   //添加内容
                .cancelable(true, false)                         //触摸对话框边缘可以取消对话框(boolean isCancelable, boolean isCancelableTouchOutside)
                .positiveButton("Return", new DroidDialog.onPositiveListener() {

                    @Override
                    public void onPositive(Dialog dialog) {

                        Toast.makeText(mContext, "positive", Toast.LENGTH_SHORT).show();

                        //在确认无误后，还给用户押金，并修改用户信息以及书籍信息
                        String bIds = "\"";
                        if (bookList.size() == 1){
                            bIds += bookIds[0] + "\"";
                        }else {
                            for (int i = 0; i < bookList.size() - 1; i++){
                                bIds += bookIds[i] + "\"" + ",";
                            }
                            bIds += "\"" + bookIds[bookList.size() -1] + "\"";
                        }
                           Log.i(TAG, "bIds: " + bIds);
                        HttpUtils.okhttp_put_return_info(mContext, bIds, MainActivity.ACCESS_TOKEN, userId);
                        dialog.dismiss();
                        ReturnActivity.this.finish();
                    }
                })
                .negativeButton("Cancel", new DroidDialog.onNegativeListener() {

                    @Override
                    public void onNegative(Dialog dialog) {
                        Toast.makeText(mContext, "Cancel", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                })
                .neutralButton("GetQR", new DroidDialog.onNeutralListener() {

                    @Override
                    public void onNeutral(Dialog dialog) {
                        Toast.makeText(mContext, "onNeutral", Toast.LENGTH_SHORT).show();

                    }
                })
                .typeface("regular.ttf")                                        //修改字体
                .animation(AnimUtils.AnimZoomInOut)                             //添加对话框弹出与消失的动画
                .color(ContextCompat.getColor(mContext, R.color.color1),         //添加字体的颜色
                        ContextCompat.getColor(mContext, R.color.indigo),
                        ContextCompat.getColor(mContext, R.color.orange))
                .divider(true, ContextCompat.getColor(mContext, R.color.orange)) //添加分隔线
                .show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
//        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        mQRCodeView = (QRCodeView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);

        mContext = this;
    }

    /**
     * 成功识别二维码
     * @param result 二维码包含的信息
     */
    @Override
    public void onScanQRCodeSuccess(String result) {
        //还书入口
        Toast.makeText(this, "扫描的二维码信息是: " + result, Toast.LENGTH_SHORT).show();
        Log.i("ReturnBookInfo", "onScanQRCodeSuccess: " + result);
        vibrate();
        mQRCodeView.startSpot();

        HttpUtils.okhttp_get_borrow_info(mContext, result, MainActivity.ACCESS_TOKEN, mHandler);


    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "打开相机出错", Toast.LENGTH_SHORT).show();

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
        super.onStop();
        mQRCodeView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQRCodeView.onDestroy();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;
            case R.id.choose_qrcde_from_gallery:
                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY_RETURN);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mQRCodeView.showScanRect();

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY_RETURN) {
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
                        Toast.makeText(mContext, "未发现二维码", Toast.LENGTH_SHORT).show();

                    } else {
                        //识别二维码的信息
                        Toast.makeText(mContext, "图片二维码信息是：" + result, Toast.LENGTH_SHORT).show();

                    }
                }
            };
            asyncTask.execute();
        }
    }
}
