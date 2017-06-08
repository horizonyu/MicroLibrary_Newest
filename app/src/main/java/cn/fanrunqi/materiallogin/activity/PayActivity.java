package cn.fanrunqi.materiallogin.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import cn.fanrunqi.materiallogin.R;
import cn.fanrunqi.materiallogin.Utils.HttpUtils;

public class PayActivity extends AppCompatActivity {
    private ImageView iv_pay_image;
    private Button bt_put_info;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        context = this;

        initialUI();
        showPyQR();

    }

    private void showPyQR() {
        String payQRUrl = (String) getIntent().getCharSequenceExtra("payQRUrl");
        HttpUtils.okhttp_show_qrImage(context, payQRUrl, iv_pay_image);
    }

    private void initialUI() {
        iv_pay_image = (ImageView) findViewById(R.id.iv_pay_image);
        bt_put_info = (Button) findViewById(R.id.bt_put_info);
    }
}
