package xyz.ukrainskiys.discorddota.bot.handlers;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.stereotype.Component;
import xyz.ukrainskiys.discorddota.lavaplayer.LavaplayerManagerService;
import xyz.ukrainskiys.discorddota.lavaplayer.errors.CannotLoadTrackException;
import xyz.ukrainskiys.discorddota.repository.PlayingHistoryRepository;
import xyz.ukrainskiys.discorddota.repository.ServersRepository;
import xyz.ukrainskiys.discorddota.utils.DiscordUtils;

@Component
@RequiredArgsConstructor
public class PlaySlashCommandHandler implements SlashCommandHandler {

  private final ServersRepository serversRepository;
  private final PlayingHistoryRepository playingHistoryRepository;
  private final LavaplayerManagerService lavaplayerManagerService;

  @Override
  public void handle(SlashCommandCreateEvent event) {
    final SlashCommandInteraction interaction = event.getSlashCommandInteraction();
    final Optional<ServerChannel> optChannel = event.getSlashCommandInteraction().getArgumentChannelValueByIndex(0);
    final Optional<String> optUrl = event.getSlashCommandInteraction().getArgumentStringValueByIndex(1);
    if (optChannel.isPresent() && optUrl.isPresent()) {
      final String url = optUrl.get();
      if (!DiscordUtils.isValidYouTubeURL(url)) {
        DiscordUtils.sendCommandRespond(interaction, "Incorrect YouTube url!");
        return;
      }

      final ServerVoiceChannel channel = (ServerVoiceChannel) optChannel.get();
      try {
        lavaplayerManagerService.playTrackFromUrl(channel, url);
        DiscordUtils.sendCommandRespond(interaction, "Track launched!");
        serversRepository.saveServer(channel.getServer().getId(), channel.getId());
        playingHistoryRepository.updatePlayingHistory(url, channel.getServer().getId());
      } catch (CannotLoadTrackException e) {
        DiscordUtils.sendCommandRespond(interaction, String.format("""
                Can't download item from url: %s
                Check that this is the correct link on the YouTube video.
                """, url));
        channel.disconnect();
      }
    }
  }
}
