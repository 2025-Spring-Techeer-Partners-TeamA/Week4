package daiseek.sbb.testClass;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class HelloLombok {

    // 롬복 생성자 애노테이션을 사용하기 위해 final 키워드 사용
    private final String hello;
    private final int lombok;


    /**
     * 롬복으로 대체
     */
//    public void setHello(String hello) {
//        this.hello = hello;
//    }
//
//    public void setLombok(int lombok) {
//        this.lombok = lombok;
//    }
//
//    public String getHello() {
//        return this.hello;
//    }
//
//    public int getLombok() {
//        return this.lombok;
//    }

//    public HelloLombok(String hello, int lombok) {
//        this.hello = hello;
//        this.lombok = lombok;
//    }


    public static void main(String[] args) {


//        HelloLombok helloLombok = new HelloLombok();

        // 롬복으로 생성자 대체시 제거
//        helloLombok.setHello("하이");
//        helloLombok.setLombok(5);

        // 롬복 생성자 사용법1
        HelloLombok helloLombok = new HelloLombok("하이", 5);

        // 롬복 생성자 사용법2

        System.out.println(helloLombok.getHello());
        System.out.println(helloLombok.getLombok());

    }
}
