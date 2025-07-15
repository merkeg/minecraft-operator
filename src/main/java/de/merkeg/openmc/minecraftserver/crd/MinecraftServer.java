package de.merkeg.openmc.minecraftserver.crd;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("openmc.merkeg.de")
@Version("v1")
public class MinecraftServer extends CustomResource<MinecraftServerSpec, MinecraftServerStatus> implements Namespaced {

  @Override
  protected MinecraftServerStatus initStatus() {
    return new MinecraftServerStatus();
  }

  @Override
  protected MinecraftServerSpec initSpec() {
    return new MinecraftServerSpec();
  }
}
