package de.merkeg.openmc.minecraftserver.crd;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinecraftServerStatus {
  boolean error;
  boolean ready;
  String message;
  String lastAppliedHash;
}
