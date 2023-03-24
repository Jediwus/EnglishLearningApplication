package com.jediwus.learningapplication.myUtil;

import android.util.Log;

import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.database.Word;

import org.litepal.LitePal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Learning controller. 单词学习流程核心
 */
public class LearningController {

    public static final String TAG = "WordController";

    // 当前ID
    public static int currentWordId;

    // 今天共需要复习的单词量
    public static int wordNeedReciteNumber;

    // 当前模式
    public static int currentMode;
    // 新学模式
    public static final int MODE_NEW_LEARNING = 1;
    // 及时复习模式
    public static final int MODE_REVIEW_IN_TIME = 2;
    // 常规复习模式（浅度 & 深度）
    public static final int MODE_REVIEW_ROUTINE = 3;
    // 今日任务已完成
    public static final int TODAY_TASK_COMPLETE = 4;

    // 候选需要学习的单词
    public static List<Integer> wordsNeedToLearnList = new ArrayList<>();

    // 候选需要复习的单词
    public static List<Integer> wordsNeedToReviewList = new ArrayList<>();

    // 用于存放一轮新学习结束的单词，供复习模块使用
    public static List<Integer> wordsJustLearnedList = new ArrayList<>();

    /**
     * 初始化学习流程
     *
     * @return the int
     */
    public static int initLearningProcess() {
        if (!wordsNeedToLearnList.isEmpty()) {
            if (wordsJustLearnedList.size() > (wordsNeedToLearnList.size() / 2)) {
                // 新学和学完复习，两个模式随机挑出一个
                return NumberController.getRandomNumber(MODE_NEW_LEARNING, MODE_REVIEW_IN_TIME);
            } else {
                // 新学模式
                return MODE_NEW_LEARNING;
            }
        } else {
            if (!wordsJustLearnedList.isEmpty()) {
                // 及时复习模式
                return MODE_REVIEW_IN_TIME;
            } else {
                if (!wordsNeedToReviewList.isEmpty()) {
                    // 常规复习模式
                    return MODE_REVIEW_ROUTINE;
                } else {
                    // 今日任务已完成
                    return TODAY_TASK_COMPLETE;
                }
            }
        }
    }

