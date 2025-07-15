package de.merkeg.openmc.minecraftserver.data;

import de.merkeg.openmc.util.EnvKey;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ServerProperties {

  @EnvKey("MAX_PLAYERS")
  private int slots = 20;

  @EnvKey("MOTD")
  private String motd = "Kubernetes Minecraft Server";

  @EnvKey("DIFFICULTY")
  private ServerDifficulty difficulty;

  @EnvKey("HARDCORE")
  private boolean hardcore = false;

  @EnvKey("LEVEL")
  private String levelName = "world";

  @EnvKey("SEED")
  private String levelSeed;

  @EnvKey("ONLINE_MODE")
  private boolean onlineMode = true;

  @EnvKey("PVP")
  private boolean pvp = true;

  @EnvKey("ALLOW_FLIGHT")
  private boolean allowFlight = false;

  @EnvKey("RESOURCE_PACK")
  private String resourcePack;

  @EnvKey("RESOURCE_PACK_ENFORCE")
  private boolean resourcePackEnforce = false;

  @EnvKey("RESOURCE_PACK_SHA1")
  private String resourcePackSha1;

  @EnvKey("VIEW_DISTANCE")
  private int viewDistance = 10;

  @EnvKey("SIMULATION_DISTANCE")
  private int simulationDistance = 10;

  @EnvKey("ENABLE_WHITELIST")
  private boolean whitelistEnabled = false;

  private List<String> whitelist;

  private List<String> operators;
}
