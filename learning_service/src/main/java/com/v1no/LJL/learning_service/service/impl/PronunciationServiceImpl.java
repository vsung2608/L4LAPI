package com.v1no.LJL.learning_service.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;
import com.v1no.LJL.learning_service.model.dto.response.*;
import com.v1no.LJL.learning_service.service.PronunciationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PronunciationServiceImpl implements PronunciationService {

    @Value("${app.azure.speech.key}")
    private String speechKey;

    @Value("${app.azure.speech.region}")
    private String speechRegion;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PronunciationResultResponse assess(byte[] audioData, String referenceText, String language) throws Exception {

        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
        speechConfig.setSpeechRecognitionLanguage(language);

        PronunciationAssessmentConfig pronConfig = new PronunciationAssessmentConfig(
            referenceText,
            PronunciationAssessmentGradingSystem.HundredMark,
            PronunciationAssessmentGranularity.Phoneme,
            true
        );
        pronConfig.enableProsodyAssessment();

        PushAudioInputStream pushStream = AudioInputStream.createPushStream(
            AudioStreamFormat.getWaveFormatPCM(16000, (short) 16, (short) 1)
        );
        pushStream.write(audioData);
        pushStream.close();

        AudioConfig audioConfig = AudioConfig.fromStreamInput(pushStream);
        SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);
        pronConfig.applyTo(recognizer);

        SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

        if (result.getReason() != ResultReason.RecognizedSpeech) {
            pronConfig.close();
            throw new RuntimeException("Speech recognition failed: " + result.getReason());
        }

        PronunciationAssessmentResult pronResult = PronunciationAssessmentResult.fromResult(result);

        if (pronResult == null) {
            pronConfig.close();
            throw new RuntimeException("Pronunciation assessment returned null (no AccuracyScore)");
        }

        recognizer.close();
        speechConfig.close();
        audioConfig.close();
        pronConfig.close();

        return PronunciationResultResponse.builder()
            .recognizedText(result.getText())
            .accuracyScore(pronResult.getAccuracyScore())
            .fluencyScore(pronResult.getFluencyScore())
            .completenessScore(pronResult.getCompletenessScore())
            .pronScore(pronResult.getPronunciationScore())
            .prosodyScore(pronResult.getProsodyScore() != null
                ? pronResult.getProsodyScore() : -1.0)
            .words(extractWordDetails(result))
            .build();
    }

    private List<WordDetailResponse> extractWordDetails(SpeechRecognitionResult result) {
        try {
            String json = result.getProperties()
                .getProperty("SpeechServiceResponse_JsonResult");

            if (json == null || json.isEmpty()) {
                return Collections.emptyList();
            }

            JsonNode root = objectMapper.readTree(json);
            JsonNode words = root.path("NBest").get(0).path("Words");

            List<WordDetailResponse> wordList = new ArrayList<>();
            for (JsonNode wordNode : words) {
                JsonNode pa = wordNode.path("PronunciationAssessment");

                wordList.add(WordDetailResponse.builder()
                    .word(wordNode.path("Word").asText())
                    .accuracyScore(pa.path("AccuracyScore").asDouble())
                    .errorType(pa.path("ErrorType").asText("None"))
                    .syllables(extractSyllables(wordNode.path("Syllables")))
                    .phonemes(extractPhonemes(wordNode.path("Phonemes")))
                    .build());
            }

            return wordList;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse word details: " + e.getMessage(), e);
        }
    }

    private List<SyllableDetailResponse> extractSyllables(JsonNode node) {
        List<SyllableDetailResponse> list = new ArrayList<>();
        for (JsonNode s : node) {
            list.add(SyllableDetailResponse.builder()
                .syllable(s.path("Syllable").asText())
                .accuracyScore(s.path("PronunciationAssessment").path("AccuracyScore").asDouble())
                .offsetMs(s.path("Offset").asLong() / 10000)
                .durationMs(s.path("Duration").asLong() / 10000)
                .build());
        }
        return list;
    }

    private List<PhonemeDetailResponse> extractPhonemes(JsonNode node) {
        List<PhonemeDetailResponse> list = new ArrayList<>();
        for (JsonNode p : node) {
            list.add(PhonemeDetailResponse.builder()
                .phoneme(p.path("Phoneme").asText())
                .accuracyScore(p.path("PronunciationAssessment").path("AccuracyScore").asDouble())
                .offsetMs(p.path("Offset").asLong() / 10000)
                .durationMs(p.path("Duration").asLong() / 10000)
                .build());
        }
        return list;
    }
}