package io.broessl.treesql.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public class NavigableTree {

  private static List<NavigableTreeProvider> providers;

  public static List<NavigableTreeProvider> providers() {
    if (providers == null) {
      providers = new ArrayList<>();
      ServiceLoader<NavigableTreeProvider> loader = ServiceLoader.load(NavigableTreeProvider.class);
      loader.forEach(providers::add);
    }
    return providers;
  }

  public static NavigableTreeProvider providerFor(String directive) {
    List<NavigableTreeProvider> list = providers();
    for (NavigableTreeProvider provider : list) {
      if (directive.equals(provider.getDirective())) {
        return provider;
      }
    }
    throw new NoSuchElementException("Navigable Tree Provider for '" + directive + "' not found");
  }
}
