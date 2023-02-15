package com.jediwus.learningapplication.model;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public interface CallBackListener {

    void onFailure(Call call, IOException e);

    void onResponse(Call call, Response response) throws IOException;

}
