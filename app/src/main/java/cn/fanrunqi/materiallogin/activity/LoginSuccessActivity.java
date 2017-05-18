package cn.fanrunqi.materiallogin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;

import cn.fanrunqi.materiallogin.R;

public class LoginSuccessActivity extends AppCompatActivity {
    private Button bt_borrow;
    private Button bt_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);

        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

//        getResponse();

        bt_borrow = (Button) findViewById(R.id.bt_borrow);
        bt_return = (Button) findViewById(R.id.bt_return);

        bt_borrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSuccessActivity.this, BorrowActivity.class);
                startActivity(intent);
            }
        });

        bt_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSuccessActivity.this, ReturnActivity.class);
                startActivity(intent);
            }
        });
    }


}
