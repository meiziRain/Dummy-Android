package com.meizi.dummy.utils;

import android.os.Environment;

import java.io.File;

/**
 * @Classname Constants
 * @Description TODO
 * @Date 2020/2/13 15:35
 * @Created by jion
 */
public class Constants {
    public static final String CAMERA_IMAGE_PATH = "camera_image_path";
    public static final String IMAGE_WIDTH = "image_width";
    public static final String IMAGE_HEIGHT = "image_height";
    public static final String VIDEO_TIME = "video_time";
    public static final String CAMERA_VIDEO_PATH = "camera_video_path";
    public static final String IMAGE_DATA = "image_data";
    public static final String SELF_MESSAGE = "self_message";
    public static final String CAMERA_TYPE = "camera_type";


    public static String APP_DIR = File.separator + "mnt"
            + File.separator + "sdcard" + File.separator + "dummy";

    // 用户没有上传到Dummy后台的录音
    public static String RECORD_DIR = APP_DIR + File.separator + "record" + File.separator;
    // 用户保存的需要用来接龙的录音
    public static String RECORD_Star_DIR = APP_DIR + File.separator + "record" + File.separator + "star" + File.separator;
    public static String RECORD_DOWNLOAD_DIR = APP_DIR + File.separator + "record/download/";
    // 录音最大时长
    public final static int DEFAULT_AUDIO_RECORD_MAX_TIME = 60;



    public static String VIDEO_DOWNLOAD_DIR = APP_DIR + "/video/download/";
    public static String IMAGE_BASE_DIR = APP_DIR + "/image/";
    public static String IMAGE_DOWNLOAD_DIR = IMAGE_BASE_DIR + "download/";
    public static String MEDIA_DIR = APP_DIR + "/media";
    public static String FILE_DOWNLOAD_DIR = APP_DIR + "/file/download/";
    public static String CRASH_LOG_DIR = APP_DIR + "/crash/";
    public static String UI_PARAMS = "ilive_ui_params";
    public static String SOFT_KEY_BOARD_HEIGHT = "soft_key_board_height";

    public static String covert2HTMLString(String original) {
        return "\"<font color=\"#5B6B92\">" + original + "</font>\"";
    }

    public static final class ActivityRequest {
        public static final int CODE_1 = 1;
    }

    public static final class Group {
        public static final int MODIFY_GROUP_NAME = 0X01;
        public static final int MODIFY_GROUP_NOTICE = 0X02;
        public static final int MODIFY_GROUP_JOIN_TYPE = 0X03;
        public static final int MODIFY_MEMBER_NAME = 0X11;

        public static final String GROUP_ID = "group_id";
        public static final String GROUP_INFO = "groupInfo";
        public static final String MEMBER_APPLY = "apply";
    }

    public static class Selection {
        public static final String CONTENT = "content";
        public static final String TYPE = "type";
        public static final String TITLE = "title";
        public static final String INIT_CONTENT = "init_content";
        public static final String DEFAULT_SELECT_ITEM_INDEX = "default_select_item_index";
        public static final String LIST = "list";
        public static final String LIMIT = "limit";
        public static final int TYPE_TEXT = 1;
        public static final int TYPE_LIST = 2;
    }

    public static class ProfileType {
        public static final String CONTENT = "content";
        public static final String FROM = "from";
        public static final int FROM_CHAT = 1;
        public static final int FROM_NEW_FRIEND = 2;
        public static final int FROM_CONTACT = 3;
        public static final int FROM_GROUP_APPLY = 4;
    }

    public static class GroupType {
        public static final String TYPE = "type";
        public static final String GROUP = "isGroup";
        public static final int PRIVATE = 0;
        public static final int PUBLIC = 1;
        public static final int CHAT_ROOM = 2;

        public static final String TYPE_PRIVATE = "Private";
        public static final String TYPE_PUBLIC = "Public";
        public static final String TYPE_CHAT_ROOM = "ChatRoom";
    }

}
