package j.test;

public class MyBean {
	private String id;

	private String value;

	public MyBean(String id, String value) {
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return this.id;
	}

	public static void main(String[] args) {
		MyBean[] beans = new MyBean[2];

		MyBean bean1 = new MyBean("abc", "123");

		MyBean bean2 = new MyBean("xyz", "456");

		beans[0] = bean1;
		beans[1] = bean2;

		beans[0] = null;

		System.out.println(bean1.getId());
	}
}
