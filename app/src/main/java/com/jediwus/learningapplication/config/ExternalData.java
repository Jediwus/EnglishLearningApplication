package com.jediwus.learningapplication.config;

import com.jediwus.learningapplication.R;

public class ExternalData {

    // 数据存放目录（外置存储卡）
    public static final String DIR_STORE = "learningApplication";
    // 解压后的数据目录
    public static final String DIR_AFTER_ZIP = "jsonData";

    // 通知渠道ID
    public static final String channelId = "default";
    public static final String channelId1 = "default1";
    // 通知渠道名称
    public static final String channelName = "默认通知";
    public static final String channelName1 = "默认通知1";

    /**
     * 必应每日一图 API
     */
    public static final String IMG_API = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=7&n=1";
    //public static final String IMG_API = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=5&mkt=zh-CN";

    /**
     * 必应每日一图 API 前置域名
     */
    public static final String IMG_API_BEFORE = "https://www.bing.com";

    /**
     * 每日一句 API
     */
    public static final String DAILY_SENTENCE_API = "https://open.iciba.com/dsapi/";
    public static final String SENTENCE_API = "https://www.mxnzp.com/api/daily_word/recommend?count=10&app_id=j1njmus6ivuetemh&app_secret=TDNST0M2NUtNOXZMTmFsTHBxZXNTZz09";

    // 回答正确的提示音
    public static final int RIGHT_SIGN = R.raw.answer_right;
    // 回答错误的提示音
    public static final int WRONG_SIGN = R.raw.answer_wrong;

    // 有道美式发音 API
    public static final String YOU_DAO_VOICE_US = "https://dict.youdao.com/dictvoice?type=0&audio=";
    // 有道英式发音 API
    public static final String YOU_DAO_VOICE_UK = "https://dict.youdao.com/dictvoice?type=1&audio=";

    // 罗翔经典语录
    public static final String[] sayings = {
            "人无完人,事无尽美",
            "努力告别昨天,即使深陷泥潭也要努力爬出来",
            "人生最大的失败就是知道的太多,会做的太少",
            "唯有真理的光照,才能学会谦卑",
            "走出自我的偏狭,自由而不放纵,独立而不狂狷,尽责而不懈怠",
            "永远年轻,永远热泪盈眶",
            "一定要成为懂得思考自身的人",
            "过好每一天,演好当下的剧本",
            "把今天当作一个礼物,去珍惜享受你现在拥有的平凡的幸福",
            "珍惜你的低谷期，你会看到很多真相",
            "做你该做的事,并接受它的事与愿违",
            "当我们越多地理解世界,我们也就越多地理解自己",
            "人心隐藏着整个世界的败坏,我们每个人心中都藏着个张三",
            "真正的朋友,并不会嘲笑你的软弱,你也无需假装强大",
            "要爱具体的人,不要爱抽象的人;要爱生活,不要爱生活的意义",
            "这世间,本就是各人下雪,各人有各人的隐晦与皎洁",
            "整个一生说白了,就是北大保安说的3句话:你是谁?你去哪?你干嘛?"
    };

    //----------------------------------- 词书数据部分--------------------------------------

    // 书默认ID
    public static final int CET4_CoreWord = 1;
    public static final int CET6_CoreWord = 2;
    public static final int CET4_All = 3;
    public static final int CET6_All = 4;
    public static final int KaoYan_CoreWord = 5;
    public static final int kaoYan_All = 6;
    public static final int Level4_CoreWord = 7;
    public static final int Level8_CoreWord = 8;
    public static final int Level4_All = 9;
    public static final int Level8_All = 10;

    // 根据 词书ID 获取该书的单词总量
    public static int getWordsTotalNumbersById(int bookId) {
        int wordsNum = 0;
        switch (bookId) {
            case CET4_CoreWord:
                wordsNum = 1162;
                break;
            case CET6_CoreWord:
                wordsNum = 1228;
                break;
            case CET4_All:
                wordsNum = 3739;
                break;
            case CET6_All:
                wordsNum = 2078;
                break;
            case KaoYan_CoreWord:
                wordsNum = 1341;
                break;
            case kaoYan_All:
                wordsNum = 4533;
                break;
            case Level4_CoreWord:
                wordsNum = 595;
                break;
            case Level8_CoreWord:
                wordsNum = 684;
                break;
            case Level4_All:
                wordsNum = 4025;
                break;
            case Level8_All:
                wordsNum = 12197;
                break;
        }
        return wordsNum;
    }

    // 根据 词书ID 获取该书的书名
    public static String getBookNameById(int bookId) {
        String bookName = "";
        switch (bookId) {
            case CET4_CoreWord:
                bookName = "四级英语真题核心词";
                break;
            case CET6_CoreWord:
                bookName = "六级英语真题核心词";
                break;
            case CET4_All:
                bookName = "四级英语大纲";
                break;
            case CET6_All:
                bookName = "六级英语大纲";
                break;
            case KaoYan_CoreWord:
                bookName = "考研必考词汇";
                break;
            case kaoYan_All:
                bookName = "考研英语大纲";
                break;
            case Level4_CoreWord:
                bookName = "专四真题高频词";
                break;
            case Level8_CoreWord:
                bookName = "专八真题高频词";
                break;
            case Level4_All:
                bookName = "专四核心词汇";
                break;
            case Level8_All:
                bookName = "专八核心词汇";
                break;
        }
        return bookName;
    }

