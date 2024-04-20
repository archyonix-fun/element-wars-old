package net.minestom.server.event;

import org.jetbrains.annotations.NotNull;

public class PublicEventNode extends EventNodeImpl<Event> {
  public PublicEventNode(@NotNull String name) {
    super(name, EventFilter.ALL, null);
  }
}