    /**
     * 设置需要学习的单词
     *
     * @param lastTimeThatStartLearning 上次学习的时间
     */
    public static void setWordsNeededToLearn(long lastTimeThatStartLearning) {
        // 先清空 List
        wordsNeedToLearnList.clear();
        wordsJustLearnedList.clear();
        // 获得用户偏好数据
        List<UserPreference> userPreferenceList = LitePal
                .where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                .find(UserPreference.class);
        // 从用户偏好中获取每日计划学习单词数
        int wordReciteNumInPlan = userPreferenceList.get(0).getWordNeedReciteNum();

        // 查询出：需要学习的且不是刚刚学习过的，没有在指定时间去学习的单词
        List<Word> queryWordNeedToLearnList = LitePal
                .where("isNeededToLearn = ? and justLearned = ? and dateNeededToLearn <= ?", "1", "0", TimeController.getCurrentDateStamp() + "")
                .select("wordId")
                .find(Word.class);
        // 查询出：需要学习的且根本没有学习过的单词，即供分配单词的库，不需要学习的单词有可能是需要复习的单词，用haveLearned将其排除
        List<Word> queryWordNoNeedToLearnList = LitePal.where("isNeededToLearn = ? and haveLearned = ?", "0", "0")
                .select("wordId")
                .find(Word.class);

        Log.d(TAG, "需要学习的且根本没有学习过的单词 queryWordNoNeedToLearnList = " + queryWordNoNeedToLearnList.size());

        // 点击开始任务按钮时已经是新的一天，单词需要新的分配
        if (!TimeController.isTheSameDay(lastTimeThatStartLearning, TimeController.getCurrentTimeStamp())) {

            Log.d(TAG, "setWordsNeededToLearn: 今天任务开启，开始分配单词");

            // 若需要的单词不够计划量，需额外分配
            if (queryWordNeedToLearnList.size() < wordReciteNumInPlan) {
                int amountDiffer = wordReciteNumInPlan - queryWordNeedToLearnList.size();
                int[] extraInQueryWordNoNeedToLearnList = NumberController.getRandomNumberList(0, queryWordNoNeedToLearnList.size() - 1, amountDiffer);

                Log.d(TAG, "额外单词 extraInQueryWordNoNeedToLearnList = " + Arrays.toString(extraInQueryWordNoNeedToLearnList));

                // 将额外单词归并到每日任务需要学习的单词中
                assert extraInQueryWordNoNeedToLearnList != null;
                for (int i : extraInQueryWordNoNeedToLearnList) {
                    wordsNeedToLearnList.add(queryWordNoNeedToLearnList.get(i).getWordId());
                    // 将额外分配的单词数据更新
                    Word word = new Word();
                    word.setIsNeededToLearn(1);
                    word.setDateNeededToLearn(TimeController.getCurrentDateStamp());
                    word.updateAll("wordId = ?", queryWordNoNeedToLearnList.get(i).getWordId() + "");
                }
                // 合并至待学习单词列表
                if (!queryWordNeedToLearnList.isEmpty()) {
                    for (Word word : queryWordNeedToLearnList) {
                        wordsNeedToLearnList.add(word.getWordId());
                    }
                }
            } else { // 若查询到的 queryWordNeedToLearnList 单词量可以匹配计划量，添加至待学习单词列表
                int i = 0;
                for (Word word : queryWordNoNeedToLearnList) {
                    i++;
                    if (i <= wordReciteNumInPlan) {
                        wordsNeedToLearnList.add(word.getWordId());
                    } else {
                        break;
                    }
                }
                Log.d(TAG, "待学习单词列表 wordsNeedToLearnList = " + wordsNeedToLearnList.size());
            }
        } else {
            Log.d(TAG, "setWordsNeededToLearn: 点击开始任务按钮时,仍是同一天,无需重新分配");
            // 点击开始任务按钮时,仍是同一天,无需重新分配,直接续上回已分配的单词
            int i = 0;
            for (Word word : queryWordNoNeedToLearnList) {
                i++;
                if (i <= wordReciteNumInPlan) {
                    wordsNeedToLearnList.add(word.getWordId());
                } else {
                    break;
                }
            }
            Log.d(TAG, "待学习单词列表 wordsNeedToLearnList = " + wordsNeedToLearnList.size());
        }
    }

