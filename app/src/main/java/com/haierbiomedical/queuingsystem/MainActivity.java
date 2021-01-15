package com.haierbiomedical.queuingsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.haierbiomedical.ttslibrary.TTSManager;
import com.haierbiomedical.ttslibrary.TTSManager2;
import com.haierbiomedical.ttslibrary.listener.TTSListener;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TTSListener,TextToSpeech.OnInitListener {
    private Button clickBtn;
    private TextToSpeech mTextToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clickBtn = findViewById(R.id.btn_click);
        initTextToSpeech();
        clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TTSManager2.getInstance().init(MainActivity.this,MainActivity.this);
////                TTSManager.getInstance().init(MainActivity.this,"23331281","DZOD7fjkuI1dx0tOmW3WYwbz",
////                        "RNqNw5XMGQgNj6lLR7woRvL8gUVQKNvn","9ac3f213-79efb19c-0a52-0075-67dc3-00");
////                TTSManager2.getInstance().setListener(MainActivity.this);
                TTSManager2.getInstance().speak("Hello world");
//                TTSManager2.getInstance().getSynthesizer().release();
//                mTextToSpeech.speak("请100号到2号接种台接种", TextToSpeech.QUEUE_ADD, null);
            }
        });
        initPermission();

    }
    private void initTextToSpeech() {
        // 参数Context,TextToSpeech.OnInitListener
        mTextToSpeech = new TextToSpeech(getApplicationContext(), this);
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        mTextToSpeech.setPitch(0.5f);
        // 设置语速
        mTextToSpeech.setSpeechRate(0.9f);
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE // demo使用
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    @Override
    public void onSynthesizeStart(String var1) {
        Log.d(">>>>>","onSynthesizeStart:"+var1);
    }

    @Override
    public void onSynthesizeDataArrived(String var1, byte[] var2, int var3, int var4) {
        Log.d(">>>>>","onSynthesizeDataArrived:"+var1);
    }

    @Override
    public void onSynthesizeFinish(String var1) {
        Log.d(">>>>>","onSynthesizeFinish:"+var1);
    }

    @Override
    public void onSpeechStart(String var1) {
        Log.d(">>>>>","onSpeechStart:"+var1);
    }

    @Override
    public void onSpeechProgressChanged(String var1, int var2) {
        Log.d(">>>>>","onSpeechProgressChanged:"+var1);
    }

    @Override
    public void onSpeechFinish(String var1) {
        Log.d(">>>>>","onSpeechFinish:"+var1);
    }

    @Override
    public void onError(String var1, String var2) {
        Log.d(">>>>>","onError:"+var1);
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            /*
                使用的是小米手机进行测试，打开设置，在系统和设备列表项中找到更多设置，
            点击进入更多设置，在点击进入语言和输入法，见语言项列表，点击文字转语音（TTS）输出，
            首选引擎项有三项为Pico TTs，科大讯飞语音引擎3.0，度秘语音引擎3.0。其中Pico TTS不支持
            中文语言状态。其他两项支持中文。选择科大讯飞语音引擎3.0。进行测试。

                如果自己的测试机里面没有可以读取中文的引擎，
            那么不要紧，我在该Module包中放了一个科大讯飞语音引擎3.0.apk，将该引擎进行安装后，进入到
            系统设置中，找到文字转语音（TTS）输出，将引擎修改为科大讯飞语音引擎3.0即可。重新启动测试
            Demo即可体验到文字转中文语言。
             */
            // setLanguage设置语言
            int result = mTextToSpeech.setLanguage(Locale.CHINA);
            // TextToSpeech.LANG_MISSING_DATA：表示语言的数据丢失
            // TextToSpeech.LANG_NOT_SUPPORTED：不支持
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mTextToSpeech != null){
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }
}