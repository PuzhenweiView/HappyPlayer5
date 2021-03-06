package com.zlm.hp.application;

import android.app.Application;

import com.zlm.hp.constants.PreferencesConstants;
import com.zlm.hp.constants.ResourceConstants;
import com.zlm.hp.libs.crash.CrashHandler;
import com.zlm.hp.libs.utils.LoggerUtil;
import com.zlm.hp.libs.utils.PreferencesUtil;
import com.zlm.hp.manager.AudioPlayerManager;
import com.zlm.hp.model.AudioInfo;
import com.zlm.hp.model.AudioMessage;
import com.zlm.hp.net.entity.RankListResult;
import com.zlm.hp.utils.ResourceFileUtil;
import com.zlm.hp.utils.SerializableObjUtil;

import java.io.File;
import java.util.List;

/**
 * Created by zhangliangming on 2017/7/15.
 */
public class HPApplication extends Application {
    /**
     * 播放服务是否被强迫回收
     */
    private boolean playServiceForceDestroy = false;
    /**
     * 应用关闭
     */
    private boolean appClose = false;
    /**
     * 应用是否是第一次启动
     */
    private boolean isFrist = true;

    /**
     * 是否开启问候音
     */
    private boolean isSayHello = false;

    /**
     * 应用是否在wifi下联网
     */
    private boolean isWifi = true;

    /**
     * 播放歌曲id
     */
    private String playIndexHashID = "";

    /**
     * 底部按钮是否打开
     */
    private boolean isBarMenuShow = false;

    /**
     * 歌曲播放模式
     */
    private int playModel = 0; // 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放

    /**
     * 播放歌曲状态
     */
    private int playStatus;

    /**
     * 当前播放列表
     */
    private List<AudioInfo> curAudioInfos;
    /**
     * 设置当前正在播放的歌曲
     */
    private AudioInfo curAudioInfo;

    /**
     * 当前歌曲
     */
    private AudioMessage curAudioMessage;

    /**
     * 排行数据
     */
    private RankListResult rankListResult;
    ;
    /**
     *
     */
    private LoggerUtil logger;

    /**
     * 是否是歌词快进
     */
    private boolean isLrcSeekTo = false;

    /**
     * 歌词字体大小
     */
    private int lrcFontSize = 30;
    /**
     * 最小字体大小
     */
    private int minLrcFontSize = 30;

    /**
     * 最大字体大小
     */
    private int maxLrcFontSize = 50;
    /**
     * 歌词颜色索引
     */
    private int lrcColorIndex = 0;

    /**
     * 歌词颜色集合
     */
    private String[] lrcColorStr = {"#fada83", "#fe8db6", "#feb88e",
            "#adfe8e", "#8dc7ff", "#e69bff"};


    /**
     * 是否线控
     */
    private boolean isWire = true;

    private static HPApplication instance;

    public static HPApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //
        //注册捕捉全局异常
        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init(getApplicationContext());

