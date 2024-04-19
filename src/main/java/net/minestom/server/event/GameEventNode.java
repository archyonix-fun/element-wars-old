package net.minestom.server.event;

import org.jetbrains.annotations.NotNull;

public class GameEventNode extends EventNodeImpl<Event> {
  public GameEventNode(@NotNull String name) {
    super(name, EventFilter.ALL, null);
  }
}
