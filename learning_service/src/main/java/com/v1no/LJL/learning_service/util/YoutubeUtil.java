package com.v1no.LJL.learning_service.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.v1no.LJL.learning_service.model.dto.response.YoutubeVideoInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class YoutubeUtil {

    @Value("${app.youtube.api-key}")
    private String YOUTUBE_API_KEY;

    private final Pattern YOUTUBE_VIDEO_ID_PATTERN = Pattern.compile(
            "(?<=v=|v/|vi=|vi/|embed/|shorts/|youtu.be/)([a-zA-Z0-9_-]{11})"
    );

    public String extractVideoId(String url) {
        if (url == null || url.isBlank()) return null;

        Matcher matcher = YOUTUBE_VIDEO_ID_PATTERN.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public Integer getVideoDurationInSeconds(String videoId) {
        try {
            if (videoId == null || videoId.isBlank() || YOUTUBE_API_KEY == null || YOUTUBE_API_KEY.isBlank())
                return null;

            String apiUrl = String.format(
                    "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id=%s&key=%s",
                    videoId, YOUTUBE_API_KEY
            );

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);
            if (response.isBlank()) return null;

            JsonObject json = JsonParser.parseString(response).getAsJsonObject();

            if (json.has("items") && !json.getAsJsonArray("items").isEmpty()) {
                String isoDuration = json.getAsJsonArray("items")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("contentDetails")
                        .get("duration").getAsString();

                Duration duration = Duration.parse(isoDuration);
                return (int) duration.getSeconds();
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    public YoutubeVideoInfo getVideoInfo(String videoId) {
        try {
            if (videoId == null || videoId.isBlank() || YOUTUBE_API_KEY == null) return null;

            String apiUrl = String.format(
                "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails&id=%s&key=%s",
                videoId, YOUTUBE_API_KEY
            );

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);
            if (response == null || response.isBlank()) return null;

            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            JsonArray items = json.getAsJsonArray("items");
            if (items == null || items.isEmpty()) return null;

            JsonObject item = items.get(0).getAsJsonObject();
            JsonObject snippet = item.getAsJsonObject("snippet");
            JsonObject contentDetails = item.getAsJsonObject("contentDetails");

            String title = snippet.get("title").getAsString();
            int duration = (int) Duration.parse(contentDetails.get("duration").getAsString()).getSeconds();

            JsonObject thumbnails = snippet.getAsJsonObject("thumbnails");
            String thumbnail = null;
            for (String quality : List.of("maxres", "standard", "high", "medium", "default")) {
                if (thumbnails.has(quality)) {
                    thumbnail = thumbnails.getAsJsonObject(quality).get("url").getAsString();
                    break;
                }
            }

            return new YoutubeVideoInfo(title, thumbnail, duration);

        } catch (Exception e) {
            log.warn("Failed to fetch video info for videoId={}: {}", videoId, e.getMessage());
            return null;
        }
    }

    public String getThumbnailUrl(String videoId) {
        if (videoId == null || videoId.isBlank()) return null;
        return String.format("https://img.youtube.com/vi/%s/maxresdefault.jpg", videoId);
    }

}