    // 根据 词书ID 获取该书的类型
    public static String getTypeById(int bookId) {
        String type = "";
        switch (bookId) {
            case CET4_CoreWord:
            case CET4_All:
                type = "四级英语";
                break;
            case CET6_CoreWord:
            case CET6_All:
                type = "六级英语";
                break;
            case KaoYan_CoreWord:
            case kaoYan_All:
                type = "考研英语";
                break;
            case Level4_CoreWord:
            case Level4_All:
                type = "专四英语";
                break;
            case Level8_CoreWord:
            case Level8_All:
                type = "专八英语";
                break;
        }
        return type;
    }

    // 根据 词书ID 获取该书的图片
    public static String getBookPicById(int bookId) {
        String picAddress = "";
        switch (bookId) {
            case CET4_CoreWord:
                picAddress = "https://nos.netease.com/ydschool-online/1496632727200CET4luan_1.jpg";
                break;
            case CET6_CoreWord:
                picAddress = "https://nos.netease.com/ydschool-online/1496655382926CET6luan_1.jpg";
                break;
            case KaoYan_CoreWord:
                picAddress = "https://nos.netease.com/ydschool-online/1496632762670KaoYanluan_1.jpg";
                break;
            case Level4_CoreWord:
                picAddress = "https://nos.netease.com/ydschool-online/1496632776935Level4luan_1.jpg";
                break;
            case Level8_CoreWord:
                picAddress = "https://nos.netease.com/ydschool-online/1491037703359Level8_1.jpg";
                break;
            case CET4_All:
                picAddress = "https://nos.netease.com/ydschool-online/youdao_CET4_2.jpg";
                break;
            case CET6_All:
                picAddress = "https://nos.netease.com/ydschool-online/youdao_CET6_2.jpg";
                break;
            case kaoYan_All:
                picAddress = "https://nos.netease.com/ydschool-online/youdao_KaoYan_2.jpg";
                break;
            case Level4_All:
                picAddress = "https://nos.netease.com/ydschool-online/youdao_Level4_2.jpg";
                break;
            case Level8_All:
                picAddress = "https://nos.netease.com/ydschool-online/youdao_Level8_2.jpg";
                break;
        }
        return picAddress;
    }

    // 根据 词书ID 获取该书的下载地址
    public static String getBookDownLoadAddressById(int bookId) {
        String downloadAddress = "";
        switch (bookId) {
            case CET4_CoreWord:
                downloadAddress = "http://ydschool-online.nos.netease.com/1523620217431_CET4luan_1.zip";
                break;
            case CET6_CoreWord:
                downloadAddress = "http://ydschool-online.nos.netease.com/1521164660466_CET6luan_1.zip";
                break;
            case KaoYan_CoreWord:
                downloadAddress = "http://ydschool-online.nos.netease.com/1521164661106_KaoYanluan_1.zip";
                break;
            case Level4_CoreWord:
                downloadAddress = "http://ydschool-online.nos.netease.com/1521164630387_Level4luan_1.zip";
                break;
            case Level8_CoreWord:
                downloadAddress = "http://ydschool-online.nos.netease.com/1521164635290_Level8_1.zip";
                break;
            case CET4_All:
                downloadAddress = "http://ydschool-online.nos.netease.com/1524052539052_CET4luan_2.zip";
                break;
            case CET6_All:
                downloadAddress = "http://ydschool-online.nos.netease.com/1524052554766_CET6_2.zip";
                break;
            case kaoYan_All:
                downloadAddress = "http://ydschool-online.nos.netease.com/1521164654696_KaoYan_2.zip";
                break;
            case Level4_All:
                downloadAddress = "http://ydschool-online.nos.netease.com/1521164625401_Level4luan_2.zip";
                break;
            case Level8_All:
                downloadAddress = "http://ydschool-online.nos.netease.com/1521164650006_Level8luan_2.zip";
                break;
        }
        return downloadAddress;
    }

    // 根据 词书ID 获取该书的下载后的文件名
    public static String getBookFileNameById(int bookId) {
        String fileName = "";
        switch (bookId) {
            case CET4_CoreWord:
                fileName = "CET4luan_1.zip";
                break;
            case CET6_CoreWord:
                fileName = "CET6luan_1.zip";
                break;
            case KaoYan_CoreWord:
                fileName = "KaoYanluan_1.zip";
                break;
            case Level4_CoreWord:
                fileName = "Level4luan_1.zip";
                break;
            case Level8_CoreWord:
                fileName = "Level8_1.zip";
                break;
            case CET4_All:
                fileName = "CET4luan_2.zip";
                break;
            case CET6_All:
                fileName = "CET6_2.zip";
                break;
            case kaoYan_All:
                fileName = "KaoYan_2.zip";
                break;
            case Level4_All:
                fileName = "Level4luan_2.zip";
                break;
            case Level8_All:
                fileName = "Level8luan_2.zip";
                break;
        }
        return fileName;
    }

}
