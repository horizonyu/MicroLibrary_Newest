package cn.fanrunqi.materiallogin.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cn.fanrunqi.materiallogin.R;

public class LoginSuccessActivity extends AppCompatActivity {
    private Button bt_borrow;
    private Button bt_return;
    private Button bt_exit;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);

//        Explode explode = new Explode();
//        explode.setDuration(500);
//        getWindow().setExitTransition(explode);
//        getWindow().setEnterTransition(explode);

//        okhttp_login();

        bt_borrow = (Button) findViewById(R.id.bt_borrow);
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_exit = (Button) findViewById(R.id.bt_exit);

    }

    public void btOnClick(View view){
        switch (view.getId()){
            case R.id.bt_borrow:
                startActivity(new Intent(LoginSuccessActivity.this, BorrowActivity.class));
                break;
            case R.id.bt_return:
                startActivity(new Intent(LoginSuccessActivity.this, ReturnActivity.class));
                break;
            case R.id.bt_exit:
                LoginSuccessActivity.this.finish();
            default:
                break;
        }
    }


}
