package imagecup.nju.intellichens.automemo.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import imagecup.nju.intellichens.automemo.util.HttpConnector;
import imagecup.nju.intellichens.automemo.util.User;

/**
 * Created by Hanifor on 3/23/2017.
 */

public class AudioRecorder {
    private final static int INTERNAL = 40;

    private static AudioRecorder audioRecorder;
    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;

    //录音对象
    private AudioRecord audioRecord;

    //录音状态
    private Status status = Status.STATUS_NO_READY;

    private FileUtils fileUtils;
    //pcm文件
    private File pcmFile;
    //wav文件
    private File wavFile;

    private String recordID;

    private AudioRecorder() {}

    //单例模式
    public static AudioRecorder getInstance() {
        if (audioRecorder == null) {
            audioRecorder = new AudioRecorder();
        }
        return audioRecorder;
    }

    /**
     * 创建录音对象
     */
    public void createAudio(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        JSONObject result = (JSONObject)HttpConnector.post("record/ready", null);
        try {
            recordID = result.getString("res");
        } catch (JSONException e) {
            return;
        }

        if(recordID == null || recordID.equals("-1")){
            return;
        }
        Log.i("Record Index", recordID);
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        status = Status.STATUS_READY;
        fileUtils = FileUtils.getInstance();
    }

    /**
     * 创建默认的录音对象
     *
     */
    public void createDefaultAudio() {
        this.createAudio(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
    }
    /**
     * 开始录音
     */
    public boolean startRecord() {
        if (status == Status.STATUS_NO_READY ) {
            Log.e("start Audio error","录音尚未初始化,请检查是否禁止了录音权限~");
            return false;
        }
        Log.d("AudioRecorder","===startRecord==="+audioRecord.getState());
        pcmFile = fileUtils.getPcmFile();
        wavFile = fileUtils.getWavFile();
        audioRecord.startRecording();
        status = Status.STATUS_START;
        new Thread(new Runnable() {
            public void run() {
                writeDataTOFile(pcmFile, wavFile);
            }
        }).start();
        return true;
    }

    /**
     * 暂停录音
     */
    public boolean pauseRecord() {
        if (status != Status.STATUS_START) {
            Log.e("Pause Audio Error","Not Recording Now");
            return false;
        } else {
            status = Status.STATUS_PAUSE;
            audioRecord.stop();
            Log.d("AudioRecorder","===pauseRecord===");
            return true;
        }
    }

    /**
     * 取消录音
     */
    public boolean cancel() {
        //TODO
        Map<String, String> paras = new HashMap<>();
        paras.put("id", recordID);
        JSONObject result = (JSONObject)HttpConnector.post("record/cancel", paras);
        int res;
        try {
            res = result.getInt("res");
        } catch (JSONException e) {
            return false;
        }
        if(res == 1){
            audioRecord.release();
            audioRecord = null;
            recordID = null;
            status = Status.STATUS_NO_READY;
            return  true;
        }
        return  false;
    }

    /**
     * 提交结束信号，获取record编号
     * @return
     */
    public String analysis() {
        //TODO
        Map<String, String> paras = new HashMap<>();
        paras.put("id", recordID);
        paras.put("flag", recordID);
        HttpConnector.post("record/finish", paras);
        return recordID;
    }

    /**
     * 将音频信息写入文件
     */
    private void writeDataTOFile(File rawFile, File audioFile) {
        Log.d("AudioRecorder","===file created==="+audioRecord.getState());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pcmFile);// 建立一个可存取字节的文件
        } catch (FileNotFoundException e) {
            Log.e("AudioRecorder", e.getMessage());
        }

        status = Status.STATUS_START;
        Log.d("AudioRecorder","===file created==="+ pcmFile.getAbsolutePath());
        byte[] audiodata = new byte[bufferSizeInBytes];
        int buffread;
        TimeListener listener = new TimeListener(INTERNAL);
        while (status == Status.STATUS_START ) {
            buffread = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != buffread && fos != null) {
                try {
                    fos.write(audiodata);
                    if (listener.isTimeUp()) {
                        break;
                    }
                } catch (IOException e) {
                    Log.e("AudioRecorder", e.getMessage());
                }
            }
        }
        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            Log.e("AudioRecorder", e.getMessage());
        }
        if(status == Status.STATUS_START){
            Log.e("Start Audio stop","录音暂停");
            audioRecord.stop();
            this.startRecord();
        }
        this.convertPCMToWAV(rawFile, audioFile);
    }

    /**
     * 将pcm合并成wav
     */
    private void convertPCMToWAV(File rawFile, File audioFile) {
        PcmToWav convertor = new PcmToWav();
        convertor.convertToWav(rawFile, audioFile);
        Log.v("Audio File Save", audioFile.getAbsolutePath());
        Log.v("Audio File Size", Long.toString(audioFile.length()));
        this.uploadFile(audioFile);
        rawFile.delete();
        audioFile.delete();
    }

    private void uploadFile(File audioFile){
        try {
            URL url = new URL(HttpConnector.ADDRESS + "speech/receive");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("cookie", User.getSessionId());
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            conn.setRequestProperty("recordId", recordID);
//            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
//            osw.write("recordId=" + recordID);
//            osw.flush();

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            InputStream inputStream = new FileInputStream(audioFile);
            int size = 1024;
            byte[] buffer = new byte[size];
            int length = -1;
            while((length = inputStream.read(buffer)) != -1){
                dataOutputStream.write(buffer, 0, length);
            }
            dataOutputStream.flush();
            dataOutputStream.close();
            inputStream.close();

            BufferedReader br;
            if (conn.getResponseCode() != 200) {
                Log.e("Response Result", "HTTP error code : " + conn.getResponseCode());
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            String line = br.readLine();
            Log.e("Return Data", line);
        } catch (MalformedURLException e) {
            Log.e("upload error", e.getMessage());
        }catch (IOException e) {
            Log.e("upload error", e.getMessage());
        }
    }

    /**
     * 获取录音对象的状态
     *
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 录音对象的状态
     */
    public  enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE
    }
}