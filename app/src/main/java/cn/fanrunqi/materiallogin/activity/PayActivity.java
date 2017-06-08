package cn.fanrunqi.materiallogin.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import cn.fanrunqi.materiallogin.R;
import cn.fanrunqi.materiallogin.Utils.HttpUtils;

public class PayActivity extends AppCompatActivity {
    private ImageView iv_pay_image;
    private Button bt_put_info;
    private Context context;
    private ProgressBar pb;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 5:
                    Bitmap response = (Bitmap) msg.obj;
                    if (response != null){
                        pb.setVisibility(View.GONE);
                        iv_pay_image.setImageBitmap(response);

                    }
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        context = this;

        initialUI();
        showPyQR();

    }

    private void showPyQR() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String payQRUrl = bundle.getString("payQRUrl");
        HttpUtils.okhttp_show_qrImage(context, payQRUrl, iv_pay_image, mHandler);
    }

    private void initialUI() {
        iv_pay_image = (ImageView) findViewById(R.id.iv_pay_image);
        bt_put_info = (Button) findViewById(R.id.bt_put_info);
        pb = (ProgressBar) findViewById(R.id.pb_wait);
    }
}
