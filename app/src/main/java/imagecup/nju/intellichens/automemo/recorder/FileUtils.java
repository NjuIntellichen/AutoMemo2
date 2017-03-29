package imagecup.nju.intellichens.automemo.recorder;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * Created by Hanifor on 3/23/2017.
 */

public class FileUtils {
    private static FileUtils instance = null;
    private String AUDIO_BASEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recorder/";
    private File dir;

    private FileUtils(){
        dir = new File(AUDIO_BASEPATH);
        //创建目录
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static FileUtils getInstance(){
        if(instance ==null && FileUtils.isSdcardMounted()){
            instance = new FileUtils();
        }
        return instance;
    }

    public File getPcmFile(){
        File file = null;
        try {
            file = File.createTempFile("recorder", ".pcm", dir);
        } catch (IOException e) {
        }
        return file;
    }

    public File getWavFile() {
        File file = null;
        try {
            file = File.createTempFile("recorder", ".wav", dir);
        } catch (IOException e) {
        }
        return file;
    }

    /**
     * 判断是否有外部存储设备sdcard
     * @return true | false
     */
    public static boolean isSdcardMounted() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        } else {
            Log.e("SD ERROR", "No SDCard");
            return false;
        }
    }

    public static void empty(File file){
        if(file.exists() && file.length() > 0){
            try {
                FileWriter fw = new FileWriter(file);
                fw.write("");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}