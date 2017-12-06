package com.example.myapplication;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    // 웹뷰 사용시 필요한 요소들
    private WebView mWebView;
    private EditText url_String;

    // 데이터베이스 사용시 필요한 요소
    TextView idView;
    EditText productBox;
    EditText quantityBox;

    // 스레드 (클라이언트)
    private Socket socket;
    BufferedReader socket_in;
    PrintWriter socket_out;
    EditText input;
    Button button;
    TextView output;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        // 데이터베이스 부분 #################
        idView = (TextView) findViewById(R.id.productID);
        productBox = (EditText) findViewById(R.id.productName);
        quantityBox = (EditText) findViewById(R.id.productQuantity);


        // 스레드 (클라이언트) ########################
        input = (EditText) findViewById(R.id.input);
        button = (Button) findViewById(R.id.button);
        output = (TextView) findViewById(R.id.output);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String data = input.getText().toString();
                Log.w("NETWORK", " " + data);
                if(data != null) {
                    socket_out.println(data);
                }
            }
        });


        // id가 tabhost1인 탭 호스트를 생성.
        TabHost tabHost = (TabHost) findViewById(R.id.tabhost1);
        tabHost.setup();

        // 탭호스트를 등록하는 부분. id 값에 맞는 탭에 연결이 된다. setIndicator("") 부분은 탭의 이름을 별도로 지정해준다.
        TabHost.TabSpec tab1 = tabHost.newTabSpec("1").setContent(R.id.w).setIndicator("WEB");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("2").setContent(R.id.d).setIndicator("DB");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("3").setContent(R.id.t).setIndicator("THREAD");

        // 탭과 탭호스트를 연결해준다. 3개의 탭 생성.
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);


        // 웹뷰를 생성해줌. id가 webv인 웹뷰 컨텐츠에 연결. ############################################################################################     웹뷰
        mWebView = (WebView) findViewById(R.id.webv);

        // 페이지를 이동해주는 요소들 생성. edittext와 button을 사용해서
        Button pm = (Button)findViewById(R.id.pm);
        url_String = (EditText)findViewById(R.id.url);


        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 자바스크립드를 실행할 수 있도록 설정,
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND); // 플러그인을 사용할 수 있도록 설정
        webSettings.setSupportMultipleWindows(true); // 여러개의 윈도우를 사용할 수 있도록 설정.
        webSettings.setSupportZoom(true); // 페이지 확대 축소 기능을 사용할 수 있도록 설정

        // 출처: http://jaehoon0210.tistory.com/entry/안드로이드-WebSettings를-사용하여-WebView-설정


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            // 새로운 창을 만들지 않고 웹뷰 안에서 다른 페이지를 로딩한다.
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            // http://webnautes.tistory.com/473 에서 페이지 이동방법 가져옴.
            // onPageStarted : 페이지로드가 시작되었음을 호스트 응용 프로그램에 알립니다.
            // Bitmap: 데이터베이스에 이미있는 경우이 페이지에 대한 favicon입니다.
            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon){
                String urlString = mWebView.getUrl().toString();
                url_String.setText(urlString);
            }
        });

        mWebView.loadUrl("http://www.google.com");

        // 버튼에 리스너를 연결. view에 관련된 리스너이다.
        pm.setOnClickListener(new View.OnClickListener(){

            // onClick이벤트. urlString 에 editText(url_String)의 값을 가져와 String값으로 저장해준다.
            @Override
            public void onClick(View view) {
                String urlString = url_String.getText().toString();
                // 만약에 urlString이 http로 시작하지 않을 경우에는 http://를 붙여 다시 저장해준다.
                if(urlString.startsWith("http") != true)
                    urlString = "http://"+urlString;

                // 주소를 웹뷰의 Url로 연결해준다.
                mWebView.loadUrl(urlString);
            }
        });

        url_String.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER)
                {
                    String urlString = url_String.getText().toString();

                    if (urlString.startsWith("http") != true)
                        urlString = "http://" + urlString;
                    mWebView.loadUrl(urlString);
                    return true;
                }
                return false;
            }
        });
        // ########################################################################################################################################     웹뷰 끝 아직 onCreate() 스레드 부분

        Thread worker = new Thread() {
            public void run() {
                try {
//                    socket = new Socket("192.168.219.108", 8000); // 집 와이파이 IP
                    socket = new Socket("10.1.14.60", 8000); // 학교 와이파이 IP
                    socket_out = new PrintWriter(socket.getOutputStream(), true);
                    socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    while (true) {
                        data = socket_in.readLine();
                        output.post(new Runnable() {
                            public void run() {
                                output.setText(data);
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        };
        worker.start();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ########################################################################################################################################    스레드 끝, 데이타베이스 시작
    public void newProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int quantity =
                Integer.parseInt(quantityBox.getText().toString());
        Product product =
                new Product(productBox.getText().toString(), quantity);
        dbHandler.addProduct(product);
        productBox.setText("");
        quantityBox.setText("");
    }

    public void lookupProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        Product product =
                dbHandler.findProduct(productBox.getText().toString());
        if (product != null) {
            idView.setText(String.valueOf(product.getID()));
            quantityBox.setText(String.valueOf(product.getQuantity()));
        } else {
            idView.setText("No Match Found");
        }
    }

    public void removeProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null,
                null, 1);
        boolean result = dbHandler.deleteProduct(
                productBox.getText().toString());
        if (result)
        {
            idView.setText("Record Deleted");
            productBox.setText("");
            quantityBox.setText("");
        }
        else
            idView.setText("No Match Found");
    }


    // ################################################################################################## 데이터베이스 끝 스레드





}

