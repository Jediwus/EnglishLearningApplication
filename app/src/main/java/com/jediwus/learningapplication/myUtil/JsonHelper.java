package com.jediwus.learningapplication.myUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jediwus.learningapplication.database.Conjugation;
import com.jediwus.learningapplication.database.ConjugationItems;
import com.jediwus.learningapplication.database.Phrase;
import com.jediwus.learningapplication.database.Sentence;
import com.jediwus.learningapplication.database.Synonym;
import com.jediwus.learningapplication.database.SynonymItems;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.gson.JsonPhrases;
import com.jediwus.learningapplication.gson.JsonRels;
import com.jediwus.learningapplication.gson.JsonRelsWords;
import com.jediwus.learningapplication.gson.JsonSentences;
import com.jediwus.learningapplication.gson.JsonSynos;
import com.jediwus.learningapplication.gson.JsonSynosHwds;
import com.jediwus.learningapplication.gson.JsonTrans;
import com.jediwus.learningapplication.gson.JsonWord;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class JsonHelper {

    // 采用Google的GSON开源框架
    public static Gson gson = new Gson();

    /**
     * 解析默认词库数据文件，然后存放到数据库中.
     *
     * @param jsonData the json data
     */
    public static void analyseDefaultAndSave(String jsonData) {
        if (!LitePal.findAll(Word.class).isEmpty()) {
            LitePal.deleteAll(Word.class);
            LitePal.deleteAll(Translation.class);
            LitePal.deleteAll(Phrase.class);
            LitePal.deleteAll(Sentence.class);
        }
        // 解析的数据格式
        List<JsonTrans> jsonTransList;
        List<JsonPhrases> jsonPhrasesList = new ArrayList<>();
        List<JsonSentences> jsonSentencesList = new ArrayList<>();

        List<JsonRels> jsonRelsList;
        List<JsonRelsWords> jsonRelsWordsList;

        List<JsonSynos> jsonSynosList;
        List<JsonSynosHwds> jsonSynosHwdsList;


        List<JsonWord> jsonWordList = gson.fromJson(jsonData, new TypeToken<List<JsonWord>>() {
        }.getType());
        for (JsonWord jsonWord : jsonWordList) {
            Word word = new Word();
            // 设置ID
            word.setWordId(jsonWord.getWordRank());
            // 设置单词
            word.setWord(jsonWord.getHeadWord());
            // 设置音标-英式
            if (jsonWord.getContent().getWord().getContent().getUkphone() != null) {
                if (!jsonWord.getContent().getWord().getContent().getUkphone().contains(";")) {
                    word.setUkPhone("[" + jsonWord.getContent().getWord().getContent().getUkphone() + "]");
                } else {
                    word.setUkPhone("[" + jsonWord.getContent().getWord().getContent().getUkphone().split(";")[0] + "]");
                }
            }
            // 设置音标-美式
            if (jsonWord.getContent().getWord().getContent().getUsphone() != null) {
                if (!jsonWord.getContent().getWord().getContent().getUsphone().contains(";")) {
                    word.setUsPhone("[" + jsonWord.getContent().getWord().getContent().getUsphone() + "]");
                } else {
                    word.setUsPhone("[" + jsonWord.getContent().getWord().getContent().getUsphone().split(";")[0] + "]");
                }
            }
            // 设置图片描述
            if (jsonWord.getContent().getWord().getContent().getPicture() != null) {
                word.setPicAddress(jsonWord.getContent().getWord().getContent().getPicture());
            }
            // 设置巧记
            if (jsonWord.getContent().getWord().getContent().getRemMethod() != null) {
                word.setRemMethod(jsonWord.getContent().getWord().getContent().getRemMethod().getVal());
            }
            // 设置归属书目
            word.setBelongBook(jsonWord.getBookId());
            // 保存数据，单词的基本内容已经保存，接下来把其他表的数据保存并绑定到这个单词上
            word.save();

            // 设置短语
            if (jsonWord.getContent().getWord().getContent().getPhrase() != null) {
                jsonPhrasesList = jsonWord.getContent().getWord().getContent().getPhrase().getPhrases();
                for (JsonPhrases jsonPhrases : jsonPhrasesList) {
                    Phrase phrase = new Phrase();
                    phrase.setCnPhrase(jsonPhrases.getpCn());
                    phrase.setEnPhrase(jsonPhrases.getpContent());
                    phrase.setWordId(jsonWord.getWordRank());
                    phrase.save();
                }
            }

            // 设置释义
            jsonTransList = jsonWord.getContent().getWord().getContent().getTrans();
            for (JsonTrans jsonTrans : jsonTransList) {
                Translation translation = new Translation();
                translation.setWordType(jsonTrans.getPos());
                translation.setCnMeaning(jsonTrans.getTranCn()
                        .replace("；", ";")
                        .replace(",", "，")
                );
                translation.setEnMeaning(jsonTrans.getTranOther());
                translation.setWordId(jsonWord.getWordRank());
                translation.save();
            }

            // 设置例句：
            if (jsonWord.getContent().getWord().getContent().getSentence() != null) {
                jsonSentencesList = jsonWord.getContent().getWord().getContent().getSentence().getSentences();
                for (JsonSentences jsonSentences : jsonSentencesList) {
                    Sentence sentence = new Sentence();
                    sentence.setCnSentence(jsonSentences.getsCn());
                    sentence.setEnSentence(jsonSentences.getsContent()
                            .replace('’', '\'')
                            .replace('‘', '\'')
                            .replace('“', '"')
                            .replace('”', '"'));
                    sentence.setWordId(jsonWord.getWordRank());
                    sentence.save();
                }
            }

            // 设置同根词
            if (jsonWord.getContent().getWord().getContent().getRelWord() != null) {
                int i = 1;
                jsonRelsList = jsonWord.getContent().getWord().getContent().getRelWord().getRels();
                for (JsonRels jsonRels : jsonRelsList) {
                    Conjugation conjugation = new Conjugation();
                    conjugation.setId(i++);
                    conjugation.setPos(jsonRels.getPos());
                    conjugation.setWordId(jsonWord.getWordRank());
                    conjugation.save();
                    jsonRelsWordsList = jsonRels.getWords();
                    for (JsonRelsWords jsonRelsWords : jsonRelsWordsList) {
                        ConjugationItems conjugationItems = new ConjugationItems();
                        conjugationItems.setEnConjugation(jsonRelsWords.getHwd());
                        conjugationItems.setCnConjugation(jsonRelsWords.getTran()
                                .replace("，", ",")
                                .replace("（", "(")
                                .replace("）", ")"));
                        conjugationItems.setConjugationId(conjugation.getId());
                        conjugationItems.save();
                    }
                }
            }

            // 设置同近义词
            if (jsonWord.getContent().getWord().getContent().getSyno() != null) {
                int i = 1;
                jsonSynosList = jsonWord.getContent().getWord().getContent().getSyno().getSynos();
                for (JsonSynos jsonSynos : jsonSynosList) {
                    Synonym synonym = new Synonym();
                    synonym.setId(i++);
                    synonym.setPos(jsonSynos.getPos());
                    synonym.setTran(jsonSynos.getTran()
                            .replace("（", "(")
                            .replace("）", ")"));
                    synonym.setWordId(jsonWord.getWordRank());
                    synonym.save();
                    jsonSynosHwdsList = jsonSynos.getHwds();
                    for (JsonSynosHwds jsonSynosHwds : jsonSynosHwdsList) {
                        SynonymItems synonymItems = new SynonymItems();
                        synonymItems.setItemWords(jsonSynosHwds.getW());
                        synonymItems.setSynonymId(synonym.getId());
                        synonymItems.save();
                    }
                }
            }

            // 清空数据，防止重复
            jsonPhrasesList.clear();
            jsonSentencesList.clear();
            jsonTransList.clear();


        }


    }


}
