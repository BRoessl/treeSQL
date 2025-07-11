package io.broessl.treesql.xml;

import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import io.broessl.treesql.spi.NavigableTreeProvider;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class NavigableXmlProvider implements NavigableTreeProvider {

  @Override
  public String getDirective() {
    return "XML";
  }

  @Override
  public Optional<NavigableTreeNode> buildTreeRoot(TreeValue fromContent) {
    if (fromContent instanceof TreeString tString) {
      try {
        SAXReader reader = new SAXReader();
        Element root = reader.read(new StringReader(tString.getValue())).getRootElement();
        return Optional.of(new NavigableXmlNode(root, null, (String) null));
      } catch (Exception e) {
        // log and ignore
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<NavigableTreeNode> attachTreeNode(
      String rootName, TreeValue fromContent, NavigableTreeNode parentNode, List<String> argument) {
    if (fromContent instanceof TreeString tString) {
      try {
        SAXReader reader = new SAXReader();
        Element root = reader.read(new StringReader(tString.getValue())).getRootElement();
        if (root.getName().equals(rootName)) {
          return Optional.of(new NavigableXmlNode(root, parentNode, rootName));
        }
      } catch (Exception e) {
        // log and ignore
      }
    }
    return Optional.empty();
  }
}
