package com.v1no.LJL.learning_service.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeUtil {

    public static String YOUTUBE_API_KEY;

    private static final Pattern YOUTUBE_VIDEO_ID_PATTERN = Pattern.compile(
            "(?<=v=|v/|vi=|vi/|embed/|shorts/|youtu.be/)([a-zA-Z0-9_-]{11})"
    );

    public static String extractVideoId(String url) {
        if (url == null || url.isBlank()) return null;

        Matcher matcher = YOUTUBE_VIDEO_ID_PATTERN.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public static Integer getVideoDurationInSeconds(String videoId) {
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

    public static String getThumbnailUrl(String videoId) {
    if (videoId == null || videoId.isBlank()) return null;
    return String.format("https://img.youtube.com/vi/%s/maxresdefault.jpg", videoId);
}

}
