package com.haierbiomedical.ttslibrary.listener;

import com.baidu.tts.client.SpeechError;

public interface TTSListener{
    void onSynthesizeStart(String var1);

    void onSynthesizeDataArrived(String var1, byte[] var2, int var3, int var4);

    void onSynthesizeFinish(String var1);

    void onSpeechStart(String var1);

    void onSpeechProgressChanged(String var1, int var2);

    void onSpeechFinish(String var1);

    void onError(String var1, String var2);

}
