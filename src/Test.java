import java.util.HashSet;

public class Test {
    public static void main(String[] args) throws InterruptedException, NoSuchMethodException {
        HashSet<String > set = new HashSet<>() {{
            add("1");
            add("2");
        }};

        set.remove("1");
        set.add("213");
        set.forEach(System.out::println);
    }

    static class A {
        public String a;
        public String b;

        public A() {
        }

        public A(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

}
