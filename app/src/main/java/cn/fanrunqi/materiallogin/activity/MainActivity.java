package cn.fanrunqi.materiallogin.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.fanrunqi.materiallogin.R;
import cn.fanrunqi.materiallogin.Utils.HttpUtils;

public class MainActivity extends AppCompatActivity {

    public static final int SHOW_RESPONSE = 0;
    public static String RESPONSE_CODE;
    public static String ACCESS_TOKEN = "";
    public static final int GET_RESPONSE = 1;
    public static final String WEB_URL = "https://eighthundred.cn/OAuth/Token";
//    public static final String GET_RESPONSE_URL = "http://122.112.226.254/api/Test";


    private static String access_token;
    private static String token_type;
    private static String error;
    private static String errorDescription;
    private static String response = "";

    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(SHOW_RESPONSE == msg.what){
                response = msg.obj.toString();
            }

        }
    };



    @InjectView(R.id.et_username)
    EditText etUsername;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.bt_go)
    Button btGo;
    @InjectView(R.id.cv)
    CardView cv;
    @InjectView(R.id.fab)
    FloatingActionButton fab;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


//        onClick(btGo);

    }

    // 设置按钮的点击事件
    @OnClick({R.id.bt_go, R.id.fab})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
            case R.id.bt_go:
//                response = "Test";
//                Explode explode = new Explode();
//                explode.setDuration(500);

//                getWindow().setExitTransition(explode);
//                getWindow().setEnterTransition(explode);
                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(this);

                //对用户信息进行判断，若账户信息正确，则登录成功
                //登录
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String url = WEB_URL ;
                HttpUtils.getAuthorization(url,username,password, mHandler);


                if (!TextUtils.isEmpty(response)){
                    if (response.equals("bearer")){
                        Intent i2 = new Intent(this,LoginSuccessActivity.class);
                        startActivity(i2, oc2.toBundle());
                    }else {
                        Toast.makeText(MainActivity.this, "用户或密码无效", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "无法获取响应！", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    /**
     * @param response 解析Json数据，并将指定的数据返回
     * @return
     */
    public static String parseJSONWithJSONObiect(String response){

        try {

            JSONObject jsonObect = new JSONObject(response);
//            error = jsonObect.getString("error");

            //如果出现错误信息，则直接返回错误提示
//            if(!error.isEmpty()){
//                errorDescription = jsonObect.getString("error_description");
//                return errorDescription;
//            }
//            //否则，获取需要的信息
//            else {
                //否则，获取需要的信息
                access_token = jsonObect.getString("access_token");
                token_type = jsonObect.getString("token_type");

                String expires_in = jsonObect.getString("expires_in");
                String refresh_token = jsonObect.getString("refresh_token");

                Log.d("MainActivity","access_token: "+ access_token);
                Log.d("MainActivity","token_type: "+ token_type);
                Log.d("MainActivity","expires_in: "+ expires_in);
                Log.d("MainActivity","refresh_token: "+ refresh_token);
//            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
        String result = access_token + " " + token_type;
        return result;
    }
}
