package com.zendaimoney.coreaccount.data.redis;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.redis.core.StringRedisTemplate;

@Named
public class BasicRedisOpts {
	@Inject
	private StringRedisTemplate stringRedisTemplate;

	public void persist(String k, String v) {
		stringRedisTemplate.opsForValue().set(k, v);
	}

	/**
	 * 持久化同时设置Key的过期时间
	 * 
	 * @param k
	 * @param v
	 * @param d
	 */
	public void persist(String k, String v, Date d) {
		persist(k, v);
		stringRedisTemplate.expireAt(k, d);
	}

	public void batchSave(Map<String, String> p) {
		stringRedisTemplate.opsForValue().multiSet(p);
	}

	public boolean expireAt(String k, Calendar d) {
		return stringRedisTemplate.expireAt(k, d.getTime());
	}

	public String getSingleResult(String k) {
		return stringRedisTemplate.opsForValue().get(k);
	}

	/**
	 * 
	 * @param keys
	 *            (key的集合)
	 * @return (value的集合)
	 */
	public List<String> getResultList(Collection<String> keys) {
		return stringRedisTemplate.opsForValue().multiGet(keys);
	}

	public void remove(String k) {
		stringRedisTemplate.delete(k);
	}

	public void remove(Collection<String> keys) {
		stringRedisTemplate.delete(keys);
	}

	public boolean exists(String k) {
		return stringRedisTemplate.hasKey(k);
	}

	public Set<String> getKeySet() {
		return stringRedisTemplate.keys("*");
	}

	public long count() {
		return stringRedisTemplate.getConnectionFactory().getConnection().dbSize(); 
	}
}
