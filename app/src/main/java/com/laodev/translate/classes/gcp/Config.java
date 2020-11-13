package com.laodev.translate.classes.gcp;


import com.laodev.translate.utils.Constants;

class Config {
    static final String API_KEY = Constants.getAPIKey(); // put your key to here
    static final String API_KEY_HEADER = "X-Goog-Api-Key";
    static final String SYNTHESIZE_ENDPOINT = "https://texttospeech.googleapis.com/v1beta1/text:synthesize";
    static final String VOICES_ENDPOINT = "https://texttospeech.googleapis.com/v1beta1/voices";
}
