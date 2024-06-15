import com.dreams.controller.UserController;
import com.dreams.springframework.context.ClassPathXmlApplicationContext;

/**
 * @author PoemsAndDreams
 * @date 2023-09-27 07:54
 */
public class Test {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //......
//
//        UserController userController = (UserController) context.getBean("uc");
//
//        userController.test();

        UserController userController = (UserController) context.getBean(UserController.class);
        userController.test();

        System.out.println(userController == userController);

    }
}
