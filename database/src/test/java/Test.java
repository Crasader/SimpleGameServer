import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wq.database.redis.gameServer.RedisUserDao;
import com.wq.entity.mould.User;



public class Test {
	
	static ApplicationContext context = new ClassPathXmlApplicationContext("springDB.xml");
	
	@Resource
	RedisUserDao redisUserDao;

	public static void main(String[] args){
		Test test = (Test)context.getBean("Test");
		User user = new User();
		user.setId(1);
		user.setName("WQ");
		test.redisUserDao.insert(user);
		String name = test.redisUserDao.query(user);
		System.out.println(name);
	}
}
