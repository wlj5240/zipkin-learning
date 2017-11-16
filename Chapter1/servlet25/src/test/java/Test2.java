
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Test2 {
    public static void main(String[] args) {
        Test2 test = new Test2();
        test.test(test::get);
        test.test(Map<String, String>::get);
        test.test(HttpServletRequest::getHeader);

    }

    public <D> void test(Getter<D> getter){
        Map<String, String> map = new HashMap<>();
//        getter.get(map,"aa");
    }

    public String get(Map data, String key){
        return (String) data.get(key);
    }
}
