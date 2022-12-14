package xyz.ukrainskiys.discorddota.utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import org.javacord.api.event.interaction.ApplicationCommandEvent;

public class DiscordBotUtils {

  public static void sendMessage(ApplicationCommandEvent event, String message) {
    event.getInteraction().getChannel().ifPresent(textChannel -> textChannel.sendMessage(message));
  }

  public static boolean isValidYouTubeURL(String url) {
    try {
      return Objects.equals(new URL(url).toURI().getHost(), "www.youtube.com");
    } catch (MalformedURLException | URISyntaxException e) {
      return false;
    }
  }
}