        logger = LoggerUtil.getZhangLogger(getApplicationContext());
    }

    public boolean isPlayServiceForceDestroy() {
        return playServiceForceDestroy;
    }

    public void setPlayServiceForceDestroy(boolean playServiceForceDestroy) {
        this.playServiceForceDestroy = playServiceForceDestroy;
    }

    public boolean isAppClose() {
        return appClose;
    }

    public void setAppClose(boolean appClose) {
        this.appClose = appClose;
    }

    public boolean isFrist() {
        return isFrist;
    }

    public void setFrist(boolean frist) {
        isFrist = frist;
        //
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.isFrist_KEY, isFrist);
    }

    public boolean isSayHello() {
        return isSayHello;
    }

    public void setSayHello(boolean sayHello) {
        isSayHello = sayHello;
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.isSayHello_KEY, isSayHello);
    }

    public boolean isWifi() {
        return (boolean) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.isWifi_KEY, isWifi);
    }

    public void setWifi(boolean wifi) {
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.isWifi_KEY, wifi);
    }

    public String getPlayIndexHashID() {
        return (String) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.playIndexHashID_KEY, playIndexHashID);
    }

    public void setPlayIndexHashID(String playIndexHashID) {
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.playIndexHashID_KEY, playIndexHashID);
    }

    public boolean isBarMenuShow() {
        return (boolean) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.isBarMenuShow_KEY, isBarMenuShow);
    }

    public void setBarMenuShow(boolean barMenuShow) {

        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.isBarMenuShow_KEY, barMenuShow);
    }

    public int getPlayModel() {
        return (int) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.playModel_KEY, playModel);
    }

    public void setPlayModel(int playModel) {
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.playModel_KEY, playModel);
    }

    public int getPlayStatus() {
        return (int) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.playStatus_KEY, AudioPlayerManager.STOP);
    }

    public void setPlayStatus(int playStatus) {
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.playStatus_KEY, playStatus);
    }

    public List<AudioInfo> getCurAudioInfos() {
        if (curAudioInfos == null) {
            logger.e("curAudioInfos为空，从本地获取");
            String filePath = ResourceFileUtil.getFilePath(getApplicationContext(), ResourceConstants.PATH_CACHE_SERIALIZABLE) + File.separator + "curAudioInfos.ser";
            curAudioInfos = (List<AudioInfo>) SerializableObjUtil.readObj(filePath);
        }

        return curAudioInfos;
    }

    public void setCurAudioInfos(final List<AudioInfo> curAudioInfos) {
        this.curAudioInfos = curAudioInfos;
        new Thread() {
            @Override
            public void run() {
                String filePath = ResourceFileUtil.getFilePath(getApplicationContext(), ResourceConstants.PATH_CACHE_SERIALIZABLE) + File.separator + "curAudioInfos.ser";
                if (curAudioInfos != null) {
                    SerializableObjUtil.saveObj(filePath, curAudioInfos);
                } else {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }.start();
    }

    public AudioInfo getCurAudioInfo() {
        if (curAudioInfo == null) {
            logger.e("curAudioInfo为空，从本地获取");
            String filePath = ResourceFileUtil.getFilePath(getApplicationContext(), ResourceConstants.PATH_CACHE_SERIALIZABLE) + File.separator + "curAudioInfo.ser";
            curAudioInfo = (AudioInfo) SerializableObjUtil.readObj(filePath);
        }
        return curAudioInfo;
    }

    public void setCurAudioInfo(final AudioInfo curAudioInfo) {
        this.curAudioInfo = curAudioInfo;
        new Thread() {
            @Override
            public void run() {
                String filePath = ResourceFileUtil.getFilePath(getApplicationContext(), ResourceConstants.PATH_CACHE_SERIALIZABLE) + File.separator + "curAudioInfo.ser";
                if (curAudioInfo != null) {
                    SerializableObjUtil.saveObj(filePath, curAudioInfo);
                } else {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }.start();


    }

    public AudioMessage getCurAudioMessage() {
        if (curAudioMessage == null) {
            logger.e("curAudioMessage为空，从本地获取");
            String filePath = ResourceFileUtil.getFilePath(getApplicationContext(), ResourceConstants.PATH_CACHE_SERIALIZABLE) + File.separator + "curAudioMessage.ser";
            curAudioMessage = (AudioMessage) SerializableObjUtil.readObj(filePath);
        }
        return curAudioMessage;
    }

    public void setCurAudioMessage(final AudioMessage curAudioMessage) {
        this.curAudioMessage = curAudioMessage;
        new Thread() {
            @Override
            public void run() {
                String filePath = ResourceFileUtil.getFilePath(getApplicationContext(), ResourceConstants.PATH_CACHE_SERIALIZABLE) + File.separator + "curAudioMessage.ser";
                if (curAudioMessage != null) {
                    SerializableObjUtil.saveObj(filePath, curAudioMessage);
                } else {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }.start();
    }

    public RankListResult getRankListResult() {
        if (rankListResult == null) {
            logger.e("rankListResult为空，从本地获取");
            String filePath = ResourceFileUtil.getFilePath(getApplicationContext(), ResourceConstants.PATH_CACHE_SERIALIZABLE) + File.separator + "rankListResult.ser";
            rankListResult = (RankListResult) SerializableObjUtil.readObj(filePath);
        }
        return rankListResult;
    }

    public void setRankListResult(final RankListResult rankListResult) {
        this.rankListResult = rankListResult;
        new Thread() {
            @Override
            public void run() {
                String filePath = ResourceFileUtil.getFilePath(getApplicationContext(), ResourceConstants.PATH_CACHE_SERIALIZABLE) + File.separator + "rankListResult.ser";
                if (rankListResult != null) {
                    SerializableObjUtil.saveObj(filePath, rankListResult);
                } else {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }.start();
    }

    public boolean isLrcSeekTo() {
        return isLrcSeekTo;
    }

    public void setLrcSeekTo(boolean lrcSeekTo) {
        isLrcSeekTo = lrcSeekTo;
    }

    public int getLrcFontSize() {
        return (int) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.lrcFontSize_KEY, lrcFontSize);
    }

    public void setLrcFontSize(int lrcFontSize) {

        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.lrcFontSize_KEY, lrcFontSize);
    }

    public int getLrcColorIndex() {
        return (int) PreferencesUtil.getValue(getApplicationContext(), PreferencesConstants.lrcColorIndex_KEY, lrcColorIndex);

    }

    public void setLrcColorIndex(int lrcColorIndex) {
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.lrcColorIndex_KEY, lrcColorIndex);
    }

    public boolean isWire() {
        return isWire;
    }

    public void setWire(boolean wire) {
        isWire = wire;
        PreferencesUtil.saveValue(getApplicationContext(), PreferencesConstants.isWire_KEY, isWire);
    }

    ///////////////////////


    public String[] getLrcColorStr() {
        return lrcColorStr;
    }

    public int getMinLrcFontSize() {
        return minLrcFontSize;
    }

    public int getMaxLrcFontSize() {
        return maxLrcFontSize;
    }
}
