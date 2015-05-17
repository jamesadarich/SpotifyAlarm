package net.getsett.spotifyalarm.integrations.spotify;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by James on 16/05/2015.
 */
public class SpotifyToken {

    private Date expiryTime;
    private String token;
    private String refreshToken;
    private final SpotifyTokenGenerator tokenGenerator;

    public SpotifyToken(JSONObject jsonToken, SpotifyTokenGenerator tokenGenerator){
        try {
            expiryTime = new Date();
            expiryTime.setTime(expiryTime.getTime() + (jsonToken.getInt("expires_in") * 1000));
        }
        catch (JSONException exception){

        }

        try {
            token = jsonToken.getString("access_token");
        }
        catch (JSONException exception){

        }

        try {
            refreshToken = jsonToken.getString("refresh_token");
        }
        catch (JSONException exception){

        }

        this.tokenGenerator = tokenGenerator;
    }

    public String toString() {
        return token;
    }

    public boolean isExpired() {
        return new Date().after(expiryTime);
    }

    public SpotifyToken refresh() {
        return tokenGenerator.refreshToken(refreshToken);
    }

    public String getRefreshToken() {
        return  refreshToken;
    }
}
