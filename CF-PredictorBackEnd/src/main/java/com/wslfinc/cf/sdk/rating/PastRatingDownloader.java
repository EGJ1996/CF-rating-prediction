package com.wslfinc.cf.sdk.rating;

import static com.wslfinc.cf.sdk.Constants.*;
import com.wslfinc.cf.sdk.CodeForcesSDK;
import com.wslfinc.cf.sdk.entities.Contest;
import com.wslfinc.cf.sdk.entities.RatingChange;
import com.wslfinc.helpers.web.JsonReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class is responsible for getting from codeforces all users rating for
 * all previous contests.
 *
 * @author Wsl_F
 */
public class PastRatingDownloader {

    public static boolean getRatingBeforeContest(int maxId, String filePrefix) {
        List<Contest> contests = CodeForcesSDK.getFinishedContests(maxId, false);
        Map<String, Integer> rating = new HashMap<>();

        boolean result = true;
        int n = contests.size();
        for (int i = 0; i < n; i++) {
            int contestId = contests.get(i).getId();
            if (!writeToFiles(filePrefix, rating, "" + contestId)) {
                result = false;
                System.err.println("Couldn't write rating after contest " + contestId);
            }

            List<RatingChange> ratingChanges = CodeForcesSDK.getRatingChanges(contestId);
            for (RatingChange ratingChange : ratingChanges) {
                rating.put(ratingChange.getHandle(), ratingChange.getNewRating());
            }
        }

        return writeToFiles(filePrefix, rating, "current") && result;
    }

    private static JSONObject toJSON(Map<String, Integer> rating) {
        List<JSONObject> list = new ArrayList<>(rating.size());

        for (String handle : rating.keySet()) {
            JSONObject contestant = new JSONObject();
            contestant.put("handle", handle);
            contestant.put("rating", rating.get(handle));
            list.add(contestant);
        }

        JSONObject contest = new JSONObject();
        contest.put("status", SUCCESSFUL_STATUS);
        contest.put(JSON_RESULTS, new JSONArray(list));

        return contest;
    }

    private static String getFileName(String contestId, String filePrefix) {
        return filePrefix + "/contest_" + contestId + ".html";
    }

    private static boolean writeToFiles(String filePrefix, Map<String, Integer> rating, String contestId) {
        boolean result = true;
        String fileName = getFileName(contestId, filePrefix);
        JSONObject json = toJSON(rating);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
            json.write(writer);
            writer.write("\n");
        } catch (Exception ex) {
            System.err.println("Couldn't write past rating to the file\n"
                    + ex.getMessage());
            result = false;
        }

        return result;
    }

    private static Map<String, Integer> toMap(JSONObject json) {
        if (!json.has("status") || !json.has(JSON_RESULTS)) {
            return new HashMap<>();
        }

        Map<String, Integer> ratings = new HashMap<>();
        JSONArray ratingsArray = json.getJSONArray(JSON_RESULTS);
        for (Object rating : ratingsArray) {
            JSONObject r = (JSONObject) rating;
            ratings.put(r.getString("handle"), r.getInt("rating"));
        }

        return ratings;
    }

    private static boolean validate(int maxId, String filePrefix) {
        boolean result = true;
        List<Contest> contests = CodeForcesSDK.getFinishedContests(maxId, false);
        for (Contest contest : contests) {
            int contestId = contest.getId();
            String path = "file://" + getFileName("" + contestId, filePrefix);
            try {
                JSONObject json = JsonReader.read(path);
                Map<String, Integer> rating = toMap(json);
                List<RatingChange> ratingChanges = CodeForcesSDK.getRatingChanges(contestId);
                for (RatingChange ratingChange : ratingChanges) {
                    int prevRating = ratingChange.getOldRating();
                    if (prevRating != INITIAL_RATING) {
                        String handle = ratingChange.getHandle();
                        if (!rating.containsKey(handle)
                                || rating.get(handle) != prevRating) {
                            System.err.println("Wrong rating on contest: "
                                    + contestId + " handle: " + handle);
                            result = false;
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Couldn't read file " + path + "\n" + ex.getMessage());
                result = false;
            }
        }

        return result;
    }
}