package tockenring.sber;

public class ContentPackage {
   public String content;



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
