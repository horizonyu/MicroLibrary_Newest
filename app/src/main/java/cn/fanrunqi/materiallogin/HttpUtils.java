package cn.fanrunqi.materiallogin;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by horizon on 4/3/2017.
 */

public class HttpUtils {

    //获取登录的authorization信息
    public static void getAuthorization(final String url, final String username, final String password, final Handler mHandler) {
        //网络请求是耗时操作，需要在子线程中执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Accept", "application/json");
//                    connection.setRequestProperty("client_id","WXClientId");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    //拼接链接，在用户输入正确的用户名和密码的情况下进行授权登录
                    out.writeBytes("grant_type=password&username=" + username + "&password=" + password + "&client_id=WXClientId");


                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

//                    Message msg = new Message();
//                    msg.what = MainActivity.SHOW_RESPONSE;
                    //将服务器返回的结果放到message中
                    String access_token = MainActivity.parseJSONWithJSONObiect(builder.toString());
                    login(MainActivity.GET_RESPONSE_URL, access_token, mHandler);

//                    msg.obj = builder.toString();
//                    msg.obj = access_token;
//                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }


    public static void login(final String url, final String authorization, final Handler mHandler) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                HttpURLConnection connection = null;
                try {

                    connection = (HttpURLConnection) new URL(url).openConnection();     //实例化一个HttpURLConnection
                    connection.setRequestMethod("GET");                                 //设置请求方法
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
//                    connection.setRequestProperty("Accept","application/json");         //设置请求头
                    connection.setRequestProperty("Authorization", "Bearer " + authorization);       //设置认证权限
//                    connection.setRequestProperty("http","//122.112.226.254/api/Test");

                    InputStream in = connection.getInputStream();                       //获取响应流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder = new StringBuilder();

                    String line = " ";                                                  //从缓存器中将数据读出来
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    Message msg = new Message();
                    msg.what = MainActivity.GET_RESPONSE;
                    if (builder != null) {
                        msg.obj = builder.toString();
                        mHandler.sendMessage(msg);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }.start();

    }
}
