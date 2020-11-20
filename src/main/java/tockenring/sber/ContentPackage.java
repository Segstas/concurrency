package tockenring.sber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ContentPackage {
   public String content;

   public List<Long> timestamps = new ArrayList();
   public List<Long> latency = new ArrayList();


   public void putTimeStamp () {
      timestamps.add(System.nanoTime());
   }

   public void calculateLatency () {
      for (int i = 1; i < timestamps.size(); i++) {
          if (timestamps.get(i) != null) {
             latency.add(timestamps.get(i) - timestamps.get(i - 1));
          }
      }
   }

   public ContentPackage(String content) {
      this.content = content;
   }

   @Override
   public String toString() {
      return "ContentPackage{" +
              "content='" + content + '\'' +
              '}';
   }
}
