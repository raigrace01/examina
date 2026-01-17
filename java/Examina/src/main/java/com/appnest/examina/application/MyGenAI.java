package com.appnest.examina.application;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;


import java.time.Duration;

public class MyGenAI {
    private String GEMINI_KEY = "";
    private String PROJECT_NAME="projects/329930347212";
    private String AI_MODEL = "gemini-3-pro-preview";

    public static void main(String[] args) {

        MyGenAI  genAI = new MyGenAI();
        // Create the model instance
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(genAI.GEMINI_KEY)
                .modelName(genAI.AI_MODEL)
                .timeout(Duration.ofHours(5))
                .maxRetries(5)
                .build();


        // Generate response
        String answer = model.generate("Explain polymorphism in one sentence.");

        System.out.println(answer);
    }

}
