package com.haierbiomedical.ttslibrary;

import android.content.Context;
import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.haierbiomedical.ttslibrary.control.InitConfig;
import com.haierbiomedical.ttslibrary.control.NonBlockSyntherizer;
import com.haierbiomedical.ttslibrary.listener.FileSaveListener;
import com.haierbiomedical.ttslibrary.listener.TTSListener;
import com.haierbiomedical.ttslibrary.util.Auth;
import com.haierbiomedical.ttslibrary.util.AutoCheck;
import com.haierbiomedical.ttslibrary.util.OfflineResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TTSManager2 {
    private static volatile TTSManager2 instance = null;
    protected String offlineVoice = OfflineResource.VOICE_MALE;
    private Context mContext;
    private NonBlockSyntherizer synthesizer;
    protected String appId;
    protected String appKey;
    protected String secretKey;
    protected String sn; // 纯离线合成SDK授权码；离在线合成SDK没有此参数

    private TTSListener listener = null;

    public static synchronized TTSManager2 getInstance() {
        if (instance == null)
            instance = new TTSManager2();
        return instance;
    }
    public TTSManager2(){}

    public void init(Context context){
        this.mContext = context;
        appId = Auth.getInstance(mContext).getAppId();
        appKey = Auth.getInstance(mContext).getAppKey();
        secretKey = Auth.getInstance(mContext).getSecretKey();
        sn = Auth.getInstance(mContext).getSn(); // 离线合成SDK必须有此参数；在线合成SDK没有此参数
        initialTts(appId,appKey,secretKey,sn);
    }


    protected void initialTts(String appId, String appKey, String secretKey, String sn) {
        Map<String, String> params = getParams();
        // 添加你自己的参数
        InitConfig initConfig;
        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。

        initConfig = new InitConfig(appId, appKey, secretKey, sn, TtsMode.OFFLINE, params, new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {
                if(listener == null) return;
                listener.onSynthesizeStart(s);
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i, int i1) {
                if(listener == null) return;
                listener.onSynthesizeDataArrived(s,bytes,i,i1);
            }

            @Override
            public void onSynthesizeFinish(String s) {
                if(listener == null) return;
                listener.onSynthesizeFinish(s);
            }

            @Override
            public void onSpeechStart(String s) {
                if(listener == null) return;
                listener.onSynthesizeStart(s);
            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {
                if(listener == null) return;
                listener.onSpeechProgressChanged(s,i);
            }

            @Override
            public void onSpeechFinish(String s) {
                if(listener == null) return;
                listener.onSpeechFinish(s);
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                if(listener == null) return;
                listener.onError(s,speechError.toString());
            }
        });

        // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
        // 上线时请删除AutoCheck的调用
        AutoCheck.getInstance(mContext).check(initConfig);
        synthesizer = new NonBlockSyntherizer(mContext, initConfig);
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>, 其它发音人见文档
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "15");
        // 设置合成的语速，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

            // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
            OfflineResource offlineResource = createOfflineResource(offlineVoice);
            // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
            params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
            params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, offlineResource.getModelFilename());
        return params;
    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(mContext, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            Log.e(">>>>>>",e.getMessage());
            e.printStackTrace();
        }
        return offlineResource;
    }

    public void speak(String msg){
        synthesizer.speak(msg);
    }
    public TTSListener getListener() {
        return listener;
    }

    public void setListener(TTSListener listener) {
        this.listener = listener;
    }
    public NonBlockSyntherizer getSynthesizer() {
        return synthesizer;
    }

    public void setSynthesizer(NonBlockSyntherizer synthesizer) {
        this.synthesizer = synthesizer;
    }
}