    /**
     * 设置需要复习的单词
     */
    public static void setWordsNeededToReview() {
        // 先清空 List
        wordsNeedToReviewList.clear();
        wordsJustLearnedList.clear();
        // 查询出：一轮未结束，新学且并未完成复习的单词
        List<Word> queryJustLearnButNotReviewList = LitePal.where("justLearned = ? and haveLearned = ?", "1", "0")
                .select("wordId")
                .find(Word.class);
        // 查询出：浅度复习单词，即已经学习过的并且单词掌握程度未到10的单词
        List<Word> queryShallowReviewList = LitePal.where("haveLearned = ? and masterDegree < ?", "1", "10")
                .select("wordId")
                .find(Word.class);
        // 查询出：深度复习单词，单词掌握程度达到10并且达到了单词复习的时间
        List<Word> queryDeepReviewList = LitePal.where("masterDegree = ?", "10")
                .select("wordId")
                .find(Word.class);

        // 【首先处理，优先级最高】：深度复习或者已经到了深度学习的阶段的单词，加入复习列表
        for (Word word : queryDeepReviewList) {
            switch (word.getDeepMasterTimes()) {
                case 0: // 深度掌握次数为 0
                    try {
                        if (TimeController.daysInternal(word.getLastMasterTime(), TimeController.getCurrentDateStamp()) > 4) {
                            // 时间之差为大于 4 天，未及时进行深度复习，将其单词掌握程度 -2
                            Word newWord = new Word();
                            newWord.setMasterDegree(8);
                            newWord.updateAll("wordId = ?", word.getWordId() + "");
                            wordsNeedToReviewList.add(word.getWordId());
                        } else if (TimeController.daysInternal(word.getLastMasterTime(), TimeController.getCurrentDateStamp()) == 4) {
                            // 第 4 天，需进行深度复习
                            wordsNeedToReviewList.add(word.getWordId());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

                case 1: // 深度掌握次数为 1
                    try {
                        if (TimeController.daysInternal(word.getLastMasterTime(), TimeController.getCurrentDateStamp()) > 3) {
                            // 时间之差为大于 3 天，未及时进行深度复习，将其单词掌握程度 -2
                            Word newWord = new Word();
                            newWord.setMasterDegree(8);
                            newWord.updateAll("wordId = ?", word.getWordId() + "");
                            wordsNeedToReviewList.add(word.getWordId());
                        } else if (TimeController.daysInternal(word.getLastMasterTime(), TimeController.getCurrentDateStamp()) == 3) {
                            // 第 3 天，需进行深度复习
                            wordsNeedToReviewList.add(word.getWordId());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

                case 2: // 深度掌握次数为 2
                    try {
                        if (TimeController.daysInternal(word.getLastMasterTime(), TimeController.getCurrentDateStamp()) > 8) {
                            // 时间之差为大于 8 天，未及时进行深度复习，将其单词掌握程度 -2
                            Word newWord = new Word();
                            newWord.setMasterDegree(8);
                            newWord.updateAll("wordId = ?", word.getWordId() + "");
                            wordsNeedToReviewList.add(word.getWordId());
                        } else if (TimeController.daysInternal(word.getLastMasterTime(), TimeController.getCurrentDateStamp()) == 8) {
                            // 第 8 天，需进行深度复习
                            wordsNeedToReviewList.add(word.getWordId());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
        // 【其次处理，优先级居中】：浅度复习的单词（即掌握程度未达到 10），加入复习列表
        for (Word word : queryShallowReviewList) {
            wordsNeedToReviewList.add(word.getWordId());
        }
        // 【最后处理，优先级最低】：一轮未结束，新学且并未完成复习的单词，加入复习列表
        for (Word word : queryJustLearnButNotReviewList) {
            wordsNeedToReviewList.add(word.getWordId());
        }
    }


    /**
     * 从待学习的单词列表中抽取随机ID
     *
     * @return the new word to learn id
     */
    public static int getNewWordToLearn() {
        if (!wordsNeedToLearnList.isEmpty()) {
            int index = NumberController.getRandomNumber(0, wordsNeedToLearnList.size() - 1);
            return wordsNeedToLearnList.get(index);
        } else
            return -1;
    }

    /**
     * 一轮未结束，已初学完这个单词，还未进行立即复习
     * 所以从 wordsNeedToLearnList 中去掉它
     *
     * @param wordId the word id
     */
    public static void completeNewWordToLearn(int wordId) {
        // 从 wordsNeedToLearnList 需新学 中移除单词
        for (int i = 0; i < wordsNeedToLearnList.size(); i++) {
            if (wordsNeedToLearnList.get(i) == wordId) {
                wordsNeedToLearnList.remove(i);
                break;
            }
        }
        // 放进临时需要复习的列表里
        wordsJustLearnedList.add(wordId);
        // 更新数据库数据
        Word word = new Word();
        word.setJustLearned(1);
        word.setToDefault("isNeededToLearn");
        word.updateAll("wordId = ?", wordId + "");
    }

    /**
     * 从刚学习过的的单词列表中抽取随机ID
     *
     * @return the just learned id
     */
    public static int getJustLearnedToReview() {
        if (!wordsJustLearnedList.isEmpty()) {
            int index = NumberController.getRandomNumber(0, wordsJustLearnedList.size() - 1);
            return wordsJustLearnedList.get(index);
        } else
            return -1;
    }

    /**
     * 完成刚学过的词的复习
     *
     * @param wordId  the word id
     * @param ifRight the answer is right or not
     */
    public static void completeJustLearnedToReview(int wordId, boolean ifRight) {
        List<Word> words = LitePal.where("wordId = ?", wordId + "")
                .select("wordId", "word", "masterDegree", "examRightNum", "deepMasterTimes")
                .find(Word.class);
        // 回答正确
        if (ifRight) {
            // 从 wordsJustLearnedList新学 中移除单词
            for (int i = 0; i < wordsJustLearnedList.size(); ++i) {
                if (wordsJustLearnedList.get(i) == wordId) {
                    wordsJustLearnedList.remove(i);
                    break;
                }
            }
            // 更新单词库内单词掌握等属性
            Word word = new Word();
            // 设置成已经学过
            word.setHaveLearned(1);
            // 回答正确，掌握程度加2点
            if (words.get(0).getMasterDegree() < 10) {
                if (words.get(0).getMasterDegree() != 8) {
                    word.setMasterDegree(words.get(0).getMasterDegree() + 2);
                } else {
                    word.setMasterDegree(10);
                }
                word.updateAll("wordId = ?", wordId + "");
            } else {
                // 深度掌握次数 +1
                word.setDeepMasterTimes(words.get(0).getDeepMasterTimes() + 1);
                // 设置上次已掌握时间
                word.setLastMasterTime(TimeController.getCurrentDateStamp());
            }
            // 设置上次复习时间
            word.setLastReviewTime(TimeController.getCurrentTimeStamp());
            // 更新
            word.updateAll("wordId = ?", wordId + "");
        }
        // 回答错误
        // else { // 不增加掌握程度 }
    }

    /**
     * 从需要复习的单词列表中抽取随机ID
     *
     * @return the word to review id
     */
    public static int getWordToReview() {
        Log.d(TAG, "reviewOneWord: 从需要复习的单词列表中抽取随机ID");
        if (!wordsNeedToReviewList.isEmpty()) {
            int index = NumberController.getRandomNumber(0, wordsNeedToReviewList.size() - 1);
            return wordsNeedToReviewList.get(index);
        } else
            return -1;
    }

    /**
     * 完成复习
     *
     * @param wordId  the word id
     * @param ifRight the answer is right or not
     */
    public static void completeWordToReview(int wordId, boolean ifRight) {
        List<Word> words = LitePal.where("wordId = ?", wordId + "")
                .select("wordId", "word", "masterDegree", "examRightNum", "deepMasterTimes")
                .find(Word.class);
        // 回答正确
        if (ifRight) {
            // 从 wordsNeedToReviewList 复习列表 中移除单词
            for (int i = 0; i < wordsNeedToReviewList.size(); ++i) {
                if (wordsNeedToReviewList.get(i) == wordId) {
                    wordsNeedToReviewList.remove(i);
                    break;
                }
            }
            Word word = new Word();
            // 浅度掌握的复习（掌握程度<10）
            if (words.get(0).getMasterDegree() < 10) {
                // 测试正确次数 +1
                word.setExamRightNum(words.get(0).getExamRightNum() + 1);
                // 掌握程度 +2
                if (words.get(0).getMasterDegree() != 8) {
                    word.setMasterDegree(words.get(0).getMasterDegree() + 2);
                } else {
                    word.setMasterDegree(10);
                }
            } else { // 深度掌握的复习（掌握程度=10）
                // 深度掌握次数 +1
                word.setDeepMasterTimes(words.get(0).getDeepMasterTimes() + 1);
                // 设置上次已掌握时间
                word.setLastMasterTime(TimeController.getCurrentDateStamp());
            }
            word.updateAll("wordId = ?", wordId + "");
        }
        // 更新测试次数
        Word word = new Word();
        // 设置测试次数
        word.setExamNum(words.get(0).getExamNum() + 1);
        word.updateAll("wordId = ?", wordId + "");
    }


    /**
     * 从三个列表中移除指定 wordId 的单词
     *
     * @param wordId the word id
     */
    public static void removeSelectedWord(int wordId) {
        for (int i = 0; i < wordsNeedToLearnList.size(); i++) {
            if (wordId == wordsNeedToLearnList.get(i)) {
                wordsNeedToLearnList.remove(i);
                i--; // 这里要用索引遍历
            }
        }
        for (int i = 0; i < wordsNeedToReviewList.size(); i++) {
            if (wordId == wordsNeedToReviewList.get(i)) {
                wordsNeedToReviewList.remove(i);
                i--; // 这里要用索引遍历
            }
        }
        for (int i = 0; i < wordsJustLearnedList.size(); i++) {
            if (wordId == wordsJustLearnedList.get(i)) {
                wordsJustLearnedList.remove(i);
                i--; // 这里要用索引遍历
            }
        }
    }


}
