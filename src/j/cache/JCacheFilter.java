package j.cache;

import java.io.Serializable;

/**
 * 对于复杂的查询逻辑，根据key或者index查询不能满足需求，业务代码需根据实际情况（如用户输入的缓存操作参数）实现此类并把它传递给缓存单元，
 * 缓存单元会条用该对象的matches方法来判断对象是否符合缓存操作参数， 设计此类的目的是为了避免“把所有对象取回本地再进行对比”的低效率做法（甚至当缓存数据量大时会引发系统瘫痪，如巨大的延时、内存耗尽等），
 * 而是在缓存单元通过该对象过滤后，仅返回符合条件的记录。
 * @author 肖炯
 *
 */
public interface JCacheFilter extends Serializable{
	/**
	 * 判断某对象是否符合给定的条件
	 * @param object
	 * @return
	 */
	public boolean matches(Object object);
}
