package cn.lunadeer.dominion.utils.scui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.world.item.component.ResolvableProfile;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Helper class for resolving player skin textures into {@link ResolvableProfile}
 * instances for use with player head items in the CUI system.
 * <p>
 * This replaces the Bukkit {@code PlayerProfile} + {@code PlayerTextures} approach
 * with Fabric-compatible {@link ResolvableProfile} and Mojang's {@link GameProfile}.
 */
public class SkinHelper {

    private static final UUID DUMMY_UUID = UUID.randomUUID();

    /**
     * Creates a {@link ResolvableProfile} from a base64-encoded skin texture.
     * <p>
     * The base64 string decodes to JSON of the form:
     * {@code {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/..."}}}}
     *
     * @param base64 the base64-encoded texture data
     * @return a ResolvableProfile with the texture property set, or null on failure
     */
    public static ResolvableProfile createProfileFromB64(String base64) {
        try {
            // The base64 value is the textures property value itself
            // We need to decode it to get the skin URL for the property
            String decoded = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(decoded).getAsJsonObject();
            String skinUrl = json.getAsJsonObject("textures")
                    .getAsJsonObject("SKIN")
                    .get("url").getAsString();

            GameProfile profile = new GameProfile(DUMMY_UUID, "");
            profile.getProperties().put("textures", new Property("textures", base64, skinUrl));
            return new ResolvableProfile(profile);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a {@link ResolvableProfile} from a skin URL.
     * <p>
     * The URL is encoded into the standard textures JSON format and then
     * base64-encoded for the textures property.
     *
     * @param skinUrl the URL of the skin texture
     * @return a ResolvableProfile with the texture property set, or null on failure
     */
    public static ResolvableProfile createProfileFromUrl(String skinUrl) {
        try {
            String b64 = encodeSkinUrlToB64(skinUrl);
            GameProfile profile = new GameProfile(DUMMY_UUID, "");
            profile.getProperties().put("textures", new Property("textures", b64, skinUrl));
            return new ResolvableProfile(profile);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a {@link ResolvableProfile} from a player name.
     * <p>
     * This attempts to look up the player's skin URL. If the player is not
     * found in the cache, a default Steve skin texture is used.
     *
     * @param playerName the player name to look up
     * @return a ResolvableProfile with the texture property set, or null on failure
     */
    public static ResolvableProfile createProfileFromName(String playerName) {
        try {
            // Try to get the player's skin URL from the cache
            // CacheManager is not yet ported, so we use a default skin for now.
            // TODO: When CacheManager is ported, resolve player skin URL from cache
            String defaultSkinUrl = "http://textures.minecraft.net/texture/613ba1403f98221fab6f4ae0f9e5298068262258966e8f9e53cdedd97aa45ef1";
            return createProfileFromUrl(defaultSkinUrl);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Encodes a skin URL into the base64 textures property format.
     *
     * @param skinUrl the raw skin texture URL
     * @return the base64-encoded textures JSON
     */
    private static String encodeSkinUrlToB64(String skinUrl) {
        // Build the textures JSON structure
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + skinUrl + "\"}}}";
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }
}
