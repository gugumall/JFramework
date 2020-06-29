package j.dao;

/**
 * 
 * @author 肖炯
 *
 * 2020年6月6日
 *
 * <b>功能描述</b> j_text表用来存储mysql的text字段（gzip压缩），避免mysql因text字段导致查询慢的问题。<br/>
 * 注意：只有通过单个bean对象插入和更新的text字段值才会保存到该表中，如批量更新多个记录则会导致数据不一致。<br/>
 * j_text表必须在所操作的数据库中创建，系统启动后j_text表中数据会被加载进内存（与数据库保持一致）。<br/>
 * j_text表中存储那些表的那些text字段在配置文件JDAO.xml中指定，其中哪些记录在启动时加载进内存可由sql指定。
 */
public class TextDB {

}
