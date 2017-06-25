package cn.fanrunqi.materiallogin.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;
import com.unstoppable.submitbuttonview.SubmitButton;

import cn.fanrunqi.materiallogin.R;

public class LoginSuccessActivity extends AppCompatActivity {
    private Button bt_borrow;
    private Button bt_return;
    private Button bt_exit;
    private SubmitButton sbt_borrow;
    private SubmitButton sbt_return;
    private SubmitButton sbt_exit;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);

        animation();
//        okhttp_login();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        bt_borrow = (Button) findViewById(R.id.bt_borrow);
        bt_return = (Button) findViewById(R.id.bt_return);
        bt_exit = (Button) findViewById(R.id.bt_exit);

        sbt_borrow = (SubmitButton) findViewById(R.id.sbt_borrow);

        CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);
        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                switch (menuButton.getId()){
                    case R.id.borrow_book:
                        showMessage("borrow_book!");
                        startActivity(new Intent(LoginSuccessActivity.this, BorrowActivity.class));
                        break;
                    case R.id.return_book:
                        showMessage("return_book!");
                        startActivity(new Intent(LoginSuccessActivity.this, ReturnActivity.class));
                        break;
                    case R.id.exit:
                        showMessage("exit");
                        LoginSuccessActivity.this.finish();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 入场动画
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void animation() {
        //入场动画
        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                break;

            case R.id.sbt_borrow:
                startActivity(new Intent(LoginSuccessActivity.this, BorrowActivity.class));
                sbt_borrow.reset();
                break;
            case R.id.sbt_return:
                startActivity(new Intent(LoginSuccessActivity.this, ReturnActivity.class));
                sbt_return.reset();
                break;
            case R.id.sbt_exit:
                LoginSuccessActivity.this.finish();
                sbt_exit.reset();
                break;
            default:
                break;
        }
    }


}
