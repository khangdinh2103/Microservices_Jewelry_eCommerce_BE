package Service_Catalog.edu.vn.service_product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisConnection() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            System.out.println("Redis is running!");
   
            String key = "testKey";
            String value = "testValue";
            redisTemplate.opsForValue().set(key, value);
            String retrievedValue = (String) redisTemplate.opsForValue().get(key);
            assertEquals(value, retrievedValue, "Redis không hoạt động đúng!");
            
        } catch (Exception e) {
            System.err.println("Redis is not running! Error: " + e.getMessage());
            throw e;
        }
    }
}